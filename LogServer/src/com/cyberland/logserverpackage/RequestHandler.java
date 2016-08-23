package com.cyberland.logserverpackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
			writeInLog(request);

			
			socket.close();
		}
		catch (IOException e)
		{
			
		}
		
	
	}
	public void writeInLog(String request)
	{
		String[] parts = request.split("/");
		String customerName = parts[0];
		String time = parts[1];
		String station = parts[2];

		String filePathCustomer = "data/customerlogs/"+customerName;
		Path path = Paths.get(filePathCustomer);
		

		
		if (Files.notExists(path))
		{
			File dir = new File("data/customerlogs/"+customerName);
			dir.mkdir();
		}
		
	}

}
