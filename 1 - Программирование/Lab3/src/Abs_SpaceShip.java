
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Abs_SpaceShip  {

    protected String name;
    protected int hp;
    protected int passengers;
    protected ArrayList<Abs_Human> lobby = new ArrayList<>();
    protected int base_HP;
    protected double damage;


    public Abs_SpaceShip(){

    }

    public Abs_SpaceShip (String name){
        this.name = name;

    }

    public Abs_SpaceShip (String name, int hp, int passengers){
        this.name = name;
        this.hp = hp;
        this.passengers = passengers;
        this.base_HP  = this.hp;
        this.damage = 0;
    }

    public Abs_SpaceShip(String name, int hp, int passengers, Abs_Human... obj) {
        this.name = name;
        this.hp = hp;
        this.passengers = passengers;
        this.base_HP  = this.hp;
        this.damage = 0;
        this.setLobby(obj);
    }
    public Abs_SpaceShip(String name, int hp, int passengers, double damage, Abs_Human... obj) {
        this.name = name;
        this.hp = hp;
        this.passengers = passengers;
        this.base_HP  = this.hp;
        this.damage = damage;
        this.setLobby(obj);
    }

    protected void setLobby(Abs_Human... obj) {
        int var3 = obj.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Abs_Human i = obj[var4];
            this.lobby.add(i);
            System.out.println(this.lobby);
            this.passengers +=1;
        }

    }



    public abstract void showLobby();

    public int GetHp() {
        return this.hp;
    }
    public void SetHp(int hp) {
        this.hp = hp;
    }
    public int GetPassengers() {
        return this.passengers;
    }
    public int GetBaseHp() {
        return this.base_HP;
    }
    public double GetDamage() {
        return this.damage;
    }
    public void SetDamage(double damage) {
        this.damage = damage;}
    public String GetName() { return this.name;}
    @Override
    public int hashCode() {
        return super.hashCode() + this.GetName().hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }
    public String toString() {
        return null;
    }
}
