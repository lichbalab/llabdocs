package com.lichbalab.docs.error;

import java.util.Random;

public class RandomStringGenerator {
    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final Random random = new Random();
    public static String generateRandomString(int maxLength) {
        StringBuilder builder = new StringBuilder(maxLength);
        for (int i = 0; i < maxLength; i++) {
            int randomIndex = random.nextInt(CHAR_SET.length());
            builder.append(CHAR_SET.charAt(randomIndex));
        }
        return builder.toString();
    }
}