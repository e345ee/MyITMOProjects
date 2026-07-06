package moves;

import ru.ifmo.se.pokemon.*;

public class Sand_Attack extends PhysicalMove {
    public Sand_Attack() {
        super(Type.NORMAL, 0, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        p.setMod(Stat.ACCURACY, -1);
    }
    @Override
    protected String describe() {
        return "снижает точность противника с помощью Sand_Attack";
    }
}