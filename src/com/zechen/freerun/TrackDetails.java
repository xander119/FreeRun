package com.zechen.freerun;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.zechen.freerun.database.DBHandler;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TrackDetails extends Activity {
	private TextView tDis,tTime,aSpeed,type;
	private GoogleMap map;
	private DBHandler db;
	private ArrayList<LatLng> trace;
	private String trackName,totaldis,totaltime,speed,category;
	private Cursor trackDetails;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_details);
		initViews();
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(trackName);
		this.setTitle(trackName);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initMapWithTrack() ;
		displayData();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; goto parent activity.
	            this.finish();
	            
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	private void initViews() {
		// TODO Auto-generated method stub
		tDis = (TextView) findViewById(R.id.recordTotalDis);
		tTime = (TextView) findViewById(R.id.recordTotalTime);
		aSpeed = (TextView) findViewById(R.id.recordAvgSpeed);
		type = (TextView) findViewById(R.id.recordType);
		db = new DBHandler(this);
		Bundle bundle = getIntent().getExtras();
		trackName = bundle.getString("TrackName");
		trackDetails = db.getTrackInfo(trackName);

	}

	private void displayData() {
		if (trackDetails.getCount() != 0) {
			if (trackDetails.moveToFirst()) {

				totaldis = trackDetails.getString(trackDetails
						.getColumnIndex("totaldis"));
				totaltime = trackDetails.getString(trackDetails
						.getColumnIndex("totaltime"));
				speed = trackDetails.getString(trackDetails
						.getColumnIndex("avgspeed"));
				category = trackDetails.getString(trackDetails
						.getColumnIndex("category"));

			}
		}
		if (totaldis == null) {
			tDis.setText("Not available");
		}
		else{
			tDis.setText("Total Distance : " +totaldis + " meters");
		}
		if (totaltime == null) {
			tTime.setText("Not available");
		}
		else{
			tTime.setText("Total Time : " + totaltime + " seconds");
		}
		if (speed == null) {
			aSpeed.setText("Not available");
		}
		else{
			aSpeed.setText("Average Speed : "+speed + " m/s");
		}
		if (category == null) {
			type.setText("Not available");
		}
		else{
			type.setText(category);
		}
		
		
		
		
	}

	private void initMapWithTrack() {
		// TODO Auto-generated method stub
		
		if (map == null) {
			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (map != null) {
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				//get all track points 
				if (trace == null) {
					trace = new ArrayList<LatLng>();
				}
				Cursor trackPoints = db.getTrackPointsByTrackName(trackName);
				if (trackPoints.moveToFirst()) {
					do {
						 double longitude = Double.valueOf(trackPoints.getString(trackPoints.getColumnIndex("longitude")));
						 double latitude = Double.valueOf(trackPoints.getString(trackPoints.getColumnIndex("latitude")));
						 LatLng newLl = new LatLng(latitude, longitude);
						 trace.add(newLl);
					} while (trackPoints.moveToNext());
				}
				// draw line from last location to current location
				PolylineOptions polylineOpt = new PolylineOptions();
				for (LatLng latlng : trace) {
					polylineOpt.add(latlng);
				}

				polylineOpt.color(Color.RED);
				int midpoint =(int) trace.size()/2;
				LatLng mid = trace.get(midpoint);
				Polyline line = map.addPolyline(polylineOpt);
				line.setWidth(5);
				
				MarkerOptions markerOpt2 = new MarkerOptions();
				markerOpt2.position(trace.get(0));
				markerOpt2.title("Start");
				map.addMarker(markerOpt2);
				
				CameraPosition cameraPosition = new CameraPosition.Builder()
			    .target(mid)      		// Sets the center of the map
			    .zoom(12)                   // Sets the zoom
			    .tilt(45)
			    .build();                   // Creates a CameraPosition from the builder
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}
		}
	}
}


