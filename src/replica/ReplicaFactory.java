package replica;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;

import dbs.corba.FailureFreeFEPOA;

public class ReplicaFactory
{
	//Call by ReplicaManagerImpl to create Replica servers
	public static void createBranch(String branchCobraTag, String branchID, int branchPort, 
									String RMHost, POA rootPOA, ORB orb) throws UserException
	{
		
		FailureFreeFEPOA impl = getBranch(branchID, branchPort, RMHost);
		
	}

	private static FailureFreeFEPOA getBranch(String branchID, int branchPort, String RMHost)
	{
		FailureFreeFEPOA branch = null;
		
		//This will create the Branch instance based on the replica manager name
		switch(RMHost)
		{
			case "rmBC":
				break;
			case "rmMB":
				break;
			case "rmNB":
				break;
			case "rmQC":
				break;
				
		}
		
		return null;
	}

}
