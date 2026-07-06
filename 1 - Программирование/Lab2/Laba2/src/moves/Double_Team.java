package moves;

import ru.ifmo.se.pokemon.*;

public class Double_Team extends StatusMove{
    public Double_Team(){
        super(Type.NORMAL,0,1);
    }

    @Override
    protected void applySelfEffects(Pokemon p) {
        p.setMod(Stat.EVASION, 1);
    }

    @Override
    protected String describe() {
        return "использует Double Team ";
    }

}


