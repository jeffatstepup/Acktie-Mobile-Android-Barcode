package com.acktie.mobile.android;

import org.appcelerator.kroll.KrollFunction;

public interface InputArgs {
	public boolean isContinuous();
	public void setContinuous(boolean continuous);

	public boolean isScanFromImageCapture();
	public void setScanFromImageCapture(boolean scanFromImageCapture);

	public boolean isUseJISEncoding();
	public void setUseJISEncoding(boolean useJISEncoding);

	public KrollFunction getSuccessCallback();
	public void setSuccessCallback(KrollFunction successCallback);

	public KrollFunction getCancelCallback();
	public void setCancelCallback(KrollFunction cancelCallback);

	public KrollFunction getErrorCallback();
	public void setErrorCallback(KrollFunction errorCallback);

	public String getColor();
	public void setColor(String color);

	public String getLayout();
	public void setLayout(String layout);

	public String getImageName();
	public void setImageName(String imageName);

	public float getAlpha();
	public void setAlpha(float alpha);

	public int getCameraDevice();
	public void setCameraDevice(int cameraDevice);
}
