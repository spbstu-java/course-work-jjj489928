package spbstu.oop.v30321.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import spbstu.oop.v30321.lr4.Main;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class Task4Pane extends VBox {
    private PrintStream originalOut, originalErr; // для сохранения стандартных потоков вывода

    public Task4Pane() {
        setSpacing(10);
        setAlignment(Pos.CENTER); // все элементы по центру

        // подпись + комбо-бокс для выбора метода
        Label methodLabel = new Label("Выберите метод:");
        ComboBox<String> methodBox = new ComboBox<>();
        methodBox.getItems().addAll(
                "1. Среднее значение целых чисел",
                "2. Строки в верхний регистр с префиксом",
                "3. Квадраты уникальных элементов",
                "4. Последний элемент коллекции",
                "5. Сумма четных чисел массива",
                "6. Преобразование строк в Map"
        );
        methodBox.setValue("1. Среднее значение целых чисел"); // значение по умолчанию

        // строка с подписью и комбо-бокс
        HBox methodRow = new HBox(5);
        methodRow.setAlignment(Pos.CENTER);
        methodRow.getChildren().addAll(methodLabel, methodBox);

        // оборачиваем в VBox, чтобы задать отступ сверху
        VBox methodWrapper = new VBox(methodRow);
        methodWrapper.setAlignment(Pos.CENTER);
        methodWrapper.setPadding(new Insets(10, 0, 0, 0));

        // поле ввода данных
        TextArea inputArea = new TextArea();
        inputArea.setPromptText("Введите данные через пробел...");
        inputArea.setWrapText(true);
        inputArea.setMaxHeight(100);

        // кнопка запуска
        Button runButton = new Button("Выполнить задание");

        // поле вывода результатов
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setMaxHeight(100);

        // логика кнопки
        runButton.setOnAction(e -> {
            redirectOutput(outputArea); // перенаправляем вывод в TextArea

            String choice = methodBox.getValue().substring(0, 1); // номер выбранного метода
            String input = inputArea.getText().trim();

            try {
                switch (choice) {
                    case "1": { // среднее значение
                        List<Integer> nums = parseIntegers(input);
                        System.out.println("Результат: " + Main.average(nums));
                        break;
                    }
                    case "2": { // строки в верхний регистр с префиксом
                        List<String> strs = parseStrings(input, false);
                        System.out.println("Результат: " + Main.toUpperWithPrefix(strs));
                        break;
                    }
                    case "3": { // квадраты уникальных элементов
                        List<Integer> nums = parseIntegers(input);
                        try {
                            System.out.println("Результат: " + Main.uniqueSquares(nums));
                        } catch (ArithmeticException ex) {
                            System.err.println("Ошибка: " + ex.getMessage());
                        }
                        break;
                    }
                    case "4": { // последний элемент коллекции
                        List<String> strs = parseStrings(input, true);
                        try {
                            System.out.println("Результат: " + Main.lastElement(strs));
                        } catch (NoSuchElementException ex) {
                            System.err.println("Ошибка: " + ex.getMessage());
                        }
                        break;
                    }
                    case "5": { // сумма четных чисел массива
                        List<Integer> nums = parseIntegers(input);
                        int[] arr = nums.stream().mapToInt(Integer::intValue).toArray();
                        System.out.println("Результат: " + Main.sumOfEven(arr));
                        break;
                    }
                    case "6": { // преобразование строк в Map
                        List<String> strs = parseStrings(input, false);
                        try {
                            System.out.println("Результат: " + Main.toMap(strs));
                        } catch (IllegalStateException ex) {
                            System.err.println("Ошибка: " + ex.getMessage());
                        }
                        break;
                    }
                }
            } catch (NumberFormatException ex) {
                System.err.println("Ошибка: введите корректные числовые данные.");
            } catch (IllegalArgumentException ex) {
                System.err.println("Ошибка: " + ex.getMessage());
            }

            restoreOutput(); // возвращаем стандартные потоки
        });

        // добавляем элементы в VBox
        getChildren().addAll(methodWrapper, inputArea, runButton, outputArea);
    }

    // парсинг строки в список целых чисел
    private List<Integer> parseIntegers(String input) {
        return Arrays.stream(input.trim().split("\\s+"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    // парсинг строки в список строк
    private List<String> parseStrings(String input, boolean allowEmpty) {
        List<String> items = Arrays.stream(input.trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        if (!allowEmpty && items.isEmpty()) {
            throw new IllegalArgumentException("список пуст.");
        }
        return items;
    }

    // перенаправление вывода в TextArea
    private void redirectOutput(TextArea area) {
        originalOut = System.out;
        originalErr = System.err;
        PrintStream taOut = new PrintStream(new TextAreaOutputStream(area), true, Charset.defaultCharset());
        System.setOut(taOut);
        System.setErr(taOut);
    }

    // восстановление стандартных потоков
    private void restoreOutput() {
        System.setOut(originalOut);
        System.setErr(originalErr);
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