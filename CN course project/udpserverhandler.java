import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Paths;

public class udpserverhandler implements Runnable {
    static private InetAddress clientAddress;
    static private int clientPort;
    private String request;
    static FileOutputStream fos;
    static private DatagramSocket udpsocket;
    static private DatagramPacket receivePacket;
    static private DatagramPacket sendPacket; 

    public udpserverhandler(DatagramSocket serverSocket, InetAddress clientAddress, int clientPort, String request) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.request = request;
        this.udpsocket = serverSocket;
    }

    @Override
    public void run() {
        if (request.equals("1")) {
            sendviaudp(clientPort);
        } else if (request.equals("2")) {
            receiveviaudp(clientPort);
        } else {
            System.out.println("Invalid request from client " + clientAddress + ":" + clientPort);
            sendfile(request);
        }
    }
    void sendfile(String filename){
        String[] header = new String[3];
        header[0] = "."+filename.split("\\.")[1];
        File file = new File("serverfiles/"+filename);
        long fileSizeInBytes;
        if(file.exists()){
            fileSizeInBytes = file.length();
            header[1] = fileSizeInBytes+"";
            header[2] = "B";
            System.out.println("Initial information: ");
            System.out.println("Extension: " + header[0]+" File size: "+header[1]+header[2]);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            try {
                for (String str : header) {
                    byte[] strBytes = str.getBytes("UTF-8"); 
                    baos.write(strBytes);
                }
                byte[] newheaders = baos.toByteArray();
                sendPacket = new DatagramPacket(newheaders, newheaders.length, InetAddress.getByName("192.168.0.104"), clientPort);
                udpsocket.send(sendPacket);
                byte[] fileData = Files.readAllBytes(Paths.get("serverfiles/"+filename));
            
            int packetSize = 1024; // Adjust the packet size as needed
            int totalPackets = (fileData.length + packetSize - 1) / packetSize;
            long startTime = System.currentTimeMillis();           ///
            for (int packetNumber = 0; packetNumber < totalPackets; packetNumber++) {
                int offset = packetNumber * packetSize;
                int length = Math.min(packetSize, fileData.length - offset);
                byte[] packetData = new byte[length];
                System.arraycopy(fileData, offset, packetData, 0, length);
                
                DatagramPacket packet = new DatagramPacket(packetData, length, clientAddress, clientPort);
                udpsocket.send(packet);
                
                System.out.println("Sent packet " + (packetNumber + 1) + " of " + totalPackets);
                
                // Add a delay or sleep if needed to control the sending rate
                // Thread.sleep(10); // Milliseconds
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Throughput: "+(fileSizeInBytes*8)/((endTime-startTime)/1000.0));
            System.out.println("Latency: "+(endTime-startTime)/(double)(totalPackets));
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("File not found");
            return;
        }
       
    }

    void receiveviaudp(int port){
        System.out.println("receiving via udp");
        Scanner input = new Scanner(System.in);
        try{
            // byte[] receiveData = new byte[1024];
            // DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            // System.out.println("Here");
            // udpsocket.receive(receivePacket);
            // System.out.println("Here");
            // // Deserialize the received data
            // ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
            // ObjectInputStream ois = new ObjectInputStream(bais);
            // byte[][] names = (byte[][]) ois.readObject();
            // int n = 1;
            // for(byte[] i : names){
            //     System.out.println((n++)+". "+new String(i, "UTF-8"));
            // }
            // System.out.print("Enter the name of the file: ");
            // String file = input.nextLine();
            // byte[] filename = file.getBytes();
            // DatagramPacket newpacket = new DatagramPacket(filename, filename.length, serverAddress, port);
            // udpsocket.send(newpacket);

    
            byte[] headerData = new byte[1024];  // Adjust the buffer size as needed
            DatagramPacket headerPacket = new DatagramPacket(headerData, headerData.length);
            udpsocket.receive(headerPacket);
            
            // // Extract the header data as a string
            String headerInfo = new String(headerPacket.getData(),0, headerPacket.getLength(), "UTF-8");
            String[] header = headerInfo.split(" ");
            String file = headerInfo;
            for(String i : header){
                System.out.println(i);
            }
            System.out.println(header[0]);
            byte[] receivedData = new byte[1024]; // Adjust the buffer size as needed
            DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            while (true) {
                udpsocket.receive(receivedPacket);
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

            File folder = new File("serverfiles");
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


    void sendviaudp(int port){
        System.out.println("sending via udp");
        try{
            File directoryPath = new File("serverfiles");
            File filesList[] = directoryPath.listFiles();
            System.out.println("filesfound");
            if (filesList != null) {
                byte[][] names = new byte[filesList.length][1024];
                int i = 0;
                for (File file : filesList){
                    if (file.isFile()) {
                        try {
                            byte[] fileName = file.getName().getBytes("UTF-8");
                            System.out.println(file.getName());
                            names[i++] = fileName;
                        } 
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(names);
                byte[] serializedData = baos.toByteArray();
                byte[] filename = new byte[1024];
                sendPacket = new DatagramPacket(serializedData, serializedData.length, InetAddress.getByName("192.168.0.104"), port);
                System.out.println("sending packet wait....");
                udpsocket.send(sendPacket);
                DatagramPacket newpack = new DatagramPacket(filename, filename.length);
                udpsocket.receive(newpack);
                System.out.println("Sending file " + new String(filename, "UTF-8"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
