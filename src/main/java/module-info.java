module ru.sudrf.fairrecord.fairrecord {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.desktop;


    opens ru.sudrf.fairrecord.fairrecord to javafx.fxml;
    exports ru.sudrf.fairrecord.fairrecord;
    exports ru.sudrf.fairrecord.fairrecord.controllers;
    opens ru.sudrf.fairrecord.fairrecord.controllers to javafx.fxml;
    exports ru.sudrf.fairrecord.fairrecord.helpers;
    opens ru.sudrf.fairrecord.fairrecord.helpers to javafx.fxml;
    exports ru.sudrf.fairrecord.fairrecord.managers;
    opens ru.sudrf.fairrecord.fairrecord.managers to javafx.fxml;
}