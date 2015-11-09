package com.spring.weather.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spring.weather.R;
import com.spring.weather.app.Utils;
import com.spring.weather.model.WeatherNext;

public class WeatherNextAdapter extends ArrayAdapter<WeatherNext> {
	ArrayList<WeatherNext> objects = new ArrayList<WeatherNext>();
	Context context;
	int row;
	public WeatherNextAdapter(Context context, int resource,ArrayList<WeatherNext> objects) {
		super(context, resource, objects);
		this.objects = objects;
		this.context = context;
		this.row = resource;
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 View view = inflater.inflate(row, parent, false);
		 //get views
		 TextView day = (TextView) view.findViewById(R.id.row_day);
		 TextView temp = (TextView) view.findViewById(R.id.row_temp);
		 ImageView icon = (ImageView) view.findViewById(R.id.row_img);
		 //set font
		 Typeface ubuntu = Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-R.ttf");
		 day.setTypeface(ubuntu);
		 temp.setTypeface(ubuntu);
		 //get day
		 WeatherNext i = objects.get(position);
		 day.setText(getDay(i.getDay()));
		 //set temp
		 boolean isMetric = Utils.getSettingsBoolean("units", context);
		 if(isMetric){
			temp.setText(String.valueOf(Math.round(Double.valueOf(i.getMin())))+"/"+String.valueOf(Math.round(Double.valueOf(i.getMax())))+ " "+context.getString(R.string.units_name_metric));
		    }else{
		    temp.setText(String.valueOf(Math.round(Double.valueOf(i.getMin())))+"/"+String.valueOf(Math.round(Double.valueOf(i.getMax())))+ " "+context.getString(R.string.units_name_imperial));
		 }
		 //set icon
		 Bitmap current_icon_bitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("img"+i.getIcon(), "drawable", context.getPackageName()));
		 icon.setImageBitmap(current_icon_bitmap);
		 //return view
		 return view;
	}

	/**
	 * @param day (unix string from OpenWeather.org)
	 * @return String from strings.xml
	 */
	private String getDay(String day) {
		Date time=new Date((long)Long.valueOf(day) *1000);
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		int daynum = cal.get(Calendar.DAY_OF_WEEK);

		switch (daynum) {
		case 1:
			return context.getString(R.string.sunday);
		case 2:
			return context.getString(R.string.monday);
		case 3:
			return context.getString(R.string.tuesday);
		case 4:
			return context.getString(R.string.wednesday);
		case 5:
			return context.getString(R.string.thursday);
		case 6:
			return context.getString(R.string.friday);
		case 7:
			return context.getString(R.string.saturday);
	}
		return null;
	}
	
}
