package domain;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

public class Client 
{
	private String firstName;
	private String lastName;
	private String address;
	private String phoneNumber;
	private String customerID;
	
	private String branchID;
	private float balance;
	private String filename;
	private Logger logger = null;
	
	private static final int ACCOUNT_TYPE_POS = 2;
	private static Pattern ACCOUNT_NUMER_PATTERN = java.util.regex.Pattern.compile("^(BC|MB|NB|QC)(C|M)[a-zA-Z](\\d{4})$");
	private static Pattern PHONE_PATTERN = java.util.regex.Pattern.compile("^\\d{3}-\\d{3}-\\d{4}$");
	
	public Client(String firstName, String lastName, String address, String phoneNumber, String customerID, String branchID) 
			throws Exception
	{
		//If pass verification test ...
		if(verify(firstName, lastName, phoneNumber, customerID))
		{
			this.firstName = firstName;
			this.lastName = lastName;
			this.address = address;
			this.phoneNumber = phoneNumber;
			this.customerID = customerID;
			this.branchID = branchID;
			
			this.balance = 0;
		}
		
		char accountType = Character.toUpperCase(customerID.charAt(ACCOUNT_TYPE_POS));
		
		this.logger = logFile(accountType);
		this.logger.info("Client " + customerID + " created successfully.");
		
		System.out.println("Account created successed.");
		
	}

	private Logger logFile(char accountType) throws Exception
	{
		if(accountType == 'C')
		{
			System.out.println("Account Type : " + accountType + " | Branch ID: " + this.branchID + " | Account Number: " + this.customerID);
			this.filename = "Clients Logs/" + this.branchID + " - " + this.customerID;
			System.out.println("File Path: " + this.filename);
		}
		else if (accountType == 'M')
		{
			System.out.println("Account type is M.");
			this.filename = "managers/" + this.branchID + " - " + this.customerID; 		
		}
		else
		{
			throw new Exception ("Error: Account mismatch.");
		}
		
		Logger logger = Logger.getLogger(this.filename);
		FileHandler fh;
		
		try
		{
			fh = new FileHandler(this.filename + ".log");
			
			logger.setUseParentHandlers(false);
			logger.addHandler(fh);
			
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		}
		catch (SecurityException e)
		{
			System.err.println("Logging Error: Security Exception "+ e);
		}
		catch (IOException e)
		{
			System.err.println("Logging Error: IO Exception " + e);
		}
		
		System.out.println("Client Log: Logger Initialization Success.");
		
		return logger;
			
	}

	private boolean verify(String firstName, String lastName, String phoneNumber, String customerID) throws Exception
	{	
		if (firstName.isEmpty())
		{
			throw new Exception ("Error: Client missing first name.");
		}
		
		if (lastName.isEmpty())
		{
			throw new Exception ("Error: Client missing last name.");
		}
		
		if (!ACCOUNT_NUMER_PATTERN.matcher(customerID).matches())
		{
			throw new Exception ("Error: Client account number format error.");
		}
		
		boolean result = verifyPhoneNumber(phoneNumber);
		
		return result;
	}
	
	private boolean verifyPhoneNumber (String phoneNumber) throws Exception
	{
		if (!PHONE_PATTERN.matcher(phoneNumber).matches())
		{
			throw new Exception ("Error: Client phone number format error.");
		}
		
		return true;
	}
	
	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public String getBranchID()
	{
		return branchID;
	}

	public void setBranchID(String branchID)
	{
		this.branchID = branchID;
	}
	
	public String getCustomerID()
	{
		return customerID;
	}
	
	public float getBalance()
	{
		return balance;
	}
	
	public void setBalance(float balance)
	{
		this.balance = balance;
	}

	@Override
	public String toString()
	{
		return "Client [firstName=" + firstName + ", lastName=" + lastName + ", address=" + address + ", phoneNumber="
		        + phoneNumber + ", accountNumber=" + customerID + ", branchID=" + branchID + ", balance=" + balance
		        + "]";
	}
	
}