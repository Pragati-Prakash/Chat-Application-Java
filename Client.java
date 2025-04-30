import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server!");

            // Create reader for server messages
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Create writer for sending messages to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            // Create reader for user input
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Start a thread to handle server messages
            Thread serverListener = new Thread(() -> {
                try {
                    String message;
                    while ((message = serverIn.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            serverListener.start();

            // Main loop to send messages
            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }

            // Clean up
            socket.close();
            System.exit(0);

        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }
} 