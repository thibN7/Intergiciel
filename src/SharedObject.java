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
	public static enum LockState { NL, WLT, RLT, RLC, WLC, RLT_WLC }
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
    	//dump(Client.debug_id,"lock_read -->");
    	System.out.println("SharedObject invoked lock_read");
    	System.out.println("Etat du SharedObject avant lock_read :" + this.lockState);
    	synchronized(this) {
	    	switch (this.lockState) {
	    		case NL: this.obj=Client.lock_read(this.id); this.lockState=LockState.RLT; break;
	    		case RLC: this.lockState=LockState.RLT; break;
	    		case WLC: this.lockState=LockState.RLT_WLC; break;
	    		case RLT: //Erreur : unlock oublié
	    		case WLT: //Erreur : unlock oublié
	    		case RLT_WLC: //Erreur : unlock oublié
	    			exit("lock_read invalide : unlock oublié");
	    	}
	    	System.out.println("Etat du SharedObject après lock_read :" + this.lockState);
	    }
    }

    // invoked by the user program on the client node
    public void lock_write() {
    	System.out.println("SharedObject invoked lock_write");
    	//dump(Client.debug_id,"lock_write -->");
    	System.out.println("Etat du SharedObject avant lock_write :" + this.lockState);
    	synchronized(this) {
	    	switch (this.lockState) {
	    		case NL: this.obj=Client.lock_write(this.id); this.lockState=LockState.WLT; break;
	    		case RLC: this.obj=Client.lock_write(this.id); this.lockState=LockState.WLT; break;
	    		case WLC: this.lockState=LockState.WLT; break;
	    		case WLT: //Erreur : unlock oublié
	    		case RLT: //Erreur : unlock oublié
	    		case RLT_WLC: //Erreur : unlock oublié
	    			exit("lock_write invalide : unlock oublié");
	    	}
	    	System.out.println("Etat du SharedObject après lock_write :" + this.lockState);
    	}
    }

    // invoked by the user program on the client node
    public synchronized void unlock() {
    	//dump(Client.debug_id,"unlock -->");
    	System.out.println("SharedObject invoked unlock");
    	System.out.println("Etat du SharedObject avant unlock :" + this.lockState);
    	switch(this.lockState) {
    		case RLT: this.lockState=LockState.RLC; break;
    		case WLT: this.lockState=LockState.WLC; break;
    		case RLT_WLC: this.lockState=LockState.WLC; break;
    		case WLC: //Erreur : lock oublié
    		case RLC: //Erreur : lock oublié
    		case NL: //Erreur : lock oublié
    			exit("unlock invalide : lock oublié");
    	}
    	System.out.println("Etat du SharedObject après unlock :" + this.lockState);
    }


    // callback invoked remotely by the server
    public synchronized Object reduce_lock() {
    	//dump(Client.debug_id,"<-- reduce_lock");
    	System.out.println("SharedObject received reduce_lock");
    	System.out.println("Etat du SharedObject avant callback reduce_lock :" + this.lockState);
    	SharedObject so=new SharedObject(null);
    	switch(this.lockState) {
    		case WLT: this.lockState=LockState.RLC; break; //TODO : Attente
    		case WLC: this.lockState=LockState.RLC; break;
    		case RLT_WLC: this.lockState=LockState.RLT; break;
    		case RLT: //Erreur : état invalide
    		case RLC: //Erreur : état invalide
    		case NL: //Erreur : état invalide
    			exit("reduce_lock invalide : état invalide");
    	}
    	System.out.println("Etat du SharedObject après callback reduce_lock :" + this.lockState);
    	return so.obj;
    }

    // callback invoked remotely by the server
    public synchronized void invalidate_reader() {
    	//dump(Client.debug_id,"<-- invalidate_reader");
    	System.out.println("SharedObject received invalidate_reader");
    	System.out.println("Etat du SharedObject avant callback invalidate_reader :" + this.lockState);
    	switch(this.lockState) {
    		case RLT: this.lockState=LockState.NL; break; // TODO : attente
    		case RLC: this.lockState=LockState.NL; break;
    		case RLT_WLC: //Erreur : état invalide
    		case WLT: //Erreur : conflit avec un write_lock
    		case WLC: //Erreur : état invalide
    		case NL: //Erreur : état invalide
    			exit("invalidate_reader invalide : conflit avec un write_lock (WLT) ou état invalide");
    	}
    	System.out.println("Etat du SharedObject après callback invalidate_reader :" + this.lockState);
    }

    public synchronized Object invalidate_writer() {
    	//dump(Client.debug_id,"<-- invalidate_writer");
    	System.out.println("SharedObject received invalidate_writer");
    	System.out.println("Etat du SharedObject avant callback invalidate_writer :" + this.lockState);
    	SharedObject so=new SharedObject(null);
    	switch(this.lockState) {
    		case WLT: this.lockState=LockState.NL; break; //TODO : attente
    		case WLC: this.lockState=LockState.NL; break;
    		case RLT_WLC: this.lockState=LockState.NL; break; //TODO : attente
    		case RLT: //Erreur : état invalide
    		case RLC: //Erreur : état invalide
    		case NL: //Erreur : état invalide
    			exit("invalidate_writer invalide : état invalide");
    	}
    	System.out.println("Etat du SharedObject après callback invalidate_writer :" + this.lockState);
    	return so.obj;
    }
}
