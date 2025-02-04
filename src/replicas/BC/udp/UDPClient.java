package replicas.BC.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


//This client will make the UDP call/request for the interbank operations
public class UDPClient
{
	private String UDPHost;
	private int UDPPort;	
	private BankUDPInterface request;
	private BankUDPInterface response;
	
	private String branchID;
	Logger logger = null;
	
	
	public UDPClient(String hostName, int portNum, String branchID)
	{
		this.UDPHost = hostName;
		this.UDPPort = portNum;
		this.branchID = branchID;
		
		logger = initiateLogger();
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
			System.err.println("Server Log: | UDPClient Log Initialization Failed | Error: Security Exception " + e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.println("Server Log: | UDPClient Log Initialization Failed | Error: IO Exception " + e);
			e.printStackTrace();
		}
			
		System.out.println("Server Log: | UDPClient Log Initialization | Logger initialization success.");
		
		return logger;
	}
	
	public void send(BankUDPInterface requestCall)
	{
		//1. Make sure the old request is cleared.
		request = null;
		
		try
		{
			this.logger.info("Server Log: | UDPClient Log |Initializating UDP Request.");
			
			//2. Prepare packet
			//2.1 Get the destination IP
			InetAddress ip = InetAddress.getByName(UDPHost);
			
			//2.2 Prepare containers for the outgoing request and incoming reply.
			byte[] requestByte = MarshallService.marshall(requestCall);
			byte[] replyByte = new byte[requestByte.length];
			
			//2.3 Prepare a socket and a packet for the request.
			DatagramSocket reqSocket = new DatagramSocket();
			DatagramPacket reqPacket = new DatagramPacket(requestByte, requestByte.length, ip, UDPPort);
			
			//3. Send out the packet
			reqSocket.send(reqPacket);
			
			this.logger.info("Server Log: | UDPClient Log | UDP Packet Sent.");
			
			//4. Wait for a response
			DatagramPacket replyPacket = new DatagramPacket(replyByte, replyByte.length);
			
			
			//5. Unmarshall the response
			response = MarshallService.unmarshall(replyPacket.getData());
			
			this.logger.info("Server Log: | UDPClient Log | UDP Reply Received.");
			
			//6. Close connection.
			reqSocket.close();
			
			this.logger.info("Server Log: | UDPClient Log | UDP Request Completed. Connection Terminated.");
			
		}
		catch (IOException e)
		{
			this.logger.severe("Server Log: | UDPClient Log | UDP Request Failed. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	//Getter for the response
	public BankUDPInterface getResponse()
	{
		return response;
	}
}
