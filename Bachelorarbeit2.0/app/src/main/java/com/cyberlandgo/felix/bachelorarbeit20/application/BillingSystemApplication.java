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
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.cyberlandgo.felix.bachelorarbeit20.BroadcastReceiver.BluetoothGuard;
import com.cyberlandgo.felix.bachelorarbeit20.BroadcastReceiver.DateChangedReceiver;
import com.cyberlandgo.felix.bachelorarbeit20.Helper.CalendarHelper;
import com.cyberlandgo.felix.bachelorarbeit20.Helper.RegionBuilder;
import com.cyberlandgo.felix.bachelorarbeit20.Helper.StationDistanceHelper;
import com.cyberlandgo.felix.bachelorarbeit20.Helper.TicketDetailHelper;
import com.cyberlandgo.felix.bachelorarbeit20.R;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.StationDataSource;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.SubsectionDataSource;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Station;
import com.cyberlandgo.felix.bachelorarbeit20.ui.MainActivity;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Felix on 06.08.2016.
 */
public class BillingSystemApplication extends Application implements BootstrapNotifier,SharedPreferences.OnSharedPreferenceChangeListener
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


    //Datasource für die Teilstrecken
    SubsectionDataSource _subsectionDataSource;

    @Override
    public void onCreate()
    {
        super.onCreate();

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

        //instanziieren der Schnittstelle zur Teilstrecken-Tabelle
        _subsectionDataSource = new SubsectionDataSource(this);
        _subsectionDataSource.open();
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
        Identifier currentMajorIdentifier = region.getId2();
        Identifier currentMinorIdentifier = region.getId3();

        //aktuelle Identifier in Strings umwandeln
        String currentMajorIdentifierString = "" + currentMajorIdentifier;
        String currentMinorIdentifierString = "" + currentMinorIdentifier;

        //für Debugging: aktuelle Station loggen
        Preferences.saveCurrentStartstation(minorStationMap.get(currentMinorIdentifierString));


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
        }

        //wenn der Train-Automat schon einmal durchlaufen wurde und eine weitere
        //Startstation betreten wurde
        else if (!Preferences.getSecondStartStation().equals(""))
        {
            positionStartStation = StationDistanceHelper.getPositionForName(Preferences.getSecondStartStation());
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
        _subsectionDataSource.createSubsection(TicketDetailHelper.getLineForMinorID(Preferences.getCurrentMinorIDTargetStation()),
                Preferences.getStartStation(),
                Preferences.getCurrentTargetStation());

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
}
