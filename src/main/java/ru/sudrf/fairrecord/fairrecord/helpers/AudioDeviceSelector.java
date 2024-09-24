package ru.sudrf.fairrecord.fairrecord.helpers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;

import javax.sound.sampled.Mixer;
import java.util.List;

public class AudioDeviceSelector {

    @Getter
    private static Mixer selectedMixer;
    private static ComboBox<Mixer> deviceComboBox;
    private static Stage primaryStage;

    public static void showAndWait() {
        primaryStage = new Stage();
        primaryStage.setTitle("Select Audio Device");

        List<Mixer> availableMixers = MixerHelper.getAllMixers();
        ObservableList<Mixer> mixers = FXCollections.observableArrayList(availableMixers);

        deviceComboBox = new ComboBox<>(mixers);
        deviceComboBox.setPromptText("Select Audio Device");

        // Устанавливаем строковое представление миксера для отображения в ComboBox
        deviceComboBox.setCellFactory(param -> new MixerListCell());
        deviceComboBox.setButtonCell(new MixerListCell());

        Button selectButton = new Button("Select");
        selectButton.setOnAction(e -> {
            Mixer selectedDevice = deviceComboBox.getValue();
            if (selectedDevice != null) {
                selectedMixer = selectedDevice;
                primaryStage.close();
            }
        });

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(deviceComboBox, selectButton);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.showAndWait();
    }

    // Класс для отображения миксера в ComboBox
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