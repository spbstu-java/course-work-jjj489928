package spbstu.oop.v30321.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import spbstu.oop.v30321.lr3.*;

import java.io.*;
import java.nio.charset.Charset;

public class Task3Pane extends VBox {
    private Translator translator; // объект переводчика
    private PrintStream originalOut, originalErr; // для сохранения стандартных потоков вывода

    public Task3Pane() {
        setSpacing(10);
        setAlignment(Pos.CENTER); // все элементы по центру

        // поле ввода текста для перевода
        TextArea inputArea = new TextArea();
        inputArea.setPromptText("Введите текст для перевода...");
        inputArea.setWrapText(true);
        inputArea.setPrefHeight(100);
        inputArea.setMaxHeight(100);

        // поле вывода результатов
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(100);
        outputArea.setMaxHeight(100);

        // две кнопки рядом (выбор словаря и текста)
        Button chooseDictBtn = createChooseDictButton(outputArea);
        Button chooseTextBtn = createChooseTextButton(inputArea, outputArea);

        HBox topButtons = new HBox(10, chooseDictBtn, chooseTextBtn);
        topButtons.setAlignment(Pos.CENTER);

        // оборачиваем в VBox, чтобы задать отступ сверху
        VBox buttonsWrapper = new VBox(topButtons);
        buttonsWrapper.setAlignment(Pos.CENTER);
        buttonsWrapper.setPadding(new Insets(10, 0, 0, 0));

        // кнопка перевода
        Button translateBtn = createTranslateButton(inputArea, outputArea);

        // добавляем элементы в нужном порядке
        getChildren().addAll(buttonsWrapper, inputArea, translateBtn, outputArea);
    }

    // кнопка выбора словаря
    private Button createChooseDictButton(TextArea outputArea) {
        Button btn = new Button("Выбрать словарь");
        btn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Выбор файла словаря");
            File file = fc.showOpenDialog(getScene().getWindow());
            if (file != null) {
                translator = new Translator(file.getAbsolutePath());
                try {
                    redirectOutput(outputArea); // перенаправляем вывод в TextArea
                    System.out.println("Загрузка словаря...");
                    translator.loadDictionary();
                    System.out.println("Словарь загружен! Записей: " + translator.getDictionarySize());
                } catch (InvalidFileFormatException ex) {
                    System.err.println("Ошибка формата файла: " + ex.getMessage());
                    if (ex.getCause() != null) {
                        System.err.println("Причина: ");
                        ex.getCause().printStackTrace(System.err);
                    }
                } catch (FileReadException ex) {
                    System.err.println("Ошибка чтения файла: " + ex.getMessage());
                } finally {
                    restoreOutput(); // возвращаем стандартные потоки
                }
            }
        });
        return btn;
    }

    // кнопка выбора текста для перевода
    private Button createChooseTextButton(TextArea inputArea, TextArea outputArea) {
        Button btn = new Button("Выбрать текст");
        btn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Выбор файла текста");
            File file = fc.showOpenDialog(getScene().getWindow());
            if (file != null) {
                try {
                    String text = readTextFromFile(file.getAbsolutePath());
                    inputArea.setText(text); // загружаем текст в поле ввода
                } catch (FileReadException ex) {
                    redirectOutput(outputArea);
                    System.err.println("Ошибка чтения файла: " + ex.getMessage());
                    restoreOutput();
                }
            }
        });
        return btn;
    }

    // кнопка перевода
    private Button createTranslateButton(TextArea inputArea, TextArea outputArea) {
        Button btn = new Button("Перевести");
        btn.setOnAction(e -> {
            if (translator == null) {
                redirectOutput(outputArea);
                System.out.println("Сначала выберите и загрузите словарь!");
                restoreOutput();
                return;
            }
            String text = inputArea.getText().trim();
            if (text.isEmpty()) {
                redirectOutput(outputArea);
                System.out.println("Введите текст или выберите файл с текстом!");
                restoreOutput();
                return;
            }
            redirectOutput(outputArea);
            String translation = translator.translateText(text); // выполняем перевод
            System.out.println("Перевод: " + translation);
            restoreOutput();
        });
        return btn;
    }

    // чтение текста из файла
    private String readTextFromFile(String filename) throws FileReadException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (content.length() > 0) {
                    content.append(System.lineSeparator());
                }
                content.append(line);
            }
            return content.toString();
        } catch (FileNotFoundException e) {
            throw new FileReadException("Файл не найден: " + filename, e);
        } catch (IOException e) {
            throw new FileReadException("Ошибка чтения файла: " + filename, e);
        }
    }

    // перенаправление вывода в TextArea
    private void redirectOutput(TextArea area) {
        originalOut = System.out;
        originalErr = System.err;
        PrintStream taOut = new PrintStream(new TextAreaOutputStream(area), true, Charset.defaultCharset());
        System.setOut(taOut);
        System.setErr(taOut);
    }

    // восстановление стандартных потоков вывода
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