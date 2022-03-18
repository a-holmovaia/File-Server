package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static final int PORT = 24650;
    protected int maxThreads = 4;

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            FileManager.loadIdRegister();
            System.out.println("Server started!");
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                handleRequest(inputStream, outputStream, serverSocket);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }
    private void handleRequest(DataInputStream inputStream, DataOutputStream outputStream, ServerSocket socket) throws IOException {
        switch (inputStream.readUTF()) {
            case "PUT":
                put(inputStream, outputStream);
                break;
            case "GET":
                get(inputStream, outputStream);
                break;
            case "DELETE":
                delete(inputStream, outputStream);
                break;
            case "EXIT":
                socket.close();
                System.exit(0);
        }
    }

    private void put(DataInputStream input, DataOutputStream output) {
        try {
            String name = input.readUTF();
            int length = input.readInt();
            byte[] bytes = new byte[length];
            input.readFully(bytes, 0, length);
            int id = FileManager.generateNewId(length, name.length());
            if (name.isEmpty()) {
                name = String.valueOf(id);
            }
            if (FileManager.saveFile(name, bytes)) {
                FileManager.saveIdRegister();
                output.writeUTF(String.valueOf(Status.SUCCESFUL.code) + " " + id);
                return;
            }
            output.writeUTF(String.valueOf(Status.FORBIDDEN.code));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void get(DataInputStream input, DataOutputStream output) {
        try {
            String data = input.readUTF();
            String name = getName(data);
            byte[] content = FileManager.loadFile(name);
            if (content != null) {
                output.writeUTF(String.valueOf(Status.SUCCESFUL.code));
                output.writeInt(content.length);
                output.write(content);
            } else {
                output.writeUTF(String.valueOf(Status.NOT_FOUND.code));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void delete(DataInputStream input, DataOutputStream output) {
        try {
            String data = input.readUTF();
            String name = getName(data);
            if (FileManager.deleteFile(name)) {
                output.writeUTF(String.valueOf(Status.SUCCESFUL.code));
            } else {
                output.writeUTF(String.valueOf(Status.NOT_FOUND.code));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getName(String data) {
        if (data.startsWith("ID_")) {
            return FileManager.getNamebyId(Integer.parseInt(data.substring(3)));
        }
        return data.substring(5);
    }

}