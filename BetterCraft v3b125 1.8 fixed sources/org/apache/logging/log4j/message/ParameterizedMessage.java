/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.message;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.message.Message;

public class ParameterizedMessage
implements Message {
    public static final String RECURSION_PREFIX = "[...";
    public static final String RECURSION_SUFFIX = "...]";
    public static final String ERROR_PREFIX = "[!!!";
    public static final String ERROR_SEPARATOR = "=>";
    public static final String ERROR_MSG_SEPARATOR = ":";
    public static final String ERROR_SUFFIX = "!!!]";
    private static final long serialVersionUID = -665975803997290697L;
    private static final int HASHVAL = 31;
    private static final char DELIM_START = '{';
    private static final char DELIM_STOP = '}';
    private static final char ESCAPE_CHAR = '\\';
    private final String messagePattern;
    private final String[] stringArgs;
    private transient Object[] argArray;
    private transient String formattedMessage;
    private transient Throwable throwable;

    public ParameterizedMessage(String messagePattern, String[] stringArgs, Throwable throwable) {
        this.messagePattern = messagePattern;
        this.stringArgs = stringArgs;
        this.throwable = throwable;
    }

    public ParameterizedMessage(String messagePattern, Object[] objectArgs, Throwable throwable) {
        this.messagePattern = messagePattern;
        this.throwable = throwable;
        this.stringArgs = this.parseArguments(objectArgs);
    }

    public ParameterizedMessage(String messagePattern, Object[] arguments) {
        this.messagePattern = messagePattern;
        this.stringArgs = this.parseArguments(arguments);
    }

    public ParameterizedMessage(String messagePattern, Object arg2) {
        this(messagePattern, new Object[]{arg2});
    }

    public ParameterizedMessage(String messagePattern, Object arg1, Object arg2) {
        this(messagePattern, new Object[]{arg1, arg2});
    }

    private String[] parseArguments(Object[] arguments) {
        String[] strArgs;
        if (arguments == null) {
            return null;
        }
        int argsCount = ParameterizedMessage.countArgumentPlaceholders(this.messagePattern);
        int resultArgCount = arguments.length;
        if (argsCount < arguments.length && this.throwable == null && arguments[arguments.length - 1] instanceof Throwable) {
            this.throwable = (Throwable)arguments[arguments.length - 1];
            --resultArgCount;
        }
        this.argArray = new Object[resultArgCount];
        for (int i2 = 0; i2 < resultArgCount; ++i2) {
            this.argArray[i2] = arguments[i2];
        }
        if (argsCount == 1 && this.throwable == null && arguments.length > 1) {
            strArgs = new String[]{ParameterizedMessage.deepToString(arguments)};
        } else {
            strArgs = new String[resultArgCount];
            for (int i3 = 0; i3 < strArgs.length; ++i3) {
                strArgs[i3] = ParameterizedMessage.deepToString(arguments[i3]);
            }
        }
        return strArgs;
    }

    @Override
    public String getFormattedMessage() {
        if (this.formattedMessage == null) {
            this.formattedMessage = this.formatMessage(this.messagePattern, this.stringArgs);
        }
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

    protected String formatMessage(String msgPattern, String[] sArgs) {
        return ParameterizedMessage.format(msgPattern, sArgs);
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        ParameterizedMessage that = (ParameterizedMessage)o2;
        if (this.messagePattern != null ? !this.messagePattern.equals(that.messagePattern) : that.messagePattern != null) {
            return false;
        }
        return Arrays.equals(this.stringArgs, that.stringArgs);
    }

    public int hashCode() {
        int result = this.messagePattern != null ? this.messagePattern.hashCode() : 0;
        result = 31 * result + (this.stringArgs != null ? Arrays.hashCode(this.stringArgs) : 0);
        return result;
    }

    public static String format(String messagePattern, Object[] arguments) {
        if (messagePattern == null || arguments == null || arguments.length == 0) {
            return messagePattern;
        }
        StringBuilder result = new StringBuilder();
        int escapeCounter = 0;
        int currentArgument = 0;
        for (int i2 = 0; i2 < messagePattern.length(); ++i2) {
            char curChar = messagePattern.charAt(i2);
            if (curChar == '\\') {
                ++escapeCounter;
                continue;
            }
            if (curChar == '{' && i2 < messagePattern.length() - 1 && messagePattern.charAt(i2 + 1) == '}') {
                int escapedEscapes = escapeCounter / 2;
                for (int j2 = 0; j2 < escapedEscapes; ++j2) {
                    result.append('\\');
                }
                if (escapeCounter % 2 == 1) {
                    result.append('{');
                    result.append('}');
                } else {
                    if (currentArgument < arguments.length) {
                        result.append(arguments[currentArgument]);
                    } else {
                        result.append('{').append('}');
                    }
                    ++currentArgument;
                }
                ++i2;
                escapeCounter = 0;
                continue;
            }
            if (escapeCounter > 0) {
                for (int j3 = 0; j3 < escapeCounter; ++j3) {
                    result.append('\\');
                }
                escapeCounter = 0;
            }
            result.append(curChar);
        }
        return result.toString();
    }

    public static int countArgumentPlaceholders(String messagePattern) {
        if (messagePattern == null) {
            return 0;
        }
        int delim = messagePattern.indexOf(123);
        if (delim == -1) {
            return 0;
        }
        int result = 0;
        boolean isEscaped = false;
        for (int i2 = 0; i2 < messagePattern.length(); ++i2) {
            char curChar = messagePattern.charAt(i2);
            if (curChar == '\\') {
                isEscaped = !isEscaped;
                continue;
            }
            if (curChar == '{') {
                if (!isEscaped && i2 < messagePattern.length() - 1 && messagePattern.charAt(i2 + 1) == '}') {
                    ++result;
                    ++i2;
                }
                isEscaped = false;
                continue;
            }
            isEscaped = false;
        }
        return result;
    }

    public static String deepToString(Object o2) {
        if (o2 == null) {
            return null;
        }
        if (o2 instanceof String) {
            return (String)o2;
        }
        StringBuilder str = new StringBuilder();
        HashSet<String> dejaVu = new HashSet<String>();
        ParameterizedMessage.recursiveDeepToString(o2, str, dejaVu);
        return str.toString();
    }

    private static void recursiveDeepToString(Object o2, StringBuilder str, Set<String> dejaVu) {
        if (o2 == null) {
            str.append("null");
            return;
        }
        if (o2 instanceof String) {
            str.append(o2);
            return;
        }
        Class<?> oClass = o2.getClass();
        if (oClass.isArray()) {
            if (oClass == byte[].class) {
                str.append(Arrays.toString((byte[])o2));
            } else if (oClass == short[].class) {
                str.append(Arrays.toString((short[])o2));
            } else if (oClass == int[].class) {
                str.append(Arrays.toString((int[])o2));
            } else if (oClass == long[].class) {
                str.append(Arrays.toString((long[])o2));
            } else if (oClass == float[].class) {
                str.append(Arrays.toString((float[])o2));
            } else if (oClass == double[].class) {
                str.append(Arrays.toString((double[])o2));
            } else if (oClass == boolean[].class) {
                str.append(Arrays.toString((boolean[])o2));
            } else if (oClass == char[].class) {
                str.append(Arrays.toString((char[])o2));
            } else {
                String id2 = ParameterizedMessage.identityToString(o2);
                if (dejaVu.contains(id2)) {
                    str.append(RECURSION_PREFIX).append(id2).append(RECURSION_SUFFIX);
                } else {
                    dejaVu.add(id2);
                    Object[] oArray = (Object[])o2;
                    str.append("[");
                    boolean first = true;
                    for (Object current : oArray) {
                        if (first) {
                            first = false;
                        } else {
                            str.append(", ");
                        }
                        ParameterizedMessage.recursiveDeepToString(current, str, new HashSet<String>(dejaVu));
                    }
                    str.append("]");
                }
            }
        } else if (o2 instanceof Map) {
            String id3 = ParameterizedMessage.identityToString(o2);
            if (dejaVu.contains(id3)) {
                str.append(RECURSION_PREFIX).append(id3).append(RECURSION_SUFFIX);
            } else {
                dejaVu.add(id3);
                Map oMap = (Map)o2;
                str.append("{");
                boolean isFirst = true;
                Iterator i$ = oMap.entrySet().iterator();
                while (i$.hasNext()) {
                    Map.Entry o1;
                    Map.Entry current = o1 = i$.next();
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        str.append(", ");
                    }
                    Object key = current.getKey();
                    Object value = current.getValue();
                    ParameterizedMessage.recursiveDeepToString(key, str, new HashSet<String>(dejaVu));
                    str.append("=");
                    ParameterizedMessage.recursiveDeepToString(value, str, new HashSet<String>(dejaVu));
                }
                str.append("}");
            }
        } else if (o2 instanceof Collection) {
            String id4 = ParameterizedMessage.identityToString(o2);
            if (dejaVu.contains(id4)) {
                str.append(RECURSION_PREFIX).append(id4).append(RECURSION_SUFFIX);
            } else {
                dejaVu.add(id4);
                Collection oCol = (Collection)o2;
                str.append("[");
                boolean isFirst = true;
                for (Object anOCol : oCol) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        str.append(", ");
                    }
                    ParameterizedMessage.recursiveDeepToString(anOCol, str, new HashSet<String>(dejaVu));
                }
                str.append("]");
            }
        } else if (o2 instanceof Date) {
            Date date = (Date)o2;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            str.append(format.format(date));
        } else {
            try {
                str.append(o2.toString());
            }
            catch (Throwable t2) {
                str.append(ERROR_PREFIX);
                str.append(ParameterizedMessage.identityToString(o2));
                str.append(ERROR_SEPARATOR);
                String msg = t2.getMessage();
                String className = t2.getClass().getName();
                str.append(className);
                if (!className.equals(msg)) {
                    str.append(ERROR_MSG_SEPARATOR);
                    str.append(msg);
                }
                str.append(ERROR_SUFFIX);
            }
        }
    }

    public static String identityToString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
    }

    public String toString() {
        return "ParameterizedMessage[messagePattern=" + this.messagePattern + ", stringArgs=" + Arrays.toString(this.stringArgs) + ", throwable=" + this.throwable + "]";
    }
}

