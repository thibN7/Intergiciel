

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
	
	public void flush(int id) {
		Debug.dump("Server","Server flush id: " + id);
		(this.servers.get(id)).flush();
	}
	
    public static void main(String args[]) {
        try {
        	int port=8081;
            LocateRegistry.createRegistry(port);
            Naming.bind("rmi://localhost:" + port + "/TVServer",new Server());
        }
        catch (Exception e) { System.err.println(e); }
        Debug.msg("Le serveur est pret.");
    }
    
	public int lookup(String name) throws RemoteException {
		Debug.dump("Server","Server lookup name: " + name);
		return (this.name_server.get(name) != null)?this.name_server.get(name):0;
	}
	public void register(String name,int id) throws RemoteException {
		Debug.dump("Server","Server register name: " + name + ", id: " + id);
		if (this.servers.get(id) != null) this.name_server.put(name,id);
	}
	public int create(Object o) throws RemoteException {
		Debug.dump("Server","Server create object: " + o);
		ServerObject serv=new ServerObject(Server.object_id,o);
		Debug.addServer(serv);
		this.servers.put(Server.object_id,serv);
		return Server.object_id++;
	}
	public Object lock_read(int id,Client_itf client) throws RemoteException {
		Debug.dump("Server","Server received lock_read id: " + id + ", client: " + client);
		return (this.servers.get(id)).lock_read(client);
	}
	public Object lock_write(int id,Client_itf client) throws RemoteException {
		Debug.dump("Server","Server received lock_write id: " + id + ", client: " + client);
		return (this.servers.get(id)).lock_write(client);
	}

}
