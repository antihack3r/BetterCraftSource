/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.namehistory;

import java.util.UUID;
import net.labymod.utils.UUIDFetcher;
import org.apache.commons.lang3.ArrayUtils;

public class NameHistory {
    private UUID uuid;
    private UUIDFetcher[] changes;

    public NameHistory(UUID uuid, UUIDFetcher[] changes) {
        this.uuid = uuid;
        this.changes = changes;
        ArrayUtils.reverse(changes);
    }

    public UUIDFetcher[] getChanges() {
        return this.changes;
    }

    public UUID getUUID() {
        return this.uuid;
    }
}

