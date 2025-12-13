module com.example.pingponggame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pingponggame to javafx.fxml;
    exports com.example.pingponggame;
}