module com.iae {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires com.google.gson;

    exports com.iae.controller;
    exports com.iae.model;
    opens com.iae.controller to javafx.fxml, javafx.graphics;
    opens com.iae.model to com.google.gson, javafx.base, javafx.fxml;
}