package replica;

import java.util.Map;
import java.util.Set;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

public class ReplicaManagerImpl implements ReplicaManagerInterface
{
	private String RMHostName;
	
	private Map<String, ServerDetails> serversList;
	private Map<String, Integer> RMUDPPortsList;
	private Set<String> crashedReplicaList;
	
	//CORBA Variables
	private ORB orb;
	private POA rootPOA;
	
	public ReplicaManagerImpl(String host, String[] branchList, String[] branchPorts, 
						      Map<String, Integer> RMUDPPorts, ORB orb, POA rootPOA)
	{
		
		
		
		
	}

	@Override
	public void replicaReplace(String replica, String rmID)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean heartbeatChecker(String replica)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void heartbeatResponse(String replica, String destRM, String response)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean ifReplicaCrash(String replica, String destRM, String sourceRM)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String handleUDPMsg(String message)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getRMHostName()
	{
		return RMHostName;
	}

}
