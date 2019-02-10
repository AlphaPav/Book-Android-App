package com.example.alphapav.book.util;


import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Check {
    public static void main(String[] args) {
        // test
        System.out.println(get_timestamp());
        String myPlainText = "xiaoxie";
        String myRandom = "sssssss";
        System.out.println("HASH:   " + CalculateSHA256(myPlainText));
        String prove = getStringXor(myPlainText, myRandom);
        System.out.println("prove:  " + getStringXor(myPlainText, myRandom));
        System.out.println("recover:" + getStringXor(prove, myRandom));
    }

    // check if is valid
    public static boolean isValidPhone(String phone) {
        if(phone == null || phone.equals("")) {
            return false;
        }

        int length = phone.length();
        if(length != 11) {
            return false;
        }

        // check if is all numbers
        for(int i = 0; i < length; i++) {
            char ch = phone.charAt(i);
            if(!(ch <= '9' && ch >= '0')) {
                return false;
            }
        }
        return true;
    }

    // calculate xor for string s1 s2
    public static String getStringXor(String s1, String s2) {
        if(s1 == null || s2 == null) {
            return null;
        }

        if((s1.length() != s2.length()) || s1.length() == 0) {
            return null;
        }

        String realCode = "";
        for (int i = 0; i < s1.length(); i++) {
            int c1 = myChar2Int(s1.charAt(i));
            int c2 = myChar2Int(s2.charAt(i));
            if(c1 == -1 || c2 == -1) {
                return null;
            }
            // myXor
            char c = myInt2Char((c1 ^ c2) & 0x3f);
            if(c == '$') {
                return null;
            }
            realCode += c;
        }
        return realCode;
    }

    // calculate hash for string
    public static String CalculateSHA256(String plainText){
        if(plainText == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("sha-256");
            messageDigest.update(plainText.getBytes("utf-8"));
            return bytesToHexString(messageDigest.digest());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    // bytes to hex
    private static String bytesToHexString(byte[] bytes){
        StringBuffer sb = new StringBuffer(bytes.length);
        String temp = null;
        for (int i = 0;i< bytes.length;i++){
            temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() <2){
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    // personal encrypt string
    private static int  myChar2Int(char c) {
        /*a-z A-Z 0-9 # * size = 10 + 26 + 26 = 64 = 2^6*/
        if(c >= 'a' && c <= 'z') {
            return c-'a';
        }
        if(c >= 'A' && c <= 'Z') {
            return (c-'A') + 26;
        }
        if(c >= '0' && c <= '9') {
            return (c-'0') + 52;
        }
        if(c == '#') {
            return 62;
        }
        if(c == '*') {
            return 63;
        }
        return -1;
    }

    // personal encrypt string
    private static char  myInt2Char(int x) {
        if(x >= 0 && x < 26) {
            return (char)(x +'a');
        }
        if(x >= 26 && x < 52) {
            return (char)(x - 26 +'A');
        }
        if(x >= 52 && x < 62) {
            return (char)(x - 52 +'0');
        }
        if(x == 62) {
            return '#';
        }
        if(x == 63) {
            return '*';
        }

        return '$';
    }

    // get ramdom string
    public static String get_ramdon_string(int len){
        String str="1234567890ABCDEFGHIJKLMNOPQRSTUZWXYZabcdefghijklmnopqrstuvwxy";
        String str_random = "";
        // generate
        Random random = new Random();
        for(int i = 0; i < len; i++) {
            int ran = random.nextInt(str.length());
            str_random += str.charAt(ran);
        }
        // end form verify code
        return str_random;
    }

    // get time stamp
    public static String get_timestamp(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");  // set data format
        // get current data
        return df.format(new Date());
    }
}
