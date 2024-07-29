/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_19to1_19_1.storage;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;
import java.util.Arrays;

public final class ReceivedMessagesStorage
implements StorableObject {
    private final PlayerMessageSignature[] signatures = new PlayerMessageSignature[5];
    private int size;
    private int unacknowledged;

    public void add(PlayerMessageSignature signature) {
        PlayerMessageSignature toPush = signature;
        for (int i2 = 0; i2 < this.size; ++i2) {
            PlayerMessageSignature entry = this.signatures[i2];
            this.signatures[i2] = toPush;
            toPush = entry;
            if (!entry.uuid().equals(signature.uuid())) continue;
            return;
        }
        if (this.size < this.signatures.length) {
            this.signatures[this.size++] = toPush;
        }
    }

    public PlayerMessageSignature[] lastSignatures() {
        return Arrays.copyOf(this.signatures, this.size);
    }

    public int tickUnacknowledged() {
        return this.unacknowledged++;
    }

    public void resetUnacknowledgedCount() {
        this.unacknowledged = 0;
    }
}

