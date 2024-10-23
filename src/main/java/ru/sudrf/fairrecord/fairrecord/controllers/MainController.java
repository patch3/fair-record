package ru.sudrf.fairrecord.fairrecord.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import ru.sudrf.fairrecord.fairrecord.AudioRecorder;
import ru.sudrf.fairrecord.fairrecord.FairRecord;
import ru.sudrf.fairrecord.fairrecord.managers.SoundtrackManager;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Основной контроллер приложения FairRecord.
 *
 * <p>Класс {@code MainController} реализует интерфейс {@link Initializable} и отвечает за управление
 * основным интерфейсом приложения. Он предоставляет методы для добавления и удаления звуковых дорожек.
 *
 * <p>Этот класс использует JavaFX для создания графического интерфейса и взаимодействия с пользователем.
 *
 * @see Initializable
 * @see SoundtrackManager
 * @see SoundtrackController
 */
public class MainController implements Initializable {

    /**
     * Экземпляр основного контроллера.
     */
    protected static MainController instance;

    /**
     * Кнопка для добавления новой звуковой дорожки.
     */
    @FXML
    private Button addSoundtrack;

    /**
     * Контейнер для отображения звуковых дорожек.
     */
    @FXML
    private VBox soundtracks;

    /**
     * Менеджер звуковых дорожек.
     */
    public static final SoundtrackManager soundtrackManager = new SoundtrackManager();

    /**
     * Инициализация контроллера.
     *
     * <p>Метод вызывается после загрузки FXML-файла и инициализирует элементы интерфейса.
     * Устанавливает обработчик события для кнопки добавления звуковой дорожки.
     *
     * @param url URL для корневого объекта или {@code null} если не применимо.
     * @param resourceBundle ResourceBundle для корневого объекта или {@code null} если не применимо.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addSoundtrack.setOnMouseClicked(this::addSoundtrack);
        instance = this;
    }

    /**
     * Обработчик события добавления новой звуковой дорожки.
     *
     * <p>Загружает FXML-файл для новой звуковой дорожки, добавляет её в контейнер и регистрирует
     * в менеджере звуковых дорожек.
     *
     * @param actionEvent Событие мыши.
     */
    private void addSoundtrack(MouseEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(FairRecord.class.getResource("soundtrack.fxml")));
            VBox soundtrackNode = loader.load();
            SoundtrackController trackController = loader.getController();
            soundtracks.getChildren().add(soundtrackNode);
            soundtrackManager.addSoundtrack(trackController, new AudioRecorder.RecorderSettings());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Удаляет звуковую дорожку из интерфейса.
     *
     * <p>Удаляет узел звуковой дорожки из контейнера.
     *
     * @param soundtrackController Контроллер звуковой дорожки для удаления.
     */
    public static void deleteSoundtrack(SoundtrackController soundtrackController) {
        instance.soundtracks.getChildren().remove(soundtrackController.getRoot());
    }
}