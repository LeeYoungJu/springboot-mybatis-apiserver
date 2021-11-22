package kr.co.meatmatch.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

@Component
public class CommonFunc {
    private static String profile;

    @Value("${spring.profiles.active}")
    public void setProfile(String active) {
        profile = active;
    }

    public static boolean isRealMode() {
        return profile.equals("real");
    }

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

    public static String removeBearerFromToken(String token) {
        return token.substring(7);
    }

    public static String addDays(String date, int addDay) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj = format.parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObj);

        cal.add(Calendar.DATE, addDay);
        Date resDate = cal.getTime();
        return format.format(resDate);
    }

    public static String[] splitDate(String date, String regex) {
        String[] res = new String[2];
        String[] dateArr = date.split(regex);
        res[0] = dateArr[0] + " 00:00:00";
        res[1] = dateArr[1] + " 23:59:59";
        return res;
    }

    public static String calcExpDate(int hopeMon) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, hopeMon);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    public static String convertHopeMonOpt(String value) {
        String compare = "";
        if(value.equals("over")) {
            compare = ">=";
        } else if(value.equals("under")) {
            compare = "<=";
        }
        return compare;
    }

    public static String setEmptyFormat(String originStr, String replaceStr, int len) {
        String result = "";
        for(int i=0; i < len - originStr.length(); i++) {
            result += replaceStr;
        }
        return result + originStr;
    }

    public static boolean checkAuthId(String authId) {
        return Pattern.matches("^((?=.*[a-zA-Z])[a-zA-Z0-9])[A-Za-z\\d]{5,17}$", authId);
    }

    public static boolean checkPassword(String password) {
        return Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,20}$", password);
    }

    public static boolean checkPhone(String phone) {
//        return Pattern.matches("^\\d{3}-\\d{4}-\\d{4}$", phone);
        return Pattern.matches("^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$", phone);
    }
}
