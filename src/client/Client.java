package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;

import dbs.corba.FailureFreeFE;
import dbs.corba.FailureFreeFEHelper;

public class Client {

	private ORB clientORB;
	private NamingContext directory;
	
	public Client(String[] args) throws InvalidName {
		Properties p = System.getProperties();
		clientORB = ORB.init(args, p);
		org.omg.CORBA.Object obj = clientORB.resolve_initial_references( "NameService" ) ;

		directory = NamingContextHelper.narrow(obj) ;
	}
	
	public void getMenu() throws IOException {
		
		while(true) {
			InputStreamReader in = new InputStreamReader ( System.in ) ;
			BufferedReader r = new BufferedReader ( in ) ;
			
			/**
			 * deposit(in long long accountNum,in float amount);
			double withdraw(in long long accountNum,in float amount);
			double balance(in long long accountNum);
			double transfer
			 */
			System.out.println( "Enter an operation" );
			System.out.println("1. Create Account" ); 
			System.out.println("2. Deposit");
			System.out.println("3. Withdraw") ;
			System.out.println("4. Check balance") ;
			System.out.println("5. Transfer") ;
			System.out.println("6. Exit") ;

			int choice = Integer.parseInt(r.readLine()) ;
			if ( choice  == 1 ) {
				createAccount () ;
			} else if ( choice == 2 ) {
				deposit() ;
			} else if ( choice == 3 ) {
				withdraw() ;
			} else if ( choice == 4 ) {
				balance();
			} else if ( choice == 5 ) {
				transfer();
			} else if ( choice == 6 ) {
				break ;
			}else {
				System.out.println( "You have entered a wrong choice." ) ;
				continue ;
			}

			System.out.println("Press any key to continue:...");
			r.readLine() ;
		}
	}


	private void createAccount() {
		InputStreamReader in = new InputStreamReader ( System.in ) ;
		BufferedReader r = new BufferedReader ( in ) ;
		
		System.out.print("Enter the first name: ");
		String fName = r.readLine () ;
		
		System.out.print("Enter the last name: ");
		String lName = r.readLine () ;
		
		System.out.print("Enter the phone number: ");
		String phoneNumber = r.readLine () ;
		
		System.out.print("Enter the username: ");
		String userName = r.readLine () ;
		
		System.out.print("Enter the password: ");
		String password = r.readLine () ;
		
		System.out.print("Enter the email address: ");
		String email = r.readLine () ;
		
		System.out.print("Enter the educational institue: ");
		String educationalInstitute = r.readLine () ;

		FailureFreeFE lb = getRemoteObject ( educationalInstitute ) ;

		if ( lb == null ) {
			return ;
//			System.out.println( "Sorry! the demanded service can't be provided at the moment" );
//			System.out.println("Please try again later" );
		}

		try {
			boolean result ;
			result = lb.createAccount(fName, lName, email, phoneNumber, userName, password, educationalInstitute);
			System.out.println ( result ) ;
		} catch ( Exception e ) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	
	public void deposit() throws IOException {
		InputStreamReader in = new InputStreamReader ( System.in ) ;
		BufferedReader r = new BufferedReader ( in ) ;

		System.out.print("Enter the accountNumber: ");
		String accountNumber = r.readLine () ;

		System.out.print("Enter the password: ");
		String password = r.readLine () ;

		System.out.print("Enter the Bank branch initials: ");
		String branchName = r.readLine () ;

		System.out.print("Enter the deposit amount: ");
		String amount = r.readLine () ;
		
		FailureFreeFE feInterface = getRemoteObject ( branchName ) ;

		if ( feInterface == null ) {
			return ;
//			System.out.println( "Sorry! the demanded service can't be provided at the moment" );
//			System.out.println("Please try again later" );
		}

		try {
			double result ;
//			result = feInterface..reserveInterLibrary(userName, password, bookName, amount) ;
			result = feInterface.deposit(Integer.parseInt(accountNumber), Integer.parseInt(amount));
			System.out.println ( "You deposited: " + result ) ;
		} catch ( Exception e ) {
			System.out.println("Exception: " + e.getMessage() );
		}
	}
	
	
	
	private void withdraw() throws IOException {
		InputStreamReader in = new InputStreamReader ( System.in ) ;
		BufferedReader r = new BufferedReader ( in ) ;

		System.out.print("Enter the accountNumber: ");
		String accountNumber = r.readLine () ;

		System.out.print("Enter the password: ");
		String password = r.readLine () ;

		System.out.print("Enter the Bank branch initials: ");
		String branchName = r.readLine () ;

		System.out.print("Enter the deposit amount: ");
		String amount = r.readLine () ;
		
		FailureFreeFE feInterface = getRemoteObject ( branchName ) ;

		if ( feInterface == null ) {
			return ;
//			System.out.println( "Sorry! the demanded service can't be provided at the moment" );
//			System.out.println("Please try again later" );
		}

		try {
			double result ;
			result = feInterface.withdraw(Integer.parseInt(accountNumber), Integer.parseInt(amount));
			System.out.println ( "You withdrawed: " + result ) ;
		} catch ( Exception e ) {
			System.out.println("Exception: " + e.getMessage() );
		}
	}
	
	private void balance() throws IOException {
		InputStreamReader in = new InputStreamReader ( System.in ) ;
		BufferedReader r = new BufferedReader ( in ) ;

		System.out.print("Enter the accountNumber: ");
		String accountNumber = r.readLine () ;

		System.out.print("Enter the password: ");
		String password = r.readLine () ;

		System.out.print("Enter the Bank branch initials: ");
		String branchName = r.readLine () ;

		FailureFreeFE feInterface = getRemoteObject ( branchName ) ;

		if ( feInterface == null ) {
			return ;
//			System.out.println( "Sorry! the demanded service can't be provided at the moment" );
//			System.out.println("Please try again later" );
		}

		try {
			double result ;
			result = feInterface.balance(Integer.parseInt(accountNumber));
			System.out.println ( "Your balance: " + result ) ;
		} catch ( Exception e ) {
			System.out.println("Exception: " + e.getMessage() );
		}
	}
	
	private void transfer() throws IOException {
		InputStreamReader in = new InputStreamReader ( System.in ) ;
		BufferedReader r = new BufferedReader ( in ) ;

		System.out.print("Enter your account number: ");
		String accountNumber = r.readLine () ;

		System.out.print("Enter the password: ");
		String password = r.readLine () ;

		System.out.print("Enter the Bank branch initials: ");
		String branchName = r.readLine () ;
		
		System.out.print("Enter the destination account number: ");
		String dest_acc_num = r.readLine () ;
		
		System.out.print("Enter the deposit amount: ");
		String amount = r.readLine () ;

		FailureFreeFE feInterface = getRemoteObject ( branchName ) ;

		if ( feInterface == null ) {
			return ;
		}

		try {
			double result ;
			result = feInterface.transfer(Integer.parseInt(accountNumber), Integer.parseInt(dest_acc_num), Integer.parseInt(amount));
			System.out.println ( "You transferred: " + result + " to account: " + dest_acc_num) ;
		} catch ( Exception e ) {
			System.out.println("Exception: " + e.getMessage() );
		}
	}
	
	
	
	
	public FailureFreeFE getRemoteObject(String branchName ) {

		NameComponent[] name = new NameComponent[1] ;
		name[0] = new NameComponent () ;
		name[0].id = branchName ;
		name[0].kind = "replica" ;
		FailureFreeFE feInterface = null ;

		try {
			org.omg.CORBA.Object obj = directory.resolve(name) ;
			feInterface = FailureFreeFEHelper.narrow(obj) ;
		} catch ( org.omg.CosNaming.NamingContextPackage.NotFound e ) {
			System.out.println ( "The requested binding is not present in the directory" ) ;
		} catch ( org.omg.CosNaming.NamingContextPackage.CannotProceed e ) {
			System.out.println ( "Can't proced due to some implementation error: " + e.getMessage() ) ;
		} catch ( org.omg.CosNaming.NamingContextPackage.InvalidName e ) {
			System.out.println ( "You have entered an invalid library name" ) ;
		}

		return feInterface ;
	}
	
	
	
	
	
	
}
