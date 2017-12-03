package replicas.BC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.omg.CORBA.ORB;
import dbs.corba.CallBack;
import dbs.corba.FailureFreeFEPOA;

import domain.Client;
import replicas.BC.udp.BankUDP;
import replicas.BC.udp.BankUDPInterface;
import replicas.BC.udp.UDPClient;
import replicas.BC.udp.UDPServer;;



public class BankServerImpl extends FailureFreeFEPOA implements Runnable
{
	//Variable for each separate bank server
	private Logger logger = null;
	private String branchID; //Replica name
	private String RMID;
	private int clientCount;
	private Map<String, ArrayList<Client>> clientList = new HashMap<String, ArrayList<Client>>();
	
	//CORBA Variables
	private ORB orb = null;
	private int UDPPort;
	private String UDPHost;
	
	//UDP Server for listening incoming requests
	private UDPServer UDPServer;
	
	//Holds other servers' addresses : ["ServerName", "hostName:portNumber"]
	HashMap<String, String> serversList = new HashMap();
	
	private static final int CLIENT_NAME_INI_POS = 3;
	
	public BankServerImpl(String branchID, int branchPort, String RMID)
	{
		//Replica's name
		this.branchID = branchID;
		this.UDPHost = branchID;
		this.UDPPort = branchPort;
		this.RMID = RMID;
		
		this.UDPServer = new UDPServer(branchID, branchPort, this);
		
		//1.1 Logging Initiation
		this.logger = this.initiateLogger();
		this.logger.info("Server Log: | BankServerImpl Server Instance Creation | Branch: " + branchID + " | Port : " + UDPPort);
		
		//1.2 Method to generate test accounts here.
		this.initiateAccount();
				
		System.out.println("Server Log: | BankServerImpl Server Instance Creation | Initialization Successed.");
		System.out.println("Server Log: | BankServerImpl Server Instance Creation | Branch: " + branchID + " | Port : " + UDPPort);		
	}
	
	private Logger initiateLogger()
	{
		Logger logger = Logger.getLogger("Server Logs/" + this.branchID + "- Server Log");
		FileHandler fh;
		
		try
		{
			//FileHandler Configuration and Format Configuration
			fh = new FileHandler("Server Logs/" + this.branchID + " - Server Log.log");
			
			//Disable console handling
			logger.setUseParentHandlers(false);
			logger.addHandler(fh);
			
			//Formatting configuration
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		}
		catch (SecurityException e)
		{
			System.err.println("Server Log: Error: Security Exception " + e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.println("Server Log: Error: IO Exception " + e);
			e.printStackTrace();
		}
		
		System.out.println("Server Log: | BankServerImpl Initialization | Logging Initialization Successed.");
		System.out.println("Server Log: | BankServerImpl Initialization | Server ID: " + branchID.toString() + " | Port : " + UDPPort);
		
		return logger;
	}

	
	private void initiateAccount()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String sayHello()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean createAccount(String firstName, String lastName, String address, String phone, String customerID,
	        String branchID)
	{
		this.logger.info("Initiating user account creation for " + firstName + " " + lastName);
				
		//If the user IS at the right branch ... we start the operation.
		if (this.branchID.equals(branchID))
		{
			//Each character will be a key, each key will starts with 10 buckets.
			String key = Character.toString((char)customerID.charAt(CLIENT_NAME_INI_POS));
			ArrayList<Client> values = new ArrayList<Client>(10);
			
			//If a key doesn't exist ... for example no client with last name Z ... then ...
			if(!clientList.containsKey(key))
			{
				//Adds a key and 10 buckets
				clientList.put(key, values);
			}
			
			//This is to test if the account is already exist.
			//1. Extract the buckets of the last Name ... for example, we will get all the clients with last name start with Z
			values = clientList.get(key);
			
			//2. After extracted the buckets, we loops to check if the customerID is already exist.
			for (Client client: values)
			{
				if (client.getCustomerID().equals(customerID))
				{
					this.logger.severe("Server Log: | Account Creation Error: Account Already Exists | Customer ID: " + customerID);
					
					return false;
				}
			}
			
			//3. If no existing account found, then we create a new account.
			Client newClient;
			
			try
			{
				newClient = new Client(firstName, lastName, address, phone, customerID, branchID);
				values.add(newClient);
				clientList.put(key, values);
				
				this.logger.info("Server Log: | Account Creation Successful | Customer ID: " + customerID);
				this.logger.info(newClient.toString());
				
				return true;
			}
			catch (Exception e)
			{
				this.logger.severe("Server Log: | Account Creation Error. | Customer ID: " + customerID + " | " + e.getMessage());
				
				return false;
			}	
		}//end if clause ... if not the same branch
		else
		{
			this.logger.severe("Server Log: | Account Creation Error: BranchID Mismatch | Customer ID: " + customerID);
			
			return false;
		}
	}

	@Override
	public boolean deposit(String customerID, float amount)
	{
		if (amount <= 0)
		{
			this.logger.info("Server Log: | Deposit Error: Attempted to deposit incorrect amount. | Amount: " + amount + " | Customer ID: " + customerID);
			return false;
		}
		
		//1. Verify the customerID is valid.
		try
		{
			//Maybe move the verification process to a separate method
			String key = Character.toString((char)customerID.charAt(CLIENT_NAME_INI_POS));
			ArrayList<Client> values = clientList.get(key);
			
			for (Client client : values)
			{
				if (client.getCustomerID().equals(customerID))
				{
					client.setBalance(amount);
					double newBalance = client.getBalance();
					
					this.logger.info("Server Log: | Deposit Log: | Deposit: " + amount + " | Balance: " + newBalance + " | Customer ID: " + customerID);
					
					return true;
				}
			}
		}
		catch (Exception e)
		{
			this.logger.severe("Server Log: | Deposit Error: | Unable to locate account. | Customer ID: " + customerID);
			
			return false;
		}
		
		return false;
	}

	@Override
	public boolean withdraw(String customerID, float amount)
	{
		if (amount <= 0)
		{
			this.logger.info("Server Log: | Withdrawl Error: Attempted to withdraw incorrect amount. | Amount: " + amount + " | Customer ID: " + customerID);
			System.out.println("Server Log: | Withdrawl Error: Attempted to withdraw incorrect amount. | Amount: " + amount + " | Customer ID: " + customerID);
			
			return false;
		}
		
		//1. Verify the customerID is valid.
		try
		{
			//Maybe move the verification process to a separate method
			String key = Character.toString((char)customerID.charAt(CLIENT_NAME_INI_POS));
			ArrayList<Client> values = clientList.get(key);
			
			for (Client client : values)
			{
				if (client.getCustomerID().equals(customerID))
				{
					float oldBalance = client.getBalance();
					float newBalance = oldBalance - amount;				
					
					if (newBalance < 0 )
					{
						this.logger.severe("Server Log: | Withdrawl Error: Attempted to withdraw more than current balance. | Amount: " 
								+ amount + " | Customer Balance: " + oldBalance + " | Customer ID: " + customerID);
						
						System.out.println("Server Log: | Withdrawl Error: Attempted to withdraw more than current balance. | Amount: " 
								+ amount + " | Customer Balance: " + oldBalance + " | Customer ID: " + customerID);
						
						return false;
					}
					else
					{
						client.setBalance(newBalance);
						
						this.logger.info("Server Log: | Withdrawl Log: | Withdrawl: " + amount + " | Balance: " + newBalance + " | Customer ID: " + customerID);
						System.out.println("Server Log: | Withdrawl Log: | Withdrawl: " + amount + " | Balance: " + newBalance + " | Customer ID: " + customerID);
						
						return true;
					}					
				}
			}
		}
		catch (Exception e)
		{
			this.logger.severe("Server Log: | Deposit Error: | Unable to locate account. | Customer ID: " + customerID);
			System.out.println("Server Log: | Deposit Error: | Unable to locate account. | Customer ID: " + customerID);
			
			return false;
		}
		
		return false;
	}

	@Override
	public float balance(String customerID)
	{
		float newBalance = 0;
		
		//1. Verify the customerID is valid.
		try
		{
			String key = Character.toString((char)customerID.charAt(CLIENT_NAME_INI_POS));		
			ArrayList<Client> values = clientList.get(key);
					
			for (Client client : values)
			{
				if (client.getCustomerID().equals(customerID))
				{
					newBalance = client.getBalance();
							
					this.logger.info("Server Log: | Balance Log: | Balance: " + newBalance + " | Customer ID: " + customerID);
					
					return newBalance;
				}
			}
		}
		catch (Exception e)
		{
			this.logger.severe("Server Log: | Withdrawl Error: | Unable to locate account. | Customer ID: " + customerID);
			System.out.println("Server Log: | Withdrawl Error: | Unable to locate account. | Customer ID: " + customerID);
			
			return newBalance;
		}
		
		return newBalance;
	}

	//Account ID Format: QCMA1234
	//Account ID Format: [Branch ID][AccountType][Last Name 1st Letter][4 Digits]
	@Override
	public boolean transfer(String sourceID, String destID, float amount)
	{
		String sourceBranchID = sourceID.substring(0, 2);
		String destBranchID = destID.substring(0, 2);
		
		boolean isSourceLocal = sourceBranchID.equals(branchID.toString());
		boolean isDestLocal = destBranchID.equals(branchID.toString());
		
		boolean isTransferStatus = false;
		
		try
		{
			//1. If this is a local-local transfer
			if(isSourceLocal == isDestLocal)
			{
				if(withdraw(sourceID, amount) == true)
				{
					if(deposit(destID, amount) == true)
					{
						this.logger.info("Server Log: | Transfer Fund Log: | Fund Transfer Successfully | Source Client ID: " + sourceID 
						 		                  + " | Destination Client ID: " + destID + " | Amount: $" + amount);
						
						return true;						
					}
					else
					{
						//We can't deposit for some reason. Deposit back the amount to source.
						deposit(sourceID, amount);
						
						return false;
					}
				}						
			}
			//2. If this is a local-foreign transfer
			else if(isSourceLocal == true && isDestLocal == false)
			{
				//2.1 Make sure the local client has enough fund.
				if(withdraw(sourceID, amount) == true)
				{
					//3. Loop through the serversList to find the information of the remote server
					for(String remoteBranchID : serversList.keySet())
					{											
						if(destBranchID.equals(remoteBranchID))
						{
							this.logger.info("Server Log: | Transfer Fund Log: | Connection Initialized.");
							
							//3.1 Extract the key that is associated with the destination branch.
							String connectionData = serversList.get(destBranchID);
							
							//3.2 Extract the host and IP [host:IP]
							String hostDest = connectionData.split(":")[0];
							int portDest = Integer.parseInt(connectionData.split(":")[1]);
							
							//3.3 Create an UDPClient and prepare the request.
							UDPClient requestClient = new UDPClient(hostDest, portDest, branchID);
							
							BankUDPInterface transferReq = new BankUDP(sourceID, destID, amount);
							requestClient.send(transferReq);
							
							//3.4 Receive the response.
							BankUDPInterface transferResp = requestClient.getResponse();
							
							//3.5 IF successfully transfer ...
							if(((BankUDP)transferResp).isTransferStatus() == true)
							{
								this.logger.info("Server Log: | Transfer Fund Log: | Fund Transfer Successfully | Source Client ID: " + sourceID 
				 		                  + " | Destination Client ID: " + destID + " | Amount: $" + amount);
								
								isTransferStatus = true;
								
								return true;
							}
						}	
					}//end for-loop:remoteBranchID
					
					if (isTransferStatus == false)
					{
						deposit(sourceID, amount);
						
						return false;
					}
				}	
			}//ends local-dest
			else if(isSourceLocal == false && isDestLocal == true)
			{
				//This is the case for incoming transfer.
				isTransferStatus = deposit(destID, amount);
				
				//If we can successfully deposit ...
				if(isTransferStatus == true)
				{
					this.logger.info("Server Log: | Transfer Fund Log: | Fund Transfer Successfully | Source Client ID: " + sourceID 
			                  + " | Destination Client ID: " + destID + " | Amount: $" + amount);
					
					return true;		
				}
				else
				{
					return false;
				}					
			}	
		}//end try-clause
		catch (Exception e)
		{
			this.logger.severe("Replica.BC.BankServerImpl Log: | Fund Transfer Error: | Invalid Client : " + e.getMessage());
			System.out.println("Replica.BC.BankServerImpl Log: | Fund Transfer Error: | Invalid Client : " + e.getMessage());
			
			return false;
		}
			
		return false;
	}



	@Override
	public String requestResponse(CallBack cb)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setByzantineFlag(boolean flag)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown()
	{
		this.UDPServer.stop();	
	}
	
	@Override
	public void run()
	{
		// TODO Auto-generated method stub		
	}
	
	public int getClientCount()
	{
		return clientCount;
	}

	@Override
	public int getLocalAccountCount()
	{
		return getClientCount();
	}

	public String getBranchID()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getUDPPort()
	{
		return UDPPort;
	}

	public String getUDPHost()
	{
		return UDPHost;
	}

	public String getRMID()
	{
		return RMID;
	}
}
