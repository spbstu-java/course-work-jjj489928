package spbstu.oop.v30321.lr1;

//определяем героя
public class Hero {
    private MoveStrategy moveStrategy; //текущая стратегия перемещения
    private String currentLocation; //текущая локация

    //конструктор
    public Hero(MoveStrategy moveStrategy) {
        this.moveStrategy = moveStrategy;
        this.currentLocation = "Замок"; //начальная локация
    }

    //устанавливаем новую стратегию перемещения
    public void setMoveStrategy(MoveStrategy moveStrategy) {
        this.moveStrategy = moveStrategy;
    }

    //возвращаем текущее местоположение героя
    public String getCurrentLocation() {
        return currentLocation;
    }

    //перемещаем героя в указанную точку, исп. текущую стратегию
    public void move(String to) {
        moveStrategy.move(currentLocation, to); //hero передает выполнение метода move конкретной стратегии
        currentLocation = to; //обновляем текущую локацию
    }
}
