import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;

import dbs.corba.FailureFreeFE;
import dbs.corba.FailureFreeFEHelper;

import org.omg.CosNaming.*;

//import DRMSServices.*;

import java.util.* ;
import java.net.* ;
import java.io.* ;

/**
 * This is FrontEnd main class. It creates FrontEnd objects and accepts client requests
 *
 * In CMD console, command: start orbd -ORBInitialPort 1050
 * Under Eclipse, Run -> Run Configurations..., make sure Project and Main class is correct
 * Arguments -> Program arguments: -ORBInitialPort 1050 -ORBInitialHost localhost
 *
 */

public class FrontEndMain {

	public static void main ( String[] args ) {

		// Get the system properties
		Properties orbProp = System.getProperties() ;

		// Initialize ORB
		ORB orb = ORB.init(args,orbProp);

		// The main portable object adapter
		// As we are creating only three servers the Root POA is sufficient for our purpose
		POA rootPOA = null ;
		org.omg.CORBA.Object obj = null ; // generic CORBA object
		org.omg.CORBA.Object obj1 = null ; // generic CORBA object

		NamingContext namingContext = null ;	

		try {
			// Get the reference to RootPOA and NameService
			obj = orb.resolve_initial_references( "RootPOA" ) ;
			rootPOA = POAHelper.narrow(obj);
//System.out.println(rootPOA);
System.out.println(orb.resolve_initial_references("NameService"));

			obj = orb.resolve_initial_references( "NameService" ) ;
			namingContext = NamingContextHelper.narrow(obj) ;	
		} catch ( org.omg.CORBA.ORBPackage.InvalidName e ) {
			System.out.println (e.getMessage() ) ;
		}		
		

		//Feature selection and setting 
		while ( true ) {
			try {

				InputStreamReader in = new InputStreamReader ( System.in ) ;
				BufferedReader r = new BufferedReader ( in ) ;

				System.out.println ( "Please select a property feature of the DBS system" ) ;
				System.out.println ( "1. Fault Tolerance" ) ;
				System.out.println ( "2. High Availibility" ) ;

				int choice = Integer.parseInt(r.readLine()) ;

				if ( choice == 1 ) {
					System.out.println ( "The System with Fault Tolerance is ready to accept client requests" ) ;
					FrontEnd.setSystemProperty("Fault Tolerance");
					break;
				} else if ( choice == 2 ) {
					System.out.println ( "The System with Highly Availability is ready to accept client requests" ) ;
					FrontEnd.setSystemProperty( "High Availability" );
					break;
				} else {
					System.out.println( "You have entered a wrong choice" );
					System.out.println( "Press any key to continue: " ) ;
					r.readLine();
				}
			} catch ( IOException e ) {
				System.out.println( e.getMessage() ) ;
			}	catch ( NumberFormatException e ) {
				System.out.println( "You have entered a wrong choice. Please try again" ) ;
				continue ;
			} 	
		}


		FrontEnd[] replicas = new FrontEnd[3];

		InetSocketAddress sequencerAddress = new InetSocketAddress ( 9988 ) ;

		replicas[0] = new FrontEnd ( "BC", sequencerAddress) ;
		replicas[1] = new FrontEnd ( "MB", sequencerAddress ) ;
		replicas[2] = new FrontEnd ( "QC", sequencerAddress) ;

		try {
			
			//simulate replica managers on different ports of local host
			FrontEnd.addReplicaManager("rm1", InetAddress.getLocalHost(), 10101);
			FrontEnd.addReplicaManager("rm2", InetAddress.getLocalHost(), 10102);
			FrontEnd.addReplicaManager("rm3", InetAddress.getLocalHost(), 10103);	
		} catch ( UnknownHostException e ) {
			System.out.println ( e.getMessage() ) ;
		}
		
		NameComponent[] name = new NameComponent[1] ;
		name[0] = new NameComponent() ;
		FailureFreeFE[] feInterface = new FailureFreeFE[3] ;

		try {
			
			for ( int i = 0; i != replicas.length; i++ ) {
				
				// Activate the Replica object and associate it with rootPOA
				byte[] id = rootPOA.activate_object(replicas[i]) ;
				
				/*
				 *  Get a reference to the replica object in the form of CORBA reference
				 *	and put it in the interface array
				 */
				org.omg.CORBA.Object tempOBJ = rootPOA.id_to_reference(id) ;
				feInterface[i] = FailureFreeFEHelper.narrow(tempOBJ) ;
			
				// bind the CORBA reference of replica object to NameService
				name[0].id = replicas[i].getName() ;
				name[0].kind = "replica" ;
				namingContext.rebind(name, feInterface[i]);
				
			}	
		} catch ( org.omg.CORBA.UserException e) {
			System.out.println ( e.getMessage() ) ;
		} 

		try {
			// Activate POA manager
			rootPOA.the_POAManager().activate();
		} catch (AdapterInactive e) {
			System.out.println ( e.getMessage() ) ;
		}

		// run the underlying CORBA ORB. It is now ready to receive requests from clients
		orb.run();

		System.out.println("The following " +  replicas.length + " replicas are currently part of the DBS");

		for ( FrontEnd l : replicas ) {
			System.out.println ( l.getName() ) ;
		}

	}

}