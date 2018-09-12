import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer extends Thread {

    private static int default_port;
    private static ServerSocket serverSocket = null;
    private static final String DEFAULT_NICKNAME = "Unknown";
    private static List<Client> clients;

    public static class Client {
        private static long counter = 0;
        final long id;
        String nickname;
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        public Client() {
            counter++;
            id = counter;
            nickname = DEFAULT_NICKNAME + " " + id;
        }

    }

    private static synchronized void broadcast(String msg) {
        for (Client client: clients) {
            client.out.println(msg);
        }
    }

    private static synchronized void addClient(Client client) {
        broadcast(client.nickname + " has connected.");
        clients.add(client);
        System.out.println("Client connected (" + client.id + ")");
    }

    private static synchronized void removeClient(Client client) {
        clients.remove(client);
        System.out.println("Client disconnected (" + client.id + ")");
        broadcast(client.nickname + " has disconnected.");
    }

    private static synchronized void showAllClientsResponse(Client client) {
        StringBuilder sb = new StringBuilder("Users:");
        for (Client c : clients) {
            sb.append("\n").append(c.nickname);
        }
        client.out.println(sb.toString());
    }

    private static synchronized void showCommandsResponse(Client client) {
        client.out.println("/quit\n" + "/users\n" +  "/whoami\n" + "/nick <nickname>\n" + "/help");
    }

    private static synchronized void response(Client client, String msg) {
        client.out.println(msg);
    }

    private static synchronized boolean isNickAvailable(String nickname) {
        String lowerCaseNickname = nickname.toLowerCase();
        for (Client c: clients) {
            if (c.nickname.toLowerCase().equals(lowerCaseNickname)) {
                return false;
            }
        }
        return true;
    }

    private static synchronized void nicknameResponse(Client client, String received) {
        if (received.length() < 7) {
            response(client, "No nickname specified.");
            return;
        }
        String nickname = received.substring(6);
        if (isNickAvailable(nickname)) {
            String prev = client.nickname;
            client.nickname = nickname;
            broadcast("'" + prev + "' changed nickname to '" + nickname + "'.");
        } else {
            response(client, "Nickname is already in use.");
        }
    }

    public void run() {
        Client client = new Client();
        try {
            System.out.println("Waiting for client...");
            client.socket = serverSocket.accept();
            addClient(client);
            new ChatServer().start();
        } catch (IOException e) {
            System.err.println("Failed to accept client.");
            new ChatServer().start();
            return;
        }
        try {
            client.in = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
            client.out = new PrintWriter(client.socket.getOutputStream(), true);

            client.out.println("Welcome to the Chat Server!");

            while (true) {
                String received = client.in.readLine();
                System.out.println("Client " + client.id + ": '" + received + "'");
                if (received.length() == 0) {
                    continue;
                }
                if (received.charAt(0) == '/') {
                    if (received.equals("/quit")) {
                        removeClient(client);
                        response(client, "Quiting...");
                        break;
                    }
                    else if (received.equals("/whoami")) {
                        response(client, "You are '" + client.nickname + "'.");
                    }
                    else if (received.equals("/users")) {
                        showAllClientsResponse(client);
                    }
                    else if (received.length() >= 5 && received.substring(0, 5).equals("/nick")) {
                        nicknameResponse(client, received);
                    }
                    else if (received.equals("/help")) {
                        showCommandsResponse(client);
                    }
                    else {
                        response(client, "Unknown command.");
                    }
                } else {
                    broadcast(client.nickname + ": " + received);
                }
            }
        } catch (IOException e) {
            removeClient(client);
            System.err.println(e.getMessage());
        } finally {
            try {
                if (client.in != null)
                    client.in.close();
                if (client.out != null)
                    client.out.close();
                if (client.socket != null)
                    client.socket.close();
                System.out.println("Terminating thread...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Socket socket = null;
        try {
            default_port = Integer.parseInt(args[0]);
            serverSocket = new ServerSocket(default_port);
            clients = Collections.synchronizedList(new ArrayList<>());
            new ChatServer().start();
            socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            System.out.println("Chat server started on: " + socket.getLocalAddress() + ":" + default_port);
            socket.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            try {
                if (socket != null)
                    socket.close();
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.exit(1);
        }
    }
}
