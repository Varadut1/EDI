
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
    static private DatagramSocket udpsocket;
    static private DatagramPacket receivePacket;
    static private DatagramPacket sendPacket; 
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        try{
            clientSocket = new DatagramSocket();
            serverAddress = InetAddress.getByName("192.168.0.104");

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
            System.out.println("Here");
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
        // try{
        //     File directoryPath = new File("serverfiles");
        //     File filesList[] = directoryPath.listFiles();
        //     System.out.println("filesfound");
        //     if (filesList != null) {
        //         byte[][] names = new byte[filesList.length][1024];
        //         int i = 0;
        //         for (File file : filesList){
        //             if (file.isFile()) {
        //                 try {
        //                     byte[] fileName = file.getName().getBytes("UTF-8");
        //                     System.out.println(file.getName());
        //                     names[i++] = fileName;
        //                 } 
        //                 catch (Exception e) {
        //                     e.printStackTrace();
        //                 }
        //             }
        //         }
        //         System.out.println("Enter file to send: ");
        //         String filename = input.next();
        //         String[] header = new String[4];
        // header[0] = "."+filename.split("\\.")[1];
        // File file = new File("serverfiles/"+filename);
        // long fileSizeInBytes;
        // if(file.exists()){
        //     fileSizeInBytes = file.length();
        //     header[1] = fileSizeInBytes+"";
        //     header[2] = "B";
        //     header[3] = filename;
        //     System.out.println("Initial information: ");
        //     System.out.println("Extension: " + header[0]+" File size: "+header[1]+header[2]);

        //     ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
        //     try {
        //         // for (String str : header) {
        //         //     byte[] strBytes = str.getBytes("UTF-8"); 
        //         //     baos.write(strBytes);
        //         // }
        //         byte[] filenameb = filename.getBytes();
        //         DatagramPacket newpacket = new DatagramPacket(filenameb, filenameb.length, serverAddress, port);
        //         udpsocket.send(newpacket);
        //         byte[] newheaders = baos.toByteArray();
        //         sendPacket = new DatagramPacket(newheaders, newheaders.length, InetAddress.getByName("192.168.0.104"), port);
        //         udpsocket.send(sendPacket);
        //         byte[] fileData = Files.readAllBytes(Paths.get("clientfiles/"+filename));
            
        //     int packetSize = 1024; // Adjust the packet size as needed
        //     int totalPackets = (fileData.length + packetSize - 1) / packetSize;
        //     for (int packetNumber = 0; packetNumber < totalPackets; packetNumber++) {
        //         int offset = packetNumber * packetSize;
        //         int length = Math.min(packetSize, fileData.length - offset);
        //         byte[] packetData = new byte[length];
        //         System.arraycopy(fileData, offset, packetData, 0, length);
                
        //         DatagramPacket packet = new DatagramPacket(packetData, length,  serverAddress, port);
        //         udpsocket.send(packet);
                
        //         System.out.println("Sent packet " + (packetNumber + 1) + " of " + totalPackets);
                
        //         // Add a delay or sleep if needed to control the sending rate
        //         // Thread.sleep(10); // Milliseconds
        //     }
        //     } 
        //     catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // }
        // else{
        //     System.out.println("File not found");
        //     return;
        // }
        //     }
        // }
        // catch(Exception e){
        //     e.printStackTrace();
        // }
    }
}




