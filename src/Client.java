package src;



import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {
	private static Server server;

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
				Client.server=(Server)Naming.lookup("rmi://localhost:8081/IRC");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
    	}
    }
	
    // lookup in the name server
    public static SharedObject lookup(String name) {
		return null;
    }		
	
    // binding in the name server
    public static void register(String name,SharedObject_itf so) {
    	try {
			Client.server.register(name,0);
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
    }

    // creation of a shared object
    public static SharedObject create(Object o) {
    	return new SharedObject(o);
    }
	
    /////////////////////////////////////////////////////////////
    //    Interface to be used by the consistency protocol
    ////////////////////////////////////////////////////////////

    // request a read lock from the server
    public static Object lock_read(int id) {
		return id;
    }

    // request a write lock from the server
    public static Object lock_write(int id) {
		return id;
    }

    // receive a lock reduction request from the server
    public Object reduce_lock(int id) throws java.rmi.RemoteException {
		return id;
    }


    // receive a reader invalidation request from the server
    public void invalidate_reader(int id) throws java.rmi.RemoteException {
    }


    // receive a writer invalidation request from the server
    public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		return id;
    }
}
