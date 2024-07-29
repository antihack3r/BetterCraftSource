/*
 * Decompiled with CFR 0.152.
 */
package com.TominoCZ.FBP;

import com.TominoCZ.FBP.handler.FBPEventHandler;
import com.TominoCZ.FBP.particle.FBPParticleManager;
import com.TominoCZ.FBP.util.FBPReflectionHelper;
import com.google.common.base.Throwables;
import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.StringUtils;

public class FBP {
    private static final FBP INSTANCE = new FBP();
    public static final String MODID = "fbp";
    public static final ResourceLocation LOCATION_PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");
    public static final ResourceLocation FBP_BUG = new ResourceLocation("fbp:textures/gui/bug.png");
    public static final ResourceLocation FBP_FBP = new ResourceLocation("fbp:textures/gui/fbp.png");
    public static final ResourceLocation FBP_WIDGETS = new ResourceLocation("fbp:textures/gui/widgets.png");
    public static final Vec3[] CUBE = new Vec3[]{new Vec3(1.0, 1.0, -1.0), new Vec3(1.0, 1.0, 1.0), new Vec3(-1.0, 1.0, 1.0), new Vec3(-1.0, 1.0, -1.0), new Vec3(-1.0, -1.0, -1.0), new Vec3(-1.0, -1.0, 1.0), new Vec3(1.0, -1.0, 1.0), new Vec3(1.0, -1.0, -1.0), new Vec3(-1.0, -1.0, 1.0), new Vec3(-1.0, 1.0, 1.0), new Vec3(1.0, 1.0, 1.0), new Vec3(1.0, -1.0, 1.0), new Vec3(1.0, -1.0, -1.0), new Vec3(1.0, 1.0, -1.0), new Vec3(-1.0, 1.0, -1.0), new Vec3(-1.0, -1.0, -1.0), new Vec3(-1.0, -1.0, -1.0), new Vec3(-1.0, 1.0, -1.0), new Vec3(-1.0, 1.0, 1.0), new Vec3(-1.0, -1.0, 1.0), new Vec3(1.0, -1.0, 1.0), new Vec3(1.0, 1.0, 1.0), new Vec3(1.0, 1.0, -1.0), new Vec3(1.0, -1.0, -1.0)};
    public static final Vec3[] CUBE_NORMALS = new Vec3[]{new Vec3(0.0, 1.0, 0.0), new Vec3(0.0, -1.0, 0.0), new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, 0.0, -1.0), new Vec3(-1.0, 0.0, 0.0), new Vec3(1.0, 0.0, 0.0)};
    public static File particleBlacklistFile = null;
    public static File floatingMaterialsFile = null;
    public static File config = null;
    public static int minAge;
    public static int maxAge;
    public static int particlesPerAxis;
    public static double scaleMult;
    public static double gravityMult;
    public static double rotationMult;
    public static double weatherParticleDensity;
    public static boolean enabled;
    public static boolean showInMillis;
    public static boolean infiniteDuration;
    public static boolean randomRotation;
    public static boolean cartoonMode;
    public static boolean spawnWhileFrozen;
    public static boolean spawnRedstoneBlockParticles;
    public static boolean randomizedScale;
    public static boolean randomFadingSpeed;
    public static boolean entityCollision;
    public static boolean bounceOffWalls;
    public static boolean lowTraction;
    public static boolean smartBreaking;
    public static boolean fancyRain;
    public static boolean fancySnow;
    public static boolean fancyFlame;
    public static boolean fancySmoke;
    public static boolean waterPhysics;
    public static boolean restOnFloor;
    public static boolean frozen;
    public static SplittableRandom random;
    public static VertexFormat POSITION_TEX_COLOR_LMAP_NORMAL;
    public static MethodHandle setSourcePos;
    public static FBPParticleManager fancyEffectRenderer;
    public static EffectRenderer originalEffectRenderer;
    public List<String> blockParticleBlacklist;
    public List<Material> floatingMaterials;
    public FBPEventHandler eventHandler = new FBPEventHandler();

    static {
        enabled = true;
        showInMillis = false;
        infiniteDuration = false;
        random = new SplittableRandom();
    }

    public static final FBP getInstance() {
        return INSTANCE;
    }

    public FBP() {
        POSITION_TEX_COLOR_LMAP_NORMAL = new VertexFormat();
        POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.POSITION_3F);
        POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.TEX_2F);
        POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.COLOR_4UB);
        POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.TEX_2S);
        POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.NORMAL_3B);
        this.blockParticleBlacklist = Collections.synchronizedList(new ArrayList());
        this.floatingMaterials = Collections.synchronizedList(new ArrayList());
    }

    public void init() {
        MethodHandles.Lookup lookup = MethodHandles.publicLookup();
        try {
            setSourcePos = lookup.unreflectSetter(FBPReflectionHelper.findField(EntityDiggingFX.class, "field_181019_az", "sourcePos"));
        }
        catch (Exception e2) {
            throw Throwables.propagate(e2);
        }
        this.syncWithModule();
    }

    public static boolean isEnabled() {
        if (!enabled) {
            frozen = false;
        }
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        if (FBP.enabled != enabled) {
            if (enabled) {
                fancyEffectRenderer.carryOver();
                Minecraft.getMinecraft().effectRenderer = fancyEffectRenderer;
            } else {
                Minecraft.getMinecraft().effectRenderer = originalEffectRenderer;
            }
        }
        FBP.enabled = enabled;
    }

    public boolean isBlacklisted(Block b2) {
        return b2 == null || this.blockParticleBlacklist.contains(b2.getLocalizedName().toString());
    }

    public boolean doesMaterialFloat(Material mat) {
        return this.floatingMaterials.contains(mat);
    }

    public void addToBlacklist(String name) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        Block b2 = Block.getBlockFromName(name);
        if (b2 == null || b2 == Blocks.redstone_block) {
            return;
        }
        this.addToBlacklist(b2);
    }

    public void addToBlacklist(Block b2) {
        if (b2 == null) {
            return;
        }
        String name = b2.getLocalizedName().toString();
        if (!this.blockParticleBlacklist.contains(name)) {
            this.blockParticleBlacklist.add(name);
        }
    }

    public void removeFromBlacklist(Block b2) {
        if (b2 == null) {
            return;
        }
        String name = b2.getLocalizedName().toString();
        if (this.blockParticleBlacklist.contains(name)) {
            this.blockParticleBlacklist.remove(name);
        }
    }

    public void syncWithModule() {
        minAge = 10;
        maxAge = 50;
        infiniteDuration = false;
        particlesPerAxis = 4;
        scaleMult = 0.7;
        gravityMult = 1.0;
        rotationMult = 1.0;
        randomRotation = true;
        cartoonMode = false;
        randomizedScale = true;
        randomFadingSpeed = true;
        spawnRedstoneBlockParticles = true;
        entityCollision = false;
        bounceOffWalls = true;
        lowTraction = false;
        smartBreaking = true;
        fancyFlame = true;
        fancySmoke = true;
        waterPhysics = true;
        restOnFloor = true;
    }

    public void resetBlacklist() {
        this.blockParticleBlacklist.clear();
    }
}

