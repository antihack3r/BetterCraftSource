/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ArrayUtils {
    public static boolean contains(Object[] arr2, Object val) {
        if (arr2 == null) {
            return false;
        }
        int i2 = 0;
        while (i2 < arr2.length) {
            Object object = arr2[i2];
            if (object == val) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static int[] addIntsToArray(int[] intArray, int[] copyFrom) {
        if (intArray != null && copyFrom != null) {
            int i2 = intArray.length;
            int j2 = i2 + copyFrom.length;
            int[] aint = new int[j2];
            System.arraycopy(intArray, 0, aint, 0, i2);
            int k2 = 0;
            while (k2 < copyFrom.length) {
                aint[k2 + i2] = copyFrom[k2];
                ++k2;
            }
            return aint;
        }
        throw new NullPointerException("The given array is NULL");
    }

    public static int[] addIntToArray(int[] intArray, int intValue) {
        return ArrayUtils.addIntsToArray(intArray, new int[]{intValue});
    }

    public static Object[] addObjectsToArray(Object[] arr2, Object[] objs) {
        if (arr2 == null) {
            throw new NullPointerException("The given array is NULL");
        }
        if (objs.length == 0) {
            return arr2;
        }
        int i2 = arr2.length;
        int j2 = i2 + objs.length;
        Object[] aobject = (Object[])Array.newInstance(arr2.getClass().getComponentType(), j2);
        System.arraycopy(arr2, 0, aobject, 0, i2);
        System.arraycopy(objs, 0, aobject, i2, objs.length);
        return aobject;
    }

    public static Object[] addObjectToArray(Object[] arr2, Object obj) {
        if (arr2 == null) {
            throw new NullPointerException("The given array is NULL");
        }
        int i2 = arr2.length;
        int j2 = i2 + 1;
        Object[] aobject = (Object[])Array.newInstance(arr2.getClass().getComponentType(), j2);
        System.arraycopy(arr2, 0, aobject, 0, i2);
        aobject[i2] = obj;
        return aobject;
    }

    public static Object[] addObjectToArray(Object[] arr2, Object obj, int index) {
        ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(arr2));
        list.add(index, obj);
        Object[] aobject = (Object[])Array.newInstance(arr2.getClass().getComponentType(), list.size());
        return list.toArray(aobject);
    }

    public static String arrayToString(boolean[] arr2, String separator) {
        if (arr2 == null) {
            return "";
        }
        StringBuffer stringbuffer = new StringBuffer(arr2.length * 5);
        int i2 = 0;
        while (i2 < arr2.length) {
            boolean flag = arr2[i2];
            if (i2 > 0) {
                stringbuffer.append(separator);
            }
            stringbuffer.append(String.valueOf(flag));
            ++i2;
        }
        return stringbuffer.toString();
    }

    public static String arrayToString(float[] arr2) {
        return ArrayUtils.arrayToString(arr2, ", ");
    }

    public static String arrayToString(float[] arr2, String separator) {
        if (arr2 == null) {
            return "";
        }
        StringBuffer stringbuffer = new StringBuffer(arr2.length * 5);
        int i2 = 0;
        while (i2 < arr2.length) {
            float f2 = arr2[i2];
            if (i2 > 0) {
                stringbuffer.append(separator);
            }
            stringbuffer.append(String.valueOf(f2));
            ++i2;
        }
        return stringbuffer.toString();
    }

    public static String arrayToString(float[] arr2, String separator, String format) {
        if (arr2 == null) {
            return "";
        }
        StringBuffer stringbuffer = new StringBuffer(arr2.length * 5);
        int i2 = 0;
        while (i2 < arr2.length) {
            float f2 = arr2[i2];
            if (i2 > 0) {
                stringbuffer.append(separator);
            }
            stringbuffer.append(String.format(format, Float.valueOf(f2)));
            ++i2;
        }
        return stringbuffer.toString();
    }

    public static String arrayToString(int[] arr2) {
        return ArrayUtils.arrayToString(arr2, ", ");
    }

    public static String arrayToString(int[] arr2, String separator) {
        if (arr2 == null) {
            return "";
        }
        StringBuffer stringbuffer = new StringBuffer(arr2.length * 5);
        int i2 = 0;
        while (i2 < arr2.length) {
            int j2 = arr2[i2];
            if (i2 > 0) {
                stringbuffer.append(separator);
            }
            stringbuffer.append(String.valueOf(j2));
            ++i2;
        }
        return stringbuffer.toString();
    }

    public static String arrayToHexString(int[] arr2, String separator) {
        if (arr2 == null) {
            return "";
        }
        StringBuffer stringbuffer = new StringBuffer(arr2.length * 5);
        int i2 = 0;
        while (i2 < arr2.length) {
            int j2 = arr2[i2];
            if (i2 > 0) {
                stringbuffer.append(separator);
            }
            stringbuffer.append("0x");
            stringbuffer.append(Integer.toHexString(j2));
            ++i2;
        }
        return stringbuffer.toString();
    }

    public static String arrayToString(Object[] arr2) {
        return ArrayUtils.arrayToString(arr2, ", ");
    }

    public static String arrayToString(Object[] arr2, String separator) {
        if (arr2 == null) {
            return "";
        }
        StringBuffer stringbuffer = new StringBuffer(arr2.length * 5);
        int i2 = 0;
        while (i2 < arr2.length) {
            Object object = arr2[i2];
            if (i2 > 0) {
                stringbuffer.append(separator);
            }
            stringbuffer.append(String.valueOf(object));
            ++i2;
        }
        return stringbuffer.toString();
    }

    public static Object[] collectionToArray(Collection coll, Class elementClass) {
        if (coll == null) {
            return null;
        }
        if (elementClass == null) {
            return null;
        }
        if (elementClass.isPrimitive()) {
            throw new IllegalArgumentException("Can not make arrays with primitive elements (int, double), element class: " + elementClass);
        }
        Object[] aobject = (Object[])Array.newInstance(elementClass, coll.size());
        return coll.toArray(aobject);
    }

    public static boolean equalsOne(int val, int[] vals) {
        int i2 = 0;
        while (i2 < vals.length) {
            if (vals[i2] == val) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static boolean equalsOne(Object a2, Object[] bs2) {
        if (bs2 == null) {
            return false;
        }
        int i2 = 0;
        while (i2 < bs2.length) {
            Object object = bs2[i2];
            if (ArrayUtils.equals(a2, object)) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static boolean equals(Object o1, Object o2) {
        return o1 == o2 ? true : (o1 == null ? false : o1.equals(o2));
    }

    public static boolean isSameOne(Object a2, Object[] bs2) {
        if (bs2 == null) {
            return false;
        }
        int i2 = 0;
        while (i2 < bs2.length) {
            Object object = bs2[i2];
            if (a2 == object) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static Object[] removeObjectFromArray(Object[] arr2, Object obj) {
        ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(arr2));
        list.remove(obj);
        Object[] aobject = ArrayUtils.collectionToArray(list, arr2.getClass().getComponentType());
        return aobject;
    }

    public static int[] toPrimitive(Integer[] arr2) {
        if (arr2 == null) {
            return null;
        }
        if (arr2.length == 0) {
            return new int[0];
        }
        int[] aint = new int[arr2.length];
        int i2 = 0;
        while (i2 < aint.length) {
            aint[i2] = arr2[i2];
            ++i2;
        }
        return aint;
    }
}

