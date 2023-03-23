package moves;

import ru.ifmo.se.pokemon.*;

public class Fury_Swipes extends PhysicalMove {
    public Fury_Swipes()  {super(Type.NORMAL, 18, 80);}

    private int x =(int)(Math.random() *(8 - 1)+1) +1;


    @Override
    public void applyOppDamage(Pokemon def, double damage) {
        switch (x) {
            case 1:
            case 2:
            case 3:
                def.setMod(Stat.HP, 2 * (int) Math.round(damage));
                break;
            case 4:
            case 5:
            case 6:
                def.setMod(Stat.HP, 3 * (int) Math.round(damage));
                break;
            case 7:
                def.setMod(Stat.HP, 4 * (int) Math.round(damage));
                break;
            case 8:
                def.setMod(Stat.HP, 5 * (int) Math.round(damage));
                break;

        }
    }
    @Override
    protected String describe() {
        return "наносит множество ударов с помощью Fury_Swipes";


    }
}
