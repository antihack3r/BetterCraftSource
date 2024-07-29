/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.nbt;

public class ParseHelper {
    public static byte parseByte(String s2) throws NumberFormatException {
        try {
            return Byte.parseByte(s2);
        }
        catch (NumberFormatException e2) {
            throw new NumberFormatException("Not a valid byte");
        }
    }

    public static short parseShort(String s2) throws NumberFormatException {
        try {
            return Short.parseShort(s2);
        }
        catch (NumberFormatException e2) {
            throw new NumberFormatException("Not a valid short");
        }
    }

    public static int parseInt(String s2) throws NumberFormatException {
        try {
            return Integer.parseInt(s2);
        }
        catch (NumberFormatException e2) {
            throw new NumberFormatException("Not a valid int");
        }
    }

    public static long parseLong(String s2) throws NumberFormatException {
        try {
            return Long.parseLong(s2);
        }
        catch (NumberFormatException e2) {
            throw new NumberFormatException("Not a valid long");
        }
    }

    public static float parseFloat(String s2) throws NumberFormatException {
        try {
            return Float.parseFloat(s2);
        }
        catch (NumberFormatException e2) {
            throw new NumberFormatException("Not a valid float");
        }
    }

    public static double parseDouble(String s2) throws NumberFormatException {
        try {
            return Double.parseDouble(s2);
        }
        catch (NumberFormatException e2) {
            throw new NumberFormatException("Not a valid double");
        }
    }

    public static byte[] parseByteArray(String s2) throws NumberFormatException {
        try {
            String[] input = s2.split(" ");
            byte[] arr2 = new byte[input.length];
            int i2 = 0;
            while (i2 < input.length) {
                arr2[i2] = ParseHelper.parseByte(input[i2]);
                ++i2;
            }
            return arr2;
        }
        catch (NumberFormatException e2) {
            throw new NumberFormatException("Not a valid byte array");
        }
    }

    public static int[] parseIntArray(String s2) throws NumberFormatException {
        try {
            String[] input = s2.split(" ");
            int[] arr2 = new int[input.length];
            int i2 = 0;
            while (i2 < input.length) {
                arr2[i2] = ParseHelper.parseInt(input[i2]);
                ++i2;
            }
            return arr2;
        }
        catch (NumberFormatException e2) {
            throw new NumberFormatException("Not a valid int array");
        }
    }
}

