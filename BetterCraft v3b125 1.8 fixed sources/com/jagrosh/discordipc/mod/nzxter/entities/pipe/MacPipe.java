/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.entities.pipe;

import com.jagrosh.discordipc.mod.nzxter.IPCClient;
import com.jagrosh.discordipc.mod.nzxter.entities.Callback;
import com.jagrosh.discordipc.mod.nzxter.entities.pipe.UnixPipe;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MacPipe
extends UnixPipe {
    MacPipe(IPCClient ipcClient, HashMap<String, Callback> callbacks, File fileLocation) throws IOException {
        super(ipcClient, callbacks, fileLocation);
    }
}

