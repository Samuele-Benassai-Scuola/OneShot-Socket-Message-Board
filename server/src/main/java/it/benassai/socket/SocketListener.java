package it.benassai.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketListener implements Runnable {
    private int port;

    public SocketListener(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("Server Started");

        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);

            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("connesso a " + socket.getInetAddress());

                    Thread thread = new Thread(new ServerSocketRunnable(socket));
                    thread.start();
                }
            }
            finally {
                serverSocket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
