package spbstu.oop.v30321.gui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import spbstu.oop.v30321.lr1.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Random;

public class Task1Pane extends VBox {
    // возможные локации
    private final String[] locations = {
            "Замок", "Лес", "Болото", "Подземелье", "Таверна", "Озеро"
    };
    private final Random random = new Random();
    private final Hero hero; // главный перс

    public Task1Pane() {
        setSpacing(10);
        setAlignment(Pos.CENTER); // все элементы по центру

        hero = new Hero(new WalkStrategy());

        // подпись + выпадающий список стратегий
        Label strategyLabel = new Label("Выберите стратегию:");
        ComboBox<String> strategyBox = new ComboBox<>();
        strategyBox.getItems().addAll("Пешком", "На лошади", "Полет");
        strategyBox.setValue("На лошади"); // значение по умолчанию

        // горизонтальный контейнер для подписи и списка
        HBox strategyRow = new HBox(5);
        strategyRow.setAlignment(Pos.CENTER);
        strategyRow.getChildren().addAll(strategyLabel, strategyBox);

        // отображение текущей локации
        Label locationLabel = new Label("Текущая локация: " + hero.getCurrentLocation());

        // текстовое поле для вывода действий
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);           // только для чтения
        outputArea.setPrefSize(300, 170); // фиксированный размер

        // кнопка перехода в следующую локацию
        Button nextLocationBtn = createNextButton(strategyBox, locationLabel, outputArea);

        // добавляем все элементы в VBox по порядку
        getChildren().addAll(strategyRow, locationLabel, outputArea, nextLocationBtn);
    }

    // создание кнопки "Следующая локация"
    private Button createNextButton(ComboBox<String> strategyBox, Label locationLabel, TextArea outputArea) {
        Button btn = new Button("Следующая локация");
        btn.setOnAction(e -> {
            // перенаправляем System.out и System.err в TextArea
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;
            PrintStream taOut = new PrintStream(new TextAreaOutputStream(outputArea), true, Charset.defaultCharset());
            System.setOut(taOut);
            System.setErr(taOut);

            try {
                // выбираем случайную новую локацию, отличную от текущей
                String to;
                do {
                    to = locations[random.nextInt(locations.length)];
                } while (to.equals(hero.getCurrentLocation()));

                // устанавливаем стратегию движения в зависимости от выбора
                switch (strategyBox.getValue()) {
                    case "Пешком":
                        hero.setMoveStrategy(new WalkStrategy());
                        break;
                    case "На лошади":
                        hero.setMoveStrategy(new HorseRideStrategy());
                        break;
                    case "Полет":
                        hero.setMoveStrategy(new FlyStrategy());
                        break;
                    default:
                        break;
                }

                // двигаем героя и обновляем метку с текущей локацией
                hero.move(to);
                locationLabel.setText("Текущая локация: " + hero.getCurrentLocation());
            } finally {
                // возвращаем стандартные потоки вывода
                System.setOut(originalOut);
                System.setErr(originalErr);
            }
        });
        return btn;
    }

    // поток, который пишет вывод напрямую в TextArea
    private static class TextAreaOutputStream extends OutputStream {
        private final TextArea area;

        TextAreaOutputStream(TextArea area) {
            this.area = area;
        }

        public void write(int b) {
            area.appendText(String.valueOf((char) b));
        }

        public void write(byte[] b, int off, int len) {
            String text = new String(b, off, len, Charset.defaultCharset());
            area.appendText(text);
        }
    }
}