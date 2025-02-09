/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.splash.splashdates;

import java.beans.ConstructorProperties;

public class SplashDate {
    private String displayString;
    private boolean birthday;
    private int month;
    private int day;

    public String getDisplayString() {
        return this.birthday ? "Happy birthday, " + this.displayString : this.displayString;
    }

    public boolean isBirthday() {
        return this.birthday;
    }

    public int getMonth() {
        return this.month;
    }

    public int getDay() {
        return this.day;
    }

    @ConstructorProperties(value={"displayString", "birthday", "month", "day"})
    public SplashDate(String displayString, boolean birthday, int month, int day) {
        this.displayString = displayString;
        this.birthday = birthday;
        this.month = month;
        this.day = day;
    }
}

