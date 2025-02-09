/*
 * Decompiled with CFR 0.152.
 */
package org.javapluginapi.team;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javapluginapi.team.JavaPluginApi;
import org.javapluginapi.team.api.Plugin;

public class PluginLogger {
    private static final Logger logger = LogManager.getLogger();

    public String logInfo(String i2) {
        String returned = String.valueOf(JavaPluginApi.outName) + "(" + Plugin.descriptionFile.get().getName() + ") [Info] " + i2;
        logger.info(returned);
        return returned;
    }

    public String logError(String i2) {
        String returned = String.valueOf(JavaPluginApi.outName) + "(" + Plugin.descriptionFile.get().getName() + ") [Error] " + i2;
        logger.error(returned);
        return returned;
    }
}

