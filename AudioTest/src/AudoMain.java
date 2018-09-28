import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class AudoMain {

    public static void main(String[] args) {
        try {
            AudioStreamUDP audio = new AudioStreamUDP();
            int localPort = audio.getLocalPort();
            Socket socket;
            (socket = new Socket()).connect(new InetSocketAddress("google.com", 80));

            System.out.println("Server started on: " + socket.getLocalAddress() + ":" + localPort);
            Scanner scan = new Scanner(System.in);
            System.out.print("Enter address: ");
            String address = scan.nextLine();
            System.out.print("Enter port: ");
            String port = scan.nextLine();

            audio.connectTo(InetAddress.getByName(address), Integer.parseInt(port));
            System.out.println("Connection established...");

            audio.startStreaming();
            Thread.sleep(10000);
            audio.stopStreaming();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
