package src;

public interface Client_itf extends java.rmi.Remote {
    public Object reduce_lock(int id) throws java.rmi.RemoteException;
    public void invalidate_reader(int id) throws java.rmi.RemoteException;
    public Object invalidate_writer(int id) throws java.rmi.RemoteException;
    public String get_debugId() throws java.rmi.RemoteException;
}
