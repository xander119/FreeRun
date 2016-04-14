package com.zechen.freerun;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.zechen.freerun.database.DBHandler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

public class Run extends Activity {
	private Chronometer timer;
	private TextView distence, pace;
	private LocationManager lm;
	private ArrayList<LatLng> trace;
	private Location startLocation;
	private Location lastLocation;
	private double totalDis;
	private GoogleMap map;
	private LocationListener ll;
	private String provider;
	private Marker markerMe;
	private long timeWhenStopped = 0;
	private DBHandler dbHandler;
	private String category;
	private int trackid;
	private String strDate;
	public static AudioManager mAudioManager;
	public static final String CMDTOGGLEPAUSE = "togglepause";
	public static final String CMDPAUSE = "pause";
	public static final String CMDPREVIOUS = "previous";
	public static final String CMDNEXT = "next";
	public static final String SERVICECMD = "com.android.music.musicservicecommand";
	public static final String CMDNAME = "command";
	public static final String CMDSTOP = "stop";
	public String track;
	DecimalFormat df = new DecimalFormat("#.0");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);
		
		dbHandler = new DBHandler(this);
		
		//get the category that selected 
		Bundle bundle = getIntent().getExtras();
		category = bundle.getString("category");
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
		 strDate = sdf.format(c.getTime());
		 
		//create local user if not logged in 
		if (!dbHandler.isUserExist("Local01")) {
			dbHandler.createLocalUser(strDate);
		}
		
		//if user not logged in add new track to local:
		trackid = dbHandler.addNewTrack(category, strDate, strDate, 0);
		//else dbHandler.addNewTrack(category, strDate, strDate, UserID`);
		
		// start time counting
		timer = (Chronometer) findViewById(R.id.chronometer1);
		timer.start();
		
		//set music related views 
		IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");
         final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String cmd = intent.getStringExtra("command");
                Log.i("music action", action + " / " + cmd);
                 track = intent.getStringExtra("track");
                Log.i("track:",  track+ " ");
                
            }
        };
        registerReceiver(mReceiver, iF);
        
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		TextView currentPlaying = (TextView) findViewById(R.id.songName);
		if (track!=null){
			currentPlaying.setText(track);
		}
		
		final ImageButton previousSong = (ImageButton) findViewById(R.id.previous);
		final ImageButton nextSong = (ImageButton) findViewById(R.id.next);
		previousSong.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mAudioManager.isMusicActive()) {
				    Intent i = new Intent(SERVICECMD);
				    i.putExtra(CMDNAME , CMDPREVIOUS );
				    Run.this.sendBroadcast(i);
				}
				else{
					Log.i("Media Player", "Not Active");
				}
			}
			
		});
		nextSong.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mAudioManager.isMusicActive()) {
				    Intent i = new Intent(SERVICECMD);
				    i.putExtra(CMDNAME , CMDNEXT );
				    Run.this.sendBroadcast(i);
				}
			}
			
		});
		//finish music views part

		
		//set distance textview
		distence = (TextView) findViewById(R.id.meters);
		distence.setText("0.0 M");
		
		//set pace text view 
		pace = (TextView) findViewById(R.id.pace);
		pace.setText("00 m/s");
		
		// resume run button visible after stop button is clicked.
		final ImageButton pauseRun = (ImageButton) findViewById(R.id.PauseRun);

		pauseRun.setOnClickListener(new OnClickListener() {
			Drawable resume = getResources().getDrawable(R.drawable.ic_resume);
			Drawable pause = getResources().getDrawable(R.drawable.ic_pause);

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (pauseRun.getDrawable().getCurrent().equals(resume)) {
					pauseRun.setImageDrawable(pause);
					pauseRun.invalidate();
					timer.setBase(SystemClock.elapsedRealtime()
							+ timeWhenStopped);
					timer.start();
					lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
							(float) 2.0, ll);
					if(mAudioManager.isMusicActive()) {
					    Intent i = new Intent(SERVICECMD);
					    i.putExtra(CMDNAME , CMDTOGGLEPAUSE );
					    Run.this.sendBroadcast(i);
					}
					
				} else {
					pauseRun.setImageDrawable(resume);
					pauseRun.invalidate();
					// pause timer tracing update,
					timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
					timer.stop();
					lm.removeUpdates(ll);
					if(mAudioManager.isMusicActive()) {
					    Intent i = new Intent(SERVICECMD);
					    i.putExtra(CMDNAME , CMDPAUSE );
					    Run.this.sendBroadcast(i);
					}
				}

			}
		});
		
		// Stop button clicked
		ImageButton stopRun = (ImageButton) findViewById(R.id.StopRun);
		stopRun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pauseRun.setEnabled(false);
				
				lm.removeUpdates(ll);
				//complete track info
				long elapsedMillis = SystemClock.elapsedRealtime() - timer.getBase(); 
				timer.stop();
				timeWhenStopped = 0;
				int totalTime = (int)elapsedMillis / 1000;
				double avgspeed = Double.valueOf(df.format(totalDis / Double.valueOf(totalTime)));
				double maxspeed = dbHandler.getMaxSpeedByTrackId(trackid);
				dbHandler.completeFinishedTrack(strDate, totalDis, totalTime, avgspeed,maxspeed);
				if(mAudioManager.isMusicActive()) {
				    Intent i = new Intent(SERVICECMD);
				    i.putExtra(CMDNAME , CMDSTOP );
				    Run.this.sendBroadcast(i);
				}
				 unregisterReceiver(mReceiver);
				Run.this.finish();
				
				

			}
		});

		ll = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				updateLocationTo(location);
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
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

		};

		if (initLocationProvider()) {
			startLocation = lm.getLastKnownLocation(provider);
			Log.i("provider:", provider);
			initMap();
			showMe();
		} else {
			// return to main
		}
	}

	/**
	 * show last know location and current gps location on map
	 */
	private void showMe() {
		// TODO Auto-generated method stub
		updateLocationTo(startLocation);

		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
				(float) 2.0, ll);
	}

	private void updateLocationTo(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {

			double lng = location.getLongitude();

			double lat = location.getLatitude();

			LatLng currentLatLng = new LatLng(lat, lng);

			float speed = location.getSpeed();

			long time = location.getTime();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String timeString = format.format(time);
			// update marker with new location
			if (markerMe != null) {
				markerMe.remove();
			}

			MarkerOptions markerOpt = new MarkerOptions();
			markerOpt.position(currentLatLng);
			markerOpt.title("me");
			markerMe = map.addMarker(markerOpt);
			// update camera view with new location
			CameraPosition camPosition = new CameraPosition.Builder()
					.target(currentLatLng).zoom(16).build();
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(camPosition));
			// draw line from last location to current location
			if (trace == null) {
				trace = new ArrayList<LatLng>();
			}
			trace.add(currentLatLng);

			PolylineOptions polylineOpt = new PolylineOptions();
			for (LatLng latlng : trace) {
				polylineOpt.add(latlng);
			}

			polylineOpt.color(Color.GREEN);

			Polyline line = map.addPolyline(polylineOpt);
			line.setWidth(5);
			
			
			if (lastLocation != null && location != null
					&& lastLocation != location) {

				totalDis += lastLocation.distanceTo(location);
				
				String disInString = String.valueOf(df.format(totalDis));
				distence.setText(disInString +" M");
				pace.setText(df.format(speed) +" m/s");
			}
			if(startLocation == null){
				startLocation = location;
			}
			lastLocation = location;
			
			dbHandler.addTrackpoints(lng, lat, timeString, speed,trackid);
				
			
		} else {

		}

	}

	private boolean initLocationProvider() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
			return true;
		}
		if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
			return true;
		}
		

		return false;
	}

	private void initMap() {
		// TODO Auto-generated method stub
		if (map == null) {
			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (map != null) {
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				if(startLocation != null){
					LatLng newLatLng = new LatLng(startLocation.getLatitude(),
							startLocation.getLongitude());
					// Marker1
					MarkerOptions markerOpt = new MarkerOptions();
					markerOpt.position(newLatLng);
					markerOpt.title("I'm here.");
					markerOpt.draggable(false);
					markerOpt.visible(true);
					markerOpt.anchor(0.5f, 0.5f);
					markerOpt.icon(BitmapDescriptorFactory
							.fromResource(android.R.drawable.ic_menu_mylocation));
					map.addMarker(markerOpt);
				}
				else{
					Log.i("location status:", "null");
					lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
							(float) 2.0, ll);
				}
				
			}
		}
	}

}
