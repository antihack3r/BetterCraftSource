/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import com.TominoCZ.FBP.FBP;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mcf.davidee.nbtedit.gui.GuiEditNBTTree;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import de.florianmichael.viamcp.fixes.AttackOrder;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.Manager;
import me.nzxtercode.bettercraft.client.events.ClientTickEvent;
import me.nzxtercode.bettercraft.client.gui.GuiFirstTime;
import me.nzxtercode.bettercraft.client.gui.GuiRShift;
import me.nzxtercode.bettercraft.client.gui.section.GuiMisc;
import me.nzxtercode.bettercraft.client.hud.HUDManager;
import me.nzxtercode.bettercraft.client.misc.creativetabs.TabCreativeOwnItems;
import net.labymod.ingamechat.GuiChatCustom;
import net.lenni0451.eventapi.manager.ASMEventManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.stream.GuiStreamUnavailable;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class Minecraft
implements IThreadListener,
IPlayerUsage {
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean isRunningOnMac = Util.getOSType() == Util.EnumOS.OSX;
    public static byte[] memoryReserve = new byte[0xA00000];
    private static final List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
    private final File fileResourcepacks;
    private final PropertyMap twitchDetails;
    private final PropertyMap profileProperties;
    private ServerData currentServerData;
    public TextureManager renderEngine;
    private static Minecraft theMinecraft;
    public PlayerControllerMP playerController;
    private boolean fullscreen;
    private boolean enableGLErrorChecking = true;
    private boolean hasCrashed;
    private CrashReport crashReporter;
    public int displayWidth;
    public int displayHeight;
    private boolean connectedToRealms = false;
    public Timer timer = new Timer(20.0f);
    private PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getCurrentTimeMillis());
    public WorldClient theWorld;
    public RenderGlobal renderGlobal;
    private RenderManager renderManager;
    private RenderItem renderItem;
    private ItemRenderer itemRenderer;
    public EntityPlayerSP thePlayer;
    private Entity renderViewEntity;
    public Entity pointedEntity;
    public EffectRenderer effectRenderer;
    public static Session session;
    private boolean isGamePaused;
    public FontRenderer fontRendererObj;
    public FontRenderer standardGalacticFontRenderer;
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;
    private int leftClickCounter;
    private int tempDisplayWidth;
    private int tempDisplayHeight;
    private IntegratedServer theIntegratedServer;
    public GuiAchievement guiAchievement;
    public GuiIngame ingameGUI;
    public boolean skipRenderWorld;
    public MovingObjectPosition objectMouseOver;
    public GameSettings gameSettings;
    public MouseHelper mouseHelper;
    public final File mcDataDir;
    private final File fileAssets;
    private final String launchedVersion;
    private final Proxy proxy;
    private ISaveFormat saveLoader;
    public static int debugFPS;
    private int rightClickDelayTimer;
    private String serverName;
    private int serverPort;
    public boolean inGameHasFocus;
    long systemTime = Minecraft.getSystemTime();
    private int joinPlayerCounter;
    public final FrameTimer frameTimer = new FrameTimer();
    long startNanoTime = System.nanoTime();
    private final boolean jvm64bit;
    private final boolean isDemo;
    private NetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;
    public final Profiler mcProfiler = new Profiler();
    private long debugCrashKeyPressTime = -1L;
    private IReloadableResourceManager mcResourceManager;
    private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
    private final List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
    private final DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    private LanguageManager mcLanguageManager;
    private IStream stream;
    private Framebuffer framebufferMc;
    private TextureMap textureMapBlocks;
    private SoundHandler mcSoundHandler;
    private MusicTicker mcMusicTicker;
    private ResourceLocation mojangLogo;
    private final MinecraftSessionService sessionService;
    private SkinManager skinManager;
    private final Queue<FutureTask<?>> scheduledTasks = Queues.newArrayDeque();
    private long field_175615_aJ = 0L;
    private final Thread mcThread = Thread.currentThread();
    private ModelManager modelManager;
    private BlockRendererDispatcher blockRenderDispatcher;
    volatile boolean running = true;
    public String debug = "";
    public boolean field_175613_B = false;
    public boolean field_175614_C = false;
    public boolean field_175611_D = false;
    public boolean renderChunksMany = true;
    long debugUpdateTime = Minecraft.getSystemTime();
    int fpsCounter;
    long prevFrameTime = -1L;
    private String debugProfilerName = "root";

    public Minecraft(GameConfiguration gameConfig) {
        theMinecraft = this;
        this.mcDataDir = gameConfig.folderInfo.mcDataDir;
        this.fileAssets = gameConfig.folderInfo.assetsDir;
        this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
        this.launchedVersion = gameConfig.gameInfo.version;
        this.twitchDetails = gameConfig.userInfo.userProperties;
        this.profileProperties = gameConfig.userInfo.profileProperties;
        this.mcDefaultResourcePack = new DefaultResourcePack(new ResourceIndex(gameConfig.folderInfo.assetsDir, gameConfig.folderInfo.assetIndex).getResourceMap());
        this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
        this.sessionService = new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
        session = gameConfig.userInfo.session;
        logger.info("Setting user: " + session.getUsername() + " | User ID is: " + session.getPlayerID());
        StringBuilder stringBuilder = new StringBuilder("Session ID is: ");
        BetterCraft.getInstance();
        logger.info(stringBuilder.append(BetterCraft.seeToken ? session.getSessionID() : "#").toString());
        this.isDemo = gameConfig.gameInfo.isDemo;
        this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
        this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
        this.tempDisplayWidth = gameConfig.displayInfo.width;
        this.tempDisplayHeight = gameConfig.displayInfo.height;
        this.fullscreen = gameConfig.displayInfo.fullscreen;
        this.jvm64bit = Minecraft.isJvm64bit();
        this.theIntegratedServer = new IntegratedServer(this);
        if (gameConfig.serverInfo.serverName != null) {
            this.serverName = gameConfig.serverInfo.serverName;
            this.serverPort = gameConfig.serverInfo.serverPort;
        }
        ImageIO.setUseCache(false);
        Bootstrap.register();
    }

    public void run() {
        this.running = true;
        try {
            this.startGame();
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        try {
            try {
                while (this.running) {
                    if (this.hasCrashed && this.crashReporter != null) continue;
                    try {
                        this.runGameLoop();
                    }
                    catch (OutOfMemoryError ignored) {
                        this.freeMemory();
                        System.gc();
                    }
                    catch (Throwable t2) {
                        t2.printStackTrace();
                    }
                }
            }
            catch (Throwable throwable1) {
                throwable1.printStackTrace();
                this.shutdownMinecraftApplet();
            }
        }
        finally {
            this.shutdownMinecraftApplet();
        }
    }

    private void startGame() throws LWJGLException, IOException {
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
        this.startTimerHackThread();
        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }
        logger.info("LWJGL Version: " + Sys.getVersion());
        this.setWindowIcon();
        this.setInitialDisplayMode();
        this.createDisplay();
        OpenGlHelper.initializeTextures();
        this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
        this.framebufferMc.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.registerMetadataSerializers();
        this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        this.refreshResources();
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.initStream();
        if (Manager.isLicenseVailed()) {
            this.shutdownMinecraftApplet();
        }
        this.drawSplashScreen(this.renderEngine);
        this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
        this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        this.mcMusicTicker = new MusicTicker(this);
        this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);
        if (this.gameSettings.language != null) {
            this.fontRendererObj.setUnicodeFlag(this.isUnicode());
            this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }
        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRendererObj);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat(){

            @Override
            public String formatString(String str) {
                try {
                    return String.format(str, GameSettings.getKeyDisplayString(Minecraft.this.gameSettings.keyBindInventory.getKeyCode()));
                }
                catch (Exception exception) {
                    return "Error: " + exception.getLocalizedMessage();
                }
            }
        });
        this.mouseHelper = new MouseHelper();
        this.checkGLError("Pre startup");
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.cullFace(1029);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        this.checkGLError("Startup");
        this.textureMapBlocks = new TextureMap("textures");
        this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
        this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
        this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
        this.modelManager = new ModelManager(this.textureMapBlocks);
        this.mcResourceManager.registerReloadListener(this.modelManager);
        this.renderItem = new RenderItem(this.renderEngine, this.modelManager);
        this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
        this.itemRenderer = new ItemRenderer(this);
        this.mcResourceManager.registerReloadListener(this.renderItem);
        this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.entityRenderer);
        this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
        this.renderGlobal = new RenderGlobal(this);
        this.mcResourceManager.registerReloadListener(this.renderGlobal);
        this.guiAchievement = new GuiAchievement(this);
        GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);
        BetterCraft.getInstance().initialize();
        if (this.serverName != null) {
            this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
        } else if (GuiFirstTime.isEnabled) {
            this.displayGuiScreen(new GuiFirstTime());
        } else {
            this.displayGuiScreen(new GuiMainMenu());
        }
        this.renderEngine.deleteTexture(this.mojangLogo);
        this.mojangLogo = null;
        this.loadingScreen = new LoadingScreenRenderer(this);
        if (this.gameSettings.fullScreen && !this.fullscreen) {
            this.toggleFullscreen();
        }
        try {
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
        }
        catch (OpenGLException var2) {
            this.gameSettings.enableVsync = false;
            this.gameSettings.saveOptions();
        }
        this.renderGlobal.makeEntityOutlineShader();
        BetterCraft.getInstance();
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(BetterCraft.clientName)).append(" ");
        BetterCraft.getInstance();
        StringBuilder stringBuilder2 = stringBuilder.append(BetterCraft.clientVersion).append(" ");
        BetterCraft.getInstance();
        Display.setTitle(stringBuilder2.append(BetterCraft.clientBuild).append(" | Minecraft 1.8.9").toString());
    }

    private void registerMetadataSerializers() {
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
    }

    private void initStream() {
        try {
            this.stream = new TwitchStream(this, Iterables.getFirst(this.twitchDetails.get("twitch_access_token"), null));
        }
        catch (Throwable throwable) {
            this.stream = new NullStream(throwable);
            logger.error("Couldn't initialize twitch stream");
        }
    }

    private void createDisplay() throws LWJGLException {
        Display.setResizable(true);
        Display.setTitle("Loading...");
        try {
            Display.create(new PixelFormat().withDepthBits(24));
        }
        catch (LWJGLException lwjglexception) {
            logger.error("Couldn't set pixel format", (Throwable)lwjglexception);
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            if (this.fullscreen) {
                this.updateDisplayMode();
            }
            Display.create();
        }
    }

    private void setInitialDisplayMode() throws LWJGLException {
        if (this.fullscreen) {
            Display.setFullscreen(true);
            DisplayMode displaymode = Display.getDisplayMode();
            this.displayWidth = Math.max(1, displaymode.getWidth());
            this.displayHeight = Math.max(1, displaymode.getHeight());
        } else {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }
    }

    public static void setIcon(String icon16, String icon32) {
        block6: {
            InputStream inputstream = null;
            InputStream inputstream1 = null;
            try {
                try {
                    inputstream = Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(new ResourceLocation(icon16));
                    inputstream1 = Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(new ResourceLocation(icon32));
                    if (inputstream != null && inputstream1 != null) {
                        Display.setIcon(new ByteBuffer[]{Minecraft.getMinecraft().readImageToBuffer(inputstream), Minecraft.getMinecraft().readImageToBuffer(inputstream1)});
                    }
                }
                catch (IOException ioexception) {
                    logger.error("Couldn't set icon", (Throwable)ioexception);
                    IOUtils.closeQuietly(inputstream);
                    IOUtils.closeQuietly(inputstream1);
                    break block6;
                }
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(inputstream1);
                throw throwable;
            }
            IOUtils.closeQuietly(inputstream);
            IOUtils.closeQuietly(inputstream1);
        }
    }

    private void setWindowIcon() {
        if (Util.getOSType() != Util.EnumOS.OSX) {
            Minecraft.setIcon("client/icons/icon16x16.png", "client/icons/icon32x32.png");
        }
    }

    private static boolean isJvm64bit() {
        String[] astring;
        String[] stringArray = astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        int n2 = astring.length;
        int n3 = 0;
        while (n3 < n2) {
            String s2 = stringArray[n3];
            String s1 = System.getProperty(s2);
            if (s1 != null && s1.contains("64")) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    public Framebuffer getFramebuffer() {
        return this.framebufferMc;
    }

    public String getVersion() {
        return this.launchedVersion;
    }

    private void startTimerHackThread() {
        Thread thread = new Thread("Timer hack thread"){

            @Override
            public void run() {
                while (Minecraft.this.running) {
                    try {
                        Thread.sleep(Integer.MAX_VALUE);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void crashed(CrashReport crash) {
        this.hasCrashed = true;
        this.crashReporter = crash;
    }

    public void displayCrashReport(CrashReport crashReportIn) {
        File file1 = new File(Minecraft.getMinecraft().mcDataDir, "crash-reports");
        File file2 = new File(file1, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
        Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());
        if (crashReportIn.getFile() != null) {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
            System.exit(-1);
        } else if (crashReportIn.saveToFile(file2)) {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            System.exit(-1);
        } else {
            Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public boolean isUnicode() {
        return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
    }

    public void refreshResources() {
        ArrayList<IResourcePack> list = Lists.newArrayList(this.defaultResourcePacks);
        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries()) {
            list.add(resourcepackrepository$entry.getResourcePack());
        }
        if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
            list.add(this.mcResourcePackRepository.getResourcePackInstance());
        }
        try {
            this.mcResourceManager.reloadResources(list);
        }
        catch (RuntimeException runtimeexception) {
            logger.info("Caught error stitching, removing all assigned resourcepacks", (Throwable)runtimeexception);
            list.clear();
            list.addAll(this.defaultResourcePacks);
            this.mcResourcePackRepository.setRepositories(Collections.emptyList());
            this.mcResourceManager.reloadResources(list);
            this.gameSettings.resourcePacks.clear();
            this.gameSettings.incompatibleResourcePacks.clear();
            this.gameSettings.saveOptions();
        }
        this.mcLanguageManager.parseLanguageMetadata(list);
        if (this.renderGlobal != null) {
            this.renderGlobal.loadRenderers();
        }
    }

    private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(imageStream);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
        int[] nArray = aint;
        int n2 = aint.length;
        int n3 = 0;
        while (n3 < n2) {
            int i2 = nArray[n3];
            bytebuffer.putInt(i2 << 8 | i2 >> 24 & 0xFF);
            ++n3;
        }
        bytebuffer.flip();
        return bytebuffer;
    }

    private void updateDisplayMode() throws LWJGLException {
        HashSet<DisplayMode> set = Sets.newHashSet();
        Collections.addAll(set, Display.getAvailableDisplayModes());
        DisplayMode displaymode = Display.getDesktopDisplayMode();
        if (!set.contains(displaymode) && Util.getOSType() == Util.EnumOS.OSX) {
            block0: for (DisplayMode displaymode1 : macDisplayModes) {
                boolean flag = true;
                for (DisplayMode displaymode2 : set) {
                    if (displaymode2.getBitsPerPixel() != 32 || displaymode2.getWidth() != displaymode1.getWidth() || displaymode2.getHeight() != displaymode1.getHeight()) continue;
                    flag = false;
                    break;
                }
                if (flag) continue;
                for (DisplayMode displaymode3 : set) {
                    if (displaymode3.getBitsPerPixel() != 32 || displaymode3.getWidth() != displaymode1.getWidth() / 2 || displaymode3.getHeight() != displaymode1.getHeight() / 2) continue;
                    displaymode = displaymode3;
                    continue block0;
                }
            }
        }
        Display.setDisplayMode(displaymode);
        this.displayWidth = displaymode.getWidth();
        this.displayHeight = displaymode.getHeight();
    }

    private void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException {
        Framebuffer framebuffer;
        int i2;
        ScaledResolution scaledresolution;
        block5: {
            scaledresolution = new ScaledResolution(this);
            i2 = scaledresolution.getScaleFactor();
            framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * i2, scaledresolution.getScaledHeight() * i2, true);
            framebuffer.bindFramebuffer(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0f, 0.0f, -2000.0f);
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();
            InputStream inputstream = null;
            try {
                try {
                    inputstream = this.mcDefaultResourcePack.getInputStream(locationMojangPng);
                    this.mojangLogo = textureManagerInstance.getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(inputstream)));
                    textureManagerInstance.bindTexture(this.mojangLogo);
                }
                catch (IOException ioexception) {
                    logger.error("Unable to load logo: " + locationMojangPng, (Throwable)ioexception);
                    IOUtils.closeQuietly(inputstream);
                    break block5;
                }
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(inputstream);
                throw throwable;
            }
            IOUtils.closeQuietly(inputstream);
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0, this.displayHeight, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(this.displayWidth, this.displayHeight, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(this.displayWidth, 0.0, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int j2 = 256;
        int k2 = 256;
        this.draw((scaledresolution.getScaledWidth() - j2) / 2, (scaledresolution.getScaledHeight() - k2) / 2, 0, 0, j2, k2, 255, 255, 255, 255);
        Gui.drawRect(0, scaledresolution.getScaledHeight() - 4, 1000, scaledresolution.getScaledHeight(), new Color(149, 201, 144).getRGB());
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledresolution.getScaledWidth() * i2, scaledresolution.getScaledHeight() * i2);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1f);
        this.updateDisplay();
    }

    public void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue, int alpha) {
        float f2 = 0.00390625f;
        float f1 = 0.00390625f;
        WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(posX, posY + height, 0.0).tex((float)texU * f2, (float)(texV + height) * f1).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(posX + width, posY + height, 0.0).tex((float)(texU + width) * f2, (float)(texV + height) * f1).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(posX + width, posY, 0.0).tex((float)(texU + width) * f2, (float)texV * f1).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(posX, posY, 0.0).tex((float)texU * f2, (float)texV * f1).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    public ISaveFormat getSaveLoader() {
        return this.saveLoader;
    }

    public void displayGuiScreen(GuiScreen guiScreenIn) {
        if (this.currentScreen != null) {
            this.currentScreen.onGuiClosed();
        }
        if (guiScreenIn == null && this.theWorld == null) {
            guiScreenIn = new GuiMainMenu();
        } else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0f) {
            guiScreenIn = new GuiGameOver();
        }
        if (guiScreenIn instanceof GuiMainMenu) {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages();
        }
        this.currentScreen = guiScreenIn;
        if (guiScreenIn != null) {
            this.setIngameNotInFocus();
            ScaledResolution scaledresolution = new ScaledResolution(this);
            int i2 = scaledresolution.getScaledWidth();
            int j2 = scaledresolution.getScaledHeight();
            guiScreenIn.setWorldAndResolution(this, i2, j2);
            this.skipRenderWorld = false;
        } else {
            this.mcSoundHandler.resumeSounds();
            this.setIngameFocus();
        }
    }

    private void checkGLError(String message) {
        int i2;
        if (this.enableGLErrorChecking && (i2 = GL11.glGetError()) != 0) {
            String s2 = GLU.gluErrorString(i2);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + message);
            logger.error(String.valueOf(i2) + ": " + s2);
        }
    }

    public void shutdownMinecraftApplet() {
        block15: {
            try {
                BetterCraft.getInstance().terminate();
                int n2 = Manager.readJsonFromUrl().getAsJsonObject().get("Version").getAsInt();
                BetterCraft.getInstance();
                if (n2 <= BetterCraft.clientVersionCheck) {
                    int n3 = Manager.readJsonFromUrl().getAsJsonObject().get("Version").getAsInt();
                    BetterCraft.getInstance();
                    if (n3 != BetterCraft.clientVersionCheck) break block15;
                    int n4 = Manager.readJsonFromUrl().getAsJsonObject().get("Build").getAsInt();
                    BetterCraft.getInstance();
                    if (n4 <= BetterCraft.clientBuildCheck) break block15;
                }
                Manager.downloadFile();
            }
            finally {
                try {
                    logger.info("Stopping!");
                    try {
                        this.loadWorld(null);
                    }
                    catch (Throwable throwable) {}
                    this.mcSoundHandler.unloadSounds();
                }
                finally {
                    Display.destroy();
                    if (!this.hasCrashed) {
                        System.exit(0);
                    }
                }
                System.gc();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void runGameLoop() throws IOException {
        long i2 = System.nanoTime();
        this.mcProfiler.startSection("root");
        ASMEventManager.call(new ClientTickEvent());
        if (Display.isCreated() && Display.isCloseRequested()) {
            this.shutdown();
        }
        if (this.isGamePaused && this.theWorld != null) {
            float f2 = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = f2;
        } else {
            this.timer.updateTimer();
        }
        this.mcProfiler.startSection("scheduledExecutables");
        Queue<FutureTask<?>> f2 = this.scheduledTasks;
        synchronized (f2) {
            while (!this.scheduledTasks.isEmpty()) {
                Util.runTask(this.scheduledTasks.poll(), logger);
            }
        }
        this.mcProfiler.endSection();
        long l2 = System.nanoTime();
        this.mcProfiler.startSection("tick");
        int j2 = 0;
        while (j2 < this.timer.elapsedTicks) {
            this.runTick();
            ++j2;
        }
        this.mcProfiler.endStartSection("preRenderErrors");
        long i1 = System.nanoTime() - l2;
        this.checkGLError("Pre render");
        this.mcProfiler.endStartSection("sound");
        this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GlStateManager.pushMatrix();
        GlStateManager.clear(16640);
        this.framebufferMc.bindFramebuffer(true);
        this.mcProfiler.startSection("display");
        GlStateManager.enableTexture2D();
        if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
            this.gameSettings.thirdPersonView = 0;
        }
        this.mcProfiler.endSection();
        if (!this.skipRenderWorld) {
            this.mcProfiler.endStartSection("gameRenderer");
            this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks, i2);
            this.mcProfiler.endSection();
        }
        this.mcProfiler.endSection();
        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI) {
            if (!this.mcProfiler.profilingEnabled) {
                this.mcProfiler.clearProfiling();
            }
            this.mcProfiler.profilingEnabled = true;
            this.displayDebugInfo(i1);
        } else {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }
        this.guiAchievement.updateAchievementWindow();
        this.framebufferMc.unbindFramebuffer();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.entityRenderer.renderStreamIndicator(this.timer.renderPartialTicks);
        GlStateManager.popMatrix();
        this.mcProfiler.startSection("root");
        this.updateDisplay();
        Thread.yield();
        this.mcProfiler.startSection("stream");
        this.mcProfiler.startSection("update");
        this.stream.func_152935_j();
        this.mcProfiler.endStartSection("submit");
        this.stream.func_152922_k();
        this.mcProfiler.endSection();
        this.mcProfiler.endSection();
        this.checkGLError("Post render");
        ++this.fpsCounter;
        this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
        long k2 = System.nanoTime();
        this.frameTimer.addFrame(k2 - this.startNanoTime);
        this.startNanoTime = k2;
        while (Minecraft.getSystemTime() >= this.debugUpdateTime + 1000L) {
            debugFPS = this.fpsCounter;
            Object[] objectArray = new Object[8];
            objectArray[0] = debugFPS;
            objectArray[1] = RenderChunk.renderChunksUpdated;
            objectArray[2] = RenderChunk.renderChunksUpdated != 1 ? "s" : "";
            objectArray[3] = (float)this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : Integer.valueOf(this.gameSettings.limitFramerate);
            objectArray[4] = this.gameSettings.enableVsync ? " vsync" : "";
            Object object = objectArray[5] = this.gameSettings.fancyGraphics ? "" : " fast";
            objectArray[6] = this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds");
            objectArray[7] = OpenGlHelper.useVbo() ? " vbo" : "";
            this.debug = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", objectArray);
            RenderChunk.renderChunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.usageSnooper.addMemoryStatsToSnooper();
            if (this.usageSnooper.isSnooperRunning()) continue;
            this.usageSnooper.startSnooper();
        }
        if (this.isFramerateLimitBelowMax()) {
            this.mcProfiler.startSection("fpslimit_wait");
            Display.sync(this.getLimitFramerate());
            this.mcProfiler.endSection();
        }
        this.mcProfiler.endSection();
    }

    public void updateDisplay() {
        this.mcProfiler.startSection("display_update");
        Display.update();
        this.mcProfiler.endSection();
        this.checkWindowResize();
    }

    protected void checkWindowResize() {
        if (!this.fullscreen && Display.wasResized()) {
            int i2 = this.displayWidth;
            int j2 = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();
            if (this.displayWidth != i2 || this.displayHeight != j2) {
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }
                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }

    public int getLimitFramerate() {
        return this.theWorld == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
    }

    public boolean isFramerateLimitBelowMax() {
        return (float)this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }

    public void freeMemory() {
        try {
            memoryReserve = new byte[0];
            this.renderGlobal.deleteAllDisplayLists();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            System.gc();
            this.loadWorld(null);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        System.gc();
    }

    private void updateDebugProfilerName(int keyCount) {
        List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
        if (list != null && !list.isEmpty()) {
            Profiler.Result profiler$result = list.remove(0);
            if (keyCount == 0) {
                int i2;
                if (profiler$result.field_76331_c.length() > 0 && (i2 = this.debugProfilerName.lastIndexOf(".")) >= 0) {
                    this.debugProfilerName = this.debugProfilerName.substring(0, i2);
                }
            } else if (--keyCount < list.size() && !list.get((int)keyCount).field_76331_c.equals("unspecified")) {
                if (this.debugProfilerName.length() > 0) {
                    this.debugProfilerName = String.valueOf(this.debugProfilerName) + ".";
                }
                this.debugProfilerName = String.valueOf(this.debugProfilerName) + list.get((int)keyCount).field_76331_c;
            }
        }
    }

    private void displayDebugInfo(long elapsedTicksTime) {
        if (this.mcProfiler.profilingEnabled) {
            List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
            Profiler.Result profiler$result = list.remove(0);
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.enableColorMaterial();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, this.displayWidth, this.displayHeight, 0.0, 1000.0, 3000.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0f, 0.0f, -2000.0f);
            GL11.glLineWidth(1.0f);
            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            int i2 = 160;
            int j2 = this.displayWidth - i2 - 10;
            int k2 = this.displayHeight - i2 * 2;
            GlStateManager.enableBlend();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos((float)j2 - (float)i2 * 1.1f, (float)k2 - (float)i2 * 0.6f - 16.0f, 0.0).color(200, 0, 0, 0).endVertex();
            worldrenderer.pos((float)j2 - (float)i2 * 1.1f, k2 + i2 * 2, 0.0).color(200, 0, 0, 0).endVertex();
            worldrenderer.pos((float)j2 + (float)i2 * 1.1f, k2 + i2 * 2, 0.0).color(200, 0, 0, 0).endVertex();
            worldrenderer.pos((float)j2 + (float)i2 * 1.1f, (float)k2 - (float)i2 * 0.6f - 16.0f, 0.0).color(200, 0, 0, 0).endVertex();
            tessellator.draw();
            GlStateManager.disableBlend();
            double d0 = 0.0;
            int l2 = 0;
            while (l2 < list.size()) {
                Profiler.Result profiler$result1 = list.get(l2);
                int i1 = MathHelper.floor_double(profiler$result1.field_76332_a / 4.0) + 1;
                worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
                int j1 = profiler$result1.getColor();
                int k1 = j1 >> 16 & 0xFF;
                int l1 = j1 >> 8 & 0xFF;
                int i22 = j1 & 0xFF;
                worldrenderer.pos(j2, k2, 0.0).color(k1, l1, i22, 255).endVertex();
                int j22 = i1;
                while (j22 >= 0) {
                    float f2 = (float)((d0 + profiler$result1.field_76332_a * (double)j22 / (double)i1) * Math.PI * 2.0 / 100.0);
                    float f1 = MathHelper.sin(f2) * (float)i2;
                    float f22 = MathHelper.cos(f2) * (float)i2 * 0.5f;
                    worldrenderer.pos((float)j2 + f1, (float)k2 - f22, 0.0).color(k1, l1, i22, 255).endVertex();
                    --j22;
                }
                tessellator.draw();
                worldrenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);
                int i3 = i1;
                while (i3 >= 0) {
                    float f3 = (float)((d0 + profiler$result1.field_76332_a * (double)i3 / (double)i1) * Math.PI * 2.0 / 100.0);
                    float f4 = MathHelper.sin(f3) * (float)i2;
                    float f5 = MathHelper.cos(f3) * (float)i2 * 0.5f;
                    worldrenderer.pos((float)j2 + f4, (float)k2 - f5, 0.0).color(k1 >> 1, l1 >> 1, i22 >> 1, 255).endVertex();
                    worldrenderer.pos((float)j2 + f4, (float)k2 - f5 + 10.0f, 0.0).color(k1 >> 1, l1 >> 1, i22 >> 1, 255).endVertex();
                    --i3;
                }
                tessellator.draw();
                d0 += profiler$result1.field_76332_a;
                ++l2;
            }
            DecimalFormat decimalformat = new DecimalFormat("##0.00");
            GlStateManager.enableTexture2D();
            String s2 = "";
            if (!profiler$result.field_76331_c.equals("unspecified")) {
                s2 = String.valueOf(s2) + "[0] ";
            }
            s2 = profiler$result.field_76331_c.length() == 0 ? String.valueOf(s2) + "ROOT " : String.valueOf(s2) + profiler$result.field_76331_c + " ";
            int l22 = 0xFFFFFF;
            this.fontRendererObj.drawStringWithShadow(s2, j2 - i2, k2 - i2 / 2 - 16, l22);
            s2 = String.valueOf(decimalformat.format(profiler$result.field_76330_b)) + "%";
            this.fontRendererObj.drawStringWithShadow(s2, j2 + i2 - this.fontRendererObj.getStringWidth(s2), k2 - i2 / 2 - 16, l22);
            int k22 = 0;
            while (k22 < list.size()) {
                Profiler.Result profiler$result2 = list.get(k22);
                String s1 = "";
                s1 = profiler$result2.field_76331_c.equals("unspecified") ? String.valueOf(s1) + "[?] " : String.valueOf(s1) + "[" + (k22 + 1) + "] ";
                s1 = String.valueOf(s1) + profiler$result2.field_76331_c;
                this.fontRendererObj.drawStringWithShadow(s1, j2 - i2, k2 + i2 / 2 + k22 * 8 + 20, profiler$result2.getColor());
                s1 = String.valueOf(decimalformat.format(profiler$result2.field_76332_a)) + "%";
                this.fontRendererObj.drawStringWithShadow(s1, j2 + i2 - 50 - this.fontRendererObj.getStringWidth(s1), k2 + i2 / 2 + k22 * 8 + 20, profiler$result2.getColor());
                s1 = String.valueOf(decimalformat.format(profiler$result2.field_76330_b)) + "%";
                this.fontRendererObj.drawStringWithShadow(s1, j2 + i2 - this.fontRendererObj.getStringWidth(s1), k2 + i2 / 2 + k22 * 8 + 20, profiler$result2.getColor());
                ++k22;
            }
        }
    }

    public void shutdown() {
        this.running = false;
    }

    public void setIngameFocus() {
        if (Display.isActive() && !this.inGameHasFocus) {
            this.inGameHasFocus = true;
            this.mouseHelper.grabMouseCursor();
            this.displayGuiScreen(null);
            this.leftClickCounter = 10000;
        }
    }

    public void setIngameNotInFocus() {
        if (this.inGameHasFocus) {
            KeyBinding.unPressAllKeys();
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    public void displayInGameMenu() {
        if (this.currentScreen == null) {
            this.displayGuiScreen(new GuiIngameMenu());
            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) {
                this.mcSoundHandler.pauseSounds();
            }
        }
    }

    private void sendClickBlockToController(boolean leftClick) {
        if (!leftClick) {
            this.leftClickCounter = 0;
        }
        if (this.leftClickCounter <= 0 && !this.thePlayer.isUsingItem()) {
            if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();
                if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
                    this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver.sideHit);
                    this.thePlayer.swingItem();
                }
            } else {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    private void clickMouse() {
        if (this.leftClickCounter <= 0) {
            AttackOrder.sendConditionalSwing(this.objectMouseOver);
            if (this.objectMouseOver == null) {
                logger.error("Null returned as 'hitResult', this shouldn't happen!");
                if (this.playerController.isNotCreative()) {
                    this.leftClickCounter = 10;
                }
            } else {
                switch (this.objectMouseOver.typeOfHit) {
                    case ENTITY: {
                        AttackOrder.sendFixedAttack(this.thePlayer, this.objectMouseOver.entityHit);
                        break;
                    }
                    case BLOCK: {
                        BlockPos blockpos = this.objectMouseOver.getBlockPos();
                        if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                            this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                            break;
                        }
                    }
                    default: {
                        if (!this.playerController.isNotCreative()) break;
                        this.leftClickCounter = 10;
                    }
                }
            }
        }
    }

    private void rightClickMouse() {
        if (!this.playerController.getIsHittingBlock()) {
            ItemStack itemstack1;
            this.rightClickDelayTimer = 4;
            boolean flag = true;
            ItemStack itemstack = this.thePlayer.inventory.getCurrentItem();
            if (this.objectMouseOver == null) {
                logger.warn("Null returned as 'hitResult', this shouldn't happen!");
            } else {
                switch (this.objectMouseOver.typeOfHit) {
                    case ENTITY: {
                        if (this.playerController.isPlayerRightClickingOnEntity(this.thePlayer, this.objectMouseOver.entityHit, this.objectMouseOver)) {
                            flag = false;
                            break;
                        }
                        if (!this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit)) break;
                        flag = false;
                        break;
                    }
                    case BLOCK: {
                        int i2;
                        BlockPos blockpos = this.objectMouseOver.getBlockPos();
                        if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() == Material.air) break;
                        int n2 = i2 = itemstack != null ? itemstack.stackSize : 0;
                        if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, itemstack, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
                            flag = false;
                            this.thePlayer.swingItem();
                        }
                        if (itemstack == null) {
                            return;
                        }
                        if (itemstack.stackSize == 0) {
                            this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                            break;
                        }
                        if (itemstack.stackSize == i2 && !this.playerController.isInCreativeMode()) break;
                        this.entityRenderer.itemRenderer.resetEquippedProgress();
                    }
                }
            }
            if (flag && (itemstack1 = this.thePlayer.inventory.getCurrentItem()) != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, itemstack1)) {
                this.entityRenderer.itemRenderer.resetEquippedProgress2();
            }
        }
    }

    public void toggleFullscreen() {
        try {
            this.gameSettings.fullScreen = this.fullscreen = !this.fullscreen;
            if (this.fullscreen) {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }
                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            } else {
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }
                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            }
            if (this.currentScreen != null) {
                this.resize(this.displayWidth, this.displayHeight);
            } else {
                this.updateFramebufferSize();
            }
            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.updateDisplay();
        }
        catch (Exception exception) {
            logger.error("Couldn't toggle fullscreen", (Throwable)exception);
        }
    }

    private void resize(int width, int height) {
        this.displayWidth = Math.max(1, width);
        this.displayHeight = Math.max(1, height);
        if (this.currentScreen != null) {
            ScaledResolution scaledresolution = new ScaledResolution(this);
            this.currentScreen.onResize(this, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        }
        this.loadingScreen = new LoadingScreenRenderer(this);
        this.updateFramebufferSize();
    }

    private void updateFramebufferSize() {
        this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);
        if (this.entityRenderer != null) {
            this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
        }
    }

    public MusicTicker getMusicTicker() {
        return this.mcMusicTicker;
    }

    /*
     * Unable to fully structure code
     */
    public void runTick() throws IOException {
        block106: {
            block107: {
                if (this.rightClickDelayTimer > 0) {
                    --this.rightClickDelayTimer;
                }
                this.mcProfiler.startSection("gui");
                if (!this.isGamePaused) {
                    this.ingameGUI.updateTick();
                }
                this.mcProfiler.endSection();
                this.entityRenderer.getMouseOver(1.0f);
                this.mcProfiler.startSection("gameMode");
                if (!this.isGamePaused && this.theWorld != null) {
                    this.playerController.updateController();
                }
                this.mcProfiler.endStartSection("textures");
                if (!this.isGamePaused) {
                    this.renderEngine.tick();
                }
                if (this.currentScreen == null && this.thePlayer != null) {
                    if (this.thePlayer.getHealth() <= 0.0f) {
                        this.displayGuiScreen(null);
                    } else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
                        this.displayGuiScreen(new GuiSleepMP());
                    }
                } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
                    this.displayGuiScreen(null);
                }
                if (this.currentScreen != null) {
                    this.leftClickCounter = 10000;
                }
                if (this.currentScreen != null) {
                    try {
                        this.currentScreen.handleInput();
                    }
                    catch (Throwable throwable1) {
                        crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                        crashreportcategory = crashreport.makeCategory("Affected screen");
                        crashreportcategory.addCrashSectionCallable("Screen name", new Callable<String>(){

                            @Override
                            public String call() throws Exception {
                                return Minecraft.this.currentScreen.getClass().getCanonicalName();
                            }
                        });
                        throw new ReportedException(crashreport);
                    }
                    if (this.currentScreen != null) {
                        try {
                            this.currentScreen.updateScreen();
                        }
                        catch (Throwable throwable) {
                            crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
                            crashreportcategory1 = crashreport1.makeCategory("Affected screen");
                            crashreportcategory1.addCrashSectionCallable("Screen name", new Callable<String>(){

                                @Override
                                public String call() throws Exception {
                                    return Minecraft.this.currentScreen.getClass().getCanonicalName();
                                }
                            });
                            throw new ReportedException(crashreport1);
                        }
                    }
                }
                if (this.currentScreen != null && !this.currentScreen.allowUserInput) break block106;
                this.mcProfiler.endStartSection("mouse");
                while (Mouse.next()) {
                    i = Mouse.getEventButton();
                    KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());
                    if (Mouse.getEventButtonState()) {
                        if (this.thePlayer.isSpectator() && i == 2) {
                            this.ingameGUI.getSpectatorGui().func_175261_b();
                        } else {
                            KeyBinding.onTick(i - 100);
                        }
                    }
                    if ((i1 = Minecraft.getSystemTime() - this.systemTime) > 200L) continue;
                    j = Mouse.getEventDWheel();
                    if (j != 0) {
                        if (this.thePlayer.isSpectator()) {
                            v0 = j = j < 0 ? -1 : 1;
                            if (this.ingameGUI.getSpectatorGui().func_175262_a()) {
                                this.ingameGUI.getSpectatorGui().func_175259_b(-j);
                            } else {
                                f = MathHelper.clamp_float(this.thePlayer.capabilities.getFlySpeed() + (float)j * 0.005f, 0.0f, 0.2f);
                                this.thePlayer.capabilities.setFlySpeed(f);
                            }
                        } else {
                            this.thePlayer.inventory.changeCurrentItem(j);
                        }
                    }
                    if (this.currentScreen == null) {
                        if (this.inGameHasFocus || !Mouse.getEventButtonState()) continue;
                        this.setIngameFocus();
                        continue;
                    }
                    if (this.currentScreen == null) continue;
                    this.currentScreen.handleMouseInput();
                }
                if (this.leftClickCounter > 0) {
                    --this.leftClickCounter;
                }
                this.mcProfiler.endStartSection("keyboard");
                while (Keyboard.next()) {
                    k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
                    KeyBinding.setKeyBindState(k, Keyboard.getEventKeyState());
                    if (Keyboard.getEventKeyState()) {
                        KeyBinding.onTick(k);
                    }
                    if (this.debugCrashKeyPressTime > 0L) {
                        if (Minecraft.getSystemTime() - this.debugCrashKeyPressTime >= 6000L) {
                            throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                        }
                        if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                            this.debugCrashKeyPressTime = -1L;
                        }
                    } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
                        this.debugCrashKeyPressTime = Minecraft.getSystemTime();
                    }
                    this.dispatchKeypresses();
                    if (this.currentScreen != null) {
                        this.currentScreen.handleKeyboardInput();
                    } else {
                        if (Minecraft.getMinecraft().gameSettings.CLIENT_GUI_MOD_POS.isPressed()) {
                            HUDManager.getInstance().openConfigScreen();
                        }
                        if (Minecraft.getMinecraft().gameSettings.CLIENT_GUI_NBTEDIT.isPressed() && this.thePlayer.getHeldItem().hasTagCompound()) {
                            this.displayGuiScreen(new GuiEditNBTTree(this.thePlayer.getEntityId(), this.thePlayer.getHeldItem().stackTagCompound));
                        }
                        if (Minecraft.getMinecraft().gameSettings.CLIENT_GUI_MOD_STATE.isPressed()) {
                            Minecraft.getMinecraft().displayGuiScreen(new GuiRShift(this.currentScreen));
                        }
                        if (Minecraft.getMinecraft().gameSettings.CLIENT_CREATIVE_TAB.isPressed()) {
                            TabCreativeOwnItems.saveItemstack(this.thePlayer.getHeldItem());
                            v1 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                            BetterCraft.getInstance();
                            v1.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "Item \u00a7f'" + this.thePlayer.getHeldItem().getDisplayName() + "' \u00a77saved in Own Items"));
                        }
                    }
                    if (!Keyboard.getEventKeyState()) continue;
                    if (k == 62 && this.entityRenderer != null) {
                        this.entityRenderer.switchUseShader();
                    }
                    if (this.currentScreen != null) {
                        this.currentScreen.handleKeyboardInput();
                    } else {
                        if (k == 1) {
                            this.displayInGameMenu();
                        }
                        if (k == 32 && Keyboard.isKeyDown(61) && this.ingameGUI != null) {
                            this.ingameGUI.getChatGUI().clearChatMessages();
                        }
                        if (k == 31 && Keyboard.isKeyDown(61)) {
                            this.refreshResources();
                        }
                        if (k != 17 || Keyboard.isKeyDown(61)) {
                            // empty if block
                        }
                        if (k != 18 || Keyboard.isKeyDown(61)) {
                            // empty if block
                        }
                        if (k != 47 || Keyboard.isKeyDown(61)) {
                            // empty if block
                        }
                        if (k != 38 || Keyboard.isKeyDown(61)) {
                            // empty if block
                        }
                        if (k != 22 || Keyboard.isKeyDown(61)) {
                            // empty if block
                        }
                        if (k == 20 && Keyboard.isKeyDown(61)) {
                            this.refreshResources();
                        }
                        if (k == 33 && Keyboard.isKeyDown(61)) {
                            this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() != false ? -1 : 1);
                        }
                        if (k == 30 && Keyboard.isKeyDown(61)) {
                            this.renderGlobal.loadRenderers();
                        }
                        if (k == 35 && Keyboard.isKeyDown(61)) {
                            this.gameSettings.advancedItemTooltips = this.gameSettings.advancedItemTooltips == false;
                            this.gameSettings.saveOptions();
                        }
                        if (k == 48 && Keyboard.isKeyDown(61)) {
                            this.renderManager.setDebugBoundingBox(this.renderManager.isDebugBoundingBox() == false);
                        }
                        if (k == 25 && Keyboard.isKeyDown(61)) {
                            this.gameSettings.pauseOnLostFocus = this.gameSettings.pauseOnLostFocus == false;
                            this.gameSettings.saveOptions();
                        }
                        if (k == 59) {
                            v2 = this.gameSettings.hideGUI = this.gameSettings.hideGUI == false;
                        }
                        if (k == 61) {
                            this.gameSettings.showDebugInfo = this.gameSettings.showDebugInfo == false;
                            this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                            this.gameSettings.showLagometer = GuiScreen.isAltKeyDown();
                        }
                        if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
                            ++this.gameSettings.thirdPersonView;
                            if (this.gameSettings.thirdPersonView > 2) {
                                this.gameSettings.thirdPersonView = 0;
                            }
                            if (this.gameSettings.thirdPersonView == 0) {
                                this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
                            } else if (this.gameSettings.thirdPersonView == 1) {
                                this.entityRenderer.loadEntityShader(null);
                            }
                            this.renderGlobal.setDisplayListEntitiesDirty();
                        }
                        if (this.gameSettings.keyBindSmoothCamera.isPressed()) {
                            v3 = this.gameSettings.smoothCamera = this.gameSettings.smoothCamera == false;
                        }
                    }
                    if (!this.gameSettings.showDebugInfo || !this.gameSettings.showDebugProfilerChart) continue;
                    if (k == 11) {
                        this.updateDebugProfilerName(0);
                    }
                    j1 = 0;
                    while (j1 < 9) {
                        if (k == 2 + j1) {
                            this.updateDebugProfilerName(j1 + 1);
                        }
                        ++j1;
                    }
                }
                l = 0;
                while (l < 9) {
                    if (this.gameSettings.keyBindsHotbar[l].isPressed()) {
                        if (this.thePlayer.isSpectator()) {
                            this.ingameGUI.getSpectatorGui().func_175260_a(l);
                        } else {
                            this.thePlayer.inventory.currentItem = l;
                        }
                    }
                    ++l;
                }
                flag = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;
                while (this.gameSettings.keyBindInventory.isPressed()) {
                    if (this.playerController.isRidingHorse()) {
                        this.thePlayer.sendHorseInventory();
                        continue;
                    }
                    this.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    this.displayGuiScreen(new GuiInventory(this.thePlayer));
                }
                while (this.gameSettings.keyBindDrop.isPressed()) {
                    if (this.thePlayer.isSpectator()) continue;
                    this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
                }
                while (this.gameSettings.keyBindChat.isPressed() && flag) {
                    this.displayGuiScreen(new GuiChatCustom());
                }
                if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && flag) {
                    this.displayGuiScreen(new GuiChatCustom("/"));
                }
                if (!this.thePlayer.isUsingItem()) ** GOTO lbl206
                if (!this.gameSettings.keyBindUseItem.isKeyDown()) {
                    this.playerController.onStoppedUsingItem(this.thePlayer);
                }
                while (this.gameSettings.keyBindAttack.isPressed()) {
                }
                while (this.gameSettings.keyBindUseItem.isPressed()) {
                }
                while (this.gameSettings.keyBindPickBlock.isPressed()) {
                }
                break block107;
lbl-1000:
                // 1 sources

                {
                    this.clickMouse();
lbl206:
                    // 2 sources

                    ** while (this.gameSettings.keyBindAttack.isPressed())
                }
lbl207:
                // 2 sources

                while (this.gameSettings.keyBindUseItem.isPressed()) {
                    this.rightClickMouse();
                }
                while (this.gameSettings.keyBindPickBlock.isPressed()) {
                    this.middleClickMouse();
                }
            }
            if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem()) {
                this.rightClickMouse();
            }
            this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() != false && this.inGameHasFocus != false);
        }
        if (this.theWorld != null) {
            if (this.thePlayer != null) {
                ++this.joinPlayerCounter;
                if (this.joinPlayerCounter == 30) {
                    this.joinPlayerCounter = 0;
                    this.theWorld.joinEntityInSurroundings(this.thePlayer);
                }
            }
            this.mcProfiler.endStartSection("gameRenderer");
            if (!this.isGamePaused) {
                this.entityRenderer.updateRenderer();
            }
            this.mcProfiler.endStartSection("levelRenderer");
            if (!this.isGamePaused) {
                this.renderGlobal.updateClouds();
            }
            this.mcProfiler.endStartSection("level");
            if (!this.isGamePaused) {
                if (this.theWorld.getLastLightningBolt() > 0) {
                    this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1);
                }
                this.theWorld.updateEntities();
            }
        } else if (this.entityRenderer.isShaderActive()) {
            this.entityRenderer.stopUseShader();
        }
        if (!this.isGamePaused) {
            this.mcMusicTicker.update();
            this.mcSoundHandler.update();
        }
        if (this.theWorld != null) {
            if (!this.isGamePaused) {
                this.theWorld.setAllowedSpawnTypes(this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);
                try {
                    this.theWorld.tick();
                }
                catch (Throwable throwable2) {
                    crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");
                    if (this.theWorld == null) {
                        crashreportcategory2 = crashreport2.makeCategory("Affected level");
                        crashreportcategory2.addCrashSection("Problem", "Level is null!");
                    } else {
                        this.theWorld.addWorldInfoToCrashReport(crashreport2);
                    }
                    throw new ReportedException(crashreport2);
                }
            }
            this.mcProfiler.endStartSection("animateTick");
            if (!this.isGamePaused && this.theWorld != null) {
                this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
            }
            this.mcProfiler.endStartSection("particles");
            if (!this.isGamePaused) {
                this.effectRenderer.updateEffects();
            }
        } else if (this.myNetworkManager != null) {
            this.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReceivedPackets();
        }
        this.mcProfiler.endSection();
        this.systemTime = Minecraft.getSystemTime();
    }

    public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) {
        this.loadWorld(null);
        System.gc();
        ISaveHandler isavehandler = this.saveLoader.getSaveLoader(folderName, false);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();
        if (worldinfo == null && worldSettingsIn != null) {
            worldinfo = new WorldInfo(worldSettingsIn, folderName);
            isavehandler.saveWorldInfo(worldinfo);
        }
        if (worldSettingsIn == null) {
            worldSettingsIn = new WorldSettings(worldinfo);
        }
        try {
            this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
            crashreportcategory.addCrashSection("Level ID", folderName);
            crashreportcategory.addCrashSection("Level Name", worldName);
            throw new ReportedException(crashreport);
        }
        this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel", new Object[0]));
        while (!this.theIntegratedServer.serverIsInRunLoop()) {
            String s2 = this.theIntegratedServer.getUserMessage();
            if (s2 != null) {
                this.loadingScreen.displayLoadingString(I18n.format(s2, new Object[0]));
            } else {
                this.loadingScreen.displayLoadingString("");
            }
            try {
                Thread.sleep(200L);
            }
            catch (InterruptedException crashreport) {
                // empty catch block
            }
        }
        this.displayGuiScreen(null);
        SocketAddress socketaddress = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
        NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
        networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, this, null));
        networkmanager.sendPacket(new C00Handshake(47, socketaddress.toString(), 0, EnumConnectionState.LOGIN));
        networkmanager.sendPacket(new C00PacketLoginStart(this.getSession().getProfile()));
        this.myNetworkManager = networkmanager;
    }

    public void loadWorld(WorldClient worldClientIn) {
        this.loadWorld(worldClientIn, "");
    }

    public void loadWorld(WorldClient worldClientIn, String loadingMessage) {
        if (worldClientIn == null) {
            NetHandlerPlayClient nethandlerplayclient = this.getNetHandler();
            if (nethandlerplayclient != null) {
                nethandlerplayclient.cleanup();
            }
            if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet()) {
                this.theIntegratedServer.initiateShutdown();
                this.theIntegratedServer.setStaticInstance();
            }
            this.theIntegratedServer = null;
            this.guiAchievement.clearAchievements();
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }
        this.renderViewEntity = null;
        this.myNetworkManager = null;
        if (this.loadingScreen != null) {
            this.loadingScreen.resetProgressAndMessage(loadingMessage);
            this.loadingScreen.displayLoadingString("");
        }
        if (worldClientIn == null && this.theWorld != null) {
            this.mcResourcePackRepository.clearResourcePack();
            this.ingameGUI.resetPlayersOverlayFooterHeader();
            this.setServerData(null);
            this.integratedServerIsRunning = false;
        }
        this.mcSoundHandler.stopSounds();
        this.theWorld = worldClientIn;
        if (worldClientIn != null) {
            if (this.renderGlobal != null) {
                this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
            }
            if (this.effectRenderer != null) {
                this.effectRenderer.clearEffects(worldClientIn);
            }
            if (this.thePlayer == null) {
                this.thePlayer = this.playerController.func_178892_a(worldClientIn, new StatFileWriter());
                this.playerController.flipPlayer(this.thePlayer);
            }
            this.thePlayer.preparePlayerToSpawn();
            worldClientIn.spawnEntityInWorld(this.thePlayer);
            this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.thePlayer);
            this.renderViewEntity = this.thePlayer;
            if (GuiMisc.enabledMisc[1] && FBP.enabled) {
                FBP.getInstance().eventHandler.onWorldLoadEvent();
            }
        } else {
            this.saveLoader.flushCache();
            this.thePlayer = null;
        }
        System.gc();
        this.systemTime = 0L;
    }

    public void setDimensionAndSpawnPlayer(int dimension) {
        this.theWorld.setInitialSpawnLocation();
        this.theWorld.removeAllEntities();
        int i2 = 0;
        String s2 = null;
        if (this.thePlayer != null) {
            i2 = this.thePlayer.getEntityId();
            this.theWorld.removeEntity(this.thePlayer);
            s2 = this.thePlayer.getClientBrand();
        }
        this.renderViewEntity = null;
        EntityPlayerSP entityplayersp = this.thePlayer;
        this.thePlayer = this.playerController.func_178892_a(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
        this.thePlayer.getDataWatcher().updateWatchedObjectsFromList(entityplayersp.getDataWatcher().getAllWatched());
        this.thePlayer.dimension = dimension;
        this.renderViewEntity = this.thePlayer;
        this.thePlayer.preparePlayerToSpawn();
        this.thePlayer.setClientBrand(s2);
        this.theWorld.spawnEntityInWorld(this.thePlayer);
        this.playerController.flipPlayer(this.thePlayer);
        this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.thePlayer.setEntityId(i2);
        this.playerController.setPlayerCapabilities(this.thePlayer);
        this.thePlayer.setReducedDebug(entityplayersp.hasReducedDebug());
        if (this.currentScreen instanceof GuiGameOver) {
            this.displayGuiScreen(null);
        }
    }

    public final boolean isDemo() {
        return this.isDemo;
    }

    public NetHandlerPlayClient getNetHandler() {
        return this.thePlayer != null ? this.thePlayer.sendQueue : null;
    }

    public static boolean isGuiEnabled() {
        return theMinecraft == null || !Minecraft.theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled() {
        return theMinecraft != null && Minecraft.theMinecraft.gameSettings.fancyGraphics;
    }

    public static boolean isAmbientOcclusionEnabled() {
        return theMinecraft != null && Minecraft.theMinecraft.gameSettings.ambientOcclusion != 0;
    }

    private void middleClickMouse() {
        if (this.objectMouseOver != null) {
            Item item;
            boolean flag = this.thePlayer.capabilities.isCreativeMode;
            int i2 = 0;
            boolean flag1 = false;
            TileEntity tileentity = null;
            if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();
                Block block = this.theWorld.getBlockState(blockpos).getBlock();
                if (block.getMaterial() == Material.air) {
                    return;
                }
                item = block.getItem(this.theWorld, blockpos);
                if (item == null) {
                    return;
                }
                if (flag && GuiScreen.isCtrlKeyDown()) {
                    tileentity = this.theWorld.getTileEntity(blockpos);
                }
                Block block1 = item instanceof ItemBlock && !block.isFlowerPot() ? Block.getBlockFromItem(item) : block;
                i2 = block1.getDamageValue(this.theWorld, blockpos);
                flag1 = item.getHasSubtypes();
            } else {
                if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !flag) {
                    return;
                }
                if (this.objectMouseOver.entityHit instanceof EntityPainting) {
                    item = Items.painting;
                } else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
                    item = Items.lead;
                } else if (this.objectMouseOver.entityHit instanceof EntityItemFrame) {
                    EntityItemFrame entityitemframe = (EntityItemFrame)this.objectMouseOver.entityHit;
                    ItemStack itemstack = entityitemframe.getDisplayedItem();
                    if (itemstack == null) {
                        item = Items.item_frame;
                    } else {
                        item = itemstack.getItem();
                        i2 = itemstack.getMetadata();
                        flag1 = true;
                    }
                } else if (this.objectMouseOver.entityHit instanceof EntityMinecart) {
                    EntityMinecart entityminecart = (EntityMinecart)this.objectMouseOver.entityHit;
                    switch (entityminecart.getMinecartType()) {
                        case FURNACE: {
                            item = Items.furnace_minecart;
                            break;
                        }
                        case CHEST: {
                            item = Items.chest_minecart;
                            break;
                        }
                        case TNT: {
                            item = Items.tnt_minecart;
                            break;
                        }
                        case HOPPER: {
                            item = Items.hopper_minecart;
                            break;
                        }
                        case COMMAND_BLOCK: {
                            item = Items.command_block_minecart;
                            break;
                        }
                        default: {
                            item = Items.minecart;
                            break;
                        }
                    }
                } else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
                    item = Items.boat;
                } else if (this.objectMouseOver.entityHit instanceof EntityArmorStand) {
                    item = Items.armor_stand;
                } else {
                    item = Items.spawn_egg;
                    i2 = EntityList.getEntityID(this.objectMouseOver.entityHit);
                    flag1 = true;
                    if (!EntityList.entityEggs.containsKey(i2)) {
                        return;
                    }
                }
            }
            InventoryPlayer inventoryplayer = this.thePlayer.inventory;
            if (tileentity == null) {
                inventoryplayer.setCurrentItem(item, i2, flag1, flag);
            } else {
                ItemStack itemstack1 = this.pickBlockWithNBT(item, i2, tileentity);
                inventoryplayer.setInventorySlotContents(inventoryplayer.currentItem, itemstack1);
            }
            if (flag) {
                int j2 = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventoryplayer.currentItem;
                this.playerController.sendSlotPacket(inventoryplayer.getStackInSlot(inventoryplayer.currentItem), j2);
            }
        }
    }

    private ItemStack pickBlockWithNBT(Item itemIn, int meta, TileEntity tileEntityIn) {
        ItemStack itemstack = new ItemStack(itemIn, 1, meta);
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        tileEntityIn.writeToNBT(nbttagcompound);
        if (itemIn == Items.skull && nbttagcompound.hasKey("Owner")) {
            NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
            NBTTagCompound nbttagcompound3 = new NBTTagCompound();
            nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
            itemstack.setTagCompound(nbttagcompound3);
            return itemstack;
        }
        itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();
        nbttaglist.appendTag(new NBTTagString("(+NBT)"));
        nbttagcompound1.setTag("Lore", nbttaglist);
        itemstack.setTagInfo("display", nbttagcompound1);
        return itemstack;
    }

    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
        theCrash.getCategory().addCrashSectionCallable("Launched Version", new Callable<String>(){

            @Override
            public String call() throws Exception {
                return Minecraft.this.launchedVersion;
            }
        });
        theCrash.getCategory().addCrashSectionCallable("LWJGL", new Callable<String>(){

            @Override
            public String call() {
                return Sys.getVersion();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("OpenGL", new Callable<String>(){

            @Override
            public String call() {
                return String.valueOf(GL11.glGetString(7937)) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
            }
        });
        theCrash.getCategory().addCrashSectionCallable("GL Caps", new Callable<String>(){

            @Override
            public String call() {
                return OpenGlHelper.getLogText();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Using VBOs", new Callable<String>(){

            @Override
            public String call() {
                return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Is Modded", new Callable<String>(){

            @Override
            public String call() throws Exception {
                String s2 = ClientBrandRetriever.getClientModName();
                return !s2.equals("vanilla") ? "Definitely; Client brand changed to '" + s2 + "'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.");
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Type", new Callable<String>(){

            @Override
            public String call() throws Exception {
                return "Client (map_client.txt)";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Resource Packs", new Callable<String>(){

            @Override
            public String call() throws Exception {
                StringBuilder stringbuilder = new StringBuilder();
                for (String s2 : Minecraft.this.gameSettings.resourcePacks) {
                    if (stringbuilder.length() > 0) {
                        stringbuilder.append(", ");
                    }
                    stringbuilder.append(s2);
                    if (!Minecraft.this.gameSettings.incompatibleResourcePacks.contains(s2)) continue;
                    stringbuilder.append(" (incompatible)");
                }
                return stringbuilder.toString();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Current Language", new Callable<String>(){

            @Override
            public String call() throws Exception {
                return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>(){

            @Override
            public String call() throws Exception {
                return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("CPU", new Callable<String>(){

            @Override
            public String call() {
                return OpenGlHelper.getCpu();
            }
        });
        if (this.theWorld != null) {
            this.theWorld.addWorldInfoToCrashReport(theCrash);
        }
        return theCrash;
    }

    public static Minecraft getMinecraft() {
        return theMinecraft;
    }

    public ListenableFuture<Object> scheduleResourcesRefresh() {
        return this.addScheduledTask(new Runnable(){

            @Override
            public void run() {
                Minecraft.this.refreshResources();
            }
        });
    }

    @Override
    public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
        playerSnooper.addClientStat("fps", debugFPS);
        playerSnooper.addClientStat("vsync_enabled", this.gameSettings.enableVsync);
        playerSnooper.addClientStat("display_frequency", Display.getDisplayMode().getFrequency());
        playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
        playerSnooper.addClientStat("run_time", (MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
        playerSnooper.addClientStat("current_action", this.getCurrentAction());
        String s2 = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        playerSnooper.addClientStat("endianness", s2);
        playerSnooper.addClientStat("resource_packs", this.mcResourcePackRepository.getRepositoryEntries().size());
        int i2 = 0;
        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries()) {
            playerSnooper.addClientStat("resource_pack[" + i2++ + "]", resourcepackrepository$entry.getResourcePackName());
        }
        if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
            playerSnooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
        }
    }

    private String getCurrentAction() {
        return this.theIntegratedServer != null ? (this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer") : (this.currentServerData != null ? (this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer") : "out_of_game");
    }

    @Override
    public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper) {
        playerSnooper.addStatToSnooper("opengl_version", GL11.glGetString(7938));
        playerSnooper.addStatToSnooper("opengl_vendor", GL11.glGetString(7936));
        playerSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
        playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
        ContextCapabilities contextcapabilities = GLContext.getCapabilities();
        playerSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", contextcapabilities.GL_ARB_arrays_of_arrays);
        playerSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", contextcapabilities.GL_ARB_base_instance);
        playerSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", contextcapabilities.GL_ARB_blend_func_extended);
        playerSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", contextcapabilities.GL_ARB_clear_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", contextcapabilities.GL_ARB_color_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", contextcapabilities.GL_ARB_compatibility);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", contextcapabilities.GL_ARB_compressed_texture_pixel_storage);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", contextcapabilities.GL_ARB_compute_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", contextcapabilities.GL_ARB_copy_buffer);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", contextcapabilities.GL_ARB_copy_image);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", contextcapabilities.GL_ARB_depth_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", contextcapabilities.GL_ARB_compute_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", contextcapabilities.GL_ARB_copy_buffer);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", contextcapabilities.GL_ARB_copy_image);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", contextcapabilities.GL_ARB_depth_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", contextcapabilities.GL_ARB_depth_clamp);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", contextcapabilities.GL_ARB_depth_texture);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", contextcapabilities.GL_ARB_draw_buffers);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", contextcapabilities.GL_ARB_draw_buffers_blend);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", contextcapabilities.GL_ARB_draw_elements_base_vertex);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", contextcapabilities.GL_ARB_draw_indirect);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", contextcapabilities.GL_ARB_draw_instanced);
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", contextcapabilities.GL_ARB_explicit_attrib_location);
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", contextcapabilities.GL_ARB_explicit_uniform_location);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", contextcapabilities.GL_ARB_fragment_layer_viewport);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", contextcapabilities.GL_ARB_fragment_program);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", contextcapabilities.GL_ARB_fragment_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", contextcapabilities.GL_ARB_fragment_program_shadow);
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", contextcapabilities.GL_ARB_framebuffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", contextcapabilities.GL_ARB_framebuffer_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", contextcapabilities.GL_ARB_geometry_shader4);
        playerSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", contextcapabilities.GL_ARB_gpu_shader5);
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", contextcapabilities.GL_ARB_half_float_pixel);
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", contextcapabilities.GL_ARB_half_float_vertex);
        playerSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", contextcapabilities.GL_ARB_instanced_arrays);
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", contextcapabilities.GL_ARB_map_buffer_alignment);
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", contextcapabilities.GL_ARB_map_buffer_range);
        playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", contextcapabilities.GL_ARB_multisample);
        playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", contextcapabilities.GL_ARB_multitexture);
        playerSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", contextcapabilities.GL_ARB_occlusion_query2);
        playerSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", contextcapabilities.GL_ARB_pixel_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", contextcapabilities.GL_ARB_seamless_cube_map);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", contextcapabilities.GL_ARB_shader_objects);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", contextcapabilities.GL_ARB_shader_stencil_export);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", contextcapabilities.GL_ARB_shader_texture_lod);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow]", contextcapabilities.GL_ARB_shadow);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", contextcapabilities.GL_ARB_shadow_ambient);
        playerSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", contextcapabilities.GL_ARB_stencil_texturing);
        playerSnooper.addStatToSnooper("gl_caps[ARB_sync]", contextcapabilities.GL_ARB_sync);
        playerSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", contextcapabilities.GL_ARB_tessellation_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", contextcapabilities.GL_ARB_texture_border_clamp);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", contextcapabilities.GL_ARB_texture_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", contextcapabilities.GL_ARB_texture_cube_map);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", contextcapabilities.GL_ARB_texture_cube_map_array);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", contextcapabilities.GL_ARB_texture_non_power_of_two);
        playerSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", contextcapabilities.GL_ARB_uniform_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", contextcapabilities.GL_ARB_vertex_blend);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", contextcapabilities.GL_ARB_vertex_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", contextcapabilities.GL_ARB_vertex_program);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", contextcapabilities.GL_ARB_vertex_shader);
        playerSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", contextcapabilities.GL_EXT_bindable_uniform);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", contextcapabilities.GL_EXT_blend_equation_separate);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", contextcapabilities.GL_EXT_blend_func_separate);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", contextcapabilities.GL_EXT_blend_minmax);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", contextcapabilities.GL_EXT_blend_subtract);
        playerSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", contextcapabilities.GL_EXT_draw_instanced);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", contextcapabilities.GL_EXT_framebuffer_multisample);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", contextcapabilities.GL_EXT_framebuffer_object);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", contextcapabilities.GL_EXT_framebuffer_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", contextcapabilities.GL_EXT_geometry_shader4);
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", contextcapabilities.GL_EXT_gpu_program_parameters);
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", contextcapabilities.GL_EXT_gpu_shader4);
        playerSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", contextcapabilities.GL_EXT_multi_draw_arrays);
        playerSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", contextcapabilities.GL_EXT_packed_depth_stencil);
        playerSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", contextcapabilities.GL_EXT_paletted_texture);
        playerSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", contextcapabilities.GL_EXT_rescale_normal);
        playerSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", contextcapabilities.GL_EXT_separate_shader_objects);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", contextcapabilities.GL_EXT_shader_image_load_store);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", contextcapabilities.GL_EXT_shadow_funcs);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", contextcapabilities.GL_EXT_shared_texture_palette);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", contextcapabilities.GL_EXT_stencil_clear_tag);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", contextcapabilities.GL_EXT_stencil_two_side);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", contextcapabilities.GL_EXT_stencil_wrap);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", contextcapabilities.GL_EXT_texture_3d);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", contextcapabilities.GL_EXT_texture_array);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", contextcapabilities.GL_EXT_texture_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", contextcapabilities.GL_EXT_texture_integer);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", contextcapabilities.GL_EXT_texture_lod_bias);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", contextcapabilities.GL_EXT_texture_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", contextcapabilities.GL_EXT_vertex_shader);
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", contextcapabilities.GL_EXT_vertex_weighting);
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(35658));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(35657));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", GL11.glGetInteger(34921));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", GL11.glGetInteger(35660));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(34930));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(35071));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_max_texture_size", Minecraft.getGLMaximumTextureSize());
    }

    public static int getGLMaximumTextureSize() {
        int i2 = 16384;
        while (i2 > 0) {
            GL11.glTexImage2D(32868, 0, 6408, i2, i2, 0, 6408, 5121, null);
            int j2 = GL11.glGetTexLevelParameteri(32868, 0, 4096);
            if (j2 != 0) {
                return i2;
            }
            i2 >>= 1;
        }
        return -1;
    }

    @Override
    public boolean isSnooperEnabled() {
        return this.gameSettings.snooperEnabled;
    }

    public void setServerData(ServerData serverDataIn) {
        this.currentServerData = serverDataIn;
    }

    public ServerData getCurrentServerData() {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning() {
        return this.integratedServerIsRunning;
    }

    public boolean isSingleplayer() {
        return this.integratedServerIsRunning && this.theIntegratedServer != null;
    }

    public IntegratedServer getIntegratedServer() {
        return this.theIntegratedServer;
    }

    public static void stopIntegratedServer() {
        IntegratedServer integratedserver;
        if (theMinecraft != null && (integratedserver = theMinecraft.getIntegratedServer()) != null) {
            integratedserver.stopServer();
        }
    }

    public PlayerUsageSnooper getPlayerUsageSnooper() {
        return this.usageSnooper;
    }

    public static long getSystemTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    public boolean isFullScreen() {
        return this.fullscreen;
    }

    public static void setSession(Session session) {
        Minecraft.session = session;
    }

    public Session getSession() {
        return session;
    }

    public PropertyMap getTwitchDetails() {
        return this.twitchDetails;
    }

    public PropertyMap getProfileProperties() {
        if (this.profileProperties.isEmpty()) {
            GameProfile gameprofile = this.getSessionService().fillProfileProperties(session.getProfile(), false);
            this.profileProperties.putAll(gameprofile.getProperties());
        }
        return this.profileProperties;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public TextureManager getTextureManager() {
        return this.renderEngine;
    }

    public IResourceManager getResourceManager() {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository() {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager() {
        return this.mcLanguageManager;
    }

    public TextureMap getTextureMapBlocks() {
        return this.textureMapBlocks;
    }

    public boolean isJava64bit() {
        return this.jvm64bit;
    }

    public boolean isGamePaused() {
        return this.isGamePaused;
    }

    public SoundHandler getSoundHandler() {
        return this.mcSoundHandler;
    }

    public MusicTicker.MusicType getAmbientMusicType() {
        return this.thePlayer != null ? (this.thePlayer.worldObj.provider instanceof WorldProviderHell ? MusicTicker.MusicType.NETHER : (this.thePlayer.worldObj.provider instanceof WorldProviderEnd ? (BossStatus.bossName != null && BossStatus.statusBarTime > 0 ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END) : (this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME))) : MusicTicker.MusicType.MENU;
    }

    public IStream getTwitchStream() {
        return this.stream;
    }

    public void dispatchKeypresses() {
        int i2;
        int n2 = i2 = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
        if (!(i2 == 0 || Keyboard.isRepeatEvent() || this.currentScreen instanceof GuiControls && ((GuiControls)this.currentScreen).time > Minecraft.getSystemTime() - 20L)) {
            if (Keyboard.getEventKeyState()) {
                if (i2 == this.gameSettings.keyBindStreamStartStop.getKeyCode()) {
                    if (this.getTwitchStream().isBroadcasting()) {
                        this.getTwitchStream().stopBroadcasting();
                    } else if (this.getTwitchStream().isReadyToBroadcast()) {
                        this.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback(){

                            @Override
                            public void confirmClicked(boolean result, int id2) {
                                if (result) {
                                    Minecraft.this.getTwitchStream().func_152930_t();
                                }
                                Minecraft.this.displayGuiScreen(null);
                            }
                        }, I18n.format("stream.confirm_start", new Object[0]), "", 0));
                    } else if (this.getTwitchStream().func_152928_D() && this.getTwitchStream().func_152936_l()) {
                        if (this.theWorld != null) {
                            this.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("Not ready to start streaming yet!"));
                        }
                    } else {
                        GuiStreamUnavailable.func_152321_a(this.currentScreen);
                    }
                } else if (i2 == this.gameSettings.keyBindStreamPauseUnpause.getKeyCode()) {
                    if (this.getTwitchStream().isBroadcasting()) {
                        if (this.getTwitchStream().isPaused()) {
                            this.getTwitchStream().unpause();
                        } else {
                            this.getTwitchStream().pause();
                        }
                    }
                } else if (i2 == this.gameSettings.keyBindStreamCommercials.getKeyCode()) {
                    if (this.getTwitchStream().isBroadcasting()) {
                        this.getTwitchStream().requestCommercial();
                    }
                } else if (i2 == this.gameSettings.keyBindStreamToggleMic.getKeyCode()) {
                    this.stream.muteMicrophone(true);
                } else if (i2 == this.gameSettings.keyBindFullscreen.getKeyCode()) {
                    this.toggleFullscreen();
                } else if (i2 == this.gameSettings.keyBindScreenshot.getKeyCode()) {
                    this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
                }
            } else if (i2 == this.gameSettings.keyBindStreamToggleMic.getKeyCode()) {
                this.stream.muteMicrophone(false);
            }
        }
    }

    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    public SkinManager getSkinManager() {
        return this.skinManager;
    }

    public Entity getRenderViewEntity() {
        return this.renderViewEntity;
    }

    public void setRenderViewEntity(Entity viewingEntity) {
        this.renderViewEntity = viewingEntity;
        this.entityRenderer.loadEntityShader(viewingEntity);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule) {
        Validate.notNull(callableToSchedule);
        if (!this.isCallingFromMinecraftThread()) {
            ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callableToSchedule);
            Queue<FutureTask<?>> queue = this.scheduledTasks;
            synchronized (queue) {
                this.scheduledTasks.add(listenablefuturetask);
                return listenablefuturetask;
            }
        }
        try {
            return Futures.immediateFuture(callableToSchedule.call());
        }
        catch (Exception exception) {
            return Futures.immediateFailedCheckedFuture(exception);
        }
    }

    @Override
    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
        Validate.notNull(runnableToSchedule);
        return this.addScheduledTask(Executors.callable(runnableToSchedule));
    }

    @Override
    public boolean isCallingFromMinecraftThread() {
        return Thread.currentThread() == this.mcThread;
    }

    public BlockRendererDispatcher getBlockRendererDispatcher() {
        return this.blockRenderDispatcher;
    }

    public RenderManager getRenderManager() {
        return this.renderManager;
    }

    public RenderItem getRenderItem() {
        return this.renderItem;
    }

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    public static int getDebugFPS() {
        return debugFPS;
    }

    public FrameTimer getFrameTimer() {
        return this.frameTimer;
    }

    public static Map<String, String> getSessionInfo() {
        HashMap<String, String> map = Maps.newHashMap();
        map.put("X-Minecraft-Username", Minecraft.getMinecraft().getSession().getUsername());
        map.put("X-Minecraft-UUID", Minecraft.getMinecraft().getSession().getPlayerID());
        map.put("X-Minecraft-Version", "1.8.9");
        return map;
    }

    public boolean isConnectedToRealms() {
        return this.connectedToRealms;
    }

    public void setConnectedToRealms(boolean isConnected) {
        this.connectedToRealms = isConnected;
    }
}

