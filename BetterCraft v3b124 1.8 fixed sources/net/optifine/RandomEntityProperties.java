/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.IRandomEntity;
import net.optifine.RandomEntityRule;
import net.optifine.config.ConnectedParser;

public class RandomEntityProperties {
    public String name = null;
    public String basePath = null;
    public ResourceLocation[] resourceLocations = null;
    public RandomEntityRule[] rules = null;

    public RandomEntityProperties(String path, ResourceLocation[] variants) {
        ConnectedParser connectedparser = new ConnectedParser("RandomEntities");
        this.name = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.resourceLocations = variants;
    }

    public RandomEntityProperties(Properties props, String path, ResourceLocation baseResLoc) {
        ConnectedParser connectedparser = new ConnectedParser("RandomEntities");
        this.name = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.rules = this.parseRules(props, path, baseResLoc, connectedparser);
    }

    public ResourceLocation getTextureLocation(ResourceLocation loc, IRandomEntity randomEntity) {
        if (this.rules != null) {
            int i2 = 0;
            while (i2 < this.rules.length) {
                RandomEntityRule randomentityrule = this.rules[i2];
                if (randomentityrule.matches(randomEntity)) {
                    return randomentityrule.getTextureLocation(loc, randomEntity.getId());
                }
                ++i2;
            }
        }
        if (this.resourceLocations != null) {
            int j2 = randomEntity.getId();
            int k2 = j2 % this.resourceLocations.length;
            return this.resourceLocations[k2];
        }
        return loc;
    }

    private RandomEntityRule[] parseRules(Properties props, String pathProps, ResourceLocation baseResLoc, ConnectedParser cp2) {
        ArrayList<RandomEntityRule> list = new ArrayList<RandomEntityRule>();
        int i2 = props.size();
        int j2 = 0;
        while (j2 < i2) {
            RandomEntityRule randomentityrule;
            int k2 = j2 + 1;
            String s2 = props.getProperty("textures." + k2);
            if (s2 == null) {
                s2 = props.getProperty("skins." + k2);
            }
            if (s2 != null && (randomentityrule = new RandomEntityRule(props, pathProps, baseResLoc, k2, s2, cp2)).isValid(pathProps)) {
                list.add(randomentityrule);
            }
            ++j2;
        }
        RandomEntityRule[] arandomentityrule = list.toArray(new RandomEntityRule[list.size()]);
        return arandomentityrule;
    }

    public boolean isValid(String path) {
        if (this.resourceLocations == null && this.rules == null) {
            Config.warn("No skins specified: " + path);
            return false;
        }
        if (this.rules != null) {
            int i2 = 0;
            while (i2 < this.rules.length) {
                RandomEntityRule randomentityrule = this.rules[i2];
                if (!randomentityrule.isValid(path)) {
                    return false;
                }
                ++i2;
            }
        }
        if (this.resourceLocations != null) {
            int j2 = 0;
            while (j2 < this.resourceLocations.length) {
                ResourceLocation resourcelocation = this.resourceLocations[j2];
                if (!Config.hasResource(resourcelocation)) {
                    Config.warn("Texture not found: " + resourcelocation.getResourcePath());
                    return false;
                }
                ++j2;
            }
        }
        return true;
    }

    public boolean isDefault() {
        return this.rules != null ? false : this.resourceLocations == null;
    }
}

