/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.Inflater;
import net.labymod.api.events.PluginMessageEvent;
import net.labymod.api.events.ServerMessageEvent;
import net.labymod.core.LabyModCore;
import net.labymod.labyconnect.packets.PacketActionPlay;
import net.labymod.labyconnect.packets.PacketActionPlayResponse;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.support.util.Debug;
import net.labymod.user.FamiliarManager;
import net.labymod.user.User;
import net.labymod.user.cosmetic.CosmeticImageManager;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.util.CosmeticClassLoader;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.labymod.user.group.GroupManager;
import net.labymod.user.group.LabyGroup;
import net.labymod.user.gui.UserActionGui;
import net.labymod.user.util.FutureMap;
import net.labymod.user.util.UserResolvedCallback;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.PacketBuffer;

public class UserManager
implements ServerMessageEvent,
PluginMessageEvent {
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private Map<UUID, User> users = new HashMap<UUID, User>();
    private boolean whitelistLoaded = false;
    private boolean subTitlesModified = false;
    private List<Long> whitelistedUsers = new ArrayList<Long>();
    private Map<UUID, Boolean> checkedUsers = new HashMap<UUID, Boolean>();
    private Map<Integer, Class<?>> cosmeticIdToClassData = new HashMap();
    private JsonParser jsonParser = new JsonParser();
    private CosmeticImageManager cosmeticImageManager = new CosmeticImageManager(Source.getUserAgent());
    private FamiliarManager familiarManager = new FamiliarManager();
    private UserActionGui userActionGui = new UserActionGui(this);
    private CosmeticClassLoader cosmeticClassLoader = new CosmeticClassLoader();
    private GroupManager groupManager = new GroupManager(this.executorService);
    protected AtomicInteger currentRequestId = new AtomicInteger(Short.MIN_VALUE);
    protected FutureMap<Short, PacketActionPlayResponse> responseFutureMap = new FutureMap<Short, Object>(requestId -> {}, 1000L, null);
    protected boolean lastSpamProtectedLegState = false;

    public UserManager() {
        try {
            for (Class<?> loadedClassInfo : this.cosmeticClassLoader.getCosmeticClasses()) {
                CosmeticRenderer cosmeticRenderer = (CosmeticRenderer)loadedClassInfo.newInstance();
                Class<?>[] classArray = loadedClassInfo.getClasses();
                int n2 = classArray.length;
                int n3 = 0;
                while (n3 < n2) {
                    Class<?> subClasses = classArray[n3];
                    if (CosmeticData.class.isAssignableFrom(subClasses)) {
                        this.cosmeticIdToClassData.put(cosmeticRenderer.getCosmeticId(), subClasses);
                    }
                    ++n3;
                }
            }
            Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Registered " + this.cosmeticIdToClassData.size() + " cosmetics!");
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        LabyMod.getInstance().getEventManager().register(this);
        LabyMod.getInstance().getEventManager().register(this);
    }

    @Override
    public void onServerMessage(String messageKey, JsonElement serverMessage) {
        if (messageKey.equals("account_subtitle")) {
            this.subTitlesModified = true;
            try {
                JsonArray jsonArray = serverMessage.getAsJsonArray();
                int i2 = 0;
                while (i2 < jsonArray.size()) {
                    JsonObject accountEntry = jsonArray.get(i2).getAsJsonObject();
                    if (accountEntry.has("uuid")) {
                        double subTitleSize;
                        String subTitle;
                        UUID uuid = UUID.fromString(accountEntry.get("uuid").getAsString());
                        User user = this.getUser(uuid);
                        boolean prevHasSubTitle = user.getSubTitle() != null;
                        String string = subTitle = accountEntry.has("value") ? accountEntry.get("value").getAsString() : null;
                        if (subTitle != null) {
                            subTitle = ModColor.createColors(subTitle);
                        }
                        double d2 = subTitleSize = accountEntry.has("size") ? accountEntry.get("size").getAsDouble() : 0.5;
                        if (subTitleSize < 0.8) {
                            subTitleSize = 0.8;
                        }
                        if (subTitleSize > 1.6) {
                            subTitleSize = 1.6;
                        }
                        user.setSubTitle(subTitle);
                        user.setSubTitleSize(subTitleSize);
                        if (!prevHasSubTitle && subTitle != null) {
                            Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Added subtitle for " + uuid.toString() + ": " + subTitle);
                        } else if (prevHasSubTitle && subTitle == null) {
                            Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Removed subtitle of " + uuid.toString() + "!");
                        } else {
                            Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Updated subtitle of " + uuid.toString() + " to " + subTitle);
                        }
                    }
                    ++i2;
                }
            }
            catch (Exception error) {
                error.printStackTrace();
            }
        }
    }

    @Override
    public void receiveMessage(String channelName, PacketBuffer packetBuffer) {
        if (this.subTitlesModified && channelName.equals("MC|Brand")) {
            for (User user : this.users.values()) {
                user.setSubTitle(null);
            }
            this.subTitlesModified = false;
        }
    }

    public void init(final UUID clientUUID, final Consumer<Boolean> consumer) {
        this.loadWhitelist(new Consumer<Integer>(){

            @Override
            public void accept(Integer accepted) {
                Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Loaded " + accepted + " whitelisted users.");
                UserManager.this.setChecked(clientUUID, false);
                if (clientUUID != null && UserManager.this.containsInCSV(clientUUID)) {
                    UserManager.this.getUserDataOf(clientUUID, new Consumer<User>(){

                        @Override
                        public void accept(User user) {
                            UserManager.this.setChecked(clientUUID, !user.getCosmetics().isEmpty() || !user.getEmotes().isEmpty());
                            consumer.accept(true);
                        }
                    });
                } else {
                    consumer.accept(accepted != 0);
                }
            }
        });
    }

    public User getUser(UUID uuid) {
        User user = this.users.get(uuid);
        if (user == null) {
            user = new User(uuid);
            this.users.put(uuid, user);
        }
        return user;
    }

    public boolean isWhitelisted(final UUID uuid) {
        if (!this.whitelistLoaded) {
            return false;
        }
        Boolean result = this.checkedUsers.get(uuid);
        if (result == null) {
            this.checkedUsers.put(uuid, false);
            if (this.containsInCSV(uuid)) {
                this.getUserDataOf(uuid, new Consumer<User>(){

                    @Override
                    public void accept(User user) {
                        UserManager.this.setChecked(uuid, true);
                    }
                });
            }
            return false;
        }
        return result;
    }

    private boolean containsInCSV(UUID uuid) {
        long uuidPart = uuid.getMostSignificantBits() >> 32 & 0xFFFFFFFFL;
        return this.whitelistedUsers.contains(uuidPart);
    }

    private void getUserDataOf(final UUID uuid, final Consumer<User> callback) {
        try {
            this.executorService.execute(new Runnable(){

                @Override
                public void run() {
                    try {
                        final User user = UserManager.this.getUser(uuid);
                        UserManager.this.loadUserData(user, new UserResolvedCallback(){

                            @Override
                            public void resolvedGroup(LabyGroup group) {
                                user.setGroup(group);
                            }

                            @Override
                            public void resolvedCosmetics(Map<Integer, CosmeticData> cosmetics) {
                                user.setCosmetics(cosmetics);
                            }

                            @Override
                            public void resolvedDailyEmoteFlat(boolean value) {
                                user.setDailyEmoteFlat(value);
                            }

                            @Override
                            public void complete() {
                                if (callback != null) {
                                    callback.accept(user);
                                }
                            }
                        });
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            });
        }
        catch (Exception error) {
            Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Error while resolving user data of " + uuid.toString() + " (" + error.getMessage() + ")");
            error.printStackTrace();
        }
    }

    public void updateUsersJson(final UUID uuid, final String json, final Consumer<Boolean> callback) {
        this.executorService.execute(new Runnable(){

            @Override
            public void run() {
                block2: {
                    try {
                        final User user = UserManager.this.getUser(uuid);
                        UserManager.this.handleJsonString(user, json, new UserResolvedCallback(){

                            @Override
                            public void resolvedGroup(LabyGroup group) {
                                user.setGroup(group);
                            }

                            @Override
                            public void resolvedCosmetics(Map<Integer, CosmeticData> cosmetics) {
                                user.setCosmetics(cosmetics);
                            }

                            @Override
                            public void resolvedDailyEmoteFlat(boolean value) {
                                user.setDailyEmoteFlat(value);
                            }

                            @Override
                            public void complete() {
                                if (callback != null) {
                                    callback.accept(true);
                                }
                            }
                        });
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                        if (callback == null) break block2;
                        callback.accept(false);
                    }
                }
            }
        });
    }

    private void loadUserData(User user, UserResolvedCallback callback) throws Exception {
        HttpURLConnection connection = (HttpURLConnection)new URL(String.format("http://dl.labymod.net/userdata/%s.json", user.getUuid().toString())).openConnection();
        Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Load user data of " + user.getUuid().toString());
        connection.setRequestProperty("User-Agent", Source.getUserAgent());
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(2000);
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode / 100 == 2) {
            String jsonString = "";
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext()) {
                jsonString = String.valueOf(jsonString) + scanner.next();
            }
            scanner.close();
            this.handleJsonString(user, jsonString, callback);
        } else {
            Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Response code for " + user.getUuid().toString() + " is " + responseCode);
        }
        callback.complete();
    }

    private void handleJsonString(User user, String jsonString, UserResolvedCallback callback) throws Exception {
        boolean isClient = user.getUuid().equals(LabyMod.getInstance().getPlayerUUID());
        if (isClient) {
            Debug.log(Debug.EnumDebugMode.USER_MANAGER, jsonString);
        }
        JsonElement jsonElement = this.jsonParser.parse(jsonString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        try {
            JsonObject flatObject;
            JsonObject group;
            short id2;
            LabyGroup labyGroup;
            JsonArray groupArray;
            if (jsonObject.has("c")) {
                JsonArray jsonArray = jsonObject.get("c").getAsJsonArray();
                Iterator<JsonElement> cosmeticIterator = jsonArray.iterator();
                HashMap<Integer, CosmeticData> storedCosmeticData = new HashMap<Integer, CosmeticData>();
                while (cosmeticIterator.hasNext()) {
                    JsonObject cosmeticJsonObject = cosmeticIterator.next().getAsJsonObject();
                    if (!cosmeticJsonObject.has("i")) {
                        Debug.log(Debug.EnumDebugMode.USER_MANAGER, String.valueOf(cosmeticJsonObject.toString()) + " has no id");
                        continue;
                    }
                    int id3 = cosmeticJsonObject.get("i").getAsInt();
                    if (!cosmeticJsonObject.has("d")) {
                        Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Cosmetic id " + id3 + " has no data (" + cosmeticJsonObject.toString() + ")");
                        continue;
                    }
                    JsonArray dataArray = cosmeticJsonObject.get("d").getAsJsonArray();
                    Iterator<JsonElement> dataIterator = dataArray.iterator();
                    ArrayList<String> dataList = new ArrayList<String>();
                    while (dataIterator.hasNext()) {
                        JsonElement dataElement = dataIterator.next();
                        if (dataElement.isJsonNull()) continue;
                        String dataString = dataElement.getAsString();
                        dataList.add(dataString);
                    }
                    Class<?> dataClass = this.cosmeticIdToClassData.get(id3);
                    if (dataClass == null) {
                        Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Cosmetic id " + id3 + " not found in cosmeticIdToClassData (size=" + this.cosmeticIdToClassData.size() + ")");
                        continue;
                    }
                    CosmeticData cosmeticData = (CosmeticData)dataClass.newInstance();
                    storedCosmeticData.put(id3, cosmeticData);
                    try {
                        cosmeticData.init(user);
                        String[] array = dataList.toArray(new String[dataList.size()]);
                        if (array.length != 0) {
                            cosmeticData.loadData(array);
                        }
                        if (isClient) {
                            Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Loaded cosmetic " + id3 + " for client");
                        }
                    }
                    catch (Exception error) {
                        Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Parse error while loading " + dataClass.getSimpleName() + ": " + error.getMessage());
                    }
                    if (id3 == 0) {
                        user.getCloakContainer().resolved();
                    }
                    if (id3 == 22) {
                        user.getBandanaContainer().resolved();
                    }
                    if (id3 == 27) {
                        user.getShoesContainer().resolved();
                    }
                    if (id3 == 34) {
                        user.getKawaiiMaskContainer().resolved();
                    }
                    if (id3 == 31) {
                        user.getCoverMaskContainer().resolved();
                    }
                    if (id3 == 33) {
                        user.getWatchContainer().resolved();
                    }
                    if (id3 != 24) continue;
                    user.getAngelWingsContainer().resolved();
                }
                callback.resolvedCosmetics(storedCosmeticData);
                for (CosmeticData data : storedCosmeticData.values()) {
                    data.completed(user);
                }
            }
            if (jsonObject.has("e")) {
                JsonArray emoteArray = jsonObject.get("e").getAsJsonArray();
                Iterator<JsonElement> emoteIterator = emoteArray.iterator();
                ArrayList<Short> emotes = new ArrayList<Short>();
                while (emoteIterator.hasNext()) {
                    JsonElement element = emoteIterator.next();
                    short emoteId = element.getAsShort();
                    emotes.add(emoteId);
                }
                user.setEmotes(emotes);
            }
            if (jsonObject.has("st") || jsonObject.has("s")) {
                JsonObject stickerObject = (jsonObject.has("s") ? jsonObject.get("s") : jsonObject.get("st")).getAsJsonObject();
                Iterator<JsonElement> packsArray = stickerObject.get("p").getAsJsonArray().iterator();
                ArrayList<Short> packs = new ArrayList<Short>();
                while (packsArray.hasNext()) {
                    short packId = packsArray.next().getAsShort();
                    packs.add(packId);
                }
                user.setStickerPacks(packs);
            }
            if (jsonObject.has("g") && (groupArray = jsonObject.get("g").getAsJsonArray()).size() > 0 && (labyGroup = this.groupManager.getGroupById(id2 = (group = groupArray.get(0).getAsJsonObject()).get("i").getAsShort())) != null) {
                callback.resolvedGroup(labyGroup);
            }
            if (jsonObject.has("f") && (flatObject = jsonObject.get("f").getAsJsonObject()).has("e")) {
                callback.resolvedDailyEmoteFlat(flatObject.get("e").getAsBoolean());
            }
        }
        catch (Exception error2) {
            error2.printStackTrace();
        }
        callback.complete();
    }

    public void loadWhitelist(final Consumer<Integer> callback) {
        this.executorService.execute(new Runnable(){

            @Override
            public void run() {
                Debug.log(Debug.EnumDebugMode.USER_MANAGER, "Load whitelist..");
                try {
                    HttpURLConnection connection = (HttpURLConnection)new URL("http://dl.labymod.net/whitelist.bin").openConnection();
                    connection.setRequestProperty("User-Agent", Source.getUserAgent());
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(2000);
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode / 100 == 2) {
                        int size;
                        InputStream is2 = connection.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[4096];
                        while ((size = is2.read(buffer)) != -1) {
                            baos.write(buffer, 0, size);
                        }
                        is2.close();
                        baos.close();
                        byte[] compressedBytes = baos.toByteArray();
                        Inflater inflater = new Inflater();
                        inflater.setInput(compressedBytes);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedBytes.length);
                        byte[] buffer2 = new byte[1024];
                        while (!inflater.finished()) {
                            int count = inflater.inflate(buffer2);
                            outputStream.write(buffer2, 0, count);
                        }
                        outputStream.close();
                        byte[] decompressedBytes = outputStream.toByteArray();
                        int b2 = 0;
                        while (b2 < decompressedBytes.length) {
                            long uuidPart = 0L;
                            int i2 = 0;
                            while (i2 < 8) {
                                uuidPart += ((long)decompressedBytes[b2 + i2] & 0xFFL) << 8 * i2;
                                ++i2;
                            }
                            UserManager.this.whitelistedUsers.add(uuidPart);
                            b2 += 8;
                        }
                    }
                    UserManager.this.whitelistLoaded = true;
                    callback.accept(UserManager.this.whitelistedUsers.size());
                }
                catch (Exception error) {
                    error.printStackTrace();
                }
            }
        });
    }

    public void setChecked(UUID uuid, boolean value) {
        this.checkedUsers.put(uuid, value);
    }

    public void removeCheckedUser(UUID uuid) {
        this.checkedUsers.remove(uuid);
    }

    public void clearCache() {
        this.whitelistLoaded = false;
        this.users.clear();
        this.whitelistedUsers.clear();
        this.checkedUsers.clear();
        this.cosmeticImageManager.unloadUnusedTextures(true, true);
    }

    public void broadcastBitUpdate(boolean bit2) {
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        int i2 = 0;
        for (EnumPlayerModelParts enumplayermodelparts : gameSettings.getModelParts()) {
            i2 |= enumplayermodelparts.getPartMask();
        }
        boolean spamProtection = LabyMod.getInstance().isServerHasEmoteSpamProtection();
        if (bit2) {
            if (spamProtection && this.lastSpamProtectedLegState) {
                i2 ^= 0x10;
            }
            this.sendBitMask(i2 ^ 0x90);
            if (spamProtection) {
                this.lastSpamProtectedLegState = !this.lastSpamProtectedLegState;
            } else {
                this.sendBitMask(i2 ^ 0x80);
            }
        } else if (!spamProtection) {
            this.sendBitMask(i2);
        }
    }

    private void sendBitMask(int bitMask) {
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        LabyModCore.getMinecraft().sendClientSettings(gameSettings.language, gameSettings.renderDistanceChunks, gameSettings.chatVisibility, gameSettings.chatColours, bitMask);
    }

    public void requestAction(short id2, byte[] bytes, FutureCallback<PacketActionPlayResponse> callback) {
        int requestId = 0;
        requestId = this.currentRequestId.incrementAndGet();
        if (requestId > Short.MAX_VALUE) {
            this.currentRequestId.set(Short.MIN_VALUE);
            requestId = Short.MIN_VALUE;
        }
        Futures.addCallback(this.responseFutureMap.get((short)requestId), callback);
        LabyMod.getInstance().getLabyConnect().getClientConnection().sendPacket(new PacketActionPlay((short)requestId, id2, bytes));
    }

    public void resolveAction(short requestId, PacketActionPlayResponse packetActionPlayResponse) {
        this.responseFutureMap.resolve(requestId, packetActionPlayResponse);
    }

    public Map<UUID, User> getUsers() {
        return this.users;
    }

    public CosmeticImageManager getCosmeticImageManager() {
        return this.cosmeticImageManager;
    }

    public FamiliarManager getFamiliarManager() {
        return this.familiarManager;
    }

    public UserActionGui getUserActionGui() {
        return this.userActionGui;
    }

    public CosmeticClassLoader getCosmeticClassLoader() {
        return this.cosmeticClassLoader;
    }

    public GroupManager getGroupManager() {
        return this.groupManager;
    }
}

