package src;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server extends UnicastRemoteObject implements Server_itf, Serializable {
	private static int object_id=1;
	private HashMap<String,Integer> name_server=new HashMap<String,Integer>();
	private HashMap<Integer,ServerObject> servers=new HashMap<Integer,ServerObject>();

	protected Server() throws RemoteException {
		super();
	}
	
    public static void main (String args[]) {
        try {
        	int port=8081;
            LocateRegistry.createRegistry(port);
            Naming.bind("rmi://localhost:" + port + "/TVServer",new Server());
        }
        catch (Exception e) { System.err.println(e); }
        Debug.msg("Le serveur est pret.");
    }
    
	public int lookup(String name) throws RemoteException {
		return (this.name_server.get(name) != null)?this.name_server.get(name):0;
	}
	public void register(String name,int id) throws RemoteException {
		if (this.servers.get(id) != null) this.name_server.put(name,id);
	}
	public int create(Object o) throws RemoteException {
		this.servers.put(Server.object_id,new ServerObject(Server.object_id,o));
		return Server.object_id++;
	}
	public Object lock_read(int id,Client_itf client) throws RemoteException {
		return (this.servers.get(id)).lock_read(client);
	}
	public Object lock_write(int id,Client_itf client) throws RemoteException {
		return (this.servers.get(id)).lock_write(client);
	}

}
