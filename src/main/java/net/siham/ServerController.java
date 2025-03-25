package net.siham;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController {

    @FXML private TextArea chatArea;
    @FXML private TextField messageField;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    @FXML
    public void initialize() {
        startServer();
    }

    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(9090);

                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                readMessages();

            } catch (IOException e) {
                updateChat("Erreur lors du démarrage du serveur : " + e.getMessage() + "\n");
            }
        }).start();
    }

    private void readMessages() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                updateChat("Client : " + msg + "\n");

                if (msg.equalsIgnoreCase("bye")) {
                    updateChat("Le client a quitté la conversation.\n");
                    closeConnection();
                    break;
                }
            }
        } catch (IOException e) {
            updateChat("Erreur lors de la lecture des messages : " + e.getMessage() + "\n");
        }
    }

    @FXML
    public void sendMessage() {
        String msg = messageField.getText().trim();
        if (!msg.isEmpty() && out != null) {
            out.println(msg);
            updateChat("Vous : " + msg + "\n");
            messageField.clear();

            if (msg.equalsIgnoreCase("bye")) {
                updateChat("Le serveur a quitté la conversation.\n");
                closeConnection();
            }
        }
    }

    private void updateChat(String message) {
        Platform.runLater(() -> chatArea.appendText(message));
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {
            updateChat("Erreur lors de la fermeture de la connexion : " + e.getMessage() + "\n");
        } finally {
            Platform.exit();
            System.exit(0);
        }
    }
}
