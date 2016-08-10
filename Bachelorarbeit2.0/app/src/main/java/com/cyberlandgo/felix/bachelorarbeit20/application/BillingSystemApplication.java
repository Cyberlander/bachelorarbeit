package com.cyberlandgo.felix.bachelorarbeit20.application;

import android.app.Application;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.cyberlandgo.felix.bachelorarbeit20.Helper.CalendarHelper;
import com.cyberlandgo.felix.bachelorarbeit20.Helper.RegionBuilder;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.StationDataSource;
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
public class BillingSystemApplication extends Application implements BootstrapNotifier
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
            //TODO logs entfernen
            Toast.makeText(getApplicationContext(), currentMajorIdentifierString +":" +currentMajorIdentifier,Toast.LENGTH_LONG).show();
            Log.e("!!!!!!!!!!",currentMajorIdentifierString +":" +currentMajorIdentifier);


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
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);

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
        Log.e("!!!!!!!!!!","flies away");

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
        if (currentMajorNumber.equals("00000") || currentMajorNumber.equals("18475"))
        {
            Preferences.saveStatusStateMachine(StateMachine.STATUS_START_REGION_TRAIN);

        }
        //wenn eine Region betreten wurde, bei der die Major-ID 11111 ist (was für Bus steht)
        //dann ist der folgende Status STATUS_START_REGION_TRAIN
        else if (currentMajorNumber.equals("11111"))
        {
            Preferences.saveStatusStateMachine(StateMachine.STATUS_START_REGION_BUS);
        }

        //TODO logs entfernen
        Log.e("!!!!!!!!!!!!!",Preferences.getStartStation());
        Log.e("!!!!!!!!!!!!!",Preferences.getStartDate());
        Log.e("!!!!!!!!!!!!!",Preferences.getStartTime());

    }

}
