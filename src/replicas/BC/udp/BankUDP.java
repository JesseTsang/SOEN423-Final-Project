package replicas.BC.udp;



import java.util.Properties;

import org.omg.CORBA.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import dbs.corba.FailureFreeFE;
import dbs.corba.FailureFreeFEHelper;
import dbs.corba.FailureFreeFEPOA;

public class BankUDP implements BankUDPInterface
{
	private static final long serialVersionUID = 1L;
	
	private String clientIDSource;
	private String clientIDDest;
	private float amount;
	

	private String operationType;
	private boolean transferStatus = false;
	private int totalClientsCount;
	
	//Constructor for inter-banks fund transfer
	public BankUDP(String clientIDSource, String clientIDDest, float amount)
	{
		this.clientIDSource = clientIDSource;
		this.clientIDDest 	= clientIDDest;
		this.amount 		= amount;
		
		this.operationType = "fundTransfer";
	}
	
	//Constructor for get total client numbers.
	public BankUDP()
	{
		this.operationType = "getTotalClients";
	}
	
	public boolean isTransferStatus()
	{
		return transferStatus;
	}

	public int getTotalClientsCount()
	{
		return totalClientsCount;
	}
		
	@Override
	public void execute(FailureFreeFEPOA server, String branchID)
	{
		Properties sysProperties = System.getProperties();
		
		sysProperties.setProperty("org.omg.CORBA.ORBClass", "com.sun.corba.se.internal.POA.POAORB");
		sysProperties.setProperty("org.omg.CORBA.ORBSingletonClass", "com.sun.corba.se.internal.corba.ORBSingleton");
		
		sysProperties.put("org.omg.CORBA.ORBInitialHost", "localhost");
		sysProperties.put("org.omg.CORBA.ORBInitialPort", "1050");
		
		ORB orb = ORB.init(new String[1], sysProperties);
		
		try
		{
			org.omg.CORBA.Object objNS = orb.resolve_initial_references("NameService");
			NamingContextExt namingContext = NamingContextExtHelper.narrow(objNS);
			
			org.omg.CORBA.Object objBranch = namingContext.resolve_str(branchID.toString());
			FailureFreeFE bankServer = FailureFreeFEHelper.narrow(objBranch);
			
			if(this.operationType.equals("fundTransfer"))
			{
				transferStatus = bankServer.transfer(clientIDSource, clientIDDest, amount);	
			}
			else if(this.operationType.equals("getTotalClients"))
			{
				totalClientsCount = bankServer.getLocalAccountCount();
			}
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}		
	}
}
