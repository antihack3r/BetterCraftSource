/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import net.labymod.core.LabyModCore;
import net.labymod.core.ServerPingerData;
import net.labymod.gui.ModGuiMultiplayer;
import net.labymod.gui.elements.CheckBox;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.gui.elements.Tabs;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.Source;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ServerData;
import net.labymod.utils.manager.ServerInfoRenderer;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.multiplayer.ServerList;

public class GuiServerList
extends GuiScreen {
    private static Map<String, ServerInfoRenderer> serverInfoRenderers = new HashMap<String, ServerInfoRenderer>();
    private static List<ServerInfoRenderer> serverInfoRenderersSorted = new ArrayList<ServerInfoRenderer>();
    private static List<ServerData> publicServerListEntrys = new ArrayList<ServerData>();
    private static boolean initialized;
    private static boolean partnersOnly;
    private GuiScreen parentScreen;
    private Scrollbar scrollbar;
    private long lastServerDataUpdate;
    private ServerPingerData joinOnServerData;
    private ServerPingerData saveServerData;
    private ServerPingerData hoverServerData;
    private ServerPingerData selectedServerData;
    private int selectedServerId;
    private long lastTimeSelected;
    private GuiButton buttonConnect;
    private GuiButton buttonSaveServer;
    private CheckBox checkBox;
    private int alternativeDragClickY = -1;

    public GuiServerList(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        if (initialized) {
            return;
        }
        initialized = true;
        new Thread(new Runnable(){

            @Override
            public void run() {
                JsonParser jsonParser = new JsonParser();
                try {
                    HttpURLConnection connection = (HttpURLConnection)new URL("http://dl.labymod.net/public_servers.json").openConnection();
                    connection.setRequestProperty("User-Agent", Source.getUserAgent());
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(2000);
                    connection.connect();
                    int index = 0;
                    int responseCode = connection.getResponseCode();
                    if (responseCode / 100 == 2) {
                        String jsonString = "";
                        Scanner scanner = new Scanner(connection.getInputStream());
                        while (scanner.hasNext()) {
                            jsonString = String.valueOf(jsonString) + scanner.next();
                        }
                        scanner.close();
                        JsonElement jsonElement = jsonParser.parse(jsonString);
                        JsonObject servers = jsonElement.getAsJsonObject().get("servers").getAsJsonObject();
                        Set<Map.Entry<String, JsonElement>> entrySet = servers.entrySet();
                        for (Map.Entry<String, JsonElement> entry : entrySet) {
                            String address = entry.getKey();
                            JsonObject data = entry.getValue().getAsJsonObject();
                            boolean partner = data.has("partner") && data.get("partner").getAsBoolean();
                            ServerData serverData = new ServerData(address, 25565, partner);
                            serverData.setIndex(index);
                            publicServerListEntrys.add(serverData);
                            ++index;
                        }
                    }
                }
                catch (Exception error) {
                    error.printStackTrace();
                }
                GuiServerList.this.refreshServerList();
                GuiServerList.this.sortServerList();
            }
        }).start();
    }

    @Override
    public void initGui() {
        Tabs.initMultiplayerTabs(1);
        this.scrollbar = new Scrollbar(36);
        this.scrollbar.setPosition(width / 2 + 150 + 4, 41, width / 2 + 150 + 4 + 6, height - 40);
        this.scrollbar.setSpeed(20);
        this.scrollbar.setSpaceBelow(5);
        Tabs.initGuiScreen(this.buttonList, this);
        this.refreshServerList();
        this.sortServerList();
        this.lastServerDataUpdate = System.currentTimeMillis() + 5000L;
        this.buttonSaveServer = new GuiButton(6, width / 2 - 50 - 5 - 100, height - 30, 100, 20, LanguageManager.translate("button_save_server"));
        this.buttonList.add(this.buttonSaveServer);
        this.buttonConnect = new GuiButton(5, width / 2 - 50, height - 30, 100, 20, LanguageManager.translate("button_connect"));
        this.buttonList.add(this.buttonConnect);
        GuiButton cancelButton = new GuiButton(4, width / 2 + 50 + 5, height - 30, 100, 20, LanguageManager.translate("button_cancel"));
        if (LabyMod.getInstance().isInGame()) {
            cancelButton.enabled = false;
        }
        this.buttonList.add(cancelButton);
        this.checkBox = new CheckBox("", partnersOnly ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED, null, width / 2 - 180, height - 28, 19, 19);
        super.initGui();
    }

    private void refreshServerList() {
        boolean firstInit = serverInfoRenderers.isEmpty();
        for (final ServerData entry : publicServerListEntrys) {
            if (firstInit) {
                ServerPingerData dummyPingerData = new ServerPingerData(entry.getIp(), 0L);
                dummyPingerData.setPingToServer(-1L);
                dummyPingerData.setMotd(LanguageManager.translate("status_pinging_server"));
                dummyPingerData.setPinging(true);
                dummyPingerData.setVersion(Source.ABOUT_MC_PROTOCOL_VERSION);
                ServerInfoRenderer serverInfoRenderer = new ServerInfoRenderer(entry.getIp(), entry.getIp(), dummyPingerData).setIndex(entry.getIndex());
                serverInfoRenderer.setLabymodServerData(entry);
                serverInfoRenderers.put(entry.getIp(), serverInfoRenderer);
            }
            final long pingStartTime = System.currentTimeMillis();
            LabyModCore.getServerPinger().pingServer(null, pingStartTime, String.valueOf(entry.getIp()) + ":" + entry.getPort(), new Consumer<ServerPingerData>(){

                @Override
                public void accept(ServerPingerData accepted) {
                    if (accepted != null && accepted.getTimePinged() != pingStartTime) {
                        return;
                    }
                    ServerInfoRenderer preInfo = (ServerInfoRenderer)serverInfoRenderers.get(entry.getIp());
                    if (preInfo != null) {
                        if (accepted == null) {
                            if (!preInfo.canReachServer()) {
                                preInfo.setHidden(true);
                            }
                        } else {
                            preInfo.init(entry.getIp(), entry.getIp(), accepted);
                            preInfo.setHidden(false);
                        }
                    }
                }
            });
        }
        if (firstInit) {
            this.sortServerList();
        }
    }

    private void sortServerList() {
        ArrayList<ServerInfoRenderer> list = new ArrayList<ServerInfoRenderer>();
        int count = 0;
        String versionSplit = Source.ABOUT_MC_VERSION.replaceFirst("\\.", "/");
        String majorVersion = (versionSplit.contains(".") ? versionSplit.split("\\.")[0] : versionSplit).replaceAll("/", ".");
        for (ServerInfoRenderer serverInfoRenderer : serverInfoRenderers.values()) {
            boolean partner;
            if ((serverInfoRenderer.isClientOutOfDate() || serverInfoRenderer.isServerOutOfDate()) && !serverInfoRenderer.getServerData().getMotd().contains(majorVersion) && !serverInfoRenderer.getServerData().getGameVersion().contains(majorVersion) || serverInfoRenderer.getServerData() != null && serverInfoRenderer.canReachServer() && !serverInfoRenderer.getServerData().isPinging() && serverInfoRenderer.getServerData().getCurrentPlayers() < 30 || serverInfoRenderer.isHidden()) continue;
            boolean bl2 = partner = serverInfoRenderer.getLabymodServerData() != null && serverInfoRenderer.getLabymodServerData().isPartner();
            if (!partner && partnersOnly) continue;
            list.add(serverInfoRenderer);
            if (++count >= 50) break;
        }
        Collections.sort(list, new Comparator<ServerInfoRenderer>(){

            @Override
            public int compare(ServerInfoRenderer a2, ServerInfoRenderer b2) {
                return a2.getIndex() - b2.getIndex();
            }
        });
        serverInfoRenderersSorted = list;
        this.scrollbar.update(serverInfoRenderersSorted.size());
    }

    @Override
    public void updateScreen() {
        if (LabyMod.getSettings().serverlistLiveView && this.lastServerDataUpdate < System.currentTimeMillis()) {
            this.lastServerDataUpdate = System.currentTimeMillis() + 15000L;
            this.sortServerList();
            this.refreshServerList();
        }
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        draw.drawAutoDimmedBackground(this.scrollbar.getScrollY());
        this.joinOnServerData = null;
        this.saveServerData = null;
        this.hoverServerData = null;
        this.selectedServerId = -1;
        int id2 = 1;
        int midX = width / 2;
        int entryWidth = 300;
        int entryHeight = 36;
        double posY = 45.0 + this.scrollbar.getScrollY();
        for (ServerInfoRenderer serverInfoRenderer : serverInfoRenderersSorted) {
            boolean partner;
            boolean bl2 = partner = serverInfoRenderer.getLabymodServerData() != null && serverInfoRenderer.getLabymodServerData().isPartner();
            if (this.selectedServerData != null && this.selectedServerData.getIpAddress().equals(serverInfoRenderer.getServerData().getIpAddress())) {
                DrawUtils.drawRect((double)(midX - 150 - 2), posY - 2.0, (double)(midX + 150 + 2), posY + 36.0 - 2.0, ModColor.toRGB(partner ? 155 : 128, partner ? 155 : 128, partner ? 0 : 128, 255));
                DrawUtils.drawRect((double)(midX - 150 - 1), posY - 1.0, (double)(midX + 150 + 1), posY + 36.0 - 3.0, ModColor.toRGB(0, 0, 0, 255));
                this.selectedServerId = id2;
            } else if (partner) {
                int color = ModColor.toRGB(100, 80, 0, 30);
                int x2 = midX - 150 - 1;
                int y2 = (int)posY - 1;
                int x22 = midX + 150 + 2;
                int y22 = (int)posY + 36 - 3;
                DrawUtils.drawRect(x2 + 1, y2 + 1, x22 - 1, y22 - 1, color);
                DrawUtils.drawRect(x2, y2 + 2, x2 + 1, y22 - 2, color);
                DrawUtils.drawRect(x22 - 1, y2 + 2, x22, y22 - 2, color);
                DrawUtils.drawRect(x2 + 2, y2, x22 - 2, y2 + 1, color);
                DrawUtils.drawRect(x2 + 2, y22 - 1, x22 - 2, y22, color);
            }
            if (mouseY > 41 && mouseY < height - 40) {
                if (serverInfoRenderer.drawJoinServerButton(midX - 150, (int)posY, 305, 36, mouseX, mouseY)) {
                    this.joinOnServerData = serverInfoRenderer.getServerData();
                }
                if (serverInfoRenderer.drawSaveServerButton(midX - 150, (int)posY, 305, 36, mouseX, mouseY)) {
                    this.saveServerData = serverInfoRenderer.getServerData();
                    TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 300L, LanguageManager.translate("button_save_in_my_server_list"));
                }
                if (mouseX > midX - 150 && mouseX < midX + 150 + 5 && (double)mouseY > posY && (double)mouseY < posY + 36.0) {
                    this.hoverServerData = serverInfoRenderer.getServerData();
                }
            }
            String number = "#" + id2;
            if (partner) {
                double x3 = midX - 150 - draw.getStringWidth(number) - 12;
                boolean hover = (double)mouseX > x3 && (double)mouseX < x3 + 10.0 && (double)mouseY > posY && (double)mouseY < posY + 10.0;
                draw.bindTexture(ModTextures.MISC_PARTNER_CROWN);
                draw.drawTexture(x3 - (double)(hover ? 2 : 0), posY - (double)(hover ? 2 : 0), 255.0, 255.0, 10 + (hover ? 4 : 0), 10 + (hover ? 4 : 0), 1.1f);
                if (hover) {
                    TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, String.valueOf(ModColor.cl('e')) + LanguageManager.translate("partner_server_list"));
                }
                number = String.valueOf(ModColor.cl('e')) + number;
            }
            draw.drawRightString(number, midX - 150 - 3, posY + 2.0, 0.7);
            posY += 36.0;
            ++id2;
        }
        this.buttonConnect.enabled = this.selectedServerData != null;
        this.buttonSaveServer.enabled = this.selectedServerData != null;
        draw.drawOverlayBackground(0, 41);
        draw.drawGradientShadowTop(41.0, 0.0, width);
        draw.drawOverlayBackground(height - 40, height);
        draw.drawGradientShadowBottom(height - 40, 0.0, width);
        if (serverInfoRenderersSorted.isEmpty()) {
            draw.drawCenteredString(LanguageManager.translate("status_loading_server_list"), width / 2, height / 2);
        }
        int qiX = width - 12;
        int qiY = 44;
        int qiW = 8;
        int qiH = 12;
        boolean hover2 = mouseX > qiX && mouseY > 44 && mouseX < qiX + 8 && mouseY < 56;
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_QUESTION);
        draw.drawTexture(qiX, 44.0, hover2 ? 135.0 : 0.0, 0.0, 122.0, 255.0, 8.0, 12.0);
        if (hover2) {
            String string = String.valueOf(ModColor.cl("9")) + ModColor.cl("n") + LanguageManager.translate("information") + "\n" + ModColor.cl("r") + LanguageManager.translate("info_public_server_list");
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, (String[])draw.listFormattedStringToWidth(string, width / 4).toArray());
        }
        this.scrollbar.draw();
        super.drawScreen(mouseX, mouseY, partialTicks);
        boolean isScrolled = !LabyMod.getInstance().isInGame() && this.scrollbar.getScrollY() == 0.0;
        boolean isIndex0Selected = !LabyMod.getInstance().isInGame() && this.selectedServerId == 1;
        Tabs.drawMultiplayerTabs(1, mouseX, mouseY, isScrolled, isIndex0Selected);
        Tabs.drawParty(mouseX, mouseY, width);
        draw.bindTexture(ModTextures.MISC_PARTNER_CROWN);
        draw.drawTexture(width / 2 - 176, height - 36, 255.0, 255.0, 10.0, 10.0, 1.1f);
        this.checkBox.drawCheckbox(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Tabs.mouseClickedMultiplayerTabs(1, mouseX, mouseY);
        if (this.hoverServerData != null) {
            if (this.selectedServerData != null && this.selectedServerData.getIpAddress().equals(this.hoverServerData.getIpAddress()) && this.lastTimeSelected + 400L > System.currentTimeMillis()) {
                this.joinOnServerData = this.selectedServerData;
            }
            this.selectedServerData = this.hoverServerData;
            this.lastTimeSelected = System.currentTimeMillis();
            this.alternativeDragClickY = (int)(this.scrollbar.getScrollY() - (double)mouseY);
        }
        if (this.joinOnServerData != null) {
            this.joinServer();
        }
        if (this.saveServerData != null) {
            this.saveServer(this.selectedServerData);
        }
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
        if (this.checkBox != null && this.checkBox.mouseClicked(mouseX, mouseY, mouseButton)) {
            partnersOnly = this.checkBox.getValue() == CheckBox.EnumCheckBoxValue.ENABLED;
            this.sortServerList();
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void joinServer() {
        final ServerPingerData serverPingerData = this.selectedServerData;
        if (serverPingerData != null) {
            if (LabyMod.getInstance().isInGame() && !Minecraft.getMinecraft().isSingleplayer() && LabyMod.getSettings().confirmDisconnect) {
                final GuiScreen lastScreen = Minecraft.getMinecraft().currentScreen;
                Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(new GuiYesNoCallback(){

                    @Override
                    public void confirmClicked(boolean result, int id2) {
                        if (result) {
                            LabyMod.getInstance().getLabyConnect().setViaServerList(true);
                            LabyMod.getInstance().connectToServer(serverPingerData.getIpAddress());
                        } else {
                            Minecraft.getMinecraft().displayGuiScreen(lastScreen);
                        }
                    }
                }, LanguageManager.translate("warning_server_disconnect"), String.valueOf(ModColor.cl("c")) + serverPingerData.getIpAddress(), 0));
            } else {
                LabyMod.getInstance().getLabyConnect().setViaServerList(true);
                LabyMod.getInstance().connectToServer(serverPingerData.getIpAddress());
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
        if (this.alternativeDragClickY != -1) {
            this.scrollbar.setScrollY(this.alternativeDragClickY + mouseY);
            this.scrollbar.checkOutOfBorders();
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
        this.alternativeDragClickY = -1;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 4) {
            if (this.parentScreen instanceof ModGuiMultiplayer) {
                Minecraft.getMinecraft().displayGuiScreen(((ModGuiMultiplayer)this.parentScreen).getParentScreen());
            } else {
                Minecraft.getMinecraft().displayGuiScreen(this.parentScreen);
            }
        }
        if (button.id == 6) {
            this.saveServer(this.selectedServerData);
        }
        if (button.id == 5 && this.selectedServerData != null) {
            this.joinServer();
        }
        Tabs.actionPerformedButton(button);
    }

    private void saveServer(ServerPingerData selectedServerData) {
        if (this.parentScreen instanceof ModGuiMultiplayer) {
            ModGuiMultiplayer gmp = (ModGuiMultiplayer)this.parentScreen;
            gmp.getServerList().addServerData(selectedServerData.toMCServerData());
            gmp.getServerList().saveServerList();
            LabyModCore.getMinecraft().updateServerList(gmp.getServerSelector(), gmp.getServerList());
            gmp.getServerSelector().scrollBy(Integer.MAX_VALUE);
            gmp.getServerSelector().setSelectedSlotIndex(gmp.getServerList().countServers() - 1);
            Minecraft.getMinecraft().displayGuiScreen(this.parentScreen);
        } else {
            ServerList serverList = new ServerList(Minecraft.getMinecraft());
            serverList.loadServerList();
            serverList.addServerData(selectedServerData.toMCServerData());
            serverList.saveServerList();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        this.scrollbar.mouseInput();
        super.handleMouseInput();
    }

    public GuiScreen getParentScreen() {
        return this.parentScreen;
    }
}

