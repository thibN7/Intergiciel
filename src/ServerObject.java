package src;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerObject {
	
	// 1 Shared object avec un numéro
	// clients qui s'adressent à lui
	
	// Attributes
	private int id ;
	private Object object ;
	private ArrayList<Client_itf> listReaders = new ArrayList<Client_itf>() ;
	private Client_itf writer ;
	private final Lock mutexMonitor = new ReentrantLock();
	
	// Constructor
	public ServerObject(int id, Object object) {
		this.id = id ;
		this.object = object ;
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
				
		mutexMonitor.unlock();
		
		return obj;		
	}
	
	
	// LOCK_READ
	public Object lock_read(Client_itf client) throws RemoteException {
		
		mutexMonitor.lock();
		
		if (writerExists()) {
			this.object = this.writer.reduce_lock(this.id);
			this.writer = null ;
		}
		
		this.listReaders.add(client);
		
		mutexMonitor.unlock();
		
		return this.object;
	}
	
}
