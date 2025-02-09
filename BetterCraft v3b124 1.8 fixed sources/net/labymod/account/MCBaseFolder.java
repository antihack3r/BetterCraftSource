/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.account;

import java.io.File;

public class MCBaseFolder {
    public static File getWorkingDirectory() {
        return MCBaseFolder.getWorkingDirectory("minecraft");
    }

    public static File getWorkingDirectory(String applicationName) {
        String userHome = System.getProperty("user.home", ".");
        File workingDirectory = null;
        switch (MCBaseFolder.getPlatform()) {
            case LINUX: {
                workingDirectory = new File(userHome, String.valueOf('.') + applicationName + '/');
                break;
            }
            case UNKNOWN: {
                workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
                break;
            }
            case WINDOWS: {
                String applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    workingDirectory = new File(applicationData, "." + applicationName + '/');
                    break;
                }
                workingDirectory = new File(userHome, String.valueOf('.') + applicationName + '/');
                break;
            }
            case MACOS: {
                workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
                break;
            }
            case SOLARIS: {
                String applicationDataW = System.getenv("APPDATA");
                if (applicationDataW != null) {
                    workingDirectory = new File(applicationDataW, "." + applicationName + '/');
                    break;
                }
                workingDirectory = new File(userHome, String.valueOf('.') + applicationName + '/');
                break;
            }
            default: {
                workingDirectory = new File(userHome, String.valueOf(applicationName) + '/');
            }
        }
        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);
        }
        return workingDirectory;
    }

    public static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        OS[] oSArray = OS.values();
        int n2 = oSArray.length;
        int n3 = 0;
        while (n3 < n2) {
            OS os2 = oSArray[n3];
            if (os2 != OS.UNKNOWN) {
                String[] stringArray = os2.getOsNames();
                int n4 = stringArray.length;
                int n5 = 0;
                while (n5 < n4) {
                    String name = stringArray[n5];
                    if (osName.contains(name)) {
                        return os2;
                    }
                    ++n5;
                }
            }
            ++n3;
        }
        return OS.UNKNOWN;
    }

    public static enum OS {
        LINUX(new String[]{"linux", "unix"}),
        SOLARIS(new String[]{"solaris", "sunos"}),
        WINDOWS(new String[]{"win"}),
        MACOS(new String[]{"mac"}),
        UNKNOWN(new String[0]);

        private String[] osNames;

        private OS(String[] osNames) {
            this.osNames = osNames;
        }

        public String[] getOsNames() {
            return this.osNames;
        }
    }
}

