/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main;

import java.util.ArrayList;
import net.labymod.api.permissions.Permissions;
import net.labymod.api.protocol.liquid.FixedLiquidBucketProtocol;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.labyconnect.user.EnumAlertDisplayType;
import net.labymod.main.LabyMod;
import net.labymod.main.ModSettings;
import net.labymod.main.ModTextures;
import net.labymod.main.Source;
import net.labymod.mojang.inventory.scale.EnumGuiScale;
import net.labymod.settings.SettingsCategory;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.settings.elements.KeyElement;
import net.labymod.settings.elements.ListContainerElement;
import net.labymod.settings.elements.NumberElement;
import net.labymod.settings.elements.SliderElement;
import net.labymod.settings.elements.StringElement;
import net.labymod.user.cosmetic.custom.handler.CloakImageHandler;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import net.labymod.utils.manager.LavaLightUpdater;

public class DefinedSettings {
    private static boolean isMC18 = true;
    private static ArrayList<SettingsCategory> mainSettingsCategories;
    private static SettingsCategory chatSetingsCategory;

    static {
        try {
            mainSettingsCategories = new ArrayList();
            mainSettingsCategories.add(DefinedSettings.getInformation());
            mainSettingsCategories.add(DefinedSettings.getAnimations());
            mainSettingsCategories.add(DefinedSettings.getBugfixes());
            mainSettingsCategories.add(DefinedSettings.getMinecraftChat());
            mainSettingsCategories.add(DefinedSettings.getPvP());
            mainSettingsCategories.add(DefinedSettings.getMenuGUI());
            mainSettingsCategories.add(DefinedSettings.getAdditional());
            mainSettingsCategories.add(DefinedSettings.getKeys());
            mainSettingsCategories.add(DefinedSettings.getCosmetics());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static ArrayList<SettingsCategory> getCategories() {
        return mainSettingsCategories;
    }

    private static SettingsCategory getInformation() {
        SettingsCategory mainCategory = new SettingsCategory("settings_category_information");
        BooleanElement elementPingOnTab = new BooleanElement("tabPing", new ControlElement.IconData());
        elementPingOnTab.getSubSettings().add(new BooleanElement("tabPing_colored", new ControlElement.IconData()));
        mainCategory.addSetting(elementPingOnTab);
        mainCategory.addSetting(new BooleanElement("notifyPermissionChanges", new ControlElement.IconData()));
        BooleanElement elementFamiliarUsers = new BooleanElement("revealFamiliarUsers", new ControlElement.IconData(ModTextures.LOGO_LABYMOD_LOGO)).addCallback(new Consumer<Boolean>(){

            @Override
            public void accept(Boolean accepted) {
                if (accepted.booleanValue()) {
                    LabyMod.getInstance().getUserManager().getFamiliarManager().refresh();
                }
            }
        });
        elementFamiliarUsers.getSubSettings().add(new BooleanElement("revealFamiliarUsersPercentage", new ControlElement.IconData()));
        mainCategory.addSetting(elementFamiliarUsers);
        mainCategory.addSetting(new BooleanElement("outOfMemoryWarning", new ControlElement.IconData(Material.BARRIER)));
        return mainCategory;
    }

    private static SettingsCategory getAnimations() {
        SettingsCategory mainCategory = new SettingsCategory("settings_category_animations");
        mainCategory.addSetting(new BooleanElement("oldDamage", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("oldHearts", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("oldHitbox", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("oldTablist", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("oldSneaking", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("oldInventory", new ControlElement.IconData()));
        if (isMC18) {
            mainCategory.addSetting(new BooleanElement("oldSword", new ControlElement.IconData(Material.IRON_SWORD)));
            mainCategory.addSetting(new BooleanElement("oldFood", new ControlElement.IconData(Material.GOLDEN_APPLE)));
            mainCategory.addSetting(new BooleanElement("oldBow", new ControlElement.IconData(Material.BOW)));
            mainCategory.addSetting(new BooleanElement("oldFishing", new ControlElement.IconData(Material.FISHING_ROD)));
            mainCategory.addSetting(new BooleanElement("oldBlockhit", new ControlElement.IconData()));
            mainCategory.addSetting(new BooleanElement("oldItemSwitch", new ControlElement.IconData()));
            mainCategory.addSetting(new BooleanElement("oldItemHold", new ControlElement.IconData()));
        } else {
            mainCategory.addSetting(new BooleanElement("oldWalking", new ControlElement.IconData()));
        }
        mainCategory.bindPermissionToAll(Permissions.Permission.ANIMATIONS);
        mainCategory.bindCustomBooleanToAll("1.7", Source.ABOUT_MC_VERSION);
        return mainCategory;
    }

    private static SettingsCategory getBugfixes() {
        SettingsCategory mainCategory = new SettingsCategory("settings_category_bugfixes");
        ListContainerElement elementImprovedLava = new ListContainerElement("improved_lava", new ControlElement.IconData(Material.LAVA_BUCKET));
        elementImprovedLava.getSubSettings().add(new BooleanElement("improvedLavaFixedGhostBlocks", new ControlElement.IconData(Material.BUCKET)).addCallback(new Consumer<Boolean>(){

            @Override
            public void accept(Boolean accepted) {
                FixedLiquidBucketProtocol.handleBucketAction(accepted != false ? FixedLiquidBucketProtocol.Action.ENABLE : FixedLiquidBucketProtocol.Action.DISABLE, 0, 0, 0);
            }
        }));
        elementImprovedLava.getSubSettings().add(new BooleanElement("improvedLavaNoLight", new ControlElement.IconData(Material.TORCH)).addCallback(new Consumer<Boolean>(){

            @Override
            public void accept(Boolean accepted) {
                LavaLightUpdater.update();
            }
        }));
        mainCategory.addSetting(elementImprovedLava);
        mainCategory.bindPermissionToAll(Permissions.Permission.IMPROVED_LAVA);
        mainCategory.addSetting(new BooleanElement("refillFix", new ControlElement.IconData(Material.MUSHROOM_SOUP)).bindPermission(Permissions.Permission.REFILL_FIX));
        if (isMC18) {
            mainCategory.addSetting(new BooleanElement("crosshairSync", new ControlElement.IconData()).bindPermission(Permissions.Permission.CROSSHAIR_SYNC));
            mainCategory.addSetting(new BooleanElement("oldBlockbuild", new ControlElement.IconData()).bindPermission(Permissions.Permission.BLOCKBUILD));
        }
        mainCategory.addSetting(new BooleanElement("particleFix", new ControlElement.IconData()));
        BooleanElement chunkCachingElement = new BooleanElement("chunkCaching", new ControlElement.IconData());
        chunkCachingElement.getSubSettings().add(new NumberElement("chunkCachingSize", new ControlElement.IconData(Material.BOOK)).setRange(1, (int)(Runtime.getRuntime().maxMemory() / 1000000L)));
        chunkCachingElement.getSubSettings().add(new BooleanElement("chunkCachingStoreInFile", new ControlElement.IconData(Material.PAPER)));
        mainCategory.addSetting(chunkCachingElement);
        mainCategory.addSetting(new BooleanElement("fastWorldLoading", new ControlElement.IconData()));
        return mainCategory;
    }

    private static SettingsCategory getMinecraftChat() {
        SettingsCategory mainCategory = new SettingsCategory("settings_category_minecraft_chat");
        mainCategory.addSetting(new BooleanElement("autoText", new ControlElement.IconData()));
        ListContainerElement nameHistoryElement = new ListContainerElement("name_history", new ControlElement.IconData(Material.BOOK));
        nameHistoryElement.getSubSettings().add(new BooleanElement("hoverNameHistory", new ControlElement.IconData(Material.BOOK_AND_QUILL)));
        nameHistoryElement.getSubSettings().add(new BooleanElement("nameHistory", new ControlElement.IconData(Material.BOOK)));
        mainCategory.addSetting(nameHistoryElement);
        mainCategory.addSetting(new BooleanElement("chatSymbols", new ControlElement.IconData()).bindPermission(Permissions.Permission.CHAT));
        mainCategory.addSetting(new BooleanElement("chatFilter", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("chatShortcuts", new ControlElement.IconData()).bindPermission(Permissions.Permission.CHAT));
        BooleanElement playerMenu = new BooleanElement("playerMenu", new ControlElement.IconData());
        playerMenu.getSubSettings().add(new BooleanElement("playerMenuEditor", new ControlElement.IconData()));
        playerMenu.getSubSettings().add(new BooleanElement("playerMenuAnimation", new ControlElement.IconData()));
        mainCategory.addSetting(playerMenu);
        mainCategory.addSetting(new BooleanElement("fastChat", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("scalableChat", new ControlElement.IconData()));
        ListContainerElement advancedElement = new ListContainerElement("advanced_chat_settings", new ControlElement.IconData());
        advancedElement.getSubSettings().add(new SliderElement("chatScrollSpeed", new ControlElement.IconData(Material.DIODE)).setRange(1, 10));
        advancedElement.getSubSettings().add(new SliderElement("chatLineLimit", new ControlElement.IconData(Material.DIODE)).setRange(100, 1000).setSteps(100));
        advancedElement.getSubSettings().add(new BooleanElement("chatAnimation", new ControlElement.IconData()));
        if (!isMC18) {
            advancedElement.getSubSettings().add(new BooleanElement("clearChatOnJoin", new ControlElement.IconData(Material.BARRIER)));
        }
        mainCategory.addSetting(advancedElement);
        ListContainerElement secondChatElement = new ListContainerElement("second_chat", new ControlElement.IconData());
        secondChatElement.getSubSettings().add(new BooleanElement("chatPositionRight", new ControlElement.IconData()));
        secondChatElement.getSubSettings().add(new SliderElement("secondChatWidth", new ControlElement.IconData(Material.DIODE)).setRange(40, 320));
        secondChatElement.getSubSettings().add(new SliderElement("secondChatHeight", new ControlElement.IconData(Material.DIODE)).setRange(20, 180));
        mainCategory.addSetting(secondChatElement);
        mainCategory.addSetting(new BooleanElement("showModuleEditorShortcut", new ControlElement.IconData()));
        return mainCategory;
    }

    private static SettingsCategory getPvP() {
        SettingsCategory mainCategory = new SettingsCategory("settings_category_pvp");
        mainCategory.addSetting(new BooleanElement("speedFov", new ControlElement.IconData(Material.EYE_OF_ENDER)));
        if (isMC18) {
            mainCategory.addSetting(new BooleanElement("swapBow", new ControlElement.IconData()));
        }
        return mainCategory;
    }

    private static SettingsCategory getMenuGUI() {
        SettingsCategory mainCategory = new SettingsCategory("settings_category_menu_gui");
        mainCategory.addSetting(new BooleanElement("guiBackground", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("directConnectInfo", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("confirmDisconnect", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("quickPlay", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("publicServerList", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("multiplayerIngame", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("borderlessWindow", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("betterSkinCustomization", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("betterShaderSelection", new ControlElement.IconData()));
        BooleanElement serverlistLiveView = new BooleanElement("serverlistLiveView", new ControlElement.IconData());
        serverlistLiveView.getSubSettings().add(new NumberElement("serverlistLiveViewInterval", new ControlElement.IconData(Material.WATCH)).setRange(3, 60));
        mainCategory.addSetting(serverlistLiveView);
        DropDownMenu<EnumGuiScale> dropDownMenu = new DropDownMenu<EnumGuiScale>(null, 0, 0, 0, 0).fill(EnumGuiScale.values());
        final DropDownElement<EnumGuiScale> dropDownElement = new DropDownElement<EnumGuiScale>("customInventoryScale", dropDownMenu, new ControlElement.IconData(), new DropDownElement.DrowpDownLoadValue<EnumGuiScale>(){

            @Override
            public EnumGuiScale load(String value) {
                return EnumGuiScale.valueOf(value);
            }
        });
        dropDownElement.setChangeListener(new Consumer<EnumGuiScale>(){

            @Override
            public void accept(EnumGuiScale accepted) {
                try {
                    ModSettings.class.getDeclaredField(dropDownElement.getConfigEntryName()).set(LabyMod.getSettings(), (Object)accepted);
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
        dropDownMenu.setEntryDrawer(new DropDownMenu.DropDownEntryDrawer(){

            @Override
            public void draw(Object object, int x2, int y2, String trimmedEntry) {
                LabyMod.getInstance().getDrawUtils().drawString(((EnumGuiScale)((Object)object)).getDisplayName(), x2, y2);
            }
        });
        mainCategory.addSetting(dropDownElement);
        return mainCategory;
    }

    private static SettingsCategory getAdditional() {
        SettingsCategory mainCategory = new SettingsCategory("settings_category_additional");
        mainCategory.addSetting(new BooleanElement("showMyName", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("showBossBar", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("showSaturation", new ControlElement.IconData()).bindPermission(Permissions.Permission.SATURATION_BAR));
        mainCategory.addSetting(new BooleanElement("signSearch", new ControlElement.IconData(Material.SIGN)));
        if (isMC18) {
            mainCategory.addSetting(new BooleanElement("leftHand", new ControlElement.IconData()));
        }
        return mainCategory;
    }

    private static SettingsCategory getKeys() {
        SettingsCategory mainCategory = new SettingsCategory("settings_category_keys");
        mainCategory.addSetting(new KeyElement("keyModuleEditor", new ControlElement.IconData()));
        mainCategory.addSetting(new KeyElement("keyAddons", new ControlElement.IconData()));
        mainCategory.addSetting(new KeyElement("keyEmote", new ControlElement.IconData()));
        mainCategory.addSetting(new KeyElement("keyStickerMenu", new ControlElement.IconData()));
        mainCategory.addSetting(new KeyElement("keyPlayerMenu", new ControlElement.IconData()));
        mainCategory.addSetting(new KeyElement("keyToggleHitbox", new ControlElement.IconData()));
        return mainCategory;
    }

    private static SettingsCategory getCosmetics() {
        SettingsCategory mainCategory = new SettingsCategory("settings_category_cosmetics");
        mainCategory.addSetting(new BooleanElement("emotes", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("stickers", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("cosmetics", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("cosmeticsCustomTextures", new ControlElement.IconData()));
        DropDownMenu<CloakImageHandler.EnumCapePriority> dropDownMenu = new DropDownMenu<CloakImageHandler.EnumCapePriority>(null, 0, 0, 0, 0).fill(CloakImageHandler.EnumCapePriority.values());
        DropDownElement dropDownElement = new DropDownElement<CloakImageHandler.EnumCapePriority>("capePriority", dropDownMenu, new ControlElement.IconData(), new DropDownElement.DrowpDownLoadValue<CloakImageHandler.EnumCapePriority>(){

            @Override
            public CloakImageHandler.EnumCapePriority load(String value) {
                return CloakImageHandler.EnumCapePriority.valueOf(value);
            }
        }).setCallback(new Consumer<CloakImageHandler.EnumCapePriority>(){

            @Override
            public void accept(CloakImageHandler.EnumCapePriority accepted) {
                LabyMod.getInstance().getUserManager().getCosmeticImageManager().getCloakImageHandler().setPriority(accepted);
            }
        });
        mainCategory.addSetting(dropDownElement);
        mainCategory.addSetting(new BooleanElement("capeOriginalParticles", new ControlElement.IconData()));
        return mainCategory;
    }

    private static SettingsCategory getLabyModChat() {
        SettingsCategory mainCategory = new SettingsCategory("settings_category_labymod_chat");
        mainCategory.addSetting(new StringElement("motd", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("ignoreRequests", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("showConnectedIp", new ControlElement.IconData()).addCallback(accepted -> LabyMod.getInstance().getLabyConnect().getClientProfile().sendSettingsToServer()));
        DropDownMenu<EnumAlertDisplayType> dropDownMenu = new DropDownMenu<EnumAlertDisplayType>(null, 0, 0, 0, 0).fill(EnumAlertDisplayType.values());
        DropDownElement dropDownElement = new DropDownElement<EnumAlertDisplayType>("alertDisplayType", dropDownMenu, null, value -> EnumAlertDisplayType.valueOf(value)).setCallback(accepted -> LabyMod.getInstance().getLabyConnect().updateAlertDisplayType());
        LabyMod.getInstance().getLabyConnect().updateAlertDisplayType();
        mainCategory.addSetting(dropDownElement);
        mainCategory.addSetting(new BooleanElement("alertsPlayingOn", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("alertsOnlineStatus", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("alertPlaySounds", new ControlElement.IconData()));
        mainCategory.addSetting(new BooleanElement("unreadMessageIcon", new ControlElement.IconData(Material.WORKBENCH)));
        return mainCategory;
    }

    public static SettingsCategory getChatSetingsCategory() {
        return chatSetingsCategory;
    }
}

