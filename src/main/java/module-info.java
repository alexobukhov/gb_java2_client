module gb.safronov.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.gb.client to javafx.fxml;
    exports ru.gb.client;
    exports ru.gb.client.controllers;
    opens ru.gb.client.controllers to javafx.fxml;
}
