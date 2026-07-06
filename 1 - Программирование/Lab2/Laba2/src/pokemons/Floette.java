package pokemons;

import moves.*;
import ru.ifmo.se.pokemon.*;

public class Floette extends Flabebe {
    public Floette(String name, int level) {
        super(name, level);
        setStats(54, 45, 47, 75, 98, 52);
        addMove(new Fairy_Wind());
    }
}
