 import RMI.ChatClientInt;
import RMI.ChatServerInt;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ChatServer  extends UnicastRemoteObject implements ChatServerInt {

    private List<ChatClientInt> clients;
    private int counter = 0;
    private static final String DEFAULT_NAME = "Unknown";

    public ChatServer() throws RemoteException{
        clients = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public synchronized void message(ChatClientInt client, String msg) {
        System.out.println("Message: " + msg);
        try {
            client.response("Response: " + msg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            clients.remove(client);
        }
    }

    private synchronized boolean isNickAvailable(String nick) {
        String lowerCaseNickname = nick.toLowerCase();
        Iterator itr = clients.iterator();
        while (itr.hasNext()) {
            ChatClientInt c = (ChatClientInt) itr.next();
            try {
                if (c.getName().toLowerCase().equals(lowerCaseNickname)) {
                    return false;
                }
            } catch (Exception e) {
                clients.remove(c);
            }
        }
        return true;
    }

    public synchronized void nickname(ChatClientInt client, String nick) throws RemoteException {
        try {
            if (isNickAvailable(nick)) {
                client.setName(nick);
                client.response("Nickname changed to: " + nick);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            clients.remove(c);
        }
    }

    @Override
    public synchronized void help(ChatClientInt client) throws RemoteException {
        client.response ("Commands:\n" + "/quit\n" + "/users\n" +  "/whoami\n" + "/nick <nickname>\n" + "/help");
    }

    @Override
    public synchronized void users(ChatClientInt client) throws RemoteException {
        client.response ("Users...\nA...");
    }

    @Override
    public void quit(ChatClientInt client)  throws RemoteException {
        disconnect(client);
    }

    @Override
    public synchronized void connect(ChatClientInt client) throws RemoteException {
        System.out.println(client + " got connected...");
        client.response("Welcome to the RMI Chat Server!");
        client.setName(DEFAULT_NAME + " " + ++counter);
        clients.add(client);
    }

    @Override
    public synchronized void disconnect(ChatClientInt client) throws RemoteException {
        // String name
        System.out.println(client + " got disconnected...");
        clients.remove(client);
        for (ChatClientInt localClient : clients) {
            localClient.response(client.getName() + " has disconnected");
        }
    }

    public static void main(String[] args) {
        try {
            //System.setSecurityManager(new RMISecurityManager());
            java.rmi.registry.LocateRegistry.createRegistry(1099);

            ChatServerInt server = new ChatServer();
            Naming.rebind("rmi://localhost/myabc", server);
            System.out.println("Chat Server is ready.");
        }catch (Exception e) {
            System.out.println("Chat Server failed: " + e.getMessage());
        }
    }
}
