package com.spring.weather.fragment;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.spring.weather.MainActivity;
import com.spring.weather.R;
import com.spring.weather.adapter.WeatherNextAdapter;
import com.spring.weather.model.WeatherNext;


public class WeatherNextFragment extends Fragment {
	ArrayList<WeatherNext> next;
	TextView nextdays;
	WeatherNextAdapter adapter;


	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_next, container, false);
	    //get Font
	    ArrayList<WeatherNext> next = MainActivity.getNextData();
	    Typeface ubuntu = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Ubuntu-R.ttf");
	    nextdays = (TextView) view.findViewById(R.id.text_days);
	    nextdays.setTypeface(ubuntu);
	    //setup adapter 
	    WeatherNextAdapter adapter = new WeatherNextAdapter(getActivity(), R.layout.rowlayout, next);
	    ListView lv = (ListView) view.findViewById(android.R.id.list);
	    //disable scroll on listview
	    lv.setOnTouchListener(new OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            return (event.getAction() == MotionEvent.ACTION_MOVE);
	        }
	    });
	    //add adapter to listvew
	    lv.setAdapter(adapter);
	    
	    return view;
	    
	  }

}
