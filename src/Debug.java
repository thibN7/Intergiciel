package src;

import java.rmi.RemoteException;
import java.util.ArrayList;

import src.SharedObject.LockState;
public class Debug {
	public static enum LogState { DEBUG,ANY }
	private static LogState logState=LogState.DEBUG;
	private static ArrayList<ServerObject> listServers=new ArrayList<ServerObject>();
	
	//LOGS
	public static void msg(String data) {
		if (Debug.logState == LogState.DEBUG)
			System.out.println(data);
	}
	
	public static void error(String data) {
		if (Debug.logState == LogState.DEBUG)
			System.err.println(data);
	}
	
	public static void dump(String id,String data) {
		if (Debug.logState == LogState.DEBUG)
			System.out.println("[" + id + "] " + data);
	}
	
	public static void dumpStateServer(String id,String data,ServerObject server,Client_itf writer,ArrayList<Client_itf> readers) {
		try {
			String d="";
			for(Client_itf a : readers)
			d+=", "+a.get_debugId();
			Debug.dump(id,data + " (" + ((!server.writerExists())?"pas d'Žcrivain":("Žcrivain: " + writer.get_debugId())) + ", " + ((d.equals(""))?"pas de lecteur":"lecteurs :") + d + ")");
		}
		catch(RemoteException e) { e.printStackTrace(); }
	}
	
	public static void exit(String data) {
		Debug.msg(data);
		Debug.msg("[Fin du processus]");
		System.exit(0);
	}
	
	//CLIENT-SERVER CONNECTION
	public static void addServer(ServerObject server) {
		Debug.listServers.add(server);
	}
	
	public static void flushServer(ServerObject server) {
		ServerObject so=Debug.listServers.get(Debug.listServers.indexOf(server));
		so.flush();
	}
}
