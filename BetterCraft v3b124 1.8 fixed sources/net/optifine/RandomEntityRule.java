/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.Properties;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.IRandomEntity;
import net.optifine.RandomEntities;
import net.optifine.RandomEntity;
import net.optifine.config.ConnectedParser;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.config.VillagerProfession;
import net.optifine.config.Weather;
import net.optifine.reflect.Reflector;
import net.optifine.util.ArrayUtils;
import net.optifine.util.MathUtils;

public class RandomEntityRule {
    private String pathProps = null;
    private ResourceLocation baseResLoc = null;
    private int index;
    private int[] textures = null;
    private ResourceLocation[] resourceLocations = null;
    private int[] weights = null;
    private BiomeGenBase[] biomes = null;
    private RangeListInt heights = null;
    private RangeListInt healthRange = null;
    private boolean healthPercent = false;
    private NbtTagValue nbtName = null;
    public int[] sumWeights = null;
    public int sumAllWeights = 1;
    private VillagerProfession[] professions = null;
    private EnumDyeColor[] collarColors = null;
    private Boolean baby = null;
    private RangeListInt moonPhases = null;
    private RangeListInt dayTimes = null;
    private Weather[] weatherList = null;

    public RandomEntityRule(Properties props, String pathProps, ResourceLocation baseResLoc, int index, String valTextures, ConnectedParser cp2) {
        String s2;
        this.pathProps = pathProps;
        this.baseResLoc = baseResLoc;
        this.index = index;
        this.textures = cp2.parseIntList(valTextures);
        this.weights = cp2.parseIntList(props.getProperty("weights." + index));
        this.biomes = cp2.parseBiomes(props.getProperty("biomes." + index));
        this.heights = cp2.parseRangeListInt(props.getProperty("heights." + index));
        if (this.heights == null) {
            this.heights = this.parseMinMaxHeight(props, index);
        }
        if ((s2 = props.getProperty("health." + index)) != null) {
            this.healthPercent = s2.contains("%");
            s2 = s2.replace("%", "");
            this.healthRange = cp2.parseRangeListInt(s2);
        }
        this.nbtName = cp2.parseNbtTagValue("name", props.getProperty("name." + index));
        this.professions = cp2.parseProfessions(props.getProperty("professions." + index));
        this.collarColors = cp2.parseDyeColors(props.getProperty("collarColors." + index), "collar color", ConnectedParser.DYE_COLORS_INVALID);
        this.baby = cp2.parseBooleanObject(props.getProperty("baby." + index));
        this.moonPhases = cp2.parseRangeListInt(props.getProperty("moonPhase." + index));
        this.dayTimes = cp2.parseRangeListInt(props.getProperty("dayTime." + index));
        this.weatherList = cp2.parseWeather(props.getProperty("weather." + index), "weather." + index, null);
    }

    private RangeListInt parseMinMaxHeight(Properties props, int index) {
        String s2 = props.getProperty("minHeight." + index);
        String s1 = props.getProperty("maxHeight." + index);
        if (s2 == null && s1 == null) {
            return null;
        }
        int i2 = 0;
        if (s2 != null && (i2 = Config.parseInt(s2, -1)) < 0) {
            Config.warn("Invalid minHeight: " + s2);
            return null;
        }
        int j2 = 256;
        if (s1 != null && (j2 = Config.parseInt(s1, -1)) < 0) {
            Config.warn("Invalid maxHeight: " + s1);
            return null;
        }
        if (j2 < 0) {
            Config.warn("Invalid minHeight, maxHeight: " + s2 + ", " + s1);
            return null;
        }
        RangeListInt rangelistint = new RangeListInt();
        rangelistint.addRange(new RangeInt(i2, j2));
        return rangelistint;
    }

    public boolean isValid(String path) {
        if (this.textures != null && this.textures.length != 0) {
            if (this.resourceLocations != null) {
                return true;
            }
            this.resourceLocations = new ResourceLocation[this.textures.length];
            boolean flag = this.pathProps.startsWith("mcpatcher/mob/");
            ResourceLocation resourcelocation = RandomEntities.getLocationRandom(this.baseResLoc, flag);
            if (resourcelocation == null) {
                Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
                return false;
            }
            int i2 = 0;
            while (i2 < this.resourceLocations.length) {
                int j2 = this.textures[i2];
                if (j2 <= 1) {
                    this.resourceLocations[i2] = this.baseResLoc;
                } else {
                    ResourceLocation resourcelocation1 = RandomEntities.getLocationIndexed(resourcelocation, j2);
                    if (resourcelocation1 == null) {
                        Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
                        return false;
                    }
                    if (!Config.hasResource(resourcelocation1)) {
                        Config.warn("Texture not found: " + resourcelocation1.getResourcePath());
                        return false;
                    }
                    this.resourceLocations[i2] = resourcelocation1;
                }
                ++i2;
            }
            if (this.weights != null) {
                if (this.weights.length > this.resourceLocations.length) {
                    Config.warn("More weights defined than skins, trimming weights: " + path);
                    int[] aint = new int[this.resourceLocations.length];
                    System.arraycopy(this.weights, 0, aint, 0, aint.length);
                    this.weights = aint;
                }
                if (this.weights.length < this.resourceLocations.length) {
                    Config.warn("Less weights defined than skins, expanding weights: " + path);
                    int[] aint1 = new int[this.resourceLocations.length];
                    System.arraycopy(this.weights, 0, aint1, 0, this.weights.length);
                    int l2 = MathUtils.getAverage(this.weights);
                    int j1 = this.weights.length;
                    while (j1 < aint1.length) {
                        aint1[j1] = l2;
                        ++j1;
                    }
                    this.weights = aint1;
                }
                this.sumWeights = new int[this.weights.length];
                int k2 = 0;
                int i1 = 0;
                while (i1 < this.weights.length) {
                    if (this.weights[i1] < 0) {
                        Config.warn("Invalid weight: " + this.weights[i1]);
                        return false;
                    }
                    this.sumWeights[i1] = k2 += this.weights[i1];
                    ++i1;
                }
                this.sumAllWeights = k2;
                if (this.sumAllWeights <= 0) {
                    Config.warn("Invalid sum of all weights: " + k2);
                    this.sumAllWeights = 1;
                }
            }
            if (this.professions == ConnectedParser.PROFESSIONS_INVALID) {
                Config.warn("Invalid professions or careers: " + path);
                return false;
            }
            if (this.collarColors == ConnectedParser.DYE_COLORS_INVALID) {
                Config.warn("Invalid collar colors: " + path);
                return false;
            }
            return true;
        }
        Config.warn("Invalid skins for rule: " + this.index);
        return false;
    }

    public boolean matches(IRandomEntity randomEntity) {
        Weather weather;
        WorldClient world2;
        int k1;
        WorldClient world1;
        int j1;
        WorldClient world;
        EntityLiving entityliving;
        RandomEntity randomentity2;
        Entity entity2;
        RandomEntity randomentity1;
        Entity entity1;
        RandomEntity randomentity;
        Entity entity;
        String s2;
        BlockPos blockpos;
        if (this.biomes != null && !Matches.biome(randomEntity.getSpawnBiome(), this.biomes)) {
            return false;
        }
        if (this.heights != null && (blockpos = randomEntity.getSpawnPosition()) != null && !this.heights.isInRange(blockpos.getY())) {
            return false;
        }
        if (this.healthRange != null) {
            int i2;
            int i1 = randomEntity.getHealth();
            if (this.healthPercent && (i2 = randomEntity.getMaxHealth()) > 0) {
                i1 = (int)((double)(i1 * 100) / (double)i2);
            }
            if (!this.healthRange.isInRange(i1)) {
                return false;
            }
        }
        if (this.nbtName != null && !this.nbtName.matchesValue(s2 = randomEntity.getName())) {
            return false;
        }
        if (this.professions != null && randomEntity instanceof RandomEntity && (entity = (randomentity = (RandomEntity)randomEntity).getEntity()) instanceof EntityVillager) {
            EntityVillager entityvillager = (EntityVillager)entity;
            int j2 = entityvillager.getProfession();
            int k2 = Reflector.getFieldValueInt(entityvillager, Reflector.EntityVillager_careerId, -1);
            if (j2 < 0 || k2 < 0) {
                return false;
            }
            boolean flag = false;
            int l2 = 0;
            while (l2 < this.professions.length) {
                VillagerProfession villagerprofession = this.professions[l2];
                if (villagerprofession.matches(j2, k2)) {
                    flag = true;
                    break;
                }
                ++l2;
            }
            if (!flag) {
                return false;
            }
        }
        if (this.collarColors != null && randomEntity instanceof RandomEntity && (entity1 = (randomentity1 = (RandomEntity)randomEntity).getEntity()) instanceof EntityWolf) {
            EntityWolf entitywolf = (EntityWolf)entity1;
            if (!entitywolf.isTamed()) {
                return false;
            }
            EnumDyeColor enumdyecolor = entitywolf.getCollarColor();
            if (!Config.equalsOne(enumdyecolor, this.collarColors)) {
                return false;
            }
        }
        if (this.baby != null && randomEntity instanceof RandomEntity && (entity2 = (randomentity2 = (RandomEntity)randomEntity).getEntity()) instanceof EntityLiving && (entityliving = (EntityLiving)entity2).isChild() != this.baby.booleanValue()) {
            return false;
        }
        if (this.moonPhases != null && (world = Config.getMinecraft().theWorld) != null && !this.moonPhases.isInRange(j1 = world.getMoonPhase())) {
            return false;
        }
        if (this.dayTimes != null && (world1 = Config.getMinecraft().theWorld) != null && !this.dayTimes.isInRange(k1 = (int)world1.getWorldInfo().getWorldTime())) {
            return false;
        }
        return this.weatherList == null || (world2 = Config.getMinecraft().theWorld) == null || ArrayUtils.contains((Object[])this.weatherList, (Object)(weather = Weather.getWeather(world2, 0.0f)));
    }

    public ResourceLocation getTextureLocation(ResourceLocation loc, int randomId) {
        if (this.resourceLocations != null && this.resourceLocations.length != 0) {
            int i2 = 0;
            if (this.weights == null) {
                i2 = randomId % this.resourceLocations.length;
            } else {
                int j2 = randomId % this.sumAllWeights;
                int k2 = 0;
                while (k2 < this.sumWeights.length) {
                    if (this.sumWeights[k2] > j2) {
                        i2 = k2;
                        break;
                    }
                    ++k2;
                }
            }
            return this.resourceLocations[i2];
        }
        return loc;
    }
}

