/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson;

import com.viaversion.viaversion.libs.gson.FieldNamingStrategy;
import java.lang.reflect.Field;
import java.util.Locale;

public enum FieldNamingPolicy implements FieldNamingStrategy
{
    IDENTITY{

        @Override
        public String translateName(Field f2) {
            return f2.getName();
        }
    }
    ,
    UPPER_CAMEL_CASE{

        @Override
        public String translateName(Field f2) {
            return _2.upperCaseFirstLetter(f2.getName());
        }
    }
    ,
    UPPER_CAMEL_CASE_WITH_SPACES{

        @Override
        public String translateName(Field f2) {
            return _3.upperCaseFirstLetter(_3.separateCamelCase(f2.getName(), ' '));
        }
    }
    ,
    UPPER_CASE_WITH_UNDERSCORES{

        @Override
        public String translateName(Field f2) {
            return _4.separateCamelCase(f2.getName(), '_').toUpperCase(Locale.ENGLISH);
        }
    }
    ,
    LOWER_CASE_WITH_UNDERSCORES{

        @Override
        public String translateName(Field f2) {
            return _5.separateCamelCase(f2.getName(), '_').toLowerCase(Locale.ENGLISH);
        }
    }
    ,
    LOWER_CASE_WITH_DASHES{

        @Override
        public String translateName(Field f2) {
            return _6.separateCamelCase(f2.getName(), '-').toLowerCase(Locale.ENGLISH);
        }
    }
    ,
    LOWER_CASE_WITH_DOTS{

        @Override
        public String translateName(Field f2) {
            return _7.separateCamelCase(f2.getName(), '.').toLowerCase(Locale.ENGLISH);
        }
    };


    static String separateCamelCase(String name, char separator) {
        StringBuilder translation = new StringBuilder();
        int length = name.length();
        for (int i2 = 0; i2 < length; ++i2) {
            char character = name.charAt(i2);
            if (Character.isUpperCase(character) && translation.length() != 0) {
                translation.append(separator);
            }
            translation.append(character);
        }
        return translation.toString();
    }

    static String upperCaseFirstLetter(String s2) {
        int length = s2.length();
        for (int i2 = 0; i2 < length; ++i2) {
            char c2 = s2.charAt(i2);
            if (!Character.isLetter(c2)) continue;
            if (Character.isUpperCase(c2)) {
                return s2;
            }
            char uppercased = Character.toUpperCase(c2);
            if (i2 == 0) {
                return uppercased + s2.substring(1);
            }
            return s2.substring(0, i2) + uppercased + s2.substring(i2 + 1);
        }
        return s2;
    }
}

