/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {
    private String name;
    private String[] aliases;

    protected Command(String name, String ... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public abstract void run(String var1, String[] var2);

    public abstract List<String> autocomplete(int var1, String[] var2);

    boolean match(String name) {
        String[] stringArray = this.aliases;
        int n2 = this.aliases.length;
        int n3 = 0;
        while (n3 < n2) {
            String alias = stringArray[n3];
            if (alias.equalsIgnoreCase(name)) {
                return true;
            }
            ++n3;
        }
        return this.name.equalsIgnoreCase(name);
    }

    public List<String> getNameAndAliases() {
        ArrayList<String> l2 = new ArrayList<String>();
        l2.add(this.name);
        l2.addAll(Arrays.asList(this.aliases));
        return l2;
    }
}

