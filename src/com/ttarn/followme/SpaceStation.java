package com.ttarn.followme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


public class SpaceStation {

	private final String TAG = getClass().getSimpleName();
	
	public interface SpaceStationListener {
		public void setSpaceStation();
	}
		
	SpaceStationListener ssListener;
	
	    private Double latitude;
	    private Double longitude;
	    
	    private Intent intent;
	    
	    public SpaceStation() {
	    	
	    	GetSpaceStationLocationRequest req = new GetSpaceStationLocationRequest();
	    	req.execute();
	    	
	    }
	    
	    public void setSpaceStationListener(Activity a) {
	    	ssListener = (SpaceStationListener) a;
	    }

	    public Double getLatitude() {
	        return latitude;
	    }

	    public void setLatitude(Double latitude) {
	        this.latitude = latitude;
	    }

	    public Double getLongitude() {
	        return longitude;
	    }

	    public void setLongitude(Double longitude) {
	        this.longitude = longitude;
	    }
	    
	    private class GetSpaceStationLocationRequest extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {
				StringBuilder builder = new StringBuilder();
		    	HttpClient client = new DefaultHttpClient();
		    	HttpGet httpGet = new HttpGet("http://api.open-notify.org/iss-now.json");
		    	try {
		    		
		    		HttpResponse response = client.execute(httpGet);
		    		StatusLine statusLine = response.getStatusLine();
		    		int statusCode = statusLine.getStatusCode();
		    		if (statusCode == 200) {
		    			HttpEntity entity = response.getEntity();
		    			InputStream content = entity.getContent();
		    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		    			String line;
		    			while ((line = reader.readLine()) != null) {
		    				builder.append(line);
		    			}
		    		} else {
		    			Log.d(TAG, "failed to download file");
		    		}
		    	} catch (IOException e){
		    		e.printStackTrace();
		    	}
		    	String output = builder.toString();
		    	
				return output;
			}
			
			@Override
			public void onPostExecute(String str) {
				try {
				JSONObject ship = new JSONObject(str);
				String status = ship.getString("message");
				Log.d("Space Station status is ", status);
				if (status.equalsIgnoreCase("success")) {
					JSONObject position = ship.getJSONObject("iss_position");
					double lat = position.getDouble("latitude");
					double longi = position.getDouble("longitude");
					setLatitude(lat);
					setLongitude(longi);
				}
				
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
				Log.d("Latitude is ", latitude.toString());
				Log.d("Longitutde is ", longitude.toString());
				ssListener.setSpaceStation();
			}
	    	
	    }

	}

