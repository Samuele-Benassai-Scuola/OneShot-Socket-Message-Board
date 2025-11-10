package it.benassai.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerSocketRunnable implements Runnable {
    private BufferedReader socketIn;
    private PrintWriter socketOut;

    public ServerSocketRunnable(Socket socket) throws IOException {
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        socketOut = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        socketOut.println("WELCOME");


    }
}
