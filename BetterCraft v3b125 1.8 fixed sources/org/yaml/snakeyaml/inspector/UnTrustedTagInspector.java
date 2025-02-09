/*
 * Decompiled with CFR 0.152.
 */
package org.yaml.snakeyaml.inspector;

import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.nodes.Tag;

public final class UnTrustedTagInspector
implements TagInspector {
    @Override
    public boolean isGlobalTagAllowed(Tag tag) {
        return false;
    }
}

