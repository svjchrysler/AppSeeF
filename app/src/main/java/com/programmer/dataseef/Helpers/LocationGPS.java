package com.programmer.dataseef.Helpers;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.programmer.dataseef.Activities.MainActivity;

/**
 * Created by SALGUERO on 14/10/2016.
 */

public class LocationGPS implements LocationListener {
    MainActivity mainActivity;

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onLocationChanged(Location location) {
        mainActivity.setLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
