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
		mModel.setSelectedCustomer(mModel.getCustomers().get(0));
		mModel.setSelectedLog(mModel.getLogsForCustomer(mModel.getSelectedCustomer()).get(0));
		
	
		
		mView.addChangeServerStateListener(e -> mModel.changeServerStatus());
		mView.addSelectedCustomerListener(e -> mModel.setSelectedCustomer(mView.getSelectedCustomer()));
		mView.addSelectedLogListener(e -> mModel.setSelectedLog(mView.getSelectedLog()));
		
		mView.setVisible(true);
	}

}
