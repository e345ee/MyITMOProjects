package products;

/**
 * Координаты.
 */
public class Coordinates {
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


}



