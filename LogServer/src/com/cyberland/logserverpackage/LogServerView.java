package com.cyberland.logserverpackage;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LogServerView extends JFrame implements Observer
{
	JPanel _masterPanel;
	JPanel _serverControlPanel;
	JPanel _logPanel;
	JPanel _chooseCustomerPanel;
	BoxLayout _boxlayoutServerControlPanel;
	BoxLayout _boxlayoutChooseCustomerPanel;
	
	JButton _startStopServerButton;
	JLabel _statusServerLabel;
	
	public LogServerView()
	{
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
		_statusServerLabel = new JLabel("Server not running");
		
		//Hinzufügen von UI-Elementen zu den Paneln
		_serverControlPanel.add(_startStopServerButton);
		_serverControlPanel.add(_statusServerLabel);
		
		
		//Panel-Hierarchie
		content.add(_serverControlPanel,BorderLayout.NORTH);
		content.add(_chooseCustomerPanel,BorderLayout.EAST);
		
		pack();
		
	
		
	}

	@Override
	public void update(Observable o, Object arg)
	{
		// TODO Auto-generated method stub
		
	}

}
