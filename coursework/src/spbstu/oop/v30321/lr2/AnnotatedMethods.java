package spbstu.oop.v30321.lr2;

public class AnnotatedMethods {
    //публичные методы
    @Annotation(3)
    public void publicMethod1(String message) {
        System.out.println("Публичный метод 1: " + message);
    }

    public void publicMethod2(int number) {
        System.out.println("Публичный метод 2: " + number);
    }

    //защищенные методы
    @Annotation(2)
    protected void protectedMethod1(double value) {
        System.out.println("Защищенный метод 1: " + value);
    }

    @Annotation(4)
    protected String protectedMethod2(String a, int b) {
        String result = b + " " + a;
        System.out.println("Защищенный метод 2: " + b + " + " + a);
        return result;
    }

    //приватные методы
    private void privateMethod1(boolean flag) {
        System.out.println("Приватный метод 1: " + flag);
    }

    @Annotation(5)
    private int privateMethod2(int x, int y) {
        int sum = x + y;
        System.out.println("Приватный метод 2: " + x + " + " + y);
        return sum;
    }

    @Annotation(3)
    private void privateMethod3(String fruit, int age, double weight, boolean isRipe) {
        String quality = isRipe ? "спелое" : "незрелое";
        System.out.println("Приватный метод 3: " + fruit + ", " + age + " дней, " + weight + " кг, " + quality);
    }
}