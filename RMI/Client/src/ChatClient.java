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

    public static void main(String[] args) {
        ChatClient client = null;
        try {
            client = new ChatClient();
            server = (ChatServerInt) Naming.lookup("rmi://"+"localhost"+"/myabc");
            server.connect(client);
            Scanner scan = new Scanner(System.in);
            run = true;
            while (run) {
                String msg = scan.nextLine();
                if (msg.length() == 0) {
                    continue;
                }
                if (msg.charAt(0) == '/') {
                    if (msg.equals("/quit")) {
                        server.quit(client);
                        shutdown();
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
        } catch (RemoteException re) {
            System.err.println(re.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            shutdown();
        }
    }
}
