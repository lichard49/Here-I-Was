package com.lichard49.hereiwas;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.view.View;

public class HardwareInterface implements SensorEventListener//,LocationListener
{
	private float[] orientation = null;
	
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Sensor magnetometer;
	
	private float[] gravity;
	private float[] geomagnetic;
	private LocationManager manager;
	
	private float direction;
	private View view;
	
	public HardwareInterface(SensorManager sm, View v, Context c)
	{
		sensorManager = sm;
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	    
	    view = v;
	    
	    /*
	    manager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
	    if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
	    {
	    	Toast.makeText(c, "Please enable location services!", Toast.LENGTH_LONG).show();
	    	Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            c.startActivity(settingsIntent);
	    }
	    else
	    {
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, this);
		    Location lastKnown = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		    
		    startSensors();
	    }
	    */
	}

	public void startSensors()
	{
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	    sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	public void stopSensors()
	{
	    sensorManager.unregisterListener(this);
	    //manager.removeUpdates(this);
	}

	/*
	@Override
	public void onLocationChanged(Location loc)
	{
	}
	*/

	@Override
	public void onSensorChanged(SensorEvent e)
	{
		
		if (e.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			gravity = e.values.clone();
	    if (e.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
	    	geomagnetic = e.values.clone();
	    if (gravity != null && geomagnetic != null)
	    {
	    	float R[] = new float[9];
	    	float I[] = new float[9];
	    	boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
	    	if (success)
	    	{
	    		if(orientation == null) orientation = new float[3];
	    		SensorManager.getOrientation(R, orientation);
	    		direction = (float) Math.toDegrees(orientation[0]);
	    		if(view != null)
	    			view.invalidate();
	    	}
	    }
	}
	
	public float getDirection()
	{
		return direction;
	}

	public double[] getLatLon()
	{
		double lat = 42.365;
        lat += Math.random()*2-1;
        double lon = -71.102;
        lon += Math.random()*2-1;
        
        return new double[]{lat, lon};
	}
	
	/*
	@Override
	public void onProviderDisabled(String s) { }

	@Override
	public void onProviderEnabled(String arg0) { }

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) { }
	*/
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) { }
}
