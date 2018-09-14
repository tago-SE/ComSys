import RMI.ChatClientInt;
import RMI.ChatServerInt;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ChatClient extends UnicastRemoteObject implements ChatClientInt {

    private static ChatServerInt server = null;
    private static boolean run;

    public ChatClient () throws RemoteException { }

    private String name;

    @Override
    public void response(String msg) throws RemoteException {
        System.out.println(msg);
    }

    @Override
    public String getName() throws RemoteException{
        return name;
    }

    @Override
    public void setName(String name) throws RemoteException{
        this.name = name;
    }

    /*
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
     */

    public static void main(String[] args) {
        ChatClient client = null;
        try {
            client = new ChatClient();
            server = (ChatServerInt) Naming.lookup("rmi://"+"localhost"+"/myabc");
            server.connect(client);
            run = true;
            Scanner scan = new Scanner(System.in);

            while (run) {
                String msg = scan.nextLine();
                if (msg.length() == 0) {
                    continue;
                }
                if (msg.charAt(0) == '/') {
                    if (msg.equals("/quit")) {
                        server.quit(client);
                    }
                    /*else if (msg.equals("/whoami")) {
                        server.whoami(client);
                    }
                    */
                    else if (msg.equals("/users")) {
                        server.users(client);
                    }
                    else if (msg.length() >= 5 && msg.substring(0, 5).equals("/nick")) {
                        if (msg.length() < 7) {
                            System.out.println("No nickname specified.");
                            continue;
                        }
                        server.nickname(client, msg.substring(6));
                    }
                    else if (msg.equals("/help")) {
                        server.help(client);
                    }
                    else {
                        System.out.println("Unknown command.");
                    }
                }
                else {
                    server.message(client, msg);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (server != null) try {
                server.disconnect(client);
            } catch (Exception e2) {
                System.err.println(e2.getMessage());
                System.exit(1);
            }
        }
    }
}


/*
import RMI.ChatInterface;
import RMI.Hello;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    private static boolean run;
    private static int port;
    private static Writer writer = null;
    private static ChatInterface chatStub = null;

    public static class Writer extends Thread {
        public void run() {
            try {
                Scanner scan = new Scanner(System.in);
                while (run) {
                    String line = scan.next();
                    //chatStub.request(line);
                }
            } catch (Exception e) {
                run = false;
            }
        }
    }

    // How to configure host-ip?
    public static void main(String[] args) {
        run = true;
        try {
            port = Integer.parseInt(args[0]);
            Registry registry = LocateRegistry.getRegistry(port);
            chatStub = (ChatInterface) registry.lookup("Chat");
            (writer = new Writer()).start();

            // Listen for server responses
            while (run) {
               // String response = chatStub.response();
            }

        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
*/
