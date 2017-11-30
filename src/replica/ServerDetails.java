package replica;

import dbs.corba.FailureFreeFE;

public class ServerDetails
{
	private String UDPHost;
	private int UDPPort;
	private FailureFreeFE server;
	private int failureStatus;
	
	public ServerDetails(String serverName, int portNum, FailureFreeFE server)
	{
		this.UDPHost = serverName;
		this.UDPPort = portNum;
		this.server = server;
		this.failureStatus = 0;
	}

	public String getUDPHost()
	{
		return UDPHost;
	}

	public int getUDPPort()
	{
		return UDPPort;
	}

	public FailureFreeFE getServer()
	{
		return server;
	}

	public int getFailureStatus()
	{
		return failureStatus;
	}

	public void setFailureStatus(int failureStatus)
	{
		this.failureStatus = failureStatus;
	}
}
