package com.spring.weather.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.spring.weather.MainActivity;
import com.spring.weather.fragment.WeatherFragment;
import com.spring.weather.fragment.WeatherNextFragment;

public class WeatherFragmentAdapter extends FragmentPagerAdapter{
	    private static int NUM_ITEMS = 2;
	        public WeatherFragmentAdapter(FragmentManager fragmentManager) {
	            super(fragmentManager);
	        }

	        // Returns total number of pages
	        @Override
	        public int getCount() {
	            return NUM_ITEMS;
	        }

			// Returns the fragment to display for that page
	        @Override
	        public Fragment getItem(int position) {
	        	
	            switch (position) {
	            case 0: 
	                return new WeatherFragment();
	            case 1: 
	                return new WeatherNextFragment();
	            default:
	                return null;
	            }
	        }

}
