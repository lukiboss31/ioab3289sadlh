package schoolcomm;


public class User {

	public int remotePort;
	public String hostName;
	public String ipAddr;
	public String username;
	public long zeitStempel;
	public int port;
	@Override
	public String toString() {
		return "User [ username=" + username +", remotePort=" + remotePort + ", port=" + port +", hostName=" + hostName
				+ ", ipAddr=" + ipAddr 
				+ ", zeitStempel=" + zeitStempel + "]";
	}
	
	
	
}
