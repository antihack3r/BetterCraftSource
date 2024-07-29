/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager.thealtening;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Utilities {
    private static final Utilities INSTANCE = new Utilities();
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = 0x7FFFFFF7;

    public byte[] readAllBytes(InputStream inputStream) throws IOException {
        byte[] buf = new byte[8192];
        int capacity = buf.length;
        int nread = 0;
        while (true) {
            int n2;
            if ((n2 = inputStream.read(buf, nread, capacity - nread)) > 0) {
                nread += n2;
                continue;
            }
            if (n2 < 0) break;
            if (capacity <= 0x7FFFFFF7 - capacity) {
                capacity <<= 1;
            } else {
                if (capacity == 0x7FFFFFF7) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                capacity = 0x7FFFFFF7;
            }
            buf = Arrays.copyOf(buf, capacity);
        }
        return capacity == nread ? buf : Arrays.copyOf(buf, nread);
    }

    public static Utilities getInstance() {
        return INSTANCE;
    }
}

