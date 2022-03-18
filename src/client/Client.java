package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class Client {
    public static final int PORT = 24650;
    private static Scanner scanner = new Scanner(System.in);


    public void start() {
        try  {
            Thread.sleep(1500);
            Socket socket = new Socket(InetAddress.getLoopbackAddress(), PORT);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            System.out.println("Enter action (1 - get a file, 2 - save a file, 3 - delete a file): ");
            switch (scanner.next()) {
                case "1":
                    get(input, output);
                    break;
                case "2":
                    add(input, output);
                    break;
                case "3":
                    delete(input, output);
                    break;
                case "exit":
                    output.writeUTF("EXIT");
                    System.out.println("The request was sent.");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void add(DataInputStream input, DataOutputStream output) {
        System.out.println("Enter name of the file: ");
        String name = scanner.next();
        byte[] content = ClientFileManager.getFileContent(name);
        System.out.println("Enter name of the file to be saved on server: ");
        String nameToSave = scanner.nextLine();
        if (nameToSave.isBlank()) {
            nameToSave = name;
        }
        try {
            output.writeUTF("PUT");
            output.writeUTF(nameToSave);
            output.writeInt(content.length);
            output.write(content);
            System.out.println("The request was sent.");
            String res = input.readUTF();
            if (res.startsWith("200")) {
                System.out.println("Response says that file is saved! ID = " + res.substring(4));
            } else if (res.startsWith("403")) {
                System.out.println("The response says that creating the file was forbidden!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void get(DataInputStream input, DataOutputStream output) {
        String name = getNameOrID();
        try {
            output.writeUTF("GET");
            output.writeUTF(name);
            System.out.println("The request was sent.");
            String msg = input.readUTF();
            if (msg.startsWith("200")) {
                int length = input.readInt();
                byte[] message = new byte[length];
                input.readFully(message, 0, message.length);
                System.out.println("The file was downloaded! Specify a name for it: ");
                String nameS = scanner.next();
                ClientFileManager.saveFile(nameS, message);
                System.out.println("File saved on the hard drive!");
            } else if (msg.startsWith("404")) {
                System.out.println("The response says that the file was not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void delete(DataInputStream input, DataOutputStream output) {
        String name = getNameOrID();
        try {
            output.writeUTF("DELETE");
            output.writeUTF(name);
            System.out.println("The request was sent.");
            String msg = input.readUTF();
            if (msg.startsWith("200")) {
                System.out.println("The response says that the file was successfully deleted!");
            } else if (msg.startsWith("404")) {
                System.out.println("The response says that the file was not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getNameOrID() {
        System.out.println("Do you want to get/delete the file by name or by id (1 - name, 2 - id): ");
        String nameOrID = scanner.next();
        if (nameOrID.equals("1")) {
            System.out.println("Enter name of the file: ");
            return "NAME_"+ scanner.next();
        }
        System.out.println("Enter id of the file: ");
        return "ID_" + scanner.next();
    }
}
