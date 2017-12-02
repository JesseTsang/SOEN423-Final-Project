package replicaManager;

import dbs.corba.FailureFreeFE;

public class ServerDetails
{
	private String UDPHost;
	private int UDPPort;
	private FailureFreeFE serverCORBAL;
	private int failureCount;
	
	public ServerDetails(String serverName, int portNum, FailureFreeFE serverCORBAL)
	{
		this.UDPHost = serverName;
		this.UDPPort = portNum;
		this.serverCORBAL = serverCORBAL;
		this.failureCount = 0;
	}

	public String getUDPHost()
	{
		return UDPHost;
	}

	public int getUDPPort()
	{
		return UDPPort;
	}

	public FailureFreeFE getServerCORBAL()
	{
		return serverCORBAL;
	}

	public int getFailureCount()
	{
		return failureCount;
	}

	public void setFailureCount(int failureStatus)
	{
		this.failureCount = failureStatus;
	}
}
