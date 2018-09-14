import RMI.ChatClientInt;
import RMI.ChatServerInt;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ChatClient extends UnicastRemoteObject implements ChatClientInt {

    private static ChatServerInt server;
    private static boolean run;

    private String name;

    public ChatClient (String name) throws RemoteException {
        this.name = name;
    }

    @Override
    public void response(String msg) throws RemoteException {
        System.out.println(msg);
    }

    @Override
    public String getName() throws RemoteException{
        return name;
    }

    public static void main(String[] args) {
        try {
            ChatClient client = new ChatClient("Tiago");
            server =(ChatServerInt) Naming.lookup("rmi://"+"localhost"+"/myabc");
            server.connect(client);
            run = true;
            Scanner scan = new Scanner(System.in);
            while (run) {
                String msg = scan.next();
                if (msg.length() == 0) {
                    continue;
                }
                if (msg.charAt(0) == '/') {
                    if (msg.equals("/quit")) {
                        System.out.println("command quit");
                    }
                    else if (msg.equals("/whoami")) {
                        System.out.println("Command whoami");
                    }
                    else if (msg.equals("/users")) {
                        System.out.println("command users");
                    }
                    else if (msg.length() >= 5 && received.substring(0, 5).equals("/nick")) {
                        System.out.println("command nickname");
                    }
                    else if (msg.equals("/help")) {
                        System.out.println("command help");
                    }
                    else {
                        response(client, "Unknown command.");
                    }
                }
                else {
                    server.message(client, msg);
                }
            }
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
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
