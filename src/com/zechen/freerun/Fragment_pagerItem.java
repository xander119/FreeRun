package com.zechen.freerun;

import java.lang.reflect.Type;

import com.zechen.freerun.database.DBHandler;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class Fragment_pagerItem extends Fragment {
	private int pageNumber;
	private DBHandler dbHandler;
	private TextView records,motivationQ;
	private ImageView pagerImage;
	private ScrollView scrollView;
	public Fragment_pagerItem() {

	}

	public Fragment_pagerItem(int pagenumber) {
		this.pageNumber = pagenumber;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(dbHandler == null)
			dbHandler = new DBHandler(this.getActivity());
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(dbHandler == null)
			dbHandler = new DBHandler(this.getActivity());
		View view = inflater.inflate(R.layout.frag_pager, container, false);
		//get pager views 
		records = ((TextView) view.findViewById(R.id.recordsText));
		pagerImage = ((ImageView) view.findViewById(R.id.pagerimage));
		scrollView = (ScrollView) view.findViewById(R.id.ScrollView);
		motivationQ = (TextView) view.findViewById(R.id.motivationQ);
		Drawable background = getResources().getDrawable(R.drawable.home);
		String speedRecords,disRecords,timeRecords;
		switch (pageNumber) {
		case 0:
			//page 1
			scrollView.setBackground(background);
			pagerImage.setImageResource(R.drawable.welcome);
			motivationQ.setVisibility(View.INVISIBLE);
			break;
		case 1:
			//page 2
			String topSpeed = dbHandler.getMaxSpeed();
			pagerImage.setImageResource(R.drawable.ic_runner);
			if(topSpeed!=null){
				
				speedRecords = "Your Top speed\n " + topSpeed + " meter/seconds";
			}
			else{
				speedRecords = "Lets Run! Challenge yourself.";
			}
			
			records.setText(speedRecords);
			break;
		case 2:
			//page 3
			String dis = dbHandler.getlargestDisRun();
			pagerImage.setImageResource(R.drawable.ic_runner);
			if (dis!=null) {
				disRecords = "Longest distance\n " + dis + " meters";
			}
			else{
				disRecords =  "Lets Run! Challenge yourself.";
			}
			records.setText(disRecords);
			break;
		case 3:
			//page 4  
			String time = dbHandler.getLongestTimeRun();
			pagerImage.setImageResource(R.drawable.ic_runner);
			if (time!=null) {
				timeRecords = "Longest time\n " + time + " seconds";
			}
			else{
				timeRecords =  "Lets Run! Challenge yourself. ";
			}
			records.setText(timeRecords);
			break;
		default:
			((TextView) view.findViewById(R.id.recordsText))
					.setText("default");
			break;
		}

		return view;
	}

}
