package it.benassai.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientSocketRunnable implements Runnable {
    private Scanner scanner = new Scanner(System.in);
    
    private Socket socket;

    private BufferedReader socketIn;
    private PrintWriter socketOut;

    private boolean logged = false;

    public ClientSocketRunnable(String ip, int port) throws IOException {
        socket = new Socket(ip, port);

        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        socketOut = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            String welcome = socketIn.readLine();
            if (!welcome.equals("WELCOME")) {
                System.out.println("Server hasn't connected properly");
                return;
            }
            while (true) {
                try {
                    String command;
                    if (!logged) {
                        System.out.println(menuLogin());
                        int mode = Integer.parseInt(scanner.nextLine());

                        switch (mode) {
                            case 0:
                                command = "QUIT";
                                break;
                            case 1:
                                command = "LOGIN";
                                break;
                            default:
                                System.out.println("Invalid mode");
                                continue;
                        }
                    }
                    else {
                        System.out.println(menuNotLogin());
                        int mode = Integer.parseInt(scanner.nextLine());

                        switch (mode) {
                            case 0:
                                command = "QUIT";
                                break;
                            case 1:
                                command = "LIST";
                                break;
                            case 2:
                                command = "ADD";
                                break;
                            case 3:
                                command = "DEL";
                                break;
                            default:
                                System.out.println("Invalid mode");
                                continue;
                        }
                    }

                    if (command.equals("ADD")) {
                        System.out.println("Write message content");
                        command += " " + scanner.nextLine();
                    }
                    if (command.equals("DEL")) {
                        System.out.println("Write id of deleted message");
                        command += " " + scanner.nextLine();
                    }
                    if (command.equals("LOGIN")) {
                        System.out.println("Write username");
                        command += " " + scanner.nextLine();
                    }

                    socketOut.println(command);
                    String result = socketIn.readLine();

                    if (result.equals("BOARD:")) {
                        String message = socketIn.readLine();
                        while (!message.equals("END")) {
                            System.out.println(message);
                            message = socketIn.readLine();
                        }
                        continue;
                    }

                    String[] parts = result.split(" ");

                    switch (parts[0]) {
                        case "BYE":
                            System.out.println("End connection");
                            return;
                        case "OK":
                            if (parts.length > 1 && parts[1].equals("ADDED"))
                                System.out.println("Added message with id " + parts[2]);
                            if (!logged)
                                logged = true;
                            break;
                        case "ERR":
                            switch (parts[1]) {
                                case "SYNTAX":
                                    System.out.println("Invalid syntax");
                                    break;
                                case "NOTFOUND":
                                    System.out.println("ID not found");
                                    break;
                                case "PERMISSION":
                                    System.out.println("You don't own this message");
                            }
                            break;
                    }
                }
                catch (NumberFormatException e) {
                    System.out.println("The mode wasn't a number");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String menuLogin() {
        return "modes\n[0]: quit\n[1]: login";
    }

    private String menuNotLogin() {
        return "modes\n[0]: quit\n[1]: list\n[2]: add message\n[3]: delete message";
    }
}
