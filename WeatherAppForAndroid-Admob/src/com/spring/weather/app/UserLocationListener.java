package com.spring.weather.app;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class UserLocationListener implements LocationListener
{

    private Context context;
    public UserLocationListener(Context context) {
		this.context = context;
	}

	
    @Override
    public void onLocationChanged(Location location) 
    {
        Utils.setLocation(location.getLatitude(), location.getLongitude(), context);
    }

    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
