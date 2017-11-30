package com.dbs.response;

import java.io.Serializable;

public class NumericResponse implements Serializable{

	private String replicaName;
	private double result;
	
	public NumericResponse(String name, double newresult) {
		replicaName = name;
		result = newresult;
	}

	public String getReplicaName() {
		return replicaName;
	}

	public double getResult() {
		return result;
	}
		
}