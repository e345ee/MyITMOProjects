package moves;

import ru.ifmo.se.pokemon.*;

public class Will_O_Wisp extends StatusMove {
    public Will_O_Wisp() {
        super(Type.FIRE, 0, 85);
    }



    @Override
    protected void applyOppEffects(Pokemon p) {
        if (!p.hasType(Type.FIRE)) {
            Effect var1 = (new Effect()).condition(Status.BURN).turns(-1);
            var1.stat(Stat.ATTACK, -2).stat(Stat.HP, ((int)p.getStat(Stat.HP) / 8));
            p.setCondition(var1); }


    }



    @Override
    protected String describe() {
        return ", гвоздь мне в кеды, использует Will_O_Wisp";
    }
}
