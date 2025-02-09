/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 */
package com.viaversion.viaversion.libs.kyori.adventure.audience;

import com.viaversion.viaversion.libs.kyori.adventure.audience.Audiences;
import com.viaversion.viaversion.libs.kyori.adventure.audience.EmptyAudience;
import com.viaversion.viaversion.libs.kyori.adventure.audience.ForwardingAudience;
import com.viaversion.viaversion.libs.kyori.adventure.audience.ForwardingAudienceOverrideNotRequired;
import com.viaversion.viaversion.libs.kyori.adventure.audience.MessageType;
import com.viaversion.viaversion.libs.kyori.adventure.bossbar.BossBar;
import com.viaversion.viaversion.libs.kyori.adventure.chat.SignedMessage;
import com.viaversion.viaversion.libs.kyori.adventure.identity.Identified;
import com.viaversion.viaversion.libs.kyori.adventure.identity.Identity;
import com.viaversion.viaversion.libs.kyori.adventure.inventory.Book;
import com.viaversion.viaversion.libs.kyori.adventure.pointer.Pointered;
import com.viaversion.viaversion.libs.kyori.adventure.sound.Sound;
import com.viaversion.viaversion.libs.kyori.adventure.sound.SoundStop;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.title.Title;
import com.viaversion.viaversion.libs.kyori.adventure.title.TitlePart;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface Audience
extends Pointered {
    @NotNull
    public static Audience empty() {
        return EmptyAudience.INSTANCE;
    }

    @NotNull
    public static Audience audience(Audience ... audiences) {
        int length = audiences.length;
        if (length == 0) {
            return Audience.empty();
        }
        if (length == 1) {
            return audiences[0];
        }
        return Audience.audience(Arrays.asList(audiences));
    }

    @NotNull
    public static ForwardingAudience audience(@NotNull Iterable<? extends Audience> audiences) {
        return () -> audiences;
    }

    @NotNull
    public static Collector<? super Audience, ?, ForwardingAudience> toAudience() {
        return Audiences.COLLECTOR;
    }

    @NotNull
    default public Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
        return filter.test(this) ? this : Audience.empty();
    }

    default public void forEachAudience(@NotNull Consumer<? super Audience> action) {
        action.accept(this);
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull ComponentLike message) {
        this.sendMessage(message.asComponent());
    }

    default public void sendMessage(@NotNull Component message) {
        this.sendMessage(message, MessageType.SYSTEM);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull ComponentLike message, @NotNull MessageType type) {
        this.sendMessage(message.asComponent(), type);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull Component message, @NotNull MessageType type) {
        this.sendMessage(Identity.nil(), message, type);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull Identified source, @NotNull ComponentLike message) {
        this.sendMessage(source, message.asComponent());
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull Identity source, @NotNull ComponentLike message) {
        this.sendMessage(source, message.asComponent());
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull Identified source, @NotNull Component message) {
        this.sendMessage(source, message, MessageType.CHAT);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull Identity source, @NotNull Component message) {
        this.sendMessage(source, message, MessageType.CHAT);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull Identified source, @NotNull ComponentLike message, @NotNull MessageType type) {
        this.sendMessage(source, message.asComponent(), type);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull Identity source, @NotNull ComponentLike message, @NotNull MessageType type) {
        this.sendMessage(source, message.asComponent(), type);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
        this.sendMessage(source.identity(), message, type);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
    }

    default public void sendMessage(@NotNull Component message,  @NotNull ChatType.Bound boundChatType) {
        this.sendMessage(message, MessageType.CHAT);
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull ComponentLike message,  @NotNull ChatType.Bound boundChatType) {
        this.sendMessage(message.asComponent(), boundChatType);
    }

    default public void sendMessage(@NotNull SignedMessage signedMessage,  @NotNull ChatType.Bound boundChatType) {
        Component content;
        Component component = content = signedMessage.unsignedContent() != null ? signedMessage.unsignedContent() : Component.text(signedMessage.message());
        if (signedMessage.isSystem()) {
            this.sendMessage(content);
        } else {
            this.sendMessage(signedMessage.identity(), content, MessageType.CHAT);
        }
    }

    @ForwardingAudienceOverrideNotRequired
    default public void deleteMessage(@NotNull SignedMessage signedMessage) {
        if (signedMessage.canDelete()) {
            this.deleteMessage(Objects.requireNonNull(signedMessage.signature()));
        }
    }

    default public void deleteMessage(@NotNull SignedMessage.Signature signature) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendActionBar(@NotNull ComponentLike message) {
        this.sendActionBar(message.asComponent());
    }

    default public void sendActionBar(@NotNull Component message) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendPlayerListHeader(@NotNull ComponentLike header) {
        this.sendPlayerListHeader(header.asComponent());
    }

    default public void sendPlayerListHeader(@NotNull Component header) {
        this.sendPlayerListHeaderAndFooter(header, Component.empty());
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendPlayerListFooter(@NotNull ComponentLike footer) {
        this.sendPlayerListFooter(footer.asComponent());
    }

    default public void sendPlayerListFooter(@NotNull Component footer) {
        this.sendPlayerListHeaderAndFooter(Component.empty(), footer);
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendPlayerListHeaderAndFooter(@NotNull ComponentLike header, @NotNull ComponentLike footer) {
        this.sendPlayerListHeaderAndFooter(header.asComponent(), footer.asComponent());
    }

    default public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void showTitle(@NotNull Title title) {
        Title.Times times = title.times();
        if (times != null) {
            this.sendTitlePart(TitlePart.TIMES, times);
        }
        this.sendTitlePart(TitlePart.SUBTITLE, title.subtitle());
        this.sendTitlePart(TitlePart.TITLE, title.title());
    }

    default public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
    }

    default public void clearTitle() {
    }

    default public void resetTitle() {
    }

    default public void showBossBar(@NotNull BossBar bar) {
    }

    default public void hideBossBar(@NotNull BossBar bar) {
    }

    default public void playSound(@NotNull Sound sound) {
    }

    default public void playSound(@NotNull Sound sound, double x2, double y2, double z2) {
    }

    default public void playSound(@NotNull Sound sound, @NotNull Sound.Emitter emitter) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void stopSound(@NotNull Sound sound) {
        this.stopSound(Objects.requireNonNull(sound, "sound").asStop());
    }

    default public void stopSound(@NotNull SoundStop stop) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void openBook(@NotNull Book.Builder book) {
        this.openBook(book.build());
    }

    default public void openBook(@NotNull Book book) {
    }
}

