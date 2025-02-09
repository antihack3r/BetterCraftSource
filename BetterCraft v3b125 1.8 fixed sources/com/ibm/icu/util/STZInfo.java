/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.util;

import com.ibm.icu.util.SimpleTimeZone;
import java.io.Serializable;

final class STZInfo
implements Serializable {
    private static final long serialVersionUID = -7849612037842370168L;
    int sy = -1;
    int sm = -1;
    int sdwm;
    int sdw;
    int st;
    int sdm;
    boolean sa;
    int em = -1;
    int edwm;
    int edw;
    int et;
    int edm;
    boolean ea;

    STZInfo() {
    }

    void setStart(int sm2, int sdwm, int sdw, int st2, int sdm, boolean sa2) {
        this.sm = sm2;
        this.sdwm = sdwm;
        this.sdw = sdw;
        this.st = st2;
        this.sdm = sdm;
        this.sa = sa2;
    }

    void setEnd(int em2, int edwm, int edw, int et2, int edm, boolean ea2) {
        this.em = em2;
        this.edwm = edwm;
        this.edw = edw;
        this.et = et2;
        this.edm = edm;
        this.ea = ea2;
    }

    void applyTo(SimpleTimeZone stz) {
        if (this.sy != -1) {
            stz.setStartYear(this.sy);
        }
        if (this.sm != -1) {
            if (this.sdm == -1) {
                stz.setStartRule(this.sm, this.sdwm, this.sdw, this.st);
            } else if (this.sdw == -1) {
                stz.setStartRule(this.sm, this.sdm, this.st);
            } else {
                stz.setStartRule(this.sm, this.sdm, this.sdw, this.st, this.sa);
            }
        }
        if (this.em != -1) {
            if (this.edm == -1) {
                stz.setEndRule(this.em, this.edwm, this.edw, this.et);
            } else if (this.edw == -1) {
                stz.setEndRule(this.em, this.edm, this.et);
            } else {
                stz.setEndRule(this.em, this.edm, this.edw, this.et, this.ea);
            }
        }
    }
}

