// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.management;

import java.text.ParseException;
import com.google.gson.JsonObject;
import java.util.Date;
import java.text.SimpleDateFormat;

public abstract class UserListEntryBan<T> extends UserListEntry<T>
{
    public static final SimpleDateFormat DATE_FORMAT;
    protected final Date banStartDate;
    protected final String bannedBy;
    protected final Date banEndDate;
    protected final String reason;
    
    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    }
    
    public UserListEntryBan(final T valueIn, final Date startDate, final String banner, final Date endDate, final String banReason) {
        super(valueIn);
        this.banStartDate = ((startDate == null) ? new Date() : startDate);
        this.bannedBy = ((banner == null) ? "(Unknown)" : banner);
        this.banEndDate = endDate;
        this.reason = ((banReason == null) ? "Banned by an operator." : banReason);
    }
    
    protected UserListEntryBan(final T valueIn, final JsonObject json) {
        super(valueIn, json);
        Date date;
        try {
            date = (json.has("created") ? UserListEntryBan.DATE_FORMAT.parse(json.get("created").getAsString()) : new Date());
        }
        catch (final ParseException var7) {
            date = new Date();
        }
        this.banStartDate = date;
        this.bannedBy = (json.has("source") ? json.get("source").getAsString() : "(Unknown)");
        Date date2;
        try {
            date2 = (json.has("expires") ? UserListEntryBan.DATE_FORMAT.parse(json.get("expires").getAsString()) : null);
        }
        catch (final ParseException var8) {
            date2 = null;
        }
        this.banEndDate = date2;
        this.reason = (json.has("reason") ? json.get("reason").getAsString() : "Banned by an operator.");
    }
    
    public Date getBanEndDate() {
        return this.banEndDate;
    }
    
    public String getBanReason() {
        return this.reason;
    }
    
    @Override
    boolean hasBanExpired() {
        return this.banEndDate != null && this.banEndDate.before(new Date());
    }
    
    @Override
    protected void onSerialization(final JsonObject data) {
        data.addProperty("created", UserListEntryBan.DATE_FORMAT.format(this.banStartDate));
        data.addProperty("source", this.bannedBy);
        data.addProperty("expires", (this.banEndDate == null) ? "forever" : UserListEntryBan.DATE_FORMAT.format(this.banEndDate));
        data.addProperty("reason", this.reason);
    }
}
