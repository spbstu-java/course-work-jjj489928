package spbstu.oop.v30321.lr1;

//стратегия - на лошади
public class HorseRideStrategy implements MoveStrategy {
    public void move(String from, String to) {
        System.out.println("Герой скачет на лошади из точки \"" + from + "\" в точку \"" + to + "\"...");
    }
}