package client;

import server.Server;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
