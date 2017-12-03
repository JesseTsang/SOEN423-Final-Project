package replicaManager;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;

import dbs.corba.FailureFreeFE;
import dbs.corba.FailureFreeFEHelper;
import dbs.corba.FailureFreeFEPOA;
import replicas.BC.BankServerImpl;
import replicas.MB.BankServerMB;
import replicas.NB.BankServerNB;
import replicas.QC.BankServerQC;

public class ReplicaFactory
{
	//Call by ReplicaManagerImpl to create Replica servers
	public static FailureFreeFE createBranch(String branchCobraTag, String branchID, int branchPort, 
									String RMHost, POA rootPOA, ORB orb) throws UserException
	{
		
		//0. Create servant and register the servant with CORBA ORB.
		FailureFreeFEPOA replicaImpl = getBranch(branchID, branchPort, RMHost);
		
		//1. Get object reference from the servant
		org.omg.CORBA.Object ref = rootPOA.servant_to_reference(replicaImpl);
		
		//2. Cast the reference to a CORBA reference
		FailureFreeFE bref = FailureFreeFEHelper.narrow(ref);
		
		//3.0 Get the root naming context.
		//3.1 NameService invokes the name service 
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		
		//4.0 Use NamingContextExt which is part of the Interoperable
		//4.1 Naming Service (INS) specification.
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		
		//4. Bind the Object Reference in Naming
		NameComponent path[] = ncRef.to_name(branchCobraTag);
		ncRef.rebind(path, bref);
		
		System.out.println("ServerDriver Log: | Bank Server (CORBA Tag): " + branchCobraTag + " initialized." );
		
		return bref;
	}

	private static FailureFreeFEPOA getBranch(String branchID, int branchPort, String RMHost)
	{
		FailureFreeFEPOA branch = null;
		
		//This will create the Branch instance based on the replica manager name
		switch(RMHost)
		{
			case "rm1":
				switch(branchID)
				{
					case "BC":
						branch = new BankServerImpl(branchID+"101", branchPort, RMHost);
						break;
					case "MB":
						branch = new BankServerMB();
						break;
					case "NB":
						branch = new BankServerNB();
						break;
					case "QC":
						branch = new BankServerQC();
						break;				
				}
				break;
				
			case "rm2":
				switch(branchID)
				{
					case "BC":
						branch = new BankServerImpl("bc201", branchPort, RMHost);
						break;
					case "MB":
						branch = new BankServerMB();
						break;
					case "NB":
						branch = new BankServerNB();
						break;
					case "QC":
						branch = new BankServerQC();
						break;				
				}
				break;
				
			case "rm3":
				switch(branchID)
				{
					case "BC":
						branch = new BankServerImpl("bc301", branchPort, RMHost);
						break;
					case "MB":
						branch = new BankServerMB();
						break;
					case "NB":
						branch = new BankServerNB();
						break;
					case "QC":
						branch = new BankServerQC();
						break;				
				}
				break;
				
			default:
				branch = new BankServerQC();
				break;
				
		}
		
		return branch;
	}
	
	public static void removeBranch(String branchCobraTag, ORB orb) throws UserException
	{
		//1.0 Get the root naming context.
		//1.1 NameService invokes the name service 
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		
		//2.0 Use NamingContextExt which is part of the Interoperable
		//2.1 Naming Service (INS) specification.
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		
		NameComponent[] path = ncRef.to_name(branchCobraTag);
		ncRef.unbind(path);
	}
	
	public static boolean isCORBAObjExist(String branchCobraTag, ORB orb) throws UserException
	{
		boolean result;
		
		//1.0 Get the root naming context.
		//1.1 NameService invokes the name service 
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		
		//2.0 Use NamingContextExt which is part of the Interoperable
		//2.1 Naming Service (INS) specification.
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		
		try
		{
			ncRef.resolve_str(branchCobraTag);
			result = true;
		}
		catch (NotFound | InvalidName e)
		{
			result = false;
		}
			
		return result;
	}
}