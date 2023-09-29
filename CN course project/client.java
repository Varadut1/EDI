
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class client {
    // private static final int SERVER_PORT = 12345;
    // private static final String SERVER_IP = "localhost";
    static DatagramSocket clientSocket;
    static InetAddress serverAddress;
    static FileOutputStream fos;
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        try{
            clientSocket = new DatagramSocket();
            serverAddress = InetAddress.getByName("192.168.43.218");

            // Prompt the user to choose between sending or receiving a file
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Choose an option:");
            System.out.println("1. Receive a file");
            System.out.println("2. Send a file");
            System.out.print("Enter your choice (1/2): ");
            String choice = reader.readLine();

            // Send the user's choice to the server
            byte[] choiceBytes = choice.getBytes();
            DatagramPacket choicePacket = new DatagramPacket(choiceBytes, choiceBytes.length, serverAddress, Integer.parseInt(args[0]));
            clientSocket.send(choicePacket);

            if (choice.equals("1")) {
                // Logic for sending a file
                // Implement the file sending logic here
                System.out.println("receiving");
                receiveviaudp(Integer.parseInt(args[0]));
            } else if (choice.equals("2")) {
                System.out.println("sending");
                sendviaudp(Integer.parseInt(args[0]));
            } else {
                System.out.println("Invalid choice. Please choose 1 or 2.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void receiveviaudp(int port){
        System.out.println("receiving via udp");
        try{
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            System.out.println("Here");
            // Deserialize the received data
            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
            ObjectInputStream ois = new ObjectInputStream(bais);
            byte[][] names = (byte[][]) ois.readObject();
            int n = 1;
            for(byte[] i : names){
                System.out.println((n++)+". "+new String(i, "UTF-8"));
            }
            System.out.print("Enter the name of the file: ");
            String file = input.nextLine();
            byte[] filename = file.getBytes();
            DatagramPacket newpacket = new DatagramPacket(filename, filename.length, serverAddress, port);
            clientSocket.send(newpacket);

    
            byte[] headerData = new byte[1024];  // Adjust the buffer size as needed
            DatagramPacket headerPacket = new DatagramPacket(headerData, headerData.length);
            clientSocket.receive(headerPacket);
            
            // Extract the header data as a string
            String headerInfo = new String(headerPacket.getData(),0, headerPacket.getLength(), "UTF-8");
            String[] header = headerInfo.split(" ");

            for(String i : header){
                System.out.println(i);
            }
            System.out.println(header[0]);
            byte[] receivedData = new byte[1024]; // Adjust the buffer size as needed
            DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            while (true) {
                clientSocket.receive(receivedPacket);
                byte[] packetData = receivedPacket.getData();
                outputStream.write(packetData, 0, receivedPacket.getLength());
                
                // Check for end-of-file marker or other termination conditions
                if (receivedPacket.getLength() < receivedData.length) {
                    break;
                }
            }
            
            // Save the received data to a file
            byte[] fileData = outputStream.toByteArray();
            System.out.print("Enter the the name of file you would like to save:- ");

            File folder = new File("clientfiles");
            // Check if the folder exists; if not, create it
            if (!folder.exists()) {
                folder.mkdirs(); 
            }

            fos = new FileOutputStream(new File(folder, input.next()+"."+file.split("\\.")[1]));
            fos.write(fileData);
        }
        catch(Exception e){
            e.printStackTrace(); 
        }
    }
    static void sendviaudp(int port){
        System.out.println("sending via udp");
    }
}
