package spbstu.oop.v30321.lr1;

import java.util.Scanner;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); //для ввода

        //локации для перемещения
        String[] locations = {
                "Замок", "Лес", "Болото",
                "Подземелье", "Таверна", "Озеро",
        };
        //для выбора случайной локации
        Random random = new Random();

        //создаем героя с начальной стратегией
        Hero hero = new Hero(new WalkStrategy());
        System.out.println("\nИгра началась! Герой отправляется в путь пешком из точки \"Замок\".");

        //цикл
        while (true) {
            String to;
            //выбор случайной локации (отличной от текущей)
            do {
                to = locations[random.nextInt(locations.length)];
            } while (to.equals(hero.getCurrentLocation()));

            //выбор способа перемещения
            System.out.println("\nСпособ перемещения в \"" + to + "\":");
            System.out.println("1 - Идти пешком.");
            System.out.println("2 - Ехать на лошади.");
            System.out.println("3 - Лететь.");
            System.out.println("0 - Выход из игры.");
            System.out.println("Выбор: ");

            //проверка на правильный ввод
            if (!scanner.hasNextInt()) {
                System.out.println("Ошибка! Только ввод числа!");
                scanner.next(); //очищаем неправильный ввод
                continue; //перезапуск цикла
            }

            //читаем выбор юзера
            int choice = scanner.nextInt();
            //выход из игры
            if (choice == 0) break;

            //выбор стратегии перемещения
            switch (choice) {
                case 1:
                    //пешком
                    hero.setMoveStrategy(new WalkStrategy());
                    break;
                case 2:
                    //на лошади
                    hero.setMoveStrategy(new HorseRideStrategy());
                    break;
                case 3:
                    //полет
                    hero.setMoveStrategy(new FlyStrategy());
                    break;
                default:
                    System.out.print("\nСтратегия прежняя.");
            }
            //просто для отступа
            System.out.println();

            //выполняем перемещение
            hero.move(to);
        }
        scanner.close();
        System.out.println("\nКонец путешествия! Герой пришел в точку \"" + hero.getCurrentLocation() + "\".");
    }
}