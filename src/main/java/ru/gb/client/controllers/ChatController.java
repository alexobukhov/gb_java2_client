package ru.gb.client.controllers;

import ru.gb.client.models.Network;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.text.DateFormat;
import java.util.*;

public class ChatController {

    @FXML
    private ListView<String> usersList;

    @FXML
    private Label usernameTitle;

    @FXML
    private TextArea chatHistory;

    @FXML
    private TextField inputField;

    @FXML
    private Button sendButton;
    private String selectedRecipient;

    @FXML
    public void initialize() {
        usersList.setItems(FXCollections.observableArrayList("Тимофей", "Дмитрий", "Диана", "Арман"));
        sendButton.setOnAction(event -> sendMessage());
        inputField.setOnAction(event -> sendMessage());
        List<String> messageHistory = getChatHistory();
        for (int i = messageHistory.size() - 100; i < messageHistory.size(); i++) {
            chatHistory.appendText(messageHistory.get(i) + System.lineSeparator());
        }

        usersList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = usersList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                usersList.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell;
        });

    }

    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        inputField.clear();


        if (message.trim().isEmpty()) {
            return;
        }

        if (selectedRecipient != null) {
            network.sendPrivateMessage(selectedRecipient, message);
        } else {
            network.sendMessage(message);
        }

        appendMessage("Я: " + message);
    }

    public void appendMessage(String message) {
        String timeStamp = DateFormat.getInstance().format(new Date());

        chatHistory.appendText(timeStamp);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(System.lineSeparator());

        updateChatHistory(message);
    }

    public void appendServerMessage(String serverMessage) {
        chatHistory.appendText(serverMessage);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(System.lineSeparator());
    }

    public void setUsernameTitle(String username) {
        this.usernameTitle.setText(username);
    }

    public void updateUsersList(String[] users) {
        Arrays.sort(users);
        for (int i = 0; i < users.length; i++) {
            if (users[i].equals(network.getUsername())) {
                users[i] = "[me] " + users[i];
            }
            usersList.getItems().clear();
            Collections.addAll(usersList.getItems(), users);
        }
    }

    public void updateChatHistory(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/lib/history_"
                + usernameTitle, true));) {
            writer.write(message + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getChatHistory() {
        List<String> messageHistory = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/lib/history_"
                + usernameTitle))){
            String message;
            while ((message = reader.readLine()) != null) {
                messageHistory.add(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messageHistory;
    }
}
