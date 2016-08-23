package com.cyberlandgo.felix.bachelorarbeit20.database.models;

/**
 * Created by Felix on 06.07.2016.
 */
public class Subsection
{
    private long _id;
    String _line;
    String _from;
    String _to;

    public Subsection()
    {

    }

    public Subsection(String line, String from, String to)
    {
        this._line = line;
        this._from = from;
        this._to = to;


    }

    public void setId(long id)
    {
        this._id = id;
    }

    public long getId()
    {
        return this._id;
    }
    public void setLine(String line)
    {
        this._line = line;
    }

    public String getLine()
    {
        return this._line;
    }

    public void setFrom(String from)
    {
        this._from = from;
    }

    public String getFrom()
    {
        return this._from;
    }

    public void setTo(String to)
    {
        this._to = to;
    }

    public String getTo()
    {
        return this._to;
    }
}
