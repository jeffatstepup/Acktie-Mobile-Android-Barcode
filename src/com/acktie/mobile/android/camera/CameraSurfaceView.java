package com.acktie.mobile.android.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.app.Activity;

public class CameraSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	private CameraManager cameraManager = null;
	private Camera camera = null;
	private PreviewCallback cameraPreviewCallback = null;
	private static final String LCAT = "Acktiemobile:CameraSurfaceView";
	private int rotation;

	public CameraSurfaceView(Context context,
			PreviewCallback cameraPreviewCallback, CameraManager cameraManager) {
		super(context);
		this.cameraPreviewCallback = cameraPreviewCallback;
		this.cameraManager = cameraManager;
		this.camera = cameraManager.getCamera();
		getHolder().addCallback(this);

		rotation = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getRotation();

		// Needed for older version of Android prior to 3.0
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		//
		if (holder.getSurface() == null) {
			return;
		}

		Camera.Parameters parameters = cameraManager.getCameraParameters();
		// If null, likely called after camera has been released.
		if (parameters == null) {
			return;
		}

		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		// NOTE this is assuming first camera is being used. 
		android.hardware.Camera.getCameraInfo(0, info);
		Log.d(LCAT, "Camera orientation : " + info.orientation);
		Log.d(LCAT, "Device orientation : " + rotation);

		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);

		Camera.Size size = cameraManager.getBestPreviewSize(camera, width,
				height);

		int previewWidth = width;
		int previewHeight = height;

		if (size != null) {
			previewWidth = size.width;
			previewHeight = size.height;
		}
		// If size is null try and reverse height and width.
		// Thanks HTC desire
		else {
			size = cameraManager.getBestPreviewSize(camera, height, width);

			if (size != null) {
				previewWidth = size.width;
				previewHeight = size.height;
			} else {
				previewWidth = 640;
				previewHeight = 480;
			}
		}

		Log.d(LCAT, "Setting Preview Size to: " + previewWidth + "x"
				+ previewHeight);
		parameters.setPreviewSize(previewWidth, previewHeight);

		// int orientation = 45 / 90 * 90;
		// int rotation = (info.orientation - orientation + 360) % 360;
		// parameters.setRotation(rotation);

		camera.setParameters(parameters);
		camera.startPreview();
		cameraManager.enableAutoFocus();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera.setPreviewCallback(cameraPreviewCallback);
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			Log.d("DBG", "Error setting camera preview: " + e.getMessage());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		cameraManager.stop();
		camera = null;
	}
}
