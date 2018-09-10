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

    public static void response(String msg, InetAddress ip, int port) throws IOException {
        byte[] data = msg.getBytes("UTF-8");
        socket.send(new DatagramPacket(data, data.length,ip, port));
        System.out.println("Response: " + msg + " " + ip + ":" + port);
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
                    System.out.println("State: " + state);
                    state = State.WaitingForHello;
                    System.out.println("State: " + state);
                }

                socket.receive(packet);
                String recv = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + recv + " " + packet.getAddress() + ":" + packet.getPort());

                // Manage connections - one client at a time
                curr = new Host(packet.getAddress(), packet.getPort());
                if (prev != null && prev.address != curr.address) {
                    if (curr.time < prev.time + 10.000) {
                        response("BUSY", curr.address, curr.port);
                        continue; // Ignore client
                    }
                    else {
                        // Terminate previous session if a new connection is
                        // accepted and the timer has expired
                        response("DROPPED", prev.address, prev.port);
                        state = State.WaitingForHello;
                        System.out.println("State: " + state);
                    }
                }



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
                            System.out.println("Guess: " + guess);
                            if (!guess(secretWord, guess, output)) {
                                remaining--;
                            }
                            String reply = new String(output) + " " + remaining;
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