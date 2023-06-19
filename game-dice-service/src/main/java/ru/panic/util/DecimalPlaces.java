package ru.panic.util;

public class DecimalPlaces {
    public static int countDecimalPlaces(double number) {
        String numberString = String.valueOf(number);
        int index = numberString.indexOf('.');
        return index < 0 ? 0 : numberString.length() - index - 1;
    }
     public static double round(double value, int decimalPlaces) {
        double scale = Math.pow(10, decimalPlaces);
        return Math.round(value * scale) / scale;
    }
}
