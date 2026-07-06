import java.lang.Math;


public class Lucky {

    private static int levelOfLucky = (int) (Math.random() * 60);

    private static String luckyString = (levelOfLucky>20) ? "good" : "bad";

    public static class LuckyOptions{

        @Override
        public String toString() {
            return "Уровень удачи " + levelOfLucky;
        }
        public void changeLucky(int t){
            levelOfLucky = t;
        }
        public boolean isGoodLuck(){
            if(luckyString == "good")
                return true;
            else
                return false;
        }
    }
}