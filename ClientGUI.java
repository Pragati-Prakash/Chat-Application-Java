import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientGUI {
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField messageInput;
    private JButton sendButton;
    private PrintWriter out;
    private String username;

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public ClientGUI() {
        // Initialize username
        username = JOptionPane.showInputDialog(null, "Enter your username:", "Username", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            System.exit(0);
        }

        // Create the main window
        frame = new JFrame("Chat Application - " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        // Create message display area
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Create input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        messageInput = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Add action listeners
        sendButton.addActionListener(e -> sendMessage());
        messageInput.addActionListener(e -> sendMessage());

        // Window listener for cleanup
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (out != null) {
                    out.println("exit");
                }
                System.exit(0);
            }
        });

        // Center the window
        frame.setLocationRelativeTo(null);
    }

    private void sendMessage() {
        String message = messageInput.getText();
        if (!message.trim().isEmpty()) {
            out.println(message);
            messageInput.setText("");
        }
        messageInput.requestFocus();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            
            // Send username to server
            out.println(username);

            // Start thread to receive messages
            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message;
                    while ((message = in.readLine()) != null) {
                        final String finalMessage = message;
                        SwingUtilities.invokeLater(() -> {
                            messageArea.append(finalMessage + "\n");
                            messageArea.setCaretPosition(messageArea.getDocument().getLength());
                        });
                    }
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        messageArea.append("*** Disconnected from server ***\n");
                        messageInput.setEnabled(false);
                        sendButton.setEnabled(false);
                    });
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error connecting to server: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void start() {
        frame.setVisible(true);
        connectToServer();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI client = new ClientGUI();
            client.start();
        });
    }
} 