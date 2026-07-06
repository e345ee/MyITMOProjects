
package pokemons;

import moves.*;
import ru.ifmo.se.pokemon.*;

    public class Heatmor extends Pokemon {
        public Heatmor(String name, int level) {
            super(name, level);
            setStats(85, 97, 66, 105, 66, 65);
            setType(Type.FIRE);
           setMove(new Fire_Lash(), new Will_O_Wisp(), new Fury_Swipes(), new Flare_Blitz());
        }
    }


