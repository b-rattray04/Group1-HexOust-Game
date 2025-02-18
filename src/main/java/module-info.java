module comp20050.softwareengineeringproject2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens comp20050.softwareengineeringproject2 to javafx.fxml;
    exports comp20050.softwareengineeringproject2;
}