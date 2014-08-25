package com.lichard49.hereiwas;

import android.hardware.Camera;
import android.view.SurfaceHolder;

public class CameraSurface implements SurfaceHolder.Callback
{
	private Camera camera;
	
	public void surfaceCreated(SurfaceHolder holder) 
	{
		try
		{
			camera = Camera.open();
			
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) { }
	
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		stopCam();
	} 
	
	public void stopCam()
	{
		if(camera != null)
		{
			System.out.println("Camera closing...");
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}
	
	public Camera getCamera()
	{
		return camera;
	}
}
