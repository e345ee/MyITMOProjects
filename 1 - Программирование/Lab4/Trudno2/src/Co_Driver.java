import Items.Abs_Items;

import java.util.ArrayList;

public class Co_Driver extends Abs_Human implements Brain_Tasks {
    public Co_Driver(String name) {
        super(name);
        System.out.println("Человек - " + this.name + " ничего не делает");
    }
    public String toString() {
        return "Co_Driver{Name = " + getName() + "}";
    }

    @Override
    public void look(Abs_Place o) {
        System.out.println(getName() + "Видит город " + o.getName());

    }

    @Override
    public void think(String text) throws OutOfMemoryError{
        class OutOfHumanMemoryError extends Error{

            public OutOfHumanMemoryError(String message) {
                super(message);
            }



        }
        if (text.length() > 128) {
            throw new OutOfHumanMemoryError("У стекляшкина нет столько воображения");
        }
        super.think(text);
    }



    public class Inventory  {
        private ArrayList<Abs_Items> items = new ArrayList<>();
        public Inventory( Abs_Items... obj) {
            this.setInventory(obj);
            System.out.println("Инвентарь у " + getName()+ " успешно создан" );
        }
        public void setInventory(Abs_Items... obj) {
            for(Abs_Items i: obj) {
                items.add(i);
            }
        }
        public void addInventory(Abs_Items... obj) {
            for (Abs_Items i : obj) {
                System.out.println(i.getName() + "добавлен в инвентарь");
                items.add(i);
            }
        }

        public void removeInventory(Abs_Items obj)  {

            if(items.contains(obj)) {
                for (int i = 0; i < items.toArray().length; i++) {
                    if (items.toArray()[i] == obj) {
                        items.remove(i);
                        System.out.println(obj.getName() + "Был Взят из инвентаря " + Co_Driver.this.name + " использует его");
                    }}}}

        protected int getItems() {
            int count = 0;
            for(Abs_Items i: items) {
                count++;
            }
            return count;
        }

        public void showInventory() {
            if (this.getItems() == 0) {
                System.out.println("Инвентарь пустой");
            } else {
                String res = "";
                for(Abs_Items i: items) {

                    if (res.equals(""))
                        res = i.getName();
                    else
                        res += ", " + i.getName();
                }
                System.out.println(res + " лежит в инвенторе");
                System.out.println("В инвенторе " + this.getItems() + " предметов");

            }

    }

}}
