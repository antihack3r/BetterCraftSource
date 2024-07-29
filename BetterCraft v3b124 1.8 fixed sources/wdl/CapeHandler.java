/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.ReflectionUtils;

public class CapeHandler {
    private static final Logger logger = LogManager.getLogger();
    private static final Map<UUID, ResourceLocation> capes = new HashMap<UUID, ResourceLocation>();
    private static final Set<EntityPlayer> handledPlayers = new HashSet<EntityPlayer>();
    private static final Map<EntityPlayer, Integer> playerFailures = new HashMap<EntityPlayer, Integer>();
    private static int totalFailures = 0;
    private static final int MAX_PLAYER_FAILURES = 40;
    private static final int MAX_TOTAL_FAILURES = 40;

    static {
        capes.put(UUID.fromString("6c8976e3-99a9-4d8b-a98e-d4c0c09b305b"), new ResourceLocation("wdl", "textures/cape_dev.png"));
        capes.put(UUID.fromString("f6c068f1-0738-4b41-bdb2-69d81d2b0f1c"), new ResourceLocation("wdl", "textures/cape_dev.png"));
    }

    public static void onWorldTick(List<EntityPlayer> players) {
        block4: {
            if (totalFailures > 40) {
                return;
            }
            try {
                handledPlayers.retainAll(players);
                for (EntityPlayer player : players) {
                    if (handledPlayers.contains(player) || !(player instanceof AbstractClientPlayer)) continue;
                    CapeHandler.setupPlayer((AbstractClientPlayer)player);
                }
            }
            catch (Exception e2) {
                logger.warn("[WDL] Failed to tick cape setup", (Throwable)e2);
                if (++totalFailures <= 40) break block4;
                logger.warn("[WDL] Disabling cape system (too many failures)");
            }
        }
    }

    private static void setupPlayer(AbstractClientPlayer player) {
        try {
            NetworkPlayerInfo info = ReflectionUtils.stealAndGetField(player, AbstractClientPlayer.class, NetworkPlayerInfo.class);
            if (info == null) {
                CapeHandler.incrementFailure(player);
                return;
            }
            GameProfile profile = info.getGameProfile();
            if (capes.containsKey(profile.getId())) {
                CapeHandler.setPlayerCape(info, capes.get(profile.getId()));
            }
            handledPlayers.add(player);
        }
        catch (Exception e2) {
            logger.warn("[WDL] Failed to perform cape set up for " + player, (Throwable)e2);
            CapeHandler.incrementFailure(player);
        }
    }

    private static void setPlayerCape(NetworkPlayerInfo info, ResourceLocation cape) throws Exception {
        boolean foundBefore = false;
        Field capeField = null;
        Field[] fieldArray = info.getClass().getDeclaredFields();
        int n2 = fieldArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Field f2 = fieldArray[n3];
            if (f2.getType().equals(ResourceLocation.class)) {
                if (foundBefore) {
                    capeField = f2;
                } else {
                    foundBefore = true;
                }
            }
            ++n3;
        }
        if (capeField != null) {
            capeField.setAccessible(true);
            capeField.set(info, cape);
        }
    }

    private static void incrementFailure(EntityPlayer player) {
        if (playerFailures.containsKey(player)) {
            int numFailures = playerFailures.get(player) + 1;
            playerFailures.put(player, numFailures);
            if (numFailures > 40) {
                handledPlayers.add(player);
                playerFailures.remove(player);
                logger.warn("[WDL] Failed to set up cape for " + player + " too many times (" + numFailures + "); skipping them");
            }
        } else {
            playerFailures.put(player, 1);
        }
    }
}

