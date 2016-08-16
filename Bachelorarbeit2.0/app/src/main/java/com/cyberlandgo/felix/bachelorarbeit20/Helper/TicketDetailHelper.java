package com.cyberlandgo.felix.bachelorarbeit20.Helper;

import com.cyberlandgo.felix.bachelorarbeit20.application.Preferences;
import com.cyberlandgo.felix.bachelorarbeit20.application.Values;

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
        else if (linelength.equals(Values.KURZSTRECKE))
        {
            resultString = "2,00 Euro";
        }
        else if (linelength.equals(Values.KURZSTRECKE))
        {
            resultString = "3,00 Euro";
        }
        
        return resultString;
    }

}
