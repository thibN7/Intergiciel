package test;
import src.*;

public class Scenario1 {
	public static void main(String argv[]) {
		//Initialisation
		Client.init();
		SharedObject s=Client.lookup("IRC");
	    if (s == null) {
	        s=Client.create(new Sentence());
	        Client.register("IRC",s);
	    }
	    //Demande d'Žcriture
	    //Client.lock_write(id)
	}
}
