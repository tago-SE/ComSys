import java.io.*;
import java.net.*;

public class Main {

    static class Host {
        int port;
        InetAddress address;
        long time;

        public Host(InetAddress address, int port) {
            this.address = address;
            this.port = port;
            this.time = System.currentTimeMillis();
        }
    }

    public static DatagramPacket createPacket(InetAddress ip, int port, String msg) throws UnsupportedEncodingException {
        byte[] data = msg.getBytes("UTF-8");
        return new DatagramPacket(data, data.length,ip, port);
    }

    public static char[] scrambleSecretWord(String secretWord) {
        String s = "";
        for (int i = 0; i < secretWord.length(); i++) {
            s += "*";
        }
        return s.toCharArray();
    }

    public static boolean guess(String secretWord,  char guess, char[] output) {
        boolean changed = false;
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == guess) {
                output[i] = guess;
                changed = true;
            }
        }
        return changed;
    }

    public static boolean isComplete(char[] output) {
        for (int i = 0; i < output.length; i++)
            if (output[i] == '*')
                return false;
        return true;
    }

    public static void main(String[] args) {

        if (args.length != 1 || !args[0].matches("[a-zA-Z]+")){
            (new IllegalArgumentException()).printStackTrace();
            System.exit(1);
        }

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(4445);
        } catch (SocketException e) {
            System.exit(1);
        }

        System.out.println("Server running...");

        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        boolean run = true;
        Host curr;
        Host prev = null;
        int guessCount = 0;
        int remaining = 0;

        String secretWord = args[0];
        char[] output = null;

        boolean waitingForHello = true;
        boolean waitingForStart = false;

        try {
            while (run) {
                socket.receive(packet);

                // Manage connections - one client at a time
                curr = new Host(packet.getAddress(), packet.getPort());
                if (prev != null && prev.address != curr.address) {
                    if (curr.time < prev.time + 10.000) {
                        socket.send(createPacket(curr.address, curr.port, "BUSY"));
                        continue; // Ignore client
                    }
                    else {
                        // Terminate previous session if a new connection is
                        // accepted and the timer has expired
                        socket.send(createPacket(prev.address, prev.port, "ERROR 1"));
                        waitingForHello = true;
                        waitingForStart = false;
                    }
                }

                String recv = new String(packet.getData(), 0, packet.getLength());

                if (waitingForHello) {
                    if (recv.equals("HELLO")) {
                        waitingForHello = false;
                        waitingForStart = true;
                        socket.send(createPacket(curr.address, curr.port, "OK"));
                    }
                    else {
                        socket.send(createPacket(curr.address, curr.port, "ERROR 2"));
                    }
                }
                else if (waitingForStart) {
                        if (recv.equals("START")) {
                            waitingForStart = false;
                            remaining = secretWord.length();

                            guessCount = 0;

                            output = scrambleSecretWord(secretWord);
                            socket.send(createPacket(curr.address, curr.port, "READY" + secretWord.length()));
                        }
                        else {
                            socket.send(createPacket(curr.address, curr.port, "ERROR 3"));
                        }
                }
                else if (recv.length() == 7 && recv.substring(0, 5).equals("GUESS") && remaining > 0) {
                    char guess = recv.charAt(6);
                    System.out.println("Guess received: " + guess);
                    if (!guess(secretWord, guess, output)) {
                        remaining--;
                        System.out.println("Incorrect. Remaining guesses: " + remaining);
                    }
                    else {
                        System.out.println("Correct. Remaining guesses: " + remaining);
                    }
                    socket.send(createPacket(curr.address, curr.port, new String(output) + " " + remaining));
                    if (isComplete(output) || remaining == 0) {
                        waitingForHello = true;
                        waitingForStart = false;
                        curr = null;
                    }
                }
                else {
                    socket.send(createPacket(curr.address, curr.port, "ERROR 5"));  // Invalid argument
                }
                prev = curr;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (Exception ex){
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
}