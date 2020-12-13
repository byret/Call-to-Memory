module com.mycompany.calltomemory {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.calltomemory to javafx.fxml;
    exports com.mycompany.calltomemory;
    requires org.apache.commons.lang3;
}
