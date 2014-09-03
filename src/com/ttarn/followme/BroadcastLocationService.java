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

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BroadcastLocationService extends Service {
	
	private static final String TAG = "BroadcastLocationService";
	public static final String BROADCAST_ACTION = "com.ttarn.followme. broadcastlocationservice";
	private final Handler handler = new Handler();
	private Intent intent;
	private int counter = 0;
	private GetSpaceStationLocationRequest locationRequest;
	
	private Double latitude;
	private Double longitude;
	
	private void setLatitude(double d) {
		latitude = d;
	}
	
	private void setLongitude(double d) {
		longitude = d;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);
		locationRequest = new GetSpaceStationLocationRequest();
		locationRequest.execute();
		Log.d(TAG, "onCreate");
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		 handler.removeCallbacks(sendUpdatesToUI);
	     handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
	     Log.d(TAG, "onStart");
	}
	
	private Runnable sendUpdatesToUI = new Runnable() {
		@Override
		public void run() {
			intent.putExtra("lat", latitude);
			intent.putExtra("long", longitude);
			
			sendSpaceStationLocation();
		}
	};
	
	private void sendSpaceStationLocation() {
		Log.d(TAG, "sendSpaceStationLocation()");
		
		sendBroadcast(intent);
	}
	
	private class GetSpaceStationLocationRequest extends AsyncTask<String, Void, String> {
		
		private StringBuilder builder = new StringBuilder();
    	private HttpClient client = new DefaultHttpClient();
    	private HttpGet httpGet = new HttpGet("http://api.open-notify.org/iss-now.json");
		
		@Override
		protected void onPreExecute() {
			
		};
		
		@Override
		protected String doInBackground(String... params) {
			
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
		}
    	
    }
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {		
        handler.removeCallbacks(sendUpdatesToUI);		
		super.onDestroy();
	}		

}
