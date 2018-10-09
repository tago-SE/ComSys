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

    @Override
    public void response(String msg) throws RemoteException {
        System.out.println(msg);
    }

    @Override
    public void shutdown() {
        System.out.println("Shutting down...");
        System.exit(0);
    }



    @Override
    public boolean isAlive() {
        return true;
    }

    public static void main(String[] args) {
        ChatClient client = null;
        try {
            client = new ChatClient();
            server = (ChatServerInt) Naming.lookup("rmi://localhost/myabc");
            server.connect(client);
            Scanner scan = new Scanner(System.in);
            run = true;

             new Thread(() -> {
                while (run) {
                    try {
                        Thread.sleep(100);
                        if (run)
                            server.isAlive();
                    } catch (Exception e) {
                        run = false;
                        System.out.println("Server died.");
                    }
                }
            }).start();

            while (run) {
                String msg = scan.nextLine();
                if (!run)
                    break;
                if (msg.length() == 0) {
                    continue;
                }
                if (msg.charAt(0) == '/') {
                    if (msg.equals("/quit")) {
                        server.quit(client);
                    }
                    else if (msg.equals("/whoami")) {
                        server.whoami(client);
                    }
                    else if (msg.equals("/users")) {
                        server.users(client);
                    }
                    else if (msg.length() >= 5 && msg.substring(0, 5).equals("/nick")) {
                        if (msg.length() < 7) {
                            System.err.println("No nickname specified.");
                            continue;
                        }
                        server.nickname(client, msg.substring(6));
                    }
                    else if (msg.equals("/help")) {
                        server.help(client);
                    }
                    else {
                        System.err.println("Unknown command.");
                    }
                }
                else {
                    server.message(client, msg);
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                System.out.println("Shutting down...");
                server.disconnect(client);
            } catch (Exception ee) {}
            System.exit(1);
        }
    }
}
