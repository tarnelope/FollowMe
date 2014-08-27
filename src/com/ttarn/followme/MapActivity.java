package com.ttarn.followme;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapActivity extends Activity {
	
	private final String TAG = getClass().getSimpleName();
	private GoogleMap googleMap;
	private LocationManager locationManager;
	private Location loc;
	
	private double myLat;
	private double myLong;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        
    
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setUpMap();
		getCurrentLocation();
	}
	
	/*
	 * setUpMap()
	 * Purpose: Initialize map, set a marker at user's current location.
	 */
	private void setUpMap() {
		if (googleMap == null) {
	        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	        googleMap.setMyLocationEnabled(true);
		} 
		
		getCurrentLocation();
		
		googleMap.addMarker(new MarkerOptions()
	        .title("ME")
	        .position(new LatLng(myLat, myLong)));
		
		CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(new LatLng(myLat, myLong))
			.zoom(14)
			.tilt(30)
			.build();
		
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	private void getCurrentLocation() {
		 locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		 
		 String provider = locationManager.getBestProvider(new Criteria(), false);
		 
		 Location location = locationManager.getLastKnownLocation(provider);
		 
		 if (location == null) {
			 locationManager.requestLocationUpdates(provider, 0, 0, listener);
		 } else {
			 loc = location;
		 }
		 
		 myLat = loc.getLatitude();
		 myLong = loc.getLongitude();
	}
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private LocationListener listener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			Log.d(TAG, "location updated : "+ location);
			loc = location;
			locationManager.removeUpdates(listener);
			
		}
	};
}
