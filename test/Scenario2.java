package test;

import src.*;

public class Scenario2 {
	public static void main(String argv[]) {
		//Initialisation du client
		Client.init();
		SharedObject so=Client.lookup("IRC");
	    if (so == null) {
	        so=Client.create(new Sentence());
	        Client.register("IRC",so);
	    }
	    //Demande de lecture
	    so.lock_read();
	    System.out.println("RES: " + ((Sentence)(so.obj)).read());
	    so.unlock();
	}
}
