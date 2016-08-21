package com.cyberland.logserverpackage;




public class LogServerController
{
	private LogServerView mView;
	private LogServerModel mModel;
	
	public LogServerController()
	{
		mModel = new LogServerModel();
		mView = new LogServerView(this);
		
		mModel.addObserver(mView);
		mModel.setServerStatus("Server not running");
		mModel.setTextButtonStartServer("Start Server");
		
		
		mView.addChangeServerStateListener(e -> mModel.changeServerStatus());
		
		mView.setVisible(true);
	}

}
