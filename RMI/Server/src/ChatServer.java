 import RMI.ChatClientInt;
import RMI.ChatServerInt;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ChatServer  extends UnicastRemoteObject implements ChatServerInt {

    private List<ChatClientInt> clients;
    private Hashtable<ChatClientInt, ClientData> hash;


    private int counter = 0;
    private static final String DEFAULT_NAME = "Unknown";

    private class ClientData {
        String nick;
    }

    public synchronized void addClient(ChatClientInt client) {
        clients.add(client);
        ClientData data = new ClientData();
        data.name = DEFAULT_NAME + " " + ++counter;
        hash.put(client, data);
    }

    public synchronized void removeClient(ChatClientInt client) {
        clients.remove(client);
    }

    public ChatServer() throws RemoteException{
        clients = Collections.synchronizedList(new ArrayList<>());
    }

    private synchronized handleClientError(ChatClientInt client, Exception e) {
        System.err.println(e.getMessage());
        removeClient(client);
    }

    @Override
    public synchronized void message(ChatClientInt client, String msg) {
        try {
            client.response("Response: " + msg);
        } catch (Exception e) {
            handleClientError(client, e);
        }
    }

    private synchronized boolean isNickAvailable(String nick) {
        String lowerCaseNickname = nick.toLowerCase();
        Iterator itr = clients.iterator();
        while (itr.hasNext()) {
            ChatClientInt localClient = (ChatClientInt) itr.next();
            try {
                if (localClient.getName().toLowerCase().equals(lowerCaseNickname)) {
                    return false;
                }
            } catch (Exception e) {
                handleClientError(client, e);
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
            handleClientError(client, e);
        }
    }

    @Override
    public synchronized void help(ChatClientInt client) throws RemoteException {
        try {
            client.response ("Commands:\n" + "/quit\n" + "/users\n" + "/nick <nickname>\n" + "/help");
        } catch (Exception e) {
            handleClientError(client, e);
        }
    }

    @Override
    public synchronized void users(ChatClientInt client) throws RemoteException {
        StringBuilder sb = new StringBuilder("Users:\n");
        try {
            Iterator itr = clients.iterator();
            while (itr.hasNext()) {
                ChatClientInt localClient = (ChatClientInt) itr.next();
                try {
                    sb.append(localClient.getName() + "\n");
                } catch (Exception e) {
                    handleClientError(localClient, e);
                }
            }
            client.response(sb.toString());
        } catch (Exception e) {
            handleClientError(client, e);
        }
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
