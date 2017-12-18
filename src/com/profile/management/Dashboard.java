package com.profile.management;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Dashboard extends Activity implements OnClickListener {
	private Button btn_profile_management;
	private Button btn_alerts;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		
		btn_profile_management = (Button) findViewById(R.id.btn_profile_management);
		btn_profile_management.setOnClickListener(this);
		btn_alerts = (Button) findViewById(R.id.btn_alerts);
		btn_alerts.setOnClickListener(this);
		
		int radius = ProfilePreference.getInteger(getApplicationContext(), ProfilePreference.RADIUS_IN_METER);
		if (radius == 0) {
			ProfilePreference.writeInteger(getApplicationContext(), ProfilePreference.RADIUS_IN_METER, 1000);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_profile_management:
			
			startActivity(new Intent(Dashboard.this, ProfileManagement.class));
			
			break;
			
		case R.id.btn_alerts:
			
			break;

		}
	}

}
