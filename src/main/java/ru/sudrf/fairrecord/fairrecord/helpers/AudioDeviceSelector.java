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

    /**
     * Отображает окно выбора записываемого устройства и ожидает выбора пользователя.
     *
     * <p>Метод создает новое окно.
     * В окне отображается выпадающий список (ComboBox) со всеми доступными миксерами,
     * которые поддерживают запись аудиоданных. Пользователь может выбрать один из миксеров.
     * После выбора миксера и нажатия кнопки, окно закрывается, и выбранный миксер
     * сохраняется в переменной {@code selectedMixer}.
     *
     * <p>Этот метод использует JavaFX для создания графического интерфейса.
     *
     * @see MixerHelper#getAllMixers()
     */
    public static void showAndWait() {
        primaryStage = new Stage();
        primaryStage.setTitle("Выбор записываемого устройства");

        List<Mixer> availableMixers = MixerHelper.getAllMixers();
        ObservableList<Mixer> mixers = FXCollections.observableArrayList(availableMixers);

        deviceComboBox = new ComboBox<>(mixers);
        deviceComboBox.setPromptText("Выбор записываемого устройства");

        // Устанавливаем строковое представление миксера для отображения в ComboBox
        deviceComboBox.setCellFactory(param -> new MixerListCell());
        deviceComboBox.setButtonCell(new MixerListCell());

        Button selectButton = new Button("Выбрать");
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

    /**
     * Класс для отображения миксера в ComboBox.
     *
     * <p>Этот класс расширяет {@link javafx.scene.control.ListCell} и переопределяет метод {@link #updateItem(Mixer, boolean)},
     * чтобы отображать имя миксера в ComboBox. Если ячейка пуста или миксер равен {@code null}, текст ячейки устанавливается в {@code null}.
     *
     * <p>Этот класс используется для настройки отображения миксеров в ComboBox в методе {@link #showAndWait()}.
     *
     * @see javafx.scene.control.ListCell
     */
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