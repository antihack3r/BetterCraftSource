/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.commands.defaultsubs;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.command.ViaSubCommand;
import com.viaversion.viaversion.util.DumpUtil;

public class DumpSubCmd
extends ViaSubCommand {
    @Override
    public String name() {
        return "dump";
    }

    @Override
    public String description() {
        return "Dump information about your server, this is helpful if you report bugs.";
    }

    @Override
    public boolean execute(ViaCommandSender sender, String[] args) {
        DumpUtil.postDump(sender.getUUID()).whenComplete((url, e2) -> {
            if (e2 != null) {
                sender.sendMessage("\u00a74" + e2.getMessage());
                return;
            }
            sender.sendMessage("\u00a72We've made a dump with useful information, report your issue and provide this url: " + url);
        });
        return true;
    }
}

