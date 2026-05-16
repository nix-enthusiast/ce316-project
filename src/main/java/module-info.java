module com.iae {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires com.google.gson;

    exports com.iae.controller;
    opens com.iae.controller to javafx.fxml, javafx.graphics;
}