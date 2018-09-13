package RMI;

import java.rmi.Remote;

public interface Chat extends Remote {

    String welcome();

    String message();

    String help();

    String whoami();

    String users();

    String nick();

    void quit();
}
