package com.spring.weather.widget;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.spring.weather.MainActivity;
import com.spring.weather.R;
import com.spring.weather.app.JSONParser;
import com.spring.weather.app.UserLocationListener;
import com.spring.weather.app.Utils;
import com.spring.weather.model.Weather;

public class Widget extends AppWidgetProvider {
	private Context context;
	static Widget widget;
	private static Service service;
	private static final String SYNC_CLICKED    = "automaticWidgetSyncButtonClick";
	@Override
	public void onUpdate(Context mcontext, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// To prevent any ANR timeouts, we perform the update in a service
		this.context=mcontext;
		widget = this;
		
		//int updateTime = Utils.getUpdateTime(context);
		//int lastUpdate = Utils.getLastUpdateTime(context);
		int currentTime = (int) System.currentTimeMillis();
		
		//if((updateTime+lastUpdate)>currentTime){
			context.startService(new Intent(context, UpdateService.class));
			Utils.setLastUpdateTime(context, currentTime);
		//}
	}

	public static class UpdateService extends Service {
		@Override
		public void onStart(Intent intent, int startId) {
			startGetData(this);
			service = this;
		}

		private void startGetData(Context context) {
			if(Utils.getSettingsBoolean("use_location", context)){
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
				
				try {
					widget.new GetData().execute(String.valueOf(user.getLatitude()),String.valueOf(user.getLongitude()),Utils.getSettings("units", context));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				try {
					widget.new GetData().execute(
					Utils.loadLastCityData(context).getId(),
					Utils.getSettings("units", context));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
		}

		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}
	}
	private class GetData extends AsyncTask<String, String, String> {
		Weather WeatherNow = new Weather();
		String weatherUrl;
		boolean error = false;

		@Override
		protected String doInBackground(String... s) {
		
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
			if(WeatherNow.getCode()!=null){
			if(WeatherNow.getCode().equals("200")){

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
			}else{
				error = true;
			}
			}else{
				error = true;
			}

			return null;
		}


		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(error!=true){
				RemoteViews updateViews = buildUpdate(WeatherNow);
				// Push update for this widget to the home screen
				ComponentName thisWidget = new ComponentName(context, Widget.class);
				AppWidgetManager manager = AppWidgetManager.getInstance(context);
				manager.updateAppWidget(thisWidget, updateViews);
			}
		}


		private RemoteViews buildUpdate(Weather weather) {
			RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			boolean isMetric = Utils.getSettingsBoolean("units", context);
			String temp = String.valueOf(Math.round(Double.valueOf(weather.getMain_temp())));
			if(isMetric){
				updateViews.setImageViewBitmap(R.id.img_city, buildBitMap(context,temp+ " °C"));
		    }else{
				updateViews.setImageViewBitmap(R.id.img_city, buildBitMap(context,temp+ " °F"));
		    }
			Bitmap current_icon_bitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("img"+weather.getWeather_icon(), "drawable", context.getPackageName()));
		    updateViews.setImageViewBitmap(R.id.widget_img, current_icon_bitmap);
			
			PendingIntent pendingIntent;
			Intent launchIntent = new Intent(Intent.ACTION_MAIN);
			launchIntent.setClass(context, MainActivity.class);
			
			pendingIntent = PendingIntent.getActivity(context, 0, 	launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			updateViews.setOnClickPendingIntent(R.id.img_city, pendingIntent);
			updateViews.setInt(R.id.main_bg, "setBackgroundColor", Color.parseColor(Utils.getColor(context)));
			updateViews.setOnClickPendingIntent(R.id.widget_button, getPendingSelfIntent(context, SYNC_CLICKED));
			
			service.stopSelf();
			return updateViews;
		}
		public Bitmap buildBitMap(Context context,String temp) 
		{

			Bitmap myBitmap = Bitmap.createBitmap(300, 200, Bitmap.Config.ARGB_4444);
		    Canvas myCanvas = new Canvas(myBitmap);
		    Paint paint = new Paint();
		    Typeface ubuntu = Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-R.ttf");
		    paint.setAntiAlias(true);
		    paint.setSubpixelText(true);
		    paint.setTypeface(ubuntu);
		    paint.setStyle(Paint.Style.FILL);
		    paint.setColor(Color.WHITE);
		    int pixel= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50, context.getResources().getDisplayMetrics());
		    paint.setTextSize(pixel);
		    paint.setTextAlign(Align.CENTER);
		    myCanvas.drawText(temp, 200, 125, paint);
		    return myBitmap;
		    
		}
}
	protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
	@Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {

        	context.startService(new Intent(context, UpdateService.class));

        }
    }
	}