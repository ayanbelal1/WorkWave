module timetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    
    opens com.timetracker to javafx.fxml;
    opens com.timetracker.controller to javafx.fxml;
    opens com.timetracker.model to javafx.base;
    
    exports com.timetracker;
    exports com.timetracker.controller;
    exports com.timetracker.model;
}