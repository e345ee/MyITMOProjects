package itmo.web.dead_web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

public class AreaCheckServlet extends HttpServlet {
    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        try {

            String xParam = req.getParameter("x");
            String yParam = req.getParameter("y");
            String rParam = req.getParameter("r");

            System.out.println("Received parameters: x=" + xParam + ", y=" + yParam + ", r=" + rParam);


            float x = Float.parseFloat(xParam);
            float y = Float.parseFloat(yParam);
            float r = Float.parseFloat(rParam);

            System.out.println("Parsed parameters: x=" + x + ", y=" + y + ", r=" + r);

            // Логируем результат валидации
            if (Validator.validateX(x) && Validator.validateY(y) && Validator.validateR(r)) {
                System.out.println("Validation passed: x=" + x + ", y=" + y + ", r=" + r);

                // Проверяем попадание
                boolean isInArea = Checker.hit(x, y, r);
                System.out.println("Hit result: " + isInArea);

                // Создаём точку
                Point point = new Point(x, y, r, isInArea);

                // Логируем работу с сессией
                PointManager pointManager = (PointManager) session.getAttribute("pointManager");
                if (pointManager == null) {
                    pointManager = new PointManager();
                    session.setAttribute("pointManager", pointManager);
                    System.out.println("PointManager created and stored in session.");
                }

                // Добавляем точку
                pointManager.addPoint(point);
                System.out.println("Point added to PointManager: " + point);

                // Передаём точку в JSP
                req.setAttribute("newPoint", point);
                System.out.println("newPoint set as request attribute: " + point);
            } else {
                System.out.println("Validation failed for: x=" + x + ", y=" + y + ", r=" + r);
            }

            // Перенаправляем на result.jsp
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
            System.out.println("Forwarded to result.jsp");

        } catch (NumberFormatException e) {
            System.err.println("Failed to parse parameters: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }


    private static class Validator {
        public static boolean validateX(float x) {
            return x >= -6 && x <= 6;
        }

        public static boolean validateY(float y) {
            return y >= -6 && y <= 6;
        }

        public static boolean validateR(float r) {
            return r >= 1 && r <= 5 && r % 1 == 0;
        }
    }

    private static class Checker {
        public static boolean hit(float x, float y, float r) {
            return inRect(x, y, r) || inTriangle(x, y, r) || inCircle(x, y, r);
        }

        private static boolean inRect(float x, float y, float r) {
            return x >= 0 && y <= 0 && x <= r && y >= -r/2;
        }

        private static boolean inTriangle(float x, float y, float r) {
            return x <= 0 && y <= 0 && x >= -r/2 && y >= -2 * x - r;
        }

        private static boolean inCircle(float x, float y, float r) {
            return x <= 0 && y >= 0 && (Math.pow(x, 2) + Math.pow(y, 2) <= Math.pow(r, 2));
        }
    }

}