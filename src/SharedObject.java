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
	public static void dump(long id,String data) {
		System.out.println("[" + id + "] " + data);
	}
	public static void exit(String data) {
		System.out.println(data);
		System.out.println("[Fin du processus]");
		System.exit(0);
	}
	
    // invoked by the user program on the client node
    public void lock_read() {
    	dump(Client.debug_id,"lock_read -->");
    	System.out.println("Etat du SharedObject avant lock_read :" + this.lockState);
    	synchronized(this) {
	    	switch (this.lockState) {
	    		case NL: this.obj=Client.lock_read(this.id); this.lockState=LockState.RLT; break;
	    		case RLC: this.lockState=LockState.RLT; break;
	    		case WLC: this.lockState=LockState.RLT_WLC; break;
	    		case RLT: //Erreur
	    		case WLT: //Erreur
	    		case RLT_WLC: //Erreur
	    			exit("lock_read invalide");
	    	}
	    	System.out.println("Etat du SharedObject après lock_read :" + this.lockState);
	    }
    }

    // invoked by the user program on the client node
    public void lock_write() {
    	dump(Client.debug_id,"lock_write -->");
    	System.out.println("Etat du SharedObject avant lock_write :" + this.lockState);
    	synchronized(this) {
	    	switch (this.lockState) {
	    		case NL: this.obj=Client.lock_write(this.id); this.lockState=LockState.WLT; break;
	    		case RLC: this.obj=Client.lock_write(this.id); this.lockState=LockState.WLT; break;
	    		case WLC: this.lockState=LockState.WLT; break;
	    		case WLT: //Erreur
	    		case RLT: //Erreur
	    		case RLT_WLC: //Erreur
	    			exit("lock_write invalide");
	    	}
	    	System.out.println("Etat du SharedObject après lock_write :" + this.lockState);
    	}
    }

    // invoked by the user program on the client node
    public synchronized void unlock() {
    	dump(Client.debug_id,"unlock -->");
    	System.out.println("Etat du SharedObject avant unlock :" + this.lockState);
    	switch(this.lockState) {
    		case RLT: this.lockState=LockState.RLC; break;
    		case WLT: this.lockState=LockState.WLC; break;
    		case RLT_WLC: this.lockState=LockState.WLC; break;
    		case WLC: //Erreur
    		case RLC: //Erreur
    		case NL: //Erreur
    			exit("unlock invalide");
    	}
    	System.out.println("Etat du SharedObject après unlock :" + this.lockState);
    }


    // callback invoked remotely by the server
    public synchronized Object reduce_lock() {
    	dump(Client.debug_id,"<-- reduce_lock");
    	System.out.println("Etat du SharedObject avant reduce_lock :" + this.lockState);
    	SharedObject so=new SharedObject(null);
    	switch(this.lockState) {
    		case WLT: this.lockState=LockState.RLC; break;
    		case WLC: this.lockState=LockState.RLC; break;
    		case RLT_WLC: this.lockState=LockState.RLT; break;
    		case RLT: //Erreur
    		case RLC: //Erreur
    		case NL: //Erreur
    			exit("reduce_lock invalide");
    	}
    	System.out.println("Etat du SharedObject après reduce_lock :" + this.lockState);
    	return so.obj;
    }

    // callback invoked remotely by the server
    public synchronized void invalidate_reader() {
    	dump(Client.debug_id,"<-- invalidate_reader");
    	System.out.println("Etat du SharedObject avant invalidate_reader :" + this.lockState);
    	switch(this.lockState) {
    		case RLT: this.lockState=LockState.NL; break;
    		case RLC: this.lockState=LockState.NL; break;
    		case RLT_WLC: //Erreur
    		case WLT: //Erreur
    		case WLC: //Erreur
    		case NL: //Erreur
    			exit("invalidate_reader invalide");
    	}
    	System.out.println("Etat du SharedObject après invalidate_reader :" + this.lockState);
    }

    public synchronized Object invalidate_writer() {
    	dump(Client.debug_id,"<-- invalidate_writer");
    	System.out.println("Etat du SharedObject avant invalidate_writer :" + this.lockState);
    	SharedObject so=new SharedObject(null);
    	switch(this.lockState) {
    		case WLT: this.lockState=LockState.NL; break;
    		case WLC: this.lockState=LockState.NL; break;
    		case RLT_WLC: this.lockState=LockState.NL; break;
    		case RLT: //Erreur
    		case RLC: //Erreur
    		case NL: //Erreur
    			exit("invalidate_writer invalide");
    	}
    	System.out.println("Etat du SharedObject après invalidate_writer :" + this.lockState);
    	return so.obj;
    }
}
