package test;
import src.*;

public class Scenario1 {
	public static void main(String argv[]) {
		//Initialisation du client
		Client.init();
		SharedObject so=Client.lookup("IRC");
	    if (so == null) {
	        so=Client.create(new Sentence());
	        Client.register("IRC",so);
	    }
	    //Demande d'Žcriture
	    so.lock_write();
	    ((Sentence)(so.obj)).write("coucou");
	    //so.unlock();
	    //Demande de lecture
	    so.lock_read();
	    System.out.println("RES: " + ((Sentence)(so.obj)).read());
	    so.unlock();
	}
}
