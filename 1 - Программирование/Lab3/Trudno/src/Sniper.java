

public class Sniper extends Abs_Human implements Shoot {


    public Sniper(String name, int hp) {
        super(name, hp);
        System.out.println("Снайпер - " + this.name  + " настраивает свое ружьё");
    }

    @Override
    public String toString() {
        return "Sniper{" +
                "HP=" + this.hp +
                "Name=" + this.name +
                '}';
    }


    public void shoot(Abs_SpaceShip o) {
        int max = 3;
        int min = 1;
        int x = (int)(Math.random()*((max-min)+1))+min;
        switch (x) {
            case 1 -> {
                System.out.println(this.GetName() + " Стреляет с помощью: " + TypeOfBullet.BULLETX2);
                o.SetHp(o.GetHp() - 30);
                System.out.println(o.GetName() + " Получил урон равный: 30");
            }
            case 2 -> {
                System.out.println(this.GetName() + " Стреляет с помощью: " + TypeOfBullet.BULLETX3);
                o.SetHp(o.GetHp() - 60);
                System.out.println(o.GetName() + " Получил урон равный: 60");
            }
            case 3 -> {
                System.out.println(this.GetName() + " Стреляет с помощью: " + TypeOfBullet.BOOMB);
                o.SetHp(o.GetHp() - 90);
                System.out.println(o.GetName() + " Получил урон равный: 90");
            }
        }

    }


}
