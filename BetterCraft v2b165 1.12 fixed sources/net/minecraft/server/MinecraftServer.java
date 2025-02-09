// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import net.minecraft.advancements.FunctionManager;
import net.minecraft.advancements.AdvancementManager;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import net.minecraft.entity.Entity;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import java.awt.GraphicsEnvironment;
import java.util.Iterator;
import net.minecraft.command.CommandBase;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.Util;
import java.util.Collections;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import com.mojang.authlib.GameProfile;
import java.awt.image.BufferedImage;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.base64.Base64;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import io.netty.buffer.ByteBufOutputStream;
import org.apache.commons.lang3.Validate;
import javax.imageio.ImageIO;
import io.netty.buffer.Unpooled;
import java.util.Date;
import java.text.SimpleDateFormat;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.ServerWorldEventHandler;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.WorldType;
import javax.annotation.Nullable;
import net.minecraft.util.IProgressUpdate;
import java.io.IOException;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import com.google.common.collect.Queues;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import java.util.concurrent.FutureTask;
import java.util.Queue;
import net.minecraft.server.management.PlayerProfileCache;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.security.KeyPair;
import java.net.Proxy;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;
import net.minecraft.util.datafix.DataFixer;
import java.util.Random;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.NetworkSystem;
import net.minecraft.profiler.Profiler;
import net.minecraft.command.ICommandManager;
import net.minecraft.util.ITickable;
import java.util.List;
import net.minecraft.profiler.Snooper;
import net.minecraft.world.storage.ISaveFormat;
import java.io.File;
import org.apache.logging.log4j.Logger;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.util.IThreadListener;
import net.minecraft.command.ICommandSender;

public abstract class MinecraftServer implements ICommandSender, Runnable, IThreadListener, ISnooperInfo
{
    private static final Logger LOG;
    public static final File USER_CACHE_FILE;
    private final ISaveFormat anvilConverterForAnvilFile;
    private final Snooper usageSnooper;
    private final File anvilFile;
    private final List<ITickable> tickables;
    public final ICommandManager commandManager;
    public final Profiler theProfiler;
    private final NetworkSystem networkSystem;
    private final ServerStatusResponse statusResponse;
    private final Random random;
    private final DataFixer dataFixer;
    private int serverPort;
    public WorldServer[] worldServers;
    private PlayerList playerList;
    private boolean serverRunning;
    private boolean serverStopped;
    private int tickCounter;
    protected final Proxy serverProxy;
    public String currentTask;
    public int percentDone;
    private boolean onlineMode;
    private boolean field_190519_A;
    private boolean canSpawnAnimals;
    private boolean canSpawnNPCs;
    private boolean pvpEnabled;
    private boolean allowFlight;
    private String motd;
    private int buildLimit;
    private int maxPlayerIdleMinutes;
    public final long[] tickTimeArray;
    public long[][] timeOfLastDimensionTick;
    private KeyPair serverKeyPair;
    private String serverOwner;
    private String folderName;
    private String worldName;
    private boolean isDemo;
    private boolean enableBonusChest;
    private String resourcePackUrl;
    private String resourcePackHash;
    private boolean serverIsRunning;
    private long timeOfLastWarning;
    private String userMessage;
    private boolean startProfiling;
    private boolean isGamemodeForced;
    private final YggdrasilAuthenticationService authService;
    private final MinecraftSessionService sessionService;
    private final GameProfileRepository profileRepo;
    private final PlayerProfileCache profileCache;
    private long nanoTimeSinceStatusRefresh;
    public final Queue<FutureTask<?>> futureTaskQueue;
    private Thread serverThread;
    private long currentTime;
    private boolean worldIconSet;
    
    static {
        LOG = LogManager.getLogger();
        USER_CACHE_FILE = new File("usercache.json");
    }
    
    public MinecraftServer(final File anvilFileIn, final Proxy proxyIn, final DataFixer dataFixerIn, final YggdrasilAuthenticationService authServiceIn, final MinecraftSessionService sessionServiceIn, final GameProfileRepository profileRepoIn, final PlayerProfileCache profileCacheIn) {
        this.usageSnooper = new Snooper("server", this, getCurrentTimeMillis());
        this.tickables = (List<ITickable>)Lists.newArrayList();
        this.theProfiler = new Profiler();
        this.statusResponse = new ServerStatusResponse();
        this.random = new Random();
        this.serverPort = -1;
        this.serverRunning = true;
        this.tickTimeArray = new long[100];
        this.resourcePackUrl = "";
        this.resourcePackHash = "";
        this.futureTaskQueue = (Queue<FutureTask<?>>)Queues.newArrayDeque();
        this.currentTime = getCurrentTimeMillis();
        this.serverProxy = proxyIn;
        this.authService = authServiceIn;
        this.sessionService = sessionServiceIn;
        this.profileRepo = profileRepoIn;
        this.profileCache = profileCacheIn;
        this.anvilFile = anvilFileIn;
        this.networkSystem = new NetworkSystem(this);
        this.commandManager = this.createNewCommandManager();
        this.anvilConverterForAnvilFile = new AnvilSaveConverter(anvilFileIn, dataFixerIn);
        this.dataFixer = dataFixerIn;
    }
    
    public ServerCommandManager createNewCommandManager() {
        return new ServerCommandManager(this);
    }
    
    public abstract boolean startServer() throws IOException;
    
    public void convertMapIfNeeded(final String worldNameIn) {
        if (this.getActiveAnvilConverter().isOldMapFormat(worldNameIn)) {
            MinecraftServer.LOG.info("Converting map!");
            this.setUserMessage("menu.convertingLevel");
            this.getActiveAnvilConverter().convertMapFormat(worldNameIn, new IProgressUpdate() {
                private long startTime = System.currentTimeMillis();
                
                @Override
                public void displaySavingString(final String message) {
                }
                
                @Override
                public void resetProgressAndMessage(final String message) {
                }
                
                @Override
                public void setLoadingProgress(final int progress) {
                    if (System.currentTimeMillis() - this.startTime >= 1000L) {
                        this.startTime = System.currentTimeMillis();
                        MinecraftServer.LOG.info("Converting... {}%", (Object)progress);
                    }
                }
                
                @Override
                public void setDoneWorking() {
                }
                
                @Override
                public void displayLoadingString(final String message) {
                }
            });
        }
    }
    
    protected synchronized void setUserMessage(final String message) {
        this.userMessage = message;
    }
    
    @Nullable
    public synchronized String getUserMessage() {
        return this.userMessage;
    }
    
    public void loadAllWorlds(final String saveName, final String worldNameIn, final long seed, final WorldType type, final String generatorOptions) {
        this.convertMapIfNeeded(saveName);
        this.setUserMessage("menu.loadingLevel");
        this.worldServers = new WorldServer[3];
        this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
        final ISaveHandler isavehandler = this.anvilConverterForAnvilFile.getSaveLoader(saveName, true);
        this.setResourcePackFromWorld(this.getFolderName(), isavehandler);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();
        WorldSettings worldsettings;
        if (worldinfo == null) {
            if (this.isDemo()) {
                worldsettings = WorldServerDemo.DEMO_WORLD_SETTINGS;
            }
            else {
                worldsettings = new WorldSettings(seed, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), type);
                worldsettings.setGeneratorOptions(generatorOptions);
                if (this.enableBonusChest) {
                    worldsettings.enableBonusChest();
                }
            }
            worldinfo = new WorldInfo(worldsettings, worldNameIn);
        }
        else {
            worldinfo.setWorldName(worldNameIn);
            worldsettings = new WorldSettings(worldinfo);
        }
        for (int i = 0; i < this.worldServers.length; ++i) {
            int j = 0;
            if (i == 1) {
                j = -1;
            }
            if (i == 2) {
                j = 1;
            }
            if (i == 0) {
                if (this.isDemo()) {
                    this.worldServers[i] = (WorldServer)new WorldServerDemo(this, isavehandler, worldinfo, j, this.theProfiler).init();
                }
                else {
                    this.worldServers[i] = (WorldServer)new WorldServer(this, isavehandler, worldinfo, j, this.theProfiler).init();
                }
                this.worldServers[i].initialize(worldsettings);
            }
            else {
                this.worldServers[i] = (WorldServer)new WorldServerMulti(this, isavehandler, j, this.worldServers[0], this.theProfiler).init();
            }
            this.worldServers[i].addEventListener(new ServerWorldEventHandler(this, this.worldServers[i]));
            if (!this.isSinglePlayer()) {
                this.worldServers[i].getWorldInfo().setGameType(this.getGameType());
            }
        }
        this.playerList.setPlayerManager(this.worldServers);
        this.setDifficultyForAllWorlds(this.getDifficulty());
        this.initialWorldChunkLoad();
    }
    
    public void initialWorldChunkLoad() {
        final int i = 16;
        final int j = 4;
        final int k = 192;
        final int l = 625;
        int i2 = 0;
        this.setUserMessage("menu.generatingTerrain");
        final int j2 = 0;
        MinecraftServer.LOG.info("Preparing start region for level 0");
        final WorldServer worldserver = this.worldServers[0];
        final BlockPos blockpos = worldserver.getSpawnPoint();
        long k2 = getCurrentTimeMillis();
        for (int l2 = -192; l2 <= 192 && this.isServerRunning(); l2 += 16) {
            for (int i3 = -192; i3 <= 192 && this.isServerRunning(); i3 += 16) {
                final long j3 = getCurrentTimeMillis();
                if (j3 - k2 > 1000L) {
                    this.outputPercentRemaining("Preparing spawn area", i2 * 100 / 625);
                    k2 = j3;
                }
                ++i2;
                worldserver.getChunkProvider().provideChunk(blockpos.getX() + l2 >> 4, blockpos.getZ() + i3 >> 4);
            }
        }
        this.clearCurrentTask();
    }
    
    public void setResourcePackFromWorld(final String worldNameIn, final ISaveHandler saveHandlerIn) {
        final File file1 = new File(saveHandlerIn.getWorldDirectory(), "resources.zip");
        if (file1.isFile()) {
            try {
                this.setResourcePack("level://" + URLEncoder.encode(worldNameIn, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
            }
            catch (final UnsupportedEncodingException var5) {
                MinecraftServer.LOG.warn("Something went wrong url encoding {}", worldNameIn);
            }
        }
    }
    
    public abstract boolean canStructuresSpawn();
    
    public abstract GameType getGameType();
    
    public abstract EnumDifficulty getDifficulty();
    
    public abstract boolean isHardcore();
    
    public abstract int getOpPermissionLevel();
    
    public abstract boolean shouldBroadcastRconToOps();
    
    public abstract boolean shouldBroadcastConsoleToOps();
    
    protected void outputPercentRemaining(final String message, final int percent) {
        this.currentTask = message;
        this.percentDone = percent;
        MinecraftServer.LOG.info("{}: {}%", message, percent);
    }
    
    protected void clearCurrentTask() {
        this.currentTask = null;
        this.percentDone = 0;
    }
    
    public void saveAllWorlds(final boolean isSilent) {
        WorldServer[] worldServers;
        for (int length = (worldServers = this.worldServers).length, i = 0; i < length; ++i) {
            final WorldServer worldserver = worldServers[i];
            if (worldserver != null) {
                if (!isSilent) {
                    MinecraftServer.LOG.info("Saving chunks for level '{}'/{}", worldserver.getWorldInfo().getWorldName(), worldserver.provider.getDimensionType().getName());
                }
                try {
                    worldserver.saveAllChunks(true, null);
                }
                catch (final MinecraftException minecraftexception) {
                    MinecraftServer.LOG.warn(minecraftexception.getMessage());
                }
            }
        }
    }
    
    public void stopServer() {
        MinecraftServer.LOG.info("Stopping server");
        if (this.getNetworkSystem() != null) {
            this.getNetworkSystem().terminateEndpoints();
        }
        if (this.playerList != null) {
            MinecraftServer.LOG.info("Saving players");
            this.playerList.saveAllPlayerData();
            this.playerList.removeAllPlayers();
        }
        if (this.worldServers != null) {
            MinecraftServer.LOG.info("Saving worlds");
            WorldServer[] worldServers;
            for (int length = (worldServers = this.worldServers).length, i = 0; i < length; ++i) {
                final WorldServer worldserver = worldServers[i];
                if (worldserver != null) {
                    worldserver.disableLevelSaving = false;
                }
            }
            this.saveAllWorlds(false);
            WorldServer[] worldServers2;
            for (int length2 = (worldServers2 = this.worldServers).length, j = 0; j < length2; ++j) {
                final WorldServer worldserver2 = worldServers2[j];
                if (worldserver2 != null) {
                    worldserver2.flush();
                }
            }
        }
        if (this.usageSnooper.isSnooperRunning()) {
            this.usageSnooper.stopSnooper();
        }
    }
    
    public boolean isServerRunning() {
        return this.serverRunning;
    }
    
    public void initiateShutdown() {
        this.serverRunning = false;
    }
    
    @Override
    public void run() {
        try {
            if (this.startServer()) {
                this.currentTime = getCurrentTimeMillis();
                long i = 0L;
                this.statusResponse.setServerDescription(new TextComponentString(this.motd));
                this.statusResponse.setVersion(new ServerStatusResponse.Version("1.12.2", 340));
                this.applyServerIconToResponse(this.statusResponse);
                while (this.serverRunning) {
                    final long k = getCurrentTimeMillis();
                    long j = k - this.currentTime;
                    if (j > 2000L && this.currentTime - this.timeOfLastWarning >= 15000L) {
                        MinecraftServer.LOG.warn("Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", (Object)j, j / 50L);
                        j = 2000L;
                        this.timeOfLastWarning = this.currentTime;
                    }
                    if (j < 0L) {
                        MinecraftServer.LOG.warn("Time ran backwards! Did the system time change?");
                        j = 0L;
                    }
                    i += j;
                    this.currentTime = k;
                    if (this.worldServers[0].areAllPlayersAsleep()) {
                        this.tick();
                        i = 0L;
                    }
                    else {
                        while (i > 50L) {
                            i -= 50L;
                            this.tick();
                        }
                    }
                    Thread.sleep(Math.max(1L, 50L - i));
                    this.serverIsRunning = true;
                }
            }
            else {
                this.finalTick(null);
            }
        }
        catch (final Throwable throwable1) {
            MinecraftServer.LOG.error("Encountered an unexpected exception", throwable1);
            CrashReport crashreport = null;
            if (throwable1 instanceof ReportedException) {
                crashreport = this.addServerInfoToCrashReport(((ReportedException)throwable1).getCrashReport());
            }
            else {
                crashreport = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", throwable1));
            }
            final File file1 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
            if (crashreport.saveToFile(file1)) {
                MinecraftServer.LOG.error("This crash report has been saved to: {}", file1.getAbsolutePath());
            }
            else {
                MinecraftServer.LOG.error("We were unable to save this crash report to disk.");
            }
            this.finalTick(crashreport);
            return;
        }
        finally {
            Label_0503: {
                try {
                    this.serverStopped = true;
                    this.stopServer();
                }
                catch (final Throwable throwable2) {
                    MinecraftServer.LOG.error("Exception stopping the server", throwable2);
                    this.systemExitNow();
                    break Label_0503;
                }
                finally {
                    this.systemExitNow();
                }
                this.systemExitNow();
            }
        }
        try {
            this.serverStopped = true;
            this.stopServer();
        }
        catch (final Throwable throwable2) {
            MinecraftServer.LOG.error("Exception stopping the server", throwable2);
            return;
        }
        finally {
            this.systemExitNow();
        }
        this.systemExitNow();
    }
    
    public void applyServerIconToResponse(final ServerStatusResponse response) {
        File file1 = this.getFile("server-icon.png");
        if (!file1.exists()) {
            file1 = this.getActiveAnvilConverter().getFile(this.getFolderName(), "icon.png");
        }
        if (file1.isFile()) {
            final ByteBuf bytebuf = Unpooled.buffer();
            try {
                final BufferedImage bufferedimage = ImageIO.read(file1);
                Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
                final ByteBuf bytebuf2 = Base64.encode(bytebuf);
                response.setFavicon("data:image/png;base64," + bytebuf2.toString(StandardCharsets.UTF_8));
            }
            catch (final Exception exception) {
                MinecraftServer.LOG.error("Couldn't load server icon", exception);
                return;
            }
            finally {
                bytebuf.release();
            }
            bytebuf.release();
        }
    }
    
    public boolean isWorldIconSet() {
        return this.worldIconSet = (this.worldIconSet || this.getWorldIconFile().isFile());
    }
    
    public File getWorldIconFile() {
        return this.getActiveAnvilConverter().getFile(this.getFolderName(), "icon.png");
    }
    
    public File getDataDirectory() {
        return new File(".");
    }
    
    public void finalTick(final CrashReport report) {
    }
    
    public void systemExitNow() {
    }
    
    public void tick() {
        final long i = System.nanoTime();
        ++this.tickCounter;
        if (this.startProfiling) {
            this.startProfiling = false;
            this.theProfiler.profilingEnabled = true;
            this.theProfiler.clearProfiling();
        }
        this.theProfiler.startSection("root");
        this.updateTimeLightAndEntities();
        if (i - this.nanoTimeSinceStatusRefresh >= 5000000000L) {
            this.nanoTimeSinceStatusRefresh = i;
            this.statusResponse.setPlayers(new ServerStatusResponse.Players(this.getMaxPlayers(), this.getCurrentPlayerCount()));
            final GameProfile[] agameprofile = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
            final int j = MathHelper.getInt(this.random, 0, this.getCurrentPlayerCount() - agameprofile.length);
            for (int k = 0; k < agameprofile.length; ++k) {
                agameprofile[k] = this.playerList.getPlayerList().get(j + k).getGameProfile();
            }
            Collections.shuffle(Arrays.asList(agameprofile));
            this.statusResponse.getPlayers().setPlayers(agameprofile);
        }
        if (this.tickCounter % 900 == 0) {
            this.theProfiler.startSection("save");
            this.playerList.saveAllPlayerData();
            this.saveAllWorlds(true);
            this.theProfiler.endSection();
        }
        this.theProfiler.startSection("tallying");
        this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - i;
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
    
    public void updateTimeLightAndEntities() {
        this.theProfiler.startSection("jobs");
        synchronized (this.futureTaskQueue) {
            while (!this.futureTaskQueue.isEmpty()) {
                Util.runTask(this.futureTaskQueue.poll(), MinecraftServer.LOG);
            }
            monitorexit(this.futureTaskQueue);
        }
        this.theProfiler.endStartSection("levels");
        for (int j = 0; j < this.worldServers.length; ++j) {
            final long i = System.nanoTime();
            if (j == 0 || this.getAllowNether()) {
                final WorldServer worldserver = this.worldServers[j];
                this.theProfiler.func_194340_a(() -> worldServer.getWorldInfo().getWorldName());
                if (this.tickCounter % 20 == 0) {
                    this.theProfiler.startSection("timeSync");
                    this.playerList.sendPacketToAllPlayersInDimension(new SPacketTimeUpdate(worldserver.getTotalWorldTime(), worldserver.getWorldTime(), worldserver.getGameRules().getBoolean("doDaylightCycle")), worldserver.provider.getDimensionType().getId());
                    this.theProfiler.endSection();
                }
                this.theProfiler.startSection("tick");
                try {
                    worldserver.tick();
                }
                catch (final Throwable throwable1) {
                    final CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Exception ticking world");
                    worldserver.addWorldInfoToCrashReport(crashreport);
                    throw new ReportedException(crashreport);
                }
                try {
                    worldserver.updateEntities();
                }
                catch (final Throwable throwable2) {
                    final CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception ticking world entities");
                    worldserver.addWorldInfoToCrashReport(crashreport2);
                    throw new ReportedException(crashreport2);
                }
                this.theProfiler.endSection();
                this.theProfiler.startSection("tracker");
                worldserver.getEntityTracker().updateTrackedEntities();
                this.theProfiler.endSection();
                this.theProfiler.endSection();
            }
            this.timeOfLastDimensionTick[j][this.tickCounter % 100] = System.nanoTime() - i;
        }
        this.theProfiler.endStartSection("connection");
        this.getNetworkSystem().networkTick();
        this.theProfiler.endStartSection("players");
        this.playerList.onTick();
        this.theProfiler.endStartSection("commandFunctions");
        this.func_193030_aL().update();
        this.theProfiler.endStartSection("tickables");
        for (int k = 0; k < this.tickables.size(); ++k) {
            this.tickables.get(k).update();
        }
        this.theProfiler.endSection();
    }
    
    public boolean getAllowNether() {
        return true;
    }
    
    public void startServerThread() {
        (this.serverThread = new Thread(this, "Server thread")).start();
    }
    
    public File getFile(final String fileName) {
        return new File(this.getDataDirectory(), fileName);
    }
    
    public void logWarning(final String msg) {
        MinecraftServer.LOG.warn(msg);
    }
    
    public WorldServer worldServerForDimension(final int dimension) {
        if (dimension == -1) {
            return this.worldServers[1];
        }
        return (dimension == 1) ? this.worldServers[2] : this.worldServers[0];
    }
    
    public String getMinecraftVersion() {
        return "1.12.2";
    }
    
    public int getCurrentPlayerCount() {
        return this.playerList.getCurrentPlayerCount();
    }
    
    public int getMaxPlayers() {
        return this.playerList.getMaxPlayers();
    }
    
    public String[] getAllUsernames() {
        return this.playerList.getAllUsernames();
    }
    
    public GameProfile[] getGameProfiles() {
        return this.playerList.getAllProfiles();
    }
    
    public String getServerModName() {
        return "vanilla";
    }
    
    public CrashReport addServerInfoToCrashReport(final CrashReport report) {
        report.getCategory().setDetail("Profiler Position", new ICrashReportDetail<String>() {
            @Override
            public String call() throws Exception {
                return MinecraftServer.this.theProfiler.profilingEnabled ? MinecraftServer.this.theProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        if (this.playerList != null) {
            report.getCategory().setDetail("Player Count", new ICrashReportDetail<String>() {
                @Override
                public String call() {
                    return String.valueOf(MinecraftServer.this.playerList.getCurrentPlayerCount()) + " / " + MinecraftServer.this.playerList.getMaxPlayers() + "; " + MinecraftServer.this.playerList.getPlayerList();
                }
            });
        }
        return report;
    }
    
    public List<String> getTabCompletions(final ICommandSender sender, String input, @Nullable final BlockPos pos, final boolean hasTargetBlock) {
        final List<String> list = (List<String>)Lists.newArrayList();
        final boolean flag = input.startsWith("/");
        if (flag) {
            input = input.substring(1);
        }
        if (!flag && !hasTargetBlock) {
            final String[] astring = input.split(" ", -1);
            final String s2 = astring[astring.length - 1];
            String[] allUsernames;
            for (int length = (allUsernames = this.playerList.getAllUsernames()).length, i = 0; i < length; ++i) {
                final String s3 = allUsernames[i];
                if (CommandBase.doesStringStartWith(s2, s3)) {
                    list.add(s3);
                }
            }
            return list;
        }
        final boolean flag2 = !input.contains(" ");
        final List<String> list2 = this.commandManager.getTabCompletionOptions(sender, input, pos);
        if (!list2.isEmpty()) {
            for (final String s4 : list2) {
                if (flag2 && !hasTargetBlock) {
                    list.add("/" + s4);
                }
                else {
                    list.add(s4);
                }
            }
        }
        return list;
    }
    
    public boolean isAnvilFileSet() {
        return this.anvilFile != null;
    }
    
    @Override
    public String getName() {
        return "Server";
    }
    
    @Override
    public void addChatMessage(final ITextComponent component) {
        MinecraftServer.LOG.info(component.getUnformattedText());
    }
    
    @Override
    public boolean canCommandSenderUseCommand(final int permLevel, final String commandName) {
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
    
    public void setServerOwner(final String owner) {
        this.serverOwner = owner;
    }
    
    public boolean isSinglePlayer() {
        return this.serverOwner != null;
    }
    
    public String getFolderName() {
        return this.folderName;
    }
    
    public void setFolderName(final String name) {
        this.folderName = name;
    }
    
    public void setWorldName(final String worldNameIn) {
        this.worldName = worldNameIn;
    }
    
    public String getWorldName() {
        return this.worldName;
    }
    
    public void setKeyPair(final KeyPair keyPair) {
        this.serverKeyPair = keyPair;
    }
    
    public void setDifficultyForAllWorlds(final EnumDifficulty difficulty) {
        WorldServer[] worldServers;
        for (int length = (worldServers = this.worldServers).length, i = 0; i < length; ++i) {
            final WorldServer worldserver1 = worldServers[i];
            if (worldserver1 != null) {
                if (worldserver1.getWorldInfo().isHardcoreModeEnabled()) {
                    worldserver1.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
                    worldserver1.setAllowedSpawnTypes(true, true);
                }
                else if (this.isSinglePlayer()) {
                    worldserver1.getWorldInfo().setDifficulty(difficulty);
                    worldserver1.setAllowedSpawnTypes(worldserver1.getDifficulty() != EnumDifficulty.PEACEFUL, true);
                }
                else {
                    worldserver1.getWorldInfo().setDifficulty(difficulty);
                    worldserver1.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
                }
            }
        }
    }
    
    public boolean allowSpawnMonsters() {
        return true;
    }
    
    public boolean isDemo() {
        return this.isDemo;
    }
    
    public void setDemo(final boolean demo) {
        this.isDemo = demo;
    }
    
    public void canCreateBonusChest(final boolean enable) {
        this.enableBonusChest = enable;
    }
    
    public ISaveFormat getActiveAnvilConverter() {
        return this.anvilConverterForAnvilFile;
    }
    
    public String getResourcePackUrl() {
        return this.resourcePackUrl;
    }
    
    public String getResourcePackHash() {
        return this.resourcePackHash;
    }
    
    public void setResourcePack(final String url, final String hash) {
        this.resourcePackUrl = url;
        this.resourcePackHash = hash;
    }
    
    @Override
    public void addServerStatsToSnooper(final Snooper playerSnooper) {
        playerSnooper.addClientStat("whitelist_enabled", false);
        playerSnooper.addClientStat("whitelist_count", 0);
        if (this.playerList != null) {
            playerSnooper.addClientStat("players_current", this.getCurrentPlayerCount());
            playerSnooper.addClientStat("players_max", this.getMaxPlayers());
            playerSnooper.addClientStat("players_seen", this.playerList.getAvailablePlayerDat().length);
        }
        playerSnooper.addClientStat("uses_auth", this.onlineMode);
        playerSnooper.addClientStat("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
        playerSnooper.addClientStat("run_time", (getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
        playerSnooper.addClientStat("avg_tick_ms", (int)(MathHelper.average(this.tickTimeArray) * 1.0E-6));
        int l = 0;
        if (this.worldServers != null) {
            WorldServer[] worldServers;
            for (int length = (worldServers = this.worldServers).length, i = 0; i < length; ++i) {
                final WorldServer worldserver1 = worldServers[i];
                if (worldserver1 != null) {
                    final WorldInfo worldinfo = worldserver1.getWorldInfo();
                    playerSnooper.addClientStat("world[" + l + "][dimension]", worldserver1.provider.getDimensionType().getId());
                    playerSnooper.addClientStat("world[" + l + "][mode]", worldinfo.getGameType());
                    playerSnooper.addClientStat("world[" + l + "][difficulty]", worldserver1.getDifficulty());
                    playerSnooper.addClientStat("world[" + l + "][hardcore]", worldinfo.isHardcoreModeEnabled());
                    playerSnooper.addClientStat("world[" + l + "][generator_name]", worldinfo.getTerrainType().getWorldTypeName());
                    playerSnooper.addClientStat("world[" + l + "][generator_version]", worldinfo.getTerrainType().getGeneratorVersion());
                    playerSnooper.addClientStat("world[" + l + "][height]", this.buildLimit);
                    playerSnooper.addClientStat("world[" + l + "][chunks_loaded]", worldserver1.getChunkProvider().getLoadedChunkCount());
                    ++l;
                }
            }
        }
        playerSnooper.addClientStat("worlds", l);
    }
    
    @Override
    public void addServerTypeToSnooper(final Snooper playerSnooper) {
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
    
    public void setOnlineMode(final boolean online) {
        this.onlineMode = online;
    }
    
    public boolean func_190518_ac() {
        return this.field_190519_A;
    }
    
    public boolean getCanSpawnAnimals() {
        return this.canSpawnAnimals;
    }
    
    public void setCanSpawnAnimals(final boolean spawnAnimals) {
        this.canSpawnAnimals = spawnAnimals;
    }
    
    public boolean getCanSpawnNPCs() {
        return this.canSpawnNPCs;
    }
    
    public abstract boolean shouldUseNativeTransport();
    
    public void setCanSpawnNPCs(final boolean spawnNpcs) {
        this.canSpawnNPCs = spawnNpcs;
    }
    
    public boolean isPVPEnabled() {
        return this.pvpEnabled;
    }
    
    public void setAllowPvp(final boolean allowPvp) {
        this.pvpEnabled = allowPvp;
    }
    
    public boolean isFlightAllowed() {
        return this.allowFlight;
    }
    
    public void setAllowFlight(final boolean allow) {
        this.allowFlight = allow;
    }
    
    public abstract boolean isCommandBlockEnabled();
    
    public String getMOTD() {
        return this.motd;
    }
    
    public void setMOTD(final String motdIn) {
        this.motd = motdIn;
    }
    
    public int getBuildLimit() {
        return this.buildLimit;
    }
    
    public void setBuildLimit(final int maxBuildHeight) {
        this.buildLimit = maxBuildHeight;
    }
    
    public boolean isServerStopped() {
        return this.serverStopped;
    }
    
    public PlayerList getPlayerList() {
        return this.playerList;
    }
    
    public void setPlayerList(final PlayerList list) {
        this.playerList = list;
    }
    
    public void setGameType(final GameType gameMode) {
        WorldServer[] worldServers;
        for (int length = (worldServers = this.worldServers).length, i = 0; i < length; ++i) {
            final WorldServer worldserver1 = worldServers[i];
            worldserver1.getWorldInfo().setGameType(gameMode);
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
    
    public abstract String shareToLAN(final GameType p0, final boolean p1);
    
    public int getTickCounter() {
        return this.tickCounter;
    }
    
    public void enableProfiling() {
        this.startProfiling = true;
    }
    
    public Snooper getPlayerUsageSnooper() {
        return this.usageSnooper;
    }
    
    @Override
    public World getEntityWorld() {
        return this.worldServers[0];
    }
    
    public boolean isBlockProtected(final World worldIn, final BlockPos pos, final EntityPlayer playerIn) {
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
    
    public void setPlayerIdleTimeout(final int idleTimeout) {
        this.maxPlayerIdleMinutes = idleTimeout;
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
    
    @Nullable
    public Entity getEntityFromUuid(final UUID uuid) {
        WorldServer[] worldServers;
        for (int length = (worldServers = this.worldServers).length, i = 0; i < length; ++i) {
            final WorldServer worldserver1 = worldServers[i];
            if (worldserver1 != null) {
                final Entity entity = worldserver1.getEntityFromUuid(uuid);
                if (entity != null) {
                    return entity;
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean sendCommandFeedback() {
        return this.worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
    }
    
    @Override
    public MinecraftServer getServer() {
        return this;
    }
    
    public int getMaxWorldSize() {
        return 29999984;
    }
    
    public <V> ListenableFuture<V> callFromMainThread(final Callable<V> callable) {
        Validate.notNull(callable);
        if (!this.isCallingFromMinecraftThread() && !this.isServerStopped()) {
            final ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callable);
            synchronized (this.futureTaskQueue) {
                this.futureTaskQueue.add(listenablefuturetask);
                final ListenableFutureTask<V> listenableFutureTask = listenablefuturetask;
                monitorexit(this.futureTaskQueue);
                return listenableFutureTask;
            }
        }
        try {
            return Futures.immediateFuture(callable.call());
        }
        catch (final Exception exception) {
            return (ListenableFuture<V>)Futures.immediateFailedCheckedFuture(exception);
        }
    }
    
    @Override
    public ListenableFuture<Object> addScheduledTask(final Runnable runnableToSchedule) {
        Validate.notNull(runnableToSchedule);
        return this.callFromMainThread(Executors.callable(runnableToSchedule));
    }
    
    @Override
    public boolean isCallingFromMinecraftThread() {
        return Thread.currentThread() == this.serverThread;
    }
    
    public int getNetworkCompressionThreshold() {
        return 256;
    }
    
    public int getSpawnRadius(@Nullable final WorldServer worldIn) {
        return (worldIn != null) ? worldIn.getGameRules().getInt("spawnRadius") : 10;
    }
    
    public AdvancementManager func_191949_aK() {
        return this.worldServers[0].func_191952_z();
    }
    
    public FunctionManager func_193030_aL() {
        return this.worldServers[0].func_193037_A();
    }
    
    public void func_193031_aM() {
        if (this.isCallingFromMinecraftThread()) {
            this.getPlayerList().saveAllPlayerData();
            this.worldServers[0].getLootTableManager().reloadLootTables();
            this.func_191949_aK().func_192779_a();
            this.func_193030_aL().func_193059_f();
            this.getPlayerList().func_193244_w();
        }
        else {
            this.addScheduledTask(this::func_193031_aM);
        }
    }
}
