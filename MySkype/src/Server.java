import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private int port;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;

    private PhoneState state;

    BufferedReader in ;
    PrintWriter out;

    boolean run = true;

    public Server(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        serverStartedMessage();
    }

    public void injectPhoneState(PhoneState state) {
        this.state = state;
    }

    private void serverStartedMessage() throws IOException{
        Socket socket = null;
        try {
            (socket = new Socket()).connect(new InetSocketAddress("google.com", 80));
            System.out.println(Strings.SERVER_STARTED_ON + socket.getLocalAddress() + ":" + this.port);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null)
                socket.close();
        }
    }

    public void close() {
        run = false;
    }

    @Override
    public void run() {
        while (run) {
            System.out.println(Strings.SERVER_WAITING_FOR_CLIENT);
            try {
                Socket socket = serverSocket.accept();
                if (clientSocket == null) { // or state is busy (calling, talking, hanging up)
                    // accept client
                    clientSocket = socket;
                    System.out.println("Server: Connection established");
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    for (;;) {
                        String msg = in.readLine();
                        switch (msg) {
                            case Protocol.INVITE: {
                                System.out.println("Server /invite");
                            } break;
                            case Protocol.TRO_ACK: {

                            } break;
                            case Protocol.BYE: {

                            } break;
                            case Protocol.BYE_ACK: {

                            } break;
                            default: {
                                System.err.println("Invalid request");
                            }
                        }
                    }

                    // State: Waiting for <Invite>
                }
                else {
                    // Send busy to socket
                }



            } catch (IOException e) {
                System.err.println(Strings.SERVER_ACCEPT_ERR);
            }
        }
    }
}