package products;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable {
    private Float x; //Поле не может быть null
    private int y;

    public Coordinates(Float x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {

    }


    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates that)) return false;
        return getY() == that.getY() && Objects.equals(getX(), that.getX());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }
}