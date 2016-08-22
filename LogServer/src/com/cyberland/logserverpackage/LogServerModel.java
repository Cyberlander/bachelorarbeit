package com.cyberland.logserverpackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


public class LogServerModel extends Observable 
{
	List<String> _customer;
	String _serverStatus;
	public String _buttonTextServerStart;
	
	boolean serverUp;
	ServerSocket _serverSocket;
	boolean _listening;
	
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
			startServer();
		}
		else if (_serverStatus.equals("Server running"))
		{
			_serverStatus = "Server not running";
			_listening = false;
			try
			{
				_serverSocket.close();
			}
			catch (IOException e)
			{
				
			}
			
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
	
	
	public void startServer()
	{
		Thread thread = new Thread(new ServerRunnable());
		thread.start();

	}
	
	
	
	
	private class ServerRunnable implements Runnable
    {
        @Override
        public void run()
        {
        	try
        	{
            	_serverSocket = new ServerSocket(6666, 100, InetAddress.getByName("0.0.0.0"));
            	
            	_listening = true;
            	System.out.println("Server listening on Port 6666");
            	
            	while (_listening)
            	{
            		new RequestHandler(_serverSocket.accept()).start();
            	}
            	_serverSocket.close();
            	
        	}
        	catch (IOException e)
        	{	

        	}


        }

    }
	

}
