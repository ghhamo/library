package job.hamo.library.util;

import java.security.SecureRandom;

public class Util {

    private static final SecureRandom secureRnd = new SecureRandom();

    private static final String lettersAndNumbers = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";



    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(lettersAndNumbers.charAt(secureRnd.nextInt(lettersAndNumbers.length())));
        }
        return sb.toString();
    }


}
