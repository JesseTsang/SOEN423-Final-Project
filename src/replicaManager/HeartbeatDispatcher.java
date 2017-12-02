package replicaManager;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//This class will send out requests (heartbeats) to check if the replica in other RM is alive.
public class HeartbeatDispatcher implements Runnable
{
	public static int TIMEOUT = 3000;
	
	private ReplicaManagerImpl replicaManager;
	private String RMID;
	private Set<String> replicasList;
	private Map<String, Integer> RMUDPPorts;

	public HeartbeatDispatcher(ReplicaManagerImpl replicaManager, Set<String> replicasList)
	{
		this.replicaManager = replicaManager;
		this.replicasList = replicasList;
		
		this.RMID = replicaManager.getRMHost();
		this.RMUDPPorts = replicaManager.getRMUDPPortsList();
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				Thread.sleep(TIMEOUT);
				
				//We want to check all replic's status of OTHER RM's replica
				for(Entry<String, Integer> entry : RMUDPPorts.entrySet())
				{
					//We are only interested in OTHER RM's replic's status
					if(!entry.getKey().equals(RMID))
					{
						for(String branchID : replicasList)
						{
							//BranchID, RMHost, RMPort
							checkHeartbeat(branchID, entry.getKey(), entry.getValue());
						}
					}
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				System.err.println("HeartbeatDispatcher Log: | Error: | " + e.getMessage());
			}
		}
		
	}

	private void checkHeartbeat(String branchID, String destRM, Integer remoteRMPort)
	{
		RMUDPClient client = new RMUDPClient("localhost", remoteRMPort);
		
		String request = UDPStatus.HEARTBEAT.name() + ";" + RMID + ";" + branchID;
		String heartbeat = client.send(request);
		
		replicaManager.heartbeatResponse(branchID, destRM, heartbeat);
	}

}
