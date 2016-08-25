package com.cyberlandgo.felix.bachelorarbeit20.BroadcastReceiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.cyberlandgo.felix.bachelorarbeit20.application.BillingSystemApplication;

/**
 * Created by Felix on 25.08.2016.
 */
public class GPSStateHasChangedBroadcastReceiver
{
    public static BroadcastReceiver getGPSStateHasChangedBroadcastReceiver(BillingSystemApplication billingSystemApplication)
    {
        final BillingSystemApplication billingSystemApplicationFinal = billingSystemApplication;


        BroadcastReceiver broadcastReceiver  = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                final String action = intent.getAction();

                LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
                boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (isEnabled)
                {
                    Log.e("GPS: ", "aktiviert");
                }
                else if (!isEnabled)
                {
                    Log.e("GPS: ", "nicht aktiviert");
                }

            }
        };






        return broadcastReceiver;
    }
}
