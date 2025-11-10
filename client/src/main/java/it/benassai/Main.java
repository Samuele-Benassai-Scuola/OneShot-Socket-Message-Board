package it.benassai;

import java.io.IOException;

import it.benassai.socket.ClientSocketRunnable;

public class Main {
    public static void main(String[] args) throws IOException {
        ClientSocketRunnable clientSocketRunnable = new ClientSocketRunnable("localhost", 3000);

        clientSocketRunnable.run();
    }
}