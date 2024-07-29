/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.splash.advertisement;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.awt.Color;
import java.io.IOException;

public class AdColorAdapter
extends TypeAdapter<Color> {
    @Override
    public Color read(JsonReader jsonReader) throws IOException {
        return Color.decode(jsonReader.nextString());
    }

    @Override
    public void write(JsonWriter jsonWriter, Color color) throws IOException {
        jsonWriter.name("color").value(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
    }
}

