package moves;

import ru.ifmo.se.pokemon.*;

public class Flare_Blitz extends PhysicalMove {
    public Flare_Blitz()  {super(Type.FIRE, 120, 100);}
    private boolean flag;


    @Override
    public void applyOppEffects(Pokemon p) {
        int chance = (int)(Math.random() * 101);
        if (chance <= 10) flag = true;
        if (flag) {
            Effect.burn(p);
        }
    }

    @Override
    public void applySelfDamage(Pokemon p, double damage) {
        p.setMod(Stat.HP, ((int) Math.round(damage) / 3)); }

    @Override
        protected String describe() {
        if (flag) {return "воспламеняет противника";}
        return "Наносит урон и сам повреждает себя с помощью Flare_Blitz";

    }
}






