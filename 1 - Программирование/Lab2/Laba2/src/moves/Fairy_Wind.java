package moves;

import ru.ifmo.se.pokemon.*;



public class Fairy_Wind extends SpecialMove {
    public Fairy_Wind()  {super(Type.FAIRY, 40, 100);}


    @Override
    protected String describe() {
        return "Просто, наносит урон с помощью Fairy_Wind";

    }
}
