import java.io.*;
import java.net.*;

public class udpserver {
    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(args[0]))) {
            System.out.println("Server is running and waiting for connections...");

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println("Received request from client " + clientAddress + ":" + clientPort + ": " + request);

                // Create a new thread to handle the request for this client
                Thread clientHandler = new Thread(new udpserverhandler(serverSocket, clientAddress, clientPort, request));
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
