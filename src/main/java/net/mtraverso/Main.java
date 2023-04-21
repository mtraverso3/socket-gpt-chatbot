package net.mtraverso;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;


public class Main {
    public static void main(String[] args) {

        Chatbot chatbot = new Chatbot();

        int towerPort = 8124;
        String towerIp = "0.0.0.0";
        ServerSocket towerSocket = null;
        Socket alienSocket = null;

        try {
            // STEP 1
            // Create and set up a socket
            towerSocket = new ServerSocket();
            InetAddress towerAddr = InetAddress.getByName(towerIp);
            InetSocketAddress socketAddress = new InetSocketAddress(towerAddr, towerPort);

            // STEP 2
            // Bind the socket with address structure
            // so that aliens can find the address
            towerSocket.bind(socketAddress);
            System.out.println("Listening");


            while (true) {
                // STEP 4
                // Accept connections from aliens
                // to enable communication
                alienSocket = towerSocket.accept();
                System.out.println("Connection established");

                // Send and receive messages
                BufferedReader reader = new BufferedReader(new InputStreamReader(alienSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(alienSocket.getOutputStream(), false);
                String receivedMessage;


                while ((receivedMessage = reader.readLine()) != null) {
                    writer.print("--------- SocketGPT --------\n");
                    System.out.println("Received: " + receivedMessage);

                    String chatbotResponse = ConsoleColors.BLUE_BRIGHT + chatbot.chat(receivedMessage) + ConsoleColors.RESET;
                    System.out.println("Replying: " + chatbotResponse);
                    writer.print(chatbotResponse);
                    writer.print("\n----------- User -----------");

                    writer.flush(); // Send the message
                }

                System.out.println("Connection lost!");
                alienSocket.close();
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

            if (alienSocket != null) {
                try {
                    alienSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing alienSocket: " + e.getMessage());
                }
            }
        }
    }
}


