module com.russan.gis {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.russun.gis to javafx.fxml;
    exports com.russun.gis;
    exports com.russun.gis.shapes;
    opens com.russun.gis.shapes to javafx.fxml;
    exports com.russun.gis.utils;
    opens com.russun.gis.utils to javafx.fxml;
}