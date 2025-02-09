// 
// Decompiled by Procyon v0.6.0
// 

package viaforge.loader;

import viaforge.ViaForge;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.bungee.providers.BungeeMovementTransmitter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;

public class VRProviderLoader implements ViaPlatformLoader
{
    @Override
    public void load() {
        Via.getManager().getProviders().use((Class<BungeeMovementTransmitter>)MovementTransmitterProvider.class, new BungeeMovementTransmitter());
        Via.getManager().getProviders().use((Class<VRProviderLoader$1>)VersionProvider.class, new BaseVersionProvider() {
            @Override
            public int getClosestServerProtocol(final UserConnection connection) throws Exception {
                if (connection.isClientSide()) {
                    return ViaForge.getInstance().getVersion();
                }
                return super.getClosestServerProtocol(connection);
            }
        });
    }
    
    @Override
    public void unload() {
    }
}
