import MyNewException.ManAlreadyInLobby;
import MyNewException.NoSuchManInLobby;

public class Rocket extends Abs_SpaceShip implements Diagnose {
    public Rocket(String name, int hp) {
        super(name, hp);
        System.out.println("Ракета - " + this.getName() + " стоит неподалёку");
        System.out.println("Её прочность сотавляет: " + this.getBaseHp());
    }

    public void showLobby() {
        if (this.getPassengers() == 0) {
            System.out.println("Рокета пустая");
        } else {
            String res = "";
            for(Abs_Human i: lobby) {

                if (res.equals(""))
                    res = i.getName();
                else
                    res += ", " + i.getName();
            }
            System.out.println(res + " сел и улетает на ракете ");
            System.out.println("В ракете " + this.getPassengers() + " пассажиров");

        }
    }

    @Override
    public void removeGuy(Abs_Human obj) throws NoSuchManInLobby {
        if(lobby.contains(obj)) {
            for (int i = 0; i < lobby.toArray().length; i++) {
                if (lobby.toArray()[i] == obj) {
                    lobby.remove(i);
                    System.out.println(obj.getName() + " покинул лодку и пошагал по берегу");
                    showLobby();
                }
            }
        }
                else{
                    throw new NoSuchManInLobby(obj.getName() + " не присутствует в лодке");
                }
    }



    public String toString() {
        return "Sniper{Name = " + this.getName() + "; HP = " + this.getHp() + "; Base_HP = " + this.getBaseHp() + "; Passengeers = " + this.getPassengers() + "}";
    }

    public void diagnose(Abs_SpaceShip o) {
        System.out.println("Ракета анализирует объект: " + o.getName());
        System.out.println(o.toString());
        double damage_percentage = 100.0 - ((double)o.getHp() + 0.0) / (double)o.getBaseHp() * 100.0;
        System.out.println("Процент повреждений корабля " + o.getName() + " равен: " + damage_percentage);
        o.setDamage(damage_percentage);
    }



    Shoot shot = new Shoot(){
        @Override
        public void shoot(Abs_SpaceShip o) {
            o.setHp(o.getHp()-90);
            System.out.println(getName()+" Взорвал " + o.getName());
        }

        @Override
        public void shoot(Abs_Human o) {
            o.setHp(o.getHp()-90);
            System.out.println(getName()+" Взорвал " + o.getName());
        }
    };

    public void bombing(Abs_Human o){
        shot.shoot(o);
    }
}