package com.cyberland.logserverpackage;




public class LogServerController
{
	private LogServerView mView;
	private LogServerModel mModel;
	
	public LogServerController()
	{
		mModel = new LogServerModel();
		mModel.getCustomers();
		mView = new LogServerView(this);
		mView.setVisible(true);
	}

}
