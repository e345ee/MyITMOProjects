package itmo.web.dead_web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ControllerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String x = req.getParameter("x");
        String y = req.getParameter("y");
        String r = req.getParameter("r");

        System.out.println("ControllerServlet received: x=" + x + ", y=" + y + ", r=" + r);

        if (x != null && y != null && r != null) {
            System.out.println("All parameters present, forwarding to /check");
            req.getRequestDispatcher("/check").forward(req, resp);
        } else {
            System.out.println("Missing parameters, forwarding to index.jsp");
            req.getRequestDispatcher("index.jsp").forward(req, resp);
        }
    }

}