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

public class SharedObject implements Serializable, SharedObject_itf {
	public Object obj;
	private int id;
	public static enum LockState { NL,WLT,RLT,RLC,WLC,RLT_WLC }
	private LockState lockState;

	public SharedObject(Object o) {
		this.id=0;
		this.obj=o;
		this.lockState=LockState.NL;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id=id;
	}
	
    // invoked by the user program on the client node
    public void lock_read() {
    	Debug.dump(Client.debug_id,"lock_read (start) --> (state: " + this.lockState + ")");
    	synchronized(this) {
	    	switch (this.lockState) {
	    		case NL: this.obj=Client.lock_read(this.id); this.lockState=LockState.RLT; break;
	    		case RLC: this.lockState=LockState.RLT; break;
	    		case WLC: this.lockState=LockState.RLT_WLC; break;
	    		case RLT: //Erreur
	    		case WLT: //Erreur
	    		case RLT_WLC: //Erreur
	    			Debug.exit("lock_read invalide");
	    	}
	    }
    	Debug.dump(Client.debug_id,"lock_read (end) --> (state: " + this.lockState + ")");
    }

    // invoked by the user program on the client node
    public void lock_write() {
    	Debug.dump(Client.debug_id,"lock_write (start) --> (state: " + this.lockState + ")");
    	synchronized(this) {
	    	switch (this.lockState) {
	    		case NL: this.obj=Client.lock_write(this.id); this.lockState=LockState.WLT; break;
	    		case RLC: this.obj=Client.lock_write(this.id); this.lockState=LockState.WLT; break;
	    		case WLC: this.lockState=LockState.WLT; break;
	    		case WLT: //Erreur
	    		case RLT: //Erreur
	    		case RLT_WLC: //Erreur
	    			Debug.exit("lock_write invalide");
	    	}
    	}
    	Debug.dump(Client.debug_id,"lock_write (end) --> (state: " + this.lockState + ")");
    }

    // invoked by the user program on the client node
    public synchronized void unlock() {
    	Debug.dump(Client.debug_id,"unlock (start) --> (state: " + this.lockState + ")");
    	switch(this.lockState) {
    		case RLT: this.lockState=LockState.RLC; break;
    		case WLT: this.lockState=LockState.WLC; break;
    		case RLT_WLC: this.lockState=LockState.WLC; break;
    		case WLC: //Erreur
    		case RLC: //Erreur
    		case NL: //Erreur
    			Debug.exit("unlock invalide");
    	}
    	Debug.dump(Client.debug_id,"unlock (end) --> (state: " + this.lockState + ")");
    }


    // callback invoked remotely by the server
    public synchronized Object reduce_lock() {
    	Debug.dump(Client.debug_id,"<-- reduce_lock (start) (state: " + this.lockState + ")");
    	SharedObject so=new SharedObject(null);
    	switch(this.lockState) {
    		case WLT: this.lockState=LockState.RLC; break;
    		case WLC: this.lockState=LockState.RLC; break;
    		case RLT_WLC: this.lockState=LockState.RLT; break;
    		case RLT: //Erreur
    		case RLC: //Erreur
    		case NL: //Erreur
    			Debug.exit("reduce_lock invalide");
    	}
    	Debug.dump(Client.debug_id,"<-- reduce_lock (end) (state: " + this.lockState + ")");
    	return so.obj;
    }

    // callback invoked remotely by the server
    public synchronized void invalidate_reader() {
    	Debug.dump(Client.debug_id,"<-- invalidate_reader (start) (state: " + this.lockState + ")");
    	switch(this.lockState) {
    		case RLT: this.lockState=LockState.NL; break;
    		case RLC: this.lockState=LockState.NL; break;
    		case RLT_WLC: //Erreur
    		case WLT: //Erreur
    		case WLC: //Erreur
    		case NL: //Erreur
    			Debug.exit("invalidate_reader invalide");
    	}
    	Debug.dump(Client.debug_id,"<-- invalidate_reader (end) (state: " + this.lockState + ")");
    }

    public synchronized Object invalidate_writer() {
    	Debug.dump(Client.debug_id,"<-- invalidate_writer (start) (state: " + this.lockState + ")");
    	SharedObject so=new SharedObject(null);
    	switch(this.lockState) {
    		case WLT: this.lockState=LockState.NL; break;
    		case WLC: this.lockState=LockState.NL; break;
    		case RLT_WLC: this.lockState=LockState.NL; break;
    		case RLT: //Erreur
    		case RLC: //Erreur
    		case NL: //Erreur
    			Debug.exit("invalidate_writer invalide");
    	}
    	Debug.dump(Client.debug_id,"<-- invalidate_writer (end) (state: " + this.lockState + ")");
    	return so.obj;
    }
}
