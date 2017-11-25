package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import server.BankServerImpl;

public class UDPServer implements Runnable
{
	private String UDPHost;
	private int UDPPort;
	private BankServerImpl bankServer;
	boolean connectionStatus;
	
	public UDPServer(String UDPHost, int UDPPort, BankServerImpl bankServer)
	{
		this.UDPHost = UDPHost;
		this.UDPPort = UDPPort;
		this.bankServer = bankServer;
	}
	
	public void start() throws IOException
	{
		DatagramSocket serverSocket = new DatagramSocket(UDPPort);
		
		byte[] dataBuffer = new byte[1048];
		
		while (connectionStatus == true)
		{
			//1. Create a datagram for incoming packets
			DatagramPacket requestPacket = new DatagramPacket (dataBuffer, dataBuffer.length);
			
			//2. The server will take the incoming request
			serverSocket.receive(requestPacket);
			
			//3. From the packet, we take the necessary information for reply
			InetAddress ip = requestPacket.getAddress();
			int requestPort = requestPacket.getPort();
			
			//4. Translate the byte data from the request to invoke the method
			BankUDPInterface requestData = MarshallService.unmarshall(requestPacket.getData());
			requestData.execute(this.bankServer, this.bankServer.getBranchID());
			
			//5. Reply
			byte[] reply = MarshallService.marshall(requestData);
			DatagramPacket replyPacket = new DatagramPacket(reply, reply.length, ip, requestPort);
			serverSocket.send(replyPacket);
		}
	}
	
	public void stop()
	{
		connectionStatus = false;
	}

	@Override
	public void run()
	{
		try
		{
			start();
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}	
	}	
}
