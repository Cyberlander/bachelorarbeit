package com.cyberlandgo.felix.bachelorarbeit20.application;

import android.app.Application;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.cyberlandgo.felix.bachelorarbeit20.Helper.RegionBuilder;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.StationDataSource;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Station;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 06.08.2016.
 */
public class BillingSystemApplication extends Application implements BootstrapNotifier
{
    Preferences preferences;
    int currentStatusStateMachine;

    //die StationDataSource ist die Schnittstelle zu lokalen SQLite-Datenbank
    //in der die Beacon-Regionen zugeordnet zu ihrem Standort gespeichert sind
    StationDataSource stationDataSource;

    ArrayList<Station> arrayListStationObjects;
    List<Region> listRegions;


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

        //holt über die StationDatasource Stationsobjekte aus der Datenbank
        arrayListStationObjects = getListOfStationObjectsFromDatabase();

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


        if (currentStatusStateMachine == StateMachine.STATUS_NOT_RUNNING)
        {
            Toast.makeText(getApplicationContext(), currentMajorIdentifierString +":" +currentMajorIdentifier,Toast.LENGTH_LONG).show();
            Log.e("!!!!!!!!!!",currentMajorIdentifierString +":" +currentMajorIdentifier);


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
}
