package ru.sudrf.fairrecord.fairrecord.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.sudrf.fairrecord.fairrecord.AudioRecorder;
import ru.sudrf.fairrecord.fairrecord.controllers.windows.SettingsWindowController;

import java.net.URL;
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
    private Button settings;



    /**
     * Записывающий аудиообъект.
     */

    private AudioRecorder recorder = new AudioRecorder();

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
        soundDb.setText(String.valueOf(Double.NaN));
        settings.setOnMouseClicked(this::clickSettings);
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
     * Обработчик события изменения микрофона.
     *
     * <p>Отображает окно выбора аудиоустройства и создает новый объект {@link AudioRecorder}
     * с выбранным миксером.
     *
     * @param mouseEvent Событие мыши.
     */
    public void clickSettings(MouseEvent mouseEvent) {
        try {
            recorder.setSettings(SettingsWindowController.showAndGetSettings(recorder.getSettings()));
            updateVisualSetting();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateVisualSetting() {
        name.setText(recorder.getSettings().getSoundtrackName());
        audioTrack.setText(String.format("Аудиоустройство: %s", recorder.getSettings().getAudioDevice().getMixerInfo().getName()));

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
        try {
            if (recorder != null && recorder.getRecordingThread() != null) {
                recorder.getRecordingThread().join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        limiter.setWidth(170);
        limiter.setFill(Color.GREEN);
        soundDb.setText(String.valueOf(Double.NaN));
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
            if (recorder == null || recorder.getRecordingThread() == null || recorder.getRecordingThread().isInterrupted() || !recorder.isRecording()) {
                return;
            }
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