


import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class Client extends UnicastRemoteObject implements Client_itf {
	private static Server_itf server;
	private static Client client;
	public static String debug_id="...";//System.currentTimeMillis();
    private static HashMap<Integer,SharedObject_itf> objects=new HashMap<Integer,SharedObject_itf>();

    public Client() throws RemoteException {
        super();
    }
    
    public String get_debugId() {
    	return Client.debug_id;
    }
    
    public static void flush(int id) {
		if (Client.objects.get(id) != null) try {
			Client.server.flush(id);
		}
		catch(RemoteException e) { e.printStackTrace(); }
    }


    ///////////////////////////////////////////////////
    //         Interface to be used by applications
    ///////////////////////////////////////////////////

    // initialization of the client layer
    public static void init() {
    	Debug.dump(Client.debug_id,"Init client");
    	//serveur ? interroger registry
    	//lancer serveur et l'enregistrer
    	try {
			Client.server=(Server_itf)Naming.lookup("rmi://localhost:8081/TVServer");
		}
		catch(Exception e) { e.printStackTrace(); }
    	try {
			Client.client=new Client();
		}
		catch(RemoteException e) { e.printStackTrace(); }
		if (Client.server == null) System.exit(0);
    }
	
    // lookup in the name server
    public static SharedObject lookup(String name) {
    	Debug.dump(Client.debug_id,"Client lookup name: " + name);
    	SharedObject res=null;
    	try {
			int id=Client.server.lookup(name);
			if (id > 0) {
				res=new SharedObject(null);
				res.setId(id);
				Client.objects.put(res.getId(),res);
			}
		}
		catch(RemoteException e) { e.printStackTrace(); }
		return res;
    }		
	
    // binding in the name server
    public static void register(String name,SharedObject_itf so) {
    	Debug.dump(Client.debug_id,"Client register name: " + name + ", so: " + so);
    	try {
    		SharedObject so_=(SharedObject)so;
			Client.server.register(name,so_.getId());
		}
		catch(RemoteException e) { e.printStackTrace(); }
    }

    // creation of a shared object
    public static SharedObject create(Object o) {
    	Debug.dump(Client.debug_id,"Client create: " + o);
    	SharedObject res=new SharedObject(o);
    	try {
			res.setId(Client.server.create(o));
		}
		catch(RemoteException e) { e.printStackTrace(); }
		Client.objects.put(res.getId(),res);
    	return res;
    }
	
    /////////////////////////////////////////////////////////////
    //    Interface to be used by the consistency protocol
    ////////////////////////////////////////////////////////////

    // request a read lock from the server
    public static Object lock_read(int id) {
    	Debug.dump(Client.debug_id,"Client lock_read id: " + id);
    	Object res=null;
		try {
			if (Client.objects.get(id) != null) {
				res=Client.server.lock_read(id,Client.client);
			}
		}
		catch(RemoteException e) { e.printStackTrace(); }
		return res;
    }

    // request a write lock from the server
    public static Object lock_write(int id) {
    	Debug.dump(Client.debug_id,"Client lock_write id: " + id);
    	Object res=null;
		try {
			if (Client.objects.get(id) != null)
				res=Client.server.lock_write(id,Client.client);
		}
		catch(RemoteException e) { e.printStackTrace(); }
		return res;
    }

    // receive a lock reduction request from the server
    public Object reduce_lock(int id) throws java.rmi.RemoteException {
    	Debug.dump(Client.debug_id,"Client reduce_lock id: " + id);
    	SharedObject res=null;
		if ((res=(SharedObject)Client.objects.get(id)) != null)
			res.reduce_lock();
		return res.obj;
    }

    // receive a reader invalidation request from the server
    public void invalidate_reader(int id) throws java.rmi.RemoteException {
    	Debug.dump(Client.debug_id,"Client invalidate_reader id: " + id);
    	SharedObject so=(SharedObject)Client.objects.get(id);
		if (so != null) so.invalidate_reader();
    }

    // receive a writer invalidation request from the server
    public Object invalidate_writer(int id) throws java.rmi.RemoteException {
    	Debug.dump(Client.debug_id,"Client invalidate_writer id: " + id);
    	SharedObject res=null;
		if ((res=(SharedObject)Client.objects.get(id)) != null)
			res.invalidate_writer();
		return res.obj;
    }
}
