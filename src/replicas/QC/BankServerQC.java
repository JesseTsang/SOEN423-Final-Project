package replicas.QC;

import dbs.corba.CallBack;
import dbs.corba.FailureFreeFEPOA;

public class BankServerQC extends FailureFreeFEPOA implements Runnable
{

	@Override
	public String sayHello()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deposit(String accountNum, float amount)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean withdraw(String accountNum, float amount)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float balance(String accountNum)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean transfer(String src_accountNum, String dest_accountNum, float amount)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String requestResponse(CallBack cb)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean createAccount(String firstName, String lastName, String address, String phone, String customerID,
	        String branchID)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLocalAccountCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setByzantineFlag(boolean flag)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}

}
