package RMI;

import java.rmi.*;
import java.util.*;

public interface ChatServerInt extends Remote {

    void message(ChatClientInt client, String msg) throws RemoteException;
    void command(String cmd) throws RemoteException;
    void connect(ChatClientInt client) throws RemoteException;
    void disconnect(ChatClientInt client) throws RemoteException;

    // OLD STUFF

    public boolean login (ChatClientInt a) throws RemoteException;
    public void publish (String s) throws RemoteException;
    public Vector getConnected() throws RemoteException;


}