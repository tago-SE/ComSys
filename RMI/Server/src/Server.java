import RMI.Hello;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Hello {

    public static void main(String[] args) {
        try {
            Server server = new Server();

            Hello stub = (Hello) UnicastRemoteObject.exportObject(server, 0);
            // Bind the remote object's stub in the registry

           // Runtime.getRuntime().exec(new String[] {"start", "rmiregistry", "2001"});

           // Thread.sleep(1000);


            Registry registry = LocateRegistry.getRegistry(2001);



            //System.setProperty("java.rmi.server.hostname","localhost");
            registry.bind("Hello", stub);
            System.err.println("Server ready");
        }
        catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String sayHello() {
        return "Hello World!";
    }
}
