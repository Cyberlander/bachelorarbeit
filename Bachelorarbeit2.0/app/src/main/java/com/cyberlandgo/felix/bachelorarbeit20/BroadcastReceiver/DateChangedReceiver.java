package com.cyberlandgo.felix.bachelorarbeit20.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cyberlandgo.felix.bachelorarbeit20.application.BillingSystemApplication;

/**
 * Created by Felix on 19.08.2016.
 */

/**
 * Diese Klasse bietet eine Methode an, die einen BroadcastReceiver liefert, der
 * lauscht, ob sich das Datum geändert hat. Falls ja, soll automatisch das Ticket
 * bezahlt werden
 */
public class DateChangedReceiver
{
    public static BroadcastReceiver getDateChangedBroadcastReceiver(BillingSystemApplication billingSystemApplication)
    {
        final BillingSystemApplication billingSystemApplicationFinal = billingSystemApplication;

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                final String action = intent.getAction();

                if (action.equals(Intent.ACTION_DATE_CHANGED))
                {
                    Log.e("Datum", "hat sich geändert");
                    billingSystemApplicationFinal.payTicketApplication();
                }

            }
        };

        return broadcastReceiver;
    }
}
