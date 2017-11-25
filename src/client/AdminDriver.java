package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import domain.BranchID;

public class AdminDriver {

	public static void main(String[] args) 
	{
		try
		{	
			String testAdmin1 = "BCMJ1234";
			BranchID branch1 = BranchID.QC;
			
			String clientF1 = "John";
			String clientL1 = "Doe";
			String address1 = "Rue Hello";
			String phone1 = "438-131-1234";
			String cid1 = "BCCA1234";
			
			AdminClient testClient1 = new AdminClient(testAdmin1, branch1);
			testClient1.createAccountRecord(clientF1, clientL1, address1, phone1, cid1, branch1);
		}
		catch (RemoteException | NotBoundException e)
		{
			e.printStackTrace();
		}

	}

}
