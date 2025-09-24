package spbstu.oop.v30321.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public void start(Stage stage) {
        // корневой контейнер
        StackPane root = new StackPane();

        // фон
        BackgroundImage bgImage = new BackgroundImage(
                new javafx.scene.image.Image(
                        Objects.requireNonNull(getClass().getResource("resources/bliss.jpg")).toExternalForm()
                ),
                BackgroundRepeat.NO_REPEAT, // не повторять по горизонтали
                BackgroundRepeat.NO_REPEAT, // не повторять по вертикали
                BackgroundPosition.CENTER,  // по центру
                // масштабировать
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        root.setBackground(new Background(bgImage));

        // окно поверх фона
        VBox notepadWindow = new VBox();
        notepadWindow.getStyleClass().add("notepad-window"); // стилизация через CSS
        notepadWindow.setMaxWidth(550);
        notepadWindow.setMaxHeight(350);

        // заголовок окна
        Label titleBar = new Label("Курсовая работа");
        titleBar.getStyleClass().add("titlebar"); // стиль заголовка
        titleBar.setMaxWidth(Double.MAX_VALUE);   // растянуть на всю ширину VBox

        // панель вкладок с заданиями
        TabPane tabPane = new TabPane(
                new Tab("Задание 1", new Task1Pane()),
                new Tab("Задание 2", new Task2Pane()),
                new Tab("Задание 3", new Task3Pane()),
                new Tab("Задание 4", new Task4Pane())
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // запрет закрытия вкладок

        // добавляем заголовок и вкладки в окно
        notepadWindow.getChildren().addAll(titleBar, tabPane);

        // размещаем окно по центру контейнера
        root.getChildren().add(notepadWindow);
        StackPane.setAlignment(notepadWindow, Pos.CENTER);

        // сцена приложения
        Scene scene = new Scene(root, 640, 480);

        // подключаем CSS‑стили
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm()
        );

        // настройка и показ окна Stage
        stage.setScene(scene);
        stage.setTitle("MyCourseWork");
        stage.setResizable(false); // запрет изменения размеров
        stage.show();
    }
}