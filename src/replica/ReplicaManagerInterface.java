package replica;

public interface ReplicaManagerInterface
{
	//Method for FE. This method will invoke by FE when FE notices a replica server produce 3 wrong result.
	void replicaReplace(String replica, String rmID);
	
	//Method for other RM. This will check if a replica is online.
	boolean heartbeatChecker(String replica);
	
	//This will pass the heartbeat response to other RM.
	void heartbeatResponse(String replica, String destRM, final String response);
	
	//Method for other RM. To confirm if a replica is crashed.
	boolean ifReplicaCrash(String replica, String destRM, String sourceRM);
	
	//Method for other RM. This will handle commands from other RM
	String handleUDPMsg(String message);
	
	String getRMHostName();
}
