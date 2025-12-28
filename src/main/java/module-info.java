module com.example.pingponggame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;


    opens com.example.pingponggame to javafx.fxml;
    exports com.example.pingponggame.Game;
}