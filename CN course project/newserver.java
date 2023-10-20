import java.io.*;
import java.net.*;

public class newserver {
    public static void main(String[] args) {
        try {
            // Create a ServerSocket and bind it to a port
            InetAddress serverAddress = InetAddress.getByName("192.168.0.104"); // Replace with the desired IP address
            ServerSocket serversocket = new ServerSocket(Integer.parseInt(args[0]), 0, serverAddress); // Change the port as needed
            System.out.println("Server is running and waiting for connections...");
            
            while (true) {
                Socket clientSocket = serversocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                clienthandler clientHandler = new clienthandler(clientSocket, Integer.parseInt(args[0]), serversocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
