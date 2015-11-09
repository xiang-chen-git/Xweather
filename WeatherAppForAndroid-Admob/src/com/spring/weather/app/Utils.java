package com.spring.weather.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.spring.weather.R;
import com.spring.weather.model.City;

public class Utils {

	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static City loadLastCityData(Context context) {
		City cityData = new City();
		SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
		cityData.setId(mSharedPreference.getString("cityID", "6058560"));
		cityData.setName(mSharedPreference.getString("cityName", "London"));
		cityData.setCode(mSharedPreference.getString("cityCode", "GB"));
		cityData.setLat(mSharedPreference.getString("cityLat", "-0.12574"));
		cityData.setLng(mSharedPreference.getString("cityLng", "51.50853"));
		return cityData;
	}
	public static void setLastCityData(City cityData,Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor mEdit = sp.edit();
	    mEdit.remove("cityID");
	    mEdit.remove("cityName");
	    mEdit.remove("cityCode");
	    mEdit.remove("cityLat");
	    mEdit.remove("cityLng");
	    mEdit.putString("cityID", cityData.getId());
	    mEdit.putString("cityName", cityData.getName());
	    mEdit.putString("cityCode", cityData.getCode());
	    mEdit.putString("cityLat", String.valueOf(cityData.getLat()));
	    mEdit.putString("cityLng", String.valueOf(cityData.getLng()));
	    mEdit.commit();
	    return;    
	}
	public static void setLocation(Double lat,Double lng,Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor mEdit = sp.edit();
	    mEdit.remove("userLat");
	    mEdit.remove("userLng");
	    mEdit.putString("userLat", String.valueOf(lat));
	    mEdit.putString("userLng", String.valueOf(lng));
	    mEdit.commit();
	    return;    
	}
	public static Location getUserLocation(Context context){
		SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
		Double lat =  Double.valueOf(mSharedPreference.getString("userLat", "0.00"));
		Double lng =  Double.valueOf(mSharedPreference.getString("userLng", "0.00"));
		Location user = new Location("user");
		user.setLatitude(lat);
		user.setLongitude(lng);
		return user;
	}
	public static String getUnitsName(String key, Context context) {
		String valute = null;
		if(key.equals("imperial")){
			valute = context.getResources().getString(R.string.units_name_imperial);
		}		
		if(key.equals("metric")){
			valute = context.getResources().getString(R.string.units_name_metric);
		}
		return valute;
	}
	public static String getSettings(String key, Context context) {
		String valute = null;
		SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
		if(key.equals("units")){
			valute = mSharedPreference.getString("units", "metric");
		}		
		if(key.equals("use_location")){
			valute = String.valueOf(mSharedPreference.getBoolean("use_location", true));
		}		
		return valute;
	}
	public static boolean getSettingsBoolean(String key, Context context) {
		boolean valute = false;
		SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
		if(key.equals("use_location")){
			valute = mSharedPreference.getBoolean("use_location", false);
		}	
		if(key.equals("units")){
			String units = mSharedPreference.getString("units", "metric");
			if(units.equalsIgnoreCase("metric")){
				valute = true;
			}else{
				valute = false;
			}
		}
		return valute;
	}

	public static void setUseLocation(Context context, boolean b) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor mEdit = sp.edit();
	    mEdit.putBoolean("use_location", b);
	    mEdit.commit();
	}

	public static String getColor(Context context) {
		SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
		return mSharedPreference.getString("theme", "#3498DB");
		
	}

	public static void setColor(Context context, String color) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor mEdit = sp.edit();
	    mEdit.putString("theme", color);
	    mEdit.commit();
	}

	public static int getLastUpdateTime(Context context) {
		SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
		return mSharedPreference.getInt("lastUpdateTime", 0);
	}

	public static void setLastUpdateTime(Context context, int currentTime) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor mEdit = sp.edit();
	    mEdit.putInt("lastUpdateTime", currentTime);
	    mEdit.commit();
	}

	public static int getUpdateTime(Context context) {
		SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.valueOf(mSharedPreference.getString("refresh", "3600000"));
	}

	

	
}
