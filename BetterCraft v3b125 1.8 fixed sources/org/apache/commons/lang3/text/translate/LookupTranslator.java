/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;

public class LookupTranslator
extends CharSequenceTranslator {
    private final HashMap<String, CharSequence> lookupMap = new HashMap();
    private final int shortest;
    private final int longest;

    public LookupTranslator(CharSequence[] ... lookup) {
        int _shortest = Integer.MAX_VALUE;
        int _longest = 0;
        if (lookup != null) {
            for (CharSequence[] seq : lookup) {
                this.lookupMap.put(seq[0].toString(), seq[1]);
                int sz = seq[0].length();
                if (sz < _shortest) {
                    _shortest = sz;
                }
                if (sz <= _longest) continue;
                _longest = sz;
            }
        }
        this.shortest = _shortest;
        this.longest = _longest;
    }

    @Override
    public int translate(CharSequence input, int index, Writer out) throws IOException {
        int max = this.longest;
        if (index + this.longest > input.length()) {
            max = input.length() - index;
        }
        for (int i2 = max; i2 >= this.shortest; --i2) {
            CharSequence subSeq = input.subSequence(index, index + i2);
            CharSequence result = this.lookupMap.get(subSeq.toString());
            if (result == null) continue;
            out.write(result.toString());
            return i2;
        }
        return 0;
    }
}

