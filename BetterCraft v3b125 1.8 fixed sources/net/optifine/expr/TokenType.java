/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.expr;

public enum TokenType {
    IDENTIFIER("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_:."),
    NUMBER("0123456789", "0123456789."),
    OPERATOR("+-*/%!&|<>=", "&|="),
    COMMA(","),
    BRACKET_OPEN("("),
    BRACKET_CLOSE(")");

    private String charsFirst;
    private String charsNext;
    public static final TokenType[] VALUES;

    static {
        VALUES = TokenType.values();
    }

    private TokenType(String charsFirst) {
        this(charsFirst, "");
    }

    private TokenType(String charsFirst, String charsNext) {
        this.charsFirst = charsFirst;
        this.charsNext = charsNext;
    }

    public String getCharsFirst() {
        return this.charsFirst;
    }

    public String getCharsNext() {
        return this.charsNext;
    }

    public static TokenType getTypeByFirstChar(char ch) {
        int i2 = 0;
        while (i2 < VALUES.length) {
            TokenType tokentype = VALUES[i2];
            if (tokentype.getCharsFirst().indexOf(ch) >= 0) {
                return tokentype;
            }
            ++i2;
        }
        return null;
    }

    public boolean hasCharNext(char ch) {
        return this.charsNext.indexOf(ch) >= 0;
    }

    private static class Const {
        static final String ALPHAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        static final String DIGITS = "0123456789";

        private Const() {
        }
    }
}

