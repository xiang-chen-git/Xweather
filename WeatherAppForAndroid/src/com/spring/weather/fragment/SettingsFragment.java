package com.spring.weather.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spring.weather.R;
import com.spring.weather.app.Utils;
import com.spring.weather.dialog.EnterCityDialog;
import com.spring.weather.dialog.ThemeDialog;
import com.spring.weather.view.CustomSwitchPreference;
import com.spring.weather.view.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements
Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener  {
static Preference city_name, app_theme;
ListPreference units;
PreferenceScreen ps;
SharedPreferences sp;
CustomSwitchPreference use_location;

	@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		addPreferencesFromResource(R.xml.pref_data);
		ps = getPreferenceScreen();
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		city_name = (Preference) ps.findPreference("city_name");
		city_name.setOnPreferenceClickListener(this);
		city_name.setSummary(Utils.loadLastCityData(getActivity()).getName());
		app_theme = (Preference) ps.findPreference("app_theme");
		app_theme.setOnPreferenceClickListener(this);
		units = (ListPreference) ps.findPreference("units");
		units.setOnPreferenceChangeListener(this);
		units.setSummary(Utils.getUnitsName(Utils.getSettings("units", getActivity()), getActivity()));
		use_location = (CustomSwitchPreference) ps.findPreference("use_location");
		use_location.setOnPreferenceChangeListener(this);
		
		
		
	return super.onCreateView(inflater, container, savedInstanceState);
}

	@Override
	public void onResume() {
		super.onResume();
		
		if(Utils.getSettings("use_location", getActivity()).equals("true")){
			city_name.setEnabled(false);
		}else{
			city_name.setEnabled(true);
		}
		
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.equals(city_name)){
			EnterCityDialog newpassdialog = new EnterCityDialog(getActivity());
			newpassdialog.show();
		}
		if(preference.equals(app_theme)){
			ThemeDialog theme = new ThemeDialog(getActivity());
			theme.show();
		}
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.equals(units)){
			units.setSummary(Utils.getUnitsName(newValue.toString(), getActivity()));
		}
		if(preference.equals(use_location)){
			if(newValue.equals(true)){
				city_name.setEnabled(false);
			}else{
				city_name.setEnabled(true);
			}
		}
		
		
		if(preference.equals(use_location)){
			LocationManager mlocManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
			boolean gps_enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean network_enabled = mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if(newValue.equals(true)){
				use_location.setChecked(false);
				Utils.setUseLocation(getActivity(),false);
				if(gps_enabled || network_enabled){
					Utils.setUseLocation(getActivity(),true);
				}else{
					use_location.setChecked(false);
					Utils.setUseLocation(getActivity(),false);
					startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1888);
				}
			}
		}
		
		return true;
	}

	public static void setCityNameSummary(String name) {
		city_name.setSummary(name);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(requestCode == 1888) {
	    	use_location.setChecked(true);
			Utils.setUseLocation(getActivity(),true);
	    }
	}
}
