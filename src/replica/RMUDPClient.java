package replica;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class RMUDPClient
{
	private static final int BYTE_SIZE = 1000;
	private static final int TIMEOUT = 1000;
	private static final String HOST_NAME = "localhost";
	
	private String UDPHost;
	private int UDPPort;	
	private String response = null;
	
	Logger logger = null;
	
	public RMUDPClient(String hostName, int portNum)
	{
		this.UDPHost = hostName;
		this.UDPPort = portNum;
		
		logger = initiateLogger();
	}
	
	private Logger initiateLogger() 
	{
		Logger logger = Logger.getLogger("Server Logs/" + UDPHost + "- Server Log");
		FileHandler fh;
		
		try
		{
			//FileHandler Configuration and Format Configuration
			fh = new FileHandler("Server Logs/" + UDPHost + " - Server Log.log");
			
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
	
	public String send(String request)
	{	
		try
		{
			//2. Prepare packet
			//2.1 Get the destination IP
			InetAddress IP = InetAddress.getByName(UDPHost);
			
			//2.2 Prepare containers for the outgoing request and incoming reply.
			byte[] requestByte = new byte[BYTE_SIZE];
			byte[] responseByte = new byte[BYTE_SIZE];
			
			//2.3 Prepare request message
			requestByte = request.getBytes();
			
			//2.4 Prepare a socket and a packet for the request.
			DatagramSocket reqSocket = new DatagramSocket();
			reqSocket.setSoTimeout(TIMEOUT);
			DatagramPacket reqPacket = new DatagramPacket(requestByte, requestByte.length, IP, UDPPort);
			
			//3. Send out the packet
			reqSocket.send(reqPacket);
			
			this.logger.info("Server Log: | UDPClient Log | UDP Packet Sent.");
			
			//4. Wait for a response
			DatagramPacket reponsePacket = new DatagramPacket(responseByte, responseByte.length);
			
			
			//5. Unmarshall the response
			response = new String(reponsePacket.getData());
			
			this.logger.info("Server Log: | UDPClient Log | UDP Reply Received.");
			
			//6. Close connection.
			reqSocket.close();
			
			this.logger.info("Server Log: | UDPClient Log | UDP Request Completed. Connection Terminated.");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			response = "RMUDPClient: | send() Error: | Unable to send UDP request to server: [" + UDPHost + UDPPort + "]";
		}
		
		return response;
	}
	
	public String UDPMessage(String sourceRM, UDPStatus status, String branchID, String destRM)
	{
		String message = status.name() + ";" + sourceRM + ";" + branchID + ";" + destRM;
		
		return message;
	}
}
