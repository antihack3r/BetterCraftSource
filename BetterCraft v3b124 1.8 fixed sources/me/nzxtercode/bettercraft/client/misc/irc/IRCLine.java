/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.irc;

import java.util.ArrayList;

public class IRCLine {
    public static ArrayList<IRCLine> lines = new ArrayList();
    public int ausrichtung;
    public String message;

    public IRCLine(int ausrichtung, String msg) {
        this.ausrichtung = ausrichtung;
        this.message = msg;
        lines.add(this);
    }
}

