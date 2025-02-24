package itmo.web.demo1.beans;

import java.sql.SQLException;


import itmo.web.demo1.database.models.Point;
import itmo.web.demo1.database.DataBaseManager;
import itmo.web.demo1.utils.PointHitManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named("formBean")
@ApplicationScoped //один раз на все приложение
public class FormBean {
    private double x;
    private double y;
    private double r;

    public void check() {
        try (var connection = DataBaseManager.connect()) {
            DataBaseManager.createPointsTable(connection);
            DataBaseManager.insertPoint(connection, new Point(x, y, r, PointHitManager.Checker.hit(x, y, r)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }
}