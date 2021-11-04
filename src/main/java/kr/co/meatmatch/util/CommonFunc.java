package kr.co.meatmatch.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Random;

public class CommonFunc {

    public static String genRandomNumber(int len) {
        Random rand = new Random();
        String numStr = "";

        for(int i=0; i<len; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr += ran;
        }
        return numStr;
    }

    public static String getCurrentDate(String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(LocalDateTime.now());
    }

    public static String encodeBase64(String str) {
        Encoder encoder = Base64.getEncoder();
        byte[] encodeBytes = encoder.encode(str.getBytes());
        return new String(encodeBytes);
    }

    public static String encodeBCrypt(String str) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(str);
    }

    public static String getFileExtension(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }
}
