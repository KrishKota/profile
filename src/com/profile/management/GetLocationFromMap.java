package com.profile.management;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GetLocationFromMap extends FragmentActivity implements LocationSource {

	private LocationManager locMan;
	private MyLocationListener locListener;
	private MarkerOptions markerOption;
	private Marker marker;
	private GetAddress getAddress;
	private String addr = null;
	private double latitude = 0.0, longitude = 0.0;
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_location_from_map);

		locListener = new MyLocationListener();
		locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		setUpMapIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();

	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.setLocationSource(this);
		mMap.setMyLocationEnabled(true);
		mMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

			@Override
			public boolean onMyLocationButtonClick() {

				locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5 * 6000, 1000, locListener);

				return true;
			}
		});
		locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5 * 6000, 1000, locListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (locListener != null)
			locMan.removeUpdates(locListener);
		
		if (mMap != null)
			mMap.clear();
	}

	@Override
	public void activate(OnLocationChangedListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	public String getLocation(double lat, double lan) {

		String address = null;

		Geocoder geoCoder = new Geocoder(GetLocationFromMap.this);
		try {
			Address addr = geoCoder.getFromLocation(lat, lan, 1).get(0);
			address = addr.getAddressLine(0);
		}
		catch (Exception e) {
			return address;
		}

		return address;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.get_location_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.done:

			if (addr == null) {
				Toast.makeText(GetLocationFromMap.this, "Address is empty", Toast.LENGTH_SHORT).show();
			}
			else {

				System.out.println("Latitude : "+latitude+" Longitude : "+longitude);
				Intent data_intent = new Intent();
				data_intent.putExtra("address", addr);
				data_intent.putExtra("latitude", latitude);
				data_intent.putExtra("longitude", longitude);
				setResult(RESULT_OK, data_intent);
				finish();
			}

			break;

		}
		return true;
	}

	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			mMap.clear();
			LatLng latLng = new LatLng(latitude, longitude);
			markerOption = new MarkerOptions().position(latLng).title("Current location").draggable(true);
			marker = mMap.addMarker(markerOption);
			marker.showInfoWindow();
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
			mMap.setOnMarkerDragListener(new OnMarkerDragListener() {

				@Override
				public void onMarkerDragStart(Marker arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onMarkerDragEnd(Marker arg0) {
					marker.setTitle("Loading address...");
					marker.showInfoWindow();

					LatLng lt = new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude);
					latitude = arg0.getPosition().latitude;
					longitude = arg0.getPosition().longitude;
					
					if (getAddress == null) {
						getAddress = new GetAddress();
						getAddress.execute(lt);
					}
					else {
						getAddress.cancel(true);
						getAddress = new GetAddress();
						getAddress.execute(lt);
					}

				}

				@Override
				public void onMarkerDrag(Marker arg0) {
					// TODO Auto-generated method stub

				}
			});
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

	}

	class GetAddress extends AsyncTask<LatLng, Void, String> {

		@Override
		protected String doInBackground(LatLng... params) {
			LatLng lt = params[0];
			String loc = getLocation(lt.latitude, lt.longitude);
			return loc;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null)
				result = "Failed to fetch location";
			addr = result;
			marker.setTitle(result);
			marker.showInfoWindow();
		}

	}

}
