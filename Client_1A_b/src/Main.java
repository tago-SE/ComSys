import java.io.IOException;
import java.net.*;
import java.util.Scanner;


import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Main {

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
                System.out.println(receivedWord.substring(0, 5));
                System.exit(1);
            }

            guesses = receivedWord.charAt(receivedWord.length() - 1) * 2;

            while (run){

                //input & send the guess
                gameInput = scan.nextLine();
                byte[] guessBuffer = gameInput.getBytes();
                packet = new DatagramPacket(guessBuffer, guessBuffer.length);
                socket.send(packet);
                guesses--;

                //receive result of the progress
                byte[] resultBuffer = new byte[4096];
                packet = new DatagramPacket(resultBuffer, resultBuffer.length);
                socket.receive(packet);
                //unsure on how to handle the result, (strings? char array? etc)


            }


        } catch (IOException ioExc){

            ioExc.printStackTrace();
            System.exit(1);
        }
        finally{

            try{
                socket.close();
            } catch (Exception ex){
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
}
