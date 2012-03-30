package src;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import src.SharedObject.LockState;

public class ServerObject {

	// 1 Shared object avec un numŽro
	// clients qui s'adressent ˆ lui

	// Attributes
	private int id ;
	private Object object ;
	public static enum LockState { No_Lock, Write_Locked, Read_Locked }
	private LockState lockState;
	private ArrayList<Client_itf> listReaders = new ArrayList<Client_itf>() ;
	private Client_itf writer =null;
	private final Lock mutexMonitor = new ReentrantLock();

	// Constructor
	public ServerObject(int id, Object object) {
		this.id = id ;
		this.object = object ;
		this.lockState = LockState.No_Lock;
	}


	// Writer exists method returns a boolean
	public boolean writerExists() {
		return (this.writer!=null);		
	}

	// Readers exist method returns a boolean
	public boolean readersExist() {
		return (this.listReaders.size()>0);
	}

	// LOCK_WRITE
	public Object lock_write(Client_itf client) throws RemoteException {
		String d=""; for(Client_itf a : this.listReaders) d+=", "+a.get_debugId();
    	SharedObject.dump(1,"ServerObject lock_write (start) --> (" + ((!this.writerExists())?"aucun":this.writer.get_debugId()) +  d + ")");

		Object obj ;

		mutexMonitor.lock();

		if (!writerExists() && !readersExist()) {
			obj = this.object;
		}
		else
		{
			if (writerExists()) {
				this.object = this.writer.invalidate_writer(this.id);
				// writer == false
			}
			for(Client_itf reader : this.listReaders) {
				reader.invalidate_reader(this.id);
			}
			listReaders.clear();
			obj = this.object;
		}
		this.writer = client;
		
		System.out.println("Etat du ServerObject avant lock_write : " + this.lockState);
		switch(this.lockState) {
			case No_Lock : this.lockState = LockState.Write_Locked; break;
			case Read_Locked : this.lockState = LockState.Write_Locked; break;
			case Write_Locked : break; // On reste dans l'etat Write_Locked
		}
		System.out.println("Etat du ServerObject après lock_write : " + this.lockState);

		mutexMonitor.unlock();
		d=""; for(Client_itf a : this.listReaders) d+=", "+a.get_debugId();
    	SharedObject.dump(1,"ServerObject lock_write (end) --> (" + ((!this.writerExists())?"aucun":this.writer.get_debugId()) +  d + ")");

		return obj;		
	}


	// LOCK_READ
	public Object lock_read(Client_itf client) throws RemoteException {
		String d=""; for(Client_itf a : this.listReaders) d+=", "+a.get_debugId();
    	SharedObject.dump(1,"ServerObject lock_read (start) --> (" + ((!this.writerExists())?"aucun":this.writer.get_debugId()) +  d + ")");

		mutexMonitor.lock();

		if (writerExists()) {
			this.object = this.writer.reduce_lock(this.id);
			this.writer = null ;
		}

		this.listReaders.add(client);
		
		System.out.println("Etat du ServerObject avant lock_read : " + this.lockState);
		switch(this.lockState) {
			case No_Lock : this.lockState = LockState.Read_Locked; break;
			case Read_Locked : break; // On reste dans cet etat
			case Write_Locked : this.lockState = LockState.Read_Locked; break;
		}
		System.out.println("Etat du ServerObject apres lock_read : " + this.lockState);

		mutexMonitor.unlock();
		d=""; for(Client_itf a : this.listReaders) d+=", "+a.get_debugId();
    	SharedObject.dump(1,"ServerObject lock_read (end) --> (" + ((!this.writerExists())?"aucun":this.writer.get_debugId()) +  d + ")");

		return this.object;
	}

}