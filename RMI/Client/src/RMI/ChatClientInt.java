package RMI;

import java.rmi.*;

public interface ChatClientInt extends Remote{

    private String name;

    void response(String msg) throws RemoteException ;
    String getName() throws RemoteException;
    void setName(String name) throws RemoteException;
}
