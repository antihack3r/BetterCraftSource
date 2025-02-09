/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

final class ASMContentHandler$RuleSet {
    private final HashMap rules = new HashMap();
    private final ArrayList lpatterns = new ArrayList();
    private final ArrayList rpatterns = new ArrayList();

    ASMContentHandler$RuleSet() {
    }

    public void add(String string, Object object) {
        String string2 = string;
        if (string.startsWith("*/")) {
            string2 = string.substring(1);
            this.lpatterns.add(string2);
        } else if (string.endsWith("/*")) {
            string2 = string.substring(0, string.length() - 1);
            this.rpatterns.add(string2);
        }
        this.rules.put(string2, object);
    }

    public Object match(String string) {
        String string2;
        if (this.rules.containsKey(string)) {
            return this.rules.get(string);
        }
        int n2 = string.lastIndexOf(47);
        Iterator iterator = this.lpatterns.iterator();
        while (iterator.hasNext()) {
            string2 = (String)iterator.next();
            if (!string.substring(n2).endsWith(string2)) continue;
            return this.rules.get(string2);
        }
        iterator = this.rpatterns.iterator();
        while (iterator.hasNext()) {
            string2 = (String)iterator.next();
            if (!string.startsWith(string2)) continue;
            return this.rules.get(string2);
        }
        return null;
    }
}

