/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.launch.platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CommandLineOptions {
    private List<String> configs = new ArrayList<String>();

    private CommandLineOptions() {
    }

    public List<String> getConfigs() {
        return Collections.unmodifiableList(this.configs);
    }

    private void parseArgs(List<String> args) {
        boolean captureNext = false;
        for (String arg2 : args) {
            if (captureNext) {
                this.configs.add(arg2);
            }
            captureNext = "--mixin".equals(arg2) || "--mixin.config".equals(arg2);
        }
    }

    public static CommandLineOptions defaultArgs() {
        return CommandLineOptions.ofArgs(null);
    }

    public static CommandLineOptions ofArgs(List<String> args) {
        String argv;
        CommandLineOptions options = new CommandLineOptions();
        if (args == null && (argv = System.getProperty("sun.java.command")) != null) {
            args = Arrays.asList(argv.split(" "));
        }
        if (args != null) {
            options.parseArgs(args);
        }
        return options;
    }

    public static CommandLineOptions of(List<String> configs) {
        CommandLineOptions options = new CommandLineOptions();
        options.configs.addAll(configs);
        return options;
    }
}

