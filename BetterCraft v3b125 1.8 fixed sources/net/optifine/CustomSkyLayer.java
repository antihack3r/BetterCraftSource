/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.Matches;
import net.optifine.config.RangeListInt;
import net.optifine.render.Blender;
import net.optifine.util.NumUtils;
import net.optifine.util.SmoothFloat;
import net.optifine.util.TextureUtils;

public class CustomSkyLayer {
    public String source = null;
    private int startFadeIn = -1;
    private int endFadeIn = -1;
    private int startFadeOut = -1;
    private int endFadeOut = -1;
    private int blend = 1;
    private boolean rotate = false;
    private float speed = 1.0f;
    private float[] axis = DEFAULT_AXIS;
    private RangeListInt days = null;
    private int daysLoop = 8;
    private boolean weatherClear = true;
    private boolean weatherRain = false;
    private boolean weatherThunder = false;
    public BiomeGenBase[] biomes = null;
    public RangeListInt heights = null;
    private float transition = 1.0f;
    private SmoothFloat smoothPositionBrightness = null;
    public int textureId = -1;
    private World lastWorld = null;
    public static final float[] DEFAULT_AXIS = new float[]{1.0f, 0.0f, 0.0f};
    private static final String WEATHER_CLEAR = "clear";
    private static final String WEATHER_RAIN = "rain";
    private static final String WEATHER_THUNDER = "thunder";

    public CustomSkyLayer(Properties props, String defSource) {
        ConnectedParser connectedparser = new ConnectedParser("CustomSky");
        this.source = props.getProperty("source", defSource);
        this.startFadeIn = this.parseTime(props.getProperty("startFadeIn"));
        this.endFadeIn = this.parseTime(props.getProperty("endFadeIn"));
        this.startFadeOut = this.parseTime(props.getProperty("startFadeOut"));
        this.endFadeOut = this.parseTime(props.getProperty("endFadeOut"));
        this.blend = Blender.parseBlend(props.getProperty("blend"));
        this.rotate = this.parseBoolean(props.getProperty("rotate"), true);
        this.speed = this.parseFloat(props.getProperty("speed"), 1.0f);
        this.axis = this.parseAxis(props.getProperty("axis"), DEFAULT_AXIS);
        this.days = connectedparser.parseRangeListInt(props.getProperty("days"));
        this.daysLoop = connectedparser.parseInt(props.getProperty("daysLoop"), 8);
        List<String> list = this.parseWeatherList(props.getProperty("weather", WEATHER_CLEAR));
        this.weatherClear = list.contains(WEATHER_CLEAR);
        this.weatherRain = list.contains(WEATHER_RAIN);
        this.weatherThunder = list.contains(WEATHER_THUNDER);
        this.biomes = connectedparser.parseBiomes(props.getProperty("biomes"));
        this.heights = connectedparser.parseRangeListInt(props.getProperty("heights"));
        this.transition = this.parseFloat(props.getProperty("transition"), 1.0f);
    }

    private List<String> parseWeatherList(String str) {
        List<String> list = Arrays.asList(WEATHER_CLEAR, WEATHER_RAIN, WEATHER_THUNDER);
        ArrayList<String> list1 = new ArrayList<String>();
        String[] astring = Config.tokenize(str, " ");
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            if (!list.contains(s2)) {
                Config.warn("Unknown weather: " + s2);
            } else {
                list1.add(s2);
            }
            ++i2;
        }
        return list1;
    }

    private int parseTime(String str) {
        if (str == null) {
            return -1;
        }
        String[] astring = Config.tokenize(str, ":");
        if (astring.length != 2) {
            Config.warn("Invalid time: " + str);
            return -1;
        }
        String s2 = astring[0];
        String s1 = astring[1];
        int i2 = Config.parseInt(s2, -1);
        int j2 = Config.parseInt(s1, -1);
        if (i2 >= 0 && i2 <= 23 && j2 >= 0 && j2 <= 59) {
            if ((i2 -= 6) < 0) {
                i2 += 24;
            }
            int k2 = i2 * 1000 + (int)((double)j2 / 60.0 * 1000.0);
            return k2;
        }
        Config.warn("Invalid time: " + str);
        return -1;
    }

    private boolean parseBoolean(String str, boolean defVal) {
        if (str == null) {
            return defVal;
        }
        if (str.toLowerCase().equals("true")) {
            return true;
        }
        if (str.toLowerCase().equals("false")) {
            return false;
        }
        Config.warn("Unknown boolean: " + str);
        return defVal;
    }

    private float parseFloat(String str, float defVal) {
        if (str == null) {
            return defVal;
        }
        float f2 = Config.parseFloat(str, Float.MIN_VALUE);
        if (f2 == Float.MIN_VALUE) {
            Config.warn("Invalid value: " + str);
            return defVal;
        }
        return f2;
    }

    private float[] parseAxis(String str, float[] defVal) {
        if (str == null) {
            return defVal;
        }
        String[] astring = Config.tokenize(str, " ");
        if (astring.length != 3) {
            Config.warn("Invalid axis: " + str);
            return defVal;
        }
        float[] afloat = new float[3];
        int i2 = 0;
        while (i2 < astring.length) {
            afloat[i2] = Config.parseFloat(astring[i2], Float.MIN_VALUE);
            if (afloat[i2] == Float.MIN_VALUE) {
                Config.warn("Invalid axis: " + str);
                return defVal;
            }
            if (afloat[i2] < -1.0f || afloat[i2] > 1.0f) {
                Config.warn("Invalid axis values: " + str);
                return defVal;
            }
            ++i2;
        }
        float f2 = afloat[0];
        float f3 = afloat[1];
        float f1 = afloat[2];
        if (f2 * f2 + f3 * f3 + f1 * f1 < 1.0E-5f) {
            Config.warn("Invalid axis values: " + str);
            return defVal;
        }
        float[] afloat1 = new float[]{f1, f3, -f2};
        return afloat1;
    }

    public boolean isValid(String path) {
        if (this.source == null) {
            Config.warn("No source texture: " + path);
            return false;
        }
        this.source = TextureUtils.fixResourcePath(this.source, TextureUtils.getBasePath(path));
        if (this.startFadeIn >= 0 && this.endFadeIn >= 0 && this.endFadeOut >= 0) {
            int l2;
            int k2;
            int j2;
            int i1;
            int i2 = this.normalizeTime(this.endFadeIn - this.startFadeIn);
            if (this.startFadeOut < 0) {
                this.startFadeOut = this.normalizeTime(this.endFadeOut - i2);
                if (this.timeBetween(this.startFadeOut, this.startFadeIn, this.endFadeIn)) {
                    this.startFadeOut = this.endFadeIn;
                }
            }
            if ((i1 = i2 + (j2 = this.normalizeTime(this.startFadeOut - this.endFadeIn)) + (k2 = this.normalizeTime(this.endFadeOut - this.startFadeOut)) + (l2 = this.normalizeTime(this.startFadeIn - this.endFadeOut))) != 24000) {
                Config.warn("Invalid fadeIn/fadeOut times, sum is not 24h: " + i1);
                return false;
            }
            if (this.speed < 0.0f) {
                Config.warn("Invalid speed: " + this.speed);
                return false;
            }
            if (this.daysLoop <= 0) {
                Config.warn("Invalid daysLoop: " + this.daysLoop);
                return false;
            }
            return true;
        }
        Config.warn("Invalid times, required are: startFadeIn, endFadeIn and endFadeOut.");
        return false;
    }

    private int normalizeTime(int timeMc) {
        while (timeMc >= 24000) {
            timeMc -= 24000;
        }
        while (timeMc < 0) {
            timeMc += 24000;
        }
        return timeMc;
    }

    public void render(World world, int timeOfDay, float celestialAngle, float rainStrength, float thunderStrength) {
        float f2 = this.getPositionBrightness(world);
        float f1 = this.getWeatherBrightness(rainStrength, thunderStrength);
        float f22 = this.getFadeBrightness(timeOfDay);
        float f3 = f2 * f1 * f22;
        if ((f3 = Config.limit(f3, 0.0f, 1.0f)) >= 1.0E-4f) {
            GlStateManager.bindTexture(this.textureId);
            Blender.setupBlend(this.blend, f3);
            GlStateManager.pushMatrix();
            if (this.rotate) {
                float f4 = 0.0f;
                if (this.speed != (float)Math.round(this.speed)) {
                    long i2 = (world.getWorldTime() + 18000L) / 24000L;
                    double d0 = this.speed % 1.0f;
                    double d1 = (double)i2 * d0;
                    f4 = (float)(d1 % 1.0);
                }
                GlStateManager.rotate(360.0f * (f4 + celestialAngle * this.speed), this.axis[0], this.axis[1], this.axis[2]);
            }
            Tessellator tessellator = Tessellator.getInstance();
            GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(-90.0f, 0.0f, 0.0f, 1.0f);
            this.renderSide(tessellator, 4);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            this.renderSide(tessellator, 1);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
            this.renderSide(tessellator, 0);
            GlStateManager.popMatrix();
            GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            this.renderSide(tessellator, 5);
            GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            this.renderSide(tessellator, 2);
            GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            this.renderSide(tessellator, 3);
            GlStateManager.popMatrix();
        }
    }

    private float getPositionBrightness(World world) {
        if (this.biomes == null && this.heights == null) {
            return 1.0f;
        }
        float f2 = this.getPositionBrightnessRaw(world);
        if (this.smoothPositionBrightness == null) {
            this.smoothPositionBrightness = new SmoothFloat(f2, this.transition);
        }
        f2 = this.smoothPositionBrightness.getSmoothValue(f2);
        return f2;
    }

    private float getPositionBrightnessRaw(World world) {
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        if (entity == null) {
            return 0.0f;
        }
        BlockPos blockpos = entity.getPosition();
        if (this.biomes != null) {
            BiomeGenBase biomegenbase = world.getBiomeGenForCoords(blockpos);
            if (biomegenbase == null) {
                return 0.0f;
            }
            if (!Matches.biome(biomegenbase, this.biomes)) {
                return 0.0f;
            }
        }
        return this.heights != null && !this.heights.isInRange(blockpos.getY()) ? 0.0f : 1.0f;
    }

    private float getWeatherBrightness(float rainStrength, float thunderStrength) {
        float f2 = 1.0f - rainStrength;
        float f1 = rainStrength - thunderStrength;
        float f22 = 0.0f;
        if (this.weatherClear) {
            f22 += f2;
        }
        if (this.weatherRain) {
            f22 += f1;
        }
        if (this.weatherThunder) {
            f22 += thunderStrength;
        }
        f22 = NumUtils.limit(f22, 0.0f, 1.0f);
        return f22;
    }

    private float getFadeBrightness(int timeOfDay) {
        if (this.timeBetween(timeOfDay, this.startFadeIn, this.endFadeIn)) {
            int k2 = this.normalizeTime(this.endFadeIn - this.startFadeIn);
            int l2 = this.normalizeTime(timeOfDay - this.startFadeIn);
            return (float)l2 / (float)k2;
        }
        if (this.timeBetween(timeOfDay, this.endFadeIn, this.startFadeOut)) {
            return 1.0f;
        }
        if (this.timeBetween(timeOfDay, this.startFadeOut, this.endFadeOut)) {
            int i2 = this.normalizeTime(this.endFadeOut - this.startFadeOut);
            int j2 = this.normalizeTime(timeOfDay - this.startFadeOut);
            return 1.0f - (float)j2 / (float)i2;
        }
        return 0.0f;
    }

    private void renderSide(Tessellator tess, int side) {
        WorldRenderer worldrenderer = tess.getWorldRenderer();
        double d0 = (double)(side % 3) / 3.0;
        double d1 = (double)(side / 3) / 2.0;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-100.0, -100.0, -100.0).tex(d0, d1).endVertex();
        worldrenderer.pos(-100.0, -100.0, 100.0).tex(d0, d1 + 0.5).endVertex();
        worldrenderer.pos(100.0, -100.0, 100.0).tex(d0 + 0.3333333333333333, d1 + 0.5).endVertex();
        worldrenderer.pos(100.0, -100.0, -100.0).tex(d0 + 0.3333333333333333, d1).endVertex();
        tess.draw();
    }

    public boolean isActive(World world, int timeOfDay) {
        if (world != this.lastWorld) {
            this.lastWorld = world;
            this.smoothPositionBrightness = null;
        }
        if (this.timeBetween(timeOfDay, this.endFadeOut, this.startFadeIn)) {
            return false;
        }
        if (this.days != null) {
            long i2 = world.getWorldTime();
            long j2 = i2 - (long)this.startFadeIn;
            while (j2 < 0L) {
                j2 += (long)(24000 * this.daysLoop);
            }
            int k2 = (int)(j2 / 24000L);
            int l2 = k2 % this.daysLoop;
            if (!this.days.isInRange(l2)) {
                return false;
            }
        }
        return true;
    }

    private boolean timeBetween(int timeOfDay, int timeStart, int timeEnd) {
        return timeStart <= timeEnd ? timeOfDay >= timeStart && timeOfDay <= timeEnd : timeOfDay >= timeStart || timeOfDay <= timeEnd;
    }

    public String toString() {
        return this.source + ", " + this.startFadeIn + "-" + this.endFadeIn + " " + this.startFadeOut + "-" + this.endFadeOut;
    }
}

