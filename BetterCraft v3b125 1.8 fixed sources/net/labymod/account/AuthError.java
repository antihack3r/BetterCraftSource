/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.account;

import net.labymod.main.lang.LanguageManager;

public enum AuthError {
    NO_FILE("account_manager_error_no_launcher_profiles"),
    INVALID_CONTENTS("account_manager_error_invalid_launcher_profiles_contents"),
    EXCEPTION(null),
    NOT_READY_TO_PLAY("account_manager_error_not_ready_to_play"),
    FAILED_TO_REFRESH(null);

    private String errorMessage;

    public String toMessage(String ... format) {
        String translate = format.length == 0 && this.errorMessage != null ? LanguageManager.translate(this.errorMessage) : (format.length == 0 ? this.name() : format[0]);
        return String.valueOf(LanguageManager.translate("account_manager_error")) + " " + this.getErrorCode() + ": " + translate;
    }

    public int getErrorCode() {
        return this.ordinal() + 1;
    }

    private AuthError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

