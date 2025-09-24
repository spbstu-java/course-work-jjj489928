package spbstu.oop.v30321.lr3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); //для ввода
        //просто отступ для вывода
        System.out.println();

        Translator translator = null;     //объект переводчика
        boolean dictionaryLoaded = false; //флаг загрузки словаря

        //запрос и проверка файла словаря
        while (!dictionaryLoaded) {
            System.out.print("Файл словаря (или exit для выхода): ");
            String dictionaryFile = scanner.nextLine().trim(); //читаем путь к словарю

            //выход из проги
            if (dictionaryFile.equalsIgnoreCase("exit")) {
                System.out.println("Вы вышли из \"Переводчика\".");
                scanner.close();
                return;
            }

            if (dictionaryFile.isEmpty()) { //пустое имя файла
                System.out.println("Имя файла не может быть пустым!");
                continue;
            }

            File file = new File(dictionaryFile); //создаем объект файла

            if (!file.exists()) { //файл не найден
                System.out.println("Файл не найден!");
                continue;
            }

            if (!file.isFile()) { //не файл
                System.out.println("Это не файл!");
                continue;
            }

            //создаем переводчик с этим словарем
            translator = new Translator(file.getAbsolutePath());

            try {
                System.out.println("Загрузка словаря...");
                translator.loadDictionary(); //загружаем словарь из файла
                System.out.println("Словарь загружен! Записей: " + translator.getDictionarySize());
                dictionaryLoaded = true;     //словарь успешно загружен

            } catch (InvalidFileFormatException e) { //ошибка формата словаря
                System.err.println("Ошибка формата файла: " + e.getMessage());
                if (e.getCause() != null) { //если есть причина - печатаем стек
                    System.err.println("Причина: ");
                    e.getCause().printStackTrace(System.err);
                }
            } catch (FileReadException e) { //ошибка чтения словаря
                System.err.println("Ошибка чтения файла: " + e.getMessage());
            }
        }

        //цикл выбора способа ввода
        while (true) {
            System.out.println("\nСпособ ввода текста:");
            System.out.println("1 - Ввод с клавиатуры");
            System.out.println("2 - Чтение из файла");
            System.out.println("exit - Выход");
            System.out.print("Выбор: ");

            String choice = scanner.nextLine().trim(); //читаем выбор юзера

            if (choice.equalsIgnoreCase("exit")) { //выход из проги
                break;
            }

            String inputText; //переменная для исходного текста

            if (choice.equals("2")) { //чтение текста из файла
                System.out.print("Файл с текстом (или back для возврата): ");
                String textFile = scanner.nextLine().trim(); //путь к файлу с текстом

                if (textFile.equalsIgnoreCase("back")) { //возврат
                    continue;
                }

                if (textFile.equalsIgnoreCase("exit")) { //выход
                    break;
                }

                try {
                    inputText = readTextFromFile(textFile); //читаем текст из файла
                    System.out.println("Текст загружен: " + inputText);

                    //переводим текст, выводим результат
                    String translation = translator.translateText(inputText);
                    System.out.println("Перевод: " + translation);

                } catch (FileReadException e) { //ошибка чтения файла с текстом
                    System.err.println("Ошибка чтения файла: " + e.getMessage());
                }

            } else if (choice.equals("1")) { //ручной ввод текста
                System.out.println("\nТекст для перевода (или back для возврата, exit для выхода):");

                while (true) {
                    System.out.print("> "); //приглашение к вводу
                    inputText = scanner.nextLine().trim(); //читаем текст введенный

                    if (inputText.equalsIgnoreCase("back")) { //возврат
                        break;
                    }

                    if (inputText.equalsIgnoreCase("exit")) { //выход
                        System.out.println("Вы вышли из \"Переводчика\".");
                        scanner.close();
                        return;
                    }

                    if (inputText.isEmpty()) { //пустая строка - пропуск
                        continue;
                    }

                    //перевод текста
                    String translation = translator.translateText(inputText);
                    System.out.println("Перевод: " + translation);
                }

            } else { //не то нажали
                System.out.println("Неверный выбор! Попробуйте снова.");
            }
        }

        //завершаем работу
        System.out.println("Вы вышли из \"Переводчика\".");
        scanner.close();
    }

    //читаем текст из файла
    private static String readTextFromFile(String filename) throws FileReadException {
        File file = new File(filename); //создаем объект файла
        if (!file.exists()) {           //файл не найден
            throw new FileReadException("Файл не найден: " + filename);
        }

        //открываем файл для чтения
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder(); //буфер для текста
            String line;                                 //текущая строка

            while ((line = reader.readLine()) != null) { //читаем построчно
                if (content.length() > 0) { //добавляем перенос, если это не первая строка
                    content.append(System.lineSeparator());
                }
                content.append(line);  //добавляем строку в буфер
            }

            return content.toString(); //возвращаем весь текст

        } catch (FileNotFoundException e) { //файл исчез или недоступен
            throw new FileReadException("Файл не найден: " + filename, e);
        } catch (IOException e) {           //ошибка чтения
            throw new FileReadException("Ошибка чтения файла: " + filename, e);
        }
    }
}