package moves;

import ru.ifmo.se.pokemon.*;

public class Fire_Lash extends PhysicalMove {
    public Fire_Lash() {
        super(Type.FIRE, 80, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        p.setMod(Stat.DEFENSE, -1);
    }


    @Override
    protected String describe() {
        return "oн хорош, он использует Fire_Lash";
    }
}
