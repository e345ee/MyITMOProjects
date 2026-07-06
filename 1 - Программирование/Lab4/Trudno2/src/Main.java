import Items.Telescope;
import MyNewException.ManAlreadyInLobby;
import MyNewException.NoSuchManInLobby;

public class Main {

    public static void main(String[] args) {

        System.out.println();
        City city = new City("Фантомас");
        Lucky.LuckyOptions luck = new Lucky.LuckyOptions();
        Co_Driver stekl = new Co_Driver("Стекляшкин");
        Co_Driver stekl2 = new Co_Driver("Стекляшкин2");
        Telescope zoom = new Telescope("Бинокль");
        Co_Driver.Inventory pocket = stekl.new Inventory(zoom);
        pocket.showInventory();
        Pilot znaika = new Pilot("Знайка", 300);


        Rocket rocket = new Rocket("Интерпрайз", 34000);
        try {
            rocket.addGuy(znaika, stekl, znaika);
        } catch (ManAlreadyInLobby e) {
            System.out.println(e.getMessage());
        }
        try {
            rocket.removeGuy(stekl2);
        } catch (NoSuchManInLobby e) {
            System.out.println(e.getMessage());
        }
        rocket.showLobby();

        pocket.removeInventory(zoom);
        stekl.look(city);

        System.out.println(luck.toString());
        if (luck.isGoodLuck()) {
            System.out.println("Это и вправду" + city.getName());
            try {


                stekl.think("У нас чистый город. Красивее всего летом в ясную погоду. В нашем городе много университетов и техникумов. Учиться к нам приезжают не только со всей республики, но и из других стран. У нас много памятников. Например, Ленину и Чапаеву.\n" +
                        "\n" +
                        "Есть у нас и музеи. В них можно узнать историю нашей республики. Также приезжают  выставки из других городов. Недавно приезжала кунсткамера из Петербурга.\n" +
                        "\n" +
                        "В последние годы во дворах установили новые детские площадки. А еще построили много новых больниц.\n" +
                        "\n" +
                        "Мой город стоит на берегу реки Волга. И летом горожане любят в ней купаться и загорать на ее берегу. На Волге несколько пляжей, где отдыхают люди. Летом на другой берег ходит паромчик. И можно отправиться, на нем на другой берег.\n" +
                        "\n" +
                        "Зимой тоже красиво. Можно кататься на санках. В разных районах есть лыжные базы. И любители этого вида спорта могут вдоволь накататься.\n" +
                        "\n" +
                        "В моем городе много церквей. Есть несколько мечетей. У меня спокойный, тихий город.\n" +
                        "\n" +
                        "Мне здесь очень нравится.\n" +
                        "\n" +
                        "\n" +
                        "\n");
            } catch (
                    OutOfMemoryError e) {
                System.out.println(e.getMessage());
            } finally {


                Sniper snip = new Sniper("Агент 007", 100);
                snip.shoot(rocket);
                rocket.diagnose(rocket);
                znaika.course_alignment(rocket);
            }

    } else
            System.out.println(stekl.getName()+"Не видит города");
}}


