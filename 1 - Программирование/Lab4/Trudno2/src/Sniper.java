

import java.io.PrintStream;

public class Sniper extends Abs_Human implements Shoot {
    public Sniper(String name, int hp) {
        super(name, hp);
        System.out.println("Снайпер - " + this.name + " настраивает свое ружьё");
    }

    public String toString() {
        return "Sniper{HP=" + this.hp + "Name=" + this.name + "}";
    }

    public void shoot(Abs_SpaceShip o) {
        int max = 3;
        int min = 1;
        int x = (int)(Math.random() * (double)(max - min + 1)) + min;
        PrintStream var10000;
        String var10001;
        switch (x) {
            case 1:
                var10000 = System.out;
                var10001 = this.getName();
                var10000.println(var10001 + " Стреляет с помощью: " + TypeOfBullet.BULLETX2);
                o.setHp( (o.getHp() - 30));
                System.out.println(o.getName() + " Получил урон равный: 30");
                break;
            case 2:
                var10000 = System.out;
                var10001 = this.getName();
                var10000.println(var10001 + " Стреляет с помощью: " + TypeOfBullet.BULLETX3);
                o.setHp( (o.getHp() - 60));
                System.out.println(o.getName() + " Получил урон равный: 60");
                break;
            case 3:
                var10000 = System.out;
                var10001 = this.getName();
                var10000.println(var10001 + " Стреляет с помощью: " + TypeOfBullet.BOOMB);
                o.setHp( (o.getHp() - 90));
                System.out.println(o.getName() + " Получил урон равный: 90");
        }

    }

    @Override
    public void shoot(Abs_Human o) {
        System.out.println(getName()+ "не будет убивать так людей, в особенности" + o.getName());
    }
}