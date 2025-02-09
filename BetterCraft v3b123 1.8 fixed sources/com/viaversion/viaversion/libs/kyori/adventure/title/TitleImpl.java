// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.kyori.adventure.title;

import java.time.Duration;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import com.viaversion.viaversion.libs.kyori.adventure.internal.Internals;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.stream.Stream;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;

final class TitleImpl implements Title
{
    private final Component title;
    private final Component subtitle;
    @Nullable
    private final Times times;
    
    TitleImpl(@NotNull final Component title, @NotNull final Component subtitle, @Nullable final Times times) {
        this.title = Objects.requireNonNull(title, "title");
        this.subtitle = Objects.requireNonNull(subtitle, "subtitle");
        this.times = times;
    }
    
    @NotNull
    @Override
    public Component title() {
        return this.title;
    }
    
    @NotNull
    @Override
    public Component subtitle() {
        return this.subtitle;
    }
    
    @Nullable
    @Override
    public Times times() {
        return this.times;
    }
    
    @Override
    public <T> T part(@NotNull final TitlePart<T> part) {
        Objects.requireNonNull(part, "part");
        if (part == TitlePart.TITLE) {
            return (T)this.title;
        }
        if (part == TitlePart.SUBTITLE) {
            return (T)this.subtitle;
        }
        if (part == TitlePart.TIMES) {
            return (T)this.times;
        }
        throw new IllegalArgumentException("Don't know what " + part + " is.");
    }
    
    @Override
    public boolean equals(@Nullable final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        final TitleImpl that = (TitleImpl)other;
        return this.title.equals(that.title) && this.subtitle.equals(that.subtitle) && Objects.equals(this.times, that.times);
    }
    
    @Override
    public int hashCode() {
        int result = this.title.hashCode();
        result = 31 * result + this.subtitle.hashCode();
        result = 31 * result + Objects.hashCode(this.times);
        return result;
    }
    
    @NotNull
    @Override
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of((ExaminableProperty[])new ExaminableProperty[] { ExaminableProperty.of("title", this.title), ExaminableProperty.of("subtitle", this.subtitle), ExaminableProperty.of("times", this.times) });
    }
    
    @Override
    public String toString() {
        return Internals.toString(this);
    }
    
    static class TimesImpl implements Times
    {
        private final Duration fadeIn;
        private final Duration stay;
        private final Duration fadeOut;
        
        TimesImpl(@NotNull final Duration fadeIn, @NotNull final Duration stay, @NotNull final Duration fadeOut) {
            this.fadeIn = Objects.requireNonNull(fadeIn, "fadeIn");
            this.stay = Objects.requireNonNull(stay, "stay");
            this.fadeOut = Objects.requireNonNull(fadeOut, "fadeOut");
        }
        
        @NotNull
        @Override
        public Duration fadeIn() {
            return this.fadeIn;
        }
        
        @NotNull
        @Override
        public Duration stay() {
            return this.stay;
        }
        
        @NotNull
        @Override
        public Duration fadeOut() {
            return this.fadeOut;
        }
        
        @Override
        public boolean equals(@Nullable final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof TimesImpl)) {
                return false;
            }
            final TimesImpl that = (TimesImpl)other;
            return this.fadeIn.equals(that.fadeIn) && this.stay.equals(that.stay) && this.fadeOut.equals(that.fadeOut);
        }
        
        @Override
        public int hashCode() {
            int result = this.fadeIn.hashCode();
            result = 31 * result + this.stay.hashCode();
            result = 31 * result + this.fadeOut.hashCode();
            return result;
        }
        
        @NotNull
        @Override
        public Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.of((ExaminableProperty[])new ExaminableProperty[] { ExaminableProperty.of("fadeIn", this.fadeIn), ExaminableProperty.of("stay", this.stay), ExaminableProperty.of("fadeOut", this.fadeOut) });
        }
        
        @Override
        public String toString() {
            return Internals.toString(this);
        }
    }
}
