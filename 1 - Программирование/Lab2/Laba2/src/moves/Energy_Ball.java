package moves;

import ru.ifmo.se.pokemon.*;



public class Energy_Ball extends SpecialMove {
    public Energy_Ball()  {super(Type.GRASS, 90, 100);}

        private boolean flag;

        @Override
        public void applyOppEffects(Pokemon p) {
            int chance = (int)(Math.random() * 101);
            if (chance <= 10) flag = true;
            if (flag) {
                p.setMod(Stat.SPECIAL_DEFENSE, -1);
            }
        }
    @Override
    protected String describe() {
        if (flag) {return ", удача, покемон уменьшает специальную защиту у противника";}
        return "Наносит урон с помощью Energy_Ball";

    }
}
