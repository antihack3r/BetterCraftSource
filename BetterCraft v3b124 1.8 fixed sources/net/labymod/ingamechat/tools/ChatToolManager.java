/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tools;

import java.io.File;
import java.util.List;
import net.labymod.ingamechat.tools.autotext.AutoTextKeyBinds;
import net.labymod.ingamechat.tools.autotext.AutoTextListener;
import net.labymod.ingamechat.tools.filter.Filters;
import net.labymod.ingamechat.tools.playermenu.PlayerMenu;
import net.labymod.ingamechat.tools.shortcuts.Shortcuts;
import net.labymod.main.LabyMod;
import net.labymod.utils.manager.ConfigManager;
import net.lenni0451.eventapi.manager.EventManager;

public class ChatToolManager {
    private final File fileAutoText = new File("LabyMod/", "autotext.json");
    private final File fileFilters = new File("LabyMod/", "filters.json");
    private final File fileShortcuts = new File("LabyMod/", "shortcuts.json");
    private final File filePlayerMenu = new File("LabyMod/", "playermenu.json");
    private ConfigManager<AutoTextKeyBinds> configAutoText;
    private ConfigManager<Filters> configFilters;
    private ConfigManager<Shortcuts> configShortcuts;
    private ConfigManager<PlayerMenu> configPlayerMenu;

    public void initTools() {
        this.configAutoText = new ConfigManager<AutoTextKeyBinds>(this.fileAutoText, AutoTextKeyBinds.class);
        this.configFilters = new ConfigManager<Filters>(this.fileFilters, Filters.class);
        this.configShortcuts = new ConfigManager<Shortcuts>(this.fileShortcuts, Shortcuts.class);
        this.configPlayerMenu = new ConfigManager<PlayerMenu>(this.filePlayerMenu, PlayerMenu.class);
        EventManager.register(new AutoTextListener());
        LabyMod.getInstance().getIngameChatManager().updateRooms();
    }

    public void saveTools() {
        this.configAutoText.save();
        this.configFilters.save();
        this.configShortcuts.save();
        this.configPlayerMenu.save();
        LabyMod.getInstance().getIngameChatManager().updateRooms();
    }

    public List<AutoTextKeyBinds.AutoText> getAutoTextKeyBinds() {
        return this.configAutoText.getSettings().getAutoTextKeyBinds();
    }

    public List<Filters.Filter> getFilters() {
        return this.configFilters.getSettings().getFilters();
    }

    public List<Shortcuts.Shortcut> getShortcuts() {
        return this.configShortcuts.getSettings().getShortcuts();
    }

    public List<PlayerMenu.PlayerMenuEntry> getPlayerMenu() {
        return this.configPlayerMenu.getSettings().getPlayerMenuEntries();
    }
}

