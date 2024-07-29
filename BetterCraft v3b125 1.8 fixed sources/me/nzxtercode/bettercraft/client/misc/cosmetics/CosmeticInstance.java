/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticBase;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticBandana;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticBatWings;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticBeeWings;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticBlaze;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticCap;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticCape;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticCrownKing;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticCrystalWings;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticDevilHorns;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticDevilWings;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticDragonWings;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticEnderCrystal;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticGalaxySkin;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticGuardianSpikes;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticHalo;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticHeadset;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticNerdGlasses;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticSixPath;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticSlimeGel;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticSusanno;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticTopHat;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticVexWings;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticVillagerNose;
import me.nzxtercode.bettercraft.client.misc.cosmetics.impl.CosmeticWitchHat;
import me.nzxtercode.bettercraft.client.misc.irc.IRC;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class CosmeticInstance {
    private static final List<CosmeticBase> cosmetics = new CopyOnWriteArrayList<CosmeticBase>();
    private static boolean isRegistered = false;
    public static final Map<String, List<CosmeticBase>> USER_COSMETICS = new ConcurrentHashMap<String, List<CosmeticBase>>();

    public static void registerLayers(RenderPlayer renderPlayer, Consumer<CosmeticBase> handleLayerRegister) {
        CosmeticInstance.register(handleLayerRegister, new CosmeticBandana(renderPlayer), new CosmeticBatWings(renderPlayer), new CosmeticBeeWings(renderPlayer), new CosmeticBlaze(renderPlayer), new CosmeticCap(renderPlayer), new CosmeticCape(renderPlayer), new CosmeticCrownKing(renderPlayer), new CosmeticCrystalWings(renderPlayer), new CosmeticDevilHorns(renderPlayer), new CosmeticDevilWings(renderPlayer), new CosmeticDragonWings(renderPlayer), new CosmeticEnderCrystal(renderPlayer), new CosmeticGalaxySkin(renderPlayer), new CosmeticGuardianSpikes(renderPlayer), new CosmeticHalo(renderPlayer), new CosmeticHeadset(renderPlayer), new CosmeticNerdGlasses(renderPlayer), new CosmeticSixPath(renderPlayer), new CosmeticSlimeGel(renderPlayer), new CosmeticSusanno(renderPlayer), new CosmeticTopHat(renderPlayer), new CosmeticVexWings(renderPlayer), new CosmeticVillagerNose(renderPlayer), new CosmeticWitchHat(renderPlayer));
    }

    public static void sendCosmetics() {
        JsonArray array = new JsonArray();
        CosmeticInstance.getCosmetics().forEach(cosmetic -> {
            if (cosmetic.isEnabled()) {
                array.add(new JsonPrimitive(cosmetic.getId()));
            }
        });
        IRC.getInstance().sendMessage(IRC.getInstance().currentChannel, "$Cosmetics " + array.toString());
    }

    public static void register(Consumer<CosmeticBase> handleLayerRegister, CosmeticBase ... cosmetic) {
        Arrays.asList(cosmetic).forEach(cosmeticObject -> {
            if (!isRegistered) {
                cosmetics.add((CosmeticBase)cosmeticObject);
            }
            handleLayerRegister.accept((CosmeticBase)cosmeticObject);
        });
        isRegistered = true;
    }

    public static final List<CosmeticBase> getCosmetics() {
        return cosmetics;
    }
}

