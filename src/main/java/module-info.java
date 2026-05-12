module org.nixenthusiast.ce316project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens org.nixenthusiast.ce316project to javafx.fxml;
    exports org.nixenthusiast.ce316project;
}