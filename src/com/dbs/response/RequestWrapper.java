package com.dbs.response;

import java.io.Serializable;
import java.net.InetAddress;

public class RequestWrapper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private long accountNumber;
	private float amount;               // The parameter for deposit, withdraw and transfer
	private long targetAccountNumber;   // The parameter for transfer method
	private String requestCommand;      // The name of the request
	
	private int sequenceNumber ;
	private InetAddress ip;
	private int port;
	
	// Constructor 1    For method: balance()
	public RequestWrapper(InetAddress ip, int port, String requestName, long accountNumber) {
		this.ip = ip;
		this.port = port;
		requestCommand = requestName;
		this.accountNumber = accountNumber;
	}
	
	// Constructor 2     For methods: deposit()   withdraw()
	public RequestWrapper(InetAddress ip, int port, String requestName, long accountNumber, float amount) {
		
		this(ip, port, requestName, accountNumber);
		this.amount = amount;
	}
	
	// Constructor 3     For method: transfer()
	public RequestWrapper(InetAddress ip, int port, String requestName, long accountNumber, long targetAccountNumber,float amount) {
		
		this(ip, port, requestName, accountNumber);
		this.targetAccountNumber = targetAccountNumber;
		this.amount = amount;
	}

	public long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public long getTargetAccountNumber() {
		return targetAccountNumber;
	}

	public void setTargetAccountNumber(long targetAccountNumber) {
		this.targetAccountNumber = targetAccountNumber;
	}

	public String getRequestCommand() {
		return requestCommand;
	}

	public void setRequestCommand(String requestCommand) {
		this.requestCommand = requestCommand;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
		
		
	
	
	
	
}
