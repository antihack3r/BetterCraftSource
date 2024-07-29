/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.manager;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import net.labymod.support.util.Debug;
import org.apache.commons.io.IOUtils;

public class ConfigManager<T> {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File file;
    private Class<? extends T> configDefaults;
    private T settings;

    public ConfigManager(File file, Class<? extends T> configDefaults) {
        this.file = Preconditions.checkNotNull(file);
        this.configDefaults = Preconditions.checkNotNull(configDefaults);
        this.loadConfig(false);
    }

    private void loadConfig(boolean reload) {
        if (!this.file.getParentFile().exists()) {
            this.file.getParentFile().mkdir();
        }
        boolean createdNewFile = false;
        if (reload && this.file.exists()) {
            createdNewFile = true;
        }
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
                createdNewFile = true;
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(this.file);
        }
        catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        try {
            try {
                this.settings = gson.fromJson(createdNewFile ? gson.toJson(this.configDefaults.newInstance()) : IOUtils.toString((InputStream)stream, StandardCharsets.UTF_8), this.configDefaults);
                Debug.log(Debug.EnumDebugMode.CONFIG_MANAGER, this.settings != null ? "Loaded " + this.file.getName() + "!" : "Loaded file but settings is null");
                if (!reload && this.settings == null) {
                    this.loadConfig(true);
                } else if (this.settings != null) {
                    this.save();
                }
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
                Debug.log(Debug.EnumDebugMode.CONFIG_MANAGER, "Failed to load " + this.file.getName() + " config!");
                if (!reload) {
                    this.loadConfig(true);
                }
                try {
                    stream.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
                try {
                    stream.close();
                }
                catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
        finally {
            try {
                stream.close();
            }
            catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }

    public void save() {
        try {
            PrintWriter w2 = new PrintWriter((Writer)new OutputStreamWriter((OutputStream)new FileOutputStream(this.file), StandardCharsets.UTF_8), true);
            w2.print(gson.toJson(this.settings));
            w2.flush();
            w2.close();
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }

    public T getSettings() {
        return this.settings;
    }

    public File getFile() {
        return this.file;
    }
}

