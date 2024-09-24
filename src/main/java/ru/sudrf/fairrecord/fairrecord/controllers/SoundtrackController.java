package ru.sudrf.fairrecord.fairrecord.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import ru.sudrf.fairrecord.fairrecord.AudioRecorder;
import ru.sudrf.fairrecord.fairrecord.helpers.AudioDeviceSelector;
import ru.sudrf.fairrecord.fairrecord.helpers.MixerHelper;

import javax.sound.sampled.Mixer;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SoundtrackController implements Initializable {
    @FXML
    private Text name;
    @FXML
    private Text audioTrack;
    @FXML
    private Button delete;
    @FXML
    private Button changeMicro;
    @FXML
    private Button stop;
    @FXML
    private Button start;
    @FXML
    private Rectangle limiter;
    @FXML
    private Text soundDb;

    private Mixer currentMixerName;
    private AudioRecorder recorder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.name.setText(String.format(this.name.getText(), MainController.soundtrackManager.getCount()));
    }

    public SoundtrackController getController() {
        return this;
    }

    public void clickDelete(MouseEvent mouseEvent) {
        // Логика удаления звукового трека
    }

    public void clickChangeMicro(MouseEvent mouseEvent) {
        AudioDeviceSelector.showAndWait();
        currentMixerName = AudioDeviceSelector.getSelectedMixer();
        if (currentMixerName != null) {
            this.audioTrack.setText(String.format("Аудиоустройство: %s", currentMixerName.getMixerInfo().getName()));
            recorder = new AudioRecorder(new File("test" + currentMixerName.getMixerInfo().getName() + ".wav"), currentMixerName);
        }
    }

    public void clickStop(MouseEvent mouseEvent) {
        if (recorder != null) {
            recorder.stop();
        }
    }

    public void clickStart(MouseEvent mouseEvent) {
        if (recorder != null) {
            recorder.start();
        }
    }
}