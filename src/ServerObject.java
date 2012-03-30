package src;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerObject {

	// 1 Shared object avec un numŽro
	// clients qui s'adressent ˆ lui

	// Attributes
	private int id ;
	private Object object ;
	private ArrayList<Client_itf> listReaders = new ArrayList<Client_itf>() ;
	private Client_itf writer =null;
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
		String d=""; for(Client_itf a : this.listReaders) d+=", "+a.get_debugId();
    	SharedObject.dump(1,"lock_write (start) --> (" + ((!this.writerExists())?"aucun":this.writer.get_debugId()) +  d + ")");

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
		d=""; for(Client_itf a : this.listReaders) d+=", "+a.get_debugId();
    	SharedObject.dump(1,"lock_write (end) --> (" + ((!this.writerExists())?"aucun":this.writer.get_debugId()) +  d + ")");

		return obj;		
	}


	// LOCK_READ
	public Object lock_read(Client_itf client) throws RemoteException {
		String d=""; for(Client_itf a : this.listReaders) d+=", "+a.get_debugId();
    	SharedObject.dump(1,"lock_read (start) --> (" + ((!this.writerExists())?"aucun":this.writer.get_debugId()) +  d + ")");

		mutexMonitor.lock();

		if (writerExists()) {
			this.object = this.writer.reduce_lock(this.id);
			this.writer = null ;
		}

		this.listReaders.add(client);

		mutexMonitor.unlock();
		d=""; for(Client_itf a : this.listReaders) d+=", "+a.get_debugId();
    	SharedObject.dump(1,"lock_read (end) --> (" + ((!this.writerExists())?"aucun":this.writer.get_debugId()) +  d + ")");

		return this.object;
	}

}