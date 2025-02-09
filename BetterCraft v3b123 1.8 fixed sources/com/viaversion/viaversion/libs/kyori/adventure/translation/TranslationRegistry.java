// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.kyori.adventure.translation;

import java.util.Iterator;
import java.util.function.Function;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ResourceBundle;
import java.io.Reader;
import java.util.PropertyResourceBundle;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import java.text.MessageFormat;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import java.util.regex.Pattern;

public interface TranslationRegistry extends Translator
{
    public static final Pattern SINGLE_QUOTE_PATTERN = Pattern.compile("'");
    
    @NotNull
    default TranslationRegistry create(final Key name) {
        return new TranslationRegistryImpl(Objects.requireNonNull(name, "name"));
    }
    
    boolean contains(@NotNull final String key);
    
    @Nullable
    MessageFormat translate(@NotNull final String key, @NotNull final Locale locale);
    
    void defaultLocale(@NotNull final Locale locale);
    
    void register(@NotNull final String key, @NotNull final Locale locale, @NotNull final MessageFormat format);
    
    default void registerAll(@NotNull final Locale locale, @NotNull final Map<String, MessageFormat> formats) {
        final Set<String> keySet = formats.keySet();
        Objects.requireNonNull(formats);
        this.registerAll(locale, keySet, formats::get);
    }
    
    default void registerAll(@NotNull final Locale locale, @NotNull final Path path, final boolean escapeSingleQuotes) {
        try (final BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            this.registerAll(locale, new PropertyResourceBundle(reader), escapeSingleQuotes);
        }
        catch (final IOException ex) {}
    }
    
    default void registerAll(@NotNull final Locale locale, @NotNull final ResourceBundle bundle, final boolean escapeSingleQuotes) {
        this.registerAll(locale, bundle.keySet(), key -> {
            final String format = bundle.getString(key);
            new MessageFormat(escapeSingleQuotes ? TranslationRegistry.SINGLE_QUOTE_PATTERN.matcher(format).replaceAll("''") : format, locale);
            return;
        });
    }
    
    default void registerAll(@NotNull final Locale locale, @NotNull final Set<String> keys, final Function<String, MessageFormat> function) {
        IllegalArgumentException firstError = null;
        int errorCount = 0;
        for (final String key : keys) {
            try {
                this.register(key, locale, function.apply(key));
            }
            catch (final IllegalArgumentException e) {
                if (firstError == null) {
                    firstError = e;
                }
                ++errorCount;
            }
        }
        if (firstError != null) {
            if (errorCount == 1) {
                throw firstError;
            }
            if (errorCount > 1) {
                throw new IllegalArgumentException(String.format("Invalid key (and %d more)", errorCount - 1), firstError);
            }
        }
    }
    
    void unregister(@NotNull final String key);
}
