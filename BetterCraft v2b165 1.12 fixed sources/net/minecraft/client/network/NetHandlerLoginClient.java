// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.network;

import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.network.INetHandler;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.security.PublicKey;
import net.minecraft.network.Packet;
import javax.crypto.SecretKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import java.io.IOException;
import java.net.InetSocketAddress;
import com.mojang.authlib.exceptions.AuthenticationException;
import me.amkgre.bettercraft.client.mods.mcleaks.McLeaksAPI;
import net.minecraft.client.multiplayer.GuiConnecting;
import java.math.BigInteger;
import net.minecraft.util.CryptManager;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import org.apache.logging.log4j.LogManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.login.INetHandlerLoginClient;

public class NetHandlerLoginClient implements INetHandlerLoginClient
{
    private static final Logger LOGGER;
    private final Minecraft mc;
    @Nullable
    private final GuiScreen previousGuiScreen;
    private final NetworkManager networkManager;
    private GameProfile gameProfile;
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public NetHandlerLoginClient(final NetworkManager networkManagerIn, final Minecraft mcIn, @Nullable final GuiScreen previousScreenIn) {
        this.networkManager = networkManagerIn;
        this.mc = mcIn;
        this.previousGuiScreen = previousScreenIn;
    }
    
    @Override
    public void handleEncryptionRequest(final SPacketEncryptionRequest packetIn) {
        final SecretKey secretkey = CryptManager.createNewSharedKey();
        final String s = packetIn.getServerId();
        final PublicKey publickey = packetIn.getPublicKey();
        final String s2 = new BigInteger(CryptManager.getServerIdHash(s, publickey, secretkey)).toString(16);
        GuiConnecting.verifyingsession = true;
        if (this.mc.getCurrentServerData() != null && this.mc.getCurrentServerData().isOnLAN()) {
            if (McLeaksAPI.sessions_mcLeaksSession == null) {
                try {
                    this.getSessionService().joinServer(Minecraft.getSession().getProfile(), Minecraft.getSession().getToken(), s2);
                }
                catch (final AuthenticationException var10) {
                    NetHandlerLoginClient.LOGGER.warn("Couldn't connect to auth servers but will continue to join LAN");
                }
            }
            else {
                try {
                    McLeaksAPI.joinServer(McLeaksAPI.sessions_mcLeaksSession, s2, (InetSocketAddress)this.networkManager.getRemoteAddress());
                }
                catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            try {
                if (McLeaksAPI.sessions_mcLeaksSession == null) {
                    this.getSessionService().joinServer(Minecraft.getSession().getProfile(), Minecraft.getSession().getToken(), s2);
                }
                else {
                    try {
                        McLeaksAPI.joinServer(McLeaksAPI.sessions_mcLeaksSession, s2, (InetSocketAddress)this.networkManager.getRemoteAddress());
                    }
                    catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (final AuthenticationUnavailableException var11) {
                this.networkManager.closeChannel(new TextComponentTranslation("disconnect.loginFailedInfo", new Object[] { new TextComponentTranslation("disconnect.loginFailedInfo.serversUnavailable", new Object[0]) }));
                return;
            }
            catch (final InvalidCredentialsException var12) {
                this.networkManager.closeChannel(new TextComponentTranslation("disconnect.loginFailedInfo", new Object[] { new TextComponentTranslation("disconnect.loginFailedInfo.invalidSession", new Object[0]) }));
                return;
            }
            catch (final AuthenticationException authenticationexception) {
                this.networkManager.closeChannel(new TextComponentTranslation("disconnect.loginFailedInfo", new Object[] { authenticationexception.getMessage() }));
                return;
            }
        }
        this.networkManager.sendPacket(new CPacketEncryptionResponse(secretkey, publickey, packetIn.getVerifyToken()), new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(final Future<? super Void> p_operationComplete_1_) throws Exception {
                NetHandlerLoginClient.this.networkManager.enableEncryption(secretkey);
            }
        }, (GenericFutureListener<? extends Future<? super Void>>[])new GenericFutureListener[0]);
    }
    
    private MinecraftSessionService getSessionService() {
        return this.mc.getSessionService();
    }
    
    @Override
    public void handleLoginSuccess(final SPacketLoginSuccess packetIn) {
        GuiConnecting.sucess = true;
        this.gameProfile = packetIn.getProfile();
        this.networkManager.setConnectionState(EnumConnectionState.PLAY);
        this.networkManager.setNetHandler(new NetHandlerPlayClient(this.mc, this.previousGuiScreen, this.networkManager, this.gameProfile));
    }
    
    @Override
    public void onDisconnect(final ITextComponent reason) {
        if (this.previousGuiScreen != null && this.previousGuiScreen instanceof GuiScreenRealmsProxy) {
            this.mc.displayGuiScreen(new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.previousGuiScreen).getProxy(), "connect.failed", reason).getProxy());
        }
        else {
            this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", reason));
        }
    }
    
    @Override
    public void handleDisconnect(final SPacketDisconnect packetIn) {
        this.networkManager.closeChannel(packetIn.getReason());
    }
    
    @Override
    public void handleEnableCompression(final SPacketEnableCompression packetIn) {
        if (!this.networkManager.isLocalChannel()) {
            this.networkManager.setCompressionThreshold(packetIn.getCompressionThreshold());
        }
    }
}
