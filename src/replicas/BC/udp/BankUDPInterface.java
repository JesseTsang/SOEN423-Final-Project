package replicas.BC.udp;

import java.io.Serializable;

import dbs.corba.FailureFreeFEPOA;


public interface BankUDPInterface extends Serializable
{
	public void execute(FailureFreeFEPOA bankServer, String branchID);
}
