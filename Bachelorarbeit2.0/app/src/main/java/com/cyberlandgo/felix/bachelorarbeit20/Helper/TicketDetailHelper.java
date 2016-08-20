package com.cyberlandgo.felix.bachelorarbeit20.Helper;

import com.cyberlandgo.felix.bachelorarbeit20.application.Preferences;
import com.cyberlandgo.felix.bachelorarbeit20.application.Values;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Felix on 16.08.2016.
 */
public class TicketDetailHelper
{
    public static String getLineLengthForAmountOfStations()
    {
        int amountOfStations = Preferences.getCurrentAmountOfStations();
        String resultString = "";

        if (amountOfStations<=5)
        {
            resultString = Values.KURZSTRECKE;
        }
        else if (amountOfStations>=6 && amountOfStations<=10)
        {
            resultString = Values.MITTELSTRECKE;
        }
        else if (amountOfStations>10)
        {
            resultString = Values.LANGSTRECKE;
        }

        return resultString;
    }

    public static String getPriceForLineLength(String linelength)
    {
        String resultString = "";
        if (linelength.equals(Values.KURZSTRECKE))
        {
            resultString = "1,00 Euro";
        }
        else if (linelength.equals(Values.MITTELSTRECKE))
        {
            resultString = "2,00 Euro";
        }
        else if (linelength.equals(Values.LANGSTRECKE))
        {
            resultString = "3,00 Euro";
        }

        return resultString;
    }


    public static String getLineForMinorID(String minor)
    {
        String substring = minor.substring(0,3);
        Map<String,String> minorLineMap = new HashMap<>();
        minorLineMap.put("200","U2");
        minorLineMap.put("281","281er");

        return minorLineMap.get(substring);
    }
}
