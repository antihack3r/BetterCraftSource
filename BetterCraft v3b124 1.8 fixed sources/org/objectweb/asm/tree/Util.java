/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;

final class Util {
    Util() {
    }

    static <T> List<T> asArrayList(int length) {
        ArrayList<Object> list = new ArrayList<Object>(length);
        int i2 = 0;
        while (i2 < length) {
            list.add(null);
            ++i2;
        }
        return list;
    }

    static <T> List<T> asArrayList(T[] array) {
        if (array == null) {
            return new ArrayList();
        }
        ArrayList<T> list = new ArrayList<T>(array.length);
        T[] TArray = array;
        int n2 = array.length;
        int n3 = 0;
        while (n3 < n2) {
            T t2 = TArray[n3];
            list.add(t2);
            ++n3;
        }
        return list;
    }

    static List<Byte> asArrayList(byte[] byteArray) {
        if (byteArray == null) {
            return new ArrayList<Byte>();
        }
        ArrayList<Byte> byteList = new ArrayList<Byte>(byteArray.length);
        byte[] byArray = byteArray;
        int n2 = byteArray.length;
        int n3 = 0;
        while (n3 < n2) {
            byte b2 = byArray[n3];
            byteList.add(b2);
            ++n3;
        }
        return byteList;
    }

    static List<Boolean> asArrayList(boolean[] booleanArray) {
        if (booleanArray == null) {
            return new ArrayList<Boolean>();
        }
        ArrayList<Boolean> booleanList = new ArrayList<Boolean>(booleanArray.length);
        boolean[] blArray = booleanArray;
        int n2 = booleanArray.length;
        int n3 = 0;
        while (n3 < n2) {
            boolean b2 = blArray[n3];
            booleanList.add(b2);
            ++n3;
        }
        return booleanList;
    }

    static List<Short> asArrayList(short[] shortArray) {
        if (shortArray == null) {
            return new ArrayList<Short>();
        }
        ArrayList<Short> shortList = new ArrayList<Short>(shortArray.length);
        short[] sArray = shortArray;
        int n2 = shortArray.length;
        int n3 = 0;
        while (n3 < n2) {
            short s2 = sArray[n3];
            shortList.add(s2);
            ++n3;
        }
        return shortList;
    }

    static List<Character> asArrayList(char[] charArray) {
        if (charArray == null) {
            return new ArrayList<Character>();
        }
        ArrayList<Character> charList = new ArrayList<Character>(charArray.length);
        char[] cArray = charArray;
        int n2 = charArray.length;
        int n3 = 0;
        while (n3 < n2) {
            char c2 = cArray[n3];
            charList.add(Character.valueOf(c2));
            ++n3;
        }
        return charList;
    }

    static List<Integer> asArrayList(int[] intArray) {
        if (intArray == null) {
            return new ArrayList<Integer>();
        }
        ArrayList<Integer> intList = new ArrayList<Integer>(intArray.length);
        int[] nArray = intArray;
        int n2 = intArray.length;
        int n3 = 0;
        while (n3 < n2) {
            int i2 = nArray[n3];
            intList.add(i2);
            ++n3;
        }
        return intList;
    }

    static List<Float> asArrayList(float[] floatArray) {
        if (floatArray == null) {
            return new ArrayList<Float>();
        }
        ArrayList<Float> floatList = new ArrayList<Float>(floatArray.length);
        float[] fArray = floatArray;
        int n2 = floatArray.length;
        int n3 = 0;
        while (n3 < n2) {
            float f2 = fArray[n3];
            floatList.add(Float.valueOf(f2));
            ++n3;
        }
        return floatList;
    }

    static List<Long> asArrayList(long[] longArray) {
        if (longArray == null) {
            return new ArrayList<Long>();
        }
        ArrayList<Long> longList = new ArrayList<Long>(longArray.length);
        long[] lArray = longArray;
        int n2 = longArray.length;
        int n3 = 0;
        while (n3 < n2) {
            long l2 = lArray[n3];
            longList.add(l2);
            ++n3;
        }
        return longList;
    }

    static List<Double> asArrayList(double[] doubleArray) {
        if (doubleArray == null) {
            return new ArrayList<Double>();
        }
        ArrayList<Double> doubleList = new ArrayList<Double>(doubleArray.length);
        double[] dArray = doubleArray;
        int n2 = doubleArray.length;
        int n3 = 0;
        while (n3 < n2) {
            double d2 = dArray[n3];
            doubleList.add(d2);
            ++n3;
        }
        return doubleList;
    }

    static <T> List<T> asArrayList(int length, T[] array) {
        ArrayList<T> list = new ArrayList<T>(length);
        int i2 = 0;
        while (i2 < length) {
            list.add(array[i2]);
            ++i2;
        }
        return list;
    }
}

