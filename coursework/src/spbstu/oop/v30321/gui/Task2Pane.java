package spbstu.oop.v30321.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import spbstu.oop.v30321.lr2.AnnotatedMethods;
import spbstu.oop.v30321.lr2.Annotation;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;

public class Task2Pane extends VBox {

    public Task2Pane() {
        setSpacing(10);
        setAlignment(Pos.CENTER); // все элементы по центру

        // полотно для вывода результатов
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefSize(300, 170); // фиксированный размер
        VBox.setMargin(outputArea, new Insets(53, 0, 0, 0)); // отступ сверху

        // кнопка запуска задания
        Button runButton = new Button("Выполнить задание");
        runButton.setOnAction(e -> {
            outputArea.clear(); // очищаем перед новым запуском
            runReflectionTaskWithCapturedOutput(outputArea);
        });

        // добавляем элементы: сначала полотно, потом кнопку
        getChildren().addAll(outputArea, runButton);
    }

    // запуск задания с выводом в TextArea
    private void runReflectionTaskWithCapturedOutput(TextArea outputArea) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        // перенаправляем вывод в TextArea
        PrintStream taOut = new PrintStream(new TextAreaOutputStream(outputArea), true, Charset.defaultCharset());

        try {
            System.setOut(taOut);
            System.setErr(taOut);

            // объект с аннотированными методами
            AnnotatedMethods instance = new AnnotatedMethods();
            Class<?> clazz = AnnotatedMethods.class;
            Method[] methods = clazz.getDeclaredMethods(); // получаем все методы

            for (Method method : methods) {
                // берем только методы с нашей аннотацией
                if (!method.isAnnotationPresent(Annotation.class)) {
                    continue;
                }
                // проверяем модификаторы: только protected или private
                int modifiers = method.getModifiers();
                if (!Modifier.isProtected(modifiers) && !Modifier.isPrivate(modifiers)) {
                    continue;
                }

                // получаем значение из аннотации (количество вызовов)
                Annotation annotation = method.getAnnotation(Annotation.class);
                int times = annotation.value();
                method.setAccessible(true); // разрешаем доступ к приватным/защищенным

                System.out.println("Вызываем метод " + method.getName() + " " + times + " раз(а):");

                // вызываем метод указанное количество раз
                for (int i = 0; i < times; i++) {
                    try {
                        // генерируем параметры
                        Object[] parameters = generateParameters(method.getParameterTypes());
                        // вызов метода
                        Object result = method.invoke(instance, parameters);

                        if (result != null) {
                            System.out.println("Результат: " + result);
                        }
                    } catch (Exception ex) {
                        System.err.println("Ошибка вызова метода " + method.getName() + ":");
                        ex.printStackTrace(System.err);
                    }
                }
                System.out.println();
            }
        } finally {
            // возвращаем стандартные потоки вывода
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    // генерация параметров для метода по типам
    private Object[] generateParameters(Class<?>[] parameterTypes) {
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = getDefaultValue(parameterTypes[i]);
        }
        return parameters;
    }

    // значения по умолчанию для поддерживаемых типов
    private Object getDefaultValue(Class<?> type) {
        if (type == int.class) {
            return 7;
        } else if (type == String.class) {
            return "манго";
        } else if (type == double.class) {
            return 3.14;
        } else if (type == boolean.class) {
            return true;
        } else {
            throw new IllegalArgumentException("Неподдерживаемый тип: " + type.getName());
        }
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