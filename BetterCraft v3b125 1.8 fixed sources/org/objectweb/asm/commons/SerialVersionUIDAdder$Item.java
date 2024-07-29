/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

class SerialVersionUIDAdder$Item
implements Comparable {
    final String name;
    final int access;
    final String desc;

    SerialVersionUIDAdder$Item(String string, int n2, String string2) {
        this.name = string;
        this.access = n2;
        this.desc = string2;
    }

    public int compareTo(SerialVersionUIDAdder$Item serialVersionUIDAdder$Item) {
        int n2 = this.name.compareTo(serialVersionUIDAdder$Item.name);
        if (n2 == 0) {
            n2 = this.desc.compareTo(serialVersionUIDAdder$Item.desc);
        }
        return n2;
    }

    public boolean equals(Object object) {
        if (object instanceof SerialVersionUIDAdder$Item) {
            return this.compareTo((SerialVersionUIDAdder$Item)object) == 0;
        }
        return false;
    }

    public int hashCode() {
        return (this.name + this.desc).hashCode();
    }
}

