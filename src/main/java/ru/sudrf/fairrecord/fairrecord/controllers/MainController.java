package ru.sudrf.fairrecord.fairrecord.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import ru.sudrf.fairrecord.fairrecord.FairRecord;
import ru.sudrf.fairrecord.fairrecord.managers.SoundtrackManager;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button addSoundtrack;
    @FXML
    private VBox soundtracks;

    public static final SoundtrackManager soundtrackManager = new SoundtrackManager();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addSoundtrack.setOnMouseClicked(this::addSoundtrack);
    }

    private void addSoundtrack(MouseEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(FairRecord.class.getResource("soundtrack.fxml")));
            VBox soundtrackNode = loader.load();
            SoundtrackController trackController = loader.getController();
            soundtracks.getChildren().add(soundtrackNode);
            soundtrackManager.addSoundtrack(trackController);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}