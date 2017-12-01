package replica;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;

import dbs.corba.FailureFreeFE;

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
						      Map<String, Integer> RMUDPPorts, ORB orb, POA rootPOA) throws UserException
	{
		this.RMHostName = host;
		this.startReplica(branchList, branchPorts);
		
		this.RMUDPPortsList = RMUDPPorts;
		this.crashedReplicaList = new HashSet<String>();
		
		this.orb = orb;
		this.rootPOA = rootPOA;
		
		startUDPServer();
		startHeartBeatChecker();
	}
	
	
	private void startReplica(String[] branchList, String[] branchPorts) throws UserException
	{
		for(int i = 0; i < branchList.length; i++)
		{
			String branchID = branchList[i];
			int branchPort = new Integer(branchPorts[i]);
			
			FailureFreeFE branchServer = 
		}
		
	}
	
	private void startHeartBeatChecker()
	{
		// TODO Auto-generated method stub
		
	}

	private void startUDPServer()
	{
		// TODO Auto-generated method stub
		
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
