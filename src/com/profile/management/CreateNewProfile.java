package com.profile.management;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateNewProfile extends Activity implements OnClickListener {
	private Button btn_save_profile;
	private TextView txt_ringtone, txt_wallpaper, txt_location;
	private EditText edit_profile_name;
	private Spinner spn_profile_mode;
	public static int REQUEST_CODE_RINGTONE = 1;
	public static int REQUEST_CODE__OTHER_RINGTONE = 2;
	public static int REQUEST_CODE_LOCATION = 3;
	public static int PICK_WALLPAPER = 4;
	Bitmap bm;
	private String str_ringtone = null, str_wallpaper = null, location = null;
	private double latitude = 0.0, longitude = 0.0;
	private ProfileDatabase profileDatabase;
	private ArrayList<HashMap<String, String>> getProfile;
	private String alert_title = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_new_profile);

		edit_profile_name = (EditText) findViewById(R.id.edit_profile_name);
		btn_save_profile = (Button) findViewById(R.id.btn_save_profile);
		btn_save_profile.setOnClickListener(this);
		txt_ringtone = (TextView) findViewById(R.id.txt_ringtone);
		txt_ringtone.setOnClickListener(this);
		txt_wallpaper = (TextView) findViewById(R.id.txt_wallpaper);
		txt_wallpaper.setOnClickListener(this);
		txt_location = (TextView) findViewById(R.id.txt_location);
		txt_location.setOnClickListener(this);
		
		spn_profile_mode = (Spinner) findViewById(R.id.spn_profile_mode);

		profileDatabase = new ProfileDatabase(getApplicationContext(), ProfileDatabase.DB_NAME, null, ProfileDatabase.DB_VERSION);

		getProfile = profileDatabase.getProfileWithId(getIntent().getStringExtra("profileId"));

		if (getIntent().getStringExtra("profile").equals("edit")) {

			latitude = Double.valueOf(getProfile.get(0).get(ProfileDatabase.LATITUDE));
			longitude = Double.valueOf(getProfile.get(0).get(ProfileDatabase.LONGITUDE));

			edit_profile_name.setText(getProfile.get(0).get(ProfileDatabase.PROFILE_NAME));

			str_ringtone = Uri.parse(getProfile.get(0).get(ProfileDatabase.RINGTONE_PATH)).toString();
			txt_ringtone.setText(getFilename("Audio", Uri.parse(str_ringtone)));

			str_wallpaper = Uri.parse(getProfile.get(0).get(ProfileDatabase.WALLPAPER_PATH)).toString();

			txt_wallpaper.setText(getFilename("Image", Uri.parse(getProfile.get(0).get(ProfileDatabase.WALLPAPER_PATH))));

			location = getProfile.get(0).get(ProfileDatabase.ADDRESS);
			txt_location.setText(location);
			
			if (getProfile.get(0).get(ProfileDatabase.PROFILE_MODE).equals("Sound")) {
				spn_profile_mode.setSelection(1);
			}

			btn_save_profile.setText("Update profile");
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.txt_location:

			startActivityForResult(new Intent(CreateNewProfile.this, GetLocationFromMap.class), REQUEST_CODE_LOCATION);
			break;

		case R.id.btn_save_profile:

			if (TextUtils.isEmpty(edit_profile_name.getText().toString())) {
				Toast.makeText(CreateNewProfile.this, "Please enter Profile name", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(str_ringtone)) {
				Toast.makeText(CreateNewProfile.this, "Please select Ringtone", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(str_wallpaper)) {
				Toast.makeText(CreateNewProfile.this, "Please select Wallpaper", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(txt_location.getText().toString())) {
				Toast.makeText(CreateNewProfile.this, "Please enter location", Toast.LENGTH_SHORT).show();
			} else {

				if (btn_save_profile.getText().toString().equals("Save Profile")) {
					System.out.println("Button save");
					alert_title = "New Profile has been saved";

					profileDatabase.createNewProfile(String.valueOf(System.currentTimeMillis()), edit_profile_name.getText().toString(),
							str_ringtone, str_wallpaper, String.valueOf(latitude), String.valueOf(longitude), txt_location.getText().toString(), spn_profile_mode.getSelectedItem().toString());

				} else if (btn_save_profile.getText().toString().equals("Update profile")) {

					alert_title = "Profile has been updated";

					System.out.println("Ringtone " + str_ringtone + " Wallpaper " + str_wallpaper + " Latitude " + latitude + " Longitude "
							+ longitude);

					profileDatabase.updateProfile(getIntent().getStringExtra("profileId"), edit_profile_name.getText().toString(), str_ringtone,
							str_wallpaper, String.valueOf(latitude), String.valueOf(longitude), txt_location.getText().toString(), spn_profile_mode.getSelectedItem().toString());
				}

				if (alert_title != null) {

					AlertDialog.Builder alert = new AlertDialog.Builder(CreateNewProfile.this);

					alert.setTitle(alert_title);
					alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					alert.show();
				}
			}

			break;

		case R.id.txt_ringtone:

			AlertDialog.Builder ringtonePicker = new AlertDialog.Builder(CreateNewProfile.this);
			ringtonePicker.setTitle("Choose option to pick ringtone");
			ringtonePicker.setPositiveButton("Default Ringtone", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
					intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, RingtoneManager.TYPE_RINGTONE);
					intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.TYPE_RINGTONE);
					startActivityForResult(intent, REQUEST_CODE_RINGTONE);
				}
			});
			ringtonePicker.setNegativeButton("Pick from file manager", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("audio/*");
					startActivityForResult(Intent.createChooser(intent, "Choose music player"), REQUEST_CODE__OTHER_RINGTONE);
				}
			});
			ringtonePicker.show();

			break;
		case R.id.txt_wallpaper:

			Intent wallpaperIntent = new Intent(Intent.ACTION_GET_CONTENT);
			wallpaperIntent.setType("image/*");
			startActivityForResult(Intent.createChooser(wallpaperIntent, "Choose Gallery"), PICK_WALLPAPER);

			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_RINGTONE) {

			switch (resultCode) {
			case RESULT_OK:

				Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				txt_ringtone.setText(getFilename("Audio", uri));
				str_ringtone = uri.toString();
				System.out.println("Ringtone URI: " + uri.toString());

				break;

			case RESULT_CANCELED:

				break;

			}
		}
		if (requestCode == REQUEST_CODE__OTHER_RINGTONE) {

			switch (resultCode) {
			case RESULT_OK:
				System.out.println(" Mp3 from file manager>>>>>>" + data.getData());
				txt_ringtone.setText(getFilename("Audio", data.getData()));
				str_ringtone = data.getData().toString();

				break;

			case RESULT_CANCELED:

				break;

			}
		}

		if (requestCode == PICK_WALLPAPER) {

			switch (resultCode) {
			case RESULT_OK:
				System.out.println("Image picked >>>>>>" + data.getData());
				txt_wallpaper.setText(getFilename("Image", data.getData()));
				str_wallpaper = data.getData().toString();

				break;

			case RESULT_CANCELED:

				break;

			}
		}
		if (requestCode == REQUEST_CODE_LOCATION) {

			switch (resultCode) {
			case RESULT_OK:

				location = data.getStringExtra("address");
				latitude = data.getDoubleExtra("latitude", 0.0);
				longitude = data.getDoubleExtra("longitude", 0.0);
				txt_location.setText(data.getStringExtra("address"));

				break;

			case RESULT_CANCELED:

				break;

			}
		}

	}

	/**
	 * 
	 * @param context
	 * @param Intent
	 *            data got from activity result
	 * @return picture path
	 */
	public static String selectedWallpaperImage(Context context, Intent data) {

		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = new File(cursor.getString(columnIndex)).getAbsolutePath();
		System.out.println("Picture path>>>>>>" + picturePath);
		cursor.close();

		return picturePath;

	}

	public String getFilename(String fileType, Uri uri) {
		/*
		 * Intent intent = getIntent(); String name =
		 * intent.getData().getLastPathSegment(); return name;
		 */
		String fileName = null;
		String scheme = uri.getScheme();
		if (scheme.equals("file")) {
			fileName = uri.getLastPathSegment();
		} else if (scheme.equals("content")) {
			String[] proj = { MediaStore.Video.Media.TITLE };
			Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
			if (cursor != null && cursor.getCount() != 0) {
				int columnIndex = 0;
				if (fileType.equals("Audio")) {
					columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
				} else if (fileType.equals("Image")) {
					columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
				}
				cursor.moveToFirst();
				fileName = cursor.getString(columnIndex);

				cursor.close();
			}
		}
		return fileName;
	}

}
