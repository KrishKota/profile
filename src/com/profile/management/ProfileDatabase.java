package com.profile.management;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfileDatabase extends SQLiteOpenHelper {
	public static String DB_NAME = "PROFILE_MANAGEMENT";
	public static final String PROFILE_ID = "PROFILE_ID";
	public static final String PROFILE_NAME = "PROFILE_NAME";
	public static final String RINGTONE_PATH = "RINGTONE_PATH";
	public static final String WALLPAPER_PATH = "WALLPAPER_PATH";
	public static final String LATITUDE = "LATITUDE";
	public static final String LONGITUDE = "LONGITUDE";
	public static final String ADDRESS = "ADDRESS";
	public static final String PROFILE_MODE = "PROFILE_MODE";
	public static int DB_VERSION = 1;
	private Context mContext;

	public ProfileDatabase(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		InputStream inputStream = null;
		BufferedReader reader = null;
		String line;

		try {
			inputStream = mContext.getAssets().open("PROFILE_MANAGEMENT.txt");
			reader = new BufferedReader(new InputStreamReader(inputStream));
			do {

				line = reader.readLine();
				if (line != null) {
					db.execSQL(line);
				}
				System.out.println("Query from file: " + line);
			}
			while (line != null);

			inputStream.close();
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
//			db.close();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void createNewProfile(String profileId, String profileName, String ringtonePath, String wallpaperPath, String latitude, String longitude,
			String address, String profile_mode) {
		SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(PROFILE_ID, profileId);
		contentValues.put(PROFILE_NAME, profileName);
		contentValues.put(RINGTONE_PATH, ringtonePath);
		contentValues.put(WALLPAPER_PATH, wallpaperPath);
		contentValues.put(LATITUDE, latitude);
		contentValues.put(LONGITUDE, longitude);
		contentValues.put(ADDRESS, address);
		contentValues.put(PROFILE_MODE, profile_mode);
		db.insert("Profile", null, contentValues);
		db.close();
	}

	public void updateProfile(String profileId, String profileName, String ringtonePath, String wallpaperPath, String latitude, String longitude,
			String address, String profile_mode) {
		SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(PROFILE_NAME, profileName);
		contentValues.put(RINGTONE_PATH, ringtonePath);
		contentValues.put(WALLPAPER_PATH, wallpaperPath);
		contentValues.put(LATITUDE, latitude);
		contentValues.put(LONGITUDE, longitude);
		contentValues.put(ADDRESS, address);
		contentValues.put(PROFILE_MODE, profile_mode);

		String where = "PROFILE_ID=?"; // The where clause to identify which
										// columns to update.
		String[] value = { profileId };

		db.update("Profile", contentValues, where, value);
	}

	public ArrayList<HashMap<String, String>> getProfiles() {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<HashMap<String, String>> list_profile = new ArrayList<HashMap<String, String>>();
		Cursor cursor = db.rawQuery("SELECT * FROM Profile", null);
		while (cursor.moveToNext()) {
			HashMap<String, String> map_profile = new HashMap<String, String>();
			map_profile.put(PROFILE_ID, cursor.getString(cursor.getColumnIndex(PROFILE_ID)));
			map_profile.put(PROFILE_NAME, cursor.getString(cursor.getColumnIndex(PROFILE_NAME)));
			list_profile.add(map_profile);
		}
		cursor.close();
		db.close();

		return list_profile;
	}

	public ArrayList<HashMap<String, String>> getProfileWithId(String profileId) {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<HashMap<String, String>> list_profile = new ArrayList<HashMap<String, String>>();
		Cursor cursor = db.rawQuery("SELECT * FROM Profile WHERE PROFILE_ID='" + profileId + "'", null);
		while (cursor.moveToNext()) {
			HashMap<String, String> map_profile = new HashMap<String, String>();
			map_profile.put(PROFILE_ID, cursor.getString(cursor.getColumnIndex(PROFILE_ID)));
			map_profile.put(PROFILE_NAME, cursor.getString(cursor.getColumnIndex(PROFILE_NAME)));
			map_profile.put(RINGTONE_PATH, cursor.getString(cursor.getColumnIndex(RINGTONE_PATH)));
			map_profile.put(WALLPAPER_PATH, cursor.getString(cursor.getColumnIndex(WALLPAPER_PATH)));
			map_profile.put(LATITUDE, cursor.getString(cursor.getColumnIndex(LATITUDE)));
			map_profile.put(LONGITUDE, cursor.getString(cursor.getColumnIndex(LONGITUDE)));
			map_profile.put(ADDRESS, cursor.getString(cursor.getColumnIndex(ADDRESS)));
			map_profile.put(PROFILE_MODE, cursor.getString(cursor.getColumnIndex(PROFILE_MODE)));
			list_profile.add(map_profile);
		}
		cursor.close();
		db.close();

		return list_profile;
	}

	public ArrayList<HashMap<String, String>> getLocation() {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<HashMap<String, String>> list_profile = new ArrayList<HashMap<String, String>>();
		Cursor cursor = db.rawQuery("SELECT * FROM Profile", null);
		while (cursor.moveToNext()) {
			HashMap<String, String> map_profile = new HashMap<String, String>();
			map_profile.put(PROFILE_ID, cursor.getString(cursor.getColumnIndex(PROFILE_ID)));
			map_profile.put(PROFILE_NAME, cursor.getString(cursor.getColumnIndex(PROFILE_NAME)));
			map_profile.put(LATITUDE, cursor.getString(cursor.getColumnIndex(LATITUDE)));
			map_profile.put(LONGITUDE, cursor.getString(cursor.getColumnIndex(LONGITUDE)));
			map_profile.put(RINGTONE_PATH, cursor.getString(cursor.getColumnIndex(RINGTONE_PATH)));
			map_profile.put(WALLPAPER_PATH, cursor.getString(cursor.getColumnIndex(WALLPAPER_PATH)));
			map_profile.put(PROFILE_MODE, cursor.getString(cursor.getColumnIndex(PROFILE_MODE)));
			list_profile.add(map_profile);
		}
		cursor.close();
		db.close();

		return list_profile;
	}

	public void deleteProfile(String profileId) {
		SQLiteDatabase db = this.getReadableDatabase();
		String where = "PROFILE_ID=?";
		String[] value = { profileId };
		db.delete("Profile", where, value);
	}
}
