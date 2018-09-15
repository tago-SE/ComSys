 import RMI.ChatClientInt;
import RMI.ChatServerInt;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ChatServer  extends UnicastRemoteObject implements ChatServerInt {

    private int counter = 0;
    private List<ChatClientInt> clients;
    private Hashtable<ChatClientInt, ClientData> hash;
    private String[] commands = {"/quit", "/users", "/whoami", "/nick <nickname>", "/help" };

    private static final String DEFAULT_NAME = "Unknown";
    private static String helpInfo = null;

    private class ClientData {
        String nick;

        public ClientData(String nick) {
            this.nick = nick;
        }
    }

    public ChatServer() throws RemoteException {
        clients = Collections.synchronizedList(new ArrayList<>());
        hash = new Hashtable<>();
        if (helpInfo == null) {
            StringBuilder sb = new StringBuilder("Commands:\n");
            for (String cmd: commands) {
                sb.append(cmd + "\n");
            }
            helpInfo = sb.toString();
        }
    }

    public synchronized void addClient(ChatClientInt client) {
        String nickname = DEFAULT_NAME + " " + ++counter;
        clients.add(client);
        hash.put(client, new ClientData(nickname));
        broadcast(nickname + " has connected.");

    }

    public synchronized void removeClient(ChatClientInt client) {
        String nickname = (hash.remove(client)).nick;
        clients.remove(client);
        broadcast(nickname + " has disconnected.");
    }

    private synchronized void handleClientError(ChatClientInt client, Exception e) {
        System.err.println(e.getMessage());
        removeClient(client);
    }

    private synchronized boolean isNickAvailable(String nick) {
        String lowerCaseNick = nick.toLowerCase();
        for (ClientData data: hash.values()) {
            if (data.nick.toLowerCase().equals(lowerCaseNick)) {
                return false;
            }
        }
        return true;
    }

    private synchronized void broadcast(String msg) {
        Iterator itr = clients.iterator();
        while (itr.hasNext()) {
            ChatClientInt client = (ChatClientInt) itr.next();
            try {
                client.response(msg);
            } catch (Exception e) {
                handleClientError(client, e);
            }
        }
    }

    @Override
    public void message(ChatClientInt client, String msg) {
        broadcast(hash.get(client).nick + ": " + msg);
    }

    @Override
    public synchronized void whoami(ChatClientInt client) {
        try {
            client.response(hash.get(client).nick);
        } catch (Exception e) {
            handleClientError(client, e);
        }
    }

    @Override
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
            client.response (helpInfo);
        } catch (Exception e) {
            handleClientError(client, e);
        }
    }

    @Override
    public synchronized void users(ChatClientInt client) throws RemoteException {
        StringBuilder sb = new StringBuilder("Users:\n");
        try {
            for (ClientData data: hash.values()) {
                sb.append(data.nick + "\n");
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
        client.response("Welcome to the RMI Chat Server!");
        addClient(client);
    }

    @Override
    public synchronized void disconnect(ChatClientInt client) throws RemoteException {
        removeClient(client);
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
