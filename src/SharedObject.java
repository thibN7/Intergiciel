package src;

import java.io.*;
/*
 * (erreur = oubli unlock)
NL -> demande au serveur -> RLT
RLT erreur
WLT erreur
RLC -> RLT
WLC -> RLT_WLC
RLT_WLC erreur
*/
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedObject implements Serializable, SharedObject_itf {
	public Object obj;
	public int id;
	public static enum LockState { NL,WLT,RLT,RLC,WLC,RLT_WLC }
	private LockState lockState;
	private Lock mutex=new ReentrantLock();
	
	public SharedObject(Object o) {
		this.id=0;
		this.obj=o;
		this.lockState=LockState.NL;
	}
	
    // invoked by the user program on the client node
    public void lock_read() {
    	synchronized(this) {
	    	switch (this.lockState) {
	    		case NL: Client.lock_read(this.id); this.lockState=LockState.RLT; break;
	    		case RLC: this.lockState=LockState.RLT; break;
	    		case WLC: this.lockState=LockState.RLT_WLC; break;
	    		case RLT: //Erreur
	    		case WLT: //Erreur
	    		case RLT_WLC: //Erreur
	    			break;
	    	}
	    }
    }

    // invoked by the user program on the client node
    public void lock_write() {
    	synchronized(this) {
	    	switch (this.lockState) {
	    		case NL: Client.lock_write(this.id); this.lockState=LockState.WLT; break;
	    		case RLC: Client.lock_write(this.id); this.lockState=LockState.WLT; break;
	    		case WLC: this.lockState=LockState.WLT; break;
	    		case WLT: //Erreur
	    		case RLT: //Erreur
	    		case RLT_WLC: //Erreur
	    			break;
	    	}
    	}
    }

    // invoked by the user program on the client node
    public synchronized void unlock() {
    	
    }


    // callback invoked remotely by the server
    public synchronized Object reduce_lock() {
		return null;
    }

    // callback invoked remotely by the server
    public synchronized void invalidate_reader() {
    }

    public synchronized Object invalidate_writer() {
		return null;
    }
}
