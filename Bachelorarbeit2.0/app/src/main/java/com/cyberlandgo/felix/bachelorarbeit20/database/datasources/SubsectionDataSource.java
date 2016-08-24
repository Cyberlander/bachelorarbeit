package com.cyberlandgo.felix.bachelorarbeit20.database.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.cyberlandgo.felix.bachelorarbeit20.database.MySQLiteHelper;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Subsection;

import java.util.ArrayList;

/**
 * Created by Felix on 06.07.2016.
 */
public class SubsectionDataSource
{
    private SQLiteDatabase database;
    private MySQLiteHelper mySQLiteHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID2,
                                    MySQLiteHelper.COLUMN_LINE,
                                    MySQLiteHelper.COLUMN_FROM,
                                    MySQLiteHelper.COLUMN_TO};

    public SubsectionDataSource(Context context)
    {
        mySQLiteHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException
    {
        database = mySQLiteHelper.getWritableDatabase();
    }

    public void close()
    {
        mySQLiteHelper.close();
    }

    public Subsection createSubsection(String line, String from, String to)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LINE, line);
        values.put(MySQLiteHelper.COLUMN_FROM, from);
        values.put(MySQLiteHelper.COLUMN_TO, to);

        long insertid = database.insert(MySQLiteHelper.TABLE_SUBSECTIONS, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_SUBSECTIONS, allColumns, MySQLiteHelper.COLUMN_ID2 + " = " + insertid,
                null,null,null,null);
        cursor.moveToFirst();
        Subsection newSubsection = cursorToSubsection(cursor);
        cursor.close();

        return newSubsection;
    }

    public void deleteSubsection(Subsection subsection)
    {
        long id = subsection.getId();
        database.delete(MySQLiteHelper.TABLE_SUBSECTIONS, MySQLiteHelper.COLUMN_ID2 + " = " + id, null);
    }

    public void deleteAllSubsections()
    {
        database.delete(MySQLiteHelper.TABLE_SUBSECTIONS,null,null);
    }

    public ArrayList<Subsection> getAllSubsections()
    {
        ArrayList<Subsection> subsections = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_SUBSECTIONS, allColumns, null,null,null,null,null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            Subsection subsection = cursorToSubsection(cursor);
            subsections.add(subsection);
            cursor.moveToNext();
        }
        cursor.close();
        return subsections;
    }

    private Subsection cursorToSubsection(Cursor cursor)
    {
        Subsection subsection = new Subsection();
        subsection.setId(cursor.getLong(0));
        subsection.setLine(cursor.getString(1));
        subsection.setFrom(cursor.getString(2));
        subsection.setTo(cursor.getString(3));
        return subsection;
    }

}
