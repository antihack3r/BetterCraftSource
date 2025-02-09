/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator {
    private final Cipher cipher;
    private byte[] field_150505_b = new byte[0];
    private byte[] field_150506_c = new byte[0];

    protected NettyEncryptionTranslator(Cipher cipherIn) {
        this.cipher = cipherIn;
    }

    private byte[] func_150502_a(ByteBuf buf) {
        int i2 = buf.readableBytes();
        if (this.field_150505_b.length < i2) {
            this.field_150505_b = new byte[i2];
        }
        buf.readBytes(this.field_150505_b, 0, i2);
        return this.field_150505_b;
    }

    protected ByteBuf decipher(ChannelHandlerContext ctx, ByteBuf buffer) throws ShortBufferException {
        int i2 = buffer.readableBytes();
        byte[] abyte = this.func_150502_a(buffer);
        ByteBuf bytebuf = ctx.alloc().heapBuffer(this.cipher.getOutputSize(i2));
        bytebuf.writerIndex(this.cipher.update(abyte, 0, i2, bytebuf.array(), bytebuf.arrayOffset()));
        return bytebuf;
    }

    protected void cipher(ByteBuf in2, ByteBuf out) throws ShortBufferException {
        int i2 = in2.readableBytes();
        byte[] abyte = this.func_150502_a(in2);
        int j2 = this.cipher.getOutputSize(i2);
        if (this.field_150506_c.length < j2) {
            this.field_150506_c = new byte[j2];
        }
        out.writeBytes(this.field_150506_c, 0, this.cipher.update(abyte, 0, i2, this.field_150506_c));
    }
}

