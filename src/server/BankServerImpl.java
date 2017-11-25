package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.omg.CORBA.ORB;

import common.BankServerInterfacePOA;
import common.BankServerInterfacePackage.invalid_bankOperation;
import common.BankServerInterfacePackage.invalid_client;
import domain.BranchID;
import domain.Client;
import domain.EditRecordFields;
import udp.BankUDP;
import udp.BankUDPInterface;
import udp.UDPClient;
import udp.UDPServer;

public class BankServerImpl extends BankServerInterfacePOA 
{
	private static final long serialVersionUID = 1L;
	
	//Variable for each separate bank server
	private Logger logger = null;
	private BranchID branchID;
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
	
	private static final int    CLIENT_NAME_INI_POS = 3;

	//1. Each branch will have its separate server
	public BankServerImpl(BranchID branchID, ORB orb, String host, int port, HashMap<String, String> serversList)
	{
		this.branchID = branchID;
		this.UDPPort = UDPPort;
		this.clientCount = 0;
		
		this.orb = orb;
		this.UDPHost = host;
		this.UDPPort = port;
		this.serversList = serversList;
		
		this.UDPServer = new UDPServer(UDPHost, UDPPort, this);
		
		//1.1 Logging Initiation
		this.logger = this.initiateLogger();
		this.logger.info("Server Log: | BankServerImpl Server Instance Creation | Branch: " + branchID + " | Port : " + UDPPort);
				
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

	@Override
	public String[] getAllCustomerAccount()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createAccount(String firstName, String lastName, String address, String phone, String customerID,
	        common.BranchID branchID) throws invalid_client
	{
		this.logger.info("Initiating user account creation for " + firstName + " " + lastName);
		
		BranchID branchIDNew = BranchID.valueOf(branchID.toString());
		
		//If the user IS at the right branch ... we start the operation.
		if (branchIDNew == this.branchID)
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
					throw new invalid_client ("Server Error: | Account Creation Error: Account Already Exists | Customer ID: " + customerID);
				}
			}
			
			//3. If no existing account found, then we create a new account.
			Client newClient;
			
			try
			{
				newClient = new Client(firstName, lastName, address, phone, customerID, branchIDNew);
				values.add(newClient);
				clientList.put(key, values);
				
				this.logger.info("Server Log: | Account Creation Successful | Customer ID: " + customerID);
				this.logger.info(newClient.toString());
			}
			catch (Exception e)
			{
				this.logger.severe("Server Log: | Account Creation Error. | Customer ID: " + customerID + " | " + e.getMessage());
				throw new invalid_client(e.getMessage());
			}	
		}//end if clause ... if not the same branch
		else
		{
			this.logger.severe("Server Log: | Account Creation Error: BranchID Mismatch | Customer ID: " + customerID);
			throw new invalid_client("Server Error: | Account Creation Error: BranchID Mismatch | Customer ID: " + customerID);		
		}
		
		return true;
	}

	@Override
	public boolean editRecord(String customerID, common.EditRecordFields newField, String newValue)
	        throws invalid_client
	{
		EditRecordFields fieldName = EditRecordFields.valueOf(newField.toString());
		
		//1. Check if such client exist.
		String key = Character.toString((char)customerID.charAt(CLIENT_NAME_INI_POS));
		ArrayList<Client> values = clientList.get(key);
				
		for (Client client: values)
		{
			//1.1 Client Found
			if (client.getCustomerID().equals(customerID))
			{
				switch(fieldName)
				{
					case address:
						client.setAddress(newValue);
						this.logger.info("Server Log: | Edit Record Log: Address Record Modified Successful | Customer ID: " + client.getCustomerID());
						break;
						
					case phone:
						try
						{
							if(client.verifyPhoneNumber(newValue))
							{
								client.setPhoneNumber(newValue);
								this.logger.info("Server Log: | Edit Record Log: Phone Record Modified Successful | Customer ID: " + client.getCustomerID());
								break;
							}
						}
						catch (Exception e)
						{
							this.logger.severe("Server Log: | Edit Record Error: Invalid Phone Format | Customer ID: " + client.getCustomerID());
							e.printStackTrace();
							break;
						}
						
					case branch:
						try
						{
							for(BranchID enumName : BranchID.values())
							{
								if(enumName.name().equalsIgnoreCase(newValue))
								{
									client.setBranchID(enumName);
									this.logger.info("Server Log: | Edit Record Log: Branch ID Modified Successful | Customer ID: " + client.getCustomerID());
									break;
								}
								else
								{
									this.logger.severe("Server Log: | Edit Record Error: Invalid Branch ID | Customer ID: " + client.getCustomerID());
									break;
								}
							}
						}
						catch (Exception e)
						{
							this.logger.severe("Server Log: | Edit Record Error: Unknow Branch Error | Customer ID: " + client.getCustomerID());
							break;
						}					
				}//end switch statements
			}//end if clause (customer found)
			else
			{
				this.logger.severe("Server Log: | Record Edit Error: Account Not Found | Customer ID: " + customerID);
				throw new invalid_client ("Server Log: | Edit Record Error: Account Not Found | Customer ID: " + customerID);
			}
		}
		
		return false;
	}

	@Override
	public synchronized boolean deposit(String customerID, double amount) throws invalid_client, invalid_bankOperation
	{
		if (amount <= 0)
		{
			this.logger.info("Server Log: | Deposit Error: Attempted to deposit incorrect amount. | Amount: " + amount + " | Customer ID: " + customerID);
			throw new invalid_bankOperation ("Server Log: | Deposit Error: Attempted to deposit incorrect amount. | Amount: " + amount + " | Customer ID: " + customerID);
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
					client.deposit(amount);
					double newBalance = client.getBalance();
					this.logger.info("Server Log: | Deposit Log: | Deposit: " + amount + " | Balance: " + newBalance + " | Customer ID: " + customerID);
					
					return true;
				}
			}
		}
		catch (Exception e)
		{
			this.logger.severe("Server Log: | Deposit Error: | Unable to locate account. | Customer ID: " + customerID);
			throw new invalid_client("Server Log: | Deposit Error: | Unable to locate account. | Customer ID: " + customerID);
		}
		
		return false;		
	}

	@Override
	public synchronized boolean withdraw(String customerID, double amount) throws invalid_client, invalid_bankOperation
	{
		if (amount <= 0)
		{
			this.logger.info("Server Log: | Withdrawl Error: Attempted to withdraw incorrect amount. | Amount: " + amount + " | Customer ID: " + customerID);
			throw new invalid_bankOperation ("Server Log: | Withdrawl Error: Attempted to withdraw incorrect amount. | Amount: " + amount + " | Customer ID: " + customerID);
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
					double oldBalance = client.getBalance();
					double newBalance = oldBalance - amount;				
					
					if (newBalance < 0 )
					{
						this.logger.severe("Server Log: | Withdrawl Error: Attempted to withdraw more than current balance. | Amount: " 
								+ amount + " | Customer Balance: " + oldBalance + " | Customer ID: " + customerID);
						
						throw new invalid_bankOperation("Server Log: | Withdrawl Error: Attempted to withdraw more than current balance. | Amount: " 
								+ amount + " | Customer Balance: " + oldBalance + " | Customer ID: " + customerID);			
					}
					else
					{
						client.withdraw(amount);
						
						this.logger.info("Server Log: | Withdrawl Log: | Withdrawl: " + amount + " | Balance: " + newBalance + " | Customer ID: " + customerID);
						
						return true;
					}					
				}
			}
		}
		catch (Exception e)
		{
			this.logger.severe("Server Log: | Deposit Error: | Unable to locate account. | Customer ID: " + customerID);
			throw new invalid_client("Server Log: | Deposit Error: | Unable to locate account. | Customer ID: " + customerID);
		}
		
		return false;
	}

	@Override
	public synchronized double getBalance(String customerID) throws invalid_client
	{
		double newBalance = 0;
		
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
			throw new invalid_client("Server Log: | Withdrawl Error: | Unable to locate account. | Customer ID: " + customerID);
		}
		
		return newBalance;
	}

	//Account Format: QCMA1234
	//Account Format: [Branch ID][AccountType][Last Name 1st Letter][4 Digits]
	@Override
	public synchronized boolean transferFund(String sourceID, float amount, String destID) throws invalid_client, invalid_bankOperation
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
		catch (invalid_client e)
		{
			this.logger.severe("Server Log: | Fund Transfer Error: | Invalid Client : " + e.getMessage());
		}
		catch (invalid_bankOperation e)
		{
			this.logger.severe("Server Log: | Fund Transfer Error: | Invalid Bank Operation : " + e.getMessage());
		}
			
		return false;	
	}

	@Override
	public int getUDPPort()
	{
		return UDPPort;
	}

	@Override
	public String getUDPHost()
	{
		return UDPHost;
	}

	//Will destory ORB and stop UDPServer from listening requests.
	@Override
	public void shutdown()
	{
		this.orb.shutdown(false);
		this.UDPServer.stop();		
	}

	@Override
	public int getLocalAccountCount()
	{
		return getClientCount();
	}

	public BranchID getBranchID()
	{
		return branchID;
	}

	public int getClientCount()
	{
		return clientCount;
	}

	public void setOrb(ORB orb)
	{
		this.orb = orb;
	}
}