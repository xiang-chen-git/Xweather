package com.spring.weather;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.spring.weather.adapter.WeatherFragmentAdapter;
import com.spring.weather.app.Db;
import com.spring.weather.app.JSONParser;
import com.spring.weather.app.TypefaceSpan;
import com.spring.weather.app.UserLocationListener;
import com.spring.weather.app.Utils;
import com.spring.weather.model.City;
import com.spring.weather.model.Weather;
import com.spring.weather.model.WeatherNext;
import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends ActionBarActivity {
	Db db;
	LinearLayout main;
	ViewPager mPager;
	SystemBarTintManager tintManager;
	WeatherFragmentAdapter adapter;
	CirclePageIndicator mIndicator;
	Context context = this;
	ProgressDialog progress;
	static Weather WeatherNow;
	static ArrayList<WeatherNext> next;
	public static ArrayList<City> citydata;
	
	public static ArrayList<City> getCitydata() {
		return citydata;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatusAndNavigation(this);
		}
		main = (LinearLayout) findViewById(R.id.container);
        mPager = (ViewPager)findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);

		tintManager = new SystemBarTintManager(this);
	    tintManager.setStatusBarTintEnabled(true);
	    tintManager.setNavigationBarTintEnabled(true);
	    tintManager.setStatusBarTintColor(Color.parseColor(Utils.getColor(context)));
	    
	    tintManager.setNavigationBarTintColor(Color.parseColor(Utils.getColor(context)));
	    
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			addPadding(tintManager.getConfig().getActionBarHeight()+tintManager.getConfig().getStatusBarHeight());
		}else{
			addPadding(tintManager.getConfig().getActionBarHeight());
		}
	    

	    final TelephonyManager tm =(TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

	    String deviceid = tm.getDeviceId();
	    
	    AdView adView = (AdView) findViewById(R.id.ad);
	    AdRequest adRequest = new AdRequest.Builder()
	        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        .addTestDevice(deviceid)
	        .build();
	    adView.loadAd(adRequest);
	}

	@Override
	protected void onResume() {
		super.onResume();
		main.setBackgroundColor(Color.parseColor(Utils.getColor(context)));
		tintManager.setStatusBarTintColor(Color.parseColor(Utils.getColor(context)));
	    tintManager.setNavigationBarTintColor(Color.parseColor(Utils.getColor(context)));
		
	    refreshData();
	}
	
	private void refreshData() {
		if(Utils.isNetworkAvailable(this)){
			progress = new ProgressDialog(context);
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setCancelable(false);
			progress.show();

			db = new Db(context);
			if(Utils.getSettingsBoolean("use_location", context)){
				progress.setMessage("Searching for user location...");
				LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
				LocationListener mlocListener = new UserLocationListener(context);
				mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
				Location user;
				user = Utils.getUserLocation(context);
				
				while (user==null) {
				    try {
						Thread.sleep(1000);
						user = Utils.getUserLocation(context);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}; 
				new GetData().execute(String.valueOf(user.getLatitude()),String.valueOf(user.getLongitude()),Utils.getSettings("units", context));
			}else{
				new GetData().execute(Utils.loadLastCityData(context).getId(),Utils.getSettings("units", context));
			}	
		}else{
			Toast.makeText(context, "No Internet", Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(context,SettingsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	private class GetData extends AsyncTask<String, String, String> {
		
		String weatherUrl,weatherNextUrl;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress.setMessage("Getting weather data..");
		}

		@Override
		protected String doInBackground(String... s) {
			WeatherNow = new Weather();
			next = new ArrayList<WeatherNext>();
			if (!db.checkDataBase()) {
				try {
					db.copyDataBase();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(Utils.getSettingsBoolean("use_location", context)){
				weatherUrl = "http://api.openweathermap.org/data/2.5/weather?lat="+s[0]+"&lon="+s[1]+"&units="+s[2];
			}else{
				weatherUrl = "http://api.openweathermap.org/data/2.5/weather?id="+s[0]+"&units="+s[1];
			}
			
			JSONParser jParser = new JSONParser();
			JSONObject weatherUrlObject = jParser.getJSONFromUrl(weatherUrl);

			try {
				WeatherNow.setCode(weatherUrlObject.getString("cod").toString());
				WeatherNow.setName(weatherUrlObject.getString("name").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			//if we have 200 code everything is OK with API
			
			if(WeatherNow.getCode().equals("200")){
				try {
					JSONObject sys = weatherUrlObject.getJSONObject("sys");
					WeatherNow.setSys_sunrise(sys.getString("sunrise").toString());
					WeatherNow.setSys_sunset(sys.getString("sunset").toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					JSONArray weather = weatherUrlObject.getJSONArray("weather");
					WeatherNow.setWeather_id(weather.getJSONObject(0).getString("id").toString());
					WeatherNow.setWeather_main(weather.getJSONObject(0).getString("main").toString());
					WeatherNow.setWeather_description(weather.getJSONObject(0).getString("description").toString());
					WeatherNow.setWeather_icon(weather.getJSONObject(0).getString("icon").toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					JSONObject main = weatherUrlObject.getJSONObject("main");
					WeatherNow.setMain_temp(main.getString("temp").toString());
					WeatherNow.setMain_humidity(main.getString("humidity").toString());
					WeatherNow.setMain_pressure(main.getString("pressure").toString());
					WeatherNow.setMain_temp_max(main.getString("temp_min").toString());
					WeatherNow.setMain_temp_min(main.getString("temp_max").toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					JSONObject wind = weatherUrlObject.getJSONObject("wind");
					WeatherNow.setWind_deg(wind.getString("deg").toString());
					WeatherNow.setWind_speed(wind.getString("speed").toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					JSONObject clouds = weatherUrlObject.getJSONObject("clouds");
					WeatherNow.setClouds_all(clouds.getString("all").toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			if(Utils.getSettingsBoolean("use_location", context)){
				weatherNextUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="+s[0]+"&lon="+s[1]+"&cnt=5&mode=json&units="+s[2];
			}else{
				weatherNextUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?id="+s[0]+"&mode=json&units="+s[1]+"&cnt=5";
			}
			jParser = new JSONParser();
			weatherUrlObject = jParser.getJSONFromUrl(weatherNextUrl);
			JSONArray list = null;
			try {
				list = weatherUrlObject.getJSONArray("list");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < list.length(); i++) {
				WeatherNext wn = new WeatherNext();
				try {
					wn.setDay(list.getJSONObject(i).getString("dt"));
					JSONObject temp = list.getJSONObject(i).getJSONObject("temp");
					wn.setMin(temp.getString("min"));
					wn.setMax(temp.getString("max"));
					JSONArray weather = list.getJSONObject(i).getJSONArray("weather");
					wn.setIcon(weather.getJSONObject(0).getString("icon"));
					next.add(wn);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}


		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			progress.dismiss();
			adapter = new WeatherFragmentAdapter(getSupportFragmentManager());
			mPager.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			mIndicator.setViewPager(mPager);

			
		}

	}
	@TargetApi(19) 
	private void setTranslucentStatusAndNavigation(Activity a) {
		Window w = a.getWindow();
		w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	}
	private void addPadding(int i) {
		
		boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
		boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

		if(!hasMenuKey && !hasBackKey) {
			main.setPadding(0, i, 0, tintManager.getConfig().getNavigationBarHeight());
		}else{
			main.setPadding(0, i, 0, 0);
		}
	}
	public void setActionBarTitle(String title){
		SpannableString s = new SpannableString(title);
		s.setSpan(new TypefaceSpan(context, "Ubuntu-R.ttf"), 0, s.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// Update the action bar title with the TypefaceSpan instance
	    getActionBar().setTitle(s);
	}

	public static Weather getWeatherNowData() {
		return WeatherNow;
	}

	public static ArrayList<WeatherNext> getNextData() {
		return next;
	}
}
