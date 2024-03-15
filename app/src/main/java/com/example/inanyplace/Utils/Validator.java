package com.example.inanyplace.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static boolean isValidEmail(String email) {
        boolean status=false;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher=pattern.matcher(email);
        if(matcher.matches())
            status=true;
        else
            status=false;
        return status;
    }

    public static boolean isValidPassword(String password) {
        boolean status=false;
        String EMAIL_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher=pattern.matcher(password);
        if(matcher.matches())
            status=true;
        else
            status=false;
        return status;
    }
}
