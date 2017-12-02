package replicas.MB;

import dbs.corba.CallBack;
import dbs.corba.FailureFreeFEPOA;

public class BankServerMB extends FailureFreeFEPOA implements Runnable
{

	@Override
	public String sayHello()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double deposit(long accountNum, float amount)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double withdraw(long accountNum, float amount)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double balance(long accountNum)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double transfer(long src_accountNum, long dest_accountNum, float amount)
	{
		// TODO Auto-generated method stub
		return 0;
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
	public void run()
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
	public void setByzantineFlag(boolean flag)
	{
		// TODO Auto-generated method stub
		
	}

}
