package RMI;

import java.rmi.*;
import java.util.*;

public interface ChatServerInt extends Remote {

    void message(ChatClientInt client, String msg) throws RemoteException;
    void nickname(ChatClientInt client, String nick) throws RemoteException;
    void whoami(ChatClientInt client) throws RemoteException;
    void help(ChatClientInt client) throws RemoteException;
    void users(ChatClientInt client) throws RemoteException;
    void quit(ChatClientInt client)  throws RemoteException;
    void connect(ChatClientInt client) throws RemoteException;
    void disconnect(ChatClientInt client) throws RemoteException;
    boolean isAlive() throws RemoteException;

}