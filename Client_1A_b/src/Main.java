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

    private static class ClientDisconnectedException extends Exception {
        ClientDisconnectedException(){ super();}
    }

    static class WrongInputException extends Exception{
        WrongInputException(){
            super();
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length != 2){
            System.out.println("Usage: java Main <host name> <port number>");
            System.exit(1);
        }

        Scanner scan = new Scanner(System.in);
        String IPAddress = args[0];
        int portNumber = Integer.parseInt(args[1]);

        String sendHello, receivedResponse, gameInput, sendStart, receivedWord, gameResult;
        int remaining;
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

            //set the timeout of the socket to 3 seconds
            socket.setSoTimeout(3000);

            // send request
            System.out.println("Welcome to Guess the Word! Connect to the server by sending a HELLO");
            sendHello = scan.nextLine();
            if (!sendHello.equals("HELLO")){
                throw new Exception();
            }
            byte[] buffer = sendHello.getBytes();
            InetAddress address = InetAddress.getByName(IPAddress); //change to commandline arguments?
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, portNumber);
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
            } else if (receivedResponse.equals("DROPPED"))
                throw new ClientDisconnectedException();

            //send "START" packet
            System.out.println("Connection established! Type START to begin!");
            sendStart = scan.nextLine();
            byte[] sendStartBuffer = sendStart.getBytes();
            packet = new DatagramPacket(sendStartBuffer, sendStartBuffer.length, address, portNumber);
            socket.send(packet);

            //recieve "READY x" packet
            byte[] ready = new byte[4096];
            packet = new DatagramPacket(ready, ready.length);
            socket.receive(packet);

            receivedWord = new String(packet.getData(), 0, packet.getLength());


            if (receivedWord.substring(0,5).equals("ERROR")){
                System.out.println("Response: " + receivedWord.substring(0,5));
                throw new Exception();
            } else if (receivedWord.equals("DROPPED"))
                throw new ClientDisconnectedException();
            else{
                System.out.println("Response: " + receivedWord);
            }

            while (run){

                //input & send the guess
                System.out.println("Make a guess (GUESS <letter>:");
                gameInput = scan.nextLine();
                byte[] guessBuffer = gameInput.getBytes();
                packet = new DatagramPacket(guessBuffer, guessBuffer.length, address, portNumber);
                socket.send(packet);

                //receive result of the progress
                byte[] resultBuffer = new byte[4096];
                packet = new DatagramPacket(resultBuffer, resultBuffer.length);
                socket.receive(packet);
                gameResult = new String(packet.getData(), 0, packet.getLength());
                if (gameResult.substring(0,5).equals("ERROR")){
                    System.out.println(gameResult.substring(0,5));
                    throw new WrongInputException();
                } else if (gameResult.equals("DROPPED"))
                    throw new ClientDisconnectedException();
                System.out.println(gameResult);
                remaining = Integer.parseInt(gameResult.substring(gameResult.length() - 1));
                //System.out.println("CHECKING REMAINING INT: " + remaining);

                if (remaining == 0){
                    System.out.println("You lose!");
                    run = false;
                }

                if (isComplete(gameResult)){
                        System.out.println("You win!");
                        run = false;
                }

            }
        }catch (WrongInputException e){
            System.out.println("Wrong guess input, exiting");
            e.printStackTrace();
        } catch (BusyException e){
            System.out.println("Server is busy, exiting");
            e.printStackTrace();
        } catch (ClientDisconnectedException e){
            System.out.println("You've been away for too long, disconnecting");
        } catch (SocketTimeoutException e){
            System.out.println("Response from server time out");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("Socket error! Exiting");
            e.printStackTrace();
        } catch (Exception e){
            System.out.println("You did not send a HELLO or a START, exiting");
            //e.printStackTrace();
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
