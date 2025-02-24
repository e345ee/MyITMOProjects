package itmo.web.demo1.beans;

import java.sql.SQLException;
import java.util.ArrayList;


import itmo.web.demo1.database.models.Point;
import itmo.web.demo1.database.DataBaseManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named("tableBean")
@ApplicationScoped
public class TableBean {
    private ArrayList<Point> points = new ArrayList<>();

    public TableBean() {
        points = getPoints();
    }


    public void clearAllPoints() {
        try (var connection = DataBaseManager.connect()) {
            DataBaseManager.clearPoints(connection);
            points.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<Point> getPoints() {
        try (var connection = DataBaseManager.connect()) {
            DataBaseManager.createPointsTable(connection);
            points = DataBaseManager.getLastPoints(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return points;
    }
}