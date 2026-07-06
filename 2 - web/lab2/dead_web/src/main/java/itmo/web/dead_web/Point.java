package itmo.web.dead_web;

public class Point {
    private float x;
    private float y;
    private float r;
    private boolean isInArea;

    public Point(float x, float y, float r, boolean isInArea) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.isInArea = isInArea;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getR() {
        return r;
    }

    public boolean getResult() {
        return isInArea;
    }
}

