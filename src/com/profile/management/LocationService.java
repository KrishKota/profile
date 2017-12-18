package com.profile.management;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

public class LocationService extends Service implements LocationListener {
	private double lat, lang;
	private LocationManager locationManager;
	private boolean isNetworkEnabled = false;
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // 10
																		// meters
	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	private ProfileDatabase profileDatabase;
	private Context context;
	private PendingIntent pendingIntent;
	private NotificationCompat.Builder notification;
	private NotificationManager mNotifyMgr;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;

		mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	/**
	 * 
	 * @param Latitude of source location
	 * @param Longitude of source location
	 * @param Latitude of destination
	 * @param Longitude of destination
	 * @return distance in metres
	 */
	public static double calculateDistance(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {

		float results[] = new float[1];

		try {
			Location.distanceBetween(fromLatitude, fromLongitude, toLatitude, toLongitude, results);
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
		}

		int dist = (int) results[0];
		// if (dist <= 0)
		// return 0D;

		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		results[0] /= 1000D;
		String distance = decimalFormat.format(results[0]);
		double d = Double.parseDouble(distance);
		System.out.println("Distance in meters: " + dist + " Distance in KM: " + d);
		return dist;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		System.out.println("Location Service onStart");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.

		System.out.println("Alarm received");

		profileDatabase = new ProfileDatabase(getApplicationContext(), ProfileDatabase.DB_NAME, null, ProfileDatabase.DB_VERSION);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (isNetworkEnabled) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		}
		return START_STICKY;
	}

	@Override
	public void onLocationChanged(final Location location) {
		// TODO Auto-generated method stub
		lat = location.getLatitude();
		lang = location.getLongitude();

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (location != null) {
					lat = location.getLatitude();
					lang = location.getLongitude();
					ArrayList<HashMap<String, String>> loc = profileDatabase.getLocation();
					for (int i = 0; i < loc.size(); i++) {
						double distance = calculateDistance(lat, lang, Double.valueOf(loc.get(i).get(ProfileDatabase.LATITUDE)),
								Double.valueOf(loc.get(i).get(ProfileDatabase.LONGITUDE)));
						System.out.println(" Current Latitude : "+lat+" longitude : "+lang);
						System.out.println("Dest lat : "+Double.valueOf(loc.get(i).get(ProfileDatabase.LATITUDE))+" lng : "+Double.valueOf(loc.get(i).get(ProfileDatabase.LONGITUDE)));
						System.out.println(loc.get(i).get(ProfileDatabase.PROFILE_NAME) + "Distance>>>>" + distance);

						int radius = ProfilePreference.getInteger(context, ProfilePreference.RADIUS_IN_METER);

						if (distance < radius) {

							Intent intent = new Intent(LocationService.this, ProfileManagement.class);

							pendingIntent = PendingIntent.getActivity(context, Constants.NOTIFICATION_ID, intent,
									PendingIntent.FLAG_UPDATE_CURRENT);

							notification = new NotificationCompat.Builder(context).setContentIntent(pendingIntent)
									.setSmallIcon(R.drawable.ic_launcher).setContentTitle("Location based profile ON")
									.setContentText("Current Profile : " + loc.get(i).get(ProfileDatabase.PROFILE_NAME))
									.setWhen(System.currentTimeMillis())
									.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
									.setTicker("Profile Updated");
							notification.setOngoing(true);
							mNotifyMgr.notify(Constants.NOTIFICATION_ID, notification.build());

							AudioManager mode = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
							if (loc.get(i).get(ProfileDatabase.PROFILE_MODE).equals("Silent")) {
								mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
							}
							else if (loc.get(i).get(ProfileDatabase.PROFILE_MODE).equals("Sound")) {
								mode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
							}
							
							try {
								RingtoneManager.setActualDefaultRingtoneUri(LocationService.this, RingtoneManager.TYPE_RINGTONE,
										Uri.parse(loc.get(i).get(ProfileDatabase.RINGTONE_PATH)));
							} catch (Exception localException) {
								System.out.println("Set ringtone exception: " + localException.toString());
							}

							try {
								String file_path = selectedWallpaperImage(LocationService.this,
										Uri.parse(loc.get(i).get(ProfileDatabase.WALLPAPER_PATH)));

								Bitmap bm = BitmapFactory.decodeFile(file_path);

								WallpaperManager wallpaperManager = WallpaperManager.getInstance(LocationService.this);
								Bitmap userBm = Bitmap.createScaledBitmap(bm, wallpaperManager.getDesiredMinimumWidth(),
										wallpaperManager.getDesiredMinimumHeight(), false);

								if (bm != null)
									wallpaperManager.setBitmap(userBm);

							} catch (IOException e) { // TODO
								e.printStackTrace();
								break;
							}
							break;
						}
					}
				}
			}
			
		}).start();
	
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param context
	 * @param Uri of the image
	 * @return Path of the picture
	 */
	public static String selectedWallpaperImage(Context context, Uri data) {

		Uri selectedImage = data;
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = new File(cursor.getString(columnIndex)).getAbsolutePath();
		System.out.println("Picture path>>>>>>" + picturePath);
		cursor.close();

		return picturePath;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mNotifyMgr.cancel(Constants.NOTIFICATION_ID);
		locationManager.removeUpdates(this);
	}
}
