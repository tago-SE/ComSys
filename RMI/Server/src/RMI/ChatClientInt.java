package RMI;

import java.rmi.*;

public interface ChatClientInt extends Remote {

    void response(String msg) throws RemoteException ;
}
