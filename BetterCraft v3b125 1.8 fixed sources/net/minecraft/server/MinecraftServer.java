/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Util;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer
implements Runnable,
ICommandSender,
IThreadListener,
IPlayerUsage {
    private static final Logger logger = LogManager.getLogger();
    public static final File USER_CACHE_FILE = new File("usercache.json");
    private static MinecraftServer mcServer;
    private final ISaveFormat anvilConverterForAnvilFile;
    private final PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("server", this, MinecraftServer.getCurrentTimeMillis());
    private final File anvilFile;
    private final List<ITickable> playersOnline = Lists.newArrayList();
    protected final ICommandManager commandManager;
    public final Profiler theProfiler = new Profiler();
    private final NetworkSystem networkSystem;
    private final ServerStatusResponse statusResponse = new ServerStatusResponse();
    private final Random random = new Random();
    private int serverPort = -1;
    public WorldServer[] worldServers;
    private ServerConfigurationManager serverConfigManager;
    private boolean serverRunning = true;
    private boolean serverStopped;
    private int tickCounter;
    protected final Proxy serverProxy;
    public String currentTask;
    public int percentDone;
    private boolean onlineMode;
    private boolean canSpawnAnimals;
    private boolean canSpawnNPCs;
    private boolean pvpEnabled;
    private boolean allowFlight;
    private String motd;
    private int buildLimit;
    private int maxPlayerIdleMinutes = 0;
    public final long[] tickTimeArray = new long[100];
    public long[][] timeOfLastDimensionTick;
    private KeyPair serverKeyPair;
    private String serverOwner;
    private String folderName;
    private String worldName;
    private boolean isDemo;
    private boolean enableBonusChest;
    private boolean worldIsBeingDeleted;
    private String resourcePackUrl = "";
    private String resourcePackHash = "";
    private boolean serverIsRunning;
    private long timeOfLastWarning;
    private String userMessage;
    private boolean startProfiling;
    private boolean isGamemodeForced;
    private final YggdrasilAuthenticationService authService;
    private final MinecraftSessionService sessionService;
    private long nanoTimeSinceStatusRefresh = 0L;
    private final GameProfileRepository profileRepo;
    private final PlayerProfileCache profileCache;
    protected final Queue<FutureTask<?>> futureTaskQueue = Queues.newArrayDeque();
    private Thread serverThread;
    private long currentTime = MinecraftServer.getCurrentTimeMillis();

    public MinecraftServer(Proxy proxy, File workDir) {
        this.serverProxy = proxy;
        mcServer = this;
        this.anvilFile = null;
        this.networkSystem = null;
        this.profileCache = new PlayerProfileCache(this, workDir);
        this.commandManager = null;
        this.anvilConverterForAnvilFile = null;
        this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
        this.sessionService = this.authService.createMinecraftSessionService();
        this.profileRepo = this.authService.createProfileRepository();
    }

    public MinecraftServer(File workDir, Proxy proxy, File profileCacheDir) {
        this.serverProxy = proxy;
        mcServer = this;
        this.anvilFile = workDir;
        this.networkSystem = new NetworkSystem(this);
        this.profileCache = new PlayerProfileCache(this, profileCacheDir);
        this.commandManager = this.createNewCommandManager();
        this.anvilConverterForAnvilFile = new AnvilSaveConverter(workDir);
        this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
        this.sessionService = this.authService.createMinecraftSessionService();
        this.profileRepo = this.authService.createProfileRepository();
    }

    protected ServerCommandManager createNewCommandManager() {
        return new ServerCommandManager();
    }

    protected abstract boolean startServer() throws IOException;

    protected void convertMapIfNeeded(String worldNameIn) {
        if (this.getActiveAnvilConverter().isOldMapFormat(worldNameIn)) {
            logger.info("Converting map!");
            this.setUserMessage("menu.convertingLevel");
            this.getActiveAnvilConverter().convertMapFormat(worldNameIn, new IProgressUpdate(){
                private long startTime = System.currentTimeMillis();

                @Override
                public void displaySavingString(String message) {
                }

                @Override
                public void resetProgressAndMessage(String message) {
                }

                @Override
                public void setLoadingProgress(int progress) {
                    if (System.currentTimeMillis() - this.startTime >= 1000L) {
                        this.startTime = System.currentTimeMillis();
                        logger.info("Converting... " + progress + "%");
                    }
                }

                @Override
                public void setDoneWorking() {
                }

                @Override
                public void displayLoadingString(String message) {
                }
            });
        }
    }

    protected synchronized void setUserMessage(String message) {
        this.userMessage = message;
    }

    public synchronized String getUserMessage() {
        return this.userMessage;
    }

    protected void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String worldNameIn2) {
        WorldSettings worldsettings;
        this.convertMapIfNeeded(saveName);
        this.setUserMessage("menu.loadingLevel");
        this.worldServers = new WorldServer[3];
        this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
        ISaveHandler isavehandler = this.anvilConverterForAnvilFile.getSaveLoader(saveName, true);
        this.setResourcePackFromWorld(this.getFolderName(), isavehandler);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();
        if (worldinfo == null) {
            if (this.isDemo()) {
                worldsettings = DemoWorldServer.demoWorldSettings;
            } else {
                worldsettings = new WorldSettings(seed, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), type);
                worldsettings.setWorldName(worldNameIn2);
                if (this.enableBonusChest) {
                    worldsettings.enableBonusChest();
                }
            }
            worldinfo = new WorldInfo(worldsettings, worldNameIn);
        } else {
            worldinfo.setWorldName(worldNameIn);
            worldsettings = new WorldSettings(worldinfo);
        }
        int i2 = 0;
        while (i2 < this.worldServers.length) {
            int j2 = 0;
            if (i2 == 1) {
                j2 = -1;
            }
            if (i2 == 2) {
                j2 = 1;
            }
            if (i2 == 0) {
                this.worldServers[i2] = this.isDemo() ? (WorldServer)new DemoWorldServer(this, isavehandler, worldinfo, j2, this.theProfiler).init() : (WorldServer)new WorldServer(this, isavehandler, worldinfo, j2, this.theProfiler).init();
                this.worldServers[i2].initialize(worldsettings);
            } else {
                this.worldServers[i2] = (WorldServer)new WorldServerMulti(this, isavehandler, j2, this.worldServers[0], this.theProfiler).init();
            }
            this.worldServers[i2].addWorldAccess(new WorldManager(this, this.worldServers[i2]));
            if (!this.isSinglePlayer()) {
                this.worldServers[i2].getWorldInfo().setGameType(this.getGameType());
            }
            ++i2;
        }
        this.serverConfigManager.setPlayerManager(this.worldServers);
        this.setDifficultyForAllWorlds(this.getDifficulty());
        this.initialWorldChunkLoad();
    }

    protected void initialWorldChunkLoad() {
        int i2 = 16;
        int j2 = 4;
        int k2 = 192;
        int l2 = 625;
        int i1 = 0;
        this.setUserMessage("menu.generatingTerrain");
        int j1 = 0;
        logger.info("Preparing start region for level " + j1);
        WorldServer worldserver = this.worldServers[j1];
        BlockPos blockpos = worldserver.getSpawnPoint();
        long k1 = MinecraftServer.getCurrentTimeMillis();
        int l1 = -192;
        while (l1 <= 192 && this.isServerRunning()) {
            int i22 = -192;
            while (i22 <= 192 && this.isServerRunning()) {
                long j22 = MinecraftServer.getCurrentTimeMillis();
                if (j22 - k1 > 1000L) {
                    this.outputPercentRemaining("Preparing spawn area", i1 * 100 / 625);
                    k1 = j22;
                }
                ++i1;
                worldserver.theChunkProviderServer.loadChunk(blockpos.getX() + l1 >> 4, blockpos.getZ() + i22 >> 4);
                i22 += 16;
            }
            l1 += 16;
        }
        this.clearCurrentTask();
    }

    protected void setResourcePackFromWorld(String worldNameIn, ISaveHandler saveHandlerIn) {
        File file1 = new File(saveHandlerIn.getWorldDirectory(), "resources.zip");
        if (file1.isFile()) {
            this.setResourcePack("level://" + worldNameIn + "/" + file1.getName(), "");
        }
    }

    public abstract boolean canStructuresSpawn();

    public abstract WorldSettings.GameType getGameType();

    public abstract EnumDifficulty getDifficulty();

    public abstract boolean isHardcore();

    public abstract int getOpPermissionLevel();

    public abstract boolean shouldBroadcastRconToOps();

    public abstract boolean shouldBroadcastConsoleToOps();

    protected void outputPercentRemaining(String message, int percent) {
        this.currentTask = message;
        this.percentDone = percent;
        logger.info(String.valueOf(message) + ": " + percent + "%");
    }

    protected void clearCurrentTask() {
        this.currentTask = null;
        this.percentDone = 0;
    }

    protected void saveAllWorlds(boolean dontLog) {
        if (!this.worldIsBeingDeleted) {
            WorldServer[] worldServerArray = this.worldServers;
            int n2 = this.worldServers.length;
            int n3 = 0;
            while (n3 < n2) {
                WorldServer worldserver = worldServerArray[n3];
                if (worldserver != null) {
                    if (!dontLog) {
                        logger.info("Saving chunks for level '" + worldserver.getWorldInfo().getWorldName() + "'/" + worldserver.provider.getDimensionName());
                    }
                    try {
                        worldserver.saveAllChunks(true, null);
                    }
                    catch (MinecraftException minecraftexception) {
                        logger.warn(minecraftexception.getMessage());
                    }
                }
                ++n3;
            }
        }
    }

    public void stopServer() {
        if (!this.worldIsBeingDeleted) {
            logger.info("Stopping server");
            if (this.getNetworkSystem() != null) {
                this.getNetworkSystem().terminateEndpoints();
            }
            if (this.serverConfigManager != null) {
                logger.info("Saving players");
                this.serverConfigManager.saveAllPlayerData();
                this.serverConfigManager.removeAllPlayers();
            }
            if (this.worldServers != null) {
                logger.info("Saving worlds");
                this.saveAllWorlds(false);
                int i2 = 0;
                while (i2 < this.worldServers.length) {
                    WorldServer worldserver = this.worldServers[i2];
                    worldserver.flush();
                    ++i2;
                }
            }
            if (this.usageSnooper.isSnooperRunning()) {
                this.usageSnooper.stopSnooper();
            }
        }
    }

    public boolean isServerRunning() {
        return this.serverRunning;
    }

    public void initiateShutdown() {
        this.serverRunning = false;
    }

    protected void setInstance() {
        mcServer = this;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void run() {
        block24: {
            try {
                try {
                    block27: {
                        if (!this.startServer()) break block27;
                        this.currentTime = MinecraftServer.getCurrentTimeMillis();
                        i = 0L;
                        this.statusResponse.setServerDescription(new ChatComponentText(this.motd));
                        this.statusResponse.setProtocolVersionInfo(new ServerStatusResponse.MinecraftProtocolVersionIdentifier("1.8.9", 47));
                        this.addFaviconToStatusResponse(this.statusResponse);
                        while (this.serverRunning) {
                            block28: {
                                k = MinecraftServer.getCurrentTimeMillis();
                                j = k - this.currentTime;
                                if (j > 2000L && this.currentTime - this.timeOfLastWarning >= 15000L) {
                                    MinecraftServer.logger.warn("Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", new Object[]{j, j / 50L});
                                    j = 2000L;
                                    this.timeOfLastWarning = this.currentTime;
                                }
                                if (j < 0L) {
                                    MinecraftServer.logger.warn("Time ran backwards! Did the system time change?");
                                    j = 0L;
                                }
                                i += j;
                                this.currentTime = k;
                                if (!this.worldServers[0].areAllPlayersAsleep()) ** GOTO lbl27
                                this.tick();
                                i = 0L;
                                break block28;
lbl-1000:
                                // 1 sources

                                {
                                    i -= 50L;
                                    this.tick();
lbl27:
                                    // 2 sources

                                    ** while (i > 50L)
                                }
                            }
                            Thread.sleep(Math.max(1L, 50L - i));
                            this.serverIsRunning = true;
                        }
                        break block24;
                    }
                    this.finalTick(null);
                }
                catch (Throwable throwable1) {
                    MinecraftServer.logger.error("Encountered an unexpected exception", throwable1);
                    crashreport = null;
                    crashreport = throwable1 instanceof ReportedException != false ? this.addServerInfoToCrashReport(((ReportedException)throwable1).getCrashReport()) : this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", throwable1));
                    file1 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
                    if (crashreport.saveToFile(file1)) {
                        MinecraftServer.logger.error("This crash report has been saved to: " + file1.getAbsolutePath());
                    } else {
                        MinecraftServer.logger.error("We were unable to save this crash report to disk.");
                    }
                    this.finalTick(crashreport);
                    try {
                        try {
                            this.serverStopped = true;
                            this.stopServer();
                        }
                        catch (Throwable throwable) {
                            MinecraftServer.logger.error("Exception stopping the server", throwable);
                            this.systemExitNow();
                        }
                    }
                    finally {
                        this.systemExitNow();
                    }
                }
            }
            finally {
                block26: {
                    try {
                        try {
                            this.serverStopped = true;
                            this.stopServer();
                        }
                        catch (Throwable throwable) {
                            MinecraftServer.logger.error("Exception stopping the server", throwable);
                            this.systemExitNow();
                            break block26;
                        }
                    }
                    catch (Throwable var9_12) {
                        this.systemExitNow();
                        throw var9_12;
                    }
                    this.systemExitNow();
                }
            }
        }
    }

    private void addFaviconToStatusResponse(ServerStatusResponse response) {
        File file1 = this.getFile("server-icon.png");
        if (file1.isFile()) {
            ByteBuf bytebuf = Unpooled.buffer();
            try {
                try {
                    BufferedImage bufferedimage = ImageIO.read(file1);
                    Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    ImageIO.write((RenderedImage)bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
                    ByteBuf bytebuf1 = Base64.encode(bytebuf);
                    response.setFavicon("data:image/png;base64," + bytebuf1.toString(Charsets.UTF_8));
                }
                catch (Exception exception) {
                    logger.error("Couldn't load server icon", (Throwable)exception);
                    bytebuf.release();
                }
            }
            finally {
                bytebuf.release();
            }
        }
    }

    public File getDataDirectory() {
        return new File(".");
    }

    protected void finalTick(CrashReport report) {
    }

    protected void systemExitNow() {
    }

    public void tick() {
        long i2 = System.nanoTime();
        ++this.tickCounter;
        if (this.startProfiling) {
            this.startProfiling = false;
            this.theProfiler.profilingEnabled = true;
            this.theProfiler.clearProfiling();
        }
        this.theProfiler.startSection("root");
        this.updateTimeLightAndEntities();
        if (i2 - this.nanoTimeSinceStatusRefresh >= 5000000000L) {
            this.nanoTimeSinceStatusRefresh = i2;
            this.statusResponse.setPlayerCountData(new ServerStatusResponse.PlayerCountData(this.getMaxPlayers(), this.getCurrentPlayerCount()));
            GameProfile[] agameprofile = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
            int j2 = MathHelper.getRandomIntegerInRange(this.random, 0, this.getCurrentPlayerCount() - agameprofile.length);
            int k2 = 0;
            while (k2 < agameprofile.length) {
                agameprofile[k2] = this.serverConfigManager.getPlayerList().get(j2 + k2).getGameProfile();
                ++k2;
            }
            Collections.shuffle(Arrays.asList(agameprofile));
            this.statusResponse.getPlayerCountData().setPlayers(agameprofile);
        }
        if (this.tickCounter % 900 == 0) {
            this.theProfiler.startSection("save");
            this.serverConfigManager.saveAllPlayerData();
            this.saveAllWorlds(true);
            this.theProfiler.endSection();
        }
        this.theProfiler.startSection("tallying");
        this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - i2;
        this.theProfiler.endSection();
        this.theProfiler.startSection("snooper");
        if (!this.usageSnooper.isSnooperRunning() && this.tickCounter > 100) {
            this.usageSnooper.startSnooper();
        }
        if (this.tickCounter % 6000 == 0) {
            this.usageSnooper.addMemoryStatsToSnooper();
        }
        this.theProfiler.endSection();
        this.theProfiler.endSection();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateTimeLightAndEntities() {
        this.theProfiler.startSection("jobs");
        Queue<FutureTask<?>> queue = this.futureTaskQueue;
        synchronized (queue) {
            while (!this.futureTaskQueue.isEmpty()) {
                Util.runTask(this.futureTaskQueue.poll(), logger);
            }
        }
        this.theProfiler.endStartSection("levels");
        int j2 = 0;
        while (j2 < this.worldServers.length) {
            long i2 = System.nanoTime();
            if (j2 == 0 || this.getAllowNether()) {
                WorldServer worldserver = this.worldServers[j2];
                this.theProfiler.startSection(worldserver.getWorldInfo().getWorldName());
                if (this.tickCounter % 20 == 0) {
                    this.theProfiler.startSection("timeSync");
                    this.serverConfigManager.sendPacketToAllPlayersInDimension(new S03PacketTimeUpdate(worldserver.getTotalWorldTime(), worldserver.getWorldTime(), worldserver.getGameRules().getBoolean("doDaylightCycle")), worldserver.provider.getDimensionId());
                    this.theProfiler.endSection();
                }
                this.theProfiler.startSection("tick");
                try {
                    worldserver.tick();
                }
                catch (Throwable throwable1) {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Exception ticking world");
                    worldserver.addWorldInfoToCrashReport(crashreport);
                    throw new ReportedException(crashreport);
                }
                try {
                    worldserver.updateEntities();
                }
                catch (Throwable throwable) {
                    CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Exception ticking world entities");
                    worldserver.addWorldInfoToCrashReport(crashreport1);
                    throw new ReportedException(crashreport1);
                }
                this.theProfiler.endSection();
                this.theProfiler.startSection("tracker");
                worldserver.getEntityTracker().updateTrackedEntities();
                this.theProfiler.endSection();
                this.theProfiler.endSection();
            }
            this.timeOfLastDimensionTick[j2][this.tickCounter % 100] = System.nanoTime() - i2;
            ++j2;
        }
        this.theProfiler.endStartSection("connection");
        this.getNetworkSystem().networkTick();
        this.theProfiler.endStartSection("players");
        this.serverConfigManager.onTick();
        this.theProfiler.endStartSection("tickables");
        int k2 = 0;
        while (k2 < this.playersOnline.size()) {
            this.playersOnline.get(k2).update();
            ++k2;
        }
        this.theProfiler.endSection();
    }

    public boolean getAllowNether() {
        return true;
    }

    public void startServerThread() {
        this.serverThread = new Thread((Runnable)this, "Server thread");
        this.serverThread.start();
    }

    public File getFile(String fileName) {
        return new File(this.getDataDirectory(), fileName);
    }

    public void logWarning(String msg) {
        logger.warn(msg);
    }

    public WorldServer worldServerForDimension(int dimension) {
        return dimension == -1 ? this.worldServers[1] : (dimension == 1 ? this.worldServers[2] : this.worldServers[0]);
    }

    public String getMinecraftVersion() {
        return "1.8.9";
    }

    public int getCurrentPlayerCount() {
        return this.serverConfigManager.getCurrentPlayerCount();
    }

    public int getMaxPlayers() {
        return this.serverConfigManager.getMaxPlayers();
    }

    public String[] getAllUsernames() {
        return this.serverConfigManager.getAllUsernames();
    }

    public GameProfile[] getGameProfiles() {
        return this.serverConfigManager.getAllProfiles();
    }

    public String getServerModName() {
        return "vanilla";
    }

    public CrashReport addServerInfoToCrashReport(CrashReport report) {
        report.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>(){

            @Override
            public String call() throws Exception {
                return MinecraftServer.this.theProfiler.profilingEnabled ? MinecraftServer.this.theProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        if (this.serverConfigManager != null) {
            report.getCategory().addCrashSectionCallable("Player Count", new Callable<String>(){

                @Override
                public String call() {
                    return String.valueOf(MinecraftServer.this.serverConfigManager.getCurrentPlayerCount()) + " / " + MinecraftServer.this.serverConfigManager.getMaxPlayers() + "; " + MinecraftServer.this.serverConfigManager.getPlayerList();
                }
            });
        }
        return report;
    }

    public List<String> getTabCompletions(ICommandSender sender, String input, BlockPos pos) {
        ArrayList<String> list = Lists.newArrayList();
        if (input.startsWith("/")) {
            boolean flag = !(input = input.substring(1)).contains(" ");
            List<String> list1 = this.commandManager.getTabCompletionOptions(sender, input, pos);
            if (list1 != null) {
                for (String s2 : list1) {
                    if (flag) {
                        list.add("/" + s2);
                        continue;
                    }
                    list.add(s2);
                }
            }
            return list;
        }
        String[] astring = input.split(" ", -1);
        String s2 = astring[astring.length - 1];
        String[] stringArray = this.serverConfigManager.getAllUsernames();
        int n2 = stringArray.length;
        int n3 = 0;
        while (n3 < n2) {
            String s1 = stringArray[n3];
            if (CommandBase.doesStringStartWith(s2, s1)) {
                list.add(s1);
            }
            ++n3;
        }
        return list;
    }

    public static MinecraftServer getServer() {
        return mcServer;
    }

    public boolean isAnvilFileSet() {
        return this.anvilFile != null;
    }

    @Override
    public String getName() {
        return "Server";
    }

    @Override
    public void addChatMessage(IChatComponent component) {
        logger.info(component.getUnformattedText());
    }

    @Override
    public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return true;
    }

    public ICommandManager getCommandManager() {
        return this.commandManager;
    }

    public KeyPair getKeyPair() {
        return this.serverKeyPair;
    }

    public String getServerOwner() {
        return this.serverOwner;
    }

    public void setServerOwner(String owner) {
        this.serverOwner = owner;
    }

    public boolean isSinglePlayer() {
        return this.serverOwner != null;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public void setFolderName(String name) {
        this.folderName = name;
    }

    public void setWorldName(String p_71246_1_) {
        this.worldName = p_71246_1_;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.serverKeyPair = keyPair;
    }

    public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
        int i2 = 0;
        while (i2 < this.worldServers.length) {
            WorldServer world = this.worldServers[i2];
            if (world != null) {
                if (world.getWorldInfo().isHardcoreModeEnabled()) {
                    world.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
                    world.setAllowedSpawnTypes(true, true);
                } else if (this.isSinglePlayer()) {
                    world.getWorldInfo().setDifficulty(difficulty);
                    world.setAllowedSpawnTypes(world.getDifficulty() != EnumDifficulty.PEACEFUL, true);
                } else {
                    world.getWorldInfo().setDifficulty(difficulty);
                    world.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
                }
            }
            ++i2;
        }
    }

    protected boolean allowSpawnMonsters() {
        return true;
    }

    public boolean isDemo() {
        return this.isDemo;
    }

    public void setDemo(boolean demo) {
        this.isDemo = demo;
    }

    public void canCreateBonusChest(boolean enable) {
        this.enableBonusChest = enable;
    }

    public ISaveFormat getActiveAnvilConverter() {
        return this.anvilConverterForAnvilFile;
    }

    public void deleteWorldAndStopServer() {
        this.worldIsBeingDeleted = true;
        this.getActiveAnvilConverter().flushCache();
        int i2 = 0;
        while (i2 < this.worldServers.length) {
            WorldServer worldserver = this.worldServers[i2];
            if (worldserver != null) {
                worldserver.flush();
            }
            ++i2;
        }
        this.getActiveAnvilConverter().deleteWorldDirectory(this.worldServers[0].getSaveHandler().getWorldDirectoryName());
        this.initiateShutdown();
    }

    public String getResourcePackUrl() {
        return this.resourcePackUrl;
    }

    public String getResourcePackHash() {
        return this.resourcePackHash;
    }

    public void setResourcePack(String url, String hash) {
        this.resourcePackUrl = url;
        this.resourcePackHash = hash;
    }

    @Override
    public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
        playerSnooper.addClientStat("whitelist_enabled", false);
        playerSnooper.addClientStat("whitelist_count", 0);
        if (this.serverConfigManager != null) {
            playerSnooper.addClientStat("players_current", this.getCurrentPlayerCount());
            playerSnooper.addClientStat("players_max", this.getMaxPlayers());
            playerSnooper.addClientStat("players_seen", this.serverConfigManager.getAvailablePlayerDat().length);
        }
        playerSnooper.addClientStat("uses_auth", this.onlineMode);
        playerSnooper.addClientStat("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
        playerSnooper.addClientStat("run_time", (MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
        playerSnooper.addClientStat("avg_tick_ms", (int)(MathHelper.average(this.tickTimeArray) * 1.0E-6));
        int i2 = 0;
        if (this.worldServers != null) {
            int j2 = 0;
            while (j2 < this.worldServers.length) {
                if (this.worldServers[j2] != null) {
                    WorldServer worldserver = this.worldServers[j2];
                    WorldInfo worldinfo = worldserver.getWorldInfo();
                    playerSnooper.addClientStat("world[" + i2 + "][dimension]", worldserver.provider.getDimensionId());
                    playerSnooper.addClientStat("world[" + i2 + "][mode]", (Object)worldinfo.getGameType());
                    playerSnooper.addClientStat("world[" + i2 + "][difficulty]", (Object)worldserver.getDifficulty());
                    playerSnooper.addClientStat("world[" + i2 + "][hardcore]", worldinfo.isHardcoreModeEnabled());
                    playerSnooper.addClientStat("world[" + i2 + "][generator_name]", worldinfo.getTerrainType().getWorldTypeName());
                    playerSnooper.addClientStat("world[" + i2 + "][generator_version]", worldinfo.getTerrainType().getGeneratorVersion());
                    playerSnooper.addClientStat("world[" + i2 + "][height]", this.buildLimit);
                    playerSnooper.addClientStat("world[" + i2 + "][chunks_loaded]", worldserver.getChunkProvider().getLoadedChunkCount());
                    ++i2;
                }
                ++j2;
            }
        }
        playerSnooper.addClientStat("worlds", i2);
    }

    @Override
    public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper) {
        playerSnooper.addStatToSnooper("singleplayer", this.isSinglePlayer());
        playerSnooper.addStatToSnooper("server_brand", this.getServerModName());
        playerSnooper.addStatToSnooper("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
        playerSnooper.addStatToSnooper("dedicated", this.isDedicatedServer());
    }

    @Override
    public boolean isSnooperEnabled() {
        return true;
    }

    public abstract boolean isDedicatedServer();

    public boolean isServerInOnlineMode() {
        return this.onlineMode;
    }

    public void setOnlineMode(boolean online) {
        this.onlineMode = online;
    }

    public boolean getCanSpawnAnimals() {
        return this.canSpawnAnimals;
    }

    public void setCanSpawnAnimals(boolean spawnAnimals) {
        this.canSpawnAnimals = spawnAnimals;
    }

    public boolean getCanSpawnNPCs() {
        return this.canSpawnNPCs;
    }

    public abstract boolean shouldUseNativeTransport();

    public void setCanSpawnNPCs(boolean spawnNpcs) {
        this.canSpawnNPCs = spawnNpcs;
    }

    public boolean isPVPEnabled() {
        return this.pvpEnabled;
    }

    public void setAllowPvp(boolean allowPvp) {
        this.pvpEnabled = allowPvp;
    }

    public boolean isFlightAllowed() {
        return this.allowFlight;
    }

    public void setAllowFlight(boolean allow) {
        this.allowFlight = allow;
    }

    public abstract boolean isCommandBlockEnabled();

    public String getMOTD() {
        return this.motd;
    }

    public void setMOTD(String motdIn) {
        this.motd = motdIn;
    }

    public int getBuildLimit() {
        return this.buildLimit;
    }

    public void setBuildLimit(int maxBuildHeight) {
        this.buildLimit = maxBuildHeight;
    }

    public boolean isServerStopped() {
        return this.serverStopped;
    }

    public ServerConfigurationManager getConfigurationManager() {
        return this.serverConfigManager;
    }

    public void setConfigManager(ServerConfigurationManager configManager) {
        this.serverConfigManager = configManager;
    }

    public void setGameType(WorldSettings.GameType gameMode) {
        int i2 = 0;
        while (i2 < this.worldServers.length) {
            MinecraftServer.getServer().worldServers[i2].getWorldInfo().setGameType(gameMode);
            ++i2;
        }
    }

    public NetworkSystem getNetworkSystem() {
        return this.networkSystem;
    }

    public boolean serverIsInRunLoop() {
        return this.serverIsRunning;
    }

    public boolean getGuiEnabled() {
        return false;
    }

    public abstract String shareToLAN(WorldSettings.GameType var1, boolean var2);

    public int getTickCounter() {
        return this.tickCounter;
    }

    public void enableProfiling() {
        this.startProfiling = true;
    }

    public PlayerUsageSnooper getPlayerUsageSnooper() {
        return this.usageSnooper;
    }

    @Override
    public BlockPos getPosition() {
        return BlockPos.ORIGIN;
    }

    @Override
    public Vec3 getPositionVector() {
        return new Vec3(0.0, 0.0, 0.0);
    }

    @Override
    public World getEntityWorld() {
        return this.worldServers[0];
    }

    @Override
    public Entity getCommandSenderEntity() {
        return null;
    }

    public int getSpawnProtectionSize() {
        return 16;
    }

    public boolean isBlockProtected(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        return false;
    }

    public boolean getForceGamemode() {
        return this.isGamemodeForced;
    }

    public Proxy getServerProxy() {
        return this.serverProxy;
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public int getMaxPlayerIdleMinutes() {
        return this.maxPlayerIdleMinutes;
    }

    public void setPlayerIdleTimeout(int idleTimeout) {
        this.maxPlayerIdleMinutes = idleTimeout;
    }

    @Override
    public IChatComponent getDisplayName() {
        return new ChatComponentText(this.getName());
    }

    public boolean isAnnouncingPlayerAchievements() {
        return true;
    }

    public MinecraftSessionService getMinecraftSessionService() {
        return this.sessionService;
    }

    public GameProfileRepository getGameProfileRepository() {
        return this.profileRepo;
    }

    public PlayerProfileCache getPlayerProfileCache() {
        return this.profileCache;
    }

    public ServerStatusResponse getServerStatusResponse() {
        return this.statusResponse;
    }

    public void refreshStatusNextTick() {
        this.nanoTimeSinceStatusRefresh = 0L;
    }

    public Entity getEntityFromUuid(UUID uuid) {
        WorldServer[] worldServerArray = this.worldServers;
        int n2 = this.worldServers.length;
        int n3 = 0;
        while (n3 < n2) {
            Entity entity;
            WorldServer worldserver = worldServerArray[n3];
            if (worldserver != null && (entity = worldserver.getEntityFromUuid(uuid)) != null) {
                return entity;
            }
            ++n3;
        }
        return null;
    }

    @Override
    public boolean sendCommandFeedback() {
        return MinecraftServer.getServer().worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
    }

    @Override
    public void setCommandStat(CommandResultStats.Type type, int amount) {
    }

    public int getMaxWorldSize() {
        return 29999984;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <V> ListenableFuture<V> callFromMainThread(Callable<V> callable) {
        Validate.notNull(callable);
        if (!this.isCallingFromMinecraftThread() && !this.isServerStopped()) {
            ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callable);
            Queue<FutureTask<?>> queue = this.futureTaskQueue;
            synchronized (queue) {
                this.futureTaskQueue.add(listenablefuturetask);
                return listenablefuturetask;
            }
        }
        try {
            return Futures.immediateFuture(callable.call());
        }
        catch (Exception exception) {
            return Futures.immediateFailedCheckedFuture(exception);
        }
    }

    @Override
    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
        Validate.notNull(runnableToSchedule);
        return this.callFromMainThread(Executors.callable(runnableToSchedule));
    }

    @Override
    public boolean isCallingFromMinecraftThread() {
        return Thread.currentThread() == this.serverThread;
    }

    public int getNetworkCompressionTreshold() {
        return 256;
    }
}

