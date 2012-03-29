package src;



import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.util.*;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {
	private static Server server;
	private static Client client;
    private static HashMap<Integer,SharedObject_itf> objects=new HashMap<Integer,SharedObject_itf>();

    public Client() throws RemoteException {
        super();
        Client.server=null;
    }


    ///////////////////////////////////////////////////
    //         Interface to be used by applications
    ///////////////////////////////////////////////////

    // initialization of the client layer
    public static void init() {
	    	//serveur ? interroger registry
	    	//lancer serveur et l'enregistrer
    	if (Client.server == null) {
	    	try {
				Client.server=(Server)Naming.lookup("rmi://localhost:8081/TVServer");
			}
			catch(Exception e) { e.printStackTrace(); }
			if (Client.server == null) System.exit(0);
    	}
    	try {
			Client.client=new Client();
		}
		catch(RemoteException e) { e.printStackTrace(); }
    }
	
    // lookup in the name server
    public static SharedObject lookup(String name) {
    	SharedObject res=null;
    	try {
			int id=Client.server.lookup(name);
			if (id > 0) {
				res=new SharedObject(null);
				res.id=id;
				Client.objects.put(res.id,res);
			}
		}
		catch(RemoteException e) { e.printStackTrace(); }
		return res;
    }		
	
    // binding in the name server
    public static void register(String name,SharedObject_itf so) {
    	try {
    		SharedObject so_=(SharedObject)so;
			Client.server.register(name,so_.id);
		}
		catch(RemoteException e) { e.printStackTrace(); }
    }

    // creation of a shared object
    public static SharedObject create(Object o) {
    	SharedObject res=new SharedObject(o);
    	try {
			res.id=Client.server.create(o);
		}
		catch(RemoteException e) { e.printStackTrace(); }
		Client.objects.put(res.id,res);
    	return res;
    }
	
    /////////////////////////////////////////////////////////////
    //    Interface to be used by the consistency protocol
    ////////////////////////////////////////////////////////////

    // request a read lock from the server
    public static Object lock_read(int id) {
    	Object res=null;
		try {
			if (Client.objects.get(id) != null)
				res=Client.server.lock_read(id,Client.client);
		}
		catch(RemoteException e) { e.printStackTrace(); }
		return res;
    }

    // request a write lock from the server
    public static Object lock_write(int id) {
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
    	SharedObject res=null;
		if ((res=(SharedObject)Client.objects.get(id)) != null)
			res.reduce_lock();
		return res;
    }


    // receive a reader invalidation request from the server
    public void invalidate_reader(int id) throws java.rmi.RemoteException {
    	SharedObject res=null;
		if ((res=(SharedObject)Client.objects.get(id)) != null)
			res.invalidate_reader();
    }


    // receive a writer invalidation request from the server
    public Object invalidate_writer(int id) throws java.rmi.RemoteException {
    	SharedObject res=null;
		if ((res=(SharedObject)Client.objects.get(id)) != null)
			res.invalidate_writer();
		return res;
    }
}
