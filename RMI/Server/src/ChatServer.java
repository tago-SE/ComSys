import RMI.ChatClientInt;
import RMI.ChatServerInt;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ChatServer  extends UnicastRemoteObject implements ChatServerInt {

    private Vector v=new Vector();
    public ChatServer() throws RemoteException{}

    @Override
    public void message(ChatClientInt client, String msg) throws RemoteException {
        System.out.println("Message: " + msg);
        client.response("Response: " + msg);
    }

    @Override
    public void command(String cmd) throws RemoteException {
        System.out.println("Command: " + cmd);
    }

    @Override
    public void connect(ChatClientInt client) throws RemoteException {
        System.out.println(client + " got connected...");
        // Set name
        client.response("Welcome to the RMI Chat Server!");
    }

    @Override
    public void disconnect(ChatClientInt client) throws RemoteException {
        System.out.println(client + " got disconnected...");
    }

    public boolean login(ChatClientInt a) throws RemoteException{
        System.out.println(a.getName() + "  got connected....");
        a.tell("You have Connected successfully.");
        publish(a.getName()+ " has just connected.");
        v.add(a);
        return true;
    }

    public void publish(String s) throws RemoteException{
        System.out.println(s);
        for(int i=0;i<v.size();i++){
            try{
                ChatClientInt tmp=(ChatClientInt)v.get(i);
                tmp.tell(s);
            }catch(Exception e){
                //problem with the client not connected.
                //Better to remove it
            }
        }
    }

    public Vector getConnected() throws RemoteException{
        return v;
    }

    public static void main(String[] args) {
        try {
            //System.setSecurityManager(new RMISecurityManager());
            java.rmi.registry.LocateRegistry.createRegistry(1099);

            ChatServerInt b=new ChatServer();
            Naming.rebind("rmi://localhost/myabc", b);
            System.out.println("[System] Chat Server is ready.");
        }catch (Exception e) {
            System.out.println("Chat Server failed: " + e);
        }
    }
}
