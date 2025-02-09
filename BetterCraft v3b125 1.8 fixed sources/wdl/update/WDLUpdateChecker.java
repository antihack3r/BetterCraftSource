/*
 * Decompiled with CFR 0.152.
 */
package wdl.update;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import wdl.WDL;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.api.IWDLMessageType;
import wdl.update.ClassHasher;
import wdl.update.GithubInfoGrabber;
import wdl.update.Release;

public class WDLUpdateChecker
extends Thread {
    private static volatile boolean started = false;
    private static volatile boolean finished = false;
    private static volatile boolean failed = false;
    private static volatile String failReason = null;
    private static volatile List<Release> releases;
    private static volatile Release runningRelease;
    private static final String FORUMS_THREAD_USAGE_LINK = "http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2520465#Usage";
    private static final String WIKI_LINK = "https://github.com/pokechu22/WorldDownloader/wiki";
    private static final String GITHUB_LINK = "https://github.com/pokechu22/WorldDownloader";
    private static final String REDISTRIBUTION_LINK = "http://pokechu22.github.io/WorldDownloader/redistribution";
    private static final String SMR_LINK = "http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/mods-discussion/2314237";

    public static List<Release> getReleases() {
        return releases;
    }

    public static Release getRunningRelease() {
        return runningRelease;
    }

    public static Release getRecomendedRelease() {
        if (releases == null) {
            return null;
        }
        if (runningRelease == null) {
            return null;
        }
        String mcVersion = WDL.getMinecraftVersion();
        boolean usePrereleases = WDL.globalProps.getProperty("UpdateAllowBetas").equals("true");
        boolean versionMustBeExact = WDL.globalProps.getProperty("UpdateMinecraftVersion").equals("client");
        boolean versionMustBeCompatible = WDL.globalProps.getProperty("UpdateMinecraftVersion").equals("server");
        for (Release release : releases) {
            if (release.hiddenInfo == null || release.prerelease && !usePrereleases) continue;
            if (versionMustBeExact) {
                if (!release.hiddenInfo.mainMinecraftVersion.equals(mcVersion)) {
                    continue;
                }
            } else if (versionMustBeCompatible) {
                boolean foundCompatible = false;
                String[] stringArray = release.hiddenInfo.supportedMinecraftVersions;
                int n2 = release.hiddenInfo.supportedMinecraftVersions.length;
                int n3 = 0;
                while (n3 < n2) {
                    String version = stringArray[n3];
                    if (version.equals(mcVersion)) {
                        foundCompatible = true;
                        break;
                    }
                    ++n3;
                }
                if (!foundCompatible) continue;
            }
            if (releases.indexOf(release) > releases.indexOf(runningRelease)) continue;
            return release;
        }
        return null;
    }

    public static boolean hasNewVersion() {
        if (runningRelease == null) {
            return false;
        }
        Release recomendedRelease = WDLUpdateChecker.getRecomendedRelease();
        if (recomendedRelease == null) {
            return false;
        }
        return runningRelease != recomendedRelease;
    }

    public static void startIfNeeded() {
        if (!started) {
            started = true;
            new WDLUpdateChecker().start();
        }
    }

    public static boolean hasFinishedUpdateCheck() {
        return finished;
    }

    public static boolean hasUpdateCheckFailed() {
        return failed;
    }

    public static String getUpdateCheckFailReason() {
        return failReason;
    }

    private WDLUpdateChecker() {
        super("WorldDownloader update check thread");
    }

    @Override
    public void run() {
        try {
            ChatComponentTranslation mcfThread;
            if (!WDL.globalProps.getProperty("TutorialShown").equals("true")) {
                WDLUpdateChecker.sleep(5000L);
                ChatComponentTranslation success = new ChatComponentTranslation("wdl.intro.success", new Object[0]);
                mcfThread = new ChatComponentTranslation("wdl.intro.forumsLink", new Object[0]);
                mcfThread.getChatStyle().setColor(EnumChatFormatting.BLUE).setUnderlined(true).setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, FORUMS_THREAD_USAGE_LINK));
                ChatComponentTranslation wikiLink = new ChatComponentTranslation("wdl.intro.wikiLink", new Object[0]);
                wikiLink.getChatStyle().setColor(EnumChatFormatting.BLUE).setUnderlined(true).setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, WIKI_LINK));
                ChatComponentTranslation usage = new ChatComponentTranslation("wdl.intro.usage", mcfThread, wikiLink);
                ChatComponentTranslation githubRepo = new ChatComponentTranslation("wdl.intro.githubRepo", new Object[0]);
                githubRepo.getChatStyle().setColor(EnumChatFormatting.BLUE).setUnderlined(true).setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, GITHUB_LINK));
                ChatComponentTranslation contribute = new ChatComponentTranslation("wdl.intro.contribute", githubRepo);
                ChatComponentTranslation redistributionList = new ChatComponentTranslation("wdl.intro.redistributionList", new Object[0]);
                redistributionList.getChatStyle().setColor(EnumChatFormatting.BLUE).setUnderlined(true).setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, REDISTRIBUTION_LINK));
                ChatComponentTranslation warning = new ChatComponentTranslation("wdl.intro.warning", new Object[0]);
                warning.getChatStyle().setColor(EnumChatFormatting.DARK_RED).setBold(true);
                ChatComponentTranslation illegally = new ChatComponentTranslation("wdl.intro.illegally", new Object[0]);
                illegally.getChatStyle().setColor(EnumChatFormatting.DARK_RED).setBold(true);
                ChatComponentTranslation stolen = new ChatComponentTranslation("wdl.intro.stolen", warning, redistributionList, illegally);
                ChatComponentTranslation smr = new ChatComponentTranslation("wdl.intro.stopModReposts", new Object[0]);
                smr.getChatStyle().setColor(EnumChatFormatting.BLUE).setUnderlined(true).setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, SMR_LINK));
                ChatComponentTranslation stolenBeware = new ChatComponentTranslation("wdl.intro.stolenBeware", smr);
                WDLMessages.chatMessage((IWDLMessageType)WDLMessageTypes.UPDATES, success);
                WDLMessages.chatMessage((IWDLMessageType)WDLMessageTypes.UPDATES, usage);
                WDLMessages.chatMessage((IWDLMessageType)WDLMessageTypes.UPDATES, contribute);
                WDLMessages.chatMessage((IWDLMessageType)WDLMessageTypes.UPDATES, stolen);
                WDLMessages.chatMessage((IWDLMessageType)WDLMessageTypes.UPDATES, stolenBeware);
                WDL.globalProps.setProperty("TutorialShown", "true");
                WDL.saveGlobalProps();
            }
            WDLUpdateChecker.sleep(5000L);
            releases = GithubInfoGrabber.getReleases();
            WDLMessages.chatMessageTranslated(WDLMessageTypes.UPDATE_DEBUG, "wdl.messages.updates.releaseCount", releases.size());
            if (releases.isEmpty()) {
                failed = true;
                failReason = "No releases found.";
                return;
            }
            int i2 = 0;
            while (i2 < releases.size()) {
                Release release = releases.get(i2);
                if (release.tag.equalsIgnoreCase("1.8.9a-beta2")) {
                    runningRelease = release;
                }
                ++i2;
            }
            if (runningRelease == null) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.UPDATE_DEBUG, "wdl.messages.updates.failedToFindMatchingRelease", "1.8.9a-beta2");
                return;
            }
            if (WDLUpdateChecker.hasNewVersion()) {
                Release recomendedRelease = WDLUpdateChecker.getRecomendedRelease();
                ChatComponentTranslation updateLink = new ChatComponentTranslation("wdl.messages.updates.newRelease.updateLink", new Object[0]);
                updateLink.getChatStyle().setColor(EnumChatFormatting.BLUE).setUnderlined(true).setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, recomendedRelease.URL));
                WDLMessages.chatMessageTranslated(WDLMessageTypes.UPDATES, "wdl.messages.updates.newRelease", WDLUpdateChecker.runningRelease.tag, recomendedRelease.tag, updateLink);
            }
            if (WDLUpdateChecker.runningRelease.hiddenInfo == null) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.UPDATE_DEBUG, "wdl.messages.updates.failedToFindMetadata", "1.8.9a-beta2");
                return;
            }
            try {
                HashMap<Release.HashData, Object> failed = new HashMap<Release.HashData, Object>();
                Release.HashData[] hashDataArray = WDLUpdateChecker.runningRelease.hiddenInfo.hashes;
                int n2 = WDLUpdateChecker.runningRelease.hiddenInfo.hashes.length;
                int n3 = 0;
                while (n3 < n2) {
                    block21: {
                        Release.HashData data = hashDataArray[n3];
                        try {
                            String hash = ClassHasher.hash(data.relativeTo, data.file);
                            String[] stringArray = data.validHashes;
                            int n4 = data.validHashes.length;
                            int n5 = 0;
                            while (n5 < n4) {
                                String validHash = stringArray[n5];
                                if (!validHash.equalsIgnoreCase(hash)) {
                                    ++n5;
                                    continue;
                                }
                                break block21;
                            }
                            WDLMessages.chatMessageTranslated(WDLMessageTypes.UPDATE_DEBUG, "wdl.messages.updates.incorrectHash", data.file, data.relativeTo, Arrays.toString(data.validHashes), hash);
                            failed.put(data, hash);
                        }
                        catch (Exception e2) {
                            WDLMessages.chatMessageTranslated(WDLMessageTypes.UPDATE_DEBUG, "wdl.messages.updates.hashException", data.file, data.relativeTo, Arrays.toString(data.validHashes), e2);
                            failed.put(data, e2);
                        }
                    }
                    ++n3;
                }
                if (failed.size() > 0) {
                    mcfThread = new ChatComponentTranslation("wdl.intro.forumsLink", new Object[0]);
                    mcfThread.getChatStyle().setColor(EnumChatFormatting.BLUE).setUnderlined(true).setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, FORUMS_THREAD_USAGE_LINK));
                    WDLMessages.chatMessageTranslated(WDLMessageTypes.UPDATES, "wdl.messages.updates.badHashesFound", mcfThread);
                }
            }
            catch (Exception e3) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.UPDATE_DEBUG, "wdl.messages.updates.updateCheckError", e3);
                failed = true;
                failReason = e3.toString();
            }
        }
        finally {
            finished = true;
        }
    }
}

