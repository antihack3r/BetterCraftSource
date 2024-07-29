/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils;

import java.awt.Color;

public enum ModColor {
    BLACK('0', 0, 0, 0),
    DARK_BLUE('1', 0, 0, 170),
    DARK_GREEN('2', 0, 170, 0),
    DARK_AQUA('3', 0, 170, 170),
    DARK_RED('4', 170, 0, 0),
    DARK_PURPLE('5', 170, 0, 170),
    GOLD('6', 255, 170, 0),
    GRAY('7', 170, 170, 170),
    DARK_GRAY('8', 85, 85, 85),
    BLUE('9', 85, 85, 255),
    GREEN('a', 85, 255, 85),
    AQUA('b', 85, 255, 255),
    RED('c', 255, 85, 85),
    PINK('d', 255, 85, 255),
    YELLOW('e', 255, 255, 85),
    WHITE('f', 255, 255, 255),
    RESET('r'),
    BOLD('l'),
    ITALIC('o'),
    UNDERLINE('n'),
    MAGIC('k'),
    STRIKETHROUGH('m');

    public static final String[] COLOR_CODES;
    public static final char COLOR_CHAR_PREFIX = '\u00a7';
    private final char colorChar;
    private Color color;

    static {
        COLOR_CODES = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "k", "m", "n", "l", "o", "r"};
    }

    private ModColor(char colorChar, int r2, int g2, int b2) {
        this.colorChar = colorChar;
        this.color = new Color(r2, g2, b2);
    }

    private ModColor(char colorChar) {
        this.colorChar = colorChar;
    }

    public static String cl(String colorChar) {
        return String.valueOf('\u00a7') + colorChar;
    }

    public static String cl(char colorChar) {
        return String.valueOf(new char[]{'\u00a7', colorChar});
    }

    private static String getColorCharPrefix() {
        return String.valueOf('\u00a7');
    }

    public static String removeColor(String string) {
        return string.replaceAll(String.valueOf(ModColor.getColorCharPrefix()) + "[a-z0-9]", "");
    }

    public static String createColors(String string) {
        return string.replaceAll("(?i)&([a-z0-9])", "\u00a7$1");
    }

    public static String booleanToColor(boolean value) {
        return value ? GREEN.toString() : RED.toString();
    }

    public static int toRGB(int r2, int g2, int b2, int a2) {
        return (a2 & 0xFF) << 24 | (r2 & 0xFF) << 16 | (g2 & 0xFF) << 8 | b2 & 0xFF;
    }

    public static String getCharAsString() {
        return String.valueOf('\u00a7');
    }

    public static Color changeBrightness(Color color, float fraction) {
        float blue;
        float green;
        float red = (float)color.getRed() + 255.0f * fraction;
        if (red > 255.0f) {
            red = 255.0f;
        }
        if (red < 0.0f) {
            red = 0.0f;
        }
        if ((green = (float)color.getGreen() + 255.0f * fraction) > 255.0f) {
            green = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        }
        if ((blue = (float)color.getBlue() + 255.0f * fraction) > 255.0f) {
            blue = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        }
        return new Color((int)red, (int)green, (int)blue);
    }

    public static Color getColorByString(String color) {
        if (color == null) {
            return null;
        }
        if (color.equals("-1")) {
            return null;
        }
        return new Color(Integer.parseInt(color));
    }

    public String toString() {
        return String.valueOf(String.valueOf('\u00a7')) + this.colorChar;
    }

    public Color getColor() {
        return this.color;
    }

    public char getColorChar() {
        return this.colorChar;
    }
}

