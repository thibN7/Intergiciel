package src;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerObject {
	
	// Attributes
	private int id ;
	private Object object ;
	private ArrayList<Client> listReaders = new ArrayList<Client>() ;
	private Client writer ;
	private final Lock mutexMonitor = new ReentrantLock();
	
	// Constructor
	public ServerObject(int id, Object object) {
		this.id = id ;
		this.object = object ;
	}
	
	
	// Writer exists method returns a boolean
	public boolean exists(Client writer) {
		return (writer!=null);		
	}
	
	
	public void lock_write(Client client) {
		mutexMonitor.lock();
		
		if (exists(this.writer)) {
			try {
				this.writer.invalidate_writer(this.id);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// writer == false
			
			
		}
		
		mutexMonitor.unlock();		
	}
	
	public void lock_read(Client client) {
		mutexMonitor.lock();
		
		
		mutexMonitor.unlock();
	}
	
}
