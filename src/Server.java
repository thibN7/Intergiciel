package src;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server implements Server_itf {
    public static void main (String args[]) {
        try {
            //  Création du serveur de noms
            LocateRegistry.createRegistry(8081);
            //  Enregistrement de l'instance de parking dans le serveur local
            Naming.bind("rmi://localhost:8081/IRCServer",new Server());
        } catch (Exception e) { System.err.println (e); }
        // Service pret : attente d'appels
        System.out.println ("Le superviseur est pret.");
    }
    
	public int lookup(String name) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	public void register(String name,int id) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	public int create(Object o) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	public Object lock_read(int id,Client_itf client) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object lock_write(int id,Client_itf client) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
