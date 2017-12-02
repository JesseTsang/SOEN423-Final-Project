package replicaManager;

public interface ReplicaManagerInterface
{
	//Method for FE. This method will invoke by FE when FE notices a replica server produce 3 wrong result.
	void replicaFailure(String replica, String rmID);
	
	//Method for RM. This method follows replicaFailure() when a replica produces incorrect results 3 times.
	//Can only kill local replica.
	void replicaReplace(String replica);
	
	//Method to simulate process crash
	void replicaTerminate(String replica);
	
	//Method for other RM. This will check if a replica is online.
	boolean heartbeatChecker(String replica);
	
	//This will pass the heartbeat response to other RM.
	void heartbeatResponse(String replica, String destRM, final String response);
	
	//Method for other RM. To confirm if a replica is crashed.
	boolean processReplicaCrashNotice(String replica, String replicaRM, String sourceRM);
	
	//Method for other RM. This will handle commands from other RM
	String handleUDPMsg(String message);
	
	String getRMHost();
}
