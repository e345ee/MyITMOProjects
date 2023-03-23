public abstract class Abs_Human  {
    protected String name;
    protected int hp;
    protected int base_HP;


    public Abs_Human (String name){
        this.name = name;

    }

    public Abs_Human (String name, int hp){
        this.name = name;
        this.hp = hp;
        this.base_HP = this.hp;
    }



    public int GetHp() {
        return hp;
    }

    public int GetBaseHp() {
        return base_HP;
    }

    public void SetHp(int hp) {
        this.hp = hp;
    }

    public String GetName() {
        return this.name;
    }


    @Override
    public int hashCode() {
        return super.hashCode() + this.GetName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }



   /* public abstract String toString();*/
}
