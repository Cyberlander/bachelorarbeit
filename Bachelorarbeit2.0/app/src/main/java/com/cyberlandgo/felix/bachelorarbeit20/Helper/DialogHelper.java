package com.cyberlandgo.felix.bachelorarbeit20.Helper;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.cyberlandgo.felix.bachelorarbeit20.ui.fragments.FragmentOverview;

/**
 * Created by Felix on 18.08.2016.
 */
public class DialogHelper
{
    public static AlertDialog getPayDialog(Context context, FragmentOverview fragmentOverview)
    {
        final FragmentOverview fragmentOverviewFinal = fragmentOverview;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle("Ticket bezahlen");
        String strecke = TicketDetailHelper.getLineLengthForAmountOfStations();
        String preis = TicketDetailHelper.getPriceForLineLength(strecke);

        String content = "Strecke: " + strecke + "\n" + "Preis: " + preis;
        builder.setMessage(content);


        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fragmentOverviewFinal.payTicketResetPreferences();
                    }
                });

        builder.setNegativeButton("Abbrechen",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();

                    }
                }
        );



        android.support.v7.app.AlertDialog dialog = builder.create();
        // display dialog
       return dialog;
    }


    public static AlertDialog getBluetoothGuardDialog(Context context)
    {android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle("Bluetooth wurde deaktiviert! :(");

        builder.setMessage("Während des Abrechnungsvorgangs wurde Bluetooth deaktiviert. Bitte " +
                "wieder einschalten, damit die App funktioniert!");


        builder.setPositiveButton("Bluetooth einschalten",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        BluetoothAdapter.getDefaultAdapter().enable();

                    }
                });

        builder.setNegativeButton("Abbrechen",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();

                    }
                }
        );



        android.support.v7.app.AlertDialog dialog = builder.create();

        return dialog;
    }


    public static AlertDialog getGPSGuardDialog(Context context)
    {android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle("GPS wurde deaktiviert! :(");

        final Context contextFinal = context;

        builder.setMessage("Während des Abrechnungsvorgangs wurde GPS deaktiviert. Bitte " +
                "wieder einschalten, damit die App funktioniert!");


        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String provider = Settings.Secure.getString(contextFinal.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

                        if(!provider.contains("gps")){ //if gps is disabled
                            final Intent poke = new Intent();
                            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                            poke.setData(Uri.parse("3"));
                            contextFinal.sendBroadcast(poke);
                        }

                    }
                });





        android.support.v7.app.AlertDialog dialog = builder.create();

        return dialog;
    }











    public static AlertDialog getBeaconDetectedDialog(Context context)
    {android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle("Bluetooth wurde deaktiviert! :(");

        builder.setMessage("Während des Abrechnungsvorgangs wurde Bluetooth deaktiviert. Bitte " +
                "wieder einschalten, damit die App funktioniert!");


        builder.setPositiveButton("Bluetooth einschalten",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        BluetoothAdapter.getDefaultAdapter().enable();

                    }
                });

        builder.setNegativeButton("Abbrechen",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();

                    }
                }
        );



        android.support.v7.app.AlertDialog dialog = builder.create();

        return dialog;
    }
}
