package com.lichard49.hereiwas;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class LookActivity extends Activity
{
	public static final LayoutParams LAYOUT_PARAMS = new LayoutParams(LayoutParams.MATCH_PARENT,
    		LayoutParams.MATCH_PARENT);
	
	private HardwareInterface hardware;
	private int direction;
	private Bitmap image;
	private LiveView liveView;
	
	private SurfaceView camPreview;
	private SurfaceHolder holder;
	private CameraSurface camSurface;
	
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.look_layout);
		
        camPreview = (SurfaceView) findViewById(R.id.camera_surface);
        holder = camPreview.getHolder();
        camSurface = new CameraSurface();
        holder.addCallback(camSurface);
		
        direction = getIntent().getIntExtra("DIRECTION", 0);
		image = getIntent().getParcelableExtra("IMAGE");
        
        liveView = new LiveView(this);
        addContentView(liveView, LAYOUT_PARAMS);
        
		hardware = new HardwareInterface(
        		(SensorManager) getSystemService(SENSOR_SERVICE), null, this);
        hardware.startSensors();
        
        Toast.makeText(getApplicationContext(), "Today's weather: " + 
        		Database.getInstance().getWeather("20131004"),
        		Toast.LENGTH_LONG).show();
        
        repaint.start();
    }
	
	Thread repaint = new Thread()
	{
		public void run()
		{
			while(true)
			{
				h.sendEmptyMessage(0);
				
				try { Thread.sleep(100); } catch (Exception e) { }
			}
		}
	};
	
	Handler h = new Handler()
	{
		public void handleMessage(Message m)
		{
			liveView.invalidate();
		}
	};
	
	class LiveView extends View
	{
		private Paint paint;
		private float ratio = -1;
		
		//Canvas c2;
		
		public LiveView(Context context)
		{
			super(context);
			
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
			Shader shader = new LinearGradient(0, 0, 0, 20, Color.TRANSPARENT, Color.TRANSPARENT, Shader.TileMode.CLAMP);
			paint.setShader(shader);
			//c2 = new Canvas(image);
		}
		
		public void onDraw(Canvas c)
		{
			if(ratio == -1)
			{
				System.out.println("Image: " + image.getWidth() + ", " + image.getHeight() + "   screen: " + getWidth() + ", " + getHeight());
				image = Bitmap.createScaledBitmap(image, 509, 382, true);
				for(int i = 0; i < image.getWidth()/2; i++)
				{
					for(int j = 0; j < image.getHeight(); j++)
					{
						//c.
					}
				}
				
				ratio = ((float)getWidth())/image.getWidth();
				System.out.println("DA RATIO: " + ratio);
			}
			
			if(hardware != null)
			{
				int delta = (int) (direction-hardware.getDirection())*10;
				c.drawBitmap(image, ((delta+getWidth()/2-image.getWidth()/2)), 0, paint);
				//c2.drawRect(delta+getWidth()/2-image.getWidth()/2, 0, image.getWidth(), image.getHeight(), paint);
			}
		}

		/*
		String data = jObject.getString("Data");
		byte[] bArray = Base64.decode(data, Base64.DEFAULT);
		Bitmap b = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
		images.add(new DBImage(id, itemID, ordinal, b));
		*/
	}
	
public String match(Bitmap i,Bitmap i2){
    	
    	ArrayList<Double> hog = new ArrayList<Double>();
    	
    	for(int j = 0;j<i.getWidth();j+=(i.getWidth()/30)){
    		for(int k = 0;k<i.getHeight();k+=(i.getHeight()/30)){
    			double sumx =0;
    			double sumy = 0;
    			double sumz = 0;
    			for(int x = j;x<j+i.getWidth()/30;x+=5){
    				for(int y = k;y<k+(i.getHeight()/30);y+=5){
    					
    					int p = i.getPixel(x, y);
        				
        				sumx+= (p >> 16) & 0xff;
        				sumy+= (p >> 8) & 0xff;
        				sumz+= p & 0xff;
        			}
    			}
    			sumx/=Math.sqrt((sumx*sumx)+(sumy*sumy)+(sumz*sumz));
    			sumy/=Math.sqrt((sumx*sumx)+(sumy*sumy)+(sumz*sumz));
    			sumz/=Math.sqrt((sumx*sumx)+(sumy*sumy)+(sumz*sumz));
    			double intensity= .333*sumx +.333*sumy +.333*sumz;
    			hog.add(intensity);
    		}
    	}
    	ArrayList<Double> hog2 = new ArrayList<Double>();
    	
    	for(int j = 0;j<i2.getWidth();j+=(i2.getWidth()/30)){
    		for(int k = 0;k<i2.getHeight();k+=(i2.getHeight()/30)){
    			double sumx =0;
    			double sumy = 0;
    			double sumz = 0;
    			for(int x = j;x<j+i2.getWidth()/30;x+=5){
    				for(int y = k;y<k+i2.getHeight()/30;y+=5){
        				int p = i2.getPixel(x, y);
        				sumx+= (p >> 16) & 0xff;
        				sumy+= (p >> 8) & 0xff;
        				sumz+= p & 0xff;
        			}
    			}
    			sumx/=Math.sqrt((sumx*sumx)+(sumy*sumy)+(sumz*sumz));
    			sumy/=Math.sqrt((sumx*sumx)+(sumy*sumy)+(sumz*sumz));
    			sumz/=Math.sqrt((sumx*sumx)+(sumy*sumy)+(sumz*sumz));
    			double intensity= .333*sumx +.333*sumy +.333*sumz;
    			hog2.add(intensity);
    		}
    	}	
    	int sum = 0;
    	for (int j =0;j<Math.min(hog.size(),hog2.size());j++){
    		if(hog.get(j)-hog2.get(j)<.2){
    			sum++;
    		}
    	}
    	Toast t2 = Toast.makeText(this, "YOO", Toast.LENGTH_LONG);
		t2.show();
    	if(sum/Math.min(hog.size(),hog2.size()) > .7){
    		return "MATCH!!";
    	}else{
    		return "NOT MATCH!";
    	}
    	
    }
}
