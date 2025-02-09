/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import wdl.api.ISpecialEntityHandler;
import wdl.api.IWDLModDescripted;

public class HologramHandler
implements ISpecialEntityHandler,
IWDLModDescripted {
    @Override
    public boolean isValidEnvironment(String version) {
        return true;
    }

    @Override
    public String getEnvironmentErrorMessage(String version) {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Hologram support";
    }

    @Override
    public Multimap<String, String> getSpecialEntities() {
        HashMultimap<String, String> returned = HashMultimap.create();
        returned.put("ArmorStand", "Hologram");
        return returned;
    }

    @Override
    public String getSpecialEntityName(Entity entity) {
        if (entity instanceof EntityArmorStand && entity.isInvisible() && entity.hasCustomName()) {
            return "Hologram";
        }
        return null;
    }

    @Override
    public String getSpecialEntityCategory(String name) {
        if (name.equals("Hologram")) {
            return "Other";
        }
        return null;
    }

    @Override
    public int getSpecialEntityTrackDistance(String name) {
        return -1;
    }

    @Override
    public String getMainAuthor() {
        return "Pokechu22";
    }

    @Override
    public String[] getAuthors() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Provides basic support for disabling holograms.";
    }
}

