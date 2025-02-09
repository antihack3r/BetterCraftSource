/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.gui;

import net.minecraft.util.ChatAllowedCharacters;

public class CharacterFilter {
    public static String filerAllowedCharacters(String str, boolean section) {
        char[] arr2;
        StringBuilder sb2 = new StringBuilder();
        char[] cArray = arr2 = str.toCharArray();
        int n2 = arr2.length;
        int n3 = 0;
        while (n3 < n2) {
            char c2 = cArray[n3];
            if (ChatAllowedCharacters.isAllowedCharacter(c2) || section && (c2 == '\u00a7' || c2 == '\n')) {
                sb2.append(c2);
            }
            ++n3;
        }
        return sb2.toString();
    }
}

