package spbstu.oop.v30321.lr2;

import java.lang.annotation.*;

//аннотация для указания кол-ва вызовов метода
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Annotation {
    int value();
}
