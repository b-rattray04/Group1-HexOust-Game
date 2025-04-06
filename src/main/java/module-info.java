module comp20050.SwEngProject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires transitive javafx.graphics;
    requires org.mockito;
    requires org.junit.jupiter.api;
    requires org.junit.platform.commons;
    requires jdk.jshell;

    exports comp20050.SwEngProject;
    opens comp20050.SwEngProject to javafx.fxml, org.junit.platform.commons;
}