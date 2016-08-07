package com.cyberlandgo.felix.bachelorarbeit20.Helper;

import java.util.Calendar;

/**
 * Created by Felix on 25.05.2016.
 */
public class CalendarHelper
{
    Calendar calendar;
    public CalendarHelper()
    {
        calendar = Calendar.getInstance();
    }

    public String getDateString()
    {
        int tag = calendar.get(Calendar.DATE);
        int monat = calendar.get(Calendar.MONTH) + 1;
        int jahr = calendar.get(Calendar.YEAR);

        String dateString = tag + "." + monat + "." + jahr;
        return dateString;
    }

    public String getTimeString()
    {
        int stunde = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String minuteString;


        if (minute<10 && minute>0)
        {
            minuteString = "0" + minute;
        }
        else
        {
            minuteString = "" + minute;
        }
        String timeString = stunde + ":" + minuteString;
        return timeString;
    }
}
