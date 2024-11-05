public class Validator {

    public static boolean validateX(int x) {

        return x >= -4 && x <= 4;
    }

    public static boolean validateY(float y) {

        return y >= -5 && y <= 5;
    }

    public static boolean validateR(float r) {

        return r >= 1.0f && r <= 3.0f && ((r * 2) % 1 == 0);
    }
}