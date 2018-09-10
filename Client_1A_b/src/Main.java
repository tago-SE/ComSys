import java.io.IOException;
import java.net.*;
import java.util.Scanner;


import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Main {

    public static boolean isComplete(String output) {
       return !output.contains("*");
    }

    public static void main(String[] args) throws IOException {

        Scanner scan = new Scanner(System.in);

        int guesses;
        String sendHello, receivedResponse, gameInput, sendStart, receivedWord;
        boolean run = false;
        DatagramSocket socket = null;

        // get a datagram socket
        try {
            socket = new DatagramSocket();
        } catch (SocketException e1) {
            e1.printStackTrace();
            System.exit(1);
        }

        try {

            socket.setSoTimeout(3000);
            // send request
            sendHello = scan.nextLine();
            byte[] buffer = sendHello.getBytes();
            InetAddress address = InetAddress.getByName("localhost"); //change to commandline arguments?
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
            socket.send(packet);

            // get response
            byte[] response = new byte[4096];
            packet = new DatagramPacket(response, response.length);
            socket.receive(packet);

            receivedResponse = new String(packet.getData(), 0, packet.getLength());
            if (receivedResponse.equals("OK")) {
                run = true;
                System.out.println("Run set to true");
            } else if (receivedResponse.equals("BUSY")) {
                System.exit(1);
            }

            //send "START" packet
            sendStart = scan.nextLine();
            byte[] sendStartBuffer = sendStart.getBytes();
            packet = new DatagramPacket(sendStartBuffer, sendStartBuffer.length);
            socket.send(packet);

            //recieve "READY x" packet
            byte[] ready = new byte[4096];
            packet = new DatagramPacket(ready, ready.length);
            socket.receive(packet);

            receivedWord = new String(packet.getData(), 0, packet.getLength());

            if (receivedWord.substring(0,5).equals("ERROR")){
                System.out.println(receivedWord);
                System.exit(1);
            }

            guesses = receivedWord.charAt(receivedWord.length() - 1) * 2;

            while (run){

                //input & send the guess
                gameInput = scan.nextLine();
                byte[] guessBuffer = gameInput.getBytes();
                packet = new DatagramPacket(guessBuffer, guessBuffer.length);
                socket.send(packet);
                guesses--;          // server sends you remaining guesses. Unecessary.

                //receive result of the progress
                byte[] resultBuffer = new byte[4096];
                packet = new DatagramPacket(resultBuffer, resultBuffer.length);
                socket.receive(packet);
                //unsure on how to handle the result, (strings? char array? etc)

                // Results have the following form:
                //
                // a**** 5 // 5 being the remaining guesses, if 0 you've lost. To extract it:
                // output = recv.substring(0, secretWord.length())
                // remaining = Integer.parseInt(recv.charAt(

                // Example
                int secretWordLength = 5;   // Should be declared after HELLO or so
                String recv = "a**** 5";
                String output = recv.substring(0, secretWordLength);
                int remaining = Integer.parseInt(recv.substring(secretWordLength + 1, secretWordLength + 2));
                System.out.println("remaining: " + remaining);
                System.out.println("output: " + output);
                if (remaining == 0) {
                    if (isComplete(output)) {
                        System.out.println("You've won!");
                    }
                    else {
                        System.out.println("You've lost");
                    }
                }

            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        finally{
            try{
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
