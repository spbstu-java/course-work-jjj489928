package spbstu.oop.v30321.lr2;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Main {
    public static void main(String[] args) {
        //просто пустая строка для вывода
        System.out.println();

        //создаем объект класса с методами
        AnnotatedMethods instance = new AnnotatedMethods();
        //получаем инфу о классе
        Class<?> clazz = AnnotatedMethods.class;

        //получаем все методы класса
        Method[] methods = clazz.getDeclaredMethods();

        //перебираем методы
        for (Method method : methods) {
            //пропускаем методы без нужной аннотации
            if (!method.isAnnotationPresent(Annotation.class)) {
                continue;
            }

            //проверяем: только защищенные и приватные методы
            int modifiers = method.getModifiers();
            if (!Modifier.isProtected(modifiers) && !Modifier.isPrivate(modifiers)) {
                continue; //пропускаем публичные методы
            }

            //получаем кол-во вызовов
            Annotation annotation = method.getAnnotation(Annotation.class);
            int times = annotation.value();

            //разрешаем доступ к приватным методам
            method.setAccessible(true);

            System.out.println("Вызываем метод " + method.getName() + " " + times + " раз(а):");

            //вызываем метод нужное кол-во раз
            for (int i = 0; i < times; i++) {
                try {
                    //создаем параметры для метода
                    Object[] parameters= generateParameters(method.getParameterTypes());
                    //вызываем метод с параметрами
                    Object result = method.invoke(instance, parameters);

                    //вызываем результат (если он есть)
                    if (result != null) {
                        System.out.println("Результат: " + result);
                    }

                } catch (Exception e) {
                    //обрабатываем ошибки
                    System.err.println("Ошибка вызова метода " + method.getName() + ":");
                    e.printStackTrace(System.err);
                }
            }
            //просто пустая строка между методами
            System.out.println();
        }
    }

    //создаем массив для хранения значений параметров
    private static Object[] generateParameters(Class<?>[] parameterTypes) {
        //длина массива равна кол-ву параметров метода
        Object[] parameters = new Object[parameterTypes.length];

        //заполняем массив значениями для каждого типа параметра
        for (int i = 0; i < parameterTypes.length; i++) {
            //для каждого параметра получаем значение по умолчанию
            parameters[i] = getDefaultValue(parameterTypes[i]);
        }
        //возвращаем готовые значения для вызова метода
        return parameters;
    }

    //получаем значения по умолчанию для разных типов
    private static Object getDefaultValue(Class<?> type) {
        if (type == int.class) {
            return 7; //целое число
        } else if (type == String.class) {
            return "манго"; //строка
        } else if (type == double.class) {
            return 3.14; //число дробное
        } else if (type == boolean.class) {
            return true; //логическое значение
        } else {
            //если тип не поддерживается
            throw new IllegalArgumentException("Неподдерживаемый тип: " + type.getName());
        }
    }
}