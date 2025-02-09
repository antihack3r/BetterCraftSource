/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.util;

import com.ibm.icu.lang.UCharacter;

public class CaseInsensitiveString {
    private String string;
    private int hash = 0;
    private String folded = null;

    private static String foldCase(String foldee) {
        return UCharacter.foldCase(foldee, true);
    }

    private void getFolded() {
        if (this.folded == null) {
            this.folded = CaseInsensitiveString.foldCase(this.string);
        }
    }

    public CaseInsensitiveString(String s2) {
        this.string = s2;
    }

    public String getString() {
        return this.string;
    }

    public boolean equals(Object o2) {
        if (o2 == null) {
            return false;
        }
        if (this == o2) {
            return true;
        }
        this.getFolded();
        try {
            CaseInsensitiveString cis = (CaseInsensitiveString)o2;
            cis.getFolded();
            return this.folded.equals(cis.folded);
        }
        catch (ClassCastException e2) {
            try {
                String s2 = (String)o2;
                return this.folded.equals(CaseInsensitiveString.foldCase(s2));
            }
            catch (ClassCastException e22) {
                return false;
            }
        }
    }

    public int hashCode() {
        this.getFolded();
        if (this.hash == 0) {
            this.hash = this.folded.hashCode();
        }
        return this.hash;
    }

    public String toString() {
        return this.string;
    }
}

