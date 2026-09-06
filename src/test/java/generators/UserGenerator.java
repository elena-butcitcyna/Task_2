package generators;

import java.util.Random;

public final class UserGenerator {

    private static final Random RANDOM = new Random();

    private UserGenerator() {
    }

    public static String randomEmail() {
        return "stellar" + RANDOM.nextInt(100_000_000) + "@yandex.ru";
    }

    public static String randomName() {
        return "TestUser" + RANDOM.nextInt(100_000_000);
    }

    public static String randomPassword() {
        return "password" + RANDOM.nextInt(100_000_000);
    }
}
