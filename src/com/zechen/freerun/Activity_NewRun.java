package com.zechen.freerun;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Activity_NewRun extends ActionBarActivity {
	AlertDialog ad;
	LocationManager locationManager;
	private boolean isGPSEnabled;
	private boolean isNetworkEnabled;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity__new_run);
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		final Button location = (Button) this.findViewById(R.id.location1);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		//When location button clicked change button text
		location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final CharSequence[] items = { "Outdoor", "Indoor" };
				ad = new AlertDialog.Builder(Activity_NewRun.this)
						.setTitle("Location")
						.setSingleChoiceItems(items, -1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										switch (which) {
										case 0:
											location.setText("Locatoin: Outdoor");
											ad.dismiss();
											break;
										case 1:
											location.setText("Locatoin: Indoor");
											ad.dismiss();
											break;
										}
									}

								}).create();
				ad.show();

			}
		});
		
		
		final Button music = (Button) this.findViewById(R.id.music_button1);
		//When Music button clicked, go to playlist of local music 
		music.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
				startActivity(intent);
			}
			
		});
		
		
		final Button startRun = (Button) this.findViewById(R.id.start_run);
		startRun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent run = new Intent(Activity_NewRun.this,Run.class);
				String category =location.getText().toString();
				
				//Create the bundle
				Bundle bundle = new Bundle();
				//Add your data to bundle
				bundle.putString("category", category);
				//Add the bundle to the intent
				run.putExtras(bundle);
				startActivity(run);
				
			}
			
		});
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        //check service provider 
        if(!isGPSEnabled){
        	showSettingsAlert(1);
        }
        if (!isNetworkEnabled){
        	showSettingsAlert(2);
        }
        

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
	public void showSettingsAlert(int flag){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
      
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);
  
        if (flag==1) {
			// Setting Dialog Title
			alertDialog.setTitle("GPS  settings");
			// Setting Dialog Message
			alertDialog
					.setMessage("GPS is not enabled. Do you want to go to settings menu?");
			// On pressing Settings button
			alertDialog.setPositiveButton("Settings",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(intent);
						}
					});
			// on pressing cancel button
			alertDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			// Showing Alert Message
			alertDialog.show();
		}
        else{
        	// Setting Dialog Title
        				alertDialog.setTitle("Network  settings");
        				// Setting Dialog Message
        				alertDialog
        						.setMessage("Network is not enabled. Do you want to go to settings menu?");
        				// On pressing Settings button
        				alertDialog.setPositiveButton("Settings",
        						new DialogInterface.OnClickListener() {
        							public void onClick(DialogInterface dialog, int which) {
        								Intent intent = new Intent(
        										Settings.ACTION_WIFI_SETTINGS);
        								startActivity(intent);
        							}
        						});
        				// on pressing cancel button
        				alertDialog.setNegativeButton("Cancel",
        						new DialogInterface.OnClickListener() {
        							public void onClick(DialogInterface dialog, int which) {
        								dialog.cancel();
        							}
        						});
        				// Showing Alert Message
        				alertDialog.show();
        }
        	
    }

}
