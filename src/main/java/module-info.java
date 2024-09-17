module ru.sudrf.fairrecord.fairrecord {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.sudrf.fairrecord.fairrecord to javafx.fxml;
    exports ru.sudrf.fairrecord.fairrecord;
}