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

    static class BusyException extends Exception{
        BusyException(){
            super();
        }
    }

    public static void main(String[] args) throws Exception {

        Scanner scan = new Scanner(System.in);

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
            System.out.println("Welcome to Guess the Word! Connect to the server by sending a HELLO");
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
            System.out.println("Response: " + receivedResponse);
            if (receivedResponse.equals("OK")) {
                run = true;
            } else if (receivedResponse.equals("BUSY")) {
                throw new BusyException();
            }

            //send "START" packet
            sendStart = scan.nextLine();
            byte[] sendStartBuffer = sendStart.getBytes();
            packet = new DatagramPacket(sendStartBuffer, sendStartBuffer.length, address, 4445);
            socket.send(packet);

            //recieve "READY x" packet
            byte[] ready = new byte[4096];
            packet = new DatagramPacket(ready, ready.length);
            socket.receive(packet);

            receivedWord = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Response: " + receivedWord);

            if (receivedWord.substring(0,5).equals("ERROR")){
                System.out.println(receivedWord);
                throw new Exception();
            }

            while (run){

                //input & send the guess
                gameInput = scan.nextLine();
                byte[] guessBuffer = gameInput.getBytes();
                packet = new DatagramPacket(guessBuffer, guessBuffer.length, address, 4445);
                socket.send(packet);

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

                /* Example
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
                }*/
            }
        } catch (BusyException e){
            System.out.println("Server is busy, exiting");
            e.printStackTrace();
        } catch (SocketTimeoutException e){
            System.out.println("Response from server time out");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("ERROR exception caught here 1");
            e.printStackTrace();
        } catch (Exception e){
            System.out.println("ERROR EXCEPTION CAUGHT HERE 2");
            e.printStackTrace();
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
