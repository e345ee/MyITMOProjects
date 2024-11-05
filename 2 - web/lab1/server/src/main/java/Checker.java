public class Checker {
    public static boolean hit(float x, float y, float r) {
        return inTriangleFirstQuadrant(x, y, r) ||
                inRectangleFourthQuadrant(x, y, r) ||
                inQuarterCircleSecondQuadrant(x, y, r);
    }

    private static boolean inTriangleFirstQuadrant(float x, float y, float r) {
        return x >= 0 && y >= 0 && x <= r/2 && y <= r/2 && y <= (-x + r/2);
    }

    private static boolean inRectangleFourthQuadrant(float x, float y, float r) {
        return x >= 0 && y <= 0 && x <= r && y >= -r/2;
    }

    private static boolean inQuarterCircleSecondQuadrant(float x, float y, float r) {
        return x <= 0 && y >= 0 && (x * x + y * y) <= (r / 2) * (r / 2);
    }
}