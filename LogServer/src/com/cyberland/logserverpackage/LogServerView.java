package com.cyberland.logserverpackage;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;

public class LogServerView extends JFrame implements Observer
{
	LogServerModel model;
	
	JPanel _masterPanel;
	JPanel _serverControlPanel;
	JPanel _logPanel;
	JPanel _chooseCustomerPanel;
	BoxLayout _boxlayoutServerControlPanel;
	BoxLayout _boxlayoutChooseCustomerPanel;
	
	JButton _startStopServerButton;
	JLabel _statusServerLabel;
	
	
	JComboBox _chooseCustomerJComboBox;
	
	public LogServerView(LogServerController controller)
	{
		//todo
		model = new LogServerModel();
		
		setTitle("Log-Server");
		setSize(650,250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		Container content = getContentPane();
		
		//Panel für die die Server-Kontrolle
		_serverControlPanel = new JPanel();
		
		//Panel für die Auswahl der Kunden
		_chooseCustomerPanel = new JPanel();
		
		//Log-Panel
		_logPanel = new JPanel();
		
		_boxlayoutServerControlPanel = new BoxLayout(_serverControlPanel,BoxLayout.X_AXIS);
		_boxlayoutChooseCustomerPanel = new BoxLayout(_chooseCustomerPanel,BoxLayout.Y_AXIS);
		
		
		//initialisieren der UI-Komponenten
		_startStopServerButton = new JButton("Start Server");
		_startStopServerButton.setSize(300,50);
		
		_statusServerLabel = new JLabel("Server not running");
		
		
		List<String> customersList = model.getCustomers();
		String[] customersArray = new String[customersList.size()];
		customersList.toArray(customersArray);
		
		
		_chooseCustomerJComboBox = new JComboBox(customersArray);
		//_chooseCustomerJComboBox.setSelectedIndex(0);
		
		
		//Hinzufügen von UI-Elementen zu den Paneln
		//Server Kontrolle
		_serverControlPanel.add(_startStopServerButton);
		_serverControlPanel.add(_statusServerLabel);
		//Customer Panel
		_chooseCustomerPanel.add(_chooseCustomerJComboBox);
		
		
		//Panel-Hierarchie
		content.add(_serverControlPanel,BorderLayout.NORTH);
		content.add(_chooseCustomerPanel,BorderLayout.WEST);

		
	
		
	}

	@Override
	public void update(Observable o, Object arg)
	{
		// TODO Auto-generated method stub
		
	}

}
