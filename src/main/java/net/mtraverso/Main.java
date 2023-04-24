package net.mtraverso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        int towerPort = 8124;
        String towerIp = "0.0.0.0";
        ServerSocket towerSocket = null;

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor(); // Create a virtual thread executor

        try {
            // Prepare socket and address
            InetAddress towerAddr = InetAddress.getByName(towerIp);
            InetSocketAddress socketAddress = new InetSocketAddress(towerAddr, towerPort);
            towerSocket = new ServerSocket();

            // Bind the socket with address structure
            towerSocket.bind(socketAddress);
            System.out.println("Listening");

            while (true) {
                // Accept connections from clients
                Socket clientSocket = towerSocket.accept();

                // Create a virtual thread for each new client
                executorService.submit(() -> {
                    try {
                        // Create a new chatbot for each connection
                        Chatbot chatbot = new Chatbot();
                        handleClient(clientSocket, chatbot);

                        clientSocket.close();
                    } catch (IOException e) {
                        System.out.println("Error handling client connection: " + e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (towerSocket != null) {
                try {
                    towerSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing towerSocket: " + e.getMessage());
                }
            }
        }
    }

    private static void handleClient(Socket clientSocket, Chatbot chatbot) {
        System.out.println("Connection established with: " + clientSocket.getInetAddress());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), false)) {
            String receivedMessage;
            while ((receivedMessage = reader.readLine()) != null) {
                writer.print("--------- SocketGPT --------\n");
                System.out.println("Received from " + clientSocket.getInetAddress() + ": " + receivedMessage);

                String chatbotResponse = ConsoleColors.BLUE_BRIGHT + chatbot.chat(receivedMessage) + ConsoleColors.RESET;
                System.out.println("Replying to " + clientSocket.getInetAddress() + ": " + chatbotResponse);
                writer.print(chatbotResponse);
                writer.print("\n----------- User -----------");

                writer.flush(); // Send the message
            }

            System.out.println("Connection lost with: " + clientSocket.getInetAddress() + "!");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}