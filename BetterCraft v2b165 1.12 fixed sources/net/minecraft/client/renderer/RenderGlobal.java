// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.item.ItemDye;
import net.minecraft.init.Items;
import net.minecraft.block.SoundType;
import net.minecraft.item.Item;
import net.minecraft.init.SoundEvents;
import optifine.RandomMobs;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.SoundCategory;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.SoundEvent;
import me.amkgre.bettercraft.client.gui.GuiClientUI;
import java.awt.Color;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockChest;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.WorldProvider;
import optifine.CustomSky;
import net.minecraft.world.DimensionType;
import optifine.CustomColors;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import shadersmod.client.ShadersRender;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.chunk.VisGraph;
import org.lwjgl.util.vector.Vector3f;
import optifine.ChunkUtils;
import shadersmod.client.ShadowUtils;
import optifine.RenderInfoLazy;
import optifine.Lagometer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ClassInheritanceMultiMap;
import java.util.Iterator;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.EntityLivingBase;
import shadersmod.client.Shaders;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import optifine.Reflector;
import net.minecraft.client.renderer.culling.ICamera;
import optifine.DynamicLights;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import java.util.Random;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import optifine.Config;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.world.IBlockAccess;
import net.minecraft.init.Blocks;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.LogManager;
import optifine.RenderEnv;
import net.minecraft.world.chunk.Chunk;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.chunk.IChunkProvider;
import java.util.Deque;
import net.minecraft.entity.Entity;
import optifine.CloudRenderer;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import org.lwjgl.util.vector.Vector4f;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.tileentity.TileEntity;
import java.util.List;
import net.minecraft.client.renderer.chunk.RenderChunk;
import java.util.Set;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.world.IWorldEventListener;

public class RenderGlobal implements IWorldEventListener, IResourceManagerReloadListener
{
    private static final Logger LOGGER;
    private static final ResourceLocation MOON_PHASES_TEXTURES;
    private static final ResourceLocation SUN_TEXTURES;
    private static final ResourceLocation CLOUDS_TEXTURES;
    private static final ResourceLocation END_SKY_TEXTURES;
    private static final ResourceLocation FORCEFIELD_TEXTURES;
    public final Minecraft mc;
    private final TextureManager renderEngine;
    private final RenderManager renderManager;
    private WorldClient theWorld;
    private Set<RenderChunk> chunksToUpdate;
    private List<ContainerLocalRenderInformation> renderInfos;
    private final Set<TileEntity> setTileEntities;
    private ViewFrustum viewFrustum;
    private int starGLCallList;
    private int glSkyList;
    private int glSkyList2;
    private final VertexFormat vertexBufferFormat;
    private VertexBuffer starVBO;
    private VertexBuffer skyVBO;
    private VertexBuffer sky2VBO;
    private int cloudTickCounter;
    public final Map<Integer, DestroyBlockProgress> damagedBlocks;
    private final Map<BlockPos, ISound> mapSoundPositions;
    private final TextureAtlasSprite[] destroyBlockIcons;
    private Framebuffer entityOutlineFramebuffer;
    private ShaderGroup entityOutlineShader;
    private double frustumUpdatePosX;
    private double frustumUpdatePosY;
    private double frustumUpdatePosZ;
    private int frustumUpdatePosChunkX;
    private int frustumUpdatePosChunkY;
    private int frustumUpdatePosChunkZ;
    private double lastViewEntityX;
    private double lastViewEntityY;
    private double lastViewEntityZ;
    private double lastViewEntityPitch;
    private double lastViewEntityYaw;
    private ChunkRenderDispatcher renderDispatcher;
    private ChunkRenderContainer renderContainer;
    private int renderDistanceChunks;
    private int renderEntitiesStartupCounter;
    private int countEntitiesTotal;
    private int countEntitiesRendered;
    private int countEntitiesHidden;
    private boolean debugFixTerrainFrustum;
    private ClippingHelper debugFixedClippingHelper;
    private final Vector4f[] debugTerrainMatrix;
    private final Vector3d debugTerrainFrustumPosition;
    private boolean vboEnabled;
    IRenderChunkFactory renderChunkFactory;
    private double prevRenderSortX;
    private double prevRenderSortY;
    private double prevRenderSortZ;
    public boolean displayListEntitiesDirty;
    private boolean entityOutlinesRendered;
    private final Set<BlockPos> setLightUpdates;
    private CloudRenderer cloudRenderer;
    public Entity renderedEntity;
    public Set chunksToResortTransparency;
    public Set chunksToUpdateForced;
    private Deque visibilityDeque;
    private List renderInfosEntities;
    private List renderInfosTileEntities;
    private List renderInfosNormal;
    private List renderInfosEntitiesNormal;
    private List renderInfosTileEntitiesNormal;
    private List renderInfosShadow;
    private List renderInfosEntitiesShadow;
    private List renderInfosTileEntitiesShadow;
    private int renderDistance;
    private int renderDistanceSq;
    private static final Set SET_ALL_FACINGS;
    private int countTileEntitiesRendered;
    private IChunkProvider worldChunkProvider;
    private Long2ObjectMap<Chunk> worldChunkProviderMap;
    private int countLoadedChunksPrev;
    private RenderEnv renderEnv;
    public boolean renderOverlayDamaged;
    public boolean renderOverlayEyes;
    static Deque<ContainerLocalRenderInformation> renderInfoCache;
    
    static {
        LOGGER = LogManager.getLogger();
        MOON_PHASES_TEXTURES = new ResourceLocation("textures/environment/moon_phases.png");
        SUN_TEXTURES = new ResourceLocation("textures/environment/sun.png");
        CLOUDS_TEXTURES = new ResourceLocation("textures/environment/clouds.png");
        END_SKY_TEXTURES = new ResourceLocation("textures/environment/end_sky.png");
        FORCEFIELD_TEXTURES = new ResourceLocation("textures/misc/forcefield.png");
        SET_ALL_FACINGS = Collections.unmodifiableSet((Set<?>)new HashSet<Object>(Arrays.asList(EnumFacing.VALUES)));
        RenderGlobal.renderInfoCache = new ArrayDeque<ContainerLocalRenderInformation>();
    }
    
    public RenderGlobal(final Minecraft mcIn) {
        this.chunksToUpdate = (Set<RenderChunk>)Sets.newLinkedHashSet();
        this.renderInfos = (List<ContainerLocalRenderInformation>)Lists.newArrayListWithCapacity(69696);
        this.setTileEntities = (Set<TileEntity>)Sets.newHashSet();
        this.starGLCallList = -1;
        this.glSkyList = -1;
        this.glSkyList2 = -1;
        this.damagedBlocks = (Map<Integer, DestroyBlockProgress>)Maps.newHashMap();
        this.mapSoundPositions = (Map<BlockPos, ISound>)Maps.newHashMap();
        this.destroyBlockIcons = new TextureAtlasSprite[10];
        this.frustumUpdatePosX = Double.MIN_VALUE;
        this.frustumUpdatePosY = Double.MIN_VALUE;
        this.frustumUpdatePosZ = Double.MIN_VALUE;
        this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
        this.lastViewEntityX = Double.MIN_VALUE;
        this.lastViewEntityY = Double.MIN_VALUE;
        this.lastViewEntityZ = Double.MIN_VALUE;
        this.lastViewEntityPitch = Double.MIN_VALUE;
        this.lastViewEntityYaw = Double.MIN_VALUE;
        this.renderDistanceChunks = -1;
        this.renderEntitiesStartupCounter = 2;
        this.debugTerrainMatrix = new Vector4f[8];
        this.debugTerrainFrustumPosition = new Vector3d();
        this.displayListEntitiesDirty = true;
        this.setLightUpdates = (Set<BlockPos>)Sets.newHashSet();
        this.chunksToResortTransparency = new LinkedHashSet();
        this.chunksToUpdateForced = new LinkedHashSet();
        this.visibilityDeque = new ArrayDeque();
        this.renderInfosEntities = new ArrayList(1024);
        this.renderInfosTileEntities = new ArrayList(1024);
        this.renderInfosNormal = new ArrayList(1024);
        this.renderInfosEntitiesNormal = new ArrayList(1024);
        this.renderInfosTileEntitiesNormal = new ArrayList(1024);
        this.renderInfosShadow = new ArrayList(1024);
        this.renderInfosEntitiesShadow = new ArrayList(1024);
        this.renderInfosTileEntitiesShadow = new ArrayList(1024);
        this.renderDistance = 0;
        this.renderDistanceSq = 0;
        this.worldChunkProvider = null;
        this.worldChunkProviderMap = null;
        this.countLoadedChunksPrev = 0;
        this.renderEnv = new RenderEnv(this.theWorld, Blocks.AIR.getDefaultState(), new BlockPos(0, 0, 0));
        this.renderOverlayDamaged = false;
        this.renderOverlayEyes = false;
        this.cloudRenderer = new CloudRenderer(mcIn);
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        (this.renderEngine = mcIn.getTextureManager()).bindTexture(RenderGlobal.FORCEFIELD_TEXTURES);
        GlStateManager.glTexParameteri(3553, 10242, 10497);
        GlStateManager.glTexParameteri(3553, 10243, 10497);
        GlStateManager.bindTexture(0);
        this.updateDestroyBlockIcons();
        this.vboEnabled = OpenGlHelper.useVbo();
        if (this.vboEnabled) {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
        }
        else {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
        }
        (this.vertexBufferFormat = new VertexFormat()).addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
        this.generateStars();
        this.generateSky();
        this.generateSky2();
    }
    
    @Override
    public void onResourceManagerReload(final IResourceManager resourceManager) {
        this.updateDestroyBlockIcons();
    }
    
    private void updateDestroyBlockIcons() {
        final TextureMap texturemap = this.mc.getTextureMapBlocks();
        for (int i = 0; i < this.destroyBlockIcons.length; ++i) {
            this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
        }
    }
    
    public void makeEntityOutlineShader() {
        if (OpenGlHelper.shadersSupported) {
            if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
                ShaderLinkHelper.setNewStaticShaderLinkHelper();
            }
            final ResourceLocation resourcelocation = new ResourceLocation("shaders/post/entity_outline.json");
            try {
                (this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocation)).createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
            }
            catch (final IOException ioexception) {
                RenderGlobal.LOGGER.warn("Failed to load shader: {}", resourcelocation, ioexception);
                this.entityOutlineShader = null;
                this.entityOutlineFramebuffer = null;
            }
            catch (final JsonSyntaxException jsonsyntaxexception) {
                RenderGlobal.LOGGER.warn("Failed to load shader: {}", resourcelocation, jsonsyntaxexception);
                this.entityOutlineShader = null;
                this.entityOutlineFramebuffer = null;
            }
        }
        else {
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
        }
    }
    
    public void renderEntityOutlineFramebuffer() {
        if (this.isRenderEntityOutlines()) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
            GlStateManager.disableBlend();
        }
    }
    
    protected boolean isRenderEntityOutlines() {
        return !Config.isFastRender() && !Config.isShaders() && !Config.isAntialiasing() && (this.entityOutlineFramebuffer != null && this.entityOutlineShader != null && this.mc.player != null);
    }
    
    private void generateSky2() {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        if (this.sky2VBO != null) {
            this.sky2VBO.deleteGlBuffers();
        }
        if (this.glSkyList2 >= 0) {
            GLAllocation.deleteDisplayLists(this.glSkyList2);
            this.glSkyList2 = -1;
        }
        if (this.vboEnabled) {
            this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(bufferbuilder, -16.0f, true);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.sky2VBO.bufferData(bufferbuilder.getByteBuffer());
        }
        else {
            GlStateManager.glNewList(this.glSkyList2 = GLAllocation.generateDisplayLists(1), 4864);
            this.renderSky(bufferbuilder, -16.0f, true);
            tessellator.draw();
            GlStateManager.glEndList();
        }
    }
    
    private void generateSky() {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        if (this.skyVBO != null) {
            this.skyVBO.deleteGlBuffers();
        }
        if (this.glSkyList >= 0) {
            GLAllocation.deleteDisplayLists(this.glSkyList);
            this.glSkyList = -1;
        }
        if (this.vboEnabled) {
            this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(bufferbuilder, 16.0f, false);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.skyVBO.bufferData(bufferbuilder.getByteBuffer());
        }
        else {
            GlStateManager.glNewList(this.glSkyList = GLAllocation.generateDisplayLists(1), 4864);
            this.renderSky(bufferbuilder, 16.0f, false);
            tessellator.draw();
            GlStateManager.glEndList();
        }
    }
    
    private void renderSky(final BufferBuilder worldRendererIn, final float posY, final boolean reverseX) {
        final int i = 64;
        final int j = 6;
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION);
        for (int k = -384; k <= 384; k += 64) {
            for (int l = -384; l <= 384; l += 64) {
                float f = (float)k;
                float f2 = (float)(k + 64);
                if (reverseX) {
                    f2 = (float)k;
                    f = (float)(k + 64);
                }
                worldRendererIn.pos(f, posY, l).endVertex();
                worldRendererIn.pos(f2, posY, l).endVertex();
                worldRendererIn.pos(f2, posY, l + 64).endVertex();
                worldRendererIn.pos(f, posY, l + 64).endVertex();
            }
        }
    }
    
    private void generateStars() {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        if (this.starVBO != null) {
            this.starVBO.deleteGlBuffers();
        }
        if (this.starGLCallList >= 0) {
            GLAllocation.deleteDisplayLists(this.starGLCallList);
            this.starGLCallList = -1;
        }
        if (this.vboEnabled) {
            this.starVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderStars(bufferbuilder);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.starVBO.bufferData(bufferbuilder.getByteBuffer());
        }
        else {
            this.starGLCallList = GLAllocation.generateDisplayLists(1);
            GlStateManager.pushMatrix();
            GlStateManager.glNewList(this.starGLCallList, 4864);
            this.renderStars(bufferbuilder);
            tessellator.draw();
            GlStateManager.glEndList();
            GlStateManager.popMatrix();
        }
    }
    
    private void renderStars(final BufferBuilder worldRendererIn) {
        final Random random = new Random(10842L);
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION);
        for (int i = 0; i < 1500; ++i) {
            double d0 = random.nextFloat() * 2.0f - 1.0f;
            double d2 = random.nextFloat() * 2.0f - 1.0f;
            double d3 = random.nextFloat() * 2.0f - 1.0f;
            final double d4 = 0.15f + random.nextFloat() * 0.1f;
            double d5 = d0 * d0 + d2 * d2 + d3 * d3;
            if (d5 < 1.0 && d5 > 0.01) {
                d5 = 1.0 / Math.sqrt(d5);
                d0 *= d5;
                d2 *= d5;
                d3 *= d5;
                final double d6 = d0 * 100.0;
                final double d7 = d2 * 100.0;
                final double d8 = d3 * 100.0;
                final double d9 = Math.atan2(d0, d3);
                final double d10 = Math.sin(d9);
                final double d11 = Math.cos(d9);
                final double d12 = Math.atan2(Math.sqrt(d0 * d0 + d3 * d3), d2);
                final double d13 = Math.sin(d12);
                final double d14 = Math.cos(d12);
                final double d15 = random.nextDouble() * 3.141592653589793 * 2.0;
                final double d16 = Math.sin(d15);
                final double d17 = Math.cos(d15);
                for (int j = 0; j < 4; ++j) {
                    final double d18 = 0.0;
                    final double d19 = ((j & 0x2) - 1) * d4;
                    final double d20 = ((j + 1 & 0x2) - 1) * d4;
                    final double d21 = 0.0;
                    final double d22 = d19 * d17 - d20 * d16;
                    final double d23 = d20 * d17 + d19 * d16;
                    final double d24 = d22 * d13 + 0.0 * d14;
                    final double d25 = 0.0 * d13 - d22 * d14;
                    final double d26 = d25 * d10 - d23 * d11;
                    final double d27 = d23 * d10 + d25 * d11;
                    worldRendererIn.pos(d6 + d26, d7 + d24, d8 + d27).endVertex();
                }
            }
        }
    }
    
    public void setWorldAndLoadRenderers(@Nullable final WorldClient worldClientIn) {
        if (this.theWorld != null) {
            this.theWorld.removeEventListener(this);
        }
        this.frustumUpdatePosX = Double.MIN_VALUE;
        this.frustumUpdatePosY = Double.MIN_VALUE;
        this.frustumUpdatePosZ = Double.MIN_VALUE;
        this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
        this.renderManager.set(worldClientIn);
        this.theWorld = worldClientIn;
        if (Config.isDynamicLights()) {
            DynamicLights.clear();
        }
        if (worldClientIn != null) {
            worldClientIn.addEventListener(this);
            this.loadRenderers();
        }
        else {
            this.chunksToUpdate.clear();
            this.renderInfos.clear();
            if (this.viewFrustum != null) {
                this.viewFrustum.deleteGlResources();
                this.viewFrustum = null;
            }
            if (this.renderDispatcher != null) {
                this.renderDispatcher.stopWorkerThreads();
            }
            this.renderDispatcher = null;
        }
    }
    
    public void loadRenderers() {
        if (this.theWorld != null) {
            if (this.renderDispatcher == null) {
                this.renderDispatcher = new ChunkRenderDispatcher();
            }
            this.displayListEntitiesDirty = true;
            Blocks.LEAVES.setGraphicsLevel(Config.isTreesFancy());
            Blocks.LEAVES2.setGraphicsLevel(Config.isTreesFancy());
            BlockModelRenderer.updateAoLightValue();
            RenderGlobal.renderInfoCache.clear();
            if (Config.isDynamicLights()) {
                DynamicLights.clear();
            }
            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
            this.renderDistance = this.renderDistanceChunks * 16;
            this.renderDistanceSq = this.renderDistance * this.renderDistance;
            final boolean flag = this.vboEnabled;
            this.vboEnabled = OpenGlHelper.useVbo();
            if (flag && !this.vboEnabled) {
                this.renderContainer = new RenderList();
                this.renderChunkFactory = new ListChunkFactory();
            }
            else if (!flag && this.vboEnabled) {
                this.renderContainer = new VboRenderList();
                this.renderChunkFactory = new VboChunkFactory();
            }
            if (flag != this.vboEnabled) {
                this.generateStars();
                this.generateSky();
                this.generateSky2();
            }
            if (this.viewFrustum != null) {
                this.viewFrustum.deleteGlResources();
            }
            this.stopChunkUpdates();
            synchronized (this.setTileEntities) {
                this.setTileEntities.clear();
                monitorexit(this.setTileEntities);
            }
            this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);
            if (this.theWorld != null) {
                final Entity entity = this.mc.getRenderViewEntity();
                if (entity != null) {
                    this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ);
                }
            }
            this.renderEntitiesStartupCounter = 2;
        }
    }
    
    protected void stopChunkUpdates() {
        this.chunksToUpdate.clear();
        this.renderDispatcher.stopChunkUpdates();
    }
    
    public void createBindEntityOutlineFbs(final int width, final int height) {
        if (OpenGlHelper.shadersSupported && this.entityOutlineShader != null) {
            this.entityOutlineShader.createBindFramebuffers(width, height);
        }
    }
    
    public void renderEntities(final Entity renderViewEntity, final ICamera camera, final float partialTicks) {
        int i = 0;
        if (Reflector.MinecraftForgeClient_getRenderPass.exists()) {
            i = Reflector.callInt(Reflector.MinecraftForgeClient_getRenderPass, new Object[0]);
        }
        if (this.renderEntitiesStartupCounter > 0) {
            if (i > 0) {
                return;
            }
            --this.renderEntitiesStartupCounter;
        }
        else {
            final double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks;
            final double d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks;
            final double d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks;
            this.theWorld.theProfiler.startSection("prepare");
            TileEntityRendererDispatcher.instance.prepare(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.objectMouseOver, partialTicks);
            this.renderManager.cacheActiveRenderInfo(this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
            if (i == 0) {
                this.countEntitiesTotal = 0;
                this.countEntitiesRendered = 0;
                this.countEntitiesHidden = 0;
                this.countTileEntitiesRendered = 0;
            }
            final Entity entity = this.mc.getRenderViewEntity();
            final double d4 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            final double d5 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            final double d6 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
            TileEntityRendererDispatcher.staticPlayerX = d4;
            TileEntityRendererDispatcher.staticPlayerY = d5;
            TileEntityRendererDispatcher.staticPlayerZ = d6;
            this.renderManager.setRenderPosition(d4, d5, d6);
            this.mc.entityRenderer.enableLightmap();
            this.theWorld.theProfiler.endStartSection("global");
            final List<Entity> list = this.theWorld.getLoadedEntityList();
            if (i == 0) {
                this.countEntitiesTotal = list.size();
            }
            if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
                GlStateManager.disableFog();
            }
            final boolean flag = Reflector.ForgeEntity_shouldRenderInPass.exists();
            final boolean flag2 = Reflector.ForgeTileEntity_shouldRenderInPass.exists();
            for (int j = 0; j < this.theWorld.weatherEffects.size(); ++j) {
                final Entity entity2 = this.theWorld.weatherEffects.get(j);
                if (!flag || Reflector.callBoolean(entity2, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                    ++this.countEntitiesRendered;
                    if (entity2.isInRangeToRender3d(d0, d2, d3)) {
                        this.renderManager.renderEntityStatic(entity2, partialTicks, false);
                    }
                }
            }
            this.theWorld.theProfiler.endStartSection("entities");
            final boolean flag3 = Config.isShaders();
            if (flag3) {
                Shaders.beginEntities();
            }
            final boolean flag4 = this.mc.gameSettings.fancyGraphics;
            this.mc.gameSettings.fancyGraphics = Config.isDroppedItemsFancy();
            final List<Entity> list2 = (List<Entity>)Lists.newArrayList();
            final List<Entity> list3 = (List<Entity>)Lists.newArrayList();
            final BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
            for (final Object renderglobal$containerlocalrenderinformation0 : this.renderInfosEntities) {
                final ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation2 = (ContainerLocalRenderInformation)renderglobal$containerlocalrenderinformation0;
                final Chunk chunk = renderglobal$containerlocalrenderinformation2.renderChunk.getChunk(this.theWorld);
                final ClassInheritanceMultiMap<Entity> classinheritancemultimap = chunk.getEntityLists()[renderglobal$containerlocalrenderinformation2.renderChunk.getPosition().getY() / 16];
                if (!classinheritancemultimap.isEmpty()) {
                    for (final Entity entity3 : classinheritancemultimap) {
                        if (!flag || Reflector.callBoolean(entity3, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                            final boolean flag5 = this.renderManager.shouldRender(entity3, camera, d0, d2, d3) || entity3.isRidingOrBeingRiddenBy(this.mc.player);
                            if (!flag5) {
                                continue;
                            }
                            final boolean flag6 = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
                            if ((entity3 == this.mc.getRenderViewEntity() && this.mc.gameSettings.thirdPersonView == 0 && !flag6) || (entity3.posY >= 0.0 && entity3.posY < 256.0 && !this.theWorld.isBlockLoaded(blockpos$pooledmutableblockpos.setPos(entity3)))) {
                                continue;
                            }
                            ++this.countEntitiesRendered;
                            this.renderedEntity = entity3;
                            if (flag3) {
                                Shaders.nextEntity(entity3);
                            }
                            this.renderManager.renderEntityStatic(entity3, partialTicks, false);
                            this.renderedEntity = null;
                            if (this.isOutlineActive(entity3, entity, camera)) {
                                list2.add(entity3);
                            }
                            if (!this.renderManager.isRenderMultipass(entity3)) {
                                continue;
                            }
                            list3.add(entity3);
                        }
                    }
                }
            }
            blockpos$pooledmutableblockpos.release();
            if (!list3.isEmpty()) {
                for (final Entity entity4 : list3) {
                    if (!flag || Reflector.callBoolean(entity4, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                        if (flag3) {
                            Shaders.nextEntity(entity4);
                        }
                        this.renderManager.renderMultipass(entity4, partialTicks);
                    }
                }
            }
            if (i == 0 && this.isRenderEntityOutlines() && (!list2.isEmpty() || this.entityOutlinesRendered)) {
                this.theWorld.theProfiler.endStartSection("entityOutlines");
                this.entityOutlineFramebuffer.framebufferClear();
                this.entityOutlinesRendered = !list2.isEmpty();
                if (!list2.isEmpty()) {
                    GlStateManager.depthFunc(519);
                    GlStateManager.disableFog();
                    this.entityOutlineFramebuffer.bindFramebuffer(false);
                    RenderHelper.disableStandardItemLighting();
                    this.renderManager.setRenderOutlines(true);
                    for (int k = 0; k < list2.size(); ++k) {
                        final Entity entity5 = list2.get(k);
                        if (!flag || Reflector.callBoolean(entity5, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                            if (flag3) {
                                Shaders.nextEntity(entity5);
                            }
                            this.renderManager.renderEntityStatic(entity5, partialTicks, false);
                        }
                    }
                    this.renderManager.setRenderOutlines(false);
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.depthMask(false);
                    this.entityOutlineShader.loadShaderGroup(partialTicks);
                    GlStateManager.enableLighting();
                    GlStateManager.depthMask(true);
                    GlStateManager.enableFog();
                    GlStateManager.enableBlend();
                    GlStateManager.enableColorMaterial();
                    GlStateManager.depthFunc(515);
                    GlStateManager.enableDepth();
                    GlStateManager.enableAlpha();
                }
                this.mc.getFramebuffer().bindFramebuffer(false);
            }
            if (!this.isRenderEntityOutlines() && (!list2.isEmpty() || this.entityOutlinesRendered)) {
                this.theWorld.theProfiler.endStartSection("entityOutlines");
                this.entityOutlinesRendered = !list2.isEmpty();
                if (!list2.isEmpty()) {
                    GlStateManager.disableFog();
                    GlStateManager.disableDepth();
                    this.mc.entityRenderer.disableLightmap();
                    RenderHelper.disableStandardItemLighting();
                    this.renderManager.setRenderOutlines(true);
                    for (int l = 0; l < list2.size(); ++l) {
                        final Entity entity6 = list2.get(l);
                        if (!flag || Reflector.callBoolean(entity6, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                            if (flag3) {
                                Shaders.nextEntity(entity6);
                            }
                            this.renderManager.renderEntityStatic(entity6, partialTicks, false);
                        }
                    }
                    this.renderManager.setRenderOutlines(false);
                    RenderHelper.enableStandardItemLighting();
                    this.mc.entityRenderer.enableLightmap();
                    GlStateManager.enableDepth();
                    GlStateManager.enableFog();
                }
            }
            this.mc.gameSettings.fancyGraphics = flag4;
            final FontRenderer fontrenderer = TileEntityRendererDispatcher.instance.getFontRenderer();
            if (flag3) {
                Shaders.endEntities();
                Shaders.beginBlockEntities();
            }
            this.theWorld.theProfiler.endStartSection("blockentities");
            RenderHelper.enableStandardItemLighting();
            if (Reflector.ForgeTileEntity_hasFastRenderer.exists()) {
                TileEntityRendererDispatcher.instance.preDrawBatch();
            }
            for (final Object renderglobal$containerlocalrenderinformation3 : this.renderInfosTileEntities) {
                final ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation4 = (ContainerLocalRenderInformation)renderglobal$containerlocalrenderinformation3;
                final List<TileEntity> list4 = renderglobal$containerlocalrenderinformation4.renderChunk.getCompiledChunk().getTileEntities();
                if (!list4.isEmpty()) {
                    for (final TileEntity tileentity1 : list4) {
                        if (flag2) {
                            if (!Reflector.callBoolean(tileentity1, Reflector.ForgeTileEntity_shouldRenderInPass, i)) {
                                continue;
                            }
                            final AxisAlignedBB axisalignedbb = (AxisAlignedBB)Reflector.call(tileentity1, Reflector.ForgeTileEntity_getRenderBoundingBox, new Object[0]);
                            if (axisalignedbb != null && !camera.isBoundingBoxInFrustum(axisalignedbb)) {
                                continue;
                            }
                        }
                        final Class oclass = tileentity1.getClass();
                        if (oclass == TileEntitySign.class && !Config.zoomMode) {
                            final EntityPlayer entityplayer = this.mc.player;
                            final double d7 = tileentity1.getDistanceSq(entityplayer.posX, entityplayer.posY, entityplayer.posZ);
                            if (d7 > 256.0) {
                                fontrenderer.enabled = false;
                            }
                        }
                        if (flag3) {
                            Shaders.nextBlockEntity(tileentity1);
                        }
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileentity1, partialTicks, -1);
                        ++this.countTileEntitiesRendered;
                        fontrenderer.enabled = true;
                    }
                }
            }
            synchronized (this.setTileEntities) {
                for (final TileEntity tileentity2 : this.setTileEntities) {
                    if (!flag2 || Reflector.callBoolean(tileentity2, Reflector.ForgeTileEntity_shouldRenderInPass, i)) {
                        if (flag3) {
                            Shaders.nextBlockEntity(tileentity2);
                        }
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileentity2, partialTicks, -1);
                    }
                }
                monitorexit(this.setTileEntities);
            }
            if (Reflector.ForgeTileEntity_hasFastRenderer.exists()) {
                TileEntityRendererDispatcher.instance.drawBatch(i);
            }
            this.renderOverlayDamaged = true;
            this.preRenderDamagedBlocks();
            for (final DestroyBlockProgress destroyblockprogress : this.damagedBlocks.values()) {
                BlockPos blockpos = destroyblockprogress.getPosition();
                if (this.theWorld.getBlockState(blockpos).getBlock().hasTileEntity()) {
                    TileEntity tileentity3 = this.theWorld.getTileEntity(blockpos);
                    if (tileentity3 instanceof TileEntityChest) {
                        final TileEntityChest tileentitychest = (TileEntityChest)tileentity3;
                        if (tileentitychest.adjacentChestXNeg != null) {
                            blockpos = blockpos.offset(EnumFacing.WEST);
                            tileentity3 = this.theWorld.getTileEntity(blockpos);
                        }
                        else if (tileentitychest.adjacentChestZNeg != null) {
                            blockpos = blockpos.offset(EnumFacing.NORTH);
                            tileentity3 = this.theWorld.getTileEntity(blockpos);
                        }
                    }
                    final IBlockState iblockstate = this.theWorld.getBlockState(blockpos);
                    if (tileentity3 == null || !iblockstate.func_191057_i()) {
                        continue;
                    }
                    if (flag3) {
                        Shaders.nextBlockEntity(tileentity3);
                    }
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileentity3, partialTicks, destroyblockprogress.getPartialBlockDamage());
                }
            }
            this.postRenderDamagedBlocks();
            this.renderOverlayDamaged = false;
            this.mc.entityRenderer.disableLightmap();
            this.mc.mcProfiler.endSection();
        }
    }
    
    private boolean isOutlineActive(final Entity p_184383_1_, final Entity p_184383_2_, final ICamera p_184383_3_) {
        final boolean flag = p_184383_2_ instanceof EntityLivingBase && ((EntityLivingBase)p_184383_2_).isPlayerSleeping();
        return (p_184383_1_ != p_184383_2_ || this.mc.gameSettings.thirdPersonView != 0 || flag) && (p_184383_1_.isGlowing() || (this.mc.player.isSpectator() && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown() && p_184383_1_ instanceof EntityPlayer && (p_184383_1_.ignoreFrustumCheck || p_184383_3_.isBoundingBoxInFrustum(p_184383_1_.getEntityBoundingBox()) || p_184383_1_.isRidingOrBeingRiddenBy(this.mc.player))));
    }
    
    public String getDebugInfoRenders() {
        final int i = this.viewFrustum.renderChunks.length;
        final int j = this.getRenderedChunks();
        return String.format("C: %d/%d %sD: %d, L: %d, %s", j, i, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.setLightUpdates.size(), (this.renderDispatcher == null) ? "null" : this.renderDispatcher.getDebugInfo());
    }
    
    protected int getRenderedChunks() {
        int i = 0;
        for (final ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos) {
            final CompiledChunk compiledchunk = renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk;
            if (compiledchunk != CompiledChunk.DUMMY && !compiledchunk.isEmpty()) {
                ++i;
            }
        }
        return i;
    }
    
    public String getDebugInfoEntities() {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ", B: " + this.countEntitiesHidden + ", " + Config.getVersionDebug();
    }
    
    public void setupTerrain(final Entity viewEntity, final double partialTicks, ICamera camera, final int frameCount, final boolean playerSpectator) {
        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks) {
            this.loadRenderers();
        }
        this.theWorld.theProfiler.startSection("camera");
        final double d0 = viewEntity.posX - this.frustumUpdatePosX;
        final double d2 = viewEntity.posY - this.frustumUpdatePosY;
        final double d3 = viewEntity.posZ - this.frustumUpdatePosZ;
        if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ || d0 * d0 + d2 * d2 + d3 * d3 > 16.0) {
            this.frustumUpdatePosX = viewEntity.posX;
            this.frustumUpdatePosY = viewEntity.posY;
            this.frustumUpdatePosZ = viewEntity.posZ;
            this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
            this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
            this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
            this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
        }
        if (Config.isDynamicLights()) {
            DynamicLights.update(this);
        }
        this.theWorld.theProfiler.endStartSection("renderlistcamera");
        final double d4 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        final double d5 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        final double d6 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        this.renderContainer.initialize(d4, d5, d6);
        this.theWorld.theProfiler.endStartSection("cull");
        if (this.debugFixedClippingHelper != null) {
            final Frustum frustum = new Frustum(this.debugFixedClippingHelper);
            frustum.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
            camera = frustum;
        }
        this.mc.mcProfiler.endStartSection("culling");
        final BlockPos blockpos1 = new BlockPos(d4, d5 + viewEntity.getEyeHeight(), d6);
        final RenderChunk renderchunk = this.viewFrustum.getRenderChunk(blockpos1);
        new BlockPos(MathHelper.floor(d4 / 16.0) * 16, MathHelper.floor(d5 / 16.0) * 16, MathHelper.floor(d6 / 16.0) * 16);
        this.displayListEntitiesDirty = (this.displayListEntitiesDirty || !this.chunksToUpdate.isEmpty() || viewEntity.posX != this.lastViewEntityX || viewEntity.posY != this.lastViewEntityY || viewEntity.posZ != this.lastViewEntityZ || viewEntity.rotationPitch != this.lastViewEntityPitch || viewEntity.rotationYaw != this.lastViewEntityYaw);
        this.lastViewEntityX = viewEntity.posX;
        this.lastViewEntityY = viewEntity.posY;
        this.lastViewEntityZ = viewEntity.posZ;
        this.lastViewEntityPitch = viewEntity.rotationPitch;
        this.lastViewEntityYaw = viewEntity.rotationYaw;
        final boolean flag = this.debugFixedClippingHelper != null;
        this.mc.mcProfiler.endStartSection("update");
        Lagometer.timerVisibility.start();
        final int i = this.getCountLoadedChunks();
        if (i != this.countLoadedChunksPrev) {
            this.countLoadedChunksPrev = i;
            this.displayListEntitiesDirty = true;
        }
        if (Shaders.isShadowPass) {
            this.renderInfos = this.renderInfosShadow;
            this.renderInfosEntities = this.renderInfosEntitiesShadow;
            this.renderInfosTileEntities = this.renderInfosTileEntitiesShadow;
            if (!flag && this.displayListEntitiesDirty) {
                this.renderInfos.clear();
                this.renderInfosEntities.clear();
                this.renderInfosTileEntities.clear();
                final RenderInfoLazy renderinfolazy = new RenderInfoLazy();
                final Iterator<RenderChunk> iterator = ShadowUtils.makeShadowChunkIterator(this.theWorld, partialTicks, viewEntity, this.renderDistanceChunks, this.viewFrustum);
                while (iterator.hasNext()) {
                    final RenderChunk renderchunk2 = iterator.next();
                    if (renderchunk2 != null) {
                        renderinfolazy.setRenderChunk(renderchunk2);
                        if (!renderchunk2.compiledChunk.isEmpty() || renderchunk2.isNeedsUpdate()) {
                            this.renderInfos.add(renderinfolazy.getRenderInfo());
                        }
                        final BlockPos blockpos2 = renderchunk2.getPosition();
                        if (ChunkUtils.hasEntities(this.theWorld.getChunkFromBlockCoords(blockpos2))) {
                            this.renderInfosEntities.add(renderinfolazy.getRenderInfo());
                        }
                        if (renderchunk2.getCompiledChunk().getTileEntities().size() <= 0) {
                            continue;
                        }
                        this.renderInfosTileEntities.add(renderinfolazy.getRenderInfo());
                    }
                }
            }
        }
        else {
            this.renderInfos = this.renderInfosNormal;
            this.renderInfosEntities = this.renderInfosEntitiesNormal;
            this.renderInfosTileEntities = this.renderInfosTileEntitiesNormal;
        }
        if (!flag && this.displayListEntitiesDirty && !Shaders.isShadowPass) {
            this.displayListEntitiesDirty = false;
            for (final ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 : this.renderInfos) {
                this.freeRenderInformation(renderglobal$containerlocalrenderinformation1);
            }
            this.renderInfos.clear();
            this.renderInfosEntities.clear();
            this.renderInfosTileEntities.clear();
            this.visibilityDeque.clear();
            final Deque deque = this.visibilityDeque;
            Entity.setRenderDistanceWeight(MathHelper.clamp(this.mc.gameSettings.renderDistanceChunks / 8.0, 1.0, 2.5));
            boolean flag2 = this.mc.renderChunksMany;
            if (renderchunk != null) {
                boolean flag3 = false;
                final ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation2 = new ContainerLocalRenderInformation(renderchunk, null, 0);
                final Set set1 = RenderGlobal.SET_ALL_FACINGS;
                if (set1.size() == 1) {
                    final Vector3f vector3f = this.getViewVector(viewEntity, partialTicks);
                    final EnumFacing enumfacing = EnumFacing.getFacingFromVector(vector3f.x, vector3f.y, vector3f.z).getOpposite();
                    set1.remove(enumfacing);
                }
                if (set1.isEmpty()) {
                    flag3 = true;
                }
                if (flag3 && !playerSpectator) {
                    this.renderInfos.add(renderglobal$containerlocalrenderinformation2);
                }
                else {
                    if (playerSpectator && this.theWorld.getBlockState(blockpos1).isOpaqueCube()) {
                        flag2 = false;
                    }
                    renderchunk.setFrameIndex(frameCount);
                    deque.add(renderglobal$containerlocalrenderinformation2);
                }
            }
            else {
                final int i2 = (blockpos1.getY() > 0) ? 248 : 8;
                for (int j1 = -this.renderDistanceChunks; j1 <= this.renderDistanceChunks; ++j1) {
                    for (int k = -this.renderDistanceChunks; k <= this.renderDistanceChunks; ++k) {
                        final RenderChunk renderchunk3 = this.viewFrustum.getRenderChunk(new BlockPos((j1 << 4) + 8, i2, (k << 4) + 8));
                        if (renderchunk3 != null && camera.isBoundingBoxInFrustum(renderchunk3.boundingBox)) {
                            renderchunk3.setFrameIndex(frameCount);
                            deque.add(new ContainerLocalRenderInformation(renderchunk3, null, 0));
                        }
                    }
                }
            }
            this.mc.mcProfiler.startSection("iteration");
            final EnumFacing[] aenumfacing = EnumFacing.VALUES;
            final int k2 = aenumfacing.length;
            while (!deque.isEmpty()) {
                final ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation3 = deque.poll();
                final RenderChunk renderchunk4 = renderglobal$containerlocalrenderinformation3.renderChunk;
                final EnumFacing enumfacing2 = renderglobal$containerlocalrenderinformation3.facing;
                boolean flag4 = false;
                final CompiledChunk compiledchunk = renderchunk4.compiledChunk;
                if (!compiledchunk.isEmpty() || renderchunk4.isNeedsUpdate()) {
                    this.renderInfos.add(renderglobal$containerlocalrenderinformation3);
                    flag4 = true;
                }
                if (ChunkUtils.hasEntities(renderchunk4.getChunk(this.theWorld))) {
                    this.renderInfosEntities.add(renderglobal$containerlocalrenderinformation3);
                    flag4 = true;
                }
                if (compiledchunk.getTileEntities().size() > 0) {
                    this.renderInfosTileEntities.add(renderglobal$containerlocalrenderinformation3);
                    flag4 = true;
                }
                for (final EnumFacing enumfacing3 : aenumfacing) {
                    if ((!flag2 || !renderglobal$containerlocalrenderinformation3.hasDirection(enumfacing3.getOpposite())) && (!flag2 || enumfacing2 == null || compiledchunk.isVisible(enumfacing2.getOpposite(), enumfacing3))) {
                        final RenderChunk renderchunk5 = this.getRenderChunkOffset(blockpos1, renderchunk4, enumfacing3);
                        if (renderchunk5 != null && renderchunk5.setFrameIndex(frameCount) && camera.isBoundingBoxInFrustum(renderchunk5.boundingBox)) {
                            final int m = renderglobal$containerlocalrenderinformation3.setFacing | 1 << enumfacing3.ordinal();
                            final ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation4 = this.allocateRenderInformation(renderchunk5, enumfacing3, m);
                            deque.add(renderglobal$containerlocalrenderinformation4);
                        }
                    }
                }
                if (!flag4) {
                    this.freeRenderInformation(renderglobal$containerlocalrenderinformation3);
                }
            }
            this.mc.mcProfiler.endSection();
        }
        this.mc.mcProfiler.endStartSection("captureFrustum");
        if (this.debugFixTerrainFrustum) {
            this.fixTerrainFrustum(d4, d5, d6);
            this.debugFixTerrainFrustum = false;
        }
        Lagometer.timerVisibility.end();
        if (Shaders.isShadowPass) {
            Shaders.mcProfilerEndSection();
        }
        else {
            this.mc.mcProfiler.endStartSection("rebuildNear");
            final Set<RenderChunk> set2 = this.chunksToUpdate;
            this.chunksToUpdate = (Set<RenderChunk>)Sets.newLinkedHashSet();
            Lagometer.timerChunkUpdate.start();
            for (final ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation5 : this.renderInfos) {
                final RenderChunk renderchunk6 = renderglobal$containerlocalrenderinformation5.renderChunk;
                if (renderchunk6.isNeedsUpdate() || set2.contains(renderchunk6)) {
                    this.displayListEntitiesDirty = true;
                    final BlockPos blockpos3 = renderchunk6.getPosition();
                    final boolean flag5 = blockpos1.distanceSq(blockpos3.getX() + 8, blockpos3.getY() + 8, blockpos3.getZ() + 8) < 768.0;
                    if (!flag5) {
                        this.chunksToUpdate.add(renderchunk6);
                    }
                    else if (!renderchunk6.isPlayerUpdate()) {
                        this.chunksToUpdateForced.add(renderchunk6);
                    }
                    else {
                        this.mc.mcProfiler.startSection("build near");
                        this.renderDispatcher.updateChunkNow(renderchunk6);
                        renderchunk6.clearNeedsUpdate();
                        this.mc.mcProfiler.endSection();
                    }
                }
            }
            Lagometer.timerChunkUpdate.end();
            this.chunksToUpdate.addAll(set2);
            this.mc.mcProfiler.endSection();
        }
    }
    
    private Set<EnumFacing> getVisibleFacings(final BlockPos pos) {
        final VisGraph visgraph = new VisGraph();
        final BlockPos blockpos = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
        final Chunk chunk = this.theWorld.getChunkFromBlockCoords(blockpos);
        for (final BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos.add(15, 15, 15))) {
            if (chunk.getBlockState(blockpos$mutableblockpos).isOpaqueCube()) {
                visgraph.setOpaqueCube(blockpos$mutableblockpos);
            }
        }
        return visgraph.getVisibleFacings(pos);
    }
    
    @Nullable
    private RenderChunk getRenderChunkOffset(final BlockPos playerPos, final RenderChunk renderChunkBase, final EnumFacing facing) {
        final BlockPos blockpos = renderChunkBase.getBlockPosOffset16(facing);
        if (blockpos.getY() >= 0 && blockpos.getY() < 256) {
            final int i = playerPos.getX() - blockpos.getX();
            final int j = playerPos.getZ() - blockpos.getZ();
            if (Config.isFogOff()) {
                if (Math.abs(i) > this.renderDistance || Math.abs(j) > this.renderDistance) {
                    return null;
                }
            }
            else {
                final int k = i * i + j * j;
                if (k > this.renderDistanceSq) {
                    return null;
                }
            }
            return renderChunkBase.getRenderChunkOffset16(this.viewFrustum, facing);
        }
        return null;
    }
    
    private void fixTerrainFrustum(final double x, final double y, final double z) {
        this.debugFixedClippingHelper = new ClippingHelperImpl();
        ((ClippingHelperImpl)this.debugFixedClippingHelper).init();
        final Matrix4f matrix4f = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
        matrix4f.transpose();
        final Matrix4f matrix4f2 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
        matrix4f2.transpose();
        final Matrix4f matrix4f3 = new Matrix4f();
        org.lwjgl.util.vector.Matrix4f.mul(matrix4f2, matrix4f, matrix4f3);
        matrix4f3.invert();
        this.debugTerrainFrustumPosition.x = x;
        this.debugTerrainFrustumPosition.y = y;
        this.debugTerrainFrustumPosition.z = z;
        this.debugTerrainMatrix[0] = new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f);
        this.debugTerrainMatrix[1] = new Vector4f(1.0f, -1.0f, -1.0f, 1.0f);
        this.debugTerrainMatrix[2] = new Vector4f(1.0f, 1.0f, -1.0f, 1.0f);
        this.debugTerrainMatrix[3] = new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f);
        this.debugTerrainMatrix[4] = new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f);
        this.debugTerrainMatrix[5] = new Vector4f(1.0f, -1.0f, 1.0f, 1.0f);
        this.debugTerrainMatrix[6] = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.debugTerrainMatrix[7] = new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 0; i < 8; ++i) {
            org.lwjgl.util.vector.Matrix4f.transform(matrix4f3, this.debugTerrainMatrix[i], this.debugTerrainMatrix[i]);
            final Vector4f vector4f = this.debugTerrainMatrix[i];
            vector4f.x /= this.debugTerrainMatrix[i].w;
            final Vector4f vector4f2 = this.debugTerrainMatrix[i];
            vector4f2.y /= this.debugTerrainMatrix[i].w;
            final Vector4f vector4f3 = this.debugTerrainMatrix[i];
            vector4f3.z /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].w = 1.0f;
        }
    }
    
    protected Vector3f getViewVector(final Entity entityIn, final double partialTicks) {
        float f = (float)(entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
        final float f2 = (float)(entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
            f += 180.0f;
        }
        final float f3 = MathHelper.cos(-f2 * 0.017453292f - 3.1415927f);
        final float f4 = MathHelper.sin(-f2 * 0.017453292f - 3.1415927f);
        final float f5 = -MathHelper.cos(-f * 0.017453292f);
        final float f6 = MathHelper.sin(-f * 0.017453292f);
        return new Vector3f(f4 * f5, f6, f3 * f5);
    }
    
    public int renderBlockLayer(final BlockRenderLayer blockLayerIn, final double partialTicks, final int pass, final Entity entityIn) {
        RenderHelper.disableStandardItemLighting();
        if (blockLayerIn == BlockRenderLayer.TRANSLUCENT) {
            this.mc.mcProfiler.startSection("translucent_sort");
            final double d0 = entityIn.posX - this.prevRenderSortX;
            final double d2 = entityIn.posY - this.prevRenderSortY;
            final double d3 = entityIn.posZ - this.prevRenderSortZ;
            if (d0 * d0 + d2 * d2 + d3 * d3 > 1.0) {
                this.prevRenderSortX = entityIn.posX;
                this.prevRenderSortY = entityIn.posY;
                this.prevRenderSortZ = entityIn.posZ;
                int k = 0;
                this.chunksToResortTransparency.clear();
                for (final ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos) {
                    if (renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk.isLayerStarted(blockLayerIn) && k++ < 15) {
                        this.chunksToResortTransparency.add(renderglobal$containerlocalrenderinformation.renderChunk);
                    }
                }
            }
            this.mc.mcProfiler.endSection();
        }
        this.mc.mcProfiler.startSection("filterempty");
        int l = 0;
        final boolean flag = blockLayerIn == BlockRenderLayer.TRANSLUCENT;
        final int i1 = flag ? (this.renderInfos.size() - 1) : 0;
        for (int j = flag ? -1 : this.renderInfos.size(), j2 = flag ? -1 : 1, m = i1; m != j; m += j2) {
            final RenderChunk renderchunk = this.renderInfos.get(m).renderChunk;
            if (!renderchunk.getCompiledChunk().isLayerEmpty(blockLayerIn)) {
                ++l;
                this.renderContainer.addRenderChunk(renderchunk, blockLayerIn);
            }
        }
        if (l == 0) {
            this.mc.mcProfiler.endSection();
            return l;
        }
        if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
            GlStateManager.disableFog();
        }
        this.mc.mcProfiler.endStartSection("render_" + blockLayerIn);
        this.renderBlockLayer(blockLayerIn);
        this.mc.mcProfiler.endSection();
        return l;
    }
    
    private void renderBlockLayer(final BlockRenderLayer blockLayerIn) {
        this.mc.entityRenderer.enableLightmap();
        if (OpenGlHelper.useVbo()) {
            GlStateManager.glEnableClientState(32884);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glEnableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.glEnableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glEnableClientState(32886);
        }
        if (Config.isShaders()) {
            ShadersRender.preRenderChunkLayer(blockLayerIn);
        }
        this.renderContainer.renderChunkLayer(blockLayerIn);
        if (Config.isShaders()) {
            ShadersRender.postRenderChunkLayer(blockLayerIn);
        }
        if (OpenGlHelper.useVbo()) {
            for (final VertexFormatElement vertexformatelement : DefaultVertexFormats.BLOCK.getElements()) {
                final VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
                final int k1 = vertexformatelement.getIndex();
                switch (vertexformatelement$enumusage) {
                    default: {
                        continue;
                    }
                    case POSITION: {
                        GlStateManager.glDisableClientState(32884);
                        continue;
                    }
                    case UV: {
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + k1);
                        GlStateManager.glDisableClientState(32888);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        continue;
                    }
                    case COLOR: {
                        GlStateManager.glDisableClientState(32886);
                        GlStateManager.resetColor();
                        continue;
                    }
                }
            }
        }
        this.mc.entityRenderer.disableLightmap();
    }
    
    private void cleanupDamagedBlocks(final Iterator<DestroyBlockProgress> iteratorIn) {
        while (iteratorIn.hasNext()) {
            final DestroyBlockProgress destroyblockprogress = iteratorIn.next();
            final int k1 = destroyblockprogress.getCreationCloudUpdateTick();
            if (this.cloudTickCounter - k1 > 400) {
                iteratorIn.remove();
            }
        }
    }
    
    public void updateClouds() {
        if (Config.isShaders() && Keyboard.isKeyDown(61) && Keyboard.isKeyDown(19)) {
            Shaders.uninit();
            Shaders.loadShaderPack();
            Reflector.Minecraft_actionKeyF3.setValue(this.mc, Boolean.TRUE);
        }
        ++this.cloudTickCounter;
        if (this.cloudTickCounter % 20 == 0) {
            this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
        }
        if (!this.setLightUpdates.isEmpty() && !this.renderDispatcher.hasNoFreeRenderBuilders() && this.chunksToUpdate.isEmpty()) {
            final Iterator<BlockPos> iterator = this.setLightUpdates.iterator();
            while (iterator.hasNext()) {
                final BlockPos blockpos = iterator.next();
                iterator.remove();
                final int k1 = blockpos.getX();
                final int l1 = blockpos.getY();
                final int i2 = blockpos.getZ();
                this.markBlocksForUpdate(k1 - 1, l1 - 1, i2 - 1, k1 + 1, l1 + 1, i2 + 1, false);
            }
        }
    }
    
    private void renderSkyEnd() {
        if (Config.isSkyEnabled()) {
            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.depthMask(false);
            this.renderEngine.bindTexture(RenderGlobal.END_SKY_TEXTURES);
            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder bufferbuilder = tessellator.getBuffer();
            for (int k1 = 0; k1 < 6; ++k1) {
                GlStateManager.pushMatrix();
                if (k1 == 1) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                }
                if (k1 == 2) {
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
                }
                if (k1 == 3) {
                    GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
                }
                if (k1 == 4) {
                    GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                }
                if (k1 == 5) {
                    GlStateManager.rotate(-90.0f, 0.0f, 0.0f, 1.0f);
                }
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int l1 = 40;
                int i2 = 40;
                int j2 = 40;
                if (Config.isCustomColors()) {
                    Vec3d vec3d = new Vec3d(l1 / 255.0, i2 / 255.0, j2 / 255.0);
                    vec3d = CustomColors.getWorldSkyColor(vec3d, this.theWorld, this.mc.getRenderViewEntity(), 0.0f);
                    l1 = (int)(vec3d.xCoord * 255.0);
                    i2 = (int)(vec3d.yCoord * 255.0);
                    j2 = (int)(vec3d.zCoord * 255.0);
                }
                bufferbuilder.pos(-100.0, -100.0, -100.0).tex(0.0, 0.0).color(l1, i2, j2, 255).endVertex();
                bufferbuilder.pos(-100.0, -100.0, 100.0).tex(0.0, 16.0).color(l1, i2, j2, 255).endVertex();
                bufferbuilder.pos(100.0, -100.0, 100.0).tex(16.0, 16.0).color(l1, i2, j2, 255).endVertex();
                bufferbuilder.pos(100.0, -100.0, -100.0).tex(16.0, 0.0).color(l1, i2, j2, 255).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }
    
    public void renderSky(final float partialTicks, final int pass) {
        if (Reflector.ForgeWorldProvider_getSkyRenderer.exists()) {
            final WorldProvider worldprovider = this.mc.world.provider;
            final Object object = Reflector.call(worldprovider, Reflector.ForgeWorldProvider_getSkyRenderer, new Object[0]);
            if (object != null) {
                Reflector.callVoid(object, Reflector.IRenderHandler_render, partialTicks, this.theWorld, this.mc);
                return;
            }
        }
        if (this.mc.world.provider.getDimensionType() == DimensionType.THE_END) {
            this.renderSkyEnd();
        }
        else if (this.mc.world.provider.isSurfaceWorld()) {
            GlStateManager.disableTexture2D();
            final boolean flag1 = Config.isShaders();
            if (flag1) {
                Shaders.disableTexture2D();
            }
            Vec3d vec3d = this.theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
            vec3d = CustomColors.getSkyColor(vec3d, this.mc.world, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ);
            if (flag1) {
                Shaders.setSkyColor(vec3d);
            }
            float f = (float)vec3d.xCoord;
            float f2 = (float)vec3d.yCoord;
            float f3 = (float)vec3d.zCoord;
            if (pass != 2) {
                final float f4 = (f * 30.0f + f2 * 59.0f + f3 * 11.0f) / 100.0f;
                final float f5 = (f * 30.0f + f2 * 70.0f) / 100.0f;
                final float f6 = (f * 30.0f + f3 * 70.0f) / 100.0f;
                f = f4;
                f2 = f5;
                f3 = f6;
            }
            GlStateManager.color(f, f2, f3);
            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.depthMask(false);
            GlStateManager.enableFog();
            if (flag1) {
                Shaders.enableFog();
            }
            GlStateManager.color(f, f2, f3);
            if (flag1) {
                Shaders.preSkyList();
            }
            if (Config.isSkyEnabled()) {
                if (this.vboEnabled) {
                    this.skyVBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.skyVBO.drawArrays(7);
                    this.skyVBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                }
                else {
                    GlStateManager.callList(this.glSkyList);
                }
            }
            GlStateManager.disableFog();
            if (flag1) {
                Shaders.disableFog();
            }
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            final float[] afloat = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(partialTicks), partialTicks);
            if (afloat != null && Config.isSunMoonEnabled()) {
                GlStateManager.disableTexture2D();
                if (flag1) {
                    Shaders.disableTexture2D();
                }
                GlStateManager.shadeModel(7425);
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate((MathHelper.sin(this.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0f) ? 180.0f : 0.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                float f7 = afloat[0];
                float f8 = afloat[1];
                float f9 = afloat[2];
                if (pass != 2) {
                    final float f10 = (f7 * 30.0f + f8 * 59.0f + f9 * 11.0f) / 100.0f;
                    final float f11 = (f7 * 30.0f + f8 * 70.0f) / 100.0f;
                    final float f12 = (f7 * 30.0f + f9 * 70.0f) / 100.0f;
                    f7 = f10;
                    f8 = f11;
                    f9 = f12;
                }
                bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(0.0, 100.0, 0.0).color(f7, f8, f9, afloat[3]).endVertex();
                final int l1 = 16;
                for (int j2 = 0; j2 <= 16; ++j2) {
                    final float f13 = j2 * 6.2831855f / 16.0f;
                    final float f14 = MathHelper.sin(f13);
                    final float f15 = MathHelper.cos(f13);
                    bufferbuilder.pos(f14 * 120.0f, f15 * 120.0f, -f15 * 40.0f * afloat[3]).color(afloat[0], afloat[1], afloat[2], 0.0f).endVertex();
                }
                tessellator.draw();
                GlStateManager.popMatrix();
                GlStateManager.shadeModel(7424);
            }
            GlStateManager.enableTexture2D();
            if (flag1) {
                Shaders.enableTexture2D();
            }
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            final float f16 = 1.0f - this.theWorld.getRainStrength(partialTicks);
            GlStateManager.color(1.0f, 1.0f, 1.0f, f16);
            GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
            CustomSky.renderSky(this.theWorld, this.renderEngine, partialTicks);
            if (flag1) {
                Shaders.preCelestialRotate();
            }
            GlStateManager.rotate(this.theWorld.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f);
            if (flag1) {
                Shaders.postCelestialRotate();
            }
            float f17 = 30.0f;
            if (Config.isSunTexture()) {
                this.renderEngine.bindTexture(RenderGlobal.SUN_TEXTURES);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(-f17, 100.0, -f17).tex(0.0, 0.0).endVertex();
                bufferbuilder.pos(f17, 100.0, -f17).tex(1.0, 0.0).endVertex();
                bufferbuilder.pos(f17, 100.0, f17).tex(1.0, 1.0).endVertex();
                bufferbuilder.pos(-f17, 100.0, f17).tex(0.0, 1.0).endVertex();
                tessellator.draw();
            }
            f17 = 20.0f;
            if (Config.isMoonTexture()) {
                this.renderEngine.bindTexture(RenderGlobal.MOON_PHASES_TEXTURES);
                final int k1 = this.theWorld.getMoonPhase();
                final int i2 = k1 % 4;
                final int k2 = k1 / 4 % 2;
                final float f18 = (i2 + 0) / 4.0f;
                final float f19 = (k2 + 0) / 2.0f;
                final float f20 = (i2 + 1) / 4.0f;
                final float f21 = (k2 + 1) / 2.0f;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(-f17, -100.0, f17).tex(f20, f21).endVertex();
                bufferbuilder.pos(f17, -100.0, f17).tex(f18, f21).endVertex();
                bufferbuilder.pos(f17, -100.0, -f17).tex(f18, f19).endVertex();
                bufferbuilder.pos(-f17, -100.0, -f17).tex(f20, f19).endVertex();
                tessellator.draw();
            }
            GlStateManager.disableTexture2D();
            if (flag1) {
                Shaders.disableTexture2D();
            }
            final float f22 = this.theWorld.getStarBrightness(partialTicks) * f16;
            if (f22 > 0.0f && Config.isStarsEnabled() && !CustomSky.hasSkyLayers(this.theWorld)) {
                GlStateManager.color(f22, f22, f22, f22);
                if (this.vboEnabled) {
                    this.starVBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.starVBO.drawArrays(7);
                    this.starVBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                }
                else {
                    GlStateManager.callList(this.starGLCallList);
                }
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableFog();
            if (flag1) {
                Shaders.enableFog();
            }
            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();
            if (flag1) {
                Shaders.disableTexture2D();
            }
            GlStateManager.color(0.0f, 0.0f, 0.0f);
            final double d3 = this.mc.player.getPositionEyes(partialTicks).yCoord - this.theWorld.getHorizon();
            if (d3 < 0.0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0f, 12.0f, 0.0f);
                if (this.vboEnabled) {
                    this.sky2VBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.sky2VBO.drawArrays(7);
                    this.sky2VBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                }
                else {
                    GlStateManager.callList(this.glSkyList2);
                }
                GlStateManager.popMatrix();
                final float f23 = 1.0f;
                final float f24 = -(float)(d3 + 65.0);
                final float f25 = -1.0f;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(-1.0, f24, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, f24, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, f24, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, f24, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, f24, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, f24, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, f24, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, f24, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
            }
            if (this.theWorld.provider.isSkyColored()) {
                GlStateManager.color(f * 0.2f + 0.04f, f2 * 0.2f + 0.04f, f3 * 0.6f + 0.1f);
            }
            else {
                GlStateManager.color(f, f2, f3);
            }
            if (this.mc.gameSettings.renderDistanceChunks <= 4) {
                GlStateManager.color(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue);
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0f, -(float)(d3 - 16.0), 0.0f);
            if (Config.isSkyEnabled()) {
                GlStateManager.callList(this.glSkyList2);
            }
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
            if (flag1) {
                Shaders.enableTexture2D();
            }
            GlStateManager.depthMask(true);
        }
    }
    
    public void renderClouds(float partialTicks, final int pass, final double p_180447_3_, final double p_180447_5_, final double p_180447_7_) {
        if (!Config.isCloudsOff()) {
            if (Reflector.ForgeWorldProvider_getCloudRenderer.exists()) {
                final WorldProvider worldprovider = this.mc.world.provider;
                final Object object = Reflector.call(worldprovider, Reflector.ForgeWorldProvider_getCloudRenderer, new Object[0]);
                if (object != null) {
                    Reflector.callVoid(object, Reflector.IRenderHandler_render, partialTicks, this.theWorld, this.mc);
                    return;
                }
            }
            if (this.mc.world.provider.isSurfaceWorld()) {
                if (Config.isShaders()) {
                    Shaders.beginClouds();
                }
                if (Config.isCloudsFancy()) {
                    this.renderCloudsFancy(partialTicks, pass, p_180447_3_, p_180447_5_, p_180447_7_);
                }
                else {
                    final float f9 = partialTicks;
                    partialTicks = 0.0f;
                    GlStateManager.disableCull();
                    final int l2 = 32;
                    final int k1 = 8;
                    final Tessellator tessellator = Tessellator.getInstance();
                    final BufferBuilder bufferbuilder = tessellator.getBuffer();
                    this.renderEngine.bindTexture(RenderGlobal.CLOUDS_TEXTURES);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    final Vec3d vec3d = this.theWorld.getCloudColour(partialTicks);
                    float f10 = (float)vec3d.xCoord;
                    float f11 = (float)vec3d.yCoord;
                    float f12 = (float)vec3d.zCoord;
                    this.cloudRenderer.prepareToRender(false, this.cloudTickCounter, f9, vec3d);
                    if (this.cloudRenderer.shouldUpdateGlList()) {
                        this.cloudRenderer.startUpdateGlList();
                        if (pass != 2) {
                            final float f13 = (f10 * 30.0f + f11 * 59.0f + f12 * 11.0f) / 100.0f;
                            final float f14 = (f10 * 30.0f + f11 * 70.0f) / 100.0f;
                            final float f15 = (f10 * 30.0f + f12 * 70.0f) / 100.0f;
                            f10 = f13;
                            f11 = f14;
                            f12 = f15;
                        }
                        final float f16 = 4.8828125E-4f;
                        final double d5 = this.cloudTickCounter + partialTicks;
                        double d6 = p_180447_3_ + d5 * 0.029999999329447746;
                        final int l3 = MathHelper.floor(d6 / 2048.0);
                        final int i2 = MathHelper.floor(p_180447_7_ / 2048.0);
                        d6 -= l3 * 2048;
                        final double d7 = p_180447_7_ - i2 * 2048;
                        float f17 = this.theWorld.provider.getCloudHeight() - (float)p_180447_5_ + 0.33f;
                        f17 += this.mc.gameSettings.ofCloudsHeight * 128.0f;
                        final float f18 = (float)(d6 * 4.8828125E-4);
                        final float f19 = (float)(d7 * 4.8828125E-4);
                        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                        for (int j2 = -256; j2 < 256; j2 += 32) {
                            for (int k2 = -256; k2 < 256; k2 += 32) {
                                bufferbuilder.pos(j2 + 0, f17, k2 + 32).tex((j2 + 0) * 4.8828125E-4f + f18, (k2 + 32) * 4.8828125E-4f + f19).color(f10, f11, f12, 0.8f).endVertex();
                                bufferbuilder.pos(j2 + 32, f17, k2 + 32).tex((j2 + 32) * 4.8828125E-4f + f18, (k2 + 32) * 4.8828125E-4f + f19).color(f10, f11, f12, 0.8f).endVertex();
                                bufferbuilder.pos(j2 + 32, f17, k2 + 0).tex((j2 + 32) * 4.8828125E-4f + f18, (k2 + 0) * 4.8828125E-4f + f19).color(f10, f11, f12, 0.8f).endVertex();
                                bufferbuilder.pos(j2 + 0, f17, k2 + 0).tex((j2 + 0) * 4.8828125E-4f + f18, (k2 + 0) * 4.8828125E-4f + f19).color(f10, f11, f12, 0.8f).endVertex();
                            }
                        }
                        tessellator.draw();
                        this.cloudRenderer.endUpdateGlList();
                    }
                    this.cloudRenderer.renderGlList();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GlStateManager.disableBlend();
                    GlStateManager.enableCull();
                }
                if (Config.isShaders()) {
                    Shaders.endClouds();
                }
            }
        }
    }
    
    public boolean hasCloudFog(final double x, final double y, final double z, final float partialTicks) {
        return false;
    }
    
    private void renderCloudsFancy(final float partialTicks, final int pass, final double p_180445_3_, final double p_180445_5_, final double p_180445_7_) {
        final float f251 = 0.0f;
        GlStateManager.disableCull();
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        final float f252 = 12.0f;
        final float f253 = 4.0f;
        final double d3 = this.cloudTickCounter + f251;
        double d4 = (p_180445_3_ + d3 * 0.029999999329447746) / 12.0;
        double d5 = p_180445_7_ / 12.0 + 0.33000001311302185;
        float f254 = this.theWorld.provider.getCloudHeight() - (float)p_180445_5_ + 0.33f;
        f254 += this.mc.gameSettings.ofCloudsHeight * 128.0f;
        final int k1 = MathHelper.floor(d4 / 2048.0);
        final int l1 = MathHelper.floor(d5 / 2048.0);
        d4 -= k1 * 2048;
        d5 -= l1 * 2048;
        this.renderEngine.bindTexture(RenderGlobal.CLOUDS_TEXTURES);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final Vec3d vec3d = this.theWorld.getCloudColour(f251);
        float f255 = (float)vec3d.xCoord;
        float f256 = (float)vec3d.yCoord;
        float f257 = (float)vec3d.zCoord;
        this.cloudRenderer.prepareToRender(true, this.cloudTickCounter, partialTicks, vec3d);
        if (pass != 2) {
            final float f258 = (f255 * 30.0f + f256 * 59.0f + f257 * 11.0f) / 100.0f;
            final float f259 = (f255 * 30.0f + f256 * 70.0f) / 100.0f;
            final float f260 = (f255 * 30.0f + f257 * 70.0f) / 100.0f;
            f255 = f258;
            f256 = f259;
            f257 = f260;
        }
        final float f261 = f255 * 0.9f;
        final float f262 = f256 * 0.9f;
        final float f263 = f257 * 0.9f;
        final float f264 = f255 * 0.7f;
        final float f265 = f256 * 0.7f;
        final float f266 = f257 * 0.7f;
        final float f267 = f255 * 0.8f;
        final float f268 = f256 * 0.8f;
        final float f269 = f257 * 0.8f;
        final float f270 = 0.00390625f;
        final float f271 = MathHelper.floor(d4) * 0.00390625f;
        final float f272 = MathHelper.floor(d5) * 0.00390625f;
        final float f273 = (float)(d4 - MathHelper.floor(d4));
        final float f274 = (float)(d5 - MathHelper.floor(d5));
        final int i2 = 8;
        final int j2 = 4;
        final float f275 = 9.765625E-4f;
        GlStateManager.scale(12.0f, 1.0f, 12.0f);
        for (int k2 = 0; k2 < 2; ++k2) {
            if (k2 == 0) {
                GlStateManager.colorMask(false, false, false, false);
            }
            else {
                switch (pass) {
                    case 0: {
                        GlStateManager.colorMask(false, true, true, true);
                        break;
                    }
                    case 1: {
                        GlStateManager.colorMask(true, false, false, true);
                        break;
                    }
                    case 2: {
                        GlStateManager.colorMask(true, true, true, true);
                        break;
                    }
                }
            }
            this.cloudRenderer.renderGlList();
        }
        if (this.cloudRenderer.shouldUpdateGlList()) {
            this.cloudRenderer.startUpdateGlList();
            for (int j3 = -3; j3 <= 4; ++j3) {
                for (int l2 = -3; l2 <= 4; ++l2) {
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                    final float f276 = (float)(j3 * 8);
                    final float f277 = (float)(l2 * 8);
                    final float f278 = f276 - f273;
                    final float f279 = f277 - f274;
                    if (f254 > -5.0f) {
                        bufferbuilder.pos(f278 + 0.0f, f254 + 0.0f, f279 + 8.0f).tex((f276 + 0.0f) * 0.00390625f + f271, (f277 + 8.0f) * 0.00390625f + f272).color(f264, f265, f266, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f278 + 8.0f, f254 + 0.0f, f279 + 8.0f).tex((f276 + 8.0f) * 0.00390625f + f271, (f277 + 8.0f) * 0.00390625f + f272).color(f264, f265, f266, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f278 + 8.0f, f254 + 0.0f, f279 + 0.0f).tex((f276 + 8.0f) * 0.00390625f + f271, (f277 + 0.0f) * 0.00390625f + f272).color(f264, f265, f266, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f278 + 0.0f, f254 + 0.0f, f279 + 0.0f).tex((f276 + 0.0f) * 0.00390625f + f271, (f277 + 0.0f) * 0.00390625f + f272).color(f264, f265, f266, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    }
                    if (f254 <= 5.0f) {
                        bufferbuilder.pos(f278 + 0.0f, f254 + 4.0f - 9.765625E-4f, f279 + 8.0f).tex((f276 + 0.0f) * 0.00390625f + f271, (f277 + 8.0f) * 0.00390625f + f272).color(f255, f256, f257, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f278 + 8.0f, f254 + 4.0f - 9.765625E-4f, f279 + 8.0f).tex((f276 + 8.0f) * 0.00390625f + f271, (f277 + 8.0f) * 0.00390625f + f272).color(f255, f256, f257, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f278 + 8.0f, f254 + 4.0f - 9.765625E-4f, f279 + 0.0f).tex((f276 + 8.0f) * 0.00390625f + f271, (f277 + 0.0f) * 0.00390625f + f272).color(f255, f256, f257, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        bufferbuilder.pos(f278 + 0.0f, f254 + 4.0f - 9.765625E-4f, f279 + 0.0f).tex((f276 + 0.0f) * 0.00390625f + f271, (f277 + 0.0f) * 0.00390625f + f272).color(f255, f256, f257, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                    }
                    if (j3 > -1) {
                        for (int i3 = 0; i3 < 8; ++i3) {
                            bufferbuilder.pos(f278 + i3 + 0.0f, f254 + 0.0f, f279 + 8.0f).tex((f276 + i3 + 0.5f) * 0.00390625f + f271, (f277 + 8.0f) * 0.00390625f + f272).color(f261, f262, f263, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f278 + i3 + 0.0f, f254 + 4.0f, f279 + 8.0f).tex((f276 + i3 + 0.5f) * 0.00390625f + f271, (f277 + 8.0f) * 0.00390625f + f272).color(f261, f262, f263, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f278 + i3 + 0.0f, f254 + 4.0f, f279 + 0.0f).tex((f276 + i3 + 0.5f) * 0.00390625f + f271, (f277 + 0.0f) * 0.00390625f + f272).color(f261, f262, f263, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f278 + i3 + 0.0f, f254 + 0.0f, f279 + 0.0f).tex((f276 + i3 + 0.5f) * 0.00390625f + f271, (f277 + 0.0f) * 0.00390625f + f272).color(f261, f262, f263, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                        }
                    }
                    if (j3 <= 1) {
                        for (int k3 = 0; k3 < 8; ++k3) {
                            bufferbuilder.pos(f278 + k3 + 1.0f - 9.765625E-4f, f254 + 0.0f, f279 + 8.0f).tex((f276 + k3 + 0.5f) * 0.00390625f + f271, (f277 + 8.0f) * 0.00390625f + f272).color(f261, f262, f263, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f278 + k3 + 1.0f - 9.765625E-4f, f254 + 4.0f, f279 + 8.0f).tex((f276 + k3 + 0.5f) * 0.00390625f + f271, (f277 + 8.0f) * 0.00390625f + f272).color(f261, f262, f263, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f278 + k3 + 1.0f - 9.765625E-4f, f254 + 4.0f, f279 + 0.0f).tex((f276 + k3 + 0.5f) * 0.00390625f + f271, (f277 + 0.0f) * 0.00390625f + f272).color(f261, f262, f263, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            bufferbuilder.pos(f278 + k3 + 1.0f - 9.765625E-4f, f254 + 0.0f, f279 + 0.0f).tex((f276 + k3 + 0.5f) * 0.00390625f + f271, (f277 + 0.0f) * 0.00390625f + f272).color(f261, f262, f263, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                        }
                    }
                    if (l2 > -1) {
                        for (int l3 = 0; l3 < 8; ++l3) {
                            bufferbuilder.pos(f278 + 0.0f, f254 + 4.0f, f279 + l3 + 0.0f).tex((f276 + 0.0f) * 0.00390625f + f271, (f277 + l3 + 0.5f) * 0.00390625f + f272).color(f267, f268, f269, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            bufferbuilder.pos(f278 + 8.0f, f254 + 4.0f, f279 + l3 + 0.0f).tex((f276 + 8.0f) * 0.00390625f + f271, (f277 + l3 + 0.5f) * 0.00390625f + f272).color(f267, f268, f269, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            bufferbuilder.pos(f278 + 8.0f, f254 + 0.0f, f279 + l3 + 0.0f).tex((f276 + 8.0f) * 0.00390625f + f271, (f277 + l3 + 0.5f) * 0.00390625f + f272).color(f267, f268, f269, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            bufferbuilder.pos(f278 + 0.0f, f254 + 0.0f, f279 + l3 + 0.0f).tex((f276 + 0.0f) * 0.00390625f + f271, (f277 + l3 + 0.5f) * 0.00390625f + f272).color(f267, f268, f269, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                        }
                    }
                    if (l2 <= 1) {
                        for (int i4 = 0; i4 < 8; ++i4) {
                            bufferbuilder.pos(f278 + 0.0f, f254 + 4.0f, f279 + i4 + 1.0f - 9.765625E-4f).tex((f276 + 0.0f) * 0.00390625f + f271, (f277 + i4 + 0.5f) * 0.00390625f + f272).color(f267, f268, f269, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                            bufferbuilder.pos(f278 + 8.0f, f254 + 4.0f, f279 + i4 + 1.0f - 9.765625E-4f).tex((f276 + 8.0f) * 0.00390625f + f271, (f277 + i4 + 0.5f) * 0.00390625f + f272).color(f267, f268, f269, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                            bufferbuilder.pos(f278 + 8.0f, f254 + 0.0f, f279 + i4 + 1.0f - 9.765625E-4f).tex((f276 + 8.0f) * 0.00390625f + f271, (f277 + i4 + 0.5f) * 0.00390625f + f272).color(f267, f268, f269, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                            bufferbuilder.pos(f278 + 0.0f, f254 + 0.0f, f279 + i4 + 1.0f - 9.765625E-4f).tex((f276 + 0.0f) * 0.00390625f + f271, (f277 + i4 + 0.5f) * 0.00390625f + f272).color(f267, f268, f269, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                        }
                    }
                    tessellator.draw();
                }
            }
            this.cloudRenderer.endUpdateGlList();
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }
    
    public void updateChunks(long finishTimeNano) {
        finishTimeNano += (long)1.0E8;
        this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads(finishTimeNano);
        if (this.chunksToUpdateForced.size() > 0) {
            final Iterator iterator = this.chunksToUpdateForced.iterator();
            while (iterator.hasNext()) {
                final RenderChunk renderchunk1 = iterator.next();
                if (!this.renderDispatcher.updateChunkLater(renderchunk1)) {
                    break;
                }
                renderchunk1.clearNeedsUpdate();
                iterator.remove();
                this.chunksToUpdate.remove(renderchunk1);
                this.chunksToResortTransparency.remove(renderchunk1);
            }
        }
        if (this.chunksToResortTransparency.size() > 0) {
            final Iterator iterator2 = this.chunksToResortTransparency.iterator();
            if (iterator2.hasNext()) {
                final RenderChunk renderchunk2 = iterator2.next();
                if (this.renderDispatcher.updateTransparencyLater(renderchunk2)) {
                    iterator2.remove();
                }
            }
        }
        int l1 = 0;
        int i2 = Config.getUpdatesPerFrame();
        final int k1 = i2 * 2;
        if (!this.chunksToUpdate.isEmpty()) {
            final Iterator<RenderChunk> iterator3 = this.chunksToUpdate.iterator();
            while (iterator3.hasNext()) {
                final RenderChunk renderchunk3 = iterator3.next();
                boolean flag1;
                if (renderchunk3.isNeedsUpdateCustom()) {
                    flag1 = this.renderDispatcher.updateChunkNow(renderchunk3);
                }
                else {
                    flag1 = this.renderDispatcher.updateChunkLater(renderchunk3);
                }
                if (!flag1) {
                    break;
                }
                renderchunk3.clearNeedsUpdate();
                iterator3.remove();
                if (renderchunk3.getCompiledChunk().isEmpty() && i2 < k1) {
                    ++i2;
                }
                if (++l1 >= i2) {
                    break;
                }
            }
        }
    }
    
    public void renderWorldBorder(final Entity entityIn, final float partialTicks) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        final WorldBorder worldborder = this.theWorld.getWorldBorder();
        final double d3 = this.mc.gameSettings.renderDistanceChunks * 16;
        if (entityIn.posX >= worldborder.maxX() - d3 || entityIn.posX <= worldborder.minX() + d3 || entityIn.posZ >= worldborder.maxZ() - d3 || entityIn.posZ <= worldborder.minZ() + d3) {
            double d4 = 1.0 - worldborder.getClosestDistance(entityIn) / d3;
            d4 = Math.pow(d4, 4.0);
            final double d5 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
            final double d6 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
            final double d7 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.renderEngine.bindTexture(RenderGlobal.FORCEFIELD_TEXTURES);
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            final int k1 = worldborder.getStatus().getID();
            final float f = (k1 >> 16 & 0xFF) / 255.0f;
            final float f2 = (k1 >> 8 & 0xFF) / 255.0f;
            final float f3 = (k1 & 0xFF) / 255.0f;
            GlStateManager.color(f, f2, f3, (float)d4);
            GlStateManager.doPolygonOffset(-3.0f, -3.0f);
            GlStateManager.enablePolygonOffset();
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.enableAlpha();
            GlStateManager.disableCull();
            final float f4 = Minecraft.getSystemTime() % 3000L / 3000.0f;
            final float f5 = 0.0f;
            final float f6 = 0.0f;
            final float f7 = 128.0f;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.setTranslation(-d5, -d6, -d7);
            double d8 = Math.max(MathHelper.floor(d7 - d3), worldborder.minZ());
            double d9 = Math.min(MathHelper.ceil(d7 + d3), worldborder.maxZ());
            if (d5 > worldborder.maxX() - d3) {
                float f8 = 0.0f;
                for (double d10 = d8; d10 < d9; ++d10, f8 += 0.5f) {
                    final double d11 = Math.min(1.0, d9 - d10);
                    final float f9 = (float)d11 * 0.5f;
                    bufferbuilder.pos(worldborder.maxX(), 256.0, d10).tex(f4 + f8, f4 + 0.0f).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 256.0, d10 + d11).tex(f4 + f9 + f8, f4 + 0.0f).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 0.0, d10 + d11).tex(f4 + f9 + f8, f4 + 128.0f).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 0.0, d10).tex(f4 + f8, f4 + 128.0f).endVertex();
                }
            }
            if (d5 < worldborder.minX() + d3) {
                float f10 = 0.0f;
                for (double d12 = d8; d12 < d9; ++d12, f10 += 0.5f) {
                    final double d13 = Math.min(1.0, d9 - d12);
                    final float f11 = (float)d13 * 0.5f;
                    bufferbuilder.pos(worldborder.minX(), 256.0, d12).tex(f4 + f10, f4 + 0.0f).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 256.0, d12 + d13).tex(f4 + f11 + f10, f4 + 0.0f).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 0.0, d12 + d13).tex(f4 + f11 + f10, f4 + 128.0f).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 0.0, d12).tex(f4 + f10, f4 + 128.0f).endVertex();
                }
            }
            d8 = Math.max(MathHelper.floor(d5 - d3), worldborder.minX());
            d9 = Math.min(MathHelper.ceil(d5 + d3), worldborder.maxX());
            if (d7 > worldborder.maxZ() - d3) {
                float f12 = 0.0f;
                for (double d14 = d8; d14 < d9; ++d14, f12 += 0.5f) {
                    final double d15 = Math.min(1.0, d9 - d14);
                    final float f13 = (float)d15 * 0.5f;
                    bufferbuilder.pos(d14, 256.0, worldborder.maxZ()).tex(f4 + f12, f4 + 0.0f).endVertex();
                    bufferbuilder.pos(d14 + d15, 256.0, worldborder.maxZ()).tex(f4 + f13 + f12, f4 + 0.0f).endVertex();
                    bufferbuilder.pos(d14 + d15, 0.0, worldborder.maxZ()).tex(f4 + f13 + f12, f4 + 128.0f).endVertex();
                    bufferbuilder.pos(d14, 0.0, worldborder.maxZ()).tex(f4 + f12, f4 + 128.0f).endVertex();
                }
            }
            if (d7 < worldborder.minZ() + d3) {
                float f14 = 0.0f;
                for (double d16 = d8; d16 < d9; ++d16, f14 += 0.5f) {
                    final double d17 = Math.min(1.0, d9 - d16);
                    final float f15 = (float)d17 * 0.5f;
                    bufferbuilder.pos(d16, 256.0, worldborder.minZ()).tex(f4 + f14, f4 + 0.0f).endVertex();
                    bufferbuilder.pos(d16 + d17, 256.0, worldborder.minZ()).tex(f4 + f15 + f14, f4 + 0.0f).endVertex();
                    bufferbuilder.pos(d16 + d17, 0.0, worldborder.minZ()).tex(f4 + f15 + f14, f4 + 128.0f).endVertex();
                    bufferbuilder.pos(d16, 0.0, worldborder.minZ()).tex(f4 + f14, f4 + 128.0f).endVertex();
                }
            }
            tessellator.draw();
            bufferbuilder.setTranslation(0.0, 0.0, 0.0);
            GlStateManager.enableCull();
            GlStateManager.disableAlpha();
            GlStateManager.doPolygonOffset(0.0f, 0.0f);
            GlStateManager.disablePolygonOffset();
            GlStateManager.enableAlpha();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
        }
    }
    
    private void preRenderDamagedBlocks() {
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
        GlStateManager.doPolygonOffset(-3.0f, -3.0f);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        if (Config.isShaders()) {
            ShadersRender.beginBlockDamage();
        }
    }
    
    private void postRenderDamagedBlocks() {
        GlStateManager.disableAlpha();
        GlStateManager.doPolygonOffset(0.0f, 0.0f);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
        if (Config.isShaders()) {
            ShadersRender.endBlockDamage();
        }
    }
    
    public void drawBlockDamageTexture(final Tessellator tessellatorIn, final BufferBuilder worldRendererIn, final Entity entityIn, final float partialTicks) {
        final double d3 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
        final double d4 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
        final double d5 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
        if (!this.damagedBlocks.isEmpty()) {
            this.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.preRenderDamagedBlocks();
            worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
            worldRendererIn.setTranslation(-d3, -d4, -d5);
            worldRendererIn.noColor();
            final Iterator<DestroyBlockProgress> iterator = this.damagedBlocks.values().iterator();
            while (iterator.hasNext()) {
                final DestroyBlockProgress destroyblockprogress = iterator.next();
                final BlockPos blockpos = destroyblockprogress.getPosition();
                final double d6 = blockpos.getX() - d3;
                final double d7 = blockpos.getY() - d4;
                final double d8 = blockpos.getZ() - d5;
                final Block block = this.theWorld.getBlockState(blockpos).getBlock();
                boolean flag3;
                if (Reflector.ForgeTileEntity_canRenderBreaking.exists()) {
                    boolean flag2 = block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull;
                    if (!flag2) {
                        final TileEntity tileentity = this.theWorld.getTileEntity(blockpos);
                        if (tileentity != null) {
                            flag2 = Reflector.callBoolean(tileentity, Reflector.ForgeTileEntity_canRenderBreaking, new Object[0]);
                        }
                    }
                    flag3 = !flag2;
                }
                else {
                    flag3 = (!(block instanceof BlockChest) && !(block instanceof BlockEnderChest) && !(block instanceof BlockSign) && !(block instanceof BlockSkull));
                }
                if (flag3) {
                    if (d6 * d6 + d7 * d7 + d8 * d8 > 1024.0) {
                        iterator.remove();
                    }
                    else {
                        final IBlockState iblockstate = this.theWorld.getBlockState(blockpos);
                        if (iblockstate.getMaterial() == Material.AIR) {
                            continue;
                        }
                        final int k1 = destroyblockprogress.getPartialBlockDamage();
                        final TextureAtlasSprite textureatlassprite = this.destroyBlockIcons[k1];
                        final BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
                        blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, this.theWorld);
                    }
                }
            }
            tessellatorIn.draw();
            worldRendererIn.setTranslation(0.0, 0.0, 0.0);
            this.postRenderDamagedBlocks();
        }
    }
    
    public void drawSelectionBox(final EntityPlayer player, final RayTraceResult movingObjectPositionIn, final int execute, final float partialTicks) {
        if (execute == 0 && movingObjectPositionIn.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            final int rainbow = Math.abs(Color.HSBtoRGB(System.currentTimeMillis() % 2500L / 2500.0f, 0.8f, 0.8f));
            final float red = (rainbow >> 16 & 0xFF) / 255.0f;
            final float green = (rainbow >> 8 & 0xFF) / 255.0f;
            final float blue = (rainbow & 0xFF) / 255.0f;
            final float r = 0.0f;
            final float g = 0.0f;
            final float b = 0.0f;
            GlStateManager.color(red, green, blue);
            GlStateManager.glLineWidth(5.0f);
            GlStateManager.disableTexture2D();
            if (Config.isShaders()) {
                Shaders.disableTexture2D();
            }
            GlStateManager.depthMask(false);
            final BlockPos blockpos = movingObjectPositionIn.getBlockPos();
            final IBlockState iblockstate = this.theWorld.getBlockState(blockpos);
            if (iblockstate.getMaterial() != Material.AIR && this.theWorld.getWorldBorder().contains(blockpos)) {
                final double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
                final double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
                final double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
                if (GuiClientUI.blockoverlay) {
                    func_181561_a(iblockstate.getSelectedBoundingBox(this.theWorld, blockpos).expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026).offset(-d3, -d4, -d5));
                }
                else {
                    drawSelectionBoundingBox(iblockstate.getSelectedBoundingBox(this.theWorld, blockpos).expandXyz(0.0020000000949949026).offset(-d3, -d4, -d5), 1.0f, 0.0f, 0.0f, 0.5f);
                }
            }
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            if (Config.isShaders()) {
                Shaders.enableTexture2D();
            }
            GlStateManager.disableBlend();
        }
    }
    
    public static void func_181561_a(final AxisAlignedBB p_181561_0_) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(1, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();
    }
    
    public static void drawSelectionBoundingBox(final AxisAlignedBB box, final float red, final float green, final float blue, final float alpha) {
        drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }
    
    public static void drawBoundingBox(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final float red, final float green, final float blue, final float alpha) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }
    
    public static void drawBoundingBox(final BufferBuilder buffer, final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final float red, final float green, final float blue, final float alpha) {
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, 0.0f).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, 0.0f).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, 0.0f).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
    }
    
    public static void renderFilledBox(final AxisAlignedBB p_189696_0_, final float p_189696_1_, final float p_189696_2_, final float p_189696_3_, final float p_189696_4_) {
        renderFilledBox(p_189696_0_.minX, p_189696_0_.minY, p_189696_0_.minZ, p_189696_0_.maxX, p_189696_0_.maxY, p_189696_0_.maxZ, p_189696_1_, p_189696_2_, p_189696_3_, p_189696_4_);
    }
    
    public static void renderFilledBox(final double p_189695_0_, final double p_189695_2_, final double p_189695_4_, final double p_189695_6_, final double p_189695_8_, final double p_189695_10_, final float p_189695_12_, final float p_189695_13_, final float p_189695_14_, final float p_189695_15_) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        addChainedFilledBoxVertices(bufferbuilder, p_189695_0_, p_189695_2_, p_189695_4_, p_189695_6_, p_189695_8_, p_189695_10_, p_189695_12_, p_189695_13_, p_189695_14_, p_189695_15_);
        tessellator.draw();
    }
    
    public static void addChainedFilledBoxVertices(final BufferBuilder p_189693_0_, final double p_189693_1_, final double p_189693_3_, final double p_189693_5_, final double p_189693_7_, final double p_189693_9_, final double p_189693_11_, final float p_189693_13_, final float p_189693_14_, final float p_189693_15_, final float p_189693_16_) {
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
    }
    
    private void markBlocksForUpdate(final int p_184385_1_, final int p_184385_2_, final int p_184385_3_, final int p_184385_4_, final int p_184385_5_, final int p_184385_6_, final boolean p_184385_7_) {
        this.viewFrustum.markBlocksForUpdate(p_184385_1_, p_184385_2_, p_184385_3_, p_184385_4_, p_184385_5_, p_184385_6_, p_184385_7_);
    }
    
    @Override
    public void notifyBlockUpdate(final World worldIn, final BlockPos pos, final IBlockState oldState, final IBlockState newState, final int flags) {
        final int k1 = pos.getX();
        final int l1 = pos.getY();
        final int i2 = pos.getZ();
        this.markBlocksForUpdate(k1 - 1, l1 - 1, i2 - 1, k1 + 1, l1 + 1, i2 + 1, (flags & 0x8) != 0x0);
    }
    
    @Override
    public void notifyLightSet(final BlockPos pos) {
        this.setLightUpdates.add(pos.toImmutable());
    }
    
    @Override
    public void markBlockRangeForRenderUpdate(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        this.markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1, false);
    }
    
    @Override
    public void playRecord(@Nullable final SoundEvent soundIn, final BlockPos pos) {
        final ISound isound = this.mapSoundPositions.get(pos);
        if (isound != null) {
            this.mc.getSoundHandler().stopSound(isound);
            this.mapSoundPositions.remove(pos);
        }
        if (soundIn != null) {
            final ItemRecord itemrecord = ItemRecord.getBySound(soundIn);
            if (itemrecord != null) {
                this.mc.ingameGUI.setRecordPlayingMessage(itemrecord.getRecordNameLocal());
            }
            final ISound isound2 = PositionedSoundRecord.getRecordSoundRecord(soundIn, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
            this.mapSoundPositions.put(pos, isound2);
            this.mc.getSoundHandler().playSound(isound2);
        }
        this.func_193054_a(this.theWorld, pos, soundIn != null);
    }
    
    private void func_193054_a(final World p_193054_1_, final BlockPos p_193054_2_, final boolean p_193054_3_) {
        for (final EntityLivingBase entitylivingbase : p_193054_1_.getEntitiesWithinAABB((Class<? extends EntityLivingBase>)EntityLivingBase.class, new AxisAlignedBB(p_193054_2_).expandXyz(3.0))) {
            entitylivingbase.func_191987_a(p_193054_2_, p_193054_3_);
        }
    }
    
    @Override
    public void playSoundToAllNearExcept(@Nullable final EntityPlayer player, final SoundEvent soundIn, final SoundCategory category, final double x, final double y, final double z, final float volume, final float pitch) {
    }
    
    @Override
    public void spawnParticle(final int particleID, final boolean ignoreRange, final double xCoord, final double yCoord, final double zCoord, final double xSpeed, final double ySpeed, final double zSpeed, final int... parameters) {
        this.func_190570_a(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }
    
    @Override
    public void func_190570_a(final int p_190570_1_, final boolean p_190570_2_, final boolean p_190570_3_, final double p_190570_4_, final double p_190570_6_, final double p_190570_8_, final double p_190570_10_, final double p_190570_12_, final double p_190570_14_, final int... p_190570_16_) {
        try {
            this.func_190571_b(p_190570_1_, p_190570_2_, p_190570_3_, p_190570_4_, p_190570_6_, p_190570_8_, p_190570_10_, p_190570_12_, p_190570_14_, p_190570_16_);
        }
        catch (final Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
            crashreportcategory.addCrashSection("ID", p_190570_1_);
            if (p_190570_16_ != null) {
                crashreportcategory.addCrashSection("Parameters", p_190570_16_);
            }
            crashreportcategory.setDetail("Position", new ICrashReportDetail<String>() {
                @Override
                public String call() throws Exception {
                    return CrashReportCategory.getCoordinateInfo(p_190570_4_, p_190570_6_, p_190570_8_);
                }
            });
            throw new ReportedException(crashreport);
        }
    }
    
    private void spawnParticle(final EnumParticleTypes particleIn, final double xCoord, final double yCoord, final double zCoord, final double xSpeed, final double ySpeed, final double zSpeed, final int... parameters) {
        this.spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }
    
    @Nullable
    private Particle spawnEntityFX(final int particleID, final boolean ignoreRange, final double xCoord, final double yCoord, final double zCoord, final double xSpeed, final double ySpeed, final double zSpeed, final int... parameters) {
        return this.func_190571_b(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }
    
    @Nullable
    private Particle func_190571_b(final int p_190571_1_, final boolean p_190571_2_, final boolean p_190571_3_, final double p_190571_4_, final double p_190571_6_, final double p_190571_8_, final double p_190571_10_, final double p_190571_12_, final double p_190571_14_, final int... p_190571_16_) {
        final Entity entity = this.mc.getRenderViewEntity();
        if (this.mc == null || entity == null || this.mc.effectRenderer == null) {
            return null;
        }
        final int k1 = this.func_190572_a(p_190571_3_);
        final double d3 = entity.posX - p_190571_4_;
        final double d4 = entity.posY - p_190571_6_;
        final double d5 = entity.posZ - p_190571_8_;
        if (p_190571_1_ == EnumParticleTypes.EXPLOSION_HUGE.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.EXPLOSION_LARGE.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.EXPLOSION_NORMAL.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.SUSPENDED.getParticleID() && !Config.isWaterParticles()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.SUSPENDED_DEPTH.getParticleID() && !Config.isVoidParticles()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.SMOKE_NORMAL.getParticleID() && !Config.isAnimatedSmoke()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.SMOKE_LARGE.getParticleID() && !Config.isAnimatedSmoke()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.SPELL_MOB.getParticleID() && !Config.isPotionParticles()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID() && !Config.isPotionParticles()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.SPELL.getParticleID() && !Config.isPotionParticles()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.SPELL_INSTANT.getParticleID() && !Config.isPotionParticles()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.SPELL_WITCH.getParticleID() && !Config.isPotionParticles()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.PORTAL.getParticleID() && !Config.isAnimatedPortal()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.FLAME.getParticleID() && !Config.isAnimatedFlame()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.REDSTONE.getParticleID() && !Config.isAnimatedRedstone()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.DRIP_WATER.getParticleID() && !Config.isDrippingWaterLava()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.DRIP_LAVA.getParticleID() && !Config.isDrippingWaterLava()) {
            return null;
        }
        if (p_190571_1_ == EnumParticleTypes.FIREWORKS_SPARK.getParticleID() && !Config.isFireworkParticles()) {
            return null;
        }
        if (!p_190571_2_) {
            double d6 = 1024.0;
            if (p_190571_1_ == EnumParticleTypes.CRIT.getParticleID()) {
                d6 = 38416.0;
            }
            if (d3 * d3 + d4 * d4 + d5 * d5 > d6) {
                return null;
            }
            if (k1 > 1) {
                return null;
            }
        }
        final Particle particle = this.mc.effectRenderer.spawnEffectParticle(p_190571_1_, p_190571_4_, p_190571_6_, p_190571_8_, p_190571_10_, p_190571_12_, p_190571_14_, p_190571_16_);
        if (p_190571_1_ == EnumParticleTypes.WATER_BUBBLE.getParticleID()) {
            CustomColors.updateWaterFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_, this.renderEnv);
        }
        if (p_190571_1_ == EnumParticleTypes.WATER_SPLASH.getParticleID()) {
            CustomColors.updateWaterFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_, this.renderEnv);
        }
        if (p_190571_1_ == EnumParticleTypes.WATER_DROP.getParticleID()) {
            CustomColors.updateWaterFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_, this.renderEnv);
        }
        if (p_190571_1_ == EnumParticleTypes.TOWN_AURA.getParticleID()) {
            CustomColors.updateMyceliumFX(particle);
        }
        if (p_190571_1_ == EnumParticleTypes.PORTAL.getParticleID()) {
            CustomColors.updatePortalFX(particle);
        }
        if (p_190571_1_ == EnumParticleTypes.REDSTONE.getParticleID()) {
            CustomColors.updateReddustFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_);
        }
        return particle;
    }
    
    private int func_190572_a(final boolean p_190572_1_) {
        int k1 = this.mc.gameSettings.particleSetting;
        if (p_190572_1_ && k1 == 2 && this.theWorld.rand.nextInt(10) == 0) {
            k1 = 1;
        }
        if (k1 == 1 && this.theWorld.rand.nextInt(3) == 0) {
            k1 = 2;
        }
        return k1;
    }
    
    @Override
    public void onEntityAdded(final Entity entityIn) {
        RandomMobs.entityLoaded(entityIn, this.theWorld);
        if (Config.isDynamicLights()) {
            DynamicLights.entityAdded(entityIn, this);
        }
    }
    
    @Override
    public void onEntityRemoved(final Entity entityIn) {
        if (Config.isDynamicLights()) {
            DynamicLights.entityRemoved(entityIn, this);
        }
    }
    
    public void deleteAllDisplayLists() {
    }
    
    @Override
    public void broadcastSound(final int soundID, final BlockPos pos, final int data) {
        switch (soundID) {
            case 1023:
            case 1028:
            case 1038: {
                final Entity entity = this.mc.getRenderViewEntity();
                if (entity == null) {
                    break;
                }
                final double d3 = pos.getX() - entity.posX;
                final double d4 = pos.getY() - entity.posY;
                final double d5 = pos.getZ() - entity.posZ;
                final double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                double d7 = entity.posX;
                double d8 = entity.posY;
                double d9 = entity.posZ;
                if (d6 > 0.0) {
                    d7 += d3 / d6 * 2.0;
                    d8 += d4 / d6 * 2.0;
                    d9 += d5 / d6 * 2.0;
                }
                if (soundID == 1023) {
                    this.theWorld.playSound(d7, d8, d9, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if (soundID == 1038) {
                    this.theWorld.playSound(d7, d8, d9, SoundEvents.field_193782_bq, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.theWorld.playSound(d7, d8, d9, SoundEvents.ENTITY_ENDERDRAGON_DEATH, SoundCategory.HOSTILE, 5.0f, 1.0f, false);
                break;
            }
        }
    }
    
    @Override
    public void playEvent(final EntityPlayer player, final int type, final BlockPos blockPosIn, final int data) {
        final Random random = this.theWorld.rand;
        switch (type) {
            case 1000: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1001: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1003: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDEREYE_LAUNCH, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1004: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_FIREWORK_SHOOT, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1005: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1006: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1007: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1008: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1009: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                break;
            }
            case 1010: {
                if (Item.getItemById(data) instanceof ItemRecord) {
                    this.theWorld.playRecord(blockPosIn, ((ItemRecord)Item.getItemById(data)).getSound());
                    break;
                }
                this.theWorld.playRecord(blockPosIn, null);
                break;
            }
            case 1011: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1012: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1013: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1014: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1015: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1029: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1032: {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4f + 0.8f));
                break;
            }
            case 1033: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1035: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1036: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1037: {
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2000: {
                final int k1 = data % 3 - 1;
                final int l1 = data / 3 % 3 - 1;
                final double d3 = blockPosIn.getX() + k1 * 0.6 + 0.5;
                final double d4 = blockPosIn.getY() + 0.5;
                final double d5 = blockPosIn.getZ() + l1 * 0.6 + 0.5;
                for (int k2 = 0; k2 < 10; ++k2) {
                    final double d6 = random.nextDouble() * 0.2 + 0.01;
                    final double d7 = d3 + k1 * 0.01 + (random.nextDouble() - 0.5) * l1 * 0.5;
                    final double d8 = d4 + (random.nextDouble() - 0.5) * 0.5;
                    final double d9 = d5 + l1 * 0.01 + (random.nextDouble() - 0.5) * k1 * 0.5;
                    final double d10 = k1 * d6 + random.nextGaussian() * 0.01;
                    final double d11 = -0.03 + random.nextGaussian() * 0.01;
                    final double d12 = l1 * d6 + random.nextGaussian() * 0.01;
                    this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d7, d8, d9, d10, d11, d12, new int[0]);
                }
                return;
            }
            case 2001: {
                final Block block = Block.getBlockById(data & 0xFFF);
                if (block.getDefaultState().getMaterial() != Material.AIR) {
                    SoundType soundtype = block.getSoundType();
                    if (Reflector.ForgeBlock_getSoundType.exists()) {
                        soundtype = (SoundType)Reflector.call(block, Reflector.ForgeBlock_getSoundType, Block.getStateById(data), this.theWorld, blockPosIn, null);
                    }
                    this.theWorld.playSound(blockPosIn, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f, false);
                }
                this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(data >> 12 & 0xFF));
                break;
            }
            case 2002:
            case 2007: {
                final double d13 = blockPosIn.getX();
                final double d14 = blockPosIn.getY();
                final double d15 = blockPosIn.getZ();
                for (int i2 = 0; i2 < 8; ++i2) {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d13, d14, d15, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15, Item.getIdFromItem(Items.SPLASH_POTION));
                }
                final float f5 = (data >> 16 & 0xFF) / 255.0f;
                final float f6 = (data >> 8 & 0xFF) / 255.0f;
                final float f7 = (data >> 0 & 0xFF) / 255.0f;
                final EnumParticleTypes enumparticletypes = (type == 2007) ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SPELL;
                for (int l2 = 0; l2 < 100; ++l2) {
                    final double d16 = random.nextDouble() * 4.0;
                    final double d17 = random.nextDouble() * 3.141592653589793 * 2.0;
                    final double d18 = Math.cos(d17) * d16;
                    final double d19 = 0.01 + random.nextDouble() * 0.5;
                    final double d20 = Math.sin(d17) * d16;
                    final Particle particle1 = this.spawnEntityFX(enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d13 + d18 * 0.1, d14 + 0.3, d15 + d20 * 0.1, d18, d19, d20, new int[0]);
                    if (particle1 != null) {
                        final float f8 = 0.75f + random.nextFloat() * 0.25f;
                        particle1.setRBGColorF(f5 * f8, f6 * f8, f7 * f8);
                        particle1.multiplyVelocity((float)d16);
                    }
                }
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2003: {
                final double d21 = blockPosIn.getX() + 0.5;
                final double d22 = blockPosIn.getY();
                final double d23 = blockPosIn.getZ() + 0.5;
                for (int j3 = 0; j3 < 8; ++j3) {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d21, d22, d23, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15, Item.getIdFromItem(Items.ENDER_EYE));
                }
                for (double d24 = 0.0; d24 < 6.283185307179586; d24 += 0.15707963267948966) {
                    this.spawnParticle(EnumParticleTypes.PORTAL, d21 + Math.cos(d24) * 5.0, d22 - 0.4, d23 + Math.sin(d24) * 5.0, Math.cos(d24) * -5.0, 0.0, Math.sin(d24) * -5.0, new int[0]);
                    this.spawnParticle(EnumParticleTypes.PORTAL, d21 + Math.cos(d24) * 5.0, d22 - 0.4, d23 + Math.sin(d24) * 5.0, Math.cos(d24) * -7.0, 0.0, Math.sin(d24) * -7.0, new int[0]);
                }
                return;
            }
            case 2004: {
                for (int i3 = 0; i3 < 20; ++i3) {
                    final double d25 = blockPosIn.getX() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    final double d26 = blockPosIn.getY() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    final double d27 = blockPosIn.getZ() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d25, d26, d27, 0.0, 0.0, 0.0, new int[0]);
                    this.theWorld.spawnParticle(EnumParticleTypes.FLAME, d25, d26, d27, 0.0, 0.0, 0.0, new int[0]);
                }
                return;
            }
            case 2005: {
                ItemDye.spawnBonemealParticles(this.theWorld, blockPosIn, data);
                break;
            }
            case 2006: {
                for (int j4 = 0; j4 < 200; ++j4) {
                    final float f9 = random.nextFloat() * 4.0f;
                    final float f10 = random.nextFloat() * 6.2831855f;
                    final double d28 = MathHelper.cos(f10) * f9;
                    final double d29 = 0.01 + random.nextDouble() * 0.5;
                    final double d30 = MathHelper.sin(f10) * f9;
                    final Particle particle2 = this.spawnEntityFX(EnumParticleTypes.DRAGON_BREATH.getParticleID(), false, blockPosIn.getX() + d28 * 0.1, blockPosIn.getY() + 0.3, blockPosIn.getZ() + d30 * 0.1, d28, d29, d30, new int[0]);
                    if (particle2 != null) {
                        particle2.multiplyVelocity(f9);
                    }
                }
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_FIREBALL_EPLD, SoundCategory.HOSTILE, 1.0f, this.theWorld.rand.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.theWorld.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, true, blockPosIn.getX() + 0.5, blockPosIn.getY() + 0.5, blockPosIn.getZ() + 0.5, 0.0, 0.0, 0.0, new int[0]);
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0f, (1.0f + (this.theWorld.rand.nextFloat() - this.theWorld.rand.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 64.0f, 0.8f + this.theWorld.rand.nextFloat() * 0.3f, false);
                break;
            }
        }
    }
    
    @Override
    public void sendBlockBreakProgress(final int breakerId, final BlockPos pos, final int progress) {
        if (progress >= 0 && progress < 10) {
            DestroyBlockProgress destroyblockprogress = this.damagedBlocks.get(breakerId);
            if (destroyblockprogress == null || destroyblockprogress.getPosition().getX() != pos.getX() || destroyblockprogress.getPosition().getY() != pos.getY() || destroyblockprogress.getPosition().getZ() != pos.getZ()) {
                destroyblockprogress = new DestroyBlockProgress(breakerId, pos);
                this.damagedBlocks.put(breakerId, destroyblockprogress);
            }
            destroyblockprogress.setPartialBlockDamage(progress);
            destroyblockprogress.setCloudUpdateTick(this.cloudTickCounter);
        }
        else {
            this.damagedBlocks.remove(breakerId);
        }
    }
    
    public boolean hasNoChunkUpdates() {
        return this.chunksToUpdate.isEmpty() && this.renderDispatcher.hasChunkUpdates();
    }
    
    public void setDisplayListEntitiesDirty() {
        this.displayListEntitiesDirty = true;
    }
    
    public void resetClouds() {
        this.cloudRenderer.reset();
    }
    
    public int getCountRenderers() {
        return this.viewFrustum.renderChunks.length;
    }
    
    public int getCountActiveRenderers() {
        return this.renderInfos.size();
    }
    
    public int getCountEntitiesRendered() {
        return this.countEntitiesRendered;
    }
    
    public int getCountTileEntitiesRendered() {
        return this.countTileEntitiesRendered;
    }
    
    public int getCountLoadedChunks() {
        if (this.theWorld == null) {
            return 0;
        }
        final IChunkProvider ichunkprovider = this.theWorld.getChunkProvider();
        if (ichunkprovider == null) {
            return 0;
        }
        if (ichunkprovider != this.worldChunkProvider) {
            this.worldChunkProvider = ichunkprovider;
            this.worldChunkProviderMap = (Long2ObjectMap)Reflector.getFieldValue(ichunkprovider, Reflector.ChunkProviderClient_chunkMapping);
        }
        return (this.worldChunkProviderMap == null) ? 0 : this.worldChunkProviderMap.size();
    }
    
    public int getCountChunksToUpdate() {
        return this.chunksToUpdate.size();
    }
    
    public RenderChunk getRenderChunk(final BlockPos p_getRenderChunk_1_) {
        return this.viewFrustum.getRenderChunk(p_getRenderChunk_1_);
    }
    
    public WorldClient getWorld() {
        return this.theWorld;
    }
    
    public void updateTileEntities(final Collection<TileEntity> tileEntitiesToRemove, final Collection<TileEntity> tileEntitiesToAdd) {
        synchronized (this.setTileEntities) {
            this.setTileEntities.removeAll(tileEntitiesToRemove);
            this.setTileEntities.addAll(tileEntitiesToAdd);
            monitorexit(this.setTileEntities);
        }
    }
    
    private ContainerLocalRenderInformation allocateRenderInformation(final RenderChunk p_allocateRenderInformation_1_, final EnumFacing p_allocateRenderInformation_2_, final int p_allocateRenderInformation_3_) {
        ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1;
        if (RenderGlobal.renderInfoCache.isEmpty()) {
            renderglobal$containerlocalrenderinformation1 = new ContainerLocalRenderInformation(p_allocateRenderInformation_1_, p_allocateRenderInformation_2_, p_allocateRenderInformation_3_);
        }
        else {
            renderglobal$containerlocalrenderinformation1 = RenderGlobal.renderInfoCache.pollLast();
            renderglobal$containerlocalrenderinformation1.initialize(p_allocateRenderInformation_1_, p_allocateRenderInformation_2_, p_allocateRenderInformation_3_);
        }
        renderglobal$containerlocalrenderinformation1.cacheable = true;
        return renderglobal$containerlocalrenderinformation1;
    }
    
    private void freeRenderInformation(final ContainerLocalRenderInformation p_freeRenderInformation_1_) {
        if (p_freeRenderInformation_1_.cacheable) {
            RenderGlobal.renderInfoCache.add(p_freeRenderInformation_1_);
        }
    }
    
    public static class ContainerLocalRenderInformation
    {
        RenderChunk renderChunk;
        EnumFacing facing;
        int setFacing;
        boolean cacheable;
        
        public ContainerLocalRenderInformation(final RenderChunk p_i1_1_, final EnumFacing p_i1_2_, final int p_i1_3_) {
            this.cacheable = false;
            this.renderChunk = p_i1_1_;
            this.facing = p_i1_2_;
            this.setFacing = p_i1_3_;
        }
        
        public void setDirection(final byte p_189561_1_, final EnumFacing p_189561_2_) {
            this.setFacing = (this.setFacing | p_189561_1_ | 1 << p_189561_2_.ordinal());
        }
        
        public boolean hasDirection(final EnumFacing p_189560_1_) {
            return (this.setFacing & 1 << p_189560_1_.ordinal()) > 0;
        }
        
        private void initialize(final RenderChunk p_initialize_1_, final EnumFacing p_initialize_2_, final int p_initialize_3_) {
            this.renderChunk = p_initialize_1_;
            this.facing = p_initialize_2_;
            this.setFacing = p_initialize_3_;
        }
    }
}
