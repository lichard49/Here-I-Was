package com.lichard49.hereiwas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class Database {
	private static final long timeOut = 5;
	private static final TimeUnit timeOutUnit = TimeUnit.SECONDS;
	private static Database db;
	
	private static final String host = "http://www.samuelpclarke.com/jerrybeans/";

	protected Database()
	{
		
	}
	
	public static Database getInstance()
	{
		if(db == null)
		{
			synchronized(Database.class)
			{
				if(db==null)
					db = new Database();
			}
		}
		return db;
	}

	public String getWeather(String yyyymmdd) {
		String key = "fcaa6ebac5b2b19d";
		String page = "http://api.wunderground.com/api/"+key+"/history_"+yyyymmdd+"/q/CA/San_Francisco.json";
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONArray result = makeHttpRequest(page, "GET", params);
		if(result!=null&&result.length()!=0)
		{
			try {
				for(int i = 0; i<result.length(); i++)
				{
					JSONObject jObject = result.getJSONObject(i);
					JSONObject history = jObject.getJSONObject("history");
					JSONArray observations = history.getJSONArray("observations");
					JSONObject observation = observations.getJSONObject(observations.length()/2);
					String conds = observation.getString("conds");
					return conds;
					//routes.add(new Route(id, routeName));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	
	public boolean createExperience(double lat, double lon, String url)
	{
		String file = "createExperience.php";
		int urlPrefixID = 1;
		String urlPrefix = "https://hereiwas.firebaseio.com/";
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("lat", lat+""));
		params.add(new BasicNameValuePair("lon", lon+""));
		params.add(new BasicNameValuePair("urlPrefixID", urlPrefixID+""));
		params.add(new BasicNameValuePair("urlSuffix", url.substring(urlPrefix.length())));
		JSONArray result = makeHttpRequest(host+file, "POST", params);
		if(result!=null && result.length()!=0)
		{
			try {
				JSONObject jObject = result.getJSONObject(0);
				int success = jObject.getInt("success");
				return success==1;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public JSONArray makeHttpRequest(String url, String method, List<NameValuePair> params)
	{
		HTTPRequest request = new HTTPRequest(url,method,params);
		HTTPTask task= new HTTPTask();
		task.execute(request);
		List<JSONArray> result;
		try {
			result = task.get(Database.timeOut, Database.timeOutUnit);
			return result.get(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private class HTTPRequest
	{
		String url;
		String method;
		List<NameValuePair> params;
		
		public HTTPRequest(String url, String method, List<NameValuePair> params)
		{
			this.url = url;
			this.method = method;
			this.params = params;
		}
	}
	
	private class HTTPTask extends AsyncTask<HTTPRequest, Integer, List<JSONArray>>
	{

		@Override
		protected List<JSONArray> doInBackground(HTTPRequest... params) 
		{
			ArrayList<JSONArray> resultList = new ArrayList<JSONArray>();
			for(int i = 0; i<params.length; i++)
			{
				InputStream is = null;
				String json = null;
		        // Making HTTP request 
		        try { 
		  
		            // check for request method 
		            if(params[i].method.equalsIgnoreCase("POST")){ 
		                // request method is POST 
		                // defaultHttpClient 
		                DefaultHttpClient httpClient = new DefaultHttpClient(); 
		                HttpPost httpPost = new HttpPost(params[i].url); 
		                httpPost.setEntity(new UrlEncodedFormEntity(params[i].params)); 
		  
		                HttpResponse httpResponse = httpClient.execute(httpPost); 
		                HttpEntity httpEntity = httpResponse.getEntity(); 
		                is = httpEntity.getContent(); 
		  
		            }else if(params[i].method.equalsIgnoreCase("GET")){ 
		                // request method is GET 
		                DefaultHttpClient httpClient = new DefaultHttpClient(); 
		                String paramString = URLEncodedUtils.format(params[i].params, "utf-8"); 
		                params[i].url += "?" + paramString; 
		                HttpGet httpGet = new HttpGet(params[i].url); 
		  
		                HttpResponse httpResponse = httpClient.execute(httpGet); 
		                HttpEntity httpEntity = httpResponse.getEntity(); 
		                is = httpEntity.getContent(); 
		            }            
		  
		        } catch (UnsupportedEncodingException e) { 
		            e.printStackTrace();
		            resultList.add(null);
		            continue;
		        } catch (ClientProtocolException e) { 
		            e.printStackTrace(); 
		            resultList.add(null);
		            continue;
		        } catch (IOException e) { 
		            e.printStackTrace();
		            resultList.add(null);
		            continue;
		        } 
		  
		        try { 
		            BufferedReader reader = new BufferedReader(new InputStreamReader( 
		                    is, "iso-8859-1"), 8); 
		            StringBuilder sb = new StringBuilder(); 
		            String line = null; 
		            while ((line = reader.readLine()) != null) { 
		                sb.append(line + "\n");
		            } 
		            is.close(); 
		            json = sb.toString(); 
		        } catch (Exception e) { 
		            Log.e("Buffer Error", "Error converting result " + e.toString()); 
		            resultList.add(null);
		            continue;
		        } 

		        //System.out.println(json);
		        
		        // try parse the string to a JSON object 
		        try { 
		        	if(json.charAt(0)!='[')
		        		json = "["+json+"]";
		           JSONArray jArray = new JSONArray(json); 
		           resultList.add(jArray);
		        } catch (JSONException e) { 
		            Log.e("JSON Parser", "Error parsing data " + e.toString());
		            resultList.add(null);
		            continue;
		        }
			}
			return resultList;
		}
	}
}