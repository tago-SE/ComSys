import java.io.*;
import java.net.*;

public class Main {

    private static DatagramSocket socket;

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

    enum State {
        Ready, WaitingForHello, WaitingForStart, WaitingForGuess;
    }

    /*
    public static DatagramPacket createPacket(InetAddress ip, int port, String msg) throws UnsupportedEncodingException {
        byte[] data = msg.getBytes("UTF-8");
        return new DatagramPacket(data, data.length,ip, port);
    }*/

    public static void response(String msg, InetAddress ip, int port) throws IOException {
        byte[] data = msg.getBytes("UTF-8");
        socket.send(new DatagramPacket(data, data.length,ip, port));
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
        int remaining = 0;

        String secretWord = args[0];
        char[] output = null;

        State state = State.Ready;

        try {
            while (run) {
                if (state == State.Ready) {
                    prev = null;
                    state = State.WaitingForHello;
                    System.out.println("State: " + state);
                }

                socket.receive(packet);

                // Manage connections - one client at a time
                curr = new Host(packet.getAddress(), packet.getPort());
                if (prev != null && prev.address != curr.address) {
                    if (curr.time < prev.time + 10.000) {
                        response("BUSY", curr.address, curr.port);
                        System.out.println("Ignored new client.");
                        continue; // Ignore client
                    }
                    else {
                        // Terminate previous session if a new connection is
                        // accepted and the timer has expired
                        response("DROPPED", prev.address, prev.port);
                        System.out.println("Previous client dropped due to timeout.");
                        state = State.WaitingForHello;
                        System.out.println("State: " + state);
                    }
                }

                String recv = new String(packet.getData(), 0, packet.getLength());

                switch (state) {

                    case WaitingForHello: {
                        if (recv.equals("HELLO")) {
                            state = State.WaitingForStart;
                            System.out.println("State: " + state);
                            response("OK", curr.address, curr.port);
                        } else {
                            response("ERROR 1", curr.address, curr.port);
                            state = State.Ready;
                        }
                    } break;

                    case WaitingForStart: {
                        if (recv.equals("START")) {
                            state = State.WaitingForGuess;
                            System.out.println("State: " + state);
                            remaining = secretWord.length();
                            output = scrambleSecretWord(secretWord);
                            response("READY" + secretWord.length(), curr.address, curr.port);
                        } else {
                            response("ERROR 2", curr.address, curr.port);
                            state = State.Ready;
                        }
                    } break;

                    case WaitingForGuess: {
                        if (recv.length() == 7 && recv.substring(0, 5).equals("GUESS") && remaining > 0) {
                            char guess = recv.charAt(6);
                            System.out.println("Guess received: " + guess);
                            if (!guess(secretWord, guess, output)) {
                                remaining--;
                            }
                            String reply = new String(output) + " " + remaining;
                            System.out.println("Response: " + reply);
                            response(reply, curr.address, curr.port);
                            if (isComplete(output) || remaining == 0) {
                                state = State.Ready;
                            }
                        } else {
                            response("ERROR 3", curr.address, curr.port);
                            state = State.Ready;
                        }
                    } break;

                    default: {
                        response("ERROR 4", curr.address, curr.port);
                        state = State.Ready;
                    }
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