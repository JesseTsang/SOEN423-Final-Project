import dbs.corba.CallBack;
import dbs.corba.FailureFreeFEPOA;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import org.omg.CORBA.ORB;

import com.dbs.response.NumericResponse;
import com.dbs.response.RequestWrapper;

import dbs.corba.CallBack;
import dbs.corba.FailureFreeFEPOA;



public class FrontEnd extends FailureFreeFEPOA{

	// Each frontEnd has a map for all RMs
	private static HashMap<String,InetSocketAddress> replicaManagerDatabase = new HashMap<String, InetSocketAddress>();
	
	
	// The address of the sequencer that FrontEnd will forward client requests to
	private InetSocketAddress sequencerAddress;
	
	// The branch name that this FrontEnd object is for
	private String replicaName;

	//The system property of the system. Options: "Fault Tolerance" and "High Availability"
	private static String systemProperty = null;
	
	public static String getSystemProperty() {
		return systemProperty;
	}
	public static void setSystemProperty(String systemProperty) {
		FrontEnd.systemProperty = systemProperty;
	}
	
	// Constructor
	public FrontEnd(String name, InetSocketAddress add) {
		replicaName = name;
		sequencerAddress = add;
	}
	
	// Add a new Replica Manager to FrontEnd class
	public static void addReplicaManager ( String replicaManagerName, InetAddress ipAddress, int portNo ) {
		replicaManagerDatabase.put( replicaManagerName, new InetSocketAddress(ipAddress, portNo)) ;
	}
	
	public String getName() {
		return replicaName;
	}
	
	
	// Calculate the majority result from the obtained results
	private double getMajority (ArrayList<NumericResponse> response) {
		
		/*
		 *  If no branch responds, then there is a process crash, then the obtained results
		 *  are assumed correct, hence, just pass the result without calculating the majority;
		 */
		System.out.println("Response size " + response.size());
		System.out.println("Responses are: ");
		
		for(NumericResponse b: response) {
			System.out.println("Replica: "  + b.getReplicaName());
			System.out.println("Response: " + b.getResult());
		}
		
		// Case 1: only some replicas responded, only return the first response
		if(response.size() != replicaManagerDatabase.size() && response.size() != 0) {
			return response.get(0).getResult();
		}
		
		// Case 2: all replicas responded
		double majorityResult = 0;
		int counter = 0;
		for(NumericResponse r: response) {
			if(counter == 0) {
				majorityResult = r.getResult();
				counter ++;
			}else if(majorityResult == r.getResult()) {
				counter ++;
			}else {
				counter--;
			}
			
		}
		
		if(counter == replicaManagerDatabase.size()) {
			return majorityResult;
		}
		
		for(NumericResponse r: response) {
			if(r.getResult() != majorityResult) {
				notifySoftwareBug(r.getReplicaName(),replicaName);
				return majorityResult;
			}
		}
		return -1;
	}
	
	
	// Send UDP message to replica manager for a software bug
	// Target assigned by parameter: replicaName
	private void notifySoftwareBug(String replicaName, String branchName) {
		DatagramSocket socket = null;
		
		try {
			socket = new DatagramSocket();
			
			String data = "FAILURE:" + replicaName + ":" + branchName;
			byte[] sendBuffer = data.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
					replicaManagerDatabase.get(replicaName).getAddress(), replicaManagerDatabase.get(replicaName).getPort()); 
			socket.send(sendPacket);
			
		}catch(Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * In system of High Availability, it is assumed that there is no software bug.
	 * So the FrontEnd awaits till it gets at least one response from any branch and then forwards it to the client
	 */
	private double receiveFirstReply ( DatagramSocket socket ) throws SocketException, IOException {
		
		NumericResponse result ;
		byte[] receiveBuffer = new byte[512] ;
		DatagramPacket receivePacket = new DatagramPacket ( receiveBuffer, receiveBuffer.length ) ;

		// Receive response
		socket.receive(receivePacket);

		// Deserialize it
		ByteArrayInputStream bs = new ByteArrayInputStream ( receivePacket.getData() ) ;

		ObjectInputStream is = new ObjectInputStream ( bs ) ;

		try {
			result = ( NumericResponse ) is.readObject() ;
			return result.getResult() ;			
		} catch ( ClassNotFoundException e ) {
			System.out.println ( e.getMessage() ) ;
			// TODO Handle Exception properly
			return 0;
		}

	}
	
	
	
	/**
	 * In the case of Fault Tolerance system, the FrontEnd waits for a reasonable time to obtain response from all the replicas.
	 * @return - ArrayList of all the responses obtained before timeout
	 */
	private ArrayList<NumericResponse> receiveReply ( DatagramSocket socket ) throws SocketException, IOException {

		ArrayList<NumericResponse> result = new ArrayList<NumericResponse> () ;
		
		// Set the time out. FrontEnd will await responses form replicas until this much time.
		socket.setSoTimeout(1000);

		try {
			while ( true ) {
				byte[] receiveBuffer = new byte[512] ;

				DatagramPacket receivePacket = new DatagramPacket ( receiveBuffer, receiveBuffer.length ) ;

				// Receive Response
				socket.receive(receivePacket);
				
				// Deserialize it
				ByteArrayInputStream bs = new ByteArrayInputStream ( receivePacket.getData() ) ;
				ObjectInputStream is = new ObjectInputStream ( bs ) ;

				try {
					NumericResponse res = ( NumericResponse ) is.readObject() ;
					result.add(res) ;		// add to results
				} catch (ClassNotFoundException e) {
					System.out.println ( e.getMessage() ) ;
				}
			}

		} catch (SocketTimeoutException e ) {   // This exception happens when the time out will happen
			return result ;		// Return the results on timeout
		}
	}

	/**
	 * Forward the client request to sequencer
	 */
	private void sendRequest ( RequestWrapper request, DatagramSocket socket ) throws IOException {
		
		// Serialize the object
		ByteArrayOutputStream bs = new ByteArrayOutputStream () ;
		ObjectOutputStream os = new ObjectOutputStream ( bs ) ;
		os.writeObject(request);
		os.close() ;
		bs.close();
		
		// Send it to the sequencer
		byte[] sendBuffer = bs.toByteArray() ;
		DatagramPacket sendPacket = new DatagramPacket ( sendBuffer, sendBuffer.length, 
				sequencerAddress.getAddress(), sequencerAddress.getPort() ) ;
		socket.send(sendPacket);
		System.out.println ( "Sent: " + request.getClass().getSimpleName()) ;
	}



	@Override
	public String sayHello() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double deposit(long accountNum, float amount) {

		DatagramSocket socket = null ;

		try {
			socket = new DatagramSocket() ;

			// Create a RequestWrapper object
			// RequestWrapper(InetAddress ip, int port, String requestName, long accountNumber, float amount)
			RequestWrapper request = new RequestWrapper(socket.getLocalAddress(), socket.getLocalPort(),"deposit", accountNum, amount);
			
			System.out.println ( socket.getLocalPort()) ;

			// Send the call object in serialized form to the sequencer
			sendRequest ( request, socket ) ;			
			
			// If High Availability is required then only wait for the first reply
			if ( FrontEnd.getSystemProperty().equalsIgnoreCase("High Availability") ) {
				return receiveFirstReply ( socket ) ;
			}

			// Else obtain all the replies
			ArrayList<NumericResponse> response  = receiveReply ( socket ) ;

			// Calculate majority and inform replica manager if there is a software bug
			double result = getMajority ( response ) ;
			return result ;
			
		} catch ( IOException e ) {
			System.out.println( e.getMessage () ); 
			return 0 ;
		} 
	}

	@Override
	public double withdraw(long accountNum, float amount) {
		DatagramSocket socket = null ;

		try {
			socket = new DatagramSocket() ;

			// Create a RequestWrapper object
			// RequestWrapper(InetAddress ip, int port, String requestName, long accountNumber, float amount)
			RequestWrapper request = new RequestWrapper(socket.getLocalAddress(), socket.getLocalPort(),"withdraw", accountNum, amount);
			
			System.out.println ( socket.getLocalPort()) ;

			// Send the call object in serialized form to the sequencer
			sendRequest ( request, socket ) ;			
			
			// If High Availability is required then only wait for the first reply
			if ( FrontEnd.getSystemProperty().equalsIgnoreCase("High Availability") ) {
				return receiveFirstReply ( socket ) ;
			}

			// Else obtain all the replies
			ArrayList<NumericResponse> response  = receiveReply ( socket ) ;

			// Calculate majority and inform replica manager if there is a software bug
			double result = getMajority ( response ) ;
			return result ;
			
		} catch ( IOException e ) {
			System.out.println( e.getMessage () ); 
			return 0 ;
		} 
	
	}

	public double balance(long accountNum) {
		// Create a new Socket
		DatagramSocket socket = null ;

		try {
			socket = new DatagramSocket() ;

			// Create a RequestWrapper object
			// RequestWrapper(InetAddress ip, int port, String requestName, long accountNumber, float amount)
			RequestWrapper request = new RequestWrapper(socket.getLocalAddress(), socket.getLocalPort(),"balance", accountNum);
			
			System.out.println ( socket.getLocalPort()) ;

			// Send the call object in serialized form to the sequencer
			sendRequest ( request, socket ) ;			
			
			// If High Availability is required then only wait for the first reply
			if ( FrontEnd.getSystemProperty().equalsIgnoreCase("High Availability") ) {
				return receiveFirstReply ( socket ) ;
			}

			// Else obtain all the replies
			ArrayList<NumericResponse> response  = receiveReply ( socket ) ;

			// Calculate majority and inform replica manager if there is a software bug
			double result = getMajority ( response ) ;
			return result ;
			
		} catch ( IOException e ) {
			System.out.println( e.getMessage () ); 
			return 0 ;
		} 
		
	}

	@Override
	public double transfer(long src_accountNum, long dest_accountNum, float amount) {
		DatagramSocket socket = null ;

		try {
			socket = new DatagramSocket() ;

			// Create a RequestWrapper object
			// RequestWrapper(InetAddress ip, int port, String requestName, long accountNumber, long targetAccountNumber,float amount)
			RequestWrapper request = new RequestWrapper(socket.getLocalAddress(), socket.getLocalPort(),"transfer", 
					src_accountNum, dest_accountNum, amount);
			
			System.out.println ( socket.getLocalPort()) ;

			// Send the call object in serialized form to the sequencer
			sendRequest ( request, socket ) ;			
			
			// If High Availability is required then only wait for the first reply
			if ( FrontEnd.getSystemProperty().equalsIgnoreCase("High Availability") ) {
				return receiveFirstReply ( socket ) ;
			}

			// Else obtain all the replies
			ArrayList<NumericResponse> response  = receiveReply ( socket ) ;

			// Calculate majority and inform replica manager if there is a software bug
			double result = getMajority ( response ) ;
			return result ;
			
		} catch ( IOException e ) {
			System.out.println( e.getMessage () ); 
			return 0 ;
		} 
		
	}

	@Override
	public String requestResponse(CallBack cb) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
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
	
	
	
	
	/*private ORB orb;
	
	public void setORB(ORB orb_val) {
		orb = orb_val;
	}
	
	}*/

	
}
