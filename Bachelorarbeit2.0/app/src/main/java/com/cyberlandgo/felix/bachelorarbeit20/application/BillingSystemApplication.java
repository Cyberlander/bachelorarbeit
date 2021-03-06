package com.cyberlandgo.felix.bachelorarbeit20.application;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cyberlandgo.felix.bachelorarbeit20.BroadcastReceiver.BluetoothGuard;
import com.cyberlandgo.felix.bachelorarbeit20.BroadcastReceiver.DateChangedReceiver;
import com.cyberlandgo.felix.bachelorarbeit20.BroadcastReceiver.GPSStateHasChangedBroadcastReceiver;
import com.cyberlandgo.felix.bachelorarbeit20.Helper.CalendarHelper;
import com.cyberlandgo.felix.bachelorarbeit20.Helper.RegionBuilder;
import com.cyberlandgo.felix.bachelorarbeit20.Helper.StationDistanceHelper;
import com.cyberlandgo.felix.bachelorarbeit20.Helper.StationGPSCoordinatesHelper;
import com.cyberlandgo.felix.bachelorarbeit20.Helper.TicketDetailHelper;
import com.cyberlandgo.felix.bachelorarbeit20.R;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.GPSCoordinatesDatasource;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.StationDataSource;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.SubsectionDataSource;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Station;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.StationGPSCoordinates;
import com.cyberlandgo.felix.bachelorarbeit20.ui.MainActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Felix on 06.08.2016.
 */
public class BillingSystemApplication extends Application implements BootstrapNotifier,SharedPreferences.OnSharedPreferenceChangeListener, LocationListener, BeaconConsumer, RangeNotifier
{
    Preferences preferences;
    int currentStatusStateMachine;

    CalendarHelper calendarHelper;

    //die StationDataSource ist die Schnittstelle zu lokalen SQLite-Datenbank
    //in der die Beacon-Regionen zugeordnet zu ihrem Standort gespeichert sind
    StationDataSource stationDataSource;

    ArrayList<Station> arrayListStationObjects;
    List<Region> listRegions;

    //mappt die Minor-ID auf einen Stationsnamen
    Map<String, String> minorStationMap;

    //die MainActivity, die bei Beacon-Detection im Hintergrund
    //gestartet werden soll
    private MainActivity monitoringActivity = null;


    //Android Beacon Library
    //BeaconManager, Schnittstelle der Api zu Beaconerkennung
    BeaconManager beaconManager;
    //Array von RegionBootstrap-Objekten, die initialisiert werden müssen,
    //damit die Callbacks ausgelöst werden
    private RegionBootstrap[] regionBootstrap;

    BroadcastReceiver bluetoothGuard;

    //dieser Receiver lauscht, ob sich das Datum geändert hat
    BroadcastReceiver dateChangedBroadcastReceiver;

    //dieser Receiver lauscht, ob GPS aktiviert/deaktiviert wurde
    BroadcastReceiver _hasGPSStatusChangedBroadcastReceiver;

    //Datasource für die Teilstrecken
    SubsectionDataSource _subsectionDataSource;

    //CountdownTimer, wenn Bus-Region verlassen wird
    Handler _handlerCountdownBus;
    Runnable _runnableForHandlerCountdownBus;

    //Datasource für die GPS-Koordinaten
    GPSCoordinatesDatasource _gpsCoordinatesDatasource;

    //LocationManager für GPS-Koordinaten-Abfrage
    LocationManager _locationManager;

    @Override
    public void onCreate()
    {
        super.onCreate();



        Log.e("DEVICE-NAME:",android.os.Build.MODEL);
        Log.e("Manufacturer:", Build.MANUFACTURER);
        Log.e("Serial:", Build.SERIAL);
        Log.e("User:", Build.USER);



        //Ezeugen des Preferences-Objekt und Zuweisung der Application als dessen Kontext
        preferences = new Preferences(this);


        //holt sich den aktuellen Status der StateMachine
        currentStatusStateMachine = Preferences.getStatusStateMachine();

        //greift auf das Calendar-Objekt zu und formatiert Datum und
        //Zeit, damit es lesbarer ist
        calendarHelper = new CalendarHelper();



        //holt über die StationDatasource Stationsobjekte aus der Datenbank
        arrayListStationObjects = getListOfStationObjectsFromDatabase();

        //Wandelt die Liste der Stationsobjekte in eine Map um, bei der
        //die Minors die Keys sind und die Stationsnamen die Values
        minorStationMap = list2map(arrayListStationObjects);

        listRegions = RegionBuilder.getRegions(arrayListStationObjects);




        initializeBeaconManager();

        regionBootstrap = new RegionBootstrap[listRegions.size()];

        for (int i = 0; i < listRegions.size(); i++) {
            regionBootstrap[i] = new RegionBootstrap(this, listRegions.get(i));
        }


        //Application lauscht nach Veränderungen der Preferences
        SharedPreferences sharedPref = this.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        sharedPref.registerOnSharedPreferenceChangeListener(this);



        //Der BluetoothGuard ist ein BroadcastReceiver, der
        //darauf reagiert, ob Bluetooth abgeschaltet wird
        bluetoothGuard = BluetoothGuard.getBluetoothGuard(this);

        if (Preferences.getBooleanIsBluetoothGuardActive())
        {
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(bluetoothGuard, filter);
        }




        dateChangedBroadcastReceiver = DateChangedReceiver.getDateChangedBroadcastReceiver(this);
        IntentFilter filterDateChanged = new IntentFilter(Intent.ACTION_DATE_CHANGED);
        registerReceiver(dateChangedBroadcastReceiver, filterDateChanged);

        _hasGPSStatusChangedBroadcastReceiver = GPSStateHasChangedBroadcastReceiver.getGPSStateHasChangedBroadcastReceiver(this);
        IntentFilter filterHasGPSStateChanged = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(_hasGPSStatusChangedBroadcastReceiver,filterHasGPSStateChanged);

        //instanziieren der Schnittstelle zur Teilstrecken-Tabelle
        _subsectionDataSource = new SubsectionDataSource(this);
        _subsectionDataSource.open();
        initLocationManager();
    }


    /**
     * Diese Methode liefert eine Liste der Stationenobjekte,
     * die ihre Daten aus der SQLite-Datenbank ziehen
     *
     * @return Liste der Stationobjekte aus der Datenbank
     */
    public ArrayList<Station> getListOfStationObjectsFromDatabase()
    {
        ArrayList<Station> listStationsFromDatabase = new ArrayList<>();
        stationDataSource = new StationDataSource(this);
        stationDataSource.open();

        return  stationDataSource.getAllStations();

    }


    /**
     * Callback-Methode, die ausgelöst wird, sobald eine Beacon-Region betreten wurde.
     * Sie wird nur ausgelöst wenn das BeaconLayout übereinstimmt
     * @param region enthält das Regions-Objekt der betretenen Region
     */
    @Override
    public void didEnterRegion(Region region)
    {
        Log.e("Region","betreten");
        Identifier currentMajorIdentifier = region.getId2();
        Identifier currentMinorIdentifier = region.getId3();

        //aktuelle Identifier in Strings umwandeln
        String currentMajorIdentifierString = "" + currentMajorIdentifier;
        String currentMinorIdentifierString = "" + currentMinorIdentifier;

        //für Debugging: aktuelle Station loggen
        Preferences.saveCurrentStartstation(minorStationMap.get(currentMinorIdentifierString));

        //Serverseitiges Loggen
        sendStationToServer(getDeviceId(this),minorStationMap.get(currentMinorIdentifierString));


        //Überprüfung ob Station mit den GPS-Koordinaten übereinstimmt
        if (Preferences.getBooleanGPSUsage())
        {
            checkBeaconWithGPSCoordinates(currentMinorIdentifierString);
        }


        if (currentStatusStateMachine == StateMachine.STATUS_NOT_RUNNING)
        {

            //registrieren des BluetoothGuards, der reagiert,
            //wenn das Bluetooth abgeschaltet wird
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(bluetoothGuard, filter);
            Preferences.saveBooleanIsBluetoothGuardActive(true);

            //speichern von Startstation,Datum und Zeit
            //sowie vom Status, der je nach Major variiert
            saveStartDataOnEnterRegionFirstTime(currentMajorIdentifierString, currentMinorIdentifierString);

            //Wenn die Activity im Vordergrund ist
            if (monitoringActivity != null)
            {
                //todo logs entfernen
                Log.e("////////","Activity ist im Vordergrund");
                monitoringActivity.ToastOnUIThread(Preferences.getStartStation());
            }
            else if (monitoringActivity==null)
            {
                showNotification("Haltestelle betreten!","Abrechnung starten?");

                /*
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                */

            }

        }

        //Betrifft nur den Train-Automatenteil
        //wenn sich der Benutzer zwischen der Start- und der Endstation befindert
        else if (currentStatusStateMachine == StateMachine.STATUS_BETWEEN_REGIONS_TRAIN)
        {
            //speichert vorläufige Enddaten
            saveCurrentEndDataOnEnterTargetRegionTrain(currentMinorIdentifierString);
        }

        //Der Automat befindet sich im Zustand END_REGION_TRAIN.
        //Mögliche Szenarien sind das Betreten einer weiteren
        //Train-Region, dann wird wieder in den Zustand between
        //Stations gewechselt. Oder der Automat wechselt in
        //den Zustand START_REGION_BUS. Feststellen lässt sich das über die Major-ID
        else if (currentStatusStateMachine == StateMachine.STATUS_END_REGION_TRAIN)
        {
            //ist es eine Train-Region?
            if (currentMajorIdentifierString.equals(Values.MAJOR_ID_TRAIN))
            {
                Preferences.saveStatusStateMachine(StateMachine.STATUS_BETWEEN_REGIONS_TRAIN);

                //todo
                // speichern der zweiten usw. Startstation, damit die Berechnung der Menge
                //der Stationen exakt erfolgt
                Preferences.saveSecondStartStation(minorStationMap.get(currentMinorIdentifierString));
            }


            else if (currentMajorIdentifierString.equals(Values.MAJOR_ID_BUS))
            {
                Preferences.saveStatusStateMachine(StateMachine.STATUS_START_REGION_BUS);

                //für Subsection-Berechnung
                Preferences.saveCurrentStartStationBusForSubsection(minorStationMap.get(currentMinorIdentifierString));
            }

        }

        //Wenn der Automat sich im Zustan START_REGION_BUS befindet, wechselt
        //er in END_REGION_BUS
        else if (currentStatusStateMachine == StateMachine.STATUS_START_REGION_BUS)
        {
            saveCurrentEndDataOnEnterTargetRegionBus(currentMinorIdentifierString);
        }


        else if (currentStatusStateMachine == StateMachine.STATUS_END_REGION_BUS)
        {


            if (currentMajorIdentifierString.equals(Values.MAJOR_ID_BUS))
            {
                saveCurrentEndDataOnEnterTargetRegionBus(currentMinorIdentifierString);
            }
            else if (currentMajorIdentifierString.equals(Values.MAJOR_ID_TRAIN))
            {
                Preferences.saveStatusStateMachine(StateMachine.STATUS_BETWEEN_REGIONS_TRAIN);
            }
        }



        //Ranging einschalten
        try
        {
            beaconManager.startRangingBeaconsInRegion(region);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }




    }


    /**
     * Callback-Methode, ausgelöst beim Verlassen einer Region, bzw. wenn
     * ein Beacon wieder außer Reichweite ist
     * @param region
     */
    @Override
    public void didExitRegion(Region region)
    {
        Log.e("Eine Region", "wurde verlassen");
        if (currentStatusStateMachine==StateMachine.STATUS_START_REGION_TRAIN)
        {
            Log.e("Nachricht:", "Wechsel vom Status start_region_train zu between");
            //Update der StateMachine, wechselt nun von Start_region_train
            // zu between_regions_train
            currentStatusStateMachine = StateMachine.STATUS_BETWEEN_REGIONS_TRAIN;
            Preferences.saveStatusStateMachine(StateMachine.STATUS_BETWEEN_REGIONS_TRAIN);

        }


        //wenn eine Bus-Region verlassen wurde, dann starte den Timer
        String currentMajorIDString = "" + region.getId2();
        if (currentMajorIDString.equals(Values.MAJOR_ID_BUS))
        {
            //setCountDownAfterLeavingBus();
        }

        //Ranging abschalten
        try
        {
            beaconManager.stopRangingBeaconsInRegion(region);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }


    }


    @Override
    public void didDetermineStateForRegion(int state, Region region)
    {

    }



    /**
     * Diese Methode initialsiert den BeaconManager, setzt den Parser
     * und fügt das iBeacon Layout hinzu, damit iBeacons erkannt werden.
     * Außerdem werden die ScanPerioden gesetzt
     */
    private void initializeBeaconManager()
    {
        //auf der statischen Methode, die den Beacon Manager liefert
        beaconManager = BeaconManager.getInstanceForApplication(this);


        //dem Beacon-Parser das Layout für iBeacons übergeben
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

        // setzen der Scan-Intervalle, hoch setzen, damit
        //schneller iBeacons erkannt werden
        setScanPeriodsForGroundBackground();


    }




    private void setScanPeriodsForGroundBackground()
    {
        // setzen der Scan-Intervalle, hoch setzen, damit
        //schneller iBeacons erkannt werden
        beaconManager.setBackgroundBetweenScanPeriod(10000L);
        beaconManager.setForegroundScanPeriod(30000L);
        beaconManager.setForegroundBetweenScanPeriod(10000L);


        //veränderte ScanIntervalle an den BeaconManager übertragen
        try
        {
            beaconManager.updateScanPeriods();
        } catch (RemoteException e)
        {

        }

    }

    /**wandelt die Liste von Stationen in eine Map um, bei der die Minor-ID der
     * Schlüssel ist und der Stationsname der dazugehörige Wert
     * @param list
     * @return
     */
    public Map<String, String> list2map(List<Station> list) {
        Map<String, String> map = new HashMap<String, String>();
        //System.out.println(list);
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).getMinor(), list.get(i).getStationName());

        }

        return map;
    }


    /**
     * setzt die MainActivity als MonitoringActivity,
     * dadurch können vom Application-Objekt Methoden
     * der Activity aufgerufen werden
     * @param activity
     */
    public void setMonitoringActivity(MainActivity activity) {
        this.monitoringActivity = activity;
    }

    /**
     * Wenn die StateMachine sich im Status 0 befindet und ein geeigneter Beacon erkannt
     * wird, dann werden die Startstation, das Datum, die Zeit und der neue Status
     * der StateMachine persistent gespeichert. Der nachfolgende Status kann dabei
     * variieren, denn die majorNumber bestimmt darüber, ob eine Bahn oder ein Bus
     * betreten wurde
     * @param majorNumber
     * @param minorNumber
     */
    public void saveStartDataOnEnterRegionFirstTime(String majorNumber, String minorNumber)
    {
        //holt sich von der Map die zur Minor-ID zugehörige Startstation
        String startStation = minorStationMap.get(minorNumber);


        String currentMajorNumber = majorNumber;

        Preferences.saveStartStation(startStation);
        Preferences.saveStartDate(calendarHelper.getDateString());
        Preferences.saveStartTime(calendarHelper.getTimeString());


        //wenn eine Region betreten wurde, bei der die Major-ID 00000 ist (was für Bahn steht)
        //dann ist der folgende Status STATUS_START_REGION_TRAIN
        if (currentMajorNumber.equals(Values.MAJOR_ID_TRAIN) || currentMajorNumber.equals("18475"))
        {
            Preferences.saveStatusStateMachine(StateMachine.STATUS_START_REGION_TRAIN);

        }
        //wenn eine Region betreten wurde, bei der die Major-ID 11111 ist (was für Bus steht)
        //dann ist der folgende Status STATUS_START_REGION_TRAIN
        else if (currentMajorNumber.equals(Values.MAJOR_ID_BUS))
        {
            Preferences.saveStatusStateMachine(StateMachine.STATUS_START_REGION_BUS);

            //für Subsection-Berechnung
            Preferences.saveCurrentStartStationBusForSubsection(startStation);
        }


    }


    /**
     * Diese Methode betrifft nur den Train-Teil des Automaten
     * Wenn sich der Automat im Status Between_stations_train befindet
     * und dann eine neue Station erkennt, dann ist das die vorläufige
     * Endstation und er speichert die wichtigen persistenten Daten
     * wie Name der Endstation, Anzahl der Zwischenstationen,
     * Ticketpreis und Streckenlänge. Die Fragments bemerkten
     * dies durch ihre PreferenceListener und aktualisieren ihre UI
     * @param minorNumber
     */
    public void saveCurrentEndDataOnEnterTargetRegionTrain(String minorNumber)
    {
        //holt sich von der Map die zur Minor-ID zugehörige Zielstation
        String targetStation = minorStationMap.get(minorNumber);
        Preferences.saveCurrentTargetStation(targetStation);
        Preferences.saveCurrentMinorIDTargetStation(minorNumber);
        Preferences.saveStatusStateMachine(StateMachine.STATUS_END_REGION_TRAIN);

        //Menge der zurückgelegten Stationen ausrechnen
        //Wenn das wirklich die erste Startstation noch die Startstation ist
        int positionStartStation = 0;
        if (Preferences.getSecondStartStation().equals(""))
        {
            positionStartStation = StationDistanceHelper.getPositionForName(Preferences.getStartStation());

            _subsectionDataSource.createSubsection(TicketDetailHelper.getLineForMinorID(Preferences.getCurrentMinorIDTargetStation()),
                    Preferences.getStartStation(),
                    Preferences.getCurrentTargetStation());
        }

        //wenn der Train-Automat schon einmal durchlaufen wurde und eine weitere
        //Startstation betreten wurde
        else if (!Preferences.getSecondStartStation().equals(""))
        {
            positionStartStation = StationDistanceHelper.getPositionForName(Preferences.getSecondStartStation());

            _subsectionDataSource.createSubsection(TicketDetailHelper.getLineForMinorID(Preferences.getCurrentMinorIDTargetStation()),
                    Preferences.getSecondStartStation(),
                    Preferences.getCurrentTargetStation());
        }

        int positionEndStation = StationDistanceHelper.getPositionForName(Preferences.getCurrentTargetStation());
        int newAmountStations = Math.abs(positionStartStation - positionEndStation);

        int oldAmountOfStation = Preferences.getCurrentAmountOfStations();
        int amountOfStation = oldAmountOfStation + newAmountStations;

        //todo
        Log.e("OLDDDDDDDDD", ""+ oldAmountOfStation);
        Log.e("NEEEEEEEEEW",""+ amountOfStation);
        //Menge der zurückgelegten Stationen speichern
        Preferences.saveCurrentAmountOfStations(amountOfStation);


        //ab jetzt kann ein Ticket bezahlt werden
        Preferences.saveBooleanHasToPayTicket(true);


    }




    public void saveCurrentEndDataOnEnterTargetRegionBus(String minorNumber)
    {
        String targetStation = minorStationMap.get(minorNumber);
        Preferences.saveCurrentTargetStation(targetStation);
        Preferences.saveCurrentMinorIDTargetStation(minorNumber);
        Preferences.saveStatusStateMachine(StateMachine.STATUS_END_REGION_BUS);

        int oldAmountOfStation = Preferences.getCurrentAmountOfStations();
        int amountOfStation = oldAmountOfStation + 1;

        //Menge der zurückgelegten Stationen speichern
        Preferences.saveCurrentAmountOfStations(amountOfStation);


        //Subsection für Bus erstellen
        //todo das soll eigentlich erst passieren, wenn Bus verlassen wurde
        /*
        _subsectionDataSource.createSubsection(TicketDetailHelper.getLineForMinorID(
                Preferences.getCurrentMinorIDTargetStation()),
                Preferences.getCurrentStartStationBusForSubsection(),
                Preferences.getCurrentTargetStation());
        */


        //ab jetzt kann ein Ticket bezahlt werden
        Preferences.saveBooleanHasToPayTicket(true);
    }








    @Override
    public void  onSharedPreferenceChanged (SharedPreferences  sharedPreferences, String  key)
    {
        if (key.equals("keyStatusStateMachine"))
        {
            Log.e("Neuer Status: ",StateMachine.getStatusNameForStatusInteger(Preferences.getStatusStateMachine()));
            currentStatusStateMachine = Preferences.getStatusStateMachine();

        }
        if (key.equals("keyBooleanIsBluetoothGuardActive"))
        {
            if (!Preferences.getBooleanIsBluetoothGuardActive())
            {
                unregisterReceiver(bluetoothGuard);
            }


        }


    }



    public void reactToBluetoothTurnedOff()
    {
        if (monitoringActivity != null)
        {
            monitoringActivity.showBluetoothGuardDialog();

        }
        else if (monitoringActivity==null)
        {
            showNotification("Bluetooth wurde deaktivert!","Bitte wieder einschalten!");
        }
    }

    /**
     * Zwecks Context-Awareness soll das Ticket automatisch
     * bezahlt werden, wenn ein neuer Tag angebrochen ist
     */
    public void payTicketApplication()
    {
        if (Preferences.getBooleanHasToPayTicket()==true)
        {
            //Löschen aller Teilstrecken
            _subsectionDataSource.deleteAllSubsections();
            //Zurücksetzen sämtlicher Werte
            Preferences.saveBooleanHasToPayTicket(false);
            Preferences.saveCurrentAmountOfStations(0);
            Preferences.saveCurrentStartstation("");
            Preferences.saveStartStation("");
            Preferences.saveSecondStartStation("");
            Preferences.saveStartDate("");
            Preferences.saveStartTime("");
            Preferences.saveCurrentTargetStation("");
            Preferences.saveStatusStateMachine(StateMachine.STATUS_NOT_RUNNING);
            Preferences.saveBooleanIsBluetoothGuardActive(false);
            Preferences.saveCurrentStartStationBusForSubsection("");


        }
    }





    public void showNotification(String title,String text)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        int notifyID = 1;


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notifyID, mBuilder.build());
    }

    public void sendStationToServer(String customer,String station)
    {
        Log.e("sendStationToServer","wird geöffnet");
        final String stationFinal = station;
        final String customerFinal = customer;

        new AsyncTask<Void,Void,List<String>>()
        {
            protected List<String> doInBackground(Void... params)
            {
                try {
                    Socket clientSocket = new Socket("192.168.0.89", 6666);


                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);


                    //while run
                    out.println(customerFinal + "/" + stationFinal);
                    Log.e("Android User","schickt an Server");
                }
                catch (IOException e)
                {

                }
                return null;
            }
        }.execute();
    }


    /**
     * Diese Methode liefert die Device-ID zurück, die mit an den Server geschickt wird
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceId != null) {
            return deviceId;
        } else {
            return android.os.Build.SERIAL;
        }
    }

    /**
     * Wenn eine Bus-Region verlassen wurde, dann wird ein Handler-Objekt
     * erzeugt, dass eine Runnable innerhalb von einer Minute startet.
     * Wird innerhalb dieser Minute eine neue Bus-Region betreten, wird
     * die Runnable wird entfernt (removeCallback). Wird innerhalb einer
     * Minute keine neue Bus-Region betreten, dann geht die Applikation
     * davon aus, dass der Bus endgültig verlassen wurde und eine Subsection
     * wird erzeugt
     */
    public void setCountDownAfterLeavingBus()
    {


        _handlerCountdownBus = new Handler();

        _runnableForHandlerCountdownBus = new CountDownAfterLavingBusRunnable();


        _handlerCountdownBus.postDelayed(_runnableForHandlerCountdownBus,1000);
    }


    private class CountDownAfterLavingBusRunnable implements Runnable
    {
        public CountDownAfterLavingBusRunnable()
        {
            Log.e("Runnable", " created");
        }
        @Override
        public void run() {
            Log.e("Timer", " terminated");
        }
    }



    public void reactToGPSTurnedOff()
    {
        if (monitoringActivity != null && Preferences.getBooleanGPSUsage())
        {
            monitoringActivity.showGPSGuardDialog();

        }
        else if (monitoringActivity==null && Preferences.getBooleanGPSUsage())
        {
            showNotification("GPS wurde deaktivert!","Bitte wieder einschalten!");
        }
    }

















    @Override
    public void onLocationChanged(Location location)
    {
        Log.e("Current Lat:",""+location.getLatitude());
        Log.e("Current Lon:",""+location.getLongitude());
    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    public void initLocationManager()
    {
        _locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try
        {
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
        catch (SecurityException e)
        {

        }

    }

    public void checkBeaconWithGPSCoordinates(String minor)
    {
        String beaconStation = minorStationMap.get(minor);

        //get GPS-Coordinates
        String gpsCoordinates = StationGPSCoordinatesHelper.getCoordinatesForStation(this,beaconStation);

        String gpsCoordinatesArray[] = gpsCoordinates.split(":");
        String latitude = gpsCoordinatesArray[0];
        String longitude = gpsCoordinatesArray[1];
        double latitudeDouble = Double.parseDouble(latitude);
        double longitudeDouble = Double.parseDouble(longitude);

        String currentLatitude = Preferences.getCurrentGPSCoordinatesLatitude();
        String currentLongitude = Preferences.getCurrentGPSCoordinatesLongitude();
        double currentLatitudeDouble = Double.parseDouble(currentLatitude);
        double currentLongitudeDouble = Double.parseDouble(currentLongitude);

        //Abweichung
        double deviationLatitude = Math.abs(currentLatitudeDouble - latitudeDouble);
        double deviationLongitude = Math.abs(currentLongitudeDouble - longitudeDouble);

        //wenn die Abweichung von den tatsächlichen Koordinaten zu Groß ist
        if (deviationLatitude>0.000025 || deviationLongitude>0.00008)
        {

        }


    }




    //Methoden für BeaconConsumer, RangeNotifier

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region)
    {
        if (beacons.size()>0)
        {
            for (Beacon b : beacons)
            {
                if (b.getId1().toString().equals(Values.GLOBAL_UUID))
                {
                    Log.e("DISTANZ","" + b.getDistance());
                }
            }
        }
    }

    @Override
    public void onBeaconServiceConnect()
    {
        beaconManager.setRangeNotifier(this);
    }


}
