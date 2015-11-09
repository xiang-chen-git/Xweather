package com.spring.weather.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spring.weather.MainActivity;
import com.spring.weather.R;
import com.spring.weather.app.Utils;
import com.spring.weather.model.Weather;


public class WeatherFragment extends Fragment {
	Weather Weather;
	int position;
	boolean isMetric;
	ImageView current_icon;
	TextView current_temp,text_pressure,text_humidity,text_wind;

	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_current, container, false);
	    isMetric = Utils.getSettingsBoolean("units", getActivity());
	    Weather = MainActivity.getWeatherNowData();
	    //set title
	    ((MainActivity) getActivity()).setActionBarTitle(Weather.getName());
	    //set current icon
	    current_icon = (ImageView) view.findViewById(R.id.current_icon);
	    Bitmap current_icon_bitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("img"+Weather.getWeather_icon(), "drawable", getActivity().getPackageName()));
	    current_icon.setImageBitmap(current_icon_bitmap);
	    //get Font
	    Typeface ubuntu = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Ubuntu-R.ttf");
	    //set temp
	    current_temp = (TextView) view.findViewById(R.id.current_temp);
	    current_temp.setTypeface(ubuntu);
	    
	    if(isMetric){
	    	current_temp.setText(String.valueOf(Math.round(Double.valueOf(Weather.getMain_temp())))+ " °C");
	    }else{
	    	current_temp.setText(String.valueOf(Math.round(Double.valueOf(Weather.getMain_temp())))+ " °F");
	    }
	    
	    //set pressure
	    text_pressure= (TextView) view.findViewById(R.id.text_pressure);
	    text_pressure.setTypeface(ubuntu);
	    text_pressure.setText(Weather.getMain_pressure()+" hpa");
	    //set humidity
	    text_humidity= (TextView) view.findViewById(R.id.text_humidity);
	    text_humidity.setTypeface(ubuntu);
	    text_humidity.setText(Weather.getMain_humidity()+" %");
	    //set wind
	    text_wind= (TextView) view.findViewById(R.id.text_wind);
	    text_wind.setTypeface(ubuntu);
	    if(isMetric){
	    	text_wind.setText(Weather.getWind_speed()+" m/s");
	    }else{
	    	text_wind.setText(Weather.getWind_speed()+" mph");
	    }

	    Bitmap bmpOriginal = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.wind);
	    Drawable d = new BitmapDrawable(getResources(),rotateImage(bmpOriginal,Float.valueOf(Weather.getWind_deg())));
	    Resources r = getResources();
	    int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, r.getDisplayMetrics());
	    d.setBounds( 0, 0, px, px);
	    text_wind.setCompoundDrawables( null, d, null, null );
	    Drawable humidity = getResources().getDrawable(R.drawable.humidity);
	    humidity.setBounds( 0, 0, px, px);
	    text_humidity.setCompoundDrawables( null, humidity, null, null );
	    Drawable pressure = getResources().getDrawable(R.drawable.pressure);
	    pressure.setBounds( 0, 0, px, px);
	    text_pressure.setCompoundDrawables( null, pressure, null, null );
	    return view;
	    
	  }

	public static Bitmap rotateImage(Bitmap src, float degree) 
	{
	        // create new matrix
	        Matrix matrix = new Matrix();
	        // setup rotation degree
	        matrix.postRotate(degree);
	        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
	        return bmp;
	}
}
