/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.logging.log4j.message.FormattedMessage;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;

public class LocalizedMessage
implements Message,
LoggerNameAwareMessage {
    private static final long serialVersionUID = 3893703791567290742L;
    private String bundleId;
    private transient ResourceBundle bundle;
    private Locale locale;
    private transient StatusLogger logger = StatusLogger.getLogger();
    private String loggerName;
    private String messagePattern;
    private String[] stringArgs;
    private transient Object[] argArray;
    private String formattedMessage;
    private transient Throwable throwable;

    public LocalizedMessage(String messagePattern, Object[] arguments) {
        this((ResourceBundle)null, (Locale)null, messagePattern, arguments);
    }

    public LocalizedMessage(String bundleId, String key, Object[] arguments) {
        this(bundleId, (Locale)null, key, arguments);
    }

    public LocalizedMessage(ResourceBundle bundle, String key, Object[] arguments) {
        this(bundle, (Locale)null, key, arguments);
    }

    public LocalizedMessage(String bundleId, Locale locale, String key, Object[] arguments) {
        this.messagePattern = key;
        this.argArray = arguments;
        this.throwable = null;
        this.setup(bundleId, null, locale);
    }

    public LocalizedMessage(ResourceBundle bundle, Locale locale, String key, Object[] arguments) {
        this.messagePattern = key;
        this.argArray = arguments;
        this.throwable = null;
        this.setup(null, bundle, locale);
    }

    public LocalizedMessage(Locale locale, String key, Object[] arguments) {
        this((ResourceBundle)null, locale, key, arguments);
    }

    public LocalizedMessage(String messagePattern, Object arg2) {
        this((ResourceBundle)null, (Locale)null, messagePattern, new Object[]{arg2});
    }

    public LocalizedMessage(String bundleId, String key, Object arg2) {
        this(bundleId, (Locale)null, key, new Object[]{arg2});
    }

    public LocalizedMessage(ResourceBundle bundle, String key, Object arg2) {
        this(bundle, (Locale)null, key, new Object[]{arg2});
    }

    public LocalizedMessage(String bundleId, Locale locale, String key, Object arg2) {
        this(bundleId, locale, key, new Object[]{arg2});
    }

    public LocalizedMessage(ResourceBundle bundle, Locale locale, String key, Object arg2) {
        this(bundle, locale, key, new Object[]{arg2});
    }

    public LocalizedMessage(Locale locale, String key, Object arg2) {
        this((ResourceBundle)null, locale, key, new Object[]{arg2});
    }

    public LocalizedMessage(String messagePattern, Object arg1, Object arg2) {
        this((ResourceBundle)null, (Locale)null, messagePattern, new Object[]{arg1, arg2});
    }

    public LocalizedMessage(String bundleId, String key, Object arg1, Object arg2) {
        this(bundleId, (Locale)null, key, new Object[]{arg1, arg2});
    }

    public LocalizedMessage(ResourceBundle bundle, String key, Object arg1, Object arg2) {
        this(bundle, (Locale)null, key, new Object[]{arg1, arg2});
    }

    public LocalizedMessage(String bundleId, Locale locale, String key, Object arg1, Object arg2) {
        this(bundleId, locale, key, new Object[]{arg1, arg2});
    }

    public LocalizedMessage(ResourceBundle bundle, Locale locale, String key, Object arg1, Object arg2) {
        this(bundle, locale, key, new Object[]{arg1, arg2});
    }

    public LocalizedMessage(Locale locale, String key, Object arg1, Object arg2) {
        this((ResourceBundle)null, locale, key, new Object[]{arg1, arg2});
    }

    @Override
    public void setLoggerName(String name) {
        this.loggerName = name;
    }

    @Override
    public String getLoggerName() {
        return this.loggerName;
    }

    private void setup(String bundleId, ResourceBundle bundle, Locale locale) {
        this.bundleId = bundleId;
        this.bundle = bundle;
        this.locale = locale;
    }

    @Override
    public String getFormattedMessage() {
        if (this.formattedMessage != null) {
            return this.formattedMessage;
        }
        ResourceBundle bundle = this.bundle;
        if (bundle == null) {
            bundle = this.bundleId != null ? this.getBundle(this.bundleId, this.locale, false) : this.getBundle(this.loggerName, this.locale, true);
        }
        String messagePattern = this.getFormat();
        String msgPattern = bundle == null || !bundle.containsKey(messagePattern) ? messagePattern : bundle.getString(messagePattern);
        Object[] array = this.argArray == null ? this.stringArgs : this.argArray;
        FormattedMessage msg = new FormattedMessage(msgPattern, array);
        this.formattedMessage = msg.getFormattedMessage();
        this.throwable = msg.getThrowable();
        return this.formattedMessage;
    }

    @Override
    public String getFormat() {
        return this.messagePattern;
    }

    @Override
    public Object[] getParameters() {
        if (this.argArray != null) {
            return this.argArray;
        }
        return this.stringArgs;
    }

    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }

    protected ResourceBundle getBundle(String key, Locale locale, boolean loop) {
        int i2;
        ResourceBundle rb2;
        block7: {
            rb2 = null;
            if (key == null) {
                return null;
            }
            try {
                rb2 = locale != null ? ResourceBundle.getBundle(key, locale) : ResourceBundle.getBundle(key);
            }
            catch (MissingResourceException ex2) {
                if (loop) break block7;
                this.logger.debug("Unable to locate ResourceBundle " + key);
                return null;
            }
        }
        String substr = key;
        while (rb2 == null && (i2 = substr.lastIndexOf(46)) > 0) {
            substr = substr.substring(0, i2);
            try {
                if (locale != null) {
                    rb2 = ResourceBundle.getBundle(substr, locale);
                    continue;
                }
                rb2 = ResourceBundle.getBundle(substr);
            }
            catch (MissingResourceException ex3) {
                this.logger.debug("Unable to locate ResourceBundle " + substr);
            }
        }
        return rb2;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.getFormattedMessage();
        out.writeUTF(this.formattedMessage);
        out.writeUTF(this.messagePattern);
        out.writeUTF(this.bundleId);
        out.writeInt(this.argArray.length);
        this.stringArgs = new String[this.argArray.length];
        int i2 = 0;
        for (Object obj : this.argArray) {
            this.stringArgs[i2] = obj.toString();
            ++i2;
        }
    }

    private void readObject(ObjectInputStream in2) throws IOException, ClassNotFoundException {
        in2.defaultReadObject();
        this.formattedMessage = in2.readUTF();
        this.messagePattern = in2.readUTF();
        this.bundleId = in2.readUTF();
        int length = in2.readInt();
        this.stringArgs = new String[length];
        for (int i2 = 0; i2 < length; ++i2) {
            this.stringArgs[i2] = in2.readUTF();
        }
        this.logger = StatusLogger.getLogger();
        this.bundle = null;
        this.argArray = null;
    }
}

