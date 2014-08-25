package com.lichard49.camerademo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraActivity extends Activity
{
	private SurfaceView camPreview;
	private SurfaceHolder holder;
	private CameraSurface camSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        
        // setup camera
        camPreview = (SurfaceView) findViewById(R.id.camera_surface);
        holder = camPreview.getHolder();
        camSurface = new CameraSurface();
        holder.addCallback(camSurface);
        
        createDialog();
    }

    protected Dialog createDialog()
    {
    	Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("\"Here I Was\", an experience-sharing application");
    	builder.setCancelable(true);
    	AlertDialog dialog = builder.create();
    	dialog.show();
    	return dialog;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }
    
}
