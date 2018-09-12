import java.io.*;
import java.net.*;

public class OneAServer {

    private static DatagramSocket socket;

    static class Host {
        int port;
        InetAddress address;
        long time;

        public void timestamp() {
            this.time = System.currentTimeMillis();
        }

        public Host(InetAddress address, int port) {
            this.address = address;
            this.port = port;
            timestamp();
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
        String secretLower = secretWord.toLowerCase();
        guess = Character.toLowerCase(guess);
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretLower.charAt(i) == guess) {
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

        String secretWord = null;
        try {
            if ( !args[0].matches("[a-zA-Z]+"))
                throw new IllegalArgumentException();
            secretWord = args[0];
            socket = new DatagramSocket(Integer.parseInt(args[1]));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        boolean run = true;
        Host curr;
        Host prev = null;
        int remaining = 0;

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

                if (prev != null && !prev.address.equals(curr.address)) {
                    if (curr.time - prev.time < 10000) {
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
                            prev = curr;
                            prev.timestamp();

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
                            prev.timestamp();
                        } else {
                            response("ERROR 2", curr.address, curr.port);
                            state = State.Ready;
                        }
                    } break;

                    case WaitingForGuess: {
                        if (recv.length() == 7 && recv.substring(0, 5).equals("GUESS") && remaining > 0) {
                            char guess = recv.charAt(6);
                            if (!guess(secretWord, guess, output)) {
                                remaining--;
                            }
                            String reply = new String(output) + " " + remaining;
                            response(reply, curr.address, curr.port);
                            if (isComplete(output) || remaining == 0) {
                                state = State.Ready;
                            }
                            prev.timestamp();
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
            }
        }
        catch(NumberFormatException ex){
            System.out.println("Please provide a port number");
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