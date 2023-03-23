package moves;

import ru.ifmo.se.pokemon.*;

public class Confind extends StatusMove {
    public Confind() {
        super(Type.NORMAL, 0, 1);
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        p.setMod(Stat.SPECIAL_ATTACK, -1);
    }


    @Override
    protected String describe() {
        return "Уменьшает Специальную аттаку противника используя Confind";
    }
}
