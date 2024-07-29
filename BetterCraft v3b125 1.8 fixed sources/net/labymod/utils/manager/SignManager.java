/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.manager;

import java.util.HashMap;
import java.util.Map;
import net.labymod.core.BlockPosition;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.IChatComponent;

public class SignManager {
    private static SignSearchSettings signSearchSettings = new SignSearchSettings();
    private static Map<BlockPosition, SignData> signDataMap = new HashMap<BlockPosition, SignData>();

    public static void render(TileEntitySign tileEntitySign) {
        if (!LabyMod.getSettings().signSearch || !signSearchSettings.isEnabled()) {
            if (!signDataMap.isEmpty()) {
                signDataMap.clear();
            }
            return;
        }
        BlockPosition blockPosition = LabyModCore.getMinecraft().getPosition(tileEntitySign.getPos());
        SignData signData = signDataMap.get(blockPosition);
        if (signData == null || signData.getLastSignUpdated() + 500L < System.currentTimeMillis()) {
            signData = new SignData(tileEntitySign);
            signDataMap.put(blockPosition, signData);
        }
        signData.getSignColor().applyColor();
    }

    public static SignSearchSettings getSignSearchSettings() {
        return signSearchSettings;
    }

    public static enum SignColor {
        NONE(1.0f, 1.0f, 1.0f, 1.0f),
        GREEN(0.6f, 23.6f, 0.6f, 0.6f),
        RED(23.6f, 0.6f, 0.6f, 0.6f),
        ORANGE(10.0f, 10.0f, 0.6f, 0.6f),
        GRAY(0.6f, 0.6f, 0.6f, 0.6f);

        private float red;
        private float green;
        private float blue;
        private float alpha;

        public void applyColor() {
            GlStateManager.color(this.red, this.green, this.blue, this.alpha);
        }

        private SignColor(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public float getRed() {
            return this.red;
        }

        public float getGreen() {
            return this.green;
        }

        public float getBlue() {
            return this.blue;
        }

        public float getAlpha() {
            return this.alpha;
        }
    }

    public static class SignData {
        private TileEntitySign tileEntitySign;
        private SignColor signColor = SignColor.NONE;
        private long lastSignUpdated;

        public SignData(TileEntitySign sign) {
            this.tileEntitySign = sign;
            this.lastSignUpdated = System.currentTimeMillis();
            this.parseSignData();
        }

        private void parseSignData() {
            boolean searchFound;
            String fullString = "";
            String[] lines = new String[4];
            int lineCount = -1;
            IChatComponent[] iChatComponentArray = this.tileEntitySign.signText;
            int n2 = this.tileEntitySign.signText.length;
            int n3 = 0;
            while (n3 < n2) {
                String line;
                IChatComponent chatComponentObj = iChatComponentArray[n3];
                ++lineCount;
                if (chatComponentObj != null && (line = LabyModCore.getMinecraft().getChatComponent(chatComponentObj).getUnformattedText()) != null) {
                    fullString = String.valueOf(fullString) + line;
                    lines[lineCount] = line;
                }
                ++n3;
            }
            if (fullString.isEmpty()) {
                return;
            }
            SignSearchSettings settings = signSearchSettings;
            fullString = fullString.toLowerCase();
            String searchString = settings.getSearchString().toLowerCase();
            boolean bl2 = searchFound = searchString.isEmpty() || fullString.contains(searchString);
            if (!searchFound && searchString.contains(",")) {
                String[] stringArray = searchString.split("\\,");
                int n4 = stringArray.length;
                int line = 0;
                while (line < n4) {
                    String word = stringArray[line];
                    if (fullString.contains(word)) {
                        searchFound = true;
                    }
                    ++line;
                }
            }
            if (settings.isUseAdvancedOptions()) {
                boolean blacklistFound = !settings.getBlacklistString().isEmpty() && fullString.contains(settings.getBlacklistString().toLowerCase());
                Integer currentUserCount = this.getUserCount(lines, true);
                Integer maxUserCount = this.getUserCount(lines, false);
                if (searchFound && !blacklistFound) {
                    boolean isFull;
                    boolean isEmpty = currentUserCount != null && currentUserCount == 0 && settings.isFilterEmptyServer();
                    boolean bl3 = isFull = maxUserCount != null && currentUserCount != null && currentUserCount >= maxUserCount && settings.isFilterFullServer();
                    this.signColor = !isEmpty && !isFull ? SignColor.GREEN : (isEmpty ? SignColor.GRAY : (isFull ? SignColor.ORANGE : SignColor.RED));
                } else {
                    this.signColor = SignColor.RED;
                }
            } else {
                this.signColor = searchFound ? SignColor.GREEN : SignColor.RED;
            }
        }

        private Integer getUserCount(String[] lines, boolean pre) {
            String[] stringArray = lines;
            int n2 = lines.length;
            int n3 = 0;
            while (n3 < n2) {
                String line = stringArray[n3];
                if (line != null && line.contains("/")) {
                    String[] stringArray2 = line.split("/");
                }
                ++n3;
            }
            return null;
        }

        public TileEntitySign getTileEntitySign() {
            return this.tileEntitySign;
        }

        public SignColor getSignColor() {
            return this.signColor;
        }

        public long getLastSignUpdated() {
            return this.lastSignUpdated;
        }

        public void setTileEntitySign(TileEntitySign tileEntitySign) {
            this.tileEntitySign = tileEntitySign;
        }

        public void setSignColor(SignColor signColor) {
            this.signColor = signColor;
        }

        public void setLastSignUpdated(long lastSignUpdated) {
            this.lastSignUpdated = lastSignUpdated;
        }
    }

    public static class SignSearchSettings {
        private String searchString = "";
        private String blacklistString = "";
        private boolean useAdvancedOptions = false;
        private boolean filterFullServer = false;
        private boolean filterEmptyServer = false;
        private boolean enabled;

        public void update() {
            this.enabled = this.useAdvancedOptions && (this.filterFullServer || this.filterEmptyServer || !this.blacklistString.isEmpty()) || !this.searchString.isEmpty();
        }

        public String getSearchString() {
            return this.searchString;
        }

        public String getBlacklistString() {
            return this.blacklistString;
        }

        public boolean isUseAdvancedOptions() {
            return this.useAdvancedOptions;
        }

        public boolean isFilterFullServer() {
            return this.filterFullServer;
        }

        public boolean isFilterEmptyServer() {
            return this.filterEmptyServer;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setSearchString(String searchString) {
            this.searchString = searchString;
        }

        public void setBlacklistString(String blacklistString) {
            this.blacklistString = blacklistString;
        }

        public void setUseAdvancedOptions(boolean useAdvancedOptions) {
            this.useAdvancedOptions = useAdvancedOptions;
        }

        public void setFilterFullServer(boolean filterFullServer) {
            this.filterFullServer = filterFullServer;
        }

        public void setFilterEmptyServer(boolean filterEmptyServer) {
            this.filterEmptyServer = filterEmptyServer;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}

