package ru.sudrf.fairrecord.fairrecord;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Основной класс приложения FairRecord, расширяющий {@link Application}.
 *
 * <p>Этот класс отвечает за запуск приложения и создание основного окна с использованием FXML.
 * Приложение загружает основной вид из файла "main-view.fxml" и отображает его в окне с заголовком "Fair Record".
 *
 * @see Application
 * @see FXMLLoader
 * @see Scene
 * @see Stage
 */
public class FairRecord extends Application {

    /**
     * Метод, вызываемый при запуске приложения.
     *
     * <p>Создает основное окно приложения, загружает основной вид из файла "main-view.fxml"
     * и отображает его в окне с заголовком "Fair Record".
     *
     * @param stage Основное окно приложения.
     * @throws IOException Если произошла ошибка при загрузке FXML-файла.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FairRecord.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Fair Record");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Точка входа в приложение.
     *
     * <p>Запускает приложение с использованием метода {@link Application#launch(String...)}.
     *
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        launch();
    }
}