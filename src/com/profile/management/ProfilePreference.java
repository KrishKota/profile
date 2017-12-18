package com.profile.management;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ProfilePreference {
	public static final String PREF_NAME = "ProfManagement";
	public static final int PREF_MODE = Context.MODE_PRIVATE;
	public static final String LOCATION_ALERT = "LOCATION_ALERT";
	public static final String RADIUS_IN_METER = "RADIUS_IN_METER";
	
	public static SharedPreferences getSharedPreferences(Context context) {
		
		return context.getSharedPreferences(ProfilePreference.PREF_NAME, PREF_MODE);
	}
	
	/**
	 * 
	 * @param context
	 * @return editor of the preference
	 */
	public static Editor getEditor(Context context) {
		
		return getSharedPreferences(context).edit();
	}
	
	/**
	 * 
	 * @param context
	 * @param prefernce key
	 * @param value to store
	 */
	public static void writeString(Context context, String key, String value) {
		getSharedPreferences(context).edit().putString(key, value).commit();
	}
	
	/**
	 * 
	 * @param context
	 * @param preference key to get string value
	 * @return string value for the key
	 */
	public static String getString(Context context, String key) {
		
		return getSharedPreferences(context).getString(key, "");
	}
	
	/**
	 * 
	 * @param context
	 * @param preference key
	 * @param boolean value to store
	 */
	public static void writeBoolean(Context context, String key, boolean value) {
		getSharedPreferences(context).edit().putBoolean(key, value).commit();
	}
	
	/**
	 * 
	 * @param context
	 * @param preference key to get boolean value
	 * @return boolean value for the key
	 */
	public static boolean getBoolean(Context context, String key) {
		System.out.println("Get boolean value");
		
		return getSharedPreferences(context).getBoolean(key, false);
	}
	
	/**
	 * 
	 * @param context
	 * @param prefernce key
	 * @param value to store
	 */
	public static void writeInteger(Context context, String key, int value) {
		getSharedPreferences(context).edit().putInt(key, value).commit();
	}
	
	/**
	 * 
	 * @param context
	 * @param preference key to get string value
	 * @return string value for the key
	 */
	public static int getInteger(Context context, String key) {
		
		return getSharedPreferences(context).getInt(key, 0);
	}

}
