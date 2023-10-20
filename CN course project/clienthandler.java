import java.net.*;
import java.io.*;
import java.util.*;

public class clienthandler implements Runnable{
    //TCP
    private static ServerSocket serversocket;
    private static Socket clientsocket;
    private static ObjectInputStream receive;
    private static ObjectOutputStream send;

    private static FileOutputStream fos;
    private static FileInputStream fis;

    private static Scanner input;
    private static int mtu;
    private static int tcpHeaderSize = 20;

    private static int port; 

    public clienthandler(Socket socket, int port, ServerSocket serversocket) {
        this.clientsocket = socket;
        this.port = port;
        this.serversocket = serversocket;
    }

    @Override
    public void run(){
        input = new Scanner(System.in);
        try{
            receive = new ObjectInputStream(clientsocket.getInputStream());
            send = new ObjectOutputStream(clientsocket.getOutputStream());
            try {
                NetworkInterface networkInterface = NetworkInterface.getByName("wlan1"); // Replace with your network interface name
                if (networkInterface != null) {
                    mtu = networkInterface.getMTU();
                    System.out.println("MTU: " + mtu);
                } else {
                    System.out.println("Network interface not found.");
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            
            int command = (int)receive.readObject();

            if(command == 1){
                sendviatcp(port);
            } 
            else if(command == 2){
                receiveviatcp(port);
            }
            else if(command ==3){
                deleteviatcp(port);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        input.close();

    }
    static void sendviatcp(int port){
        try{
            System.out.println("sending via tcp");
            File directoryPath = new File("serverfiles");
            File filesList[] = directoryPath.listFiles();
            System.out.println("filesfound");
            if (filesList != null) {
                String[] names = new String[filesList.length];
                int i = 0;
            
                for (File file : filesList) {
                    if (file.isFile()) {
                        try {
                            String fileName = file.getName();
                            // System.out.println(fileName);
                            names[i++] = fileName;
                        } 
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                send.writeObject(names);
            }
            String filepath = (String)receive.readObject();
            String[] header = new String[3];
            header[0] = "."+filepath.split("\\.")[1];
            File file = new File("serverfiles/"+filepath);
            long fileSizeInBytes;
            if(file.exists()){
                fileSizeInBytes = file.length();
                header[1] = fileSizeInBytes+"";
            }
            else{
                System.out.println("File not found");
                return;
            }
            header[2] = "B";
            System.out.println("Initial information: ");
            System.out.println("Extension: " + header[0]+" File size: "+header[1]+header[2]);
            long totalNumberOfPackets = ((fileSizeInBytes+((mtu-tcpHeaderSize)-1))/(mtu-tcpHeaderSize));
            long startTime = System.currentTimeMillis();
            send.writeObject(header);
            byte[] content = new byte[(int)file.length()];
            long endTime = System.currentTimeMillis();
            long total = endTime-startTime;
            System.out.println("Throughput: "+(fileSizeInBytes*8)/(total/1000.0));
            System.out.println("Latency: "+total/(double)totalNumberOfPackets);
            fis = new FileInputStream(file);
            fis.read(content);
            send.writeObject(content);
        }
        
        catch(Exception e){
            e.printStackTrace();
        }
    }
    void receiveviatcp(int port){
        System.out.println("receiving via tcp");
        try{
            System.out.println("receiving via tcp");
            String[] info = (String[])receive.readObject();
            for(String information : info){
                System.out.println(information);
            }
            byte[] content = (byte[])receive.readObject();

            File folder = new File("serverfiles");
            if (!folder.exists()) {
                folder.mkdirs(); 
            }
            fos = new FileOutputStream(new File(folder,info[3]+info[0]));
            fos.write(content);
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void deleteviatcp(int port){
        System.out.println("receiving via tcp");
        try{
            System.out.println("deleting via tcp");
            File directoryPath = new File("serverfiles");
            File filesList[] = directoryPath.listFiles();
            System.out.println("filesfound");
            if (filesList != null) {
                String[] names = new String[filesList.length];
                int i = 0;
            
                for (File file : filesList) {
                    if (file.isFile()) {
                        try {
                            String fileName = file.getName();
                            // System.out.println(fileName);
                            names[i++] = fileName;
                        } 
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                send.writeObject(names);
            }
            String filepath = (String)receive.readObject();
            String[] header = new String[3];
            header[0] = "."+filepath.split("\\.")[1];
            File file = new File("serverfiles/"+filepath);
            long fileSizeInBytes;
            if(file.exists()){
                file.delete();
            }
            else{
                System.out.println("File not found");
                return;
            }
            header[2] = "B";
            System.out.println("Initial information: ");
            System.out.println("Extension: " + header[0]+" File size: "+header[1]+header[2]+" deleted");
            // send.writeObject(header);

            // byte[] content = new byte[(int)file.length()];
            // fis = new FileInputStream(file);
            // fis.read(content);
            // send.writeObject(content);
        }
        
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
