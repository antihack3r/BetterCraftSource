/*
 * Decompiled with CFR 0.152.
 */
package de.florianmichael.vialoadingbase.command.impl;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.command.ViaSubCommand;
import io.netty.util.ResourceLeakDetector;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LeakDetectSubCommand
extends ViaSubCommand {
    @Override
    public String name() {
        return "leakdetect";
    }

    @Override
    public String description() {
        return "Sets ResourceLeakDetector level";
    }

    @Override
    public boolean execute(ViaCommandSender viaCommandSender, String[] strings) {
        if (strings.length == 1) {
            try {
                ResourceLeakDetector.Level level = ResourceLeakDetector.Level.valueOf(strings[0]);
                ResourceLeakDetector.setLevel(level);
                viaCommandSender.sendMessage("Set leak detector level to " + (Object)((Object)level));
            }
            catch (IllegalArgumentException e2) {
                viaCommandSender.sendMessage("Invalid level (" + Arrays.toString((Object[])ResourceLeakDetector.Level.values()) + ")");
            }
        } else {
            viaCommandSender.sendMessage("Current leak detection level is " + (Object)((Object)ResourceLeakDetector.getLevel()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(ViaCommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(ResourceLeakDetector.Level.values()).map(Enum::name).filter(it2 -> it2.startsWith(args[0])).collect(Collectors.toList());
        }
        return super.onTabComplete(sender, args);
    }
}
