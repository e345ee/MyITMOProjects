
import pokemons.*;
import ru.ifmo.se.pokemon.*;

public class Draka {
    public static void main(String[] args) {
        Battle b = new Battle();
        Heatmor p1 = new Heatmor("Шериф",5);
        Flabebe p2 = new Flabebe("Полицейский", 10);;
        Eevee p3 = new Eevee("Комиссар",10);
        Floette p4 = new Floette("Дракула", 10);;
        Leafeon p6 = new Leafeon("Демон", 10);
        Florges p5 = new Florges("Оборотень",10);

        b.addAlly(p1);
        b.addAlly(p2);
        b.addAlly(p3);
        b.addFoe(p4);
        b.addFoe(p5);
        b.addFoe(p6);
        b.go();
    }
}

