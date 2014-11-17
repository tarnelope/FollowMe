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

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class SpaceStation {

	private final String TAG = getClass().getSimpleName();
	
	private GoogleMap gMap;
	
	public interface SpaceStationListener {
		public void setSpaceStation();
	}
		
	SpaceStationListener ssListener;
	
	    private Double mLat;
	    private Double mLong;
	    private GetSpaceStationLocationRequest req;
	    public SpaceStation(GoogleMap map) {
	    	
	    	gMap = map;
	    	
	    	req = new GetSpaceStationLocationRequest();
	    	req.execute();
	    }

	    public Double getLatitude() {
	        return mLat;
	    }

	    public void setLatitude(Double latitude) {
	        this.mLat = latitude;
	    }

	    public Double getLongitude() {
	        return mLong;
	    }

	    public void setLongitude(Double longitude) {
	        this.mLong = longitude;
	    }
	    
	    public void cancelSpaceStationTask() {
	    	req.cancel(true);
	    }
	    
	    private class GetSpaceStationLocationRequest extends AsyncTask<Void, LatLng, Void> {

			@Override
			protected Void doInBackground(Void... params) {
				while (!isCancelled()) {
					
					
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
			    	String str = builder.toString();
			    	
			    	try {
						JSONObject ship = new JSONObject(str);
						String status = ship.getString("message");
						
						if (status.equalsIgnoreCase("success")) {
							JSONObject position = ship.getJSONObject("iss_position");
							
							setLatitude(position.getDouble("latitude"));
							setLongitude(position.getDouble("longitude"));
							
							LatLng coordinates = new LatLng(mLat, mLong);
							publishProgress(coordinates);
							
						}
						
					} catch (JSONException ex) {
							ex.printStackTrace();
					}
					/*Log.d("Latitude is ", mLat.toString());
					Log.d("Longitutde is ", mLong.toString()); */
					
					try{
	                    Thread.sleep(1000);
	                }catch (InterruptedException e){
	                    return null;
	                }
		    	
				}
				return null;
			}
			
			@Override
			public void onProgressUpdate(LatLng... coords) {
				gMap.clear();
				
				MarkerOptions markerOptions = new MarkerOptions()
				.position(coords[0])
				.title("ISS STATION")
				.icon(BitmapDescriptorFactory.fromAsset("deathstar.png"));
				
				gMap.addMarker(markerOptions);
			}
			
			@Override
			public void onPostExecute(Void v) {

			}
	    	
	    }

	}