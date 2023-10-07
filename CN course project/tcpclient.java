import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class tcpclient {
    static Socket clientsocket;
    static ObjectOutputStream send;
    static ObjectInputStream receive;
    static Scanner input = new Scanner(System.in);
    public static void main(String[] args){
        InetAddress serverAddress;
        int port = Integer.parseInt(args[0]);
        try{
            serverAddress = InetAddress.getByName("192.168.0.103"); 
            clientsocket = new Socket(serverAddress, port);
            send = new ObjectOutputStream(clientsocket.getOutputStream());
            receive = new ObjectInputStream(clientsocket.getInputStream());
            System.out.println("1. Receive file\n2. Send file");
            int command = input.nextInt();
            send.writeObject(command);
            if(command == 1){
                receiveviatcp(port);
            }
            else if(command == 2){
                sendviatcp(port);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void sendviatcp(int port){
        try{
            
            String[] header = new String[4];
            
            File directoryPath = new File("clientfiles");
            File filesList[] = directoryPath.listFiles();
            System.out.println("filesfound");
            if (filesList != null) {
                String[] names = new String[filesList.length];
                int i = 0;
                for (File file : filesList) {
                    if (file.isFile()) {
                        try {
                            String fileName = file.getName();
                            System.out.println(fileName);
                            names[i++] = fileName;
                        } 
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            System.out.println("Enter the name of file to send");
            String filename = input.next();
            File file = new File("clientfiles/"+filename);
            header[0] = "."+filename.split("\\.")[1];
            long fileSizeInBytes;
            if(file.exists()){
                fileSizeInBytes = file.length();
                header[1] = fileSizeInBytes+"";
                header[2] = "B";
                header[3] = filename.split("\\.")[0];
                System.out.println("Initial information: ");
                System.out.println("Extension: " + header[0]+" File size: "+header[1]+header[2]);
                send.writeObject(header);
                byte[] content = new byte[(int)file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(content);
                send.writeObject(content);
            }
            else{
                System.out.println("File not found");
                return;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void receiveviatcp(int port){
        try{
            System.out.println("receiving via tcp");
            String[] files = (String[])receive.readObject();
            int num = 1;
            for(String i : files){
                System.out.println((num++)+". "+i);
            }
            System.out.println("Enter the name of file you want: ");
            String filename = input.next();
            send.writeObject(filename);

            String[] info = (String[])receive.readObject();
            for(String information : info){
                System.out.println(information);
            }
            byte[] content = (byte[])receive.readObject();

            File folder = new File("clientfiles");
            if (!folder.exists()) {
                folder.mkdirs(); 
            }
            System.out.print("Enter the name you want to give the file: ");
            FileOutputStream fos = new FileOutputStream(new File(folder,input.next()+info[0]));
            fos.write(content);
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
