package net.siham;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.net.Socket;

public class ClientController {
    @FXML private TextArea chatArea;
    @FXML private TextField messageField;

    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public void initialize() {
        try {
            socket = new Socket("localhost", 9090);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Thread pour lire les messages reçus
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        String finalMsg = msg;
                        Platform.runLater(() -> chatArea.appendText("Serveur : " + finalMsg + "\n"));
                        if (finalMsg.equalsIgnoreCase("bye")) {
                            closeConnection();
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            chatArea.appendText("Erreur de connexion au serveur.\n");
        }
    }

    @FXML
    public void sendMessage() {
        String msg = messageField.getText();
        if (!msg.isEmpty()) {
            out.println(msg);
            chatArea.appendText("Vous : " + msg + "\n");
            messageField.clear();
            if (msg.equalsIgnoreCase("bye")) {
                closeConnection();
            }
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            Platform.exit();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
