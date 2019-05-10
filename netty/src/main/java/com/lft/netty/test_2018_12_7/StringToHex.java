package com.lft.netty.test_2018_12_7;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 16进制与ASCALL
 * @author Ryze
 * @date 2018-12-07 10:19
 */
public class StringToHex {


    //字符串转换为ascii
    public static String StringToA(String content) {
        String result = "";
        int max = content.length();
        for (int i = 0; i < max; i++) {
            char c = content.charAt(i);
            int b = (int) c;
            result = result + b;
        }
        return result;
    }

    //ascii转换为string
    public static String AToString(int i) {
        return Character.toString((char) i);
    }

    public static String formatStringWithZero(String s, int byteSize) {
        return Stream.generate(() -> "0").limit(byteSize * 8 - s.length()).collect(Collectors.joining()) + s;
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        int i = 0x23;
        int i1 = 0x23;
        int b = 0x01;
        int b1 = 0xfe;
        int b2 = 0x01;
        int b3 = 0x04;
        int x = 0x01;
        int x1 = 0x02;
        int x2 = 0x03;
        int x3 = 0x04;
        int x4 = 0x05;
        int x5 = 0x06;
        int x6 = 0x07;
        int x7 = 0x08;
        int x8 = 0x09;
        int x9 = 0x00;
        int b5 = 0x00;
        int b51 = 0x00;
        int i2 = 0x23 ^ 0x23 ^ 0x01 ^ 0xfe ^ 0x01 ^ 0x04 ^ 0x01 ^ 0x02 ^ 0x03 ^ 0x04 ^ 0x05 ^ 0x06 ^ 0x07 ^ 0x08 ^ 0x09 ^ 0x00 ^ 0x01 ^ 0x04 ^ 0x01 ^ 0x02 ^ 0x03 ^ 0x04 ^ 0x05 ^ 0x06 ^ 0x07 ^ 0x00 ^ 0x00;
        System.out.println(i2);
        int b6 = 0x5b;
        sb.append(formatStringWithZero(Integer.toBinaryString(35), 1))
            .append(formatStringWithZero(Integer.toBinaryString(35), 1))
            .append(formatStringWithZero(Integer.toBinaryString(1), 1))
            .append(formatStringWithZero(Integer.toBinaryString(254), 1))
            .append(formatStringWithZero(Integer.toBinaryString(1), 1))
            .append(formatStringWithZero(Integer.toBinaryString(4), 1))
            .append(formatStringWithZero(Long.toBinaryString(12345678901234567L), 17))
            .append(formatStringWithZero(Integer.toBinaryString(0), 2))
            .append(formatStringWithZero(Integer.toBinaryString(91), 1));
        System.out.println(sb);
    }




}