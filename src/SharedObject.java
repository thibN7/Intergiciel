package src;

import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	public Object obj;
	public int id;
	public static enum lockState {NL,WLT,RLT,RLC,WLC,RLT_WLC}
	
	public SharedObject(Object o) {
		this.obj=o;
	}
	
    // invoked by the user program on the client node
    public void lock_read() {
    	Client.lock_read(this.id);
    }

    // invoked by the user program on the client node
    public void lock_write() {
    	Client.lock_write(this.id);
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
