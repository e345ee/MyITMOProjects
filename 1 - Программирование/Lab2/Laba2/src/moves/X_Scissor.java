package moves;

import ru.ifmo.se.pokemon.*;

public class X_Scissor extends PhysicalMove {
    public X_Scissor() {
        super(Type.BUG, 80, 100);
    }


    @Override
    protected String describe() {
        return "редко да метко бьёт с помощью X_Scissor";
    }
}
