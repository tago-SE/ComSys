package RMI;

import java.rmi.*;

public interface ChatClientInt extends Remote{

    public void response(String msg) throws RemoteException ;

    public String getName() throws RemoteException;
}
