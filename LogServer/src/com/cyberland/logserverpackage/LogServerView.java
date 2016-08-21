package com.cyberland.logserverpackage;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerListModel;


public class LogServerView extends JFrame implements Observer
{
	LogServerModel model;
	
	Container _mainContainer;
	JPanel _masterPanel;
	JPanel _serverControlPanel;
	JPanel _logPanel;
	JPanel _chooseCustomerPanel;
	BoxLayout _boxlayoutServerControlPanel;
	BoxLayout _boxlayoutChooseCustomerPanel;
	
	JButton _startStopServerButton;
	JLabel _statusServerLabel;
	
	
	JComboBox _chooseCustomerJComboBox;
	
	JScrollPane _scrollPaneTable;
	JTable _logTable;
	
	String[] _columnNames = {"Zeit","Haltestelle"};
	
	//defaultData for Table
	Object[][] data = {
		    {"4:20", "Out of Mind"}};
	
	public LogServerView(LogServerController controller)
	{
		//todo
		model = new LogServerModel();
		
		setTitle("Log-Server");
		setSize(650,250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		
		
		_mainContainer = getContentPane();
		
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
		
		
		_chooseCustomerJComboBox = new JComboBox();
		//_chooseCustomerJComboBox.setSelectedIndex(0);
		
		
		
		
		_logTable = new JTable(data, _columnNames);
		_scrollPaneTable = new JScrollPane(_logTable);
		_logTable.setFillsViewportHeight(true);
		
		
		//Hinzufügen von UI-Elementen zu den Paneln
		//Server Kontrolle
		_serverControlPanel.add(_startStopServerButton);
		_serverControlPanel.add(_statusServerLabel);
		//Customer Panel
		_chooseCustomerPanel.add(_chooseCustomerJComboBox);
		
		_mainContainer.add(_scrollPaneTable, BorderLayout.CENTER);
		
		//Panel-Hierarchie
		_mainContainer.add(_serverControlPanel,BorderLayout.NORTH);
		_mainContainer.add(_chooseCustomerPanel,BorderLayout.WEST);
		
		
	
		
	}
	
	
	

	@Override
	public void update(Observable arg0, Object arg1) 
	{
		LogServerModel model = (LogServerModel) arg0;
		
		String serverStatus = model.getServerStatus();
		_statusServerLabel.setText(serverStatus);
		
		//Kundenliste
		List<String> customersList = model.getCustomers();
		String[] customersArray = new String[customersList.size()];
		customersList.toArray(customersArray);

		//Button
		_startStopServerButton.setText(model.getTextButtonStartServer());

		DefaultComboBoxModel newComboBoxModel = new DefaultComboBoxModel( customersArray );
		_chooseCustomerJComboBox.setModel(newComboBoxModel);

		
	}
	
	public void addChangeServerStateListener(ActionListener actionListener)
	{
		_startStopServerButton.addActionListener(actionListener);		
	}

}
