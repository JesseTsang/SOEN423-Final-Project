package replicaManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RMUDPServer implements Runnable
{
	private ReplicaManagerInterface replicaManager;
	private int UDPPort;
	private boolean connectionStatus;
	
	public RMUDPServer(ReplicaManagerInterface replicaManager, int port)
	{
		this.replicaManager = replicaManager;
		this.UDPPort = port;
		this.connectionStatus = true;
		
		System.out.println("RMUDPServer Log: | UDP Server Started. | Host:" + this.replicaManager);
	}
	
	public void stop()
	{
		this.connectionStatus = false;
	}
	
	@Override
	public void run()
	{
		try
		{
			DatagramSocket serverSocket = new DatagramSocket(UDPPort);
			
			while(connectionStatus == true)
			{
				//0. Create byte array buffer for incoming request
				byte[] dataBuffer = new byte[1048];
				
				//1. Create a datagram for incoming packets
				DatagramPacket requestPacket = new DatagramPacket (dataBuffer, dataBuffer.length);
				
				//2. The server will take the incoming request
				serverSocket.receive(requestPacket);
				
				//3. From the packet, we take the necessary information for reply
				InetAddress returnIP = requestPacket.getAddress();
				int returnPort = requestPacket.getPort();
				
				//4. Translate the byte data from the request to invoke the method			
				String request = new String(requestPacket.getData());
				System.out.println("RMUDPServer Log: | Request Message: | From: [" + returnIP + ":" + returnPort +"] to " 
									+ replicaManager.getRMHost() + " | Message: " + request );
				
				String requestMsg = replicaManager.handleUDPMsg(request);
				
				//If it is not empty call from FE
				if(requestMsg != null && requestMsg.length() > 0)
				{
					//5. Reply
					byte[] reply = requestMsg.getBytes();
					DatagramPacket replyPacket = new DatagramPacket(reply, reply.length, returnIP, returnPort);
					
					serverSocket.send(replyPacket);
				}			
			}//end while
			
			serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("RMUDPServer Log: | Error: " + e.getMessage());
		}
	}
}