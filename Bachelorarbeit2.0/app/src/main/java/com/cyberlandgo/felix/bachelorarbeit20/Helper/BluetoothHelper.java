package com.cyberlandgo.felix.bachelorarbeit20.Helper;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Felix on 18.08.2016.
 */
public class BluetoothHelper
{
    public static BroadcastReceiver getBluetoothGuard()
    {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent)
            {
                final String action = intent.getAction();

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
                {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);

                    switch(state)
                    {
                        case BluetoothAdapter.STATE_OFF:
                            Log.e("Guten TAG", "BLUETOOTH WURDE ABGESCHALTET! BOESE!");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.e("GUTEN TAG", "BLUETOOTH WIRD GERADE ABGESCHALTET");
                    }
                }
            }
        };

        return broadcastReceiver;
    }
}
