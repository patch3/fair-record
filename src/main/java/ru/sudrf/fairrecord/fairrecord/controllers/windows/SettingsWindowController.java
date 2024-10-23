package ru.sudrf.fairrecord.fairrecord.controllers.windows;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import ru.sudrf.fairrecord.fairrecord.AudioRecorder;
import ru.sudrf.fairrecord.fairrecord.FairRecord;
import ru.sudrf.fairrecord.fairrecord.helpers.MixerHelper;

import javax.sound.sampled.Mixer;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class SettingsWindowController implements Initializable {

    @FXML
    private CheckBox noiseReductionEnabled;
    @FXML
    private Button applyButton;
    @FXML
    private TextField soundtrackName;
    @FXML
    private HBox mixerSelecter;
    @FXML
    private ComboBox<Mixer> deviceComboBox;
    @FXML
    private Button cancelButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initComboBox();
    }

    private void initOther(AudioRecorder.RecorderSettings settings){
        soundtrackName.setText(settings.getSoundtrackName());
    }

    private void initComboBox() {
        deviceComboBox.setCellFactory(param -> new MixerListCell());
        deviceComboBox.setButtonCell(new MixerListCell());

        List<Mixer> availableMixers = MixerHelper.getAllMixers();
        ObservableList<Mixer> mixers = FXCollections.observableArrayList(availableMixers);
        deviceComboBox.setItems(mixers);
        deviceComboBox.getSelectionModel().select(0);
    }


    public static AudioRecorder.RecorderSettings showAndGetSettings(AudioRecorder.RecorderSettings lastSettings) throws IOException {
        Stage stage = new Stage();
        stage.setTitle(String.format("Настройки дорожки: %s", lastSettings.getSoundtrackName()));
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(FairRecord.class.getResource("soundtrack-settings.fxml")));

        Scene scene = new Scene(loader.load());
        SettingsWindowController controller = loader.getController();
        controller.initOther(lastSettings);
        stage.setScene(scene);

        AtomicReference<AudioRecorder.RecorderSettings> result = new AtomicReference<>();

        controller.applyButton.setOnMouseClicked(e -> {
            lastSettings.setAudioDevice(controller.deviceComboBox.getSelectionModel().getSelectedItem());
            lastSettings.setSoundtrackName(controller.soundtrackName.getText());

            result.set(lastSettings);
            stage.close();
        });

        controller.cancelButton.setOnMouseClicked(e -> {
            result.set(lastSettings);
            stage.close();
        });

        stage.showAndWait();

        return result.get(); //TODO 23.10.24: ДОДЕЛАТЬ
    }






    private static class MixerListCell extends javafx.scene.control.ListCell<Mixer> {
        @Override
        protected void updateItem(Mixer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.getMixerInfo().getName());
            }
        }
    }


}
