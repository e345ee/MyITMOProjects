import java.io.PrintStream;

public class Pilot extends Abs_Human implements Course_Alignment {
    public Pilot(String name, int hp) {
        super(name, hp);
        System.out.println("Мы лицизреем " + this.name);
    }


    public String toString() {
        String var10000 = this.getName();
        return "Znaika{Name = " + var10000 + "HP = " + this.getHp() + "}";
    }

    public void course_alignment(Abs_SpaceShip o) {
        PrintStream var10000 = System.out;
        String var10001 = this.getName();
        var10000.println(var10001 + " пытается выровнить курс " + o.getName());
        if (o.getDamage() > 80.0) {
            System.out.println(this.getName() + "урон слишком большой, ракета взрывается нагрузки");
            this.setHp(0);
            o.setHp((short) 0);
            o.setDamage(100.0);
        } else if (o.getDamage() < 50.0) {
            var10000 = System.out;
            var10001 = this.getName();
            var10000.println(var10001 + " Успешно выравнивает " + o.getName());
        } else {
            System.out.println("Трудная ситуация для " + this.getName());
            int max = 2;
            int min = 1;
            int x = (int)(Math.random() * (double)(max - min + 1)) + min;
            switch (x) {
                case 1:
                    var10000 = System.out;
                    var10001 = this.getName();
                    var10000.println(var10001 + " Успешно выравнивает " + o.getName());
                    break;
                case 2:
                    System.out.println(this.getName() + " не удаётся выровнить курс, врезается в комету и взрывается");
                    this.setHp(0);
                    o.setHp((short) 0);
                    o.setDamage(100.0);
            }
        }

    }
}
