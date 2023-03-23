public class Main {

    public static void main(String[] args) {
        System.out.println();
        Znaika znaika = new Znaika("Знайка", 100);
        Rocket rocket = new Rocket("Интерпрайз", 100, 0, znaika, znaika);
        rocket.showLobby();
        Sniper snip = new Sniper("Агент 007", 100);
        snip.shoot(rocket);
        rocket.diagnose(rocket);
        znaika.course_alignment(rocket);
    }
}
