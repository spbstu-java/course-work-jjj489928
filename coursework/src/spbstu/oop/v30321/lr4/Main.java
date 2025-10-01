package spbstu.oop.v30321.lr4;

import java.util.*;
import java.util.stream.*;

public class Main {

    //метод вычисляет среднее значение списка чисел
    public static double average(List<Integer> numbers) {
        return numbers.stream()
                .mapToInt(Integer::intValue) //каждый integer превращаем в int
                .average()                   //вычисляем среднее, получаем optionaldouble
                .orElse(0.0);          //если список пуст - должен возвращать 0.0 (сейчас не сработает)
    }

    //метод преобразует каждую строку в верхний регистр и добавляет префикс _new_
    public static List<String> toUpperWithPrefix(List<String> strings) {
        return strings.stream()                             //создаем поток строк
                .map(s -> "_new_" + s.toUpperCase()) //переводим в нужный регистр, добавляем префикс
                .collect(Collectors.toList());             //собираем результат в список
    }

    //метод возвращает квадраты уникальных чисел из списка
    public static List<Integer> uniqueSquares(List<Integer> numbers) {
        //считаем кол-во вхождений каждого числа
        Map<Integer, Long> freq = numbers.stream()
                .collect(Collectors.groupingBy(
                        n -> n,        //ключ - само число
                        LinkedHashMap::new,   //сохраняем порядок добавления
                        Collectors.counting() //значение - кол-во вхождений
                ));
        return numbers.stream()                          //снова идем по списку
                .filter(n -> freq.get(n) == 1)    //оставляем те, которые встречаются 1 раз
                .map(n -> {
                    try {
                        return Math.multiplyExact(n, n); //возводим в квадрат
                    } catch (ArithmeticException e) {
                        //если переполнение - выбрасываем сообщение
                        throw new ArithmeticException(
                                "переполнение при возведении " + n + " в квадрат."
                        );
                    }
                })
                .collect(Collectors.toList());           //собираем результат в список
    }

    //метод возвращает последний элемент коллекции
    public static <T> T lastElement(Collection<T> collection) {
        return collection.stream()                       //создаем поток элементов
                .reduce((first, second) -> second) //берем последний элемент
                //если пусто - исключение
                .orElseThrow(() -> new NoSuchElementException("коллекция пуста."));
    }

    //метод суммирует все четные числа в массиве
    public static int sumOfEven(int[] array) {
        return Arrays.stream(array)          //создаем поток int из массива
                .filter(n -> n % 2 == 0) //оставляем только четные
                .sum();                      //суммируем
    }

    //метод преобразует список строк в map
    public static Map<Character, String> toMap(List<String> strings) {
        return strings.stream()                  //создаем поток строк
                .filter(s -> !s.isEmpty()) //отбрасываем пустые строки
                .collect(Collectors.toMap(
                        s -> s.charAt(0),              //ключ - первый символ
                        s -> s.substring(1), //значение - остальная часть строки
                        (v1, v2) -> {           //обработка конфликта ключей
                            throw new IllegalStateException(
                                    "конфликт ключей: '" + v1 + "' и '" + v2 + "'."
                            );
                        },
                        LinkedHashMap::new       //сохраняем порядок вставки
                ));
    }

    //универсальный ввод чисел
    private static List<Integer> inputIntegerListUniversal(Scanner sc) {
        while (true) {
            System.out.print("Введите числа через пробел (или menu для возврата назад): ");
            String line = sc.nextLine().trim(); //читаем строку и убираем пробелы по краям
            //если menu - выходим
            if (line.equalsIgnoreCase("menu")) return null;
            try {
                return Arrays.stream(line.split("\\s+")) //разбиваем по пробелам
                        .map(Integer::parseInt)                //парсим каждую подстроку в integer
                        .collect(Collectors.toList());         //собираем в список
            } catch (NumberFormatException e) {
                //если хотя бы один элемент не число - выводим ошибку и повторяем ввод
                System.out.println("Ошибка: введите только целые числа (или menu для возврата назад).");
            }
        }
    }

    //универсальный ввод строк
    //allowEmpty = true - разрешает пустой список (для метода 4)
    //allowEmpty = false - требует хотя бы одну строку
    private static List<String> inputStringList(Scanner sc, boolean allowEmpty) {
        while (true) {
            System.out.print("Введите строки через пробел (или menu для возврата назад): ");
            String line = sc.nextLine().trim(); //читаем строку и убираем пробелы по краям
            //если menu - выходим
            if (line.equalsIgnoreCase("menu")) return null;

            //разбиваем по пробелам
            List<String> items = Arrays.stream(line.split("\\s+"))
                    .filter(s -> !s.isEmpty()) //убираем пустые элементы
                    .collect(Collectors.toList());   //собираем в список

            //если список не пустой или пустота разрешена
            if (!items.isEmpty() || allowEmpty) {
                return items;    //возвращаем результат
            } else {
                //если пустой ввод запрещен - выводим ошибку и повторяем
                System.out.println("Ошибка: введите хотя бы одну строку (или menu для возврата назад).");
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); //для чтения ввода

        while (true) {
            System.out.println();
            System.out.println("1. Среднее значение целых чисел");
            System.out.println("2. Строки в верхний регистр с префиксом");
            System.out.println("3. Квадраты уникальных элементов");
            System.out.println("4. Последний элемент коллекции");
            System.out.println("5. Сумма четных чисел массива");
            System.out.println("6. Преобразование строк в Map");
            System.out.println("0. Выход");
            System.out.print("Метод: ");

            String choice = sc.nextLine().trim(); //читаем выбор юзера

            switch (choice) {
                //среднее значение
                case "1": {
                    while (true) {
                        List<Integer> nums = inputIntegerListUniversal(sc); //ввод чисел
                        if (nums == null) break;                            //выход назад к выбору
                        System.out.println("Результат: " + average(nums));  //вывод среднего
                    }
                    break;
                }
                //строки в верхний регистр с префиксом
                case "2": {
                    while (true) {
                        List<String> strs = inputStringList(sc, false); //ввод строк
                        if (strs == null) break;
                        System.out.println("Результат: " + toUpperWithPrefix(strs));
                    }
                    break;
                }
                //квадраты уникальных элементов
                case "3": {
                    while (true) {
                        //ввод списка чисел
                        List<Integer> nums = inputIntegerListUniversal(sc);
                        if (nums == null) break; //выход назад
                        try {
                            //вывод квадратов уникальных чисел
                            System.out.println("Результат: " + uniqueSquares(nums));
                        } catch (ArithmeticException e) {
                            //сообщение об ошибке при переполнении
                            System.out.println("Ошибка: " + e.getMessage());
                        }
                    }
                    break;
                }
                //последний элемент коллекции
                case "4": {
                    while (true) {
                        //вводим список строк, разрешая пустой список
                        List<String> list = inputStringList(sc, true);
                        if (list == null) break; //выход назад
                        try {
                            //пытаемся получить последний элемент списка
                            System.out.println("Результат: " + lastElement(list));
                        } catch (NoSuchElementException e) {
                            //если список пуст - сообщение об ошибке
                            System.out.println("Ошибка: " + e.getMessage());
                        }
                    }
                    break;
                }
                //сумма четных чисел массива
                case "5": {
                    while (true) {
                        //вводим список чисел
                        List<Integer> nums = inputIntegerListUniversal(sc);
                        if (nums == null) break; //выход назад
                        //преобразуем список integer в массив int
                        int[] arr = nums.stream().mapToInt(Integer::intValue).toArray();
                        //выводим сумму чисел четных
                        System.out.println("Результат: " + sumOfEven(arr));
                    }
                    break;
                }
                //преобразование строк в map
                case "6": {
                    while (true) {
                        //вводим список строк, пустой ввод запрещен
                        List<String> strs = inputStringList(sc, false);
                        if (strs == null) break; //выход назад
                        try {
                            //преобразуем список в map и выводим
                            System.out.println("Результат: " + toMap(strs));
                        } catch (IllegalStateException e) {
                            //если ключи совпали - сообщение об ошибке
                            System.out.println("Ошибка: " + e.getMessage());
                        }
                    }
                    break;
                }
                //выход из проги
                case "0": {
                    System.out.println("Выход из программы...");
                    return;
                }
                default:
                    //если введено не то при выборе
                    System.out.println("Неверный выбор!");
            }
        }
    }
}