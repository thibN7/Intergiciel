

import java.awt.TextArea;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class Debug {
	public static enum LogState { DEBUG,ANY }
	private static LogState logState=LogState.DEBUG;
	private static ArrayList<ServerObject> listServers=new ArrayList<ServerObject>();
	public static TextArea output=null;
	
	//LOGS
	public static void msg(String data) {
		if (Debug.logState == LogState.DEBUG) {
			if (Debug.output == null) System.out.println(data);
			else Debug.output.append(data + "\n");
		}
	}
	
	public static void error(String data) {
		if (Debug.logState == LogState.DEBUG) {
			data="[ERROR] " + data;
			if (Debug.output == null) System.err.println(data);
			else Debug.output.append(data + "\n");
		}
	}
	
	public static void dump(String id,String data) {
		if (Debug.logState == LogState.DEBUG) {
			Debug.msg("[" + id + "] " + data);
		}
	}
	
	public static void dumpStateServer(String id,String data,ServerObject server,Client_itf writer,ArrayList<Client_itf> readers,ServerObject.LockState state) {
		try {
			String d="";
			for(Client_itf a : readers)
			d+=a.get_debugId();
			Debug.dump(id,data + " (" + ((!server.writerExists())?"0 writer":("writer: " + writer.get_debugId())) + ", " + ((d.equals(""))?"no reader":"readers: ") + d + "), (state: " + state + ")");
		}
		catch(RemoteException e) { e.printStackTrace(); }
	}
	
	public static void exit(String data) {
		Debug.msg(data);
		Debug.msg("[Fin du processus]");
		//System.exit(0);
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
