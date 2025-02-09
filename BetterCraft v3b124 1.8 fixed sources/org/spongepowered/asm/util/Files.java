/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public final class Files {
    private Files() {
    }

    public static File toFile(URL url) throws URISyntaxException {
        return url != null ? Files.toFile(url.toURI()) : null;
    }

    public static File toFile(URI uri) {
        String strUri;
        if (uri == null) {
            return null;
        }
        if ("file".equals(uri.getScheme()) && uri.getAuthority() != null && (strUri = uri.toString()).startsWith("file://") && !strUri.startsWith("file:///")) {
            try {
                uri = new URI("file:////" + strUri.substring(7));
            }
            catch (URISyntaxException ex2) {
                throw new IllegalArgumentException(ex2.getMessage());
            }
        }
        return new File(uri);
    }
}

