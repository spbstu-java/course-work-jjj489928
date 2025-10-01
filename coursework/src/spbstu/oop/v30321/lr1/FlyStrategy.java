package spbstu.oop.v30321.lr1;

//стратегия - полет
public class FlyStrategy implements MoveStrategy {
    public void move(String from, String to) {
        System.out.println("Герой летит из точки \"" + from + "\" в точку \"" + to + "\"...");
    }
}
