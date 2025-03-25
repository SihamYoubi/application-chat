module net.siham.applicationchat {
    requires javafx.controls;
    requires javafx.fxml;


    opens net.siham to javafx.fxml;
    exports net.siham;
}