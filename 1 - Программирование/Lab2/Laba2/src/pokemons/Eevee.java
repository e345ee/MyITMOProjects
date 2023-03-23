package pokemons;

import moves.*;
import ru.ifmo.se.pokemon.*;

public class Eevee extends Pokemon {
    public Eevee(String name, int level) {
        super(name, level);
        setStats(55, 55, 50, 45, 65, 55);
        setType(Type.NORMAL);
       setMove(new Sand_Attack(), new Double_Team(), new Quick_Attack());
    }
}
