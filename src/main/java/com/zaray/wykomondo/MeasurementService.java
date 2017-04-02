package com.zaray.wykomondo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class MeasurementService extends Service {
    public MeasurementService() {
    }

    private final IBinder binder = new MeasurementBinder();
    private static double distanceInMeters;
    private static double speedSystemLocation = 0.0;
    private static Location lastLocation = null;

    public class MeasurementBinder extends Binder {
        MeasurementService getMeasurement() {
            return MeasurementService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        LocationListener listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation == null) {
                    lastLocation = location;
                }
                distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;
                speedSystemLocation = location.getSpeed();
            }

            @Override
            public void onProviderDisabled(String arg0) {

            }

            @Override
            public void onProviderEnabled(String arg0) {

            }

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle bundle) {
            }
        };
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);
    }

    public double getSpeed() {return this.speedSystemLocation;}
    public double getDistance() {
        return this.distanceInMeters / 1000;
    }

}
