

import java.util.ArrayList;

public abstract class Abs_Place  {
    protected String name;


    public Abs_Place(String name){
        this.name = name;
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

    public String getName() {
        return this.name;
    }

}
