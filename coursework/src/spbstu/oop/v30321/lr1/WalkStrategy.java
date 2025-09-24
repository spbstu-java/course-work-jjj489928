package spbstu.oop.v30321.lr1;

//стратегия - пешком
public class WalkStrategy implements MoveStrategy {
    public void move(String from, String to) {
        System.out.println("Герой идет пешком из точки \"" + from + "\" в точку \"" + to + "\"...");
    }
}