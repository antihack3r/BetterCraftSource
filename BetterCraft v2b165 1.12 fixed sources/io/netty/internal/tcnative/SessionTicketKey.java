// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.internal.tcnative;

public final class SessionTicketKey
{
    public static final int NAME_SIZE = 16;
    public static final int HMAC_KEY_SIZE = 16;
    public static final int AES_KEY_SIZE = 16;
    public static final int TICKET_KEY_SIZE = 48;
    final byte[] name;
    final byte[] hmacKey;
    final byte[] aesKey;
    
    public SessionTicketKey(final byte[] name, final byte[] hmacKey, final byte[] aesKey) {
        if (name == null || name.length != 16) {
            throw new IllegalArgumentException("Length of name should be 16");
        }
        if (hmacKey == null || hmacKey.length != 16) {
            throw new IllegalArgumentException("Length of hmacKey should be 16");
        }
        if (aesKey == null || aesKey.length != 16) {
            throw new IllegalArgumentException("Length of aesKey should be 16");
        }
        this.name = name;
        this.hmacKey = hmacKey;
        this.aesKey = aesKey;
    }
    
    public byte[] getName() {
        return this.name.clone();
    }
    
    public byte[] getHmacKey() {
        return this.hmacKey.clone();
    }
    
    public byte[] getAesKey() {
        return this.aesKey.clone();
    }
}
