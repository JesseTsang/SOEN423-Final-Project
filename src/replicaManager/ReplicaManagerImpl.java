package replicaManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;

import dbs.corba.FailureFreeFE;

public class ReplicaManagerImpl implements ReplicaManagerInterface
{
	private String RMHost;
	
	private Map<String, ServerDetails> serversList;
	private Map<String, Integer> RMUDPPortsList;
	private Set<String> crashedReplicaList;
	
	private static final int FAILURE_THRESHOLD = 3;
	
	//CORBA Variables
	private ORB orb;
	private POA rootPOA;
	
	public ReplicaManagerImpl(String host, String[] branchList, String[] branchPorts, 
						      Map<String, Integer> RMUDPPorts, ORB orb, POA rootPOA) throws UserException
	{
		this.RMHost = host;
		this.startReplica(branchList, branchPorts);
		
		this.RMUDPPortsList = RMUDPPorts;
		this.crashedReplicaList = new HashSet<String>();
		
		this.orb = orb;
		this.rootPOA = rootPOA;
		
		startUDPServer();
		startHeartBeatChecker();
	}
	
	//Each RM will start the 4 replica represents the regional branches
	private void startReplica(String[] branchList, String[] branchPorts) throws UserException
	{		
		for(int i = 0; i < branchList.length; i++)
		{
			String branchID = branchList[i];
			String branchCobraTag = RMHost + "_" + branchID;
			int branchPort = new Integer(branchPorts[i]);
			
			//1. Create the replica
			FailureFreeFE branchServer = ReplicaFactory.createBranch(branchCobraTag, branchID, branchPort, RMHost, rootPOA, orb);
			
			//2. Add the replica to the replic list (serversList).
			ServerDetails serverDetails = new ServerDetails(branchID, branchPort, branchServer);
			this.serversList.put(branchID, serverDetails);
		}
		
	}
	
	//Listens for incoming requests from other RM or FE.
	private void startUDPServer()
	{
		RMUDPServer UDPServer = new RMUDPServer(this, RMUDPPortsList.get(RMHost));
		
		Thread thread = new Thread(UDPServer);
		thread.start();				
	}
	
	private void startHeartBeatChecker()
	{
		HeartbeatDispatcher dispatcher = new HeartbeatDispatcher(this, this.serversList.keySet());
		
		Thread thread = new Thread(dispatcher);
		thread.start();
	}
	
	@Override
	//Method for FE. This method will invoke by FE when FE notices a replica server produce 3 wrong result.
	public void replicaFailure(String replica, String RMHost)
	{
		if(this.RMHost.equals(RMHost) && (RMHost != null))
		{
			ServerDetails serverDetails = this.serversList.get(replica);
			
			int failureCount = serverDetails.getFailureCount();
			failureCount++;
			
			if(failureCount == FAILURE_THRESHOLD)
			{
				replicaReplace(replica);
				serverDetails.setFailureCount(0);
				
				System.out.println("ReplicaManagerImpl Log: | Replica: " + replica + " replaced.");
			}
			else
			{
				serverDetails.setFailureCount(failureCount);		
			}		
		}	
	}
	
	@Override
	//This method handle FE's call about replic producing incorrect result.
	//If a replica is producing an incorrect result 3 times, then RM will invoke the kill method to have it replaced. 
	public void replicaReplace(String replica)
	{
		//1. Kill the existing replica
		replicaTerminate(replica);
		
		//2. Restart the replica
		String branchCobraTag = RMHost + "_" + replica;
		ServerDetails serverDetails = this.serversList.get(replica);
		int branchPort = serverDetails.getUDPPort();
		
		try
		{
			ReplicaFactory.createBranch(branchCobraTag, replica, branchPort, RMHost, rootPOA, orb);			
		}
		catch (UserException e)
		{
			e.printStackTrace();
			System.err.println("ReplicaManagerImpl Log: | replicaReplace() Error: " + e.getMessage());
		}

	}
	
	@Override
	public void replicaTerminate(String replica)
	{
		String branchCobraTag = RMHost + "_" + replica;
		ServerDetails serverDetails = this.serversList.get(replica);
		
		FailureFreeFE branchCORBAL = serverDetails.getServerCORBAL();
		branchCORBAL.shutdown();
		
		try
		{
			ReplicaFactory.removeBranch(branchCobraTag, orb);
		}
		catch (UserException e)
		{	
			e.printStackTrace();
			System.err.println("ReplicaManagerImpl Log: | replicaTerminate() Error: " + e.getMessage());
		}
	}

	@Override
	//Method for other RM. This will check if a replica is online.
	public boolean heartbeatChecker(String replica)
	{
		boolean result = false;		
		String branchCobraTag = RMHost + "_" + replica;
		
		try
		{
			result = ReplicaFactory.isCORBAObjExist(branchCobraTag, orb);
		}
		catch (UserException e)
		{
			e.printStackTrace();
			
			System.err.println("ReplicaManagerImpl Log: | heartbeatChecker(): | " + replica + " is not online.");
			System.err.println("ReplicaManagerImpl Log: | heartbeatChecker(): | " + e.getMessage());
			
			result = false;
			
			return result;
		}
		
		return result;
	}

	@Override
	//This will pass the heartbeat response to other RM.
	public void heartbeatResponse(String replica, String destRM, String response)
	{
		boolean isAlive = Boolean.getBoolean(response);
		String branchCobraTag = destRM + "_" + replica;
		
		//If there is a result
		if(isAlive == true)
		{
			//And the replic is true (online), then we remove it from crashed replica list
			if(crashedReplicaList.contains(branchCobraTag))
			{
				crashedReplicaList.remove(branchCobraTag);
			}
		}
		//Else, server is dead, add the crashed replica to the crashed replica list and notify other RMs
		else
		{
			if(!crashedReplicaList.contains(branchCobraTag))
			{
				crashedReplicaList.add(branchCobraTag);
				sendReplicaCrashNotice(replica, destRM);
			}	
		}
	}
	
	//This method to have other RM check if a replia has crashed
	private void sendReplicaCrashNotice(String replica, String destRM)
	{
		boolean isAlive = false;
				
		for(Entry<String, Integer> entry : RMUDPPortsList.entrySet())
		{
			if((!entry.getKey().equals(RMHost)) && (!entry.getKey().equals(destRM)))
			{
				int replicaPort = entry.getValue();
				String status = UDPStatus.CRASH.name();			
				String message = status + ";" + RMHost + ";" + replica + ";" + destRM;
					
				RMUDPClient client = new RMUDPClient("localhost", replicaPort);
				String result = client.send(message);
				
				isAlive = Boolean.getBoolean(result);
			}
			
		}
		
		//If the replica is confirmed crashed. Notify destRM and remove replica from crashed list.
		if(isAlive == true)
		{
			//1. Notify target RM
			int destRMPort = RMUDPPortsList.get(destRM);
			
			String status = UDPStatus.REMOVE_CRASH.name();
			String message = status + ";" + RMHost + ";" + replica;
			
			RMUDPClient client = new RMUDPClient("localhost", destRMPort);
			client.send(message);
			
			//2. Remove from local crashed replica list
			String branchCobraTag = destRM + "_" + replica;
			crashedReplicaList.remove(branchCobraTag);
		}
		
	}

	@Override
	//Method for other RM. To confirm if a replica is crashed.
	public boolean processReplicaCrashNotice(String replica, String replicaRM, String sourceRM)
	{
		boolean result = false;
		
		String branchCobraTag = replicaRM + "_" + replica;
		
		if(crashedReplicaList.contains(branchCobraTag))
		{
			result = true;
		}
		
		return result;
	}

	@Override
	//This method will process the message from other Class and invoke appropiate methods
	public String handleUDPMsg(String message)
	{
		String result = null;
		
		//If not empty message from FE
		if(message != null & message.length() > 0)
		{
			String[] messageArray = message.trim().split(";");
			
			if((messageArray != null) && (messageArray.length > 2))
			{
				String status = messageArray[0];
				String sourceRMHost = messageArray[1];
				String[] params = Arrays.copyOfRange(messageArray, 2, messageArray.length);
				
				if(status.equals(UDPStatus.CRASH.name()))
				{
					result = Boolean.toString(processReplicaCrashNotice(params[0], params[1], sourceRMHost));
				}
				
				if(status.equals(UDPStatus.FAILURE.name()))
				{
					replicaFailure(params[0], sourceRMHost);
				}
				
				if(status.equals(UDPStatus.HEARTBEAT.name()))
				{
					result = Boolean.toString(heartbeatChecker(params[0]));
				}
				
				if(status.equals(UDPStatus.REMOVE_CRASH.name()))
				{
					replicaReplace(params[0]);
				}
			}
		}
			
		return result;
	}
	
	public void setByzantineError(String replica)
	{
		ServerDetails serverDetails = this.serversList.get(replica);
		
		//This will set the replic (BankServerQC etc) with a flag,
		//so if we have a flag == true, we just produce a wrong result.
		serverDetails.getServerCORBAL().setByzantineFlag(true);
	}
	
	@Override
	public String getRMHost()
	{
		return RMHost;
	}

	public Map<String, Integer> getRMUDPPortsList()
	{
		return RMUDPPortsList;
	}
}
