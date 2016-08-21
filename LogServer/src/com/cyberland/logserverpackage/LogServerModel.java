package com.cyberland.logserverpackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class LogServerModel extends Observable 
{
	List<String> _customer;
	String _serverStatus;
	public String _buttonTextServerStart;
	
	boolean serverUp;
	
	public List<String> getCustomers()
	{
		List<String> customerList = new ArrayList<String>();
		
		String filePath = "data/customers.txt";
		String line = null;
		
		try 
		{
			FileReader fileReader = new FileReader(filePath);
			
			
			//Filereader immer in BufferedReader wrappen
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while ((line = bufferedReader.readLine()) != null)
			{
				customerList.add(line);
			}
			bufferedReader.close();
		}
		catch (IOException e)
		{
			
		}
		
		return customerList;
	}
	
	public String getServerStatus()
	{
		return _serverStatus;
	}
	
	public void setServerStatus(String newStatus)
	{
		this._serverStatus = newStatus;
		setChanged();
		notifyObservers(this);
	}
	
	public void changeServerStatus()
	{
		if (_serverStatus.equals("Server not running"))
		{
			_serverStatus = "Server running";
		}
		else if (_serverStatus.equals("Server running"))
		{
			_serverStatus = "Server not running";
		}
		changeTextButtonStartServer();
		setChanged();
		notifyObservers(this);
	}
	
	
	
	public void setTextButtonStartServer(String text)
	{
		_buttonTextServerStart = text;
		setChanged();
		notifyObservers(this);
	}
	
	public String getTextButtonStartServer()
	{
		return this._buttonTextServerStart;
	}
	
	
	
	
	public void changeTextButtonStartServer()
	{
		if (_serverStatus.equals("Server not running"))
		{
			_buttonTextServerStart = "Start Server";
		}
		else if (_serverStatus.equals("Server running"))
		{
			_buttonTextServerStart = "Stop Server";
		}
		setChanged();
		notifyObservers(this);	
	}
	

}
