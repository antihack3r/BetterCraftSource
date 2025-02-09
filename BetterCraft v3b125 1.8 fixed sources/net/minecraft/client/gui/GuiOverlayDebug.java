/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.optifine.SmartAnimations;
import net.optifine.TextureAnimations;
import net.optifine.reflect.Reflector;
import net.optifine.util.MemoryMonitor;
import net.optifine.util.NativeMemory;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GuiOverlayDebug
extends Gui {
    private final Minecraft mc;
    private final FontRenderer fontRenderer;
    private String debugOF = null;
    private List<String> debugInfoLeft = null;
    private List<String> debugInfoRight = null;
    private long updateInfoLeftTimeMs = 0L;
    private long updateInfoRightTimeMs = 0L;

    public GuiOverlayDebug(Minecraft mc2) {
        this.mc = mc2;
        this.fontRenderer = mc2.fontRendererObj;
    }

    public void renderDebugInfo(ScaledResolution scaledResolutionIn) {
        this.mc.mcProfiler.startSection("debug");
        GlStateManager.pushMatrix();
        this.renderDebugInfoLeft();
        this.renderDebugInfoRight(scaledResolutionIn);
        GlStateManager.popMatrix();
        if (this.mc.gameSettings.showLagometer) {
            this.renderLagometer();
        }
        this.mc.mcProfiler.endSection();
    }

    private boolean isReducedDebug() {
        return this.mc.thePlayer.hasReducedDebug() || this.mc.gameSettings.reducedDebugInfo;
    }

    protected void renderDebugInfoLeft() {
        List<String> list = this.debugInfoLeft;
        if (list == null || System.currentTimeMillis() > this.updateInfoLeftTimeMs) {
            this.debugInfoLeft = list = this.call();
            this.updateInfoLeftTimeMs = System.currentTimeMillis() + 100L;
        }
        int i2 = 0;
        while (i2 < list.size()) {
            String s2 = list.get(i2);
            if (!Strings.isNullOrEmpty(s2)) {
                int j2 = this.fontRenderer.FONT_HEIGHT;
                int k2 = this.fontRenderer.getStringWidth(s2);
                int l2 = 2;
                int i1 = 2 + j2 * i2;
                GuiOverlayDebug.drawRect(1, i1 - 1, 2 + k2 + 1, i1 + j2 - 1, -1873784752);
                this.fontRenderer.drawString(s2, 2, i1, 0xE0E0E0);
            }
            ++i2;
        }
    }

    protected void renderDebugInfoRight(ScaledResolution scaledRes) {
        List<String> list = this.debugInfoRight;
        if (list == null || System.currentTimeMillis() > this.updateInfoRightTimeMs) {
            this.debugInfoRight = list = this.getDebugInfoRight();
            this.updateInfoRightTimeMs = System.currentTimeMillis() + 100L;
        }
        int i2 = 0;
        while (i2 < list.size()) {
            String s2 = list.get(i2);
            if (!Strings.isNullOrEmpty(s2)) {
                int j2 = this.fontRenderer.FONT_HEIGHT;
                int k2 = this.fontRenderer.getStringWidth(s2);
                int l2 = scaledRes.getScaledWidth() - 2 - k2;
                int i1 = 2 + j2 * i2;
                GuiOverlayDebug.drawRect(l2 - 1, i1 - 1, l2 + k2 + 1, i1 + j2 - 1, -1873784752);
                this.fontRenderer.drawString(s2, l2, i1, 0xE0E0E0);
            }
            ++i2;
        }
    }

    protected List<String> call() {
        BlockPos blockpos = new BlockPos(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);
        if (this.mc.debug != this.debugOF) {
            StringBuffer stringbuffer = new StringBuffer(this.mc.debug);
            int i2 = Config.getFpsMin();
            int j2 = this.mc.debug.indexOf(" fps ");
            if (j2 >= 0) {
                stringbuffer.insert(j2, "/" + i2);
            }
            if (Config.isSmoothFps()) {
                stringbuffer.append(" sf");
            }
            if (Config.isFastRender()) {
                stringbuffer.append(" fr");
            }
            if (Config.isAnisotropicFiltering()) {
                stringbuffer.append(" af");
            }
            if (Config.isAntialiasing()) {
                stringbuffer.append(" aa");
            }
            if (Config.isRenderRegions()) {
                stringbuffer.append(" reg");
            }
            if (Config.isShaders()) {
                stringbuffer.append(" sh");
            }
            this.debugOF = this.mc.debug = stringbuffer.toString();
        }
        StringBuilder stringbuilder = new StringBuilder();
        TextureMap texturemap = Config.getTextureMap();
        stringbuilder.append(", A: ");
        if (SmartAnimations.isActive()) {
            stringbuilder.append(texturemap.getCountAnimationsActive() + TextureAnimations.getCountAnimationsActive());
            stringbuilder.append("/");
        }
        stringbuilder.append(texturemap.getCountAnimations() + TextureAnimations.getCountAnimations());
        String s1 = stringbuilder.toString();
        if (this.isReducedDebug()) {
            return Lists.newArrayList("Minecraft 1.8.9 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities() + s1, this.mc.theWorld.getProviderName(), "", String.format("Chunk-relative: %d %d %d", blockpos.getX() & 0xF, blockpos.getY() & 0xF, blockpos.getZ() & 0xF));
        }
        Entity entity = this.mc.getRenderViewEntity();
        EnumFacing enumfacing = entity.getHorizontalFacing();
        String s2 = "Invalid";
        switch (enumfacing) {
            case NORTH: {
                s2 = "Towards negative Z";
                break;
            }
            case SOUTH: {
                s2 = "Towards positive Z";
                break;
            }
            case WEST: {
                s2 = "Towards negative X";
                break;
            }
            case EAST: {
                s2 = "Towards positive X";
            }
        }
        ArrayList<String> list = Lists.newArrayList("Minecraft 1.8.9 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities() + s1, this.mc.theWorld.getProviderName(), "", String.format("XYZ: %.3f / %.5f / %.3f", this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ), String.format("Block: %d %d %d", blockpos.getX(), blockpos.getY(), blockpos.getZ()), String.format("Chunk: %d %d %d in %d %d %d", blockpos.getX() & 0xF, blockpos.getY() & 0xF, blockpos.getZ() & 0xF, blockpos.getX() >> 4, blockpos.getY() >> 4, blockpos.getZ() >> 4), String.format("Facing: %s (%s) (%.1f / %.1f)", enumfacing, s2, Float.valueOf(MathHelper.wrapAngleTo180_float(entity.rotationYaw)), Float.valueOf(MathHelper.wrapAngleTo180_float(entity.rotationPitch))));
        if (this.mc.theWorld != null && this.mc.theWorld.isBlockLoaded(blockpos)) {
            DifficultyInstance difficultyinstance1;
            EntityPlayerMP entityplayermp;
            Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(blockpos);
            list.add("Biome: " + chunk.getBiome((BlockPos)blockpos, (WorldChunkManager)this.mc.theWorld.getWorldChunkManager()).biomeName);
            list.add("Light: " + chunk.getLightSubtracted(blockpos, 0) + " (" + chunk.getLightFor(EnumSkyBlock.SKY, blockpos) + " sky, " + chunk.getLightFor(EnumSkyBlock.BLOCK, blockpos) + " block)");
            DifficultyInstance difficultyinstance = this.mc.theWorld.getDifficultyForLocation(blockpos);
            if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null && (entityplayermp = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.thePlayer.getUniqueID())) != null && (difficultyinstance1 = this.mc.getIntegratedServer().getDifficultyAsync(entityplayermp.worldObj, new BlockPos(entityplayermp))) != null) {
                difficultyinstance = difficultyinstance1;
            }
            list.add(String.format("Local Difficulty: %.2f (Day %d)", Float.valueOf(difficultyinstance.getAdditionalDifficulty()), this.mc.theWorld.getWorldTime() / 24000L));
        }
        if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive()) {
            list.add("Shader: " + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());
        }
        if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null) {
            BlockPos blockpos1 = this.mc.objectMouseOver.getBlockPos();
            list.add(String.format("Looking at: %d %d %d", blockpos1.getX(), blockpos1.getY(), blockpos1.getZ()));
        }
        return list;
    }

    protected List<String> getDebugInfoRight() {
        long i2 = Runtime.getRuntime().maxMemory();
        long j2 = Runtime.getRuntime().totalMemory();
        long k2 = Runtime.getRuntime().freeMemory();
        long l2 = j2 - k2;
        ArrayList<String> list = Lists.newArrayList(String.format("Java: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", l2 * 100L / i2, GuiOverlayDebug.bytesToMb(l2), GuiOverlayDebug.bytesToMb(i2)), String.format("Allocated: % 2d%% %03dMB", j2 * 100L / i2, GuiOverlayDebug.bytesToMb(j2)), "", String.format("CPU: %s", OpenGlHelper.getCpu()), "", String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(7936)), GL11.glGetString(7937), GL11.glGetString(7938));
        long i1 = NativeMemory.getBufferAllocated();
        long j1 = NativeMemory.getBufferMaximum();
        String s2 = "Native: " + GuiOverlayDebug.bytesToMb(i1) + "/" + GuiOverlayDebug.bytesToMb(j1) + "MB";
        list.add(4, s2);
        list.set(5, "GC: " + MemoryMonitor.getAllocationRateMb() + "MB/s");
        if (Reflector.FMLCommonHandler_getBrandings.exists()) {
            Object object = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);
            list.add("");
            list.addAll((Collection)Reflector.call(object, Reflector.FMLCommonHandler_getBrandings, false));
        }
        if (this.isReducedDebug()) {
            return list;
        }
        if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null) {
            BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
            IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);
            if (this.mc.theWorld.getWorldType() != WorldType.DEBUG_WORLD) {
                iblockstate = iblockstate.getBlock().getActualState(iblockstate, this.mc.theWorld, blockpos);
            }
            list.add("");
            list.add(String.valueOf(Block.blockRegistry.getNameForObject(iblockstate.getBlock())));
            for (Map.Entry entry : iblockstate.getProperties().entrySet()) {
                String s1 = ((Comparable)entry.getValue()).toString();
                if (entry.getValue() == Boolean.TRUE) {
                    s1 = (Object)((Object)EnumChatFormatting.GREEN) + s1;
                } else if (entry.getValue() == Boolean.FALSE) {
                    s1 = (Object)((Object)EnumChatFormatting.RED) + s1;
                }
                list.add(String.valueOf(((IProperty)entry.getKey()).getName()) + ": " + s1);
            }
        }
        return list;
    }

    private void renderLagometer() {
    }

    private int getFrameColor(int p_181552_1_, int p_181552_2_, int p_181552_3_, int p_181552_4_) {
        return p_181552_1_ < p_181552_3_ ? this.blendColors(-16711936, -256, (float)p_181552_1_ / (float)p_181552_3_) : this.blendColors(-256, -65536, (float)(p_181552_1_ - p_181552_3_) / (float)(p_181552_4_ - p_181552_3_));
    }

    private int blendColors(int p_181553_1_, int p_181553_2_, float p_181553_3_) {
        int i2 = p_181553_1_ >> 24 & 0xFF;
        int j2 = p_181553_1_ >> 16 & 0xFF;
        int k2 = p_181553_1_ >> 8 & 0xFF;
        int l2 = p_181553_1_ & 0xFF;
        int i1 = p_181553_2_ >> 24 & 0xFF;
        int j1 = p_181553_2_ >> 16 & 0xFF;
        int k1 = p_181553_2_ >> 8 & 0xFF;
        int l1 = p_181553_2_ & 0xFF;
        int i22 = MathHelper.clamp_int((int)((float)i2 + (float)(i1 - i2) * p_181553_3_), 0, 255);
        int j22 = MathHelper.clamp_int((int)((float)j2 + (float)(j1 - j2) * p_181553_3_), 0, 255);
        int k22 = MathHelper.clamp_int((int)((float)k2 + (float)(k1 - k2) * p_181553_3_), 0, 255);
        int l22 = MathHelper.clamp_int((int)((float)l2 + (float)(l1 - l2) * p_181553_3_), 0, 255);
        return i22 << 24 | j22 << 16 | k22 << 8 | l22;
    }

    private static long bytesToMb(long bytes) {
        return bytes / 1024L / 1024L;
    }
}

