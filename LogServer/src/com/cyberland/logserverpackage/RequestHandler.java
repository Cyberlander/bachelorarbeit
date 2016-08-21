package com.cyberland.logserverpackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RequestHandler extends Thread
{
	private Socket socket = null;
	
	public RequestHandler(Socket socket) throws IOException
	{
		super("RequestHandler"); //Name des Threads
		this.socket = socket;
		
	}
	
	public void run()
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String request = in.readLine();
			System.out.println("Erhalten: " + request);
			socket.close();
		}
		catch (IOException e)
		{
			
		}
		
	}

}
