import java.util.Iterator;

public class Rocket extends Abs_SpaceShip implements Diagnose, Shoot {

        public Rocket(String name, int hp, int passengers, Abs_Human... obj) {
            super(name, hp, passengers, new Abs_Human[0]);
            this.setLobby(obj);
            System.out.println("Ракета - " + GetName()  + " стоит неподалёку");
            System.out.println("Её прочность сотавляет: " + GetBaseHp());


        }

    public void showLobby() {
        if (GetPassengers() == 0) {
            System.out.println("В ракете никого нет");
        } else {
            String res = "";
            Iterator var2 = this.lobby.iterator();

            while(var2.hasNext()) {
                Abs_Human i = (Abs_Human)var2.next();
                if (res.equals("")) {
                    res = i.GetName();
                } else {
                    res = res + ", " + i.GetName();


                }
            }

            System.out.println(res + " сел и улетает на ракете ");
            System.out.println("В ракете " + GetPassengers() + " пассажиров");
        }

    }

    @Override
    public String toString() {
        return "Sniper{" +
                "Name = " + GetName() +
                "; HP = " + GetHp() +
                "; Base_HP = " + GetBaseHp() +
                "; Passengeers = " + GetPassengers() +
                '}';
    }




    @Override
    public void diagnose(Abs_SpaceShip o) {
            System.out.println("Ракета анализирует объект: " + o.GetName());
            System.out.println(o.toString());
            double damage_percentage;
            damage_percentage = (100-((o.GetHp() + 0.0) / o.GetBaseHp()*100));
            System.out.println("Процент повреждений корабля " + o.GetName() + " равен: " + damage_percentage);
            o.SetDamage(damage_percentage);
    }

    public int GetPassengers() {
        return this.passengers;
    }


    @Override

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


