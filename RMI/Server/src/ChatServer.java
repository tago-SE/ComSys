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
            StringBuilder sb = new StringBuilder("Commands:");
            for (String cmd: commands) {
                sb.append("\n" + cmd);
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
        try {
            client.shutdown();
        } catch (Exception e) { }
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

    private synchronized void unicast(ChatClientInt client, String msg) {
        System.out.println("unicast: " + msg);
        try {
            client.response(msg);
        } catch (Exception e) {
            handleClientError(client, e);
        }
    }

    private synchronized void broadcast(String msg) {
        System.out.println("broadcast: " + msg);
        ArrayList<ChatClientInt> removedClients = new ArrayList();
        ArrayList<Exception> exceptions = new ArrayList();
        for (ChatClientInt client : clients) {
            try {
                client.response(msg);
            } catch (Exception e) {
                removedClients.add(0, client);
                exceptions.add(0, e);
            }
        }
        for (int j = 0; j > removedClients.size(); j++) {
            handleClientError(removedClients.get(j), exceptions.get(j));
        }
    }

    @Override
    public void message(ChatClientInt client, String msg) {
        broadcast(hash.get(client).nick + ": " + msg);
    }

    @Override
    public synchronized void whoami(ChatClientInt client) {
        unicast(client, hash.get(client).nick);
    }

    @Override
    public synchronized void nickname(ChatClientInt client, String nick) throws RemoteException {
        if (isNickAvailable(nick)) {
            ClientData data = hash.get(client);
            String prevNick = data.nick;
            data.nick = nick;
            broadcast(prevNick + " changed nickname to " + nick + ".");
        } else {
            unicast(client, "Nickname is already in use.");
        }
    }

    @Override
    public synchronized void help(ChatClientInt client) throws RemoteException {
        unicast(client, helpInfo);
    }

    @Override
    public synchronized void users(ChatClientInt client) throws RemoteException {
        StringBuilder sb = new StringBuilder("Users:");
        for (ClientData data: hash.values()) {
            sb.append("\n" + data.nick);
        }
        unicast(client, sb.toString());
    }

    @Override
    public synchronized void quit(ChatClientInt client)  throws RemoteException {
        disconnect(client);
    }

    @Override
    public synchronized void connect(ChatClientInt client) throws RemoteException {
        addClient(client);
        unicast(client, "Welcome to the RMI Chat Server!");
    }

    @Override
    public synchronized void disconnect(ChatClientInt client) throws RemoteException {
        removeClient(client);
    }

    public static void main(String[] args) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            ChatServerInt server = new ChatServer();
            Naming.rebind("rmi://localhost/myabc", server);
            System.out.println("Chat Server is ready.");
        } catch (Exception e) {
            System.err.println("Chat Server failed: " + e.getMessage());
        }
    }
}
