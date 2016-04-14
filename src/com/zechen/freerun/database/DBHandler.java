package com.zechen.freerun.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler {

	private static final int DATABASE_VERSION = 2;
	
	private static DBHelper dbHelper;
	private static final String DATABASE_NAME = "freeRun";
	
	
	private static final String TABLE_USER = "User";
	private static final String COL_USER_ID = "user_id";
	private static final String COL_NAME = "username";
	private static final String COL_CREATE_DATE = "create_date";
	private static final String COL_EMAIL = "email";
	
	
	private static final String TABLE_TRACK = "Track";
	private static final String COL_TRACK_ID = "id";
	private static final String COL_TRACKNAME = "name";
	private static final String COL_TRACK_CREATE_DATE = "create_date";
	private static final String COL_CATEGORY = "category";
	private static final String COL_TOTALDIS = "totaldis";
	private static final String COL_TOTALTIME = "totaltime";
	private static final String COL_AVGSPEED = "avgspeed";
	private static final String COL_MAXSPEED = "maxSpeed";
	private static final String COL_USER_TRACK_ID = "userid";
	
	private static final String TABLE_TRACKPOINTS = "Trackpoints";
	private static final String COL_TRACKPOINTS_ID = "trackpoints_id";
	private static final String COL_TRACK_TRACKPOINTS_ID = "track_id";
	private static final String COL_LONGITUDE = "longitude";
	private static final String COL_LATITUDE = "latitude";
	private static final String COL_SPEED = "speed";
	private static final String COL_TIME = "time";
	private ArrayList<String> users = new ArrayList<String>();
	
	public DBHandler(Context context) {
		dbHelper = new DBHelper(context);
	}

	public void createDB(Context context) {
		dbHelper = new DBHelper(context);
	}

	public void createLocalUser(String sysTime) {
		final SQLiteDatabase db = open();
		if (!isUserExist("Local01")) {

			ContentValues cv = new ContentValues();
			cv.put(COL_NAME, "Local01");
			cv.put(COL_EMAIL, "example@yahoo.com");
			cv.put(COL_CREATE_DATE, sysTime);
			db.insert(TABLE_USER, null, cv);
			db.close(); // Closing database connection
			users.add("Local01");
		}
	}
	
	public void  getAllUserToArrayList(){
		final SQLiteDatabase db = open();
		String selectQ = "SELECT i.username FROM " + TABLE_USER
				+ " i ;";
		Cursor cursor = db.rawQuery(selectQ,null);
		if (cursor.moveToFirst()) {
			do {
				users.add(cursor.getString(cursor.getColumnIndex("username")));
			} while (cursor.moveToNext());
		}
	}

	public boolean isUserExist(String username){
		getAllUserToArrayList();
		
		if(users.contains(username)){
			return true;
		}
		else{
			return false;
		}
			
	}

	public void createUser(String username,String email,String sysTime){
		final SQLiteDatabase db = open();

		if (!isUserExist(username)) {
			ContentValues cv = new ContentValues();
			cv.put(COL_NAME, username);
			cv.put(COL_EMAIL, email);
			cv.put(COL_CREATE_DATE, sysTime);
			db.insert(TABLE_USER, null, cv);
			db.close(); // Closing database connection
			users.add(username);
		}
		
	}

	public int updateUserInfo(String username,String email,String sysTime) {
		final SQLiteDatabase db = open();

		ContentValues cv = new ContentValues();
		cv.put(COL_NAME, username);
		cv.put(COL_EMAIL, email);
		cv.put(COL_CREATE_DATE, sysTime);
		int r = db.update(TABLE_USER, cv, "username = ?",
				new String[] { username });
		db.close(); // Closing database connection

		return r;

	}
	
	public void addTrackpoints(double longitude,double latitude,String time, double speed,int trackid ){
		final SQLiteDatabase db = open();
		
			ContentValues cv = new ContentValues();			
			cv.put(COL_TRACK_TRACKPOINTS_ID, trackid);
			cv.put(COL_LONGITUDE, longitude);
			cv.put(COL_LATITUDE, latitude);
			cv.put(COL_SPEED, speed);
			cv.put(COL_TIME, time);
			
			db.insert(TABLE_TRACKPOINTS, null, cv);
			db.close(); // Closing database connection
		
	}
	
	public double getMaxSpeedByTrackId(int trackID) {
		String selectQ = "SELECT MAX(speed) FROM " + TABLE_TRACKPOINTS
				+ " where track_id = ? ;";
		double maxspeed = 0;
		String track_id = String.valueOf(trackID);
		final SQLiteDatabase db = open();
		Cursor cursor = db.rawQuery(selectQ,new String[]{ track_id });
		
		if (cursor.getCount()!=0) {
			if (cursor.moveToFirst()) {
					maxspeed = Double.valueOf(cursor.getString(0));
			}
		}
		
		return maxspeed;
	}
	
	
	
	public String getMaxSpeed() {
		String max = null;
		String selectQ = "SELECT MAX(speed) FROM " + TABLE_TRACKPOINTS
				+ " ;";
		final SQLiteDatabase db = open();

		Cursor cursor = db.rawQuery(selectQ,null);
		
		if (cursor.getCount()!=0) {
			if (cursor.moveToFirst()) {
					max = cursor.getString(0);
			}
		}
		return max;
	}

	public int  addNewTrack(String category,String trackName, String sysTime,int userid){
		
		final SQLiteDatabase db = open();
		
		ContentValues cv = new ContentValues();			
		cv.put(COL_CATEGORY, category);
		cv.put(COL_TRACKNAME, trackName);
		cv.put(COL_USER_TRACK_ID, userid);
		cv.put(COL_TRACK_CREATE_DATE, sysTime);
		
		int r = (int) db.insert(TABLE_TRACK, null, cv);
		db.close(); // Closing database connection
		return r;
	}
	
	public int updateTrack(String category,String trackName,double totaldis,int totalTime,double avgspeed,double maxspeed, String sysTime,int userid){
		final SQLiteDatabase db = open();

		ContentValues cv = new ContentValues();
		cv.put(COL_CATEGORY, category);
		cv.put(COL_TRACKNAME, trackName);
		cv.put(COL_TOTALDIS, totaldis);
		cv.put(COL_TOTALTIME, totalTime);
		cv.put(COL_AVGSPEED, avgspeed);
		cv.put(COL_MAXSPEED, maxspeed);
		cv.put(COL_USER_TRACK_ID, userid);
		cv.put(COL_TRACK_CREATE_DATE, sysTime);
		// updating row
		int r = db.update(TABLE_TRACK, cv, "name = ?",
				new String[] { trackName });
		db.close();
		return r;
	}
	
	public Cursor getTrackInfo(String name){
		String selectQ = "SELECT i.id AS _id,i.create_date,i.name,i.totaldis,i.totaltime,i.avgspeed,i.maxSpeed,i.category FROM "
				+ TABLE_TRACK + " i where i.name = ? ;";
		final SQLiteDatabase db = open();
		Cursor cursor = db.rawQuery(selectQ,new String[]{name});
		
		return cursor;
		
	}
	public Cursor getAllTrack() {
		// TODO Auto-generated method stub
		String selectQ = "SELECT i.id AS _id,i.create_date,i.name,i.totaldis,i.totaltime FROM "
				+ TABLE_TRACK + " i ;";
		final SQLiteDatabase db = open();
		Cursor cursor = db.rawQuery(selectQ,null);
		
		return cursor;
	}
	public String getlargestDisRun(){
		final SQLiteDatabase db = open();

		String distance="";
		String selectQ = "SELECT MAX(totaldis) FROM " + TABLE_TRACK
				+ " ;";
		Cursor cursor = db.rawQuery(selectQ,null);
		
		if (cursor.getCount()!=0) {
			if (cursor.moveToFirst()) {
				distance = cursor.getString(0);
			}
		}
		
		return distance;
	}
	
	public String getLongestTimeRun() {
		// TODO Auto-generated method stub
		final SQLiteDatabase db = open();

		String time="";
		String selectQ = "SELECT MAX(totaltime) FROM " + TABLE_TRACK
				+ " ;";
		Cursor cursor = db.rawQuery(selectQ,null);
		
		if (cursor.getCount()!=0) {
			if (cursor.moveToFirst()) {
				time = cursor.getString(0);
			}
		}
		
		return time;
	}
	
	
	
	public Cursor getTrackPointsByTrackName(String trackName){
		int trackID = 0;
		String getTrackID = "SELECT i.id FROM "
				+ TABLE_TRACK + " i where i.name = ? ;";
		String getTrackPoints = "SELECT i.* FROM "+ TABLE_TRACKPOINTS + " i WHERE i.track_id = ? ;";
		final SQLiteDatabase db = open();
		Cursor cursor = db.rawQuery(getTrackID,new String[]{String.valueOf(trackName)});
		
		if (cursor.getCount()!=0) {
			if (cursor.moveToFirst()) {
				trackID = Integer.valueOf(cursor.getString(0));
			}
		}
		Cursor cursorTrackPoints = db.rawQuery(getTrackPoints,new String[]{String.valueOf(trackID)});
		return cursorTrackPoints;
	}
	
	public int completeFinishedTrack(String trackName,double totaldis,int totalTime,double avgspeed,double maxspeed){
		final SQLiteDatabase db = open();
		String selectS = "SELECT i.category,i.create_date,i.userid FROM "
				+ TABLE_TRACK + " i WHERE i.name = ?"; 
		Cursor cursor = db.rawQuery(selectS, new String[] { trackName });
		
		ContentValues cv = new ContentValues();
		if (cursor.moveToFirst()) {
			do {
				cv.put(COL_CATEGORY, cursor
						.getString(cursor.getColumnIndex("category")));
				cv.put(COL_TRACK_CREATE_DATE, cursor
						.getString(cursor.getColumnIndex("create_date")));
				cv.put(COL_USER_TRACK_ID, Integer.parseInt(cursor
						.getString(cursor.getColumnIndex("userid"))));
			}while(cursor.moveToNext());
		}
		cv.put(COL_TRACKNAME, trackName);
		cv.put(COL_TOTALDIS, totaldis);
		cv.put(COL_TOTALTIME, totalTime);
		cv.put(COL_AVGSPEED, avgspeed);
		cv.put(COL_MAXSPEED, maxspeed);
		// updating row
		int r = db.update(TABLE_TRACK, cv, "name = ?",
				new String[] { trackName });
		db.close();
		return r;
	}
	
	public int deleteTrack(String name){
		final SQLiteDatabase db = open();

		int r = db.delete(TABLE_TRACK, "name = ?",
					new String[] { name });
			db.close();
		return r;
	}
	
	private static synchronized SQLiteDatabase open()throws SQLException{
		return dbHelper.getWritableDatabase();
		
	}
	
	
	/** Main Database **/
	 class DBHelper extends SQLiteOpenHelper {
		
		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
		
		private static final String CREATE_USER_TABLE = "create table "
				+ TABLE_USER + "(" 
				+ COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ COL_NAME + " TEXT NOT NULL,"
				+ COL_EMAIL + " TEXT UNIQUE NOT NULL,"
				+ COL_CREATE_DATE + " TEXT );";

		private static final String CREATE_TRACK_TABLE = "create table " 
				+ TABLE_TRACK + "("
				+ COL_TRACK_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ COL_CATEGORY + " TEXT NOT NULL ,"
				+ COL_TRACKNAME + " TEXT UNIQUE NOT NULL ,"
				+ COL_TOTALDIS + " REAL ,"
				+ COL_TOTALTIME + " INTEGER ,"
				+ COL_AVGSPEED + " REAL ,"
				+ COL_MAXSPEED + " REAL ,"
				+ COL_USER_TRACK_ID + " INTEGER NOT NULL REFERENCES "+ TABLE_USER +"(user_id),"
				+ COL_TRACK_CREATE_DATE + " TEXT );";
		
		
		private static final String CREATE_TRACKPOINTS_TABLE = "create table " 
				+ TABLE_TRACKPOINTS +"("
				+ COL_TRACKPOINTS_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ COL_TRACK_TRACKPOINTS_ID + " INTEGER REFERENCES " + TABLE_TRACK + "(" + COL_TRACK_ID +"),"
				+ COL_LONGITUDE + " INTEGER NOT NULL, "
				+ COL_LATITUDE + " INTEGER NOT NULL, "
				+ COL_SPEED + " REAL, "
				+ COL_TIME + " TEXT );";
		
		private final String[] ALL_TABLES = {TABLE_TRACK,TABLE_USER,TABLE_TRACKPOINTS};
		
		
		


		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.beginTransaction();
			try {
				db.execSQL(CREATE_USER_TABLE);
				db.execSQL(CREATE_TRACK_TABLE);
				db.execSQL(CREATE_TRACKPOINTS_TABLE);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			
		}

		
		
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			for (String table : ALL_TABLES) {
				db.execSQL("DROP TABLE IF EXISTS " + table);
			}
			onCreate(db);
			
		}

	}


	


	
}
