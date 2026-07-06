package itmo.web.dead_web;

import java.util.ArrayList;

public class PointManager {
    private ArrayList<Point> points;

    PointManager() {
        points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void deletePoints() {
        points.clear();
    }
}
