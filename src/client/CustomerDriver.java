package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import common.BankServerInterfacePackage.invalid_bankOperation;
import common.BankServerInterfacePackage.invalid_client;
import domain.BranchID;

public class CustomerDriver 
{

	public static void main(String[] args) throws invalid_client, invalid_bankOperation 
	{
		String customer1 = "BCCA1234";
		BranchID branch1 = BranchID.QC;

		//CustomerClient testClient1 = new CustomerClient(customer1, branch1);
			
		//testClient1.getBalance();
		//testClient1.deposit(1000);
		//testClient1.withdraw(10000);
	}
}
