package com.lichard49.hereiwas;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class HereActivity extends Activity implements OnClickListener
{
	private final int CAMERA_PIC_REQUEST = 1337;
	
	private HardwareInterface hardware;
	
	private SurfaceView camPreview;
	private SurfaceHolder holder;
	private CameraSurface camSurface;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.here_layout);
        
        System.out.println("Create");
        
        camPreview = (SurfaceView) findViewById(R.id.surfaceView1);
        holder = camPreview.getHolder();
        camSurface = new CameraSurface();
        holder.addCallback(camSurface);
        
        hardware = new HardwareInterface(
        		(SensorManager) getSystemService(SENSOR_SERVICE), null, this);
        hardware.startSensors(); 
        
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(this);
        
        //createDialog();
    }


    AlertDialog dialog;
    protected Dialog createDialog()
    {
    	Builder builder = new AlertDialog.Builder(this);
    	LayoutInflater l = this.getLayoutInflater();
    	builder.setView(l.inflate(R.layout.splash_layout, null));
    	dialog = builder.create();
    	dialog.show();
    	Button button = (Button) dialog.findViewById(R.id.button1);
    	button.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
    		{
    			dialog.dismiss();
    		}
    	});
    	return dialog;
    }
	
	@Override
	public void onClick(View v)
	{
		camSurface.stopCam();
		
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//File f = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
		//cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		//u = Uri.fromFile(f);
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{  
		camSurface.stopCam();
	    if (requestCode == CAMERA_PIC_REQUEST)
	    {  
	    	int imageDirection = (int) hardware.getDirection();
	    	Bitmap image = (Bitmap) intent.getExtras().get("data");
	    	//getContentResolver().notifyChange(u, null);
	    	try
	    	{
	    		//Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), u);
	    		saveImage(69l, imageDirection, image);
	    	
				hardware.stopSensors();
				hardware = null;
				
				Intent lookIntent = new Intent(this, LookActivity.class);
				lookIntent.putExtra("IMAGE", image);
				lookIntent.putExtra("DIRECTION", imageDirection);
				startActivity(lookIntent);
			}
	    	catch (Exception e)
			{
				e.printStackTrace();
			}
	    	
	    	
	    }  
	}
	
	public boolean saveImage(long itemID, int ordinal, Bitmap b)
    {
		double lat = hardware.getLatLon()[0];
		double lon = hardware.getLatLon()[1];
		
		/* Push to Firebase */
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("itemID", itemID+""));
        params.add(new BasicNameValuePair("ordinal", ""+ordinal));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos); //b is the bitmap object
        byte[] bArray = baos.toByteArray();
        String encodedImage = Base64.encodeToString(bArray, Base64.DEFAULT);
        params.add(new BasicNameValuePair("data", encodedImage));
        Firebase f = new Firebase("https://hereiwas.firebaseio.com/");
        Firebase newF = f.push();
        Experience e = new Experience(itemID+"");
        e.setPic(encodedImage);
        e.setLat((float)lat);
        e.setLon((float)lon);
        Date d = new Date();
        String timestamp = d.getDate() + "|" + d.getTime();
        e.setTime(timestamp);
        newF.setValue(e);
        
        Database.getInstance().createExperience(lat, lon, "https://hereiwas.firebaseio.com/"+newF.getName());
        
        
        return false;
    }
}
