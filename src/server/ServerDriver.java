package server;

import java.util.HashMap;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import common.BankServerInterface;
import common.BankServerInterfaceHelper;
import domain.BranchID;
import domain.EditRecordFields;

public class ServerDriver 
{
	public static void main(String[] args) 
	{	
		HashMap<String, String> serverDetails = new HashMap();
		HashMap<String, BankServerImpl> serverDirectory = new HashMap();
		
		String[] serverList = {"BC", "MB", "NB", "QC"};
			
		try
		{
			Properties properties = new Properties();
			
			properties.put("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
			properties.put("org.omg.CORBA.ORBInitialPort", "1050");
			
			//1. Create and initialize ORB
			ORB orb = ORB.init(args, properties);
			
			//2. Get reference to RootPOA and activate the POAManager
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootPOA.the_POAManager().activate();
			
			//3.0 Get the root naming context.
			//3.1 NameService invokes the name service 
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			//4.0 Use NamingContextExt which is part of the Interoperable
			//4.1 Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			
			//5. Create servers details
			serverDetails.put("BC", "localhost:30100");
			serverDetails.put("MB", "localhost:30200");
			serverDetails.put("NB", "localhost:30300");
			serverDetails.put("QC", "localhost:30400");
			
			//6.0 Put the servers details in a HashMap
			//6.1 Create the servers
			BankServerImpl serverBC = new BankServerImpl(BranchID.BC, orb, "localhost", 30100, serverDetails);
			BankServerImpl serverMB = new BankServerImpl(BranchID.MB, orb, "localhost", 30200, serverDetails);
			BankServerImpl serverNB = new BankServerImpl(BranchID.NB, orb, "localhost", 30300, serverDetails);
			BankServerImpl serverQC = new BankServerImpl(BranchID.QC, orb, "localhost", 30400, serverDetails);
			
			serverDirectory.put("BC", serverBC);
			serverDirectory.put("MB", serverMB);
			serverDirectory.put("NB", serverNB);
			serverDirectory.put("QC", serverQC);
			
			for(String serverName : serverList)
			{
				//7. Get object reference from the servant
				org.omg.CORBA.Object ref = rootPOA.servant_to_reference(serverDirectory.get(serverName));
				
				//8. Cast the reference to a CORBA reference
				BankServerInterface href = BankServerInterfaceHelper.narrow(ref);
				
				//9. Bind the Object Reference in Naming
				NameComponent path[] = ncRef.to_name(serverName);
				ncRef.rebind(path, href);
				
				System.out.println("ServerDriver Log: | Bank Server: " + serverName + " initialized." );
			}
			
			//10. Wait for invocation from clients
			orb.run();	
		}
		catch (Exception e)
		{
			System.err.println("ServerDriver Log: | Error: " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
}
