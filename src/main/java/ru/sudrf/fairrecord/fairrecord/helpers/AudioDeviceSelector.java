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
    private static ComboBox<String> deviceComboBox;
    private static Stage primaryStage;

    public static void showAndWait() {
        primaryStage = new Stage();
        primaryStage.setTitle("Select Audio Device");

        List<String> availableDeviceNames = MixerHelper.getAllMixers();
        ObservableList<String> deviceNames = FXCollections.observableArrayList(availableDeviceNames);

        deviceComboBox = new ComboBox<>(deviceNames);
        deviceComboBox.setPromptText("Select Audio Device");

        Button selectButton = new Button("Select");
        selectButton.setOnAction(e -> {
            String selectedDeviceName = deviceComboBox.getValue();
            if (selectedDeviceName != null) {
                selectedMixer = MixerHelper.getMixerByName(selectedDeviceName);
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

}