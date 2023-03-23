

import MyNewException.ManAlreadyInLobby;

import java.util.ArrayList;

public abstract class Abs_SpaceShip implements LobbyCreator {
    protected String name;
    protected int hp;
    protected ArrayList<Abs_Human> lobby = new ArrayList();
    protected int base_HP;
    protected double damage;

    public Abs_SpaceShip() {
    }

    public Abs_SpaceShip(String name) {
        this.name = name;
    }

    public Abs_SpaceShip(String name, int hp) {
        this.name = name;
        this.hp = hp;
        this.base_HP = this.hp;
        this.damage = 0.0;
    }



    public Abs_SpaceShip(String name, int hp,  double damage,Abs_Human... obj) {
        this.name = name;
        this.hp = hp;
        this.base_HP = this.hp;
        this.damage = damage;
        this.setLobby(obj);

    }

    public void setLobby(Abs_Human... obj)  {
        for(Abs_Human i: obj)
        {lobby.add(i);
                System.out.println(i.getName() + "Летит в корабле");
            }
    }
    public void addGuy(Abs_Human... obj) throws ManAlreadyInLobby {
        for(Abs_Human i: obj)
            if(lobby.contains(i)){
                throw new ManAlreadyInLobby(i.getName()+"Человек уже на корабле");}
            else{
            lobby.add(i);
            System.out.println(i.getName() + "Сел в корабль");
            }

    }


    public abstract void showLobby();

    public int getHp() {
        return this.hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    protected int getPassengers() {
        int count = 0;
        for(Abs_Human i: lobby) {
            count++;
        }
        return count;
    }

    public int getBaseHp() {
        return this.base_HP;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        return super.hashCode() + this.getName().hashCode();
    }

    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }

    public String toString() {
        return null;
    }

}