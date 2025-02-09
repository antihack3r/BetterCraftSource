// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.StringUtil;
import java.net.IDN;
import io.netty.util.NetUtil;

public final class DefaultSocks5CommandRequest extends AbstractSocks5Message implements Socks5CommandRequest
{
    private final Socks5CommandType type;
    private final Socks5AddressType dstAddrType;
    private final String dstAddr;
    private final int dstPort;
    
    public DefaultSocks5CommandRequest(final Socks5CommandType type, final Socks5AddressType dstAddrType, String dstAddr, final int dstPort) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (dstAddrType == null) {
            throw new NullPointerException("dstAddrType");
        }
        if (dstAddr == null) {
            throw new NullPointerException("dstAddr");
        }
        if (dstAddrType == Socks5AddressType.IPv4) {
            if (!NetUtil.isValidIpV4Address(dstAddr)) {
                throw new IllegalArgumentException("dstAddr: " + dstAddr + " (expected: a valid IPv4 address)");
            }
        }
        else if (dstAddrType == Socks5AddressType.DOMAIN) {
            dstAddr = IDN.toASCII(dstAddr);
            if (dstAddr.length() > 255) {
                throw new IllegalArgumentException("dstAddr: " + dstAddr + " (expected: less than 256 chars)");
            }
        }
        else if (dstAddrType == Socks5AddressType.IPv6 && !NetUtil.isValidIpV6Address(dstAddr)) {
            throw new IllegalArgumentException("dstAddr: " + dstAddr + " (expected: a valid IPv6 address");
        }
        if (dstPort <= 0 || dstPort >= 65536) {
            throw new IllegalArgumentException("dstPort: " + dstPort + " (expected: 1~65535)");
        }
        this.type = type;
        this.dstAddrType = dstAddrType;
        this.dstAddr = dstAddr;
        this.dstPort = dstPort;
    }
    
    @Override
    public Socks5CommandType type() {
        return this.type;
    }
    
    @Override
    public Socks5AddressType dstAddrType() {
        return this.dstAddrType;
    }
    
    @Override
    public String dstAddr() {
        return this.dstAddr;
    }
    
    @Override
    public int dstPort() {
        return this.dstPort;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(128);
        buf.append(StringUtil.simpleClassName(this));
        final DecoderResult decoderResult = this.decoderResult();
        if (!decoderResult.isSuccess()) {
            buf.append("(decoderResult: ");
            buf.append(decoderResult);
            buf.append(", type: ");
        }
        else {
            buf.append("(type: ");
        }
        buf.append(this.type());
        buf.append(", dstAddrType: ");
        buf.append(this.dstAddrType());
        buf.append(", dstAddr: ");
        buf.append(this.dstAddr());
        buf.append(", dstPort: ");
        buf.append(this.dstPort());
        buf.append(')');
        return buf.toString();
    }
}
