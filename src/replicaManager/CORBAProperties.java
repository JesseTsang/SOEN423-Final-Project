package replicaManager;

public enum CORBAProperties
{
	BANKS("banks"),
	RM_IDS("rm.ids"),
	RM_UDP_PORT("rm.udp.ports"),
	RM_UDP_PORT_GEN(".udp.ports"),
	UDP_HOST("udp.initial.host"),
	ORB_HOST("orb.initial.host"),
	ORB_PORT("orb.initial.port"),
	ORB_HOST_ARG("-ORBInitialHost"),
	ORB_PORT_ARG("-ORBInitialPort");
	
	private String value;
	
	CORBAProperties(String value)
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return value;
	}

}
