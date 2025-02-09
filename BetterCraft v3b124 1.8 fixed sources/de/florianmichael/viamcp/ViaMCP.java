/*
 * Decompiled with CFR 0.152.
 */
package de.florianmichael.viamcp;

import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.Protocol1_16_4To1_17;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.gui.AsyncVersionSlider;
import java.io.File;

public class ViaMCP {
    public static final int NATIVE_VERSION = 47;
    private static final ViaMCP INSTANCE = new ViaMCP();
    private static AsyncVersionSlider asyncVersionSlider;

    public static ViaMCP getInstance() {
        return INSTANCE;
    }

    public static void init() {
        ViaLoadingBase.ViaLoadingBaseBuilder.create().runDirectory(new File("ViaMCP")).nativeVersion(47).onProtocolReload(comparableProtocolVersion -> {
            if (ViaMCP.getAsyncVersionSlider() != null) {
                ViaMCP.getAsyncVersionSlider().setVersion(comparableProtocolVersion.getVersion());
            }
        }).build();
        ViaMCP.fixTransactions();
    }

    private static void fixTransactions() {
        Protocol1_16_4To1_17 protocol = Via.getManager().getProtocolManager().getProtocol(Protocol1_16_4To1_17.class);
        protocol.registerClientbound(ClientboundPackets1_17.PING, ClientboundPackets1_16_2.WINDOW_CONFIRMATION, wrapper -> {}, true);
        protocol.registerServerbound(ServerboundPackets1_16_2.WINDOW_CONFIRMATION, ServerboundPackets1_17.PONG, wrapper -> {}, true);
    }

    public void initAsyncSlider() {
        this.initAsyncSlider(5, 5, 110, 20);
    }

    public void initAsyncSlider(int x2, int y2, int width, int height) {
        asyncVersionSlider = new AsyncVersionSlider(-1, x2, y2, Math.max(width, 110), height);
    }

    public static AsyncVersionSlider getAsyncVersionSlider() {
        return asyncVersionSlider;
    }
}

