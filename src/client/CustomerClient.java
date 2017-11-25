package client;


import common.BankServerInterfacePackage.invalid_bankOperation;
import common.BankServerInterfacePackage.invalid_client;
import domain.BranchID;

public class CustomerClient
{
	private static final String BANK_HOST = "localhost";
	private static final int BANK_PORT = 1099;
	private String customerID;
	private BranchID branchID;
	
	//CORBA Variables
		
	public CustomerClient(String customerID, BranchID branchID) throws invalid_client
	{
		this.customerID = customerID;
		this.branchID = branchID;
		
		System.out.println("Login Sucessed. | Customer ID: " + this.customerID + " | Branch ID: " + this.branchID.toString());	
	}
	
	public synchronized void deposit(double amount) throws invalid_client, invalid_bankOperation
	{
		
	}
	
	public synchronized void withdraw(double amount) throws invalid_client, invalid_bankOperation
	{
			
	}
	
	public synchronized void getBalance() throws invalid_client
	{
		
	}

	public static void main(String[] args)
	{

	}
}
