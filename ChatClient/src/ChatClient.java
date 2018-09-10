import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLOutput;

public class ChatClient {

    private static Socket socket = null;
    private static BufferedReader in = null;
    private static BufferedReader userInput = null;
    private static PrintWriter out = null;
    private static String serverAddress;
    private static int port;
    private static boolean run;

    public static class Reader extends Thread {
        public void run() {
            while (true) {
                try {
                    String line = in.readLine();
                    if (line != null) {
                        System.out.println(line);
                    }
                    else
                        throw new IOException("NullPointerException");
                } catch (IOException e) {
                    System.err.println("Reader: " + e.getMessage());
                    run = false;
                    return;
                }
            }
        }
    }

    public static class Writer extends Thread {
        public void run() {
            while (true) {
                try {
                    if (userInput.ready()) {
                        out.println(userInput.readLine());
                    }
                } catch (IOException e) {
                    System.err.println("Writer: " + e.getMessage());
                    run = false;
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid argument.");
            System.exit(1);
        }

        serverAddress = args[0];
        port = Integer.parseInt(args[1]);
        socket = null;
        in = null;
        out = null;
        Writer writer = null;
        Reader reader = null;

        try {

            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userInput = new BufferedReader(new InputStreamReader(System.in));
            run = true;

            (writer = new Writer()).start();
            (reader = new Reader()).start();

            while (run) {
                try {
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (writer != null)
                    writer.interrupt();
                if (reader != null)
                    reader.interrupt();

                if (socket != null)
                    socket.close();
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (userInput != null)
                    userInput.close();

                System.out.println("Client shutdown.");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
