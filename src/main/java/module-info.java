module com.example.doanltm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires mysql.connector.j;
    requires java.sql;


    opens com.example.doanltm to javafx.fxml;
    opens com.example.doanltm.Controller to javafx.fxml;
    opens com.example.doanltm.Model to javafx.fxml, javafx.base;  // Thêm javafx.base
    opens com.example.doanltm.Service to javafx.fxml;
   // opens com.example.doanltm.Util to javafx.fxml;
    opens com.example.doanltm.Database to javafx.fxml;



    exports com.example.doanltm;
    exports com.example.doanltm.Model;
    exports com.example.doanltm.Service;
   // exports com.example.doanltm.Util;
    exports com.example.doanltm.Controller;
    exports com.example.doanltm.Database;



}

