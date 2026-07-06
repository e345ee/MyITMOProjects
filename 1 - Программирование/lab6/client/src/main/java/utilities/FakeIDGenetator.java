package utilities;

import java.util.concurrent.ThreadLocalRandom;

public class FakeIDGenetator {


    public static Integer generateId() {
        Integer id;
        id = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        return id;
    }
}
