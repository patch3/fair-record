package ru.sudrf.fairrecord.fairrecord.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.sudrf.fairrecord.fairrecord.AudioRecorder;
import ru.sudrf.fairrecord.fairrecord.FairRecord;
import ru.sudrf.fairrecord.fairrecord.helpers.AudioDeviceSelector;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Контроллер для управления звуковой дорожкой.
 *
 * <p>Класс {@code SoundtrackController} реализует интерфейс {@link Initializable} и отвечает за управление
 * элементами интерфейса, связанными со звуковой дорожкой. Он предоставляет методы для удаления, изменения
 * микрофона, запуска и остановки записи.
 *
 * <p>Этот класс использует JavaFX для создания графического интерфейса и взаимодействия с пользователем.
 *
 * @see Initializable
 * @see AudioRecorder
 * @see AudioDeviceSelector
 * @see MainController
 */
public class SoundtrackController implements Initializable {

    /**
     * Корневой элемент для этого контроллера.
     */
    @FXML
    @Getter
    private VBox root;

    /**
     * Текстовое поле для отображения имени звуковой дорожки.
     */
    @FXML
    private Text name;

    /**
     * Текстовое поле для отображения информации о аудиоустройстве.
     */
    @FXML
    private Text audioTrack;

    /**
     * Кнопка для удаления звуковой дорожки.
     */
    @FXML
    private Button delete;

    /**
     * Кнопка для изменения микрофона.
     */
    @FXML
    private Button changeMicro;

    /**
     * Кнопка для остановки записи.
     */
    @FXML
    private Button stop;

    /**
     * Кнопка для запуска записи.
     */
    @FXML
    private Button start;

    /**
     * Прямоугольник для отображения уровня звука.
     */
    @FXML
    private Rectangle limiter;

    /**
     * Текстовое поле для отображения уровня звука в децибелах.
     */
    @FXML
    private Text soundDb;

    /**
     * Изображение для кнопки настроек.
     */
    @FXML
    private ImageView settings;

    /**
     * Текущий выбранный миксер.
     */
    private Mixer currentMixerName;

    /**
     * Записывающий аудиообъект.
     */
    private AudioRecorder recorder;

    /**
     * Инициализация контроллера.
     *
     * <p>Метод вызывается после загрузки FXML-файла и инициализирует элементы интерфейса.
     * Устанавливает изображение для кнопки настроек и отображает имя звуковой дорожки.
     *
     * @param url URL для корневого объекта или {@code null} если не применимо.
     * @param resourceBundle ResourceBundle для корневого объекта или {@code null} если не применимо.
     */
    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.name.setText(String.format(this.name.getText(), MainController.soundtrackManager.getCount()));
        this.settings.setImage(new Image(Objects.requireNonNull(FairRecord.class.getResourceAsStream("images/settingsImage.png"))));
    }

    /**
     * Возвращает экземпляр контроллера.
     *
     * @return Экземпляр контроллера.
     */
    public SoundtrackController getController() {
        return this;
    }

    /**
     * Обработчик события удаления звуковой дорожки.
     *
     * <p>Удаляет текущую звуковую дорожку из менеджера звуковых дорожек.
     *
     * @param mouseEvent Событие мыши.
     */
    public void clickDelete(MouseEvent mouseEvent) {
        MainController.soundtrackManager.removeSoundtrack(this);
    }

    /**
     * Обработчик события настроек звуковой дорожки.
     *
     * <p>Пока не реализован.
     *
     * @param mouseEvent Событие мыши.
     */
    public void clickSettings(MouseEvent mouseEvent) {
        // Пока не реализован
    }

    /**
     * Обработчик события изменения микрофона.
     *
     * <p>Отображает окно выбора аудиоустройства и создает новый объект {@link AudioRecorder}
     * с выбранным миксером.
     *
     * @param mouseEvent Событие мыши.
     */
    public void clickChangeMicro(MouseEvent mouseEvent) {
        AudioDeviceSelector.showAndWait();
        currentMixerName = AudioDeviceSelector.getSelectedMixer();
        if (currentMixerName != null) {
            this.audioTrack.setText(String.format("Аудиоустройство: %s", currentMixerName.getMixerInfo().getName()));
            recorder = new AudioRecorder(new File("test" + currentMixerName.getMixerInfo().getName() + ".wav"), currentMixerName, new AudioRecorder.RecorderSettings());
            recorder.setSoundtrackController(this);
        }
    }

    /**
     * Обработчик события остановки записи.
     *
     * <p>Останавливает текущую запись, если она активна.
     *
     * @param mouseEvent Событие мыши.
     */
    public void clickStop(MouseEvent mouseEvent) {
        if (recorder != null) {
            recorder.stop();
        }
        limiter.setWidth(170);
        limiter.setFill(Color.GREEN);
    }

    /**
     * Обработчик события запуска записи.
     *
     * <p>Запускает текущую запись, если она не активна.
     *
     * @param mouseEvent Событие мыши.
     */
    public void clickStart(MouseEvent mouseEvent) {
        if (recorder != null) {
            limiter.setWidth(0);
            recorder.start();
        }
    }

    /**
     * Обновляет отображение уровня звука.
     *
     * @param db Уровень звука в децибелах.
     */
    public void updateVolumeVisualization(double db) {
        javafx.application.Platform.runLater(() -> {
            soundDb.setText(String.format("%.2f dB", db));
            double width = Math.max(0, Math.min(100, db + 60)); // Нормализация dB в диапазон 0-100
            limiter.setWidth(width);

            if (db < -15) {
                limiter.setFill(Color.GREEN);
            } else if (db < -3) {
                limiter.setFill(Color.YELLOW);
            } else {
                limiter.setFill(Color.RED);
            }
        });
    }
}