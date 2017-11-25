package udp;

import java.io.Serializable;

import common.BankServerInterfacePOA;
import domain.BranchID;

public interface BankUDPInterface extends Serializable
{
	public void execute(BankServerInterfacePOA bankServer, BranchID branchID);
}
