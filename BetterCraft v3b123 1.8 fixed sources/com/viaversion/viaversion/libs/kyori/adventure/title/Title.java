// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.kyori.adventure.title;

import java.time.Duration;
import com.viaversion.viaversion.libs.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;

@ApiStatus.NonExtendable
public interface Title extends Examinable
{
    public static final Times DEFAULT_TIMES = Times.times(Ticks.duration(10L), Ticks.duration(70L), Ticks.duration(20L));
    
    @NotNull
    default Title title(@NotNull final Component title, @NotNull final Component subtitle) {
        return title(title, subtitle, Title.DEFAULT_TIMES);
    }
    
    @NotNull
    default Title title(@NotNull final Component title, @NotNull final Component subtitle, @Nullable final Times times) {
        return new TitleImpl(title, subtitle, times);
    }
    
    @NotNull
    Component title();
    
    @NotNull
    Component subtitle();
    
    @Nullable
    Times times();
    
     <T> T part(@NotNull final TitlePart<T> part);
    
    public interface Times extends Examinable
    {
        @Deprecated
        @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
        @NotNull
        default Times of(@NotNull final Duration fadeIn, @NotNull final Duration stay, @NotNull final Duration fadeOut) {
            return times(fadeIn, stay, fadeOut);
        }
        
        @NotNull
        default Times times(@NotNull final Duration fadeIn, @NotNull final Duration stay, @NotNull final Duration fadeOut) {
            return new TitleImpl.TimesImpl(fadeIn, stay, fadeOut);
        }
        
        @NotNull
        Duration fadeIn();
        
        @NotNull
        Duration stay();
        
        @NotNull
        Duration fadeOut();
    }
}
