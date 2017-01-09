package proheart.me.phonehelper.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by liguorui on 16/12/28.
 */

public class GPSService extends Service {
    private final static String TAG = "GPSService";
    private LocationManager lm;
    private MyLocationListener listener;
    private SharedPreferences sp;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setCostAllowed(true);
        String provider = lm.getBestProvider(criteria, true);
        listener = new MyLocationListener();
        lm.requestLocationUpdates(provider, 0, 0, listener);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(listener);
        listener = null;
        super.onDestroy();
    }

    private class MyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            float accuracy = location.getAccuracy();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
//            SmsManager sm = SmsManager.getDefault();
//            String safeNumber = sp.getString("safenumber","");
//            sm.sendTextMessage(safeNumber, null, "logitude:"+ longitude+"latitude： "+ latitude, null, null);
            String loc = "longitude: "+ longitude+", latitude： "+ latitude;
            sp.edit().putString("lastlocation", loc).commit();
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
}
