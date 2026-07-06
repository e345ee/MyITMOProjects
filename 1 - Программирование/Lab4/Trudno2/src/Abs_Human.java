public abstract class Abs_Human {
    protected String name;
    protected int hp;
    protected int base_HP;

    public Abs_Human(String name) {
        this.name = name;
    }

    public Abs_Human(String name, int hp) {
        this.name = name;
        this.hp = hp;
        this.base_HP = this.hp;
    }

    public int getHp() {
        return this.hp;
    }

    public int getBaseHp() {
        return this.base_HP;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public String getName() {
        return this.name;
    }
    public void think(String text) {
        System.out.println(this.getName() + " думает " + "\"" + text + "\"");
    }
    public int hashCode() {
        return super.hashCode() + this.getName().hashCode();
    }

    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }
}
