/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main;

import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import java.awt.Desktop;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.labymod.account.AccountManager;
import net.labymod.addon.AddonLoader;
import net.labymod.addons.teamspeak3.TeamSpeak;
import net.labymod.api.EventManager;
import net.labymod.api.LabyModAPI;
import net.labymod.api.permissions.Permissions;
import net.labymod.api.permissions.PermissionsListener;
import net.labymod.api.protocol.chunk.ChunkCachingProtocol;
import net.labymod.api.protocol.shadow.ShadowProtocol;
import net.labymod.core.CoreAdapter;
import net.labymod.core.LabyModCore;
import net.labymod.ingamechat.IngameChatManager;
import net.labymod.ingamechat.tools.ChatToolManager;
import net.labymod.labyconnect.LabyConnect;
import net.labymod.labyplay.LabyPlay;
import net.labymod.main.LabyModForge;
import net.labymod.main.ModSettings;
import net.labymod.main.ModTextures;
import net.labymod.main.Source;
import net.labymod.main.Updater;
import net.labymod.main.lang.LanguageManager;
import net.labymod.main.listeners.AddonRecommendationListener;
import net.labymod.main.listeners.CapeReportCommand;
import net.labymod.main.listeners.ClientTickListener;
import net.labymod.main.listeners.GuiOpenListener;
import net.labymod.main.listeners.PluginMessageListener;
import net.labymod.main.listeners.RenderGameOverlayListener;
import net.labymod.main.listeners.RenderTickListener;
import net.labymod.main.listeners.ServerSwitchListener;
import net.labymod.servermanager.ServerManager;
import net.labymod.support.util.Debug;
import net.labymod.user.UserManager;
import net.labymod.user.cosmetic.util.SneakingAnimationThread;
import net.labymod.user.emote.EmoteRegistry;
import net.labymod.user.sticker.StickerRegistry;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.GuiCustomAchievement;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModTextureUtils;
import net.labymod.utils.ModUtils;
import net.labymod.utils.ReflectionHelper;
import net.labymod.utils.manager.ConfigManager;
import net.labymod.utils.manager.LavaLightUpdater;
import net.labymod.utils.manager.TagManager;
import net.labymod.utils.manager.TooltipHelper;
import net.labymod.utils.texture.DynamicTextureManager;
import net.lenni0451.eventapi.manager.ASMEventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.Session;

public class LabyMod {
    private static final LabyMod instance = new LabyMod();
    private static Random random = new Random();
    private static ConfigManager<ModSettings> mainConfig;
    private final Updater updater = new Updater();
    private final DynamicTextureManager dynamicTextureManager = new DynamicTextureManager("labymod", ModTextures.MISC_HEAD_QUESTION);
    private final AccountManager accountManager = new AccountManager();
    private final IngameChatManager IngameChatManager = new IngameChatManager();
    private final EmoteRegistry emoteRegistry = new EmoteRegistry();
    private final StickerRegistry stickerRegistry = new StickerRegistry();
    protected GameProfile gameProfile = null;
    protected String playerId = null;
    private DrawUtils drawUtils;
    private UserManager userManager;
    private ChunkCachingProtocol chunkCachingProtocol;
    private ShadowProtocol shadowProtocol;
    private Field channelField;
    private SneakingAnimationThread sneakingAnimationThread;
    private boolean hasLeftHand;
    private GuiCustomAchievement guiCustomAchievement;
    private EventManager eventManager;
    private LabyModAPI labyModAPI;
    private ServerManager serverManager;
    private net.labymod.utils.ServerData currentServerData;
    private ClientTickListener clientTickListener;
    private RenderTickListener renderTickListener;
    private float partialTicks;
    private ChatToolManager chatToolManager;
    private LabyConnect labyConnect;
    private LabyPlay labyPlay;
    private boolean serverHasEmoteSpamProtection = false;

    public static ModSettings getSettings() {
        return mainConfig == null ? null : mainConfig.getSettings();
    }

    public static boolean isBlocking(EntityPlayer player) {
        return LabyModCore.getMinecraft().isBlocking(player);
    }

    public static boolean isForge() {
        return LabyModForge.isForge();
    }

    public static String getMessage(String key, Object ... args) {
        return LanguageManager.translate(key, args);
    }

    public static LabyMod getInstance() {
        return instance;
    }

    public static Random getRandom() {
        return random;
    }

    public static ConfigManager<ModSettings> getMainConfig() {
        return mainConfig;
    }

    public void init() {
        this.hasLeftHand = !Source.ABOUT_MC_VERSION.startsWith("1.8");
        this.labyModAPI = new LabyModAPI(this);
        this.eventManager = new EventManager();
        String version = "mc" + Source.getMajorVersion();
        String coreImplementationPackage = "net.labymod.core_implementation." + version;
        try {
            LabyModCore.setCoreAdapter((CoreAdapter)Class.forName(String.valueOf(coreImplementationPackage) + ".CoreImplementation").newInstance());
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        this.drawUtils = new DrawUtils();
        this.guiCustomAchievement = new GuiCustomAchievement(Minecraft.getMinecraft());
        mainConfig = new ConfigManager<ModSettings>(new File("LabyMod/LabyMod-3.json"), ModSettings.class);
        LanguageManager.updateLang();
        this.userManager = new UserManager();
        this.userManager.init(this.getPlayerUUID(), new Consumer<Boolean>(){

            @Override
            public void accept(Boolean success) {
                if (success.booleanValue()) {
                    Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Successfully loaded all userdata");
                } else {
                    Debug.log(Debug.EnumDebugMode.USER_MANAGER, "An error occurred while loading all userdata");
                }
            }
        });
        Field renderManagerField = ReflectionHelper.findField(Minecraft.class, LabyModCore.getMappingAdapter().getRenderManagerMappings());
        Field renderManagerRenderGlobalField = ReflectionHelper.findField(RenderGlobal.class, LabyModCore.getMappingAdapter().getRenderGlobalRenderManagerMappings());
        try {
            RenderManager renderManager = LabyModCore.getMinecraft().getCustomRenderManager();
            renderManagerField.set(Minecraft.getMinecraft(), renderManager);
            renderManagerRenderGlobalField.set(Minecraft.getMinecraft().renderGlobal, renderManager);
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        this.chatToolManager = new ChatToolManager();
        this.chatToolManager.initTools();
        TagManager.init();
        this.eventManager.register(new PluginMessageListener());
        this.eventManager.register(new PermissionsListener());
        ASMEventManager.register(this);
        ASMEventManager.register(new GuiOpenListener());
        this.renderTickListener = new RenderTickListener();
        ASMEventManager.register(this.renderTickListener);
        this.clientTickListener = new ClientTickListener();
        ASMEventManager.register(this.clientTickListener);
        ASMEventManager.register(new RenderGameOverlayListener());
        ASMEventManager.register(new TooltipHelper());
        this.serverManager = new ServerManager();
        ASMEventManager.register(this.serverManager);
        this.serverManager.init();
        ASMEventManager.register(this.emoteRegistry);
        this.emoteRegistry.init();
        this.stickerRegistry.init();
        this.labyConnect = new LabyConnect();
        this.labyPlay = new LabyPlay();
        AddonLoader.enableAddons(this.labyModAPI);
        LavaLightUpdater.update();
        File serverResourcePacks = new File("server-resource-packs");
        if (!serverResourcePacks.exists()) {
            serverResourcePacks.mkdir();
        }
        this.labyModAPI.getEventManager().register(new CapeReportCommand());
        ServerSwitchListener serverSwitch = new ServerSwitchListener(this);
        this.labyModAPI.getEventManager().register(serverSwitch);
        this.labyModAPI.getEventManager().register(serverSwitch);
        this.labyModAPI.getEventManager().registerOnJoin(serverSwitch);
        AddonRecommendationListener addonRecommendation = new AddonRecommendationListener(this);
        this.labyModAPI.getEventManager().register(addonRecommendation);
        this.labyModAPI.getEventManager().register(addonRecommendation);
        this.chunkCachingProtocol = new ChunkCachingProtocol();
        this.shadowProtocol = new ShadowProtocol();
        TeamSpeak.init();
        LabyModCore.getMinecraft().init(this);
        try {
            this.channelField = ReflectionHelper.findField(NetworkManager.class, LabyModCore.getMappingAdapter().getChannelMappings());
            this.channelField.setAccessible(true);
        }
        catch (Exception e3) {
            e3.printStackTrace();
        }
        this.dynamicTextureManager.init();
    }

    public String getPlayerName() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    public UUID getPlayerUUID() {
        GameProfile profile;
        Session session = Minecraft.getMinecraft().getSession();
        if (session.getPlayerID() == null) {
            return session.getProfile().getId();
        }
        if (this.playerId != null && this.gameProfile != null && session.getPlayerID().equals(this.playerId)) {
            return this.gameProfile.getId();
        }
        this.playerId = session.getPlayerID();
        this.gameProfile = profile = session.getProfile();
        return profile.getId();
    }

    public boolean isInGame() {
        return LabyModCore.getMinecraft().getPlayer() != null && LabyModCore.getMinecraft().getWorld() != null;
    }

    public void displayMessageInChat(String message) {
        LabyModCore.getMinecraft().displayMessageInChat(message);
    }

    public void notifyMessageProfile(GameProfile gameProfile, String message) {
        switch (this.labyConnect.getAlertDisplayType()) {
            case CHAT: {
                List<String> list = ModUtils.extractUrls(message);
                if (list.isEmpty()) {
                    LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl("7")) + gameProfile.getName() + ModColor.cl("f") + ": " + message);
                    break;
                }
                LabyModCore.getMinecraft().displayMessageInChatURL(String.valueOf(ModColor.cl("7")) + gameProfile.getName() + ModColor.cl("f") + ": " + message, list.get(0));
                break;
            }
            case ACHIEVEMENT: {
                LabyMod.getInstance().getGuiCustomAchievement().displayAchievement(gameProfile, gameProfile.getName(), message);
                break;
            }
        }
    }

    public void notifyMessageRaw(String title, String message) {
        switch (this.labyConnect.getAlertDisplayType()) {
            case CHAT: {
                List<String> list = ModUtils.extractUrls(message);
                if (list.isEmpty()) {
                    LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl("7")) + title + ModColor.cl("f") + ": " + message);
                    break;
                }
                LabyModCore.getMinecraft().displayMessageInChatURL(String.valueOf(ModColor.cl("7")) + title + ModColor.cl("f") + ": " + message, list.get(0));
                break;
            }
            case ACHIEVEMENT: {
                LabyMod.getInstance().getGuiCustomAchievement().displayAchievement(title, message);
                break;
            }
        }
    }

    public void connectToServer(String address) {
        if (LabyModCore.getMinecraft().getWorld() != null) {
            LabyModCore.getMinecraft().getWorld().sendQuittingDisconnectingPacket();
            Minecraft.getMinecraft().loadWorld(null);
        }
        this.onQuit();
        ServerData serverData = new ServerData("Server", address, false);
        this.serverManager.setPrevServer(null);
        Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(new GuiMainMenu(), Minecraft.getMinecraft(), serverData));
    }

    public boolean switchServer(String address, boolean force) {
        if (LabyModCore.getMinecraft().getWorld() != null) {
            ServerData currentServerData = Minecraft.getMinecraft().getCurrentServerData();
            if (!force && currentServerData != null && currentServerData.serverIP != null && ModUtils.getProfileNameByIp(currentServerData.serverIP).equalsIgnoreCase(ModUtils.getProfileNameByIp(address))) {
                return false;
            }
            LabyModCore.getMinecraft().getWorld().sendQuittingDisconnectingPacket();
            Minecraft.getMinecraft().loadWorld(null);
        }
        this.onQuit();
        ServerData serverData = new ServerData("Server", address, false);
        this.serverManager.setPrevServer(null);
        Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(new GuiMainMenu(), Minecraft.getMinecraft(), serverData));
        return true;
    }

    public void onJoinServer(ServerData currentServerData) {
        int port;
        String[] split = currentServerData.serverIP.split(":");
        try {
            port = split.length > 1 ? Integer.parseInt(split[1].replaceAll(" ", "")) : 25565;
        }
        catch (Exception error) {
            port = 25565;
        }
        this.currentServerData = new net.labymod.utils.ServerData(split[0], port);
        this.labyConnect.updatePlayingOnServerState("");
        Permissions.getPermissionNotifyRenderer().checkChangedPermissions();
        try {
            this.eventManager.callJoinServer(this.currentServerData);
        }
        catch (Exception error) {
            error.printStackTrace();
        }
        if (currentServerData.serverIP.toLowerCase().contains("hypixel")) {
            this.serverManager.getPermissionMap().put(Permissions.Permission.BLOCKBUILD, false);
            this.serverManager.getPermissionMap().put(Permissions.Permission.CHAT, false);
            this.serverHasEmoteSpamProtection = true;
        } else {
            this.serverHasEmoteSpamProtection = false;
        }
        Debug.log(Debug.EnumDebugMode.MINECRAFT, "Connected to server " + currentServerData.serverIP);
    }

    public void onQuit() {
        this.serverManager.reset();
        Permissions.getPermissionNotifyRenderer().quit();
        this.userManager.getCosmeticImageManager().unloadUnusedTextures(true, false);
        this.labyConnect.updatePlayingOnServerState("");
        try {
            this.eventManager.callQuitServer(this.currentServerData);
        }
        catch (NullPointerException e2) {
            e2.printStackTrace();
        }
        this.currentServerData = null;
    }

    public boolean openWebpage(String urlString, boolean request) {
        try {
            if (!urlString.toLowerCase().startsWith("https://") && !urlString.toLowerCase().startsWith("http://")) {
                urlString = "http://" + urlString;
            }
            final URI uri = new URL(urlString).toURI();
            if (request) {
                final GuiScreen lastScreen = Minecraft.getMinecraft().currentScreen;
                Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(new GuiYesNoCallback(){

                    @Override
                    public void confirmClicked(boolean result, int id2) {
                        if (result) {
                            Desktop desktop;
                            Desktop desktop2 = desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                                try {
                                    desktop.browse(uri);
                                }
                                catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }
                        Minecraft.getMinecraft().displayGuiScreen(lastScreen);
                    }
                }, "Do you want to open this link in your default browser?", String.valueOf(ModColor.cl("b")) + uri.toString(), 31102009));
            } else {
                Desktop desktop;
                Desktop desktop2 = desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(uri);
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
            return true;
        }
        catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public Channel getNettyChannel() throws Exception {
        NetHandlerPlayClient connecion = LabyModCore.getMinecraft().getConnection();
        if (connecion == null) {
            return null;
        }
        NetworkManager networkManager = connecion.getNetworkManager();
        return (Channel)this.channelField.get(networkManager);
    }

    @Deprecated
    public ModTextureUtils getTextureUtils() {
        return ModTextureUtils.INSTANCE;
    }

    public Updater getUpdater() {
        return this.updater;
    }

    public DynamicTextureManager getDynamicTextureManager() {
        return this.dynamicTextureManager;
    }

    public DrawUtils getDrawUtils() {
        return this.drawUtils;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public ChunkCachingProtocol getChunkCachingProtocol() {
        return this.chunkCachingProtocol;
    }

    public ShadowProtocol getShadowProtocol() {
        return this.shadowProtocol;
    }

    public Field getChannelField() {
        return this.channelField;
    }

    public AccountManager getAccountManager() {
        return this.accountManager;
    }

    public SneakingAnimationThread getSneakingAnimationThread() {
        return this.sneakingAnimationThread;
    }

    public void setSneakingAnimationThread(SneakingAnimationThread sneakingAnimationThread) {
        this.sneakingAnimationThread = sneakingAnimationThread;
    }

    public boolean isHasLeftHand() {
        return this.hasLeftHand;
    }

    public GuiCustomAchievement getGuiCustomAchievement() {
        return this.guiCustomAchievement;
    }

    public IngameChatManager getIngameChatManager() {
        return this.IngameChatManager;
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public LabyModAPI getLabyModAPI() {
        return this.labyModAPI;
    }

    public ServerManager getServerManager() {
        return this.serverManager;
    }

    public net.labymod.utils.ServerData getCurrentServerData() {
        return this.currentServerData;
    }

    public ClientTickListener getClientTickListener() {
        return this.clientTickListener;
    }

    public RenderTickListener getRenderTickListener() {
        return this.renderTickListener;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public EmoteRegistry getEmoteRegistry() {
        return this.emoteRegistry;
    }

    public StickerRegistry getStickerRegistry() {
        return this.stickerRegistry;
    }

    public ChatToolManager getChatToolManager() {
        return this.chatToolManager;
    }

    public LabyConnect getLabyConnect() {
        return this.labyConnect;
    }

    public LabyPlay getLabyPlay() {
        return this.labyPlay;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public boolean isServerHasEmoteSpamProtection() {
        return this.serverHasEmoteSpamProtection;
    }
}

