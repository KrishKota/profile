package com.profile.management;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ProfileManagement extends Activity {
	private ListView list_view_profiles;
	private ToggleButton profile_toggle;
	private ProfileDatabase profileDatabase;
	private CustomAdapter customAdapter;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	private ArrayList<HashMap<String, String>> profiles;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_management);
		
		int radius = ProfilePreference.getInteger(getApplicationContext(), ProfilePreference.RADIUS_IN_METER);
		if (radius == 0) {
			ProfilePreference.writeInteger(getApplicationContext(), ProfilePreference.RADIUS_IN_METER, 1000);
		}
		Intent intent = new Intent(ProfileManagement.this, LocationService.class);
		pendingIntent = PendingIntent.getService(ProfileManagement.this, Constants.ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		profileDatabase = new ProfileDatabase(getApplicationContext(), ProfileDatabase.DB_NAME, null, ProfileDatabase.DB_VERSION);
		
		list_view_profiles = (ListView) findViewById(R.id.list_view_profiles);
		profiles = profileDatabase.getProfiles();
		customAdapter = new CustomAdapter(profiles);
		list_view_profiles.setAdapter(customAdapter);
		list_view_profiles.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder editAlert = new AlertDialog.Builder(ProfileManagement.this);
				editAlert.setTitle("Confirmation Alert !!");
				editAlert.setMessage("Are you sure want to delete the profile \'"+profiles.get(arg2).get(ProfileDatabase.PROFILE_NAME)+"\'");
				editAlert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(ProfileManagement.this, CreateNewProfile.class).putExtra("profile", "edit").putExtra("profileId", profileDatabase.getProfiles().get(arg2).get(ProfileDatabase.PROFILE_ID)));
					}
				});
				editAlert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						profileDatabase.deleteProfile(profileDatabase.getProfiles().get(arg2).get(ProfileDatabase.PROFILE_ID));
						profiles = profileDatabase.getProfiles();
						customAdapter = new CustomAdapter(profiles);
						list_view_profiles.setAdapter(customAdapter);
					}
				});
				editAlert.setNeutralButton("Cancel", null);
				editAlert.show();
			}
		});
		profile_toggle = (ToggleButton) findViewById(R.id.profile_toggle);
		
		boolean location_alert = ProfilePreference.getBoolean(getApplicationContext(), ProfilePreference.LOCATION_ALERT);
		profile_toggle.setChecked(location_alert);
		
//		if (location_alert) {
//			list_view_profiles.setEnabled(true);
//		}
//		else {
//			list_view_profiles.setEnabled(false);
//		}
		profile_toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					list_view_profiles.setEnabled(true);
					ProfilePreference.writeBoolean(getApplicationContext(), ProfilePreference.LOCATION_ALERT, isChecked);
					
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, 60*1000, pendingIntent);
					System.out.println("Alarm started");
				}
				else {
					list_view_profiles.setEnabled(false);
					ProfilePreference.writeBoolean(getApplicationContext(), ProfilePreference.LOCATION_ALERT, isChecked);
					
					alarmManager.cancel(pendingIntent);
					stopService(new Intent(ProfileManagement.this, LocationService.class));
					System.out.println("Alarm stopped");
				}
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		profiles = profileDatabase.getProfiles();
		customAdapter = new CustomAdapter(profiles);
		list_view_profiles.setAdapter(customAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_profile_management, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_create_profile:
			
			startActivity(new Intent(ProfileManagement.this, CreateNewProfile.class).putExtra("profile", "new"));
			
			break;
			
		case R.id.menu_settings:
			
			AlertDialog.Builder alert = new AlertDialog.Builder(ProfileManagement.this);
			alert.setTitle("Set radius to change profile based on location");
			
			final EditText edit = new EditText(ProfileManagement.this);
			edit.setText(String.valueOf(ProfilePreference.getInteger(ProfileManagement.this, ProfilePreference.RADIUS_IN_METER)));
			edit.setBackgroundResource(R.drawable.edit_bg);
			edit.setTextColor(Color.BLACK);
			edit.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			alert.setView(edit);
			alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if (TextUtils.isEmpty(edit.getText().toString())) {
						Toast.makeText(ProfileManagement.this, "Given radius is empty and and so radius is set to 1000 metres.", Toast.LENGTH_SHORT).show();
					}
					else {
						ProfilePreference.writeInteger(ProfileManagement.this, ProfilePreference.RADIUS_IN_METER, Integer.valueOf(edit.getText().toString()));
					}
				}
			});
			alert.show();
			
			break;

		}
		return true;
	}
	
	class CustomAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, String>> profiles;

		public CustomAdapter(ArrayList<HashMap<String, String>> profiles) {
			this.profiles = profiles;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return profiles.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = LayoutInflater.from(ProfileManagement.this).inflate(R.layout.profile_list_item, null);
			TextView txt_profile_name = (TextView) view.findViewById(R.id.txt_profile_name);
			txt_profile_name.setText(profiles.get(position).get(ProfileDatabase.PROFILE_NAME));
			
			return view;
		}
		
	}
	
	static class ViewHolder {
		TextView profileName;
	}
	
}
