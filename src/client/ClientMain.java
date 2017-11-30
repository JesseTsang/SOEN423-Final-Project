package client;

import java.io.IOException;

import org.omg.CORBA.ORBPackage.InvalidName;

public class ClientMain {

	public static void main(String[] args) throws InvalidName, IOException {
	
		Client client = new Client(args);
		
		client.getMenu();
	}
}
