/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedPlayerList;
import net.minecraft.server.integrated.IntegratedServerCommandManager;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.ClearWater;
import net.optifine.reflect.Reflector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer
extends MinecraftServer {
    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc;
    private final WorldSettings theWorldSettings;
    private boolean isGamePaused;
    private boolean isPublic;
    private ThreadLanServerPing lanServerPing;
    private long ticksSaveLast = 0L;
    public World difficultyUpdateWorld = null;
    public BlockPos difficultyUpdatePos = null;
    public DifficultyInstance difficultyLast = null;

    public IntegratedServer(Minecraft mcIn) {
        super(mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
        this.mc = mcIn;
        this.theWorldSettings = null;
    }

    public IntegratedServer(Minecraft mcIn, String folderName, String worldName, WorldSettings settings) {
        super(new File(mcIn.mcDataDir, "saves"), mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
        NBTTagCompound nbttagcompound;
        this.setServerOwner(mcIn.getSession().getUsername());
        this.setFolderName(folderName);
        this.setWorldName(worldName);
        this.setDemo(mcIn.isDemo());
        this.canCreateBonusChest(settings.isBonusChestEnabled());
        this.setBuildLimit(256);
        this.setConfigManager(new IntegratedPlayerList(this));
        this.mc = mcIn;
        this.theWorldSettings = this.isDemo() ? DemoWorldServer.demoWorldSettings : settings;
        ISaveHandler isavehandler = this.getActiveAnvilConverter().getSaveLoader(folderName, false);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();
        if (worldinfo != null && (nbttagcompound = worldinfo.getPlayerNBTTagCompound()) != null && nbttagcompound.hasKey("Dimension")) {
            int i2;
            PacketThreadUtil.lastDimensionId = i2 = nbttagcompound.getInteger("Dimension");
            this.mc.loadingScreen.setLoadingProgress(-1);
        }
    }

    @Override
    protected ServerCommandManager createNewCommandManager() {
        return new IntegratedServerCommandManager();
    }

    @Override
    protected void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String worldNameIn2) {
        this.convertMapIfNeeded(saveName);
        boolean flag = Reflector.DimensionManager.exists();
        if (!flag) {
            this.worldServers = new WorldServer[3];
            this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
        }
        ISaveHandler isavehandler = this.getActiveAnvilConverter().getSaveLoader(saveName, true);
        this.setResourcePackFromWorld(this.getFolderName(), isavehandler);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();
        if (worldinfo == null) {
            worldinfo = new WorldInfo(this.theWorldSettings, worldNameIn);
        } else {
            worldinfo.setWorldName(worldNameIn);
        }
        if (flag) {
            Integer[] ainteger;
            WorldServer worldserver = this.isDemo() ? (WorldServer)new DemoWorldServer(this, isavehandler, worldinfo, 0, this.theProfiler).init() : (WorldServer)new WorldServer(this, isavehandler, worldinfo, 0, this.theProfiler).init();
            worldserver.initialize(this.theWorldSettings);
            Integer[] ainteger1 = ainteger = (Integer[])Reflector.call(Reflector.DimensionManager_getStaticDimensionIDs, new Object[0]);
            int i2 = ainteger.length;
            int j2 = 0;
            while (j2 < i2) {
                int k2 = ainteger1[j2];
                WorldServer worldserver1 = k2 == 0 ? worldserver : (WorldServer)new WorldServerMulti((MinecraftServer)this, isavehandler, k2, worldserver, this.theProfiler).init();
                worldserver1.addWorldAccess(new WorldManager(this, worldserver1));
                if (!this.isSinglePlayer()) {
                    worldserver1.getWorldInfo().setGameType(this.getGameType());
                }
                if (Reflector.EventBus.exists()) {
                    Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, worldserver1);
                }
                ++j2;
            }
            this.getConfigurationManager().setPlayerManager(new WorldServer[]{worldserver});
            if (worldserver.getWorldInfo().getDifficulty() == null) {
                this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
            }
        } else {
            int l2 = 0;
            while (l2 < this.worldServers.length) {
                int i1 = 0;
                if (l2 == 1) {
                    i1 = -1;
                }
                if (l2 == 2) {
                    i1 = 1;
                }
                if (l2 == 0) {
                    this.worldServers[l2] = this.isDemo() ? (WorldServer)new DemoWorldServer(this, isavehandler, worldinfo, i1, this.theProfiler).init() : (WorldServer)new WorldServer(this, isavehandler, worldinfo, i1, this.theProfiler).init();
                    this.worldServers[l2].initialize(this.theWorldSettings);
                } else {
                    this.worldServers[l2] = (WorldServer)new WorldServerMulti((MinecraftServer)this, isavehandler, i1, this.worldServers[0], this.theProfiler).init();
                }
                this.worldServers[l2].addWorldAccess(new WorldManager(this, this.worldServers[l2]));
                ++l2;
            }
            this.getConfigurationManager().setPlayerManager(this.worldServers);
            if (this.worldServers[0].getWorldInfo().getDifficulty() == null) {
                this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
            }
        }
        this.initialWorldChunkLoad();
    }

    @Override
    protected boolean startServer() throws IOException {
        logger.info("Starting integrated minecraft server version 1.9");
        this.setOnlineMode(true);
        this.setCanSpawnAnimals(true);
        this.setCanSpawnNPCs(true);
        this.setAllowPvp(true);
        this.setAllowFlight(true);
        logger.info("Generating keypair");
        this.setKeyPair(CryptManager.generateKeyPair());
        if (Reflector.FMLCommonHandler_handleServerAboutToStart.exists()) {
            Object object = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);
            if (!Reflector.callBoolean(object, Reflector.FMLCommonHandler_handleServerAboutToStart, this)) {
                return false;
            }
        }
        this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.getWorldName());
        this.setMOTD(String.valueOf(this.getServerOwner()) + " - " + this.worldServers[0].getWorldInfo().getWorldName());
        if (Reflector.FMLCommonHandler_handleServerStarting.exists()) {
            Object object1 = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);
            if (Reflector.FMLCommonHandler_handleServerStarting.getReturnType() == Boolean.TYPE) {
                return Reflector.callBoolean(object1, Reflector.FMLCommonHandler_handleServerStarting, this);
            }
            Reflector.callVoid(object1, Reflector.FMLCommonHandler_handleServerStarting, this);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void tick() {
        this.onTick();
        boolean flag = this.isGamePaused;
        boolean bl2 = this.isGamePaused = Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().isGamePaused();
        if (!flag && this.isGamePaused) {
            logger.info("Saving and pausing game...");
            this.getConfigurationManager().saveAllPlayerData();
            this.saveAllWorlds(false);
        }
        if (this.isGamePaused) {
            Queue queue = this.futureTaskQueue;
            synchronized (queue) {
                while (!this.futureTaskQueue.isEmpty()) {
                    Util.runTask((FutureTask)this.futureTaskQueue.poll(), logger);
                }
            }
        } else {
            super.tick();
            if (this.mc.gameSettings.renderDistanceChunks != this.getConfigurationManager().getViewDistance()) {
                logger.info("Changing view distance to {}, from {}", this.mc.gameSettings.renderDistanceChunks, this.getConfigurationManager().getViewDistance());
                this.getConfigurationManager().setViewDistance(this.mc.gameSettings.renderDistanceChunks);
            }
            if (this.mc.theWorld != null) {
                WorldInfo worldinfo1 = this.worldServers[0].getWorldInfo();
                WorldInfo worldinfo = this.mc.theWorld.getWorldInfo();
                if (!worldinfo1.isDifficultyLocked() && worldinfo.getDifficulty() != worldinfo1.getDifficulty()) {
                    logger.info("Changing difficulty to {}, from {}", new Object[]{worldinfo.getDifficulty(), worldinfo1.getDifficulty()});
                    this.setDifficultyForAllWorlds(worldinfo.getDifficulty());
                } else if (worldinfo.isDifficultyLocked() && !worldinfo1.isDifficultyLocked()) {
                    logger.info("Locking difficulty to {}", new Object[]{worldinfo.getDifficulty()});
                    WorldServer[] worldServerArray = this.worldServers;
                    int n2 = this.worldServers.length;
                    int n3 = 0;
                    while (n3 < n2) {
                        WorldServer worldserver = worldServerArray[n3];
                        if (worldserver != null) {
                            worldserver.getWorldInfo().setDifficultyLocked(true);
                        }
                        ++n3;
                    }
                }
            }
        }
    }

    @Override
    public boolean canStructuresSpawn() {
        return false;
    }

    @Override
    public WorldSettings.GameType getGameType() {
        return this.theWorldSettings.getGameType();
    }

    @Override
    public EnumDifficulty getDifficulty() {
        return this.mc.theWorld == null ? this.mc.gameSettings.difficulty : this.mc.theWorld.getWorldInfo().getDifficulty();
    }

    @Override
    public boolean isHardcore() {
        return this.theWorldSettings.getHardcoreEnabled();
    }

    @Override
    public boolean shouldBroadcastRconToOps() {
        return true;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return true;
    }

    @Override
    public void saveAllWorlds(boolean dontLog) {
        if (dontLog) {
            int j2;
            int i2 = this.getTickCounter();
            if ((long)i2 < this.ticksSaveLast + (long)(j2 = this.mc.gameSettings.ofAutoSaveTicks)) {
                return;
            }
            this.ticksSaveLast = i2;
        }
        super.saveAllWorlds(dontLog);
    }

    @Override
    public File getDataDirectory() {
        return this.mc.mcDataDir;
    }

    @Override
    public boolean isDedicatedServer() {
        return false;
    }

    @Override
    public boolean shouldUseNativeTransport() {
        return false;
    }

    @Override
    protected void finalTick(CrashReport report) {
        this.mc.crashed(report);
    }

    @Override
    public CrashReport addServerInfoToCrashReport(CrashReport report) {
        report = super.addServerInfoToCrashReport(report);
        report.getCategory().addCrashSectionCallable("Type", new Callable<String>(){

            @Override
            public String call() throws Exception {
                return "Integrated Server (map_client.txt)";
            }
        });
        report.getCategory().addCrashSectionCallable("Is Modded", new Callable<String>(){

            @Override
            public String call() throws Exception {
                String s2 = ClientBrandRetriever.getClientModName();
                if (!s2.equals("vanilla")) {
                    return "Definitely; Client brand changed to '" + s2 + "'";
                }
                s2 = IntegratedServer.this.getServerModName();
                return !s2.equals("vanilla") ? "Definitely; Server brand changed to '" + s2 + "'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.");
            }
        });
        return report;
    }

    @Override
    public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
        super.setDifficultyForAllWorlds(difficulty);
        if (this.mc.theWorld != null) {
            this.mc.theWorld.getWorldInfo().setDifficulty(difficulty);
        }
    }

    @Override
    public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
        super.addServerStatsToSnooper(playerSnooper);
        playerSnooper.addClientStat("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
    }

    @Override
    public boolean isSnooperEnabled() {
        return Minecraft.getMinecraft().isSnooperEnabled();
    }

    @Override
    public String shareToLAN(WorldSettings.GameType type, boolean allowCheats) {
        try {
            int i2 = -1;
            try {
                i2 = HttpUtil.getSuitableLanPort();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (i2 <= 0) {
                i2 = 25564;
            }
            this.getNetworkSystem().addLanEndpoint(null, i2);
            logger.info("Started on " + i2);
            this.isPublic = true;
            this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), String.valueOf(i2));
            this.lanServerPing.start();
            this.getConfigurationManager().setGameType(type);
            this.getConfigurationManager().setCommandsAllowedForAll(allowCheats);
            return String.valueOf(i2);
        }
        catch (IOException var6) {
            return null;
        }
    }

    @Override
    public void stopServer() {
        super.stopServer();
        if (this.lanServerPing != null) {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    @Override
    public void initiateShutdown() {
        if (!Reflector.MinecraftForge.exists() || this.isServerRunning()) {
            Futures.getUnchecked(this.addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    for (EntityPlayerMP entityplayermp : Lists.newArrayList(IntegratedServer.this.getConfigurationManager().getPlayerList())) {
                        IntegratedServer.this.getConfigurationManager().playerLoggedOut(entityplayermp);
                    }
                }
            }));
        }
        super.initiateShutdown();
        if (this.lanServerPing != null) {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    public void setStaticInstance() {
        this.setInstance();
    }

    public boolean getPublic() {
        return this.isPublic;
    }

    @Override
    public void setGameType(WorldSettings.GameType gameMode) {
        this.getConfigurationManager().setGameType(gameMode);
    }

    @Override
    public boolean isCommandBlockEnabled() {
        return true;
    }

    @Override
    public int getOpPermissionLevel() {
        return 4;
    }

    private void onTick() {
        for (WorldServer worldserver : Arrays.asList(this.worldServers)) {
            this.onTick(worldserver);
        }
    }

    public DifficultyInstance getDifficultyAsync(World p_getDifficultyAsync_1_, BlockPos p_getDifficultyAsync_2_) {
        this.difficultyUpdateWorld = p_getDifficultyAsync_1_;
        this.difficultyUpdatePos = p_getDifficultyAsync_2_;
        return this.difficultyLast;
    }

    private void onTick(WorldServer p_onTick_1_) {
        if (!Config.isTimeDefault()) {
            this.fixWorldTime(p_onTick_1_);
        }
        if (!Config.isWeatherEnabled()) {
            this.fixWorldWeather(p_onTick_1_);
        }
        if (Config.waterOpacityChanged) {
            Config.waterOpacityChanged = false;
            ClearWater.updateWaterOpacity(Config.getGameSettings(), p_onTick_1_);
        }
        if (this.difficultyUpdateWorld == p_onTick_1_ && this.difficultyUpdatePos != null) {
            this.difficultyLast = p_onTick_1_.getDifficultyForLocation(this.difficultyUpdatePos);
            this.difficultyUpdateWorld = null;
            this.difficultyUpdatePos = null;
        }
    }

    private void fixWorldWeather(WorldServer p_fixWorldWeather_1_) {
        WorldInfo worldinfo = p_fixWorldWeather_1_.getWorldInfo();
        if (worldinfo.isRaining() || worldinfo.isThundering()) {
            worldinfo.setRainTime(0);
            worldinfo.setRaining(false);
            p_fixWorldWeather_1_.setRainStrength(0.0f);
            worldinfo.setThunderTime(0);
            worldinfo.setThundering(false);
            p_fixWorldWeather_1_.setThunderStrength(0.0f);
            this.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(2, 0.0f));
            this.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(7, 0.0f));
            this.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(8, 0.0f));
        }
    }

    private void fixWorldTime(WorldServer p_fixWorldTime_1_) {
        WorldInfo worldinfo = p_fixWorldTime_1_.getWorldInfo();
        if (worldinfo.getGameType().getID() == 1) {
            long i2 = p_fixWorldTime_1_.getWorldTime();
            long j2 = i2 % 24000L;
            if (Config.isTimeDayOnly()) {
                if (j2 <= 1000L) {
                    p_fixWorldTime_1_.setWorldTime(i2 - j2 + 1001L);
                }
                if (j2 >= 11000L) {
                    p_fixWorldTime_1_.setWorldTime(i2 - j2 + 24001L);
                }
            }
            if (Config.isTimeNightOnly()) {
                if (j2 <= 14000L) {
                    p_fixWorldTime_1_.setWorldTime(i2 - j2 + 14001L);
                }
                if (j2 >= 22000L) {
                    p_fixWorldTime_1_.setWorldTime(i2 - j2 + 24000L + 14001L);
                }
            }
        }
    }
}

