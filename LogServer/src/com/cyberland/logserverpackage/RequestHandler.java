package com.cyberland.logserverpackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
		

		//erstellt einen Ordner, falls es noch keinen
		//Log-Ordner für den Kunden gibt
		if (Files.notExists(path))
		{
			File dir = new File("data/customerlogs/"+customerName);
			dir.mkdir();
		}
		
		
		//holt sich die Kunden-Datei, um zu schauen, ob Kunde schon enthalten ist
		List<String> customers = getCustomers();
		
		//wenn Kunde bereits in der Datei enthalten ist
		if (customers.contains(customerName))
		{
			
		}
		else
		{
			try
			{
				//das zweite Argument gibt an, dass der Kunde an die Datei angehängt
				//werden soll und nicht die ganze Datei überschrieben werden soll
				FileWriter fileWriter = new FileWriter("data/customers.txt",true);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				PrintWriter printWriter = new PrintWriter(bufferedWriter);
				printWriter.println();
				printWriter.write(customerName);
				printWriter.close();
				bufferedWriter.close();
				fileWriter.close();
				
			} 
			catch (IOException e)
			{

			}
		}
	}
	
	
	
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

}
