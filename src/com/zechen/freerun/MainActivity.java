package com.zechen.freerun;





import android.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;

public class MainActivity extends FragmentActivity implements  OnNavigationListener{
	private ActionBar actionBar;
	private FragmentManager fManager;
	private ViewPager mPager;
	private PagerAdapter mPageAdapter;
	private LinearLayout frag_activity;
	private Fragment activities;
	private boolean init;
	boolean notHome = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init = true;
		fManager = getSupportFragmentManager();
		
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
		        R.array.menuMain, R.layout.spinner_dropdown_item);
	
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		//assign spinner to action bar
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
	
		 frag_activity = (LinearLayout) findViewById(R.id.frag_activities);
		 frag_activity.setVisibility(View.INVISIBLE);
		mPager = (ViewPager) findViewById(R.id.ViewPager);
		mPageAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPageAdapter);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		
	}
	
	
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		
		if (!init) {
			switch (itemPosition) {
			case 0:
				
				if (notHome) {
					mPager.setVisibility(View.VISIBLE);
					frag_activity.setVisibility(View.INVISIBLE);
				}
				else{
					
				}
				break;
			case 1:
				Intent newRun = new Intent(this, Activity_NewRun.class);
				startActivity(newRun);
				actionBar.setSelectedNavigationItem(0);
				notHome = true;
				break;
			case 2:
				//add frag. set invisiable pager 
				mPager.setVisibility(View.INVISIBLE);
				frag_activity.setVisibility(View.VISIBLE);
				activities = new Fragment_activities();
				FragmentTransaction transaction = fManager.beginTransaction();
				if (transaction.isEmpty()) {
					transaction.add(R.id.frag_activities, activities);
					transaction.addToBackStack(null);
				} else {
					activities = new Fragment_activities();
					transaction.replace(R.id.frag_activities, activities);
					transaction.addToBackStack(null);
				}
				transaction.commit();
				notHome = true;
				break;
			default:
				break;
			}
		}
		else{
			init = false;
		}
		return false;
	}
	class ScreenSlidePagerAdapter extends FragmentPagerAdapter{

		
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 4;
		}
		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			return new Fragment_pagerItem(position);
		}
	}
	
}
