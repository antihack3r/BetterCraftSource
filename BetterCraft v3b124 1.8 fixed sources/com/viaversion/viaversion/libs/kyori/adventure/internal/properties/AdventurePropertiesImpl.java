/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.VisibleForTesting
 */
package com.viaversion.viaversion.libs.kyori.adventure.internal.properties;

import com.viaversion.viaversion.libs.kyori.adventure.internal.properties.AdventureProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

final class AdventurePropertiesImpl {
    private static final String FILESYSTEM_DIRECTORY_NAME = "config";
    private static final String FILESYSTEM_FILE_NAME = "adventure.properties";
    private static final Properties PROPERTIES = new Properties();

    private static void print(Throwable ex2) {
        ex2.printStackTrace();
    }

    private AdventurePropertiesImpl() {
    }

    @VisibleForTesting
    @NotNull
    static String systemPropertyName(String name) {
        return String.join((CharSequence)".", "net", "kyori", "adventure", name);
    }

    static <T> @NotNull AdventureProperties.Property<T> property(@NotNull String name, @NotNull Function<String, T> parser, @Nullable T defaultValue) {
        return new PropertyImpl<T>(name, parser, defaultValue);
    }

    static {
        Path path = Optional.ofNullable(System.getProperty(AdventurePropertiesImpl.systemPropertyName(FILESYSTEM_DIRECTORY_NAME))).map(x$0 -> Paths.get(x$0, new String[0])).orElseGet(() -> Paths.get(FILESYSTEM_DIRECTORY_NAME, FILESYSTEM_FILE_NAME));
        if (Files.isRegularFile(path, new LinkOption[0])) {
            try (InputStream is2 = Files.newInputStream(path, new OpenOption[0]);){
                PROPERTIES.load(is2);
            }
            catch (IOException e2) {
                AdventurePropertiesImpl.print(e2);
            }
        }
    }

    private static final class PropertyImpl<T>
    implements AdventureProperties.Property<T> {
        private final String name;
        private final Function<String, T> parser;
        @Nullable
        private final T defaultValue;
        private boolean valueCalculated;
        @Nullable
        private T value;

        PropertyImpl(@NotNull String name, @NotNull Function<String, T> parser, @Nullable T defaultValue) {
            this.name = name;
            this.parser = parser;
            this.defaultValue = defaultValue;
        }

        @Override
        @Nullable
        public T value() {
            if (!this.valueCalculated) {
                String property = AdventurePropertiesImpl.systemPropertyName(this.name);
                String value = System.getProperty(property, PROPERTIES.getProperty(this.name));
                if (value != null) {
                    this.value = this.parser.apply(value);
                }
                if (this.value == null) {
                    this.value = this.defaultValue;
                }
                this.valueCalculated = true;
            }
            return this.value;
        }

        public boolean equals(@Nullable Object that) {
            return this == that;
        }

        public int hashCode() {
            return this.name.hashCode();
        }
    }
}

