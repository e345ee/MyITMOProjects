public class Znaika extends Abs_Human implements Course_Alignment {


    public Znaika(String name, int hp) {
        super(name, hp);
        System.out.println("Человек - " + this.name  + " убегает от преследования!");
    }

    @Override
    public String toString() {
        return "Znaika{" +
                "Name = " + GetName() +
                "HP = " + GetHp() +
                '}';
    }



    @Override
    public void course_alignment(Abs_SpaceShip o) {
        System.out.println(GetName()+ " пытается выровнить курс " + o.GetName());
        if (o.GetDamage() > 80) {
            System.out.println(GetName()+ "урон слишком большой, ракета взрывается нагрузки");
            SetHp(0);
            o.SetHp(0);
            o.SetDamage(100);
        } else if (o.GetDamage()<50) {
            System.out.println(GetName()+ " Успешно выравнивает " + o.GetName());
        }
            else {

                System.out.println("Трудная ситуация для " + GetName());
                int max = 2;
                int min = 1;
                int x = (int)(Math.random()*((max-min)+1))+min;
                switch (x) {
                    case 1 -> System.out.println(GetName() + " Успешно выравнивает " + o.GetName());
                    case 2 -> {
                        System.out.println(GetName() + " не удаётся выровнить курс, врезается в комету и взрывается");
                        SetHp(0);
                        o.SetHp(0);
                        o.SetDamage(100);
                }
            }
        }

    }
}
