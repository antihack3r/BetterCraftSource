/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceIndex {
    private static final Logger logger = LogManager.getLogger();
    private final Map<String, File> resourceMap;

    public ResourceIndex(File p_i1047_1_, String p_i1047_2_) {
        block9: {
            this.resourceMap = Maps.newHashMap();
            if (p_i1047_2_ != null) {
                File file1 = new File(p_i1047_1_, "objects");
                File file2 = new File(p_i1047_1_, "indexes/" + p_i1047_2_ + ".json");
                BufferedReader bufferedreader = null;
                try {
                    bufferedreader = Files.newReader(file2, Charsets.UTF_8);
                    JsonObject jsonobject = new JsonParser().parse(bufferedreader).getAsJsonObject();
                    JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "objects", null);
                    if (jsonobject1 != null) {
                        for (Map.Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
                            JsonObject jsonobject2 = (JsonObject)entry.getValue();
                            String s2 = entry.getKey();
                            String[] astring = s2.split("/", 2);
                            String s1 = astring.length == 1 ? astring[0] : String.valueOf(astring[0]) + ":" + astring[1];
                            String s22 = JsonUtils.getString(jsonobject2, "hash");
                            File file3 = new File(file1, String.valueOf(s22.substring(0, 2)) + "/" + s22);
                            this.resourceMap.put(s1, file3);
                        }
                    }
                }
                catch (JsonParseException var20) {
                    logger.error("Unable to parse resource index file: " + file2);
                    IOUtils.closeQuietly(bufferedreader);
                    break block9;
                }
                catch (FileNotFoundException var21) {
                    try {
                        logger.error("Can't find the resource index file: " + file2);
                    }
                    catch (Throwable throwable) {
                        IOUtils.closeQuietly(bufferedreader);
                        throw throwable;
                    }
                    IOUtils.closeQuietly(bufferedreader);
                    break block9;
                }
                IOUtils.closeQuietly(bufferedreader);
            }
        }
    }

    public Map<String, File> getResourceMap() {
        return this.resourceMap;
    }
}

