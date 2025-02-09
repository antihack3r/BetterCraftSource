/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

final class Item {
    int a;
    int b;
    int c;
    long d;
    String g;
    String h;
    String i;
    int j;
    Item k;

    Item() {
    }

    Item(int n2) {
        this.a = n2;
    }

    Item(int n2, Item item) {
        this.a = n2;
        this.b = item.b;
        this.c = item.c;
        this.d = item.d;
        this.g = item.g;
        this.h = item.h;
        this.i = item.i;
        this.j = item.j;
    }

    void a(int n2) {
        this.b = 3;
        this.c = n2;
        this.j = Integer.MAX_VALUE & this.b + n2;
    }

    void a(long l2) {
        this.b = 5;
        this.d = l2;
        this.j = Integer.MAX_VALUE & this.b + (int)l2;
    }

    void a(float f2) {
        this.b = 4;
        this.c = Float.floatToRawIntBits(f2);
        this.j = Integer.MAX_VALUE & this.b + (int)f2;
    }

    void a(double d2) {
        this.b = 6;
        this.d = Double.doubleToRawLongBits(d2);
        this.j = Integer.MAX_VALUE & this.b + (int)d2;
    }

    void a(int n2, String string, String string2, String string3) {
        this.b = n2;
        this.g = string;
        this.h = string2;
        this.i = string3;
        switch (n2) {
            case 7: {
                this.c = 0;
            }
            case 1: 
            case 8: 
            case 16: 
            case 30: {
                this.j = Integer.MAX_VALUE & n2 + string.hashCode();
                return;
            }
            case 12: {
                this.j = Integer.MAX_VALUE & n2 + string.hashCode() * string2.hashCode();
                return;
            }
        }
        this.j = Integer.MAX_VALUE & n2 + string.hashCode() * string2.hashCode() * string3.hashCode();
    }

    void a(String string, String string2, int n2) {
        this.b = 18;
        this.d = n2;
        this.g = string;
        this.h = string2;
        this.j = Integer.MAX_VALUE & 18 + n2 * this.g.hashCode() * this.h.hashCode();
    }

    void a(int n2, int n3) {
        this.b = 33;
        this.c = n2;
        this.j = n3;
    }

    boolean a(Item item) {
        switch (this.b) {
            case 1: 
            case 7: 
            case 8: 
            case 16: 
            case 30: {
                return item.g.equals(this.g);
            }
            case 5: 
            case 6: 
            case 32: {
                return item.d == this.d;
            }
            case 3: 
            case 4: {
                return item.c == this.c;
            }
            case 31: {
                return item.c == this.c && item.g.equals(this.g);
            }
            case 12: {
                return item.g.equals(this.g) && item.h.equals(this.h);
            }
            case 18: {
                return item.d == this.d && item.g.equals(this.g) && item.h.equals(this.h);
            }
        }
        return item.g.equals(this.g) && item.h.equals(this.h) && item.i.equals(this.i);
    }
}

