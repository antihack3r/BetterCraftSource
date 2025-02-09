// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.management;

import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.Agent;
import java.util.Iterator;
import com.google.common.collect.Iterators;
import net.minecraft.util.StringUtils;
import javax.annotation.Nullable;
import com.google.common.base.Predicate;
import com.mojang.authlib.ProfileLookupCallback;
import java.util.Collection;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import java.io.File;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter
{
    private static final Logger LOGGER;
    public static final File OLD_IPBAN_FILE;
    public static final File OLD_PLAYERBAN_FILE;
    public static final File OLD_OPS_FILE;
    public static final File OLD_WHITELIST_FILE;
    
    static {
        LOGGER = LogManager.getLogger();
        OLD_IPBAN_FILE = new File("banned-ips.txt");
        OLD_PLAYERBAN_FILE = new File("banned-players.txt");
        OLD_OPS_FILE = new File("ops.txt");
        OLD_WHITELIST_FILE = new File("white-list.txt");
    }
    
    private static void lookupNames(final MinecraftServer server, final Collection<String> names, final ProfileLookupCallback callback) {
        final String[] astring = Iterators.toArray(Iterators.filter(names.iterator(), new Predicate<String>() {
            @Override
            public boolean apply(@Nullable final String p_apply_1_) {
                return !StringUtils.isNullOrEmpty(p_apply_1_);
            }
        }), String.class);
        if (server.isServerInOnlineMode()) {
            server.getGameProfileRepository().findProfilesByNames(astring, Agent.MINECRAFT, callback);
        }
        else {
            String[] array;
            for (int length = (array = astring).length, i = 0; i < length; ++i) {
                final String s = array[i];
                final UUID uuid = EntityPlayer.getUUID(new GameProfile(null, s));
                final GameProfile gameprofile = new GameProfile(uuid, s);
                callback.onProfileLookupSucceeded(gameprofile);
            }
        }
    }
    
    public static String convertMobOwnerIfNeeded(final MinecraftServer server, final String username) {
        if (StringUtils.isNullOrEmpty(username) || username.length() > 16) {
            return username;
        }
        final GameProfile gameprofile = server.getPlayerProfileCache().getGameProfileForUsername(username);
        if (gameprofile != null && gameprofile.getId() != null) {
            return gameprofile.getId().toString();
        }
        if (!server.isSinglePlayer() && server.isServerInOnlineMode()) {
            final List<GameProfile> list = (List<GameProfile>)Lists.newArrayList();
            final ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                @Override
                public void onProfileLookupSucceeded(final GameProfile p_onProfileLookupSucceeded_1_) {
                    server.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                    list.add(p_onProfileLookupSucceeded_1_);
                }
                
                @Override
                public void onProfileLookupFailed(final GameProfile p_onProfileLookupFailed_1_, final Exception p_onProfileLookupFailed_2_) {
                    PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
                }
            };
            lookupNames(server, Lists.newArrayList(username), profilelookupcallback);
            return (!list.isEmpty() && list.get(0).getId() != null) ? list.get(0).getId().toString() : "";
        }
        return EntityPlayer.getUUID(new GameProfile(null, username)).toString();
    }
}
