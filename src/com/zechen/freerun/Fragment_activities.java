package com.zechen.freerun;




import com.zechen.freerun.database.DBHandler;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment_activities extends Fragment {
	private DBHandler db;
	private String trackName;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_activities, container,false);
		ListView listView = (ListView) view.findViewById(R.id.ListViewFromDB);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//start activity
				Intent trackDetail = new Intent(getActivity(), TrackDetails.class);
				trackName = ((TextView) (view.findViewById(R.id.trackName)))
						.getText().toString();
				 Bundle bundle = new Bundle();
				 bundle.putString("TrackName", trackName);
				 trackDetail.putExtras(bundle);
				 startActivity(trackDetail);
				 
				 
			}
		});
		
		return view;
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		getData();
	}
	private void getData() {
		db = new DBHandler(getActivity());
		Cursor c = db.getAllTrack();

		getActivity().startManagingCursor(c);
		String[] fieldName = new String[] { "name", "totaldis", "totaltime","create_date" };
		int[] viewId = new int[] { R.id.trackName, R.id.trackDis, R.id.trackTime,R.id.trackDate };

		SimpleCursorAdapter sca = new SimpleCursorAdapter(getActivity(),
				R.layout.activities_item_layout, c, fieldName, viewId);

		ListView list = (ListView) getActivity().findViewById(
				R.id.ListViewFromDB);
		list.setAdapter(sca);

	}
}
