module comp20050.SwEngProject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens comp20050.SwEngProject to javafx.fxml;
    exports comp20050.SwEngProject;
}