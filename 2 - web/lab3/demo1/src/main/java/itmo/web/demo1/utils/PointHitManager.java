package itmo.web.demo1.utils;

public class PointHitManager {
    public static class Validator {
        public static boolean validateX(double x) {
            return x >= -4 && x <= 4;
        }

        public static boolean validateY(double y) {
            return y >= -5 && y <= 5;
        }

        public static boolean validateR(double r) {
            return r >= 1 && r <= 3 && r % 0.5 == 0;
        }
    }

    public static class Checker {
        public static boolean hit(double x, double y, double r) {
            return inRect(x, y, r) || inTriangle(x, y, r) || inCircle(x, y, r);
        }

        private static boolean inRect(double x, double y, double r) {
            return x >= 0 && x <= r && y >= 0 && y <= r / 2;
        }

        private static boolean inTriangle(double x, double y, double r) {
            return y >= -x - r / 2 && x <= 0 && y <=0;
        }

        private static boolean inCircle(double x, double y, double r) {
            return x >= 0 && y <= 0 && (Math.pow(x, 2) + Math.pow(y, 2) <= Math.pow(r, 2));
        }
    }



}