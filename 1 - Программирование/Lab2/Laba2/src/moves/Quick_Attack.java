package moves;

import ru.ifmo.se.pokemon.*;

public class Quick_Attack extends PhysicalMove {
    public Quick_Attack() {
        super(Type.NORMAL, 40, 100, +1, 1);
    }

  
    @Override
    protected String describe() {
        return "Он же словно молния, аттакует с приоритетом Quick_Attack";
    }
}
