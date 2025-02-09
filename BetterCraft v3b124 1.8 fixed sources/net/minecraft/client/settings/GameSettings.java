/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.settings;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.nzxtercode.bettercraft.client.BetterCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.optifine.ClearWater;
import net.optifine.CustomColors;
import net.optifine.CustomGuis;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.Lang;
import net.optifine.NaturalTextures;
import net.optifine.RandomEntities;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import net.optifine.util.KeyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class GameSettings {
    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson = new Gson();
    private static final ParameterizedType typeListString = new ParameterizedType(){

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{String.class};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };
    private static final String[] GUISCALES = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
    private static final String[] PARTICLES = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
    private static final String[] AMBIENT_OCCLUSIONS = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
    private static final String[] STREAM_COMPRESSIONS = new String[]{"options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"};
    private static final String[] STREAM_CHAT_MODES = new String[]{"options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"};
    private static final String[] STREAM_CHAT_FILTER_MODES = new String[]{"options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"};
    private static final String[] STREAM_MIC_MODES = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
    private static final String[] CLOUDS_TYPES = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
    public float mouseSensitivity = 0.5f;
    public boolean invertMouse;
    public int renderDistanceChunks = -1;
    public boolean viewBobbing = true;
    public boolean anaglyph;
    public boolean fboEnable = true;
    public int limitFramerate = 120;
    public int clouds = 2;
    public boolean fancyGraphics = true;
    public int ambientOcclusion = 2;
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> incompatibleResourcePacks = Lists.newArrayList();
    public EntityPlayer.EnumChatVisibility chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
    public boolean chatColours = true;
    public boolean chatLinks = true;
    public boolean chatLinksPrompt = true;
    public float chatOpacity = 1.0f;
    public boolean snooperEnabled = false;
    public boolean fullScreen;
    public boolean enableVsync = true;
    public boolean useVbo = false;
    public boolean allowBlockAlternatives = true;
    public boolean reducedDebugInfo = false;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips = true;
    public boolean pauseOnLostFocus = true;
    private final Set<EnumPlayerModelParts> setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
    public boolean touchscreen;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    public float chatScale = 1.0f;
    public float chatWidth = 1.0f;
    public float chatHeightUnfocused = 0.44366196f;
    public float chatHeightFocused = 1.0f;
    public boolean showInventoryAchievementHint = false;
    public int mipmapLevels = 4;
    private Map<SoundCategory, Float> mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
    public float streamBytesPerPixel = 0.5f;
    public float streamMicVolume = 1.0f;
    public float streamGameVolume = 1.0f;
    public float streamKbps = 0.5412844f;
    public float streamFps = 0.31690142f;
    public int streamCompression = 1;
    public boolean streamSendMetadata = true;
    public String streamPreferredServer = "";
    public int streamChatEnabled = 0;
    public int streamChatUserFilter = 0;
    public int streamMicToggleBehavior = 0;
    public boolean useNativeTransport = true;
    public boolean entityShadows = true;
    public boolean realmsNotifications = false;
    public KeyBinding keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
    public KeyBinding keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
    public KeyBinding keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
    public KeyBinding keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
    public KeyBinding keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
    public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
    public KeyBinding keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
    public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
    public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
    public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
    public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
    public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
    public KeyBinding keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
    public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
    public KeyBinding keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
    public KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
    public KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
    public KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
    public KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
    public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
    public KeyBinding keyBindStreamStartStop = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
    public KeyBinding keyBindStreamPauseUnpause = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
    public KeyBinding keyBindStreamCommercials = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
    public KeyBinding keyBindStreamToggleMic = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
    public KeyBinding[] keyBindsHotbar = new KeyBinding[]{new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
    public KeyBinding[] keyBindings;
    protected Minecraft mc;
    private File optionsFile;
    public EnumDifficulty difficulty;
    public boolean hideGUI;
    public int thirdPersonView;
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;
    public boolean showLagometer;
    public String lastServer;
    public boolean smoothCamera;
    public boolean debugCamEnable;
    public float fovSetting;
    public float gammaSetting = 100.0f;
    public float saturation;
    public int guiScale = 2;
    public int particleSetting;
    public String language;
    public boolean forceUnicodeFont;
    public int ofFogType = 1;
    public float ofFogStart = 0.8f;
    public int ofMipmapType = 0;
    public boolean ofOcclusionFancy = false;
    public boolean ofSmoothFps = false;
    public boolean ofSmoothWorld = Config.isSingleProcessor();
    public boolean ofLazyChunkLoading = Config.isSingleProcessor();
    public boolean ofRenderRegions = false;
    public boolean ofSmartAnimations = false;
    public float ofAoLevel = 1.0f;
    public int ofAaLevel = 0;
    public int ofAfLevel = 1;
    public int ofClouds = 0;
    public float ofCloudsHeight = 0.0f;
    public int ofTrees = 0;
    public int ofRain = 0;
    public int ofDroppedItems = 0;
    public int ofBetterGrass = 3;
    public int ofAutoSaveTicks = 4000;
    public boolean ofLagometer = false;
    public boolean ofProfiler = false;
    public boolean ofShowFps = false;
    public boolean ofWeather = true;
    public boolean ofSky = true;
    public boolean ofStars = true;
    public boolean ofSunMoon = true;
    public int ofVignette = 0;
    public int ofChunkUpdates = 1;
    public boolean ofChunkUpdatesDynamic = false;
    public int ofTime = 0;
    public boolean ofClearWater = false;
    public boolean ofBetterSnow = false;
    public String ofFullscreenMode = "Default";
    public boolean ofSwampColors = true;
    public boolean ofRandomEntities = true;
    public boolean ofSmoothBiomes = true;
    public boolean ofCustomFonts = true;
    public boolean ofCustomColors = true;
    public boolean ofCustomSky = true;
    public boolean ofShowCapes = true;
    public int ofConnectedTextures = 2;
    public boolean ofCustomItems = true;
    public boolean ofNaturalTextures = false;
    public boolean ofEmissiveTextures = true;
    public boolean ofFastMath = false;
    public boolean ofFastRender = false;
    public int ofTranslucentBlocks = 0;
    public boolean ofDynamicFov = true;
    public boolean ofAlternateBlocks = true;
    public int ofDynamicLights = 3;
    public boolean ofCustomEntityModels = true;
    public boolean ofCustomGuis = true;
    public boolean ofShowGlErrors = true;
    public int ofScreenshotSize = 1;
    public int ofAnimatedWater = 0;
    public int ofAnimatedLava = 0;
    public boolean ofAnimatedFire = true;
    public boolean ofAnimatedPortal = true;
    public boolean ofAnimatedRedstone = true;
    public boolean ofAnimatedExplosion = true;
    public boolean ofAnimatedFlame = true;
    public boolean ofAnimatedSmoke = true;
    public boolean ofVoidParticles = true;
    public boolean ofWaterParticles = true;
    public boolean ofRainSplash = true;
    public boolean ofPortalParticles = true;
    public boolean ofPotionParticles = true;
    public boolean ofFireworkParticles = true;
    public boolean ofDrippingWaterLava = true;
    public boolean ofAnimatedTerrain = true;
    public boolean ofAnimatedTextures = true;
    public static final int DEFAULT = 0;
    public static final int FAST = 1;
    public static final int FANCY = 2;
    public static final int OFF = 3;
    public static final int SMART = 4;
    public static final int ANIM_ON = 0;
    public static final int ANIM_GENERATED = 1;
    public static final int ANIM_OFF = 2;
    public static final String DEFAULT_STR = "Default";
    private static final int[] OF_TREES_VALUES;
    private static final int[] OF_DYNAMIC_LIGHTS;
    private static final String[] KEYS_DYNAMIC_LIGHTS;
    public KeyBinding ofKeyBindZoom;
    private File optionsFileOF;
    public KeyBinding CLIENT_GUI_MOD_POS;
    public KeyBinding CLIENT_GUI_NBTEDIT;
    public KeyBinding CLIENT_GUI_MOD_STATE;
    public KeyBinding CLIENT_CREATIVE_TAB;

    static {
        int[] nArray = new int[4];
        nArray[1] = 1;
        nArray[2] = 4;
        nArray[3] = 2;
        OF_TREES_VALUES = nArray;
        OF_DYNAMIC_LIGHTS = new int[]{3, 1, 2};
        KEYS_DYNAMIC_LIGHTS = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
    }

    public GameSettings(Minecraft mcIn, File optionsFileIn) {
        BetterCraft.getInstance();
        this.CLIENT_GUI_MOD_POS = new KeyBinding("Mod Pos", 50, BetterCraft.clientName);
        BetterCraft.getInstance();
        this.CLIENT_GUI_NBTEDIT = new KeyBinding("NBTEdit", 49, BetterCraft.clientName);
        BetterCraft.getInstance();
        this.CLIENT_GUI_MOD_STATE = new KeyBinding("Mod State", 54, BetterCraft.clientName);
        BetterCraft.getInstance();
        this.CLIENT_CREATIVE_TAB = new KeyBinding("Creative Item", 23, BetterCraft.clientName);
        this.keyBindings = ArrayUtils.addAll(new KeyBinding[]{this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindStreamStartStop, this.keyBindStreamPauseUnpause, this.keyBindStreamCommercials, this.keyBindStreamToggleMic, this.keyBindFullscreen, this.keyBindSpectatorOutlines}, this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 110.0f;
        this.language = "en_US";
        this.forceUnicodeFont = false;
        this.mc = mcIn;
        this.optionsFile = new File(optionsFileIn, "options.txt");
        if (mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
            Options.RENDER_DISTANCE.setValueMax(32.0f);
            long i2 = 1000000L;
            if (Runtime.getRuntime().maxMemory() >= 1500L * i2) {
                Options.RENDER_DISTANCE.setValueMax(48.0f);
            }
            if (Runtime.getRuntime().maxMemory() >= 2500L * i2) {
                Options.RENDER_DISTANCE.setValueMax(64.0f);
            }
        } else {
            Options.RENDER_DISTANCE.setValueMax(16.0f);
        }
        this.renderDistanceChunks = mcIn.isJava64bit() ? 12 : 8;
        this.optionsFileOF = new File(optionsFileIn, "optionsof.txt");
        this.limitFramerate = (int)Options.FRAMERATE_LIMIT.getValueMax();
        this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 46, "key.categories.misc");
        this.keyBindings = ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom);
        KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[]{this.ofKeyBindZoom});
        this.renderDistanceChunks = 8;
        this.loadOptions();
        Config.initGameSettings(this);
        this.addClientKeyBinds();
    }

    private void addClientKeyBinds() {
        this.keyBindings = ArrayUtils.add(this.keyBindings, this.CLIENT_GUI_MOD_POS);
        this.keyBindings = ArrayUtils.add(this.keyBindings, this.CLIENT_GUI_NBTEDIT);
        this.keyBindings = ArrayUtils.add(this.keyBindings, this.CLIENT_GUI_MOD_STATE);
        this.keyBindings = ArrayUtils.add(this.keyBindings, this.CLIENT_CREATIVE_TAB);
    }

    public GameSettings() {
        BetterCraft.getInstance();
        this.CLIENT_GUI_MOD_POS = new KeyBinding("Mod Pos", 50, BetterCraft.clientName);
        BetterCraft.getInstance();
        this.CLIENT_GUI_NBTEDIT = new KeyBinding("NBTEdit", 49, BetterCraft.clientName);
        BetterCraft.getInstance();
        this.CLIENT_GUI_MOD_STATE = new KeyBinding("Mod State", 54, BetterCraft.clientName);
        BetterCraft.getInstance();
        this.CLIENT_CREATIVE_TAB = new KeyBinding("Creative Item", 23, BetterCraft.clientName);
        this.keyBindings = ArrayUtils.addAll(new KeyBinding[]{this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindStreamStartStop, this.keyBindStreamPauseUnpause, this.keyBindStreamCommercials, this.keyBindStreamToggleMic, this.keyBindFullscreen, this.keyBindSpectatorOutlines}, this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 70.0f;
        this.language = "en_US";
        this.forceUnicodeFont = false;
    }

    public static String getKeyDisplayString(int key) {
        return key < 0 ? I18n.format("key.mouseButton", key + 101) : (key < 256 ? Keyboard.getKeyName(key) : String.format("%c", Character.valueOf((char)(key - 256))).toUpperCase());
    }

    public static boolean isKeyDown(KeyBinding key) {
        return key.getKeyCode() == 0 ? false : (key.getKeyCode() < 0 ? Mouse.isButtonDown(key.getKeyCode() + 100) : Keyboard.isKeyDown(key.getKeyCode()));
    }

    public void setOptionKeyBinding(KeyBinding key, int keyCode) {
        key.setKeyCode(keyCode);
        this.saveOptions();
    }

    public void setOptionFloatValue(Options settingsOption, float value) {
        this.setOptionFloatValueOF(settingsOption, value);
        if (settingsOption == Options.SENSITIVITY) {
            this.mouseSensitivity = value;
        }
        if (settingsOption == Options.FOV) {
            this.fovSetting = value;
        }
        if (settingsOption == Options.GAMMA) {
            this.gammaSetting = value;
        }
        if (settingsOption == Options.FRAMERATE_LIMIT) {
            this.limitFramerate = (int)value;
            this.enableVsync = false;
            if (this.limitFramerate <= 0) {
                this.limitFramerate = (int)Options.FRAMERATE_LIMIT.getValueMax();
                this.enableVsync = true;
            }
            this.updateVSync();
        }
        if (settingsOption == Options.CHAT_OPACITY) {
            this.chatOpacity = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }
        if (settingsOption == Options.CHAT_HEIGHT_FOCUSED) {
            this.chatHeightFocused = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }
        if (settingsOption == Options.CHAT_HEIGHT_UNFOCUSED) {
            this.chatHeightUnfocused = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }
        if (settingsOption == Options.CHAT_WIDTH) {
            this.chatWidth = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }
        if (settingsOption == Options.CHAT_SCALE) {
            this.chatScale = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }
        if (settingsOption == Options.MIPMAP_LEVELS) {
            int i2 = this.mipmapLevels;
            this.mipmapLevels = (int)value;
            if ((float)i2 != value) {
                this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.mc.getTextureMapBlocks().setBlurMipmapDirect(false, this.mipmapLevels > 0);
                this.mc.scheduleResourcesRefresh();
            }
        }
        if (settingsOption == Options.BLOCK_ALTERNATIVES) {
            this.allowBlockAlternatives = !this.allowBlockAlternatives;
            this.mc.renderGlobal.loadRenderers();
        }
        if (settingsOption == Options.RENDER_DISTANCE) {
            this.renderDistanceChunks = (int)value;
            this.mc.renderGlobal.setDisplayListEntitiesDirty();
        }
        if (settingsOption == Options.STREAM_BYTES_PER_PIXEL) {
            this.streamBytesPerPixel = value;
        }
        if (settingsOption == Options.STREAM_VOLUME_MIC) {
            this.streamMicVolume = value;
            this.mc.getTwitchStream().updateStreamVolume();
        }
        if (settingsOption == Options.STREAM_VOLUME_SYSTEM) {
            this.streamGameVolume = value;
            this.mc.getTwitchStream().updateStreamVolume();
        }
        if (settingsOption == Options.STREAM_KBPS) {
            this.streamKbps = value;
        }
        if (settingsOption == Options.STREAM_FPS) {
            this.streamFps = value;
        }
    }

    public void setOptionValue(Options settingsOption, int value) {
        this.setOptionValueOF(settingsOption, value);
        if (settingsOption == Options.INVERT_MOUSE) {
            boolean bl2 = this.invertMouse = !this.invertMouse;
        }
        if (settingsOption == Options.GUI_SCALE) {
            this.guiScale += value;
            if (GuiScreen.isShiftKeyDown()) {
                this.guiScale = 0;
            }
            DisplayMode displaymode = Config.getLargestDisplayMode();
            int i2 = displaymode.getWidth() / 320;
            int j2 = displaymode.getHeight() / 240;
            int k2 = Math.min(i2, j2);
            if (this.guiScale < 0) {
                this.guiScale = k2 - 1;
            }
            if (this.mc.isUnicode() && this.guiScale % 2 != 0) {
                this.guiScale += value;
            }
            if (this.guiScale < 0 || this.guiScale >= k2) {
                this.guiScale = 0;
            }
        }
        if (settingsOption == Options.PARTICLES) {
            this.particleSetting = (this.particleSetting + value) % 3;
        }
        if (settingsOption == Options.VIEW_BOBBING) {
            boolean bl3 = this.viewBobbing = !this.viewBobbing;
        }
        if (settingsOption == Options.RENDER_CLOUDS) {
            this.clouds = (this.clouds + value) % 3;
        }
        if (settingsOption == Options.FORCE_UNICODE_FONT) {
            this.forceUnicodeFont = !this.forceUnicodeFont;
            this.mc.fontRendererObj.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
        }
        if (settingsOption == Options.FBO_ENABLE) {
            boolean bl4 = this.fboEnable = !this.fboEnable;
        }
        if (settingsOption == Options.ANAGLYPH) {
            if (!this.anaglyph && Config.isShaders()) {
                Config.showGuiMessage(Lang.get("of.message.an.shaders1"), Lang.get("of.message.an.shaders2"));
                return;
            }
            this.anaglyph = !this.anaglyph;
            this.mc.refreshResources();
        }
        if (settingsOption == Options.GRAPHICS) {
            this.fancyGraphics = !this.fancyGraphics;
            this.updateRenderClouds();
            this.mc.renderGlobal.loadRenderers();
        }
        if (settingsOption == Options.AMBIENT_OCCLUSION) {
            this.ambientOcclusion = (this.ambientOcclusion + value) % 3;
            this.mc.renderGlobal.loadRenderers();
        }
        if (settingsOption == Options.CHAT_VISIBILITY) {
            this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + value) % 3);
        }
        if (settingsOption == Options.STREAM_COMPRESSION) {
            this.streamCompression = (this.streamCompression + value) % 3;
        }
        if (settingsOption == Options.STREAM_SEND_METADATA) {
            boolean bl5 = this.streamSendMetadata = !this.streamSendMetadata;
        }
        if (settingsOption == Options.STREAM_CHAT_ENABLED) {
            this.streamChatEnabled = (this.streamChatEnabled + value) % 3;
        }
        if (settingsOption == Options.STREAM_CHAT_USER_FILTER) {
            this.streamChatUserFilter = (this.streamChatUserFilter + value) % 3;
        }
        if (settingsOption == Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
            this.streamMicToggleBehavior = (this.streamMicToggleBehavior + value) % 2;
        }
        if (settingsOption == Options.CHAT_COLOR) {
            boolean bl6 = this.chatColours = !this.chatColours;
        }
        if (settingsOption == Options.CHAT_LINKS) {
            boolean bl7 = this.chatLinks = !this.chatLinks;
        }
        if (settingsOption == Options.CHAT_LINKS_PROMPT) {
            boolean bl8 = this.chatLinksPrompt = !this.chatLinksPrompt;
        }
        if (settingsOption == Options.SNOOPER_ENABLED) {
            boolean bl9 = this.snooperEnabled = !this.snooperEnabled;
        }
        if (settingsOption == Options.TOUCHSCREEN) {
            boolean bl10 = this.touchscreen = !this.touchscreen;
        }
        if (settingsOption == Options.USE_FULLSCREEN) {
            boolean bl11 = this.fullScreen = !this.fullScreen;
            if (this.mc.isFullScreen() != this.fullScreen) {
                this.mc.toggleFullscreen();
            }
        }
        if (settingsOption == Options.ENABLE_VSYNC) {
            this.enableVsync = !this.enableVsync;
            Display.setVSyncEnabled(this.enableVsync);
        }
        if (settingsOption == Options.USE_VBO) {
            this.useVbo = !this.useVbo;
            this.mc.renderGlobal.loadRenderers();
        }
        if (settingsOption == Options.BLOCK_ALTERNATIVES) {
            this.allowBlockAlternatives = !this.allowBlockAlternatives;
            this.mc.renderGlobal.loadRenderers();
        }
        if (settingsOption == Options.REDUCED_DEBUG_INFO) {
            boolean bl12 = this.reducedDebugInfo = !this.reducedDebugInfo;
        }
        if (settingsOption == Options.ENTITY_SHADOWS) {
            boolean bl13 = this.entityShadows = !this.entityShadows;
        }
        if (settingsOption == Options.REALMS_NOTIFICATIONS) {
            this.realmsNotifications = !this.realmsNotifications;
        }
        this.saveOptions();
    }

    public float getOptionFloatValue(Options settingOption) {
        float f2 = this.getOptionFloatValueOF(settingOption);
        return f2 != Float.MAX_VALUE ? f2 : (settingOption == Options.FOV ? this.fovSetting : (settingOption == Options.GAMMA ? this.gammaSetting : (settingOption == Options.SATURATION ? this.saturation : (settingOption == Options.SENSITIVITY ? this.mouseSensitivity : (settingOption == Options.CHAT_OPACITY ? this.chatOpacity : (settingOption == Options.CHAT_HEIGHT_FOCUSED ? this.chatHeightFocused : (settingOption == Options.CHAT_HEIGHT_UNFOCUSED ? this.chatHeightUnfocused : (settingOption == Options.CHAT_SCALE ? this.chatScale : (settingOption == Options.CHAT_WIDTH ? this.chatWidth : (settingOption == Options.FRAMERATE_LIMIT ? (float)this.limitFramerate : (settingOption == Options.MIPMAP_LEVELS ? (float)this.mipmapLevels : (settingOption == Options.RENDER_DISTANCE ? (float)this.renderDistanceChunks : (settingOption == Options.STREAM_BYTES_PER_PIXEL ? this.streamBytesPerPixel : (settingOption == Options.STREAM_VOLUME_MIC ? this.streamMicVolume : (settingOption == Options.STREAM_VOLUME_SYSTEM ? this.streamGameVolume : (settingOption == Options.STREAM_KBPS ? this.streamKbps : (settingOption == Options.STREAM_FPS ? this.streamFps : 0.0f)))))))))))))))));
    }

    public boolean getOptionOrdinalValue(Options settingOption) {
        switch (settingOption) {
            case INVERT_MOUSE: {
                return this.invertMouse;
            }
            case VIEW_BOBBING: {
                return this.viewBobbing;
            }
            case ANAGLYPH: {
                return this.anaglyph;
            }
            case FBO_ENABLE: {
                return this.fboEnable;
            }
            case CHAT_COLOR: {
                return this.chatColours;
            }
            case CHAT_LINKS: {
                return this.chatLinks;
            }
            case CHAT_LINKS_PROMPT: {
                return this.chatLinksPrompt;
            }
            case SNOOPER_ENABLED: {
                return this.snooperEnabled;
            }
            case USE_FULLSCREEN: {
                return this.fullScreen;
            }
            case ENABLE_VSYNC: {
                return this.enableVsync;
            }
            case USE_VBO: {
                return this.useVbo;
            }
            case TOUCHSCREEN: {
                return this.touchscreen;
            }
            case STREAM_SEND_METADATA: {
                return this.streamSendMetadata;
            }
            case FORCE_UNICODE_FONT: {
                return this.forceUnicodeFont;
            }
            case BLOCK_ALTERNATIVES: {
                return this.allowBlockAlternatives;
            }
            case REDUCED_DEBUG_INFO: {
                return this.reducedDebugInfo;
            }
            case ENTITY_SHADOWS: {
                return this.entityShadows;
            }
            case REALMS_NOTIFICATIONS: {
                return this.realmsNotifications;
            }
        }
        return false;
    }

    private static String getTranslation(String[] strArray, int index) {
        if (index < 0 || index >= strArray.length) {
            index = 0;
        }
        return I18n.format(strArray[index], new Object[0]);
    }

    public String getKeyBinding(Options settingOption) {
        String s2 = this.getKeyBindingOF(settingOption);
        if (s2 != null) {
            return s2;
        }
        String s1 = String.valueOf(I18n.format(settingOption.getEnumString(), new Object[0])) + ": ";
        if (settingOption.getEnumFloat()) {
            float f1 = this.getOptionFloatValue(settingOption);
            float f2 = settingOption.normalizeValue(f1);
            return settingOption == Options.MIPMAP_LEVELS && (double)f1 >= 4.0 ? String.valueOf(s1) + Lang.get("of.general.max") : (settingOption == Options.SENSITIVITY ? (f2 == 0.0f ? String.valueOf(s1) + I18n.format("options.sensitivity.min", new Object[0]) : (f2 == 1.0f ? String.valueOf(s1) + I18n.format("options.sensitivity.max", new Object[0]) : String.valueOf(s1) + (int)(f2 * 200.0f) + "%")) : (settingOption == Options.FOV ? (f1 == 70.0f ? String.valueOf(s1) + I18n.format("options.fov.min", new Object[0]) : (f1 == 110.0f ? String.valueOf(s1) + I18n.format("options.fov.max", new Object[0]) : String.valueOf(s1) + (int)f1)) : (settingOption == Options.FRAMERATE_LIMIT ? (f1 == settingOption.valueMax ? String.valueOf(s1) + I18n.format("options.framerateLimit.max", new Object[0]) : String.valueOf(s1) + (int)f1 + " fps") : (settingOption == Options.RENDER_CLOUDS ? (f1 == settingOption.valueMin ? String.valueOf(s1) + I18n.format("options.cloudHeight.min", new Object[0]) : String.valueOf(s1) + ((int)f1 + 128)) : (settingOption == Options.GAMMA ? (f2 == 0.0f ? String.valueOf(s1) + I18n.format("options.gamma.min", new Object[0]) : (f2 == 1.0f ? String.valueOf(s1) + I18n.format("options.gamma.max", new Object[0]) : String.valueOf(s1) + "+" + (int)(f2 * 100.0f) + "%")) : (settingOption == Options.SATURATION ? String.valueOf(s1) + (int)(f2 * 400.0f) + "%" : (settingOption == Options.CHAT_OPACITY ? String.valueOf(s1) + (int)(f2 * 90.0f + 10.0f) + "%" : (settingOption == Options.CHAT_HEIGHT_UNFOCUSED ? String.valueOf(s1) + GuiNewChat.calculateChatboxHeight(f2) + "px" : (settingOption == Options.CHAT_HEIGHT_FOCUSED ? String.valueOf(s1) + GuiNewChat.calculateChatboxHeight(f2) + "px" : (settingOption == Options.CHAT_WIDTH ? String.valueOf(s1) + GuiNewChat.calculateChatboxWidth(f2) + "px" : (settingOption == Options.RENDER_DISTANCE ? String.valueOf(s1) + (int)f1 + " chunks" : (settingOption == Options.MIPMAP_LEVELS ? (f1 == 0.0f ? String.valueOf(s1) + I18n.format("options.off", new Object[0]) : String.valueOf(s1) + (int)f1) : (settingOption == Options.STREAM_FPS ? String.valueOf(s1) + TwitchStream.formatStreamFps(f2) + " fps" : (settingOption == Options.STREAM_KBPS ? String.valueOf(s1) + TwitchStream.formatStreamKbps(f2) + " Kbps" : (settingOption == Options.STREAM_BYTES_PER_PIXEL ? String.valueOf(s1) + String.format("%.3f bpp", Float.valueOf(TwitchStream.formatStreamBps(f2))) : (f2 == 0.0f ? String.valueOf(s1) + I18n.format("options.off", new Object[0]) : String.valueOf(s1) + (int)(f2 * 100.0f) + "%"))))))))))))))));
        }
        if (settingOption.getEnumBoolean()) {
            boolean flag = this.getOptionOrdinalValue(settingOption);
            return flag ? String.valueOf(s1) + I18n.format("options.on", new Object[0]) : String.valueOf(s1) + I18n.format("options.off", new Object[0]);
        }
        if (settingOption == Options.GUI_SCALE) {
            return this.guiScale >= GUISCALES.length ? String.valueOf(s1) + this.guiScale + "x" : String.valueOf(s1) + GameSettings.getTranslation(GUISCALES, this.guiScale);
        }
        if (settingOption == Options.CHAT_VISIBILITY) {
            return String.valueOf(s1) + I18n.format(this.chatVisibility.getResourceKey(), new Object[0]);
        }
        if (settingOption == Options.PARTICLES) {
            return String.valueOf(s1) + GameSettings.getTranslation(PARTICLES, this.particleSetting);
        }
        if (settingOption == Options.AMBIENT_OCCLUSION) {
            return String.valueOf(s1) + GameSettings.getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
        }
        if (settingOption == Options.STREAM_COMPRESSION) {
            return String.valueOf(s1) + GameSettings.getTranslation(STREAM_COMPRESSIONS, this.streamCompression);
        }
        if (settingOption == Options.STREAM_CHAT_ENABLED) {
            return String.valueOf(s1) + GameSettings.getTranslation(STREAM_CHAT_MODES, this.streamChatEnabled);
        }
        if (settingOption == Options.STREAM_CHAT_USER_FILTER) {
            return String.valueOf(s1) + GameSettings.getTranslation(STREAM_CHAT_FILTER_MODES, this.streamChatUserFilter);
        }
        if (settingOption == Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
            return String.valueOf(s1) + GameSettings.getTranslation(STREAM_MIC_MODES, this.streamMicToggleBehavior);
        }
        if (settingOption == Options.RENDER_CLOUDS) {
            return String.valueOf(s1) + GameSettings.getTranslation(CLOUDS_TYPES, this.clouds);
        }
        if (settingOption == Options.GRAPHICS) {
            if (this.fancyGraphics) {
                return String.valueOf(s1) + I18n.format("options.graphics.fancy", new Object[0]);
            }
            String s22 = "options.graphics.fast";
            return String.valueOf(s1) + I18n.format("options.graphics.fast", new Object[0]);
        }
        return s1;
    }

    public void loadOptions() {
        block84: {
            FileInputStream fileinputstream;
            block83: {
                fileinputstream = null;
                try {
                    if (!this.optionsFile.exists()) break block83;
                    fileinputstream = new FileInputStream(this.optionsFile);
                    BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(fileinputstream));
                    String s2 = "";
                    this.mapSoundLevels.clear();
                    while ((s2 = bufferedreader.readLine()) != null) {
                        try {
                            String[] astring = s2.split(":");
                            if (astring[0].equals("mouseSensitivity")) {
                                this.mouseSensitivity = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("fov")) {
                                this.fovSetting = this.parseFloat(astring[1]) * 40.0f + 70.0f;
                            }
                            if (astring[0].equals("gamma")) {
                                this.gammaSetting = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("saturation")) {
                                this.saturation = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("invertYMouse")) {
                                this.invertMouse = astring[1].equals("true");
                            }
                            if (astring[0].equals("renderDistance")) {
                                this.renderDistanceChunks = Integer.parseInt(astring[1]);
                            }
                            if (astring[0].equals("guiScale")) {
                                this.guiScale = Integer.parseInt(astring[1]);
                            }
                            if (astring[0].equals("particles")) {
                                this.particleSetting = Integer.parseInt(astring[1]);
                            }
                            if (astring[0].equals("bobView")) {
                                this.viewBobbing = astring[1].equals("true");
                            }
                            if (astring[0].equals("anaglyph3d")) {
                                this.anaglyph = astring[1].equals("true");
                            }
                            if (astring[0].equals("maxFps")) {
                                this.limitFramerate = Integer.parseInt(astring[1]);
                                if (this.enableVsync) {
                                    this.limitFramerate = (int)Options.FRAMERATE_LIMIT.getValueMax();
                                }
                                if (this.limitFramerate <= 0) {
                                    this.limitFramerate = (int)Options.FRAMERATE_LIMIT.getValueMax();
                                }
                            }
                            if (astring[0].equals("fboEnable")) {
                                this.fboEnable = astring[1].equals("true");
                            }
                            if (astring[0].equals("difficulty")) {
                                this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(astring[1]));
                            }
                            if (astring[0].equals("fancyGraphics")) {
                                this.fancyGraphics = astring[1].equals("true");
                                this.updateRenderClouds();
                            }
                            if (astring[0].equals("ao")) {
                                this.ambientOcclusion = astring[1].equals("true") ? 2 : (astring[1].equals("false") ? 0 : Integer.parseInt(astring[1]));
                            }
                            if (astring[0].equals("renderClouds")) {
                                if (astring[1].equals("true")) {
                                    this.clouds = 2;
                                } else if (astring[1].equals("false")) {
                                    this.clouds = 0;
                                } else if (astring[1].equals("fast")) {
                                    this.clouds = 1;
                                }
                            }
                            if (astring[0].equals("resourcePacks")) {
                                this.resourcePacks = (List)gson.fromJson(s2.substring(s2.indexOf(58) + 1), (Type)typeListString);
                                if (this.resourcePacks == null) {
                                    this.resourcePacks = Lists.newArrayList();
                                }
                            }
                            if (astring[0].equals("incompatibleResourcePacks")) {
                                this.incompatibleResourcePacks = (List)gson.fromJson(s2.substring(s2.indexOf(58) + 1), (Type)typeListString);
                                if (this.incompatibleResourcePacks == null) {
                                    this.incompatibleResourcePacks = Lists.newArrayList();
                                }
                            }
                            if (astring[0].equals("lastServer") && astring.length >= 2) {
                                this.lastServer = s2.substring(s2.indexOf(58) + 1);
                            }
                            if (astring[0].equals("lang") && astring.length >= 2) {
                                this.language = astring[1];
                            }
                            if (astring[0].equals("chatVisibility")) {
                                this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(astring[1]));
                            }
                            if (astring[0].equals("chatColors")) {
                                this.chatColours = astring[1].equals("true");
                            }
                            if (astring[0].equals("chatLinks")) {
                                this.chatLinks = astring[1].equals("true");
                            }
                            if (astring[0].equals("chatLinksPrompt")) {
                                this.chatLinksPrompt = astring[1].equals("true");
                            }
                            if (astring[0].equals("chatOpacity")) {
                                this.chatOpacity = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("snooperEnabled")) {
                                this.snooperEnabled = astring[1].equals("true");
                            }
                            if (astring[0].equals("fullscreen")) {
                                this.fullScreen = astring[1].equals("true");
                            }
                            if (astring[0].equals("enableVsync")) {
                                this.enableVsync = astring[1].equals("true");
                                if (this.enableVsync) {
                                    this.limitFramerate = (int)Options.FRAMERATE_LIMIT.getValueMax();
                                }
                                this.updateVSync();
                            }
                            if (astring[0].equals("useVbo")) {
                                this.useVbo = astring[1].equals("true");
                            }
                            if (astring[0].equals("hideServerAddress")) {
                                this.hideServerAddress = astring[1].equals("true");
                            }
                            if (astring[0].equals("advancedItemTooltips")) {
                                this.advancedItemTooltips = astring[1].equals("true");
                            }
                            if (astring[0].equals("pauseOnLostFocus")) {
                                this.pauseOnLostFocus = astring[1].equals("true");
                            }
                            if (astring[0].equals("touchscreen")) {
                                this.touchscreen = astring[1].equals("true");
                            }
                            if (astring[0].equals("overrideHeight")) {
                                this.overrideHeight = Integer.parseInt(astring[1]);
                            }
                            if (astring[0].equals("overrideWidth")) {
                                this.overrideWidth = Integer.parseInt(astring[1]);
                            }
                            if (astring[0].equals("heldItemTooltips")) {
                                this.heldItemTooltips = astring[1].equals("true");
                            }
                            if (astring[0].equals("chatHeightFocused")) {
                                this.chatHeightFocused = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("chatHeightUnfocused")) {
                                this.chatHeightUnfocused = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("chatScale")) {
                                this.chatScale = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("chatWidth")) {
                                this.chatWidth = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("showInventoryAchievementHint")) {
                                this.showInventoryAchievementHint = astring[1].equals("true");
                            }
                            if (astring[0].equals("mipmapLevels")) {
                                this.mipmapLevels = Integer.parseInt(astring[1]);
                            }
                            if (astring[0].equals("streamBytesPerPixel")) {
                                this.streamBytesPerPixel = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("streamMicVolume")) {
                                this.streamMicVolume = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("streamSystemVolume")) {
                                this.streamGameVolume = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("streamKbps")) {
                                this.streamKbps = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("streamFps")) {
                                this.streamFps = this.parseFloat(astring[1]);
                            }
                            if (astring[0].equals("streamCompression")) {
                                this.streamCompression = Integer.parseInt(astring[1]);
                            }
                            if (astring[0].equals("streamSendMetadata")) {
                                this.streamSendMetadata = astring[1].equals("true");
                            }
                            if (astring[0].equals("streamPreferredServer") && astring.length >= 2) {
                                this.streamPreferredServer = s2.substring(s2.indexOf(58) + 1);
                            }
                            if (astring[0].equals("streamChatEnabled")) {
                                this.streamChatEnabled = Integer.parseInt(astring[1]);
                            }
                            if (astring[0].equals("streamChatUserFilter")) {
                                this.streamChatUserFilter = Integer.parseInt(astring[1]);
                            }
                            if (astring[0].equals("streamMicToggleBehavior")) {
                                this.streamMicToggleBehavior = Integer.parseInt(astring[1]);
                            }
                            if (astring[0].equals("forceUnicodeFont")) {
                                this.forceUnicodeFont = astring[1].equals("true");
                            }
                            if (astring[0].equals("allowBlockAlternatives")) {
                                this.allowBlockAlternatives = astring[1].equals("true");
                            }
                            if (astring[0].equals("reducedDebugInfo")) {
                                this.reducedDebugInfo = astring[1].equals("true");
                            }
                            if (astring[0].equals("useNativeTransport")) {
                                this.useNativeTransport = astring[1].equals("true");
                            }
                            if (astring[0].equals("entityShadows")) {
                                this.entityShadows = astring[1].equals("true");
                            }
                            if (astring[0].equals("realmsNotifications")) {
                                this.realmsNotifications = astring[1].equals("true");
                            }
                            Object[] objectArray = this.keyBindings;
                            int n2 = this.keyBindings.length;
                            int n3 = 0;
                            while (n3 < n2) {
                                KeyBinding keybinding = objectArray[n3];
                                if (astring[0].equals("key_" + keybinding.getKeyDescription())) {
                                    keybinding.setKeyCode(Integer.parseInt(astring[1]));
                                }
                                ++n3;
                            }
                            objectArray = SoundCategory.values();
                            n2 = objectArray.length;
                            n3 = 0;
                            while (n3 < n2) {
                                KeyBinding soundcategory = objectArray[n3];
                                if (astring[0].equals("soundCategory_" + ((SoundCategory)((Object)soundcategory)).getCategoryName())) {
                                    this.mapSoundLevels.put((SoundCategory)((Object)soundcategory), Float.valueOf(this.parseFloat(astring[1])));
                                }
                                ++n3;
                            }
                            objectArray = EnumPlayerModelParts.values();
                            n2 = objectArray.length;
                            n3 = 0;
                            while (n3 < n2) {
                                KeyBinding enumplayermodelparts = objectArray[n3];
                                if (astring[0].equals("modelPart_" + ((EnumPlayerModelParts)((Object)enumplayermodelparts)).getPartName())) {
                                    this.setModelPartEnabled((EnumPlayerModelParts)((Object)enumplayermodelparts), astring[1].equals("true"));
                                }
                                ++n3;
                            }
                        }
                        catch (Exception exception) {
                            logger.warn("Skipping bad option: " + s2);
                            exception.printStackTrace();
                        }
                    }
                    KeyBinding.resetKeyBindingArrayAndHash();
                    bufferedreader.close();
                    IOUtils.closeQuietly(fileinputstream);
                    break block84;
                }
                catch (Exception exception1) {
                    logger.error("Failed to load options", (Throwable)exception1);
                    break block84;
                }
                finally {
                    IOUtils.closeQuietly(fileinputstream);
                }
            }
            IOUtils.closeQuietly(fileinputstream);
            return;
        }
        this.loadOfOptions();
    }

    private float parseFloat(String str) {
        return str.equals("true") ? 1.0f : (str.equals("false") ? 0.0f : Float.parseFloat(str));
    }

    public void saveOptions() {
        Object object;
        if (Reflector.FMLClientHandler.exists() && (object = Reflector.call(Reflector.FMLClientHandler_instance, new Object[0])) != null && Reflector.callBoolean(object, Reflector.FMLClientHandler_isLoading, new Object[0])) {
            return;
        }
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.optionsFile));
            printwriter.println("invertYMouse:" + this.invertMouse);
            printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
            printwriter.println("fov:" + (this.fovSetting - 70.0f) / 40.0f);
            printwriter.println("gamma:" + this.gammaSetting);
            printwriter.println("saturation:" + this.saturation);
            printwriter.println("renderDistance:" + this.renderDistanceChunks);
            printwriter.println("guiScale:" + this.guiScale);
            printwriter.println("particles:" + this.particleSetting);
            printwriter.println("bobView:" + this.viewBobbing);
            printwriter.println("anaglyph3d:" + this.anaglyph);
            printwriter.println("maxFps:" + this.limitFramerate);
            printwriter.println("fboEnable:" + this.fboEnable);
            printwriter.println("difficulty:" + this.difficulty.getDifficultyId());
            printwriter.println("fancyGraphics:" + this.fancyGraphics);
            printwriter.println("ao:" + this.ambientOcclusion);
            switch (this.clouds) {
                case 0: {
                    printwriter.println("renderClouds:false");
                    break;
                }
                case 1: {
                    printwriter.println("renderClouds:fast");
                    break;
                }
                case 2: {
                    printwriter.println("renderClouds:true");
                }
            }
            printwriter.println("resourcePacks:" + gson.toJson(this.resourcePacks));
            printwriter.println("incompatibleResourcePacks:" + gson.toJson(this.incompatibleResourcePacks));
            printwriter.println("lastServer:" + this.lastServer);
            printwriter.println("lang:" + this.language);
            printwriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
            printwriter.println("chatColors:" + this.chatColours);
            printwriter.println("chatLinks:" + this.chatLinks);
            printwriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
            printwriter.println("chatOpacity:" + this.chatOpacity);
            printwriter.println("snooperEnabled:" + this.snooperEnabled);
            printwriter.println("fullscreen:" + this.fullScreen);
            printwriter.println("enableVsync:" + this.enableVsync);
            printwriter.println("useVbo:" + this.useVbo);
            printwriter.println("hideServerAddress:" + this.hideServerAddress);
            printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
            printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            printwriter.println("touchscreen:" + this.touchscreen);
            printwriter.println("overrideWidth:" + this.overrideWidth);
            printwriter.println("overrideHeight:" + this.overrideHeight);
            printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
            printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
            printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            printwriter.println("chatScale:" + this.chatScale);
            printwriter.println("chatWidth:" + this.chatWidth);
            printwriter.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
            printwriter.println("mipmapLevels:" + this.mipmapLevels);
            printwriter.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
            printwriter.println("streamMicVolume:" + this.streamMicVolume);
            printwriter.println("streamSystemVolume:" + this.streamGameVolume);
            printwriter.println("streamKbps:" + this.streamKbps);
            printwriter.println("streamFps:" + this.streamFps);
            printwriter.println("streamCompression:" + this.streamCompression);
            printwriter.println("streamSendMetadata:" + this.streamSendMetadata);
            printwriter.println("streamPreferredServer:" + this.streamPreferredServer);
            printwriter.println("streamChatEnabled:" + this.streamChatEnabled);
            printwriter.println("streamChatUserFilter:" + this.streamChatUserFilter);
            printwriter.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
            printwriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
            printwriter.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
            printwriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
            printwriter.println("useNativeTransport:" + this.useNativeTransport);
            printwriter.println("entityShadows:" + this.entityShadows);
            printwriter.println("realmsNotifications:" + this.realmsNotifications);
            Object[] objectArray = this.keyBindings;
            int n2 = this.keyBindings.length;
            int n3 = 0;
            while (n3 < n2) {
                KeyBinding keybinding = objectArray[n3];
                printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getKeyCode());
                ++n3;
            }
            objectArray = SoundCategory.values();
            n2 = objectArray.length;
            n3 = 0;
            while (n3 < n2) {
                KeyBinding soundcategory = objectArray[n3];
                printwriter.println("soundCategory_" + ((SoundCategory)((Object)soundcategory)).getCategoryName() + ":" + this.getSoundLevel((SoundCategory)((Object)soundcategory)));
                ++n3;
            }
            objectArray = EnumPlayerModelParts.values();
            n2 = objectArray.length;
            n3 = 0;
            while (n3 < n2) {
                KeyBinding enumplayermodelparts = objectArray[n3];
                printwriter.println("modelPart_" + ((EnumPlayerModelParts)((Object)enumplayermodelparts)).getPartName() + ":" + this.setModelParts.contains(enumplayermodelparts));
                ++n3;
            }
            printwriter.close();
        }
        catch (Exception exception) {
            logger.error("Failed to save options", (Throwable)exception);
        }
        this.saveOfOptions();
        this.sendSettingsToServer();
    }

    public float getSoundLevel(SoundCategory sndCategory) {
        return this.mapSoundLevels.containsKey((Object)sndCategory) ? this.mapSoundLevels.get((Object)sndCategory).floatValue() : 1.0f;
    }

    public void setSoundLevel(SoundCategory sndCategory, float soundLevel) {
        this.mc.getSoundHandler().setSoundLevel(sndCategory, soundLevel);
        this.mapSoundLevels.put(sndCategory, Float.valueOf(soundLevel));
    }

    public void sendSettingsToServer() {
        if (this.mc.thePlayer != null) {
            int i2 = 0;
            for (EnumPlayerModelParts enumplayermodelparts : this.setModelParts) {
                i2 |= enumplayermodelparts.getPartMask();
            }
            this.mc.thePlayer.sendQueue.addToSendQueue(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, i2));
        }
    }

    public Set<EnumPlayerModelParts> getModelParts() {
        return ImmutableSet.copyOf(this.setModelParts);
    }

    public void setModelPartEnabled(EnumPlayerModelParts modelPart, boolean enable) {
        if (enable) {
            this.setModelParts.add(modelPart);
        } else {
            this.setModelParts.remove((Object)modelPart);
        }
        this.sendSettingsToServer();
    }

    public void switchModelPartEnabled(EnumPlayerModelParts modelPart) {
        if (!this.getModelParts().contains((Object)modelPart)) {
            this.setModelParts.add(modelPart);
        } else {
            this.setModelParts.remove((Object)modelPart);
        }
        this.sendSettingsToServer();
    }

    public int shouldRenderClouds() {
        return this.renderDistanceChunks >= 4 ? this.clouds : 0;
    }

    public boolean isUsingNativeTransport() {
        return this.useNativeTransport;
    }

    private void setOptionFloatValueOF(Options p_setOptionFloatValueOF_1_, float p_setOptionFloatValueOF_2_) {
        if (p_setOptionFloatValueOF_1_ == Options.CLOUD_HEIGHT) {
            this.ofCloudsHeight = p_setOptionFloatValueOF_2_;
            this.mc.renderGlobal.resetClouds();
        }
        if (p_setOptionFloatValueOF_1_ == Options.AO_LEVEL) {
            this.ofAoLevel = p_setOptionFloatValueOF_2_;
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_setOptionFloatValueOF_1_ == Options.AA_LEVEL) {
            int i2 = (int)p_setOptionFloatValueOF_2_;
            if (i2 > 0 && Config.isShaders()) {
                Config.showGuiMessage(Lang.get("of.message.aa.shaders1"), Lang.get("of.message.aa.shaders2"));
                return;
            }
            int[] nArray = new int[7];
            nArray[1] = 2;
            nArray[2] = 4;
            nArray[3] = 6;
            nArray[4] = 8;
            nArray[5] = 12;
            nArray[6] = 16;
            int[] aint = nArray;
            this.ofAaLevel = 0;
            int j2 = 0;
            while (j2 < aint.length) {
                if (i2 >= aint[j2]) {
                    this.ofAaLevel = aint[j2];
                }
                ++j2;
            }
            this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
        }
        if (p_setOptionFloatValueOF_1_ == Options.AF_LEVEL) {
            int k2 = (int)p_setOptionFloatValueOF_2_;
            if (k2 > 1 && Config.isShaders()) {
                Config.showGuiMessage(Lang.get("of.message.af.shaders1"), Lang.get("of.message.af.shaders2"));
                return;
            }
            this.ofAfLevel = 1;
            while (this.ofAfLevel * 2 <= k2) {
                this.ofAfLevel *= 2;
            }
            this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
            this.mc.refreshResources();
        }
        if (p_setOptionFloatValueOF_1_ == Options.MIPMAP_TYPE) {
            int l2 = (int)p_setOptionFloatValueOF_2_;
            this.ofMipmapType = Config.limit(l2, 0, 3);
            this.mc.refreshResources();
        }
        if (p_setOptionFloatValueOF_1_ == Options.FULLSCREEN_MODE) {
            int i1 = (int)p_setOptionFloatValueOF_2_ - 1;
            String[] astring = Config.getDisplayModeNames();
            if (i1 < 0 || i1 >= astring.length) {
                this.ofFullscreenMode = DEFAULT_STR;
                return;
            }
            this.ofFullscreenMode = astring[i1];
        }
    }

    private float getOptionFloatValueOF(Options p_getOptionFloatValueOF_1_) {
        if (p_getOptionFloatValueOF_1_ == Options.CLOUD_HEIGHT) {
            return this.ofCloudsHeight;
        }
        if (p_getOptionFloatValueOF_1_ == Options.AO_LEVEL) {
            return this.ofAoLevel;
        }
        if (p_getOptionFloatValueOF_1_ == Options.AA_LEVEL) {
            return this.ofAaLevel;
        }
        if (p_getOptionFloatValueOF_1_ == Options.AF_LEVEL) {
            return this.ofAfLevel;
        }
        if (p_getOptionFloatValueOF_1_ == Options.MIPMAP_TYPE) {
            return this.ofMipmapType;
        }
        if (p_getOptionFloatValueOF_1_ == Options.FRAMERATE_LIMIT) {
            return (float)this.limitFramerate == Options.FRAMERATE_LIMIT.getValueMax() && this.enableVsync ? 0.0f : (float)this.limitFramerate;
        }
        if (p_getOptionFloatValueOF_1_ == Options.FULLSCREEN_MODE) {
            if (this.ofFullscreenMode.equals(DEFAULT_STR)) {
                return 0.0f;
            }
            List<String> list = Arrays.asList(Config.getDisplayModeNames());
            int i2 = list.indexOf(this.ofFullscreenMode);
            return i2 < 0 ? 0.0f : (float)(i2 + 1);
        }
        return Float.MAX_VALUE;
    }

    private void setOptionValueOF(Options p_setOptionValueOF_1_, int p_setOptionValueOF_2_) {
        if (p_setOptionValueOF_1_ == Options.FOG_FANCY) {
            switch (this.ofFogType) {
                case 1: {
                    this.ofFogType = 2;
                    if (Config.isFancyFogAvailable()) break;
                    this.ofFogType = 3;
                    break;
                }
                case 2: {
                    this.ofFogType = 3;
                    break;
                }
                case 3: {
                    this.ofFogType = 1;
                    break;
                }
                default: {
                    this.ofFogType = 1;
                }
            }
        }
        if (p_setOptionValueOF_1_ == Options.FOG_START) {
            this.ofFogStart += 0.2f;
            if (this.ofFogStart > 0.81f) {
                this.ofFogStart = 0.2f;
            }
        }
        if (p_setOptionValueOF_1_ == Options.SMOOTH_FPS) {
            boolean bl2 = this.ofSmoothFps = !this.ofSmoothFps;
        }
        if (p_setOptionValueOF_1_ == Options.SMOOTH_WORLD) {
            this.ofSmoothWorld = !this.ofSmoothWorld;
            Config.updateThreadPriorities();
        }
        if (p_setOptionValueOF_1_ == Options.CLOUDS) {
            ++this.ofClouds;
            if (this.ofClouds > 3) {
                this.ofClouds = 0;
            }
            this.updateRenderClouds();
            this.mc.renderGlobal.resetClouds();
        }
        if (p_setOptionValueOF_1_ == Options.TREES) {
            this.ofTrees = GameSettings.nextValue(this.ofTrees, OF_TREES_VALUES);
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_setOptionValueOF_1_ == Options.DROPPED_ITEMS) {
            ++this.ofDroppedItems;
            if (this.ofDroppedItems > 2) {
                this.ofDroppedItems = 0;
            }
        }
        if (p_setOptionValueOF_1_ == Options.RAIN) {
            ++this.ofRain;
            if (this.ofRain > 3) {
                this.ofRain = 0;
            }
        }
        if (p_setOptionValueOF_1_ == Options.ANIMATED_WATER) {
            ++this.ofAnimatedWater;
            if (this.ofAnimatedWater == 1) {
                ++this.ofAnimatedWater;
            }
            if (this.ofAnimatedWater > 2) {
                this.ofAnimatedWater = 0;
            }
        }
        if (p_setOptionValueOF_1_ == Options.ANIMATED_LAVA) {
            ++this.ofAnimatedLava;
            if (this.ofAnimatedLava == 1) {
                ++this.ofAnimatedLava;
            }
            if (this.ofAnimatedLava > 2) {
                this.ofAnimatedLava = 0;
            }
        }
        if (p_setOptionValueOF_1_ == Options.ANIMATED_FIRE) {
            boolean bl3 = this.ofAnimatedFire = !this.ofAnimatedFire;
        }
        if (p_setOptionValueOF_1_ == Options.ANIMATED_PORTAL) {
            boolean bl4 = this.ofAnimatedPortal = !this.ofAnimatedPortal;
        }
        if (p_setOptionValueOF_1_ == Options.ANIMATED_REDSTONE) {
            boolean bl5 = this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
        }
        if (p_setOptionValueOF_1_ == Options.ANIMATED_EXPLOSION) {
            boolean bl6 = this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
        }
        if (p_setOptionValueOF_1_ == Options.ANIMATED_FLAME) {
            boolean bl7 = this.ofAnimatedFlame = !this.ofAnimatedFlame;
        }
        if (p_setOptionValueOF_1_ == Options.ANIMATED_SMOKE) {
            boolean bl8 = this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
        }
        if (p_setOptionValueOF_1_ == Options.VOID_PARTICLES) {
            boolean bl9 = this.ofVoidParticles = !this.ofVoidParticles;
        }
        if (p_setOptionValueOF_1_ == Options.WATER_PARTICLES) {
            boolean bl10 = this.ofWaterParticles = !this.ofWaterParticles;
        }
        if (p_setOptionValueOF_1_ == Options.PORTAL_PARTICLES) {
            boolean bl11 = this.ofPortalParticles = !this.ofPortalParticles;
        }
        if (p_setOptionValueOF_1_ == Options.POTION_PARTICLES) {
            boolean bl12 = this.ofPotionParticles = !this.ofPotionParticles;
        }
        if (p_setOptionValueOF_1_ == Options.FIREWORK_PARTICLES) {
            boolean bl13 = this.ofFireworkParticles = !this.ofFireworkParticles;
        }
        if (p_setOptionValueOF_1_ == Options.DRIPPING_WATER_LAVA) {
            boolean bl14 = this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
        }
        if (p_setOptionValueOF_1_ == Options.ANIMATED_TERRAIN) {
            boolean bl15 = this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
        }
        if (p_setOptionValueOF_1_ == Options.ANIMATED_TEXTURES) {
            boolean bl16 = this.ofAnimatedTextures = !this.ofAnimatedTextures;
        }
        if (p_setOptionValueOF_1_ == Options.RAIN_SPLASH) {
            boolean bl17 = this.ofRainSplash = !this.ofRainSplash;
        }
        if (p_setOptionValueOF_1_ == Options.LAGOMETER) {
            boolean bl18 = this.ofLagometer = !this.ofLagometer;
        }
        if (p_setOptionValueOF_1_ == Options.SHOW_FPS) {
            boolean bl19 = this.ofShowFps = !this.ofShowFps;
        }
        if (p_setOptionValueOF_1_ == Options.AUTOSAVE_TICKS) {
            int i2 = 900;
            this.ofAutoSaveTicks = Math.max(this.ofAutoSaveTicks / i2 * i2, i2);
            this.ofAutoSaveTicks *= 2;
            if (this.ofAutoSaveTicks > 32 * i2) {
                this.ofAutoSaveTicks = i2;
            }
        }
        if (p_setOptionValueOF_1_ == Options.BETTER_GRASS) {
            ++this.ofBetterGrass;
            if (this.ofBetterGrass > 3) {
                this.ofBetterGrass = 1;
            }
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_setOptionValueOF_1_ == Options.CONNECTED_TEXTURES) {
            ++this.ofConnectedTextures;
            if (this.ofConnectedTextures > 3) {
                this.ofConnectedTextures = 1;
            }
            if (this.ofConnectedTextures == 2) {
                this.mc.renderGlobal.loadRenderers();
            } else {
                this.mc.refreshResources();
            }
        }
        if (p_setOptionValueOF_1_ == Options.WEATHER) {
            boolean bl20 = this.ofWeather = !this.ofWeather;
        }
        if (p_setOptionValueOF_1_ == Options.SKY) {
            boolean bl21 = this.ofSky = !this.ofSky;
        }
        if (p_setOptionValueOF_1_ == Options.STARS) {
            boolean bl22 = this.ofStars = !this.ofStars;
        }
        if (p_setOptionValueOF_1_ == Options.SUN_MOON) {
            boolean bl23 = this.ofSunMoon = !this.ofSunMoon;
        }
        if (p_setOptionValueOF_1_ == Options.VIGNETTE) {
            ++this.ofVignette;
            if (this.ofVignette > 2) {
                this.ofVignette = 0;
            }
        }
        if (p_setOptionValueOF_1_ == Options.CHUNK_UPDATES) {
            ++this.ofChunkUpdates;
            if (this.ofChunkUpdates > 5) {
                this.ofChunkUpdates = 1;
            }
        }
        if (p_setOptionValueOF_1_ == Options.CHUNK_UPDATES_DYNAMIC) {
            boolean bl24 = this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
        }
        if (p_setOptionValueOF_1_ == Options.TIME) {
            ++this.ofTime;
            if (this.ofTime > 2) {
                this.ofTime = 0;
            }
        }
        if (p_setOptionValueOF_1_ == Options.CLEAR_WATER) {
            this.ofClearWater = !this.ofClearWater;
            this.updateWaterOpacity();
        }
        if (p_setOptionValueOF_1_ == Options.PROFILER) {
            boolean bl25 = this.ofProfiler = !this.ofProfiler;
        }
        if (p_setOptionValueOF_1_ == Options.BETTER_SNOW) {
            this.ofBetterSnow = !this.ofBetterSnow;
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_setOptionValueOF_1_ == Options.SWAMP_COLORS) {
            this.ofSwampColors = !this.ofSwampColors;
            CustomColors.updateUseDefaultGrassFoliageColors();
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_setOptionValueOF_1_ == Options.RANDOM_ENTITIES) {
            this.ofRandomEntities = !this.ofRandomEntities;
            RandomEntities.update();
        }
        if (p_setOptionValueOF_1_ == Options.SMOOTH_BIOMES) {
            this.ofSmoothBiomes = !this.ofSmoothBiomes;
            CustomColors.updateUseDefaultGrassFoliageColors();
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_setOptionValueOF_1_ == Options.CUSTOM_FONTS) {
            this.ofCustomFonts = !this.ofCustomFonts;
            this.mc.fontRendererObj.onResourceManagerReload(Config.getResourceManager());
            this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
        }
        if (p_setOptionValueOF_1_ == Options.CUSTOM_COLORS) {
            this.ofCustomColors = !this.ofCustomColors;
            CustomColors.update();
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_setOptionValueOF_1_ == Options.CUSTOM_ITEMS) {
            this.ofCustomItems = !this.ofCustomItems;
            this.mc.refreshResources();
        }
        if (p_setOptionValueOF_1_ == Options.CUSTOM_SKY) {
            this.ofCustomSky = !this.ofCustomSky;
            CustomSky.update();
        }
        if (p_setOptionValueOF_1_ == Options.SHOW_CAPES) {
            boolean bl26 = this.ofShowCapes = !this.ofShowCapes;
        }
        if (p_setOptionValueOF_1_ == Options.NATURAL_TEXTURES) {
            this.ofNaturalTextures = !this.ofNaturalTextures;
            NaturalTextures.update();
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_setOptionValueOF_1_ == Options.EMISSIVE_TEXTURES) {
            this.ofEmissiveTextures = !this.ofEmissiveTextures;
            this.mc.refreshResources();
        }
        if (p_setOptionValueOF_1_ == Options.FAST_MATH) {
            MathHelper.fastMath = this.ofFastMath = !this.ofFastMath;
        }
        if (p_setOptionValueOF_1_ == Options.FAST_RENDER) {
            if (!this.ofFastRender && Config.isShaders()) {
                Config.showGuiMessage(Lang.get("of.message.fr.shaders1"), Lang.get("of.message.fr.shaders2"));
                return;
            }
            boolean bl27 = this.ofFastRender = !this.ofFastRender;
            if (this.ofFastRender) {
                this.mc.entityRenderer.stopUseShader();
            }
            Config.updateFramebufferSize();
        }
        if (p_setOptionValueOF_1_ == Options.TRANSLUCENT_BLOCKS) {
            this.ofTranslucentBlocks = this.ofTranslucentBlocks == 0 ? 1 : (this.ofTranslucentBlocks == 1 ? 2 : (this.ofTranslucentBlocks == 2 ? 0 : 0));
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_setOptionValueOF_1_ == Options.LAZY_CHUNK_LOADING) {
            boolean bl28 = this.ofLazyChunkLoading = !this.ofLazyChunkLoading;
        }
        if (p_setOptionValueOF_1_ == Options.RENDER_REGIONS) {
            this.ofRenderRegions = !this.ofRenderRegions;
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_setOptionValueOF_1_ == Options.SMART_ANIMATIONS) {
            this.ofSmartAnimations = !this.ofSmartAnimations;
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_setOptionValueOF_1_ == Options.DYNAMIC_FOV) {
            boolean bl29 = this.ofDynamicFov = !this.ofDynamicFov;
        }
        if (p_setOptionValueOF_1_ == Options.ALTERNATE_BLOCKS) {
            this.ofAlternateBlocks = !this.ofAlternateBlocks;
            this.mc.refreshResources();
        }
        if (p_setOptionValueOF_1_ == Options.DYNAMIC_LIGHTS) {
            this.ofDynamicLights = GameSettings.nextValue(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
            DynamicLights.removeLights(this.mc.renderGlobal);
        }
        if (p_setOptionValueOF_1_ == Options.SCREENSHOT_SIZE) {
            ++this.ofScreenshotSize;
            if (this.ofScreenshotSize > 4) {
                this.ofScreenshotSize = 1;
            }
            if (!OpenGlHelper.isFramebufferEnabled()) {
                this.ofScreenshotSize = 1;
            }
        }
        if (p_setOptionValueOF_1_ == Options.CUSTOM_ENTITY_MODELS) {
            this.ofCustomEntityModels = !this.ofCustomEntityModels;
            this.mc.refreshResources();
        }
        if (p_setOptionValueOF_1_ == Options.CUSTOM_GUIS) {
            this.ofCustomGuis = !this.ofCustomGuis;
            CustomGuis.update();
        }
        if (p_setOptionValueOF_1_ == Options.SHOW_GL_ERRORS) {
            boolean bl30 = this.ofShowGlErrors = !this.ofShowGlErrors;
        }
        if (p_setOptionValueOF_1_ == Options.HELD_ITEM_TOOLTIPS) {
            boolean bl31 = this.heldItemTooltips = !this.heldItemTooltips;
        }
        if (p_setOptionValueOF_1_ == Options.ADVANCED_TOOLTIPS) {
            this.advancedItemTooltips = !this.advancedItemTooltips;
        }
    }

    private String getKeyBindingOF(Options p_getKeyBindingOF_1_) {
        String s2 = String.valueOf(I18n.format(p_getKeyBindingOF_1_.getEnumString(), new Object[0])) + ": ";
        if (s2 == null) {
            s2 = p_getKeyBindingOF_1_.getEnumString();
        }
        if (p_getKeyBindingOF_1_ == Options.RENDER_DISTANCE) {
            int i1 = (int)this.getOptionFloatValue(p_getKeyBindingOF_1_);
            String s1 = I18n.format("options.renderDistance.tiny", new Object[0]);
            int i2 = 2;
            if (i1 >= 4) {
                s1 = I18n.format("options.renderDistance.short", new Object[0]);
                i2 = 4;
            }
            if (i1 >= 8) {
                s1 = I18n.format("options.renderDistance.normal", new Object[0]);
                i2 = 8;
            }
            if (i1 >= 16) {
                s1 = I18n.format("options.renderDistance.far", new Object[0]);
                i2 = 16;
            }
            if (i1 >= 32) {
                s1 = Lang.get("of.options.renderDistance.extreme");
                i2 = 32;
            }
            if (i1 >= 48) {
                s1 = Lang.get("of.options.renderDistance.insane");
                i2 = 48;
            }
            if (i1 >= 64) {
                s1 = Lang.get("of.options.renderDistance.ludicrous");
                i2 = 64;
            }
            int j2 = this.renderDistanceChunks - i2;
            String s22 = s1;
            if (j2 > 0) {
                s22 = String.valueOf(s1) + "+";
            }
            return String.valueOf(s2) + i1 + " " + s22;
        }
        if (p_getKeyBindingOF_1_ == Options.FOG_FANCY) {
            switch (this.ofFogType) {
                case 1: {
                    return String.valueOf(s2) + Lang.getFast();
                }
                case 2: {
                    return String.valueOf(s2) + Lang.getFancy();
                }
                case 3: {
                    return String.valueOf(s2) + Lang.getOff();
                }
            }
            return String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.FOG_START) {
            return String.valueOf(s2) + this.ofFogStart;
        }
        if (p_getKeyBindingOF_1_ == Options.MIPMAP_TYPE) {
            switch (this.ofMipmapType) {
                case 0: {
                    return String.valueOf(s2) + Lang.get("of.options.mipmap.nearest");
                }
                case 1: {
                    return String.valueOf(s2) + Lang.get("of.options.mipmap.linear");
                }
                case 2: {
                    return String.valueOf(s2) + Lang.get("of.options.mipmap.bilinear");
                }
                case 3: {
                    return String.valueOf(s2) + Lang.get("of.options.mipmap.trilinear");
                }
            }
            return String.valueOf(s2) + "of.options.mipmap.nearest";
        }
        if (p_getKeyBindingOF_1_ == Options.SMOOTH_FPS) {
            return this.ofSmoothFps ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.SMOOTH_WORLD) {
            return this.ofSmoothWorld ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.CLOUDS) {
            switch (this.ofClouds) {
                case 1: {
                    return String.valueOf(s2) + Lang.getFast();
                }
                case 2: {
                    return String.valueOf(s2) + Lang.getFancy();
                }
                case 3: {
                    return String.valueOf(s2) + Lang.getOff();
                }
            }
            return String.valueOf(s2) + Lang.getDefault();
        }
        if (p_getKeyBindingOF_1_ == Options.TREES) {
            switch (this.ofTrees) {
                case 1: {
                    return String.valueOf(s2) + Lang.getFast();
                }
                case 2: {
                    return String.valueOf(s2) + Lang.getFancy();
                }
                default: {
                    return String.valueOf(s2) + Lang.getDefault();
                }
                case 4: 
            }
            return String.valueOf(s2) + Lang.get("of.general.smart");
        }
        if (p_getKeyBindingOF_1_ == Options.DROPPED_ITEMS) {
            switch (this.ofDroppedItems) {
                case 1: {
                    return String.valueOf(s2) + Lang.getFast();
                }
                case 2: {
                    return String.valueOf(s2) + Lang.getFancy();
                }
            }
            return String.valueOf(s2) + Lang.getDefault();
        }
        if (p_getKeyBindingOF_1_ == Options.RAIN) {
            switch (this.ofRain) {
                case 1: {
                    return String.valueOf(s2) + Lang.getFast();
                }
                case 2: {
                    return String.valueOf(s2) + Lang.getFancy();
                }
                case 3: {
                    return String.valueOf(s2) + Lang.getOff();
                }
            }
            return String.valueOf(s2) + Lang.getDefault();
        }
        if (p_getKeyBindingOF_1_ == Options.ANIMATED_WATER) {
            switch (this.ofAnimatedWater) {
                case 1: {
                    return String.valueOf(s2) + Lang.get("of.options.animation.dynamic");
                }
                case 2: {
                    return String.valueOf(s2) + Lang.getOff();
                }
            }
            return String.valueOf(s2) + Lang.getOn();
        }
        if (p_getKeyBindingOF_1_ == Options.ANIMATED_LAVA) {
            switch (this.ofAnimatedLava) {
                case 1: {
                    return String.valueOf(s2) + Lang.get("of.options.animation.dynamic");
                }
                case 2: {
                    return String.valueOf(s2) + Lang.getOff();
                }
            }
            return String.valueOf(s2) + Lang.getOn();
        }
        if (p_getKeyBindingOF_1_ == Options.ANIMATED_FIRE) {
            return this.ofAnimatedFire ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.ANIMATED_PORTAL) {
            return this.ofAnimatedPortal ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.ANIMATED_REDSTONE) {
            return this.ofAnimatedRedstone ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.ANIMATED_EXPLOSION) {
            return this.ofAnimatedExplosion ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.ANIMATED_FLAME) {
            return this.ofAnimatedFlame ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.ANIMATED_SMOKE) {
            return this.ofAnimatedSmoke ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.VOID_PARTICLES) {
            return this.ofVoidParticles ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.WATER_PARTICLES) {
            return this.ofWaterParticles ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.PORTAL_PARTICLES) {
            return this.ofPortalParticles ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.POTION_PARTICLES) {
            return this.ofPotionParticles ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.FIREWORK_PARTICLES) {
            return this.ofFireworkParticles ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.DRIPPING_WATER_LAVA) {
            return this.ofDrippingWaterLava ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.ANIMATED_TERRAIN) {
            return this.ofAnimatedTerrain ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.ANIMATED_TEXTURES) {
            return this.ofAnimatedTextures ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.RAIN_SPLASH) {
            return this.ofRainSplash ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.LAGOMETER) {
            return this.ofLagometer ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.SHOW_FPS) {
            return this.ofShowFps ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.AUTOSAVE_TICKS) {
            int l2 = 900;
            return this.ofAutoSaveTicks <= l2 ? String.valueOf(s2) + Lang.get("of.options.save.45s") : (this.ofAutoSaveTicks <= 2 * l2 ? String.valueOf(s2) + Lang.get("of.options.save.90s") : (this.ofAutoSaveTicks <= 4 * l2 ? String.valueOf(s2) + Lang.get("of.options.save.3min") : (this.ofAutoSaveTicks <= 8 * l2 ? String.valueOf(s2) + Lang.get("of.options.save.6min") : (this.ofAutoSaveTicks <= 16 * l2 ? String.valueOf(s2) + Lang.get("of.options.save.12min") : String.valueOf(s2) + Lang.get("of.options.save.24min")))));
        }
        if (p_getKeyBindingOF_1_ == Options.BETTER_GRASS) {
            switch (this.ofBetterGrass) {
                case 1: {
                    return String.valueOf(s2) + Lang.getFast();
                }
                case 2: {
                    return String.valueOf(s2) + Lang.getFancy();
                }
            }
            return String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.CONNECTED_TEXTURES) {
            switch (this.ofConnectedTextures) {
                case 1: {
                    return String.valueOf(s2) + Lang.getFast();
                }
                case 2: {
                    return String.valueOf(s2) + Lang.getFancy();
                }
            }
            return String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.WEATHER) {
            return this.ofWeather ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.SKY) {
            return this.ofSky ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.STARS) {
            return this.ofStars ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.SUN_MOON) {
            return this.ofSunMoon ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.VIGNETTE) {
            switch (this.ofVignette) {
                case 1: {
                    return String.valueOf(s2) + Lang.getFast();
                }
                case 2: {
                    return String.valueOf(s2) + Lang.getFancy();
                }
            }
            return String.valueOf(s2) + Lang.getDefault();
        }
        if (p_getKeyBindingOF_1_ == Options.CHUNK_UPDATES) {
            return String.valueOf(s2) + this.ofChunkUpdates;
        }
        if (p_getKeyBindingOF_1_ == Options.CHUNK_UPDATES_DYNAMIC) {
            return this.ofChunkUpdatesDynamic ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.TIME) {
            return this.ofTime == 1 ? String.valueOf(s2) + Lang.get("of.options.time.dayOnly") : (this.ofTime == 2 ? String.valueOf(s2) + Lang.get("of.options.time.nightOnly") : String.valueOf(s2) + Lang.getDefault());
        }
        if (p_getKeyBindingOF_1_ == Options.CLEAR_WATER) {
            return this.ofClearWater ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.AA_LEVEL) {
            String s3 = "";
            if (this.ofAaLevel != Config.getAntialiasingLevel()) {
                s3 = " (" + Lang.get("of.general.restart") + ")";
            }
            return this.ofAaLevel == 0 ? String.valueOf(s2) + Lang.getOff() + s3 : String.valueOf(s2) + this.ofAaLevel + s3;
        }
        if (p_getKeyBindingOF_1_ == Options.AF_LEVEL) {
            return this.ofAfLevel == 1 ? String.valueOf(s2) + Lang.getOff() : String.valueOf(s2) + this.ofAfLevel;
        }
        if (p_getKeyBindingOF_1_ == Options.PROFILER) {
            return this.ofProfiler ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.BETTER_SNOW) {
            return this.ofBetterSnow ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.SWAMP_COLORS) {
            return this.ofSwampColors ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.RANDOM_ENTITIES) {
            return this.ofRandomEntities ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.SMOOTH_BIOMES) {
            return this.ofSmoothBiomes ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.CUSTOM_FONTS) {
            return this.ofCustomFonts ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.CUSTOM_COLORS) {
            return this.ofCustomColors ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.CUSTOM_SKY) {
            return this.ofCustomSky ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.SHOW_CAPES) {
            return this.ofShowCapes ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.CUSTOM_ITEMS) {
            return this.ofCustomItems ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.NATURAL_TEXTURES) {
            return this.ofNaturalTextures ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.EMISSIVE_TEXTURES) {
            return this.ofEmissiveTextures ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.FAST_MATH) {
            return this.ofFastMath ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.FAST_RENDER) {
            return this.ofFastRender ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.TRANSLUCENT_BLOCKS) {
            return this.ofTranslucentBlocks == 1 ? String.valueOf(s2) + Lang.getFast() : (this.ofTranslucentBlocks == 2 ? String.valueOf(s2) + Lang.getFancy() : String.valueOf(s2) + Lang.getDefault());
        }
        if (p_getKeyBindingOF_1_ == Options.LAZY_CHUNK_LOADING) {
            return this.ofLazyChunkLoading ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.RENDER_REGIONS) {
            return this.ofRenderRegions ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.SMART_ANIMATIONS) {
            return this.ofSmartAnimations ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.DYNAMIC_FOV) {
            return this.ofDynamicFov ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.ALTERNATE_BLOCKS) {
            return this.ofAlternateBlocks ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.DYNAMIC_LIGHTS) {
            int k2 = GameSettings.indexOf(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
            return String.valueOf(s2) + GameSettings.getTranslation(KEYS_DYNAMIC_LIGHTS, k2);
        }
        if (p_getKeyBindingOF_1_ == Options.SCREENSHOT_SIZE) {
            return this.ofScreenshotSize <= 1 ? String.valueOf(s2) + Lang.getDefault() : String.valueOf(s2) + this.ofScreenshotSize + "x";
        }
        if (p_getKeyBindingOF_1_ == Options.CUSTOM_ENTITY_MODELS) {
            return this.ofCustomEntityModels ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.CUSTOM_GUIS) {
            return this.ofCustomGuis ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.SHOW_GL_ERRORS) {
            return this.ofShowGlErrors ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.FULLSCREEN_MODE) {
            return this.ofFullscreenMode.equals(DEFAULT_STR) ? String.valueOf(s2) + Lang.getDefault() : String.valueOf(s2) + this.ofFullscreenMode;
        }
        if (p_getKeyBindingOF_1_ == Options.HELD_ITEM_TOOLTIPS) {
            return this.heldItemTooltips ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.ADVANCED_TOOLTIPS) {
            return this.advancedItemTooltips ? String.valueOf(s2) + Lang.getOn() : String.valueOf(s2) + Lang.getOff();
        }
        if (p_getKeyBindingOF_1_ == Options.FRAMERATE_LIMIT) {
            float f2 = this.getOptionFloatValue(p_getKeyBindingOF_1_);
            return f2 == 0.0f ? String.valueOf(s2) + Lang.get("of.options.framerateLimit.vsync") : (f2 == p_getKeyBindingOF_1_.valueMax ? String.valueOf(s2) + I18n.format("options.framerateLimit.max", new Object[0]) : String.valueOf(s2) + (int)f2 + " fps");
        }
        return null;
    }

    public void loadOfOptions() {
        try {
            File file1 = this.optionsFileOF;
            if (!file1.exists()) {
                file1 = this.optionsFile;
            }
            if (!file1.exists()) {
                return;
            }
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file1), "UTF-8"));
            String s2 = "";
            while ((s2 = bufferedreader.readLine()) != null) {
                try {
                    String[] astring = s2.split(":");
                    if (astring[0].equals("ofRenderDistanceChunks") && astring.length >= 2) {
                        this.renderDistanceChunks = Integer.valueOf(astring[1]);
                        this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 1024);
                    }
                    if (astring[0].equals("ofFogType") && astring.length >= 2) {
                        this.ofFogType = Integer.valueOf(astring[1]);
                        this.ofFogType = Config.limit(this.ofFogType, 1, 3);
                    }
                    if (astring[0].equals("ofFogStart") && astring.length >= 2) {
                        this.ofFogStart = Float.valueOf(astring[1]).floatValue();
                        if (this.ofFogStart < 0.2f) {
                            this.ofFogStart = 0.2f;
                        }
                        if (this.ofFogStart > 0.81f) {
                            this.ofFogStart = 0.8f;
                        }
                    }
                    if (astring[0].equals("ofMipmapType") && astring.length >= 2) {
                        this.ofMipmapType = Integer.valueOf(astring[1]);
                        this.ofMipmapType = Config.limit(this.ofMipmapType, 0, 3);
                    }
                    if (astring[0].equals("ofOcclusionFancy") && astring.length >= 2) {
                        this.ofOcclusionFancy = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofSmoothFps") && astring.length >= 2) {
                        this.ofSmoothFps = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofSmoothWorld") && astring.length >= 2) {
                        this.ofSmoothWorld = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofAoLevel") && astring.length >= 2) {
                        this.ofAoLevel = Float.valueOf(astring[1]).floatValue();
                        this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0f, 1.0f);
                    }
                    if (astring[0].equals("ofClouds") && astring.length >= 2) {
                        this.ofClouds = Integer.valueOf(astring[1]);
                        this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                        this.updateRenderClouds();
                    }
                    if (astring[0].equals("ofCloudsHeight") && astring.length >= 2) {
                        this.ofCloudsHeight = Float.valueOf(astring[1]).floatValue();
                        this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0f, 1.0f);
                    }
                    if (astring[0].equals("ofTrees") && astring.length >= 2) {
                        this.ofTrees = Integer.valueOf(astring[1]);
                        this.ofTrees = GameSettings.limit(this.ofTrees, OF_TREES_VALUES);
                    }
                    if (astring[0].equals("ofDroppedItems") && astring.length >= 2) {
                        this.ofDroppedItems = Integer.valueOf(astring[1]);
                        this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
                    }
                    if (astring[0].equals("ofRain") && astring.length >= 2) {
                        this.ofRain = Integer.valueOf(astring[1]);
                        this.ofRain = Config.limit(this.ofRain, 0, 3);
                    }
                    if (astring[0].equals("ofAnimatedWater") && astring.length >= 2) {
                        this.ofAnimatedWater = Integer.valueOf(astring[1]);
                        this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
                    }
                    if (astring[0].equals("ofAnimatedLava") && astring.length >= 2) {
                        this.ofAnimatedLava = Integer.valueOf(astring[1]);
                        this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
                    }
                    if (astring[0].equals("ofAnimatedFire") && astring.length >= 2) {
                        this.ofAnimatedFire = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofAnimatedPortal") && astring.length >= 2) {
                        this.ofAnimatedPortal = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofAnimatedRedstone") && astring.length >= 2) {
                        this.ofAnimatedRedstone = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofAnimatedExplosion") && astring.length >= 2) {
                        this.ofAnimatedExplosion = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofAnimatedFlame") && astring.length >= 2) {
                        this.ofAnimatedFlame = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofAnimatedSmoke") && astring.length >= 2) {
                        this.ofAnimatedSmoke = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofVoidParticles") && astring.length >= 2) {
                        this.ofVoidParticles = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofWaterParticles") && astring.length >= 2) {
                        this.ofWaterParticles = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofPortalParticles") && astring.length >= 2) {
                        this.ofPortalParticles = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofPotionParticles") && astring.length >= 2) {
                        this.ofPotionParticles = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofFireworkParticles") && astring.length >= 2) {
                        this.ofFireworkParticles = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofDrippingWaterLava") && astring.length >= 2) {
                        this.ofDrippingWaterLava = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofAnimatedTerrain") && astring.length >= 2) {
                        this.ofAnimatedTerrain = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofAnimatedTextures") && astring.length >= 2) {
                        this.ofAnimatedTextures = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofRainSplash") && astring.length >= 2) {
                        this.ofRainSplash = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofLagometer") && astring.length >= 2) {
                        this.ofLagometer = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofShowFps") && astring.length >= 2) {
                        this.ofShowFps = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofAutoSaveTicks") && astring.length >= 2) {
                        this.ofAutoSaveTicks = Integer.valueOf(astring[1]);
                        this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
                    }
                    if (astring[0].equals("ofBetterGrass") && astring.length >= 2) {
                        this.ofBetterGrass = Integer.valueOf(astring[1]);
                        this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
                    }
                    if (astring[0].equals("ofConnectedTextures") && astring.length >= 2) {
                        this.ofConnectedTextures = Integer.valueOf(astring[1]);
                        this.ofConnectedTextures = Config.limit(this.ofConnectedTextures, 1, 3);
                    }
                    if (astring[0].equals("ofWeather") && astring.length >= 2) {
                        this.ofWeather = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofSky") && astring.length >= 2) {
                        this.ofSky = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofStars") && astring.length >= 2) {
                        this.ofStars = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofSunMoon") && astring.length >= 2) {
                        this.ofSunMoon = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofVignette") && astring.length >= 2) {
                        this.ofVignette = Integer.valueOf(astring[1]);
                        this.ofVignette = Config.limit(this.ofVignette, 0, 2);
                    }
                    if (astring[0].equals("ofChunkUpdates") && astring.length >= 2) {
                        this.ofChunkUpdates = Integer.valueOf(astring[1]);
                        this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
                    }
                    if (astring[0].equals("ofChunkUpdatesDynamic") && astring.length >= 2) {
                        this.ofChunkUpdatesDynamic = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofTime") && astring.length >= 2) {
                        this.ofTime = Integer.valueOf(astring[1]);
                        this.ofTime = Config.limit(this.ofTime, 0, 2);
                    }
                    if (astring[0].equals("ofClearWater") && astring.length >= 2) {
                        this.ofClearWater = Boolean.valueOf(astring[1]);
                        this.updateWaterOpacity();
                    }
                    if (astring[0].equals("ofAaLevel") && astring.length >= 2) {
                        this.ofAaLevel = Integer.valueOf(astring[1]);
                        this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
                    }
                    if (astring[0].equals("ofAfLevel") && astring.length >= 2) {
                        this.ofAfLevel = Integer.valueOf(astring[1]);
                        this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
                    }
                    if (astring[0].equals("ofProfiler") && astring.length >= 2) {
                        this.ofProfiler = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofBetterSnow") && astring.length >= 2) {
                        this.ofBetterSnow = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofSwampColors") && astring.length >= 2) {
                        this.ofSwampColors = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofRandomEntities") && astring.length >= 2) {
                        this.ofRandomEntities = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofSmoothBiomes") && astring.length >= 2) {
                        this.ofSmoothBiomes = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofCustomFonts") && astring.length >= 2) {
                        this.ofCustomFonts = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofCustomColors") && astring.length >= 2) {
                        this.ofCustomColors = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofCustomItems") && astring.length >= 2) {
                        this.ofCustomItems = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofCustomSky") && astring.length >= 2) {
                        this.ofCustomSky = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofShowCapes") && astring.length >= 2) {
                        this.ofShowCapes = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofNaturalTextures") && astring.length >= 2) {
                        this.ofNaturalTextures = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofEmissiveTextures") && astring.length >= 2) {
                        this.ofEmissiveTextures = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofLazyChunkLoading") && astring.length >= 2) {
                        this.ofLazyChunkLoading = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofRenderRegions") && astring.length >= 2) {
                        this.ofRenderRegions = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofSmartAnimations") && astring.length >= 2) {
                        this.ofSmartAnimations = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofDynamicFov") && astring.length >= 2) {
                        this.ofDynamicFov = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofAlternateBlocks") && astring.length >= 2) {
                        this.ofAlternateBlocks = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofDynamicLights") && astring.length >= 2) {
                        this.ofDynamicLights = Integer.valueOf(astring[1]);
                        this.ofDynamicLights = GameSettings.limit(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                    }
                    if (astring[0].equals("ofScreenshotSize") && astring.length >= 2) {
                        this.ofScreenshotSize = Integer.valueOf(astring[1]);
                        this.ofScreenshotSize = Config.limit(this.ofScreenshotSize, 1, 4);
                    }
                    if (astring[0].equals("ofCustomEntityModels") && astring.length >= 2) {
                        this.ofCustomEntityModels = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofCustomGuis") && astring.length >= 2) {
                        this.ofCustomGuis = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofShowGlErrors") && astring.length >= 2) {
                        this.ofShowGlErrors = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofFullscreenMode") && astring.length >= 2) {
                        this.ofFullscreenMode = astring[1];
                    }
                    if (astring[0].equals("ofFastMath") && astring.length >= 2) {
                        MathHelper.fastMath = this.ofFastMath = Boolean.valueOf(astring[1]).booleanValue();
                    }
                    if (astring[0].equals("ofFastRender") && astring.length >= 2) {
                        this.ofFastRender = Boolean.valueOf(astring[1]);
                    }
                    if (astring[0].equals("ofTranslucentBlocks") && astring.length >= 2) {
                        this.ofTranslucentBlocks = Integer.valueOf(astring[1]);
                        this.ofTranslucentBlocks = Config.limit(this.ofTranslucentBlocks, 0, 2);
                    }
                    if (!astring[0].equals("key_" + this.ofKeyBindZoom.getKeyDescription())) continue;
                    this.ofKeyBindZoom.setKeyCode(Integer.parseInt(astring[1]));
                }
                catch (Exception exception) {
                    Config.dbg("Skipping bad option: " + s2);
                    exception.printStackTrace();
                }
            }
            KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[]{this.ofKeyBindZoom});
            KeyBinding.resetKeyBindingArrayAndHash();
            bufferedreader.close();
        }
        catch (Exception exception1) {
            Config.warn("Failed to load options");
            exception1.printStackTrace();
        }
    }

    public void saveOfOptions() {
        try {
            PrintWriter printwriter = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(this.optionsFileOF), "UTF-8"));
            printwriter.println("ofFogType:" + this.ofFogType);
            printwriter.println("ofFogStart:" + this.ofFogStart);
            printwriter.println("ofMipmapType:" + this.ofMipmapType);
            printwriter.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
            printwriter.println("ofSmoothFps:" + this.ofSmoothFps);
            printwriter.println("ofSmoothWorld:" + this.ofSmoothWorld);
            printwriter.println("ofAoLevel:" + this.ofAoLevel);
            printwriter.println("ofClouds:" + this.ofClouds);
            printwriter.println("ofCloudsHeight:" + this.ofCloudsHeight);
            printwriter.println("ofTrees:" + this.ofTrees);
            printwriter.println("ofDroppedItems:" + this.ofDroppedItems);
            printwriter.println("ofRain:" + this.ofRain);
            printwriter.println("ofAnimatedWater:" + this.ofAnimatedWater);
            printwriter.println("ofAnimatedLava:" + this.ofAnimatedLava);
            printwriter.println("ofAnimatedFire:" + this.ofAnimatedFire);
            printwriter.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
            printwriter.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
            printwriter.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
            printwriter.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
            printwriter.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
            printwriter.println("ofVoidParticles:" + this.ofVoidParticles);
            printwriter.println("ofWaterParticles:" + this.ofWaterParticles);
            printwriter.println("ofPortalParticles:" + this.ofPortalParticles);
            printwriter.println("ofPotionParticles:" + this.ofPotionParticles);
            printwriter.println("ofFireworkParticles:" + this.ofFireworkParticles);
            printwriter.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
            printwriter.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
            printwriter.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
            printwriter.println("ofRainSplash:" + this.ofRainSplash);
            printwriter.println("ofLagometer:" + this.ofLagometer);
            printwriter.println("ofShowFps:" + this.ofShowFps);
            printwriter.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
            printwriter.println("ofBetterGrass:" + this.ofBetterGrass);
            printwriter.println("ofConnectedTextures:" + this.ofConnectedTextures);
            printwriter.println("ofWeather:" + this.ofWeather);
            printwriter.println("ofSky:" + this.ofSky);
            printwriter.println("ofStars:" + this.ofStars);
            printwriter.println("ofSunMoon:" + this.ofSunMoon);
            printwriter.println("ofVignette:" + this.ofVignette);
            printwriter.println("ofChunkUpdates:" + this.ofChunkUpdates);
            printwriter.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
            printwriter.println("ofTime:" + this.ofTime);
            printwriter.println("ofClearWater:" + this.ofClearWater);
            printwriter.println("ofAaLevel:" + this.ofAaLevel);
            printwriter.println("ofAfLevel:" + this.ofAfLevel);
            printwriter.println("ofProfiler:" + this.ofProfiler);
            printwriter.println("ofBetterSnow:" + this.ofBetterSnow);
            printwriter.println("ofSwampColors:" + this.ofSwampColors);
            printwriter.println("ofRandomEntities:" + this.ofRandomEntities);
            printwriter.println("ofSmoothBiomes:" + this.ofSmoothBiomes);
            printwriter.println("ofCustomFonts:" + this.ofCustomFonts);
            printwriter.println("ofCustomColors:" + this.ofCustomColors);
            printwriter.println("ofCustomItems:" + this.ofCustomItems);
            printwriter.println("ofCustomSky:" + this.ofCustomSky);
            printwriter.println("ofShowCapes:" + this.ofShowCapes);
            printwriter.println("ofNaturalTextures:" + this.ofNaturalTextures);
            printwriter.println("ofEmissiveTextures:" + this.ofEmissiveTextures);
            printwriter.println("ofLazyChunkLoading:" + this.ofLazyChunkLoading);
            printwriter.println("ofRenderRegions:" + this.ofRenderRegions);
            printwriter.println("ofSmartAnimations:" + this.ofSmartAnimations);
            printwriter.println("ofDynamicFov:" + this.ofDynamicFov);
            printwriter.println("ofAlternateBlocks:" + this.ofAlternateBlocks);
            printwriter.println("ofDynamicLights:" + this.ofDynamicLights);
            printwriter.println("ofScreenshotSize:" + this.ofScreenshotSize);
            printwriter.println("ofCustomEntityModels:" + this.ofCustomEntityModels);
            printwriter.println("ofCustomGuis:" + this.ofCustomGuis);
            printwriter.println("ofShowGlErrors:" + this.ofShowGlErrors);
            printwriter.println("ofFullscreenMode:" + this.ofFullscreenMode);
            printwriter.println("ofFastMath:" + this.ofFastMath);
            printwriter.println("ofFastRender:" + this.ofFastRender);
            printwriter.println("ofTranslucentBlocks:" + this.ofTranslucentBlocks);
            printwriter.println("key_" + this.ofKeyBindZoom.getKeyDescription() + ":" + this.ofKeyBindZoom.getKeyCode());
            printwriter.close();
        }
        catch (Exception exception) {
            Config.warn("Failed to save options");
            exception.printStackTrace();
        }
    }

    private void updateRenderClouds() {
        switch (this.ofClouds) {
            case 1: {
                this.clouds = 1;
                break;
            }
            case 2: {
                this.clouds = 2;
                break;
            }
            case 3: {
                this.clouds = 0;
                break;
            }
            default: {
                this.clouds = this.fancyGraphics ? 2 : 1;
            }
        }
    }

    public void resetSettings() {
        this.renderDistanceChunks = 8;
        this.viewBobbing = true;
        this.anaglyph = false;
        this.limitFramerate = (int)Options.FRAMERATE_LIMIT.getValueMax();
        this.enableVsync = false;
        this.updateVSync();
        this.mipmapLevels = 4;
        this.fancyGraphics = true;
        this.ambientOcclusion = 2;
        this.clouds = 2;
        this.fovSetting = 70.0f;
        this.gammaSetting = 0.0f;
        this.guiScale = 0;
        this.particleSetting = 0;
        this.heldItemTooltips = true;
        this.useVbo = false;
        this.forceUnicodeFont = false;
        this.ofFogType = 1;
        this.ofFogStart = 0.8f;
        this.ofMipmapType = 0;
        this.ofOcclusionFancy = false;
        this.ofSmartAnimations = false;
        this.ofSmoothFps = false;
        Config.updateAvailableProcessors();
        this.ofSmoothWorld = Config.isSingleProcessor();
        this.ofLazyChunkLoading = false;
        this.ofRenderRegions = false;
        this.ofFastMath = false;
        this.ofFastRender = false;
        this.ofTranslucentBlocks = 0;
        this.ofDynamicFov = true;
        this.ofAlternateBlocks = true;
        this.ofDynamicLights = 3;
        this.ofScreenshotSize = 1;
        this.ofCustomEntityModels = true;
        this.ofCustomGuis = true;
        this.ofShowGlErrors = true;
        this.ofAoLevel = 1.0f;
        this.ofAaLevel = 0;
        this.ofAfLevel = 1;
        this.ofClouds = 0;
        this.ofCloudsHeight = 0.0f;
        this.ofTrees = 0;
        this.ofRain = 0;
        this.ofBetterGrass = 3;
        this.ofAutoSaveTicks = 4000;
        this.ofLagometer = false;
        this.ofShowFps = false;
        this.ofProfiler = false;
        this.ofWeather = true;
        this.ofSky = true;
        this.ofStars = true;
        this.ofSunMoon = true;
        this.ofVignette = 0;
        this.ofChunkUpdates = 1;
        this.ofChunkUpdatesDynamic = false;
        this.ofTime = 0;
        this.ofClearWater = false;
        this.ofBetterSnow = false;
        this.ofFullscreenMode = DEFAULT_STR;
        this.ofSwampColors = true;
        this.ofRandomEntities = true;
        this.ofSmoothBiomes = true;
        this.ofCustomFonts = true;
        this.ofCustomColors = true;
        this.ofCustomItems = true;
        this.ofCustomSky = true;
        this.ofShowCapes = true;
        this.ofConnectedTextures = 2;
        this.ofNaturalTextures = false;
        this.ofEmissiveTextures = true;
        this.ofAnimatedWater = 0;
        this.ofAnimatedLava = 0;
        this.ofAnimatedFire = true;
        this.ofAnimatedPortal = true;
        this.ofAnimatedRedstone = true;
        this.ofAnimatedExplosion = true;
        this.ofAnimatedFlame = true;
        this.ofAnimatedSmoke = true;
        this.ofVoidParticles = true;
        this.ofWaterParticles = true;
        this.ofRainSplash = true;
        this.ofPortalParticles = true;
        this.ofPotionParticles = true;
        this.ofFireworkParticles = true;
        this.ofDrippingWaterLava = true;
        this.ofAnimatedTerrain = true;
        this.ofAnimatedTextures = true;
        Shaders.setShaderPack("OFF");
        Shaders.configAntialiasingLevel = 0;
        Shaders.uninit();
        Shaders.storeConfig();
        this.updateWaterOpacity();
        this.mc.refreshResources();
        this.saveOptions();
    }

    public void updateVSync() {
        Display.setVSyncEnabled(this.enableVsync);
    }

    private void updateWaterOpacity() {
        if (Config.isIntegratedServerRunning()) {
            Config.waterOpacityChanged = true;
        }
        ClearWater.updateWaterOpacity(this, this.mc.theWorld);
    }

    public void setAllAnimations(boolean p_setAllAnimations_1_) {
        int i2;
        this.ofAnimatedWater = i2 = p_setAllAnimations_1_ ? 0 : 2;
        this.ofAnimatedLava = i2;
        this.ofAnimatedFire = p_setAllAnimations_1_;
        this.ofAnimatedPortal = p_setAllAnimations_1_;
        this.ofAnimatedRedstone = p_setAllAnimations_1_;
        this.ofAnimatedExplosion = p_setAllAnimations_1_;
        this.ofAnimatedFlame = p_setAllAnimations_1_;
        this.ofAnimatedSmoke = p_setAllAnimations_1_;
        this.ofVoidParticles = p_setAllAnimations_1_;
        this.ofWaterParticles = p_setAllAnimations_1_;
        this.ofRainSplash = p_setAllAnimations_1_;
        this.ofPortalParticles = p_setAllAnimations_1_;
        this.ofPotionParticles = p_setAllAnimations_1_;
        this.ofFireworkParticles = p_setAllAnimations_1_;
        this.particleSetting = p_setAllAnimations_1_ ? 0 : 2;
        this.ofDrippingWaterLava = p_setAllAnimations_1_;
        this.ofAnimatedTerrain = p_setAllAnimations_1_;
        this.ofAnimatedTextures = p_setAllAnimations_1_;
    }

    private static int nextValue(int p_nextValue_0_, int[] p_nextValue_1_) {
        int i2 = GameSettings.indexOf(p_nextValue_0_, p_nextValue_1_);
        if (i2 < 0) {
            return p_nextValue_1_[0];
        }
        if (++i2 >= p_nextValue_1_.length) {
            i2 = 0;
        }
        return p_nextValue_1_[i2];
    }

    private static int limit(int p_limit_0_, int[] p_limit_1_) {
        int i2 = GameSettings.indexOf(p_limit_0_, p_limit_1_);
        return i2 < 0 ? p_limit_1_[0] : p_limit_0_;
    }

    private static int indexOf(int p_indexOf_0_, int[] p_indexOf_1_) {
        int i2 = 0;
        while (i2 < p_indexOf_1_.length) {
            if (p_indexOf_1_[i2] == p_indexOf_0_) {
                return i2;
            }
            ++i2;
        }
        return -1;
    }

    public static enum Options {
        INVERT_MOUSE("options.invertMouse", false, true),
        SENSITIVITY("options.sensitivity", true, false),
        FOV("options.fov", true, false, 30.0f, 110.0f, 1.0f),
        GAMMA("options.gamma", true, false),
        SATURATION("options.saturation", true, false),
        RENDER_DISTANCE("options.renderDistance", true, false, 2.0f, 16.0f, 1.0f),
        VIEW_BOBBING("options.viewBobbing", false, true),
        ANAGLYPH("options.anaglyph", false, true),
        FRAMERATE_LIMIT("options.framerateLimit", true, false, 0.0f, 260.0f, 5.0f),
        FBO_ENABLE("options.fboEnable", false, true),
        RENDER_CLOUDS("options.renderClouds", false, false),
        GRAPHICS("options.graphics", false, false),
        AMBIENT_OCCLUSION("options.ao", false, false),
        GUI_SCALE("options.guiScale", false, false),
        PARTICLES("options.particles", false, false),
        CHAT_VISIBILITY("options.chat.visibility", false, false),
        CHAT_COLOR("options.chat.color", false, true),
        CHAT_LINKS("options.chat.links", false, true),
        CHAT_OPACITY("options.chat.opacity", true, false),
        CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
        SNOOPER_ENABLED("options.snooper", false, true),
        USE_FULLSCREEN("options.fullscreen", false, true),
        ENABLE_VSYNC("options.vsync", false, true),
        USE_VBO("options.vbo", false, true),
        TOUCHSCREEN("options.touchscreen", false, true),
        CHAT_SCALE("options.chat.scale", true, false),
        CHAT_WIDTH("options.chat.width", true, false),
        CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
        CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
        MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0f, 4.0f, 1.0f),
        FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
        STREAM_BYTES_PER_PIXEL("options.stream.bytesPerPixel", true, false),
        STREAM_VOLUME_MIC("options.stream.micVolumne", true, false),
        STREAM_VOLUME_SYSTEM("options.stream.systemVolume", true, false),
        STREAM_KBPS("options.stream.kbps", true, false),
        STREAM_FPS("options.stream.fps", true, false),
        STREAM_COMPRESSION("options.stream.compression", false, false),
        STREAM_SEND_METADATA("options.stream.sendMetadata", false, true),
        STREAM_CHAT_ENABLED("options.stream.chat.enabled", false, false),
        STREAM_CHAT_USER_FILTER("options.stream.chat.userFilter", false, false),
        STREAM_MIC_TOGGLE_BEHAVIOR("options.stream.micToggleBehavior", false, false),
        BLOCK_ALTERNATIVES("options.blockAlternatives", false, true),
        REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
        ENTITY_SHADOWS("options.entityShadows", false, true),
        REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
        FOG_FANCY("of.options.FOG_FANCY", false, false),
        FOG_START("of.options.FOG_START", false, false),
        MIPMAP_TYPE("of.options.MIPMAP_TYPE", true, false, 0.0f, 3.0f, 1.0f),
        SMOOTH_FPS("of.options.SMOOTH_FPS", false, false),
        CLOUDS("of.options.CLOUDS", false, false),
        CLOUD_HEIGHT("of.options.CLOUD_HEIGHT", true, false),
        TREES("of.options.TREES", false, false),
        RAIN("of.options.RAIN", false, false),
        ANIMATED_WATER("of.options.ANIMATED_WATER", false, false),
        ANIMATED_LAVA("of.options.ANIMATED_LAVA", false, false),
        ANIMATED_FIRE("of.options.ANIMATED_FIRE", false, false),
        ANIMATED_PORTAL("of.options.ANIMATED_PORTAL", false, false),
        AO_LEVEL("of.options.AO_LEVEL", true, false),
        LAGOMETER("of.options.LAGOMETER", false, false),
        SHOW_FPS("of.options.SHOW_FPS", false, false),
        AUTOSAVE_TICKS("of.options.AUTOSAVE_TICKS", false, false),
        BETTER_GRASS("of.options.BETTER_GRASS", false, false),
        ANIMATED_REDSTONE("of.options.ANIMATED_REDSTONE", false, false),
        ANIMATED_EXPLOSION("of.options.ANIMATED_EXPLOSION", false, false),
        ANIMATED_FLAME("of.options.ANIMATED_FLAME", false, false),
        ANIMATED_SMOKE("of.options.ANIMATED_SMOKE", false, false),
        WEATHER("of.options.WEATHER", false, false),
        SKY("of.options.SKY", false, false),
        STARS("of.options.STARS", false, false),
        SUN_MOON("of.options.SUN_MOON", false, false),
        VIGNETTE("of.options.VIGNETTE", false, false),
        CHUNK_UPDATES("of.options.CHUNK_UPDATES", false, false),
        CHUNK_UPDATES_DYNAMIC("of.options.CHUNK_UPDATES_DYNAMIC", false, false),
        TIME("of.options.TIME", false, false),
        CLEAR_WATER("of.options.CLEAR_WATER", false, false),
        SMOOTH_WORLD("of.options.SMOOTH_WORLD", false, false),
        VOID_PARTICLES("of.options.VOID_PARTICLES", false, false),
        WATER_PARTICLES("of.options.WATER_PARTICLES", false, false),
        RAIN_SPLASH("of.options.RAIN_SPLASH", false, false),
        PORTAL_PARTICLES("of.options.PORTAL_PARTICLES", false, false),
        POTION_PARTICLES("of.options.POTION_PARTICLES", false, false),
        FIREWORK_PARTICLES("of.options.FIREWORK_PARTICLES", false, false),
        PROFILER("of.options.PROFILER", false, false),
        DRIPPING_WATER_LAVA("of.options.DRIPPING_WATER_LAVA", false, false),
        BETTER_SNOW("of.options.BETTER_SNOW", false, false),
        FULLSCREEN_MODE("of.options.FULLSCREEN_MODE", true, false, 0.0f, Config.getDisplayModes().length, 1.0f),
        ANIMATED_TERRAIN("of.options.ANIMATED_TERRAIN", false, false),
        SWAMP_COLORS("of.options.SWAMP_COLORS", false, false),
        RANDOM_ENTITIES("of.options.RANDOM_ENTITIES", false, false),
        SMOOTH_BIOMES("of.options.SMOOTH_BIOMES", false, false),
        CUSTOM_FONTS("of.options.CUSTOM_FONTS", false, false),
        CUSTOM_COLORS("of.options.CUSTOM_COLORS", false, false),
        SHOW_CAPES("of.options.SHOW_CAPES", false, false),
        CONNECTED_TEXTURES("of.options.CONNECTED_TEXTURES", false, false),
        CUSTOM_ITEMS("of.options.CUSTOM_ITEMS", false, false),
        AA_LEVEL("of.options.AA_LEVEL", true, false, 0.0f, 16.0f, 1.0f),
        AF_LEVEL("of.options.AF_LEVEL", true, false, 1.0f, 16.0f, 1.0f),
        ANIMATED_TEXTURES("of.options.ANIMATED_TEXTURES", false, false),
        NATURAL_TEXTURES("of.options.NATURAL_TEXTURES", false, false),
        EMISSIVE_TEXTURES("of.options.EMISSIVE_TEXTURES", false, false),
        HELD_ITEM_TOOLTIPS("of.options.HELD_ITEM_TOOLTIPS", false, false),
        DROPPED_ITEMS("of.options.DROPPED_ITEMS", false, false),
        LAZY_CHUNK_LOADING("of.options.LAZY_CHUNK_LOADING", false, false),
        CUSTOM_SKY("of.options.CUSTOM_SKY", false, false),
        FAST_MATH("of.options.FAST_MATH", false, false),
        FAST_RENDER("of.options.FAST_RENDER", false, false),
        TRANSLUCENT_BLOCKS("of.options.TRANSLUCENT_BLOCKS", false, false),
        DYNAMIC_FOV("of.options.DYNAMIC_FOV", false, false),
        DYNAMIC_LIGHTS("of.options.DYNAMIC_LIGHTS", false, false),
        ALTERNATE_BLOCKS("of.options.ALTERNATE_BLOCKS", false, false),
        CUSTOM_ENTITY_MODELS("of.options.CUSTOM_ENTITY_MODELS", false, false),
        ADVANCED_TOOLTIPS("of.options.ADVANCED_TOOLTIPS", false, false),
        SCREENSHOT_SIZE("of.options.SCREENSHOT_SIZE", false, false),
        CUSTOM_GUIS("of.options.CUSTOM_GUIS", false, false),
        RENDER_REGIONS("of.options.RENDER_REGIONS", false, false),
        SHOW_GL_ERRORS("of.options.SHOW_GL_ERRORS", false, false),
        SMART_ANIMATIONS("of.options.SMART_ANIMATIONS", false, false);

        private final boolean enumFloat;
        private final boolean enumBoolean;
        private final String enumString;
        private final float valueStep;
        private float valueMin;
        private float valueMax;

        public static Options getEnumOptions(int ordinal) {
            Options[] optionsArray = Options.values();
            int n2 = optionsArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Options gamesettings$options = optionsArray[n3];
                if (gamesettings$options.returnEnumOrdinal() == ordinal) {
                    return gamesettings$options;
                }
                ++n3;
            }
            return null;
        }

        private Options(String str, boolean isFloat, boolean isBoolean) {
            this(str, isFloat, isBoolean, 0.0f, 1.0f, 0.0f);
        }

        private Options(String str, boolean isFloat, boolean isBoolean, float valMin, float valMax, float valStep) {
            this.enumString = str;
            this.enumFloat = isFloat;
            this.enumBoolean = isBoolean;
            this.valueMin = valMin;
            this.valueMax = valMax;
            this.valueStep = valStep;
        }

        public boolean getEnumFloat() {
            return this.enumFloat;
        }

        public boolean getEnumBoolean() {
            return this.enumBoolean;
        }

        public int returnEnumOrdinal() {
            return this.ordinal();
        }

        public String getEnumString() {
            return this.enumString;
        }

        public float getValueMax() {
            return this.valueMax;
        }

        public void setValueMax(float value) {
            this.valueMax = value;
        }

        public float normalizeValue(float value) {
            return MathHelper.clamp_float((this.snapToStepClamp(value) - this.valueMin) / (this.valueMax - this.valueMin), 0.0f, 1.0f);
        }

        public float denormalizeValue(float value) {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(value, 0.0f, 1.0f));
        }

        public float snapToStepClamp(float value) {
            value = this.snapToStep(value);
            return MathHelper.clamp_float(value, this.valueMin, this.valueMax);
        }

        protected float snapToStep(float value) {
            if (this.valueStep > 0.0f) {
                value = this.valueStep * (float)Math.round(value / this.valueStep);
            }
            return value;
        }
    }
}

