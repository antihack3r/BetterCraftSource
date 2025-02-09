/*
 * Decompiled with CFR 0.152.
 */
package org.jibble.pircbot;

public class User {
    private String _prefix;
    private String _nick;
    private String _lowerNick;

    User(String string, String string2) {
        this._prefix = string;
        this._nick = string2;
        this._lowerNick = string2.toLowerCase();
    }

    public String getPrefix() {
        return this._prefix;
    }

    public boolean isOp() {
        return this._prefix.indexOf(64) >= 0;
    }

    public boolean hasVoice() {
        return this._prefix.indexOf(43) >= 0;
    }

    public String getNick() {
        return this._nick;
    }

    public String toString() {
        return this.getPrefix() + this.getNick();
    }

    public boolean equals(String string) {
        return string.toLowerCase().equals(this._lowerNick);
    }

    public boolean equals(Object object) {
        if (object instanceof User) {
            User user = (User)object;
            return user._lowerNick.equals(this._lowerNick);
        }
        return false;
    }

    public int hashCode() {
        return this._lowerNick.hashCode();
    }

    public int compareTo(Object object) {
        if (object instanceof User) {
            User user = (User)object;
            return user._lowerNick.compareTo(this._lowerNick);
        }
        return -1;
    }
}

