/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.json.JSONString;
import org.json.JSONTokener;

public class JSONObject {
    private final Map<String, Object> map;
    public static final Object NULL = new Null();

    public JSONObject() {
        this.map = new HashMap<String, Object>();
    }

    public JSONObject(JSONObject jo, String[] names) {
        this(names.length);
        for (int i2 = 0; i2 < names.length; ++i2) {
            try {
                this.putOnce(names[i2], jo.opt(names[i2]));
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public JSONObject(JSONTokener x2) throws JSONException {
        this();
        if (x2.nextClean() != '{') {
            throw x2.syntaxError("A JSONObject text must begin with '{'");
        }
        block8: while (true) {
            char c2 = x2.nextClean();
            switch (c2) {
                case '\u0000': {
                    throw x2.syntaxError("A JSONObject text must end with '}'");
                }
                case '}': {
                    return;
                }
            }
            x2.back();
            String key = x2.nextValue().toString();
            c2 = x2.nextClean();
            if (c2 != ':') {
                throw x2.syntaxError("Expected a ':' after a key");
            }
            if (key != null) {
                if (this.opt(key) != null) {
                    throw x2.syntaxError("Duplicate key \"" + key + "\"");
                }
                Object value = x2.nextValue();
                if (value != null) {
                    this.put(key, value);
                }
            }
            switch (x2.nextClean()) {
                case ',': 
                case ';': {
                    if (x2.nextClean() == '}') {
                        return;
                    }
                    x2.back();
                    continue block8;
                }
                case '}': {
                    return;
                }
            }
            break;
        }
        throw x2.syntaxError("Expected a ',' or '}'");
    }

    public JSONObject(Map<?, ?> m2) {
        if (m2 == null) {
            this.map = new HashMap<String, Object>();
        } else {
            this.map = new HashMap<String, Object>(m2.size());
            for (Map.Entry<?, ?> e2 : m2.entrySet()) {
                Object value = e2.getValue();
                if (value == null) continue;
                this.map.put(String.valueOf(e2.getKey()), JSONObject.wrap(value));
            }
        }
    }

    public JSONObject(Object bean) {
        this();
        this.populateMap(bean);
    }

    public JSONObject(Object object, String[] names) {
        this(names.length);
        Class<?> c2 = object.getClass();
        for (int i2 = 0; i2 < names.length; ++i2) {
            String name = names[i2];
            try {
                this.putOpt(name, c2.getField(name).get(object));
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public JSONObject(String source) throws JSONException {
        this(new JSONTokener(source));
    }

    public JSONObject(String baseName, Locale locale) throws JSONException {
        this();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, Thread.currentThread().getContextClassLoader());
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key == null) continue;
            String[] path = key.split("\\.");
            int last = path.length - 1;
            JSONObject target = this;
            for (int i2 = 0; i2 < last; ++i2) {
                String segment = path[i2];
                JSONObject nextTarget = target.optJSONObject(segment);
                if (nextTarget == null) {
                    nextTarget = new JSONObject();
                    target.put(segment, nextTarget);
                }
                target = nextTarget;
            }
            target.put(path[last], bundle.getString(key));
        }
    }

    protected JSONObject(int initialCapacity) {
        this.map = new HashMap<String, Object>(initialCapacity);
    }

    public JSONObject accumulate(String key, Object value) throws JSONException {
        JSONObject.testValidity(value);
        Object object = this.opt(key);
        if (object == null) {
            this.put(key, value instanceof JSONArray ? new JSONArray().put(value) : value);
        } else if (object instanceof JSONArray) {
            ((JSONArray)object).put(value);
        } else {
            this.put(key, new JSONArray().put(object).put(value));
        }
        return this;
    }

    public JSONObject append(String key, Object value) throws JSONException {
        JSONObject.testValidity(value);
        Object object = this.opt(key);
        if (object == null) {
            this.put(key, new JSONArray().put(value));
        } else if (object instanceof JSONArray) {
            this.put(key, ((JSONArray)object).put(value));
        } else {
            throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
        }
        return this;
    }

    public static String doubleToString(double d2) {
        if (Double.isInfinite(d2) || Double.isNaN(d2)) {
            return "null";
        }
        String string = Double.toString(d2);
        if (string.indexOf(46) > 0 && string.indexOf(101) < 0 && string.indexOf(69) < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    public Object get(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        Object object = this.opt(key);
        if (object == null) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] not found.");
        }
        return object;
    }

    public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) throws JSONException {
        E val = this.optEnum(clazz, key);
        if (val == null) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not an enum of type " + JSONObject.quote(clazz.getSimpleName()) + ".");
        }
        return val;
    }

    public boolean getBoolean(String key) throws JSONException {
        Object object = this.get(key);
        if (object.equals(Boolean.FALSE) || object instanceof String && ((String)object).equalsIgnoreCase("false")) {
            return false;
        }
        if (object.equals(Boolean.TRUE) || object instanceof String && ((String)object).equalsIgnoreCase("true")) {
            return true;
        }
        throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a Boolean.");
    }

    public BigInteger getBigInteger(String key) throws JSONException {
        Object object = this.get(key);
        try {
            return new BigInteger(object.toString());
        }
        catch (Exception e2) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] could not be converted to BigInteger.", e2);
        }
    }

    public BigDecimal getBigDecimal(String key) throws JSONException {
        Object object = this.get(key);
        if (object instanceof BigDecimal) {
            return (BigDecimal)object;
        }
        try {
            return new BigDecimal(object.toString());
        }
        catch (Exception e2) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] could not be converted to BigDecimal.", e2);
        }
    }

    public double getDouble(String key) throws JSONException {
        Object object = this.get(key);
        try {
            return object instanceof Number ? ((Number)object).doubleValue() : Double.parseDouble(object.toString());
        }
        catch (Exception e2) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a number.", e2);
        }
    }

    public float getFloat(String key) throws JSONException {
        Object object = this.get(key);
        try {
            return object instanceof Number ? ((Number)object).floatValue() : Float.parseFloat(object.toString());
        }
        catch (Exception e2) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a number.", e2);
        }
    }

    public Number getNumber(String key) throws JSONException {
        Object object = this.get(key);
        try {
            if (object instanceof Number) {
                return (Number)object;
            }
            return JSONObject.stringToNumber(object.toString());
        }
        catch (Exception e2) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a number.", e2);
        }
    }

    public int getInt(String key) throws JSONException {
        Object object = this.get(key);
        try {
            return object instanceof Number ? ((Number)object).intValue() : Integer.parseInt((String)object);
        }
        catch (Exception e2) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not an int.", e2);
        }
    }

    public JSONArray getJSONArray(String key) throws JSONException {
        Object object = this.get(key);
        if (object instanceof JSONArray) {
            return (JSONArray)object;
        }
        throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a JSONArray.");
    }

    public JSONObject getJSONObject(String key) throws JSONException {
        Object object = this.get(key);
        if (object instanceof JSONObject) {
            return (JSONObject)object;
        }
        throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a JSONObject.");
    }

    public long getLong(String key) throws JSONException {
        Object object = this.get(key);
        try {
            return object instanceof Number ? ((Number)object).longValue() : Long.parseLong((String)object);
        }
        catch (Exception e2) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a long.", e2);
        }
    }

    public static String[] getNames(JSONObject jo) {
        int length = jo.length();
        if (length == 0) {
            return null;
        }
        return jo.keySet().toArray(new String[length]);
    }

    public static String[] getNames(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> klass = object.getClass();
        Field[] fields = klass.getFields();
        int length = fields.length;
        if (length == 0) {
            return null;
        }
        String[] names = new String[length];
        for (int i2 = 0; i2 < length; ++i2) {
            names[i2] = fields[i2].getName();
        }
        return names;
    }

    public String getString(String key) throws JSONException {
        Object object = this.get(key);
        if (object instanceof String) {
            return (String)object;
        }
        throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] not a string.");
    }

    public boolean has(String key) {
        return this.map.containsKey(key);
    }

    public JSONObject increment(String key) throws JSONException {
        Object value = this.opt(key);
        if (value == null) {
            this.put(key, 1);
        } else if (value instanceof BigInteger) {
            this.put(key, ((BigInteger)value).add(BigInteger.ONE));
        } else if (value instanceof BigDecimal) {
            this.put(key, ((BigDecimal)value).add(BigDecimal.ONE));
        } else if (value instanceof Integer) {
            this.put(key, (Integer)value + 1);
        } else if (value instanceof Long) {
            this.put(key, (Long)value + 1L);
        } else if (value instanceof Double) {
            this.put(key, (Double)value + 1.0);
        } else if (value instanceof Float) {
            this.put(key, ((Float)value).floatValue() + 1.0f);
        } else {
            throw new JSONException("Unable to increment [" + JSONObject.quote(key) + "].");
        }
        return this;
    }

    public boolean isNull(String key) {
        return NULL.equals(this.opt(key));
    }

    public Iterator<String> keys() {
        return this.keySet().iterator();
    }

    public Set<String> keySet() {
        return this.map.keySet();
    }

    protected Set<Map.Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }

    public int length() {
        return this.map.size();
    }

    public JSONArray names() {
        if (this.map.isEmpty()) {
            return null;
        }
        return new JSONArray(this.map.keySet());
    }

    public static String numberToString(Number number) throws JSONException {
        if (number == null) {
            throw new JSONException("Null pointer");
        }
        JSONObject.testValidity(number);
        String string = number.toString();
        if (string.indexOf(46) > 0 && string.indexOf(101) < 0 && string.indexOf(69) < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    public Object opt(String key) {
        return key == null ? null : this.map.get(key);
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, String key) {
        return this.optEnum(clazz, key, null);
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, String key, E defaultValue) {
        try {
            Object val = this.opt(key);
            if (NULL.equals(val)) {
                return defaultValue;
            }
            if (clazz.isAssignableFrom(val.getClass())) {
                Enum myE = (Enum)val;
                return (E)myE;
            }
            return Enum.valueOf(clazz, val.toString());
        }
        catch (IllegalArgumentException e2) {
            return defaultValue;
        }
        catch (NullPointerException e3) {
            return defaultValue;
        }
    }

    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }

    public boolean optBoolean(String key, boolean defaultValue) {
        Object val = this.opt(key);
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Boolean) {
            return (Boolean)val;
        }
        try {
            return this.getBoolean(key);
        }
        catch (Exception e2) {
            return defaultValue;
        }
    }

    public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
        Object val = this.opt(key);
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof BigDecimal) {
            return (BigDecimal)val;
        }
        if (val instanceof BigInteger) {
            return new BigDecimal((BigInteger)val);
        }
        if (val instanceof Double || val instanceof Float) {
            return new BigDecimal(((Number)val).doubleValue());
        }
        if (val instanceof Long || val instanceof Integer || val instanceof Short || val instanceof Byte) {
            return new BigDecimal(((Number)val).longValue());
        }
        try {
            return new BigDecimal(val.toString());
        }
        catch (Exception e2) {
            return defaultValue;
        }
    }

    public BigInteger optBigInteger(String key, BigInteger defaultValue) {
        Object val = this.opt(key);
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof BigInteger) {
            return (BigInteger)val;
        }
        if (val instanceof BigDecimal) {
            return ((BigDecimal)val).toBigInteger();
        }
        if (val instanceof Double || val instanceof Float) {
            return new BigDecimal(((Number)val).doubleValue()).toBigInteger();
        }
        if (val instanceof Long || val instanceof Integer || val instanceof Short || val instanceof Byte) {
            return BigInteger.valueOf(((Number)val).longValue());
        }
        try {
            String valStr = val.toString();
            if (JSONObject.isDecimalNotation(valStr)) {
                return new BigDecimal(valStr).toBigInteger();
            }
            return new BigInteger(valStr);
        }
        catch (Exception e2) {
            return defaultValue;
        }
    }

    public double optDouble(String key) {
        return this.optDouble(key, Double.NaN);
    }

    public double optDouble(String key, double defaultValue) {
        Object val = this.opt(key);
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number)val).doubleValue();
        }
        if (val instanceof String) {
            try {
                return Double.parseDouble((String)val);
            }
            catch (Exception e2) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public float optFloat(String key) {
        return this.optFloat(key, Float.NaN);
    }

    public float optFloat(String key, float defaultValue) {
        Object val = this.opt(key);
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number)val).floatValue();
        }
        if (val instanceof String) {
            try {
                return Float.parseFloat((String)val);
            }
            catch (Exception e2) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public int optInt(String key) {
        return this.optInt(key, 0);
    }

    public int optInt(String key, int defaultValue) {
        Object val = this.opt(key);
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number)val).intValue();
        }
        if (val instanceof String) {
            try {
                return new BigDecimal((String)val).intValue();
            }
            catch (Exception e2) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public JSONArray optJSONArray(String key) {
        Object o2 = this.opt(key);
        return o2 instanceof JSONArray ? (JSONArray)o2 : null;
    }

    public JSONObject optJSONObject(String key) {
        Object object = this.opt(key);
        return object instanceof JSONObject ? (JSONObject)object : null;
    }

    public long optLong(String key) {
        return this.optLong(key, 0L);
    }

    public long optLong(String key, long defaultValue) {
        Object val = this.opt(key);
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number)val).longValue();
        }
        if (val instanceof String) {
            try {
                return new BigDecimal((String)val).longValue();
            }
            catch (Exception e2) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public Number optNumber(String key) {
        return this.optNumber(key, null);
    }

    public Number optNumber(String key, Number defaultValue) {
        Object val = this.opt(key);
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return (Number)val;
        }
        if (val instanceof String) {
            try {
                return JSONObject.stringToNumber((String)val);
            }
            catch (Exception e2) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public String optString(String key) {
        return this.optString(key, "");
    }

    public String optString(String key, String defaultValue) {
        Object object = this.opt(key);
        return NULL.equals(object) ? defaultValue : object.toString();
    }

    private void populateMap(Object bean) {
        Method[] methods;
        Class<?> klass = bean.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        for (Method method : methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods()) {
            String key;
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || method.getParameterTypes().length != 0 || method.isBridge() || method.getReturnType() == Void.TYPE) continue;
            String name = method.getName();
            if (name.startsWith("get")) {
                if ("getClass".equals(name) || "getDeclaringClass".equals(name)) continue;
                key = name.substring(3);
            } else {
                if (!name.startsWith("is")) continue;
                key = name.substring(2);
            }
            if (key.length() <= 0 || !Character.isUpperCase(key.charAt(0))) continue;
            if (key.length() == 1) {
                key = key.toLowerCase(Locale.ROOT);
            } else if (!Character.isUpperCase(key.charAt(1))) {
                key = key.substring(0, 1).toLowerCase(Locale.ROOT) + key.substring(1);
            }
            try {
                Object result = method.invoke(bean, new Object[0]);
                if (result == null) continue;
                this.map.put(key, JSONObject.wrap(result));
                if (!(result instanceof Closeable)) continue;
                try {
                    ((Closeable)result).close();
                }
                catch (IOException iOException) {}
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (InvocationTargetException invocationTargetException) {
                // empty catch block
            }
        }
    }

    public JSONObject put(String key, boolean value) throws JSONException {
        this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONObject put(String key, Collection<?> value) throws JSONException {
        this.put(key, new JSONArray(value));
        return this;
    }

    public JSONObject put(String key, double value) throws JSONException {
        this.put(key, (Object)value);
        return this;
    }

    public JSONObject put(String key, float value) throws JSONException {
        this.put(key, Float.valueOf(value));
        return this;
    }

    public JSONObject put(String key, int value) throws JSONException {
        this.put(key, (Object)value);
        return this;
    }

    public JSONObject put(String key, long value) throws JSONException {
        this.put(key, (Object)value);
        return this;
    }

    public JSONObject put(String key, Map<?, ?> value) throws JSONException {
        this.put(key, new JSONObject(value));
        return this;
    }

    public JSONObject put(String key, Object value) throws JSONException {
        if (key == null) {
            throw new NullPointerException("Null key.");
        }
        if (value != null) {
            JSONObject.testValidity(value);
            this.map.put(key, value);
        } else {
            this.remove(key);
        }
        return this;
    }

    public JSONObject putOnce(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            if (this.opt(key) != null) {
                throw new JSONException("Duplicate key \"" + key + "\"");
            }
            this.put(key, value);
        }
        return this;
    }

    public JSONObject putOpt(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            this.put(key, value);
        }
        return this;
    }

    public Object query(String jsonPointer) {
        return this.query(new JSONPointer(jsonPointer));
    }

    public Object query(JSONPointer jsonPointer) {
        return jsonPointer.queryFrom(this);
    }

    public Object optQuery(String jsonPointer) {
        return this.optQuery(new JSONPointer(jsonPointer));
    }

    public Object optQuery(JSONPointer jsonPointer) {
        try {
            return jsonPointer.queryFrom(this);
        }
        catch (JSONPointerException e2) {
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String quote(String string) {
        StringWriter sw2 = new StringWriter();
        StringBuffer stringBuffer = sw2.getBuffer();
        synchronized (stringBuffer) {
            try {
                return JSONObject.quote(string, sw2).toString();
            }
            catch (IOException ignored) {
                return "";
            }
        }
    }

    public static Writer quote(String string, Writer w2) throws IOException {
        if (string == null || string.length() == 0) {
            w2.write("\"\"");
            return w2;
        }
        char c2 = '\u0000';
        int len = string.length();
        w2.write(34);
        block9: for (int i2 = 0; i2 < len; ++i2) {
            char b2 = c2;
            c2 = string.charAt(i2);
            switch (c2) {
                case '\"': 
                case '\\': {
                    w2.write(92);
                    w2.write(c2);
                    continue block9;
                }
                case '/': {
                    if (b2 == '<') {
                        w2.write(92);
                    }
                    w2.write(c2);
                    continue block9;
                }
                case '\b': {
                    w2.write("\\b");
                    continue block9;
                }
                case '\t': {
                    w2.write("\\t");
                    continue block9;
                }
                case '\n': {
                    w2.write("\\n");
                    continue block9;
                }
                case '\f': {
                    w2.write("\\f");
                    continue block9;
                }
                case '\r': {
                    w2.write("\\r");
                    continue block9;
                }
                default: {
                    if (c2 < ' ' || c2 >= '\u0080' && c2 < '\u00a0' || c2 >= '\u2000' && c2 < '\u2100') {
                        w2.write("\\u");
                        String hhhh = Integer.toHexString(c2);
                        w2.write("0000", 0, 4 - hhhh.length());
                        w2.write(hhhh);
                        continue block9;
                    }
                    w2.write(c2);
                }
            }
        }
        w2.write(34);
        return w2;
    }

    public Object remove(String key) {
        return this.map.remove(key);
    }

    public boolean similar(Object other) {
        try {
            if (!(other instanceof JSONObject)) {
                return false;
            }
            if (!this.keySet().equals(((JSONObject)other).keySet())) {
                return false;
            }
            for (Map.Entry<String, Object> entry : this.entrySet()) {
                Object valueOther;
                String name = entry.getKey();
                Object valueThis = entry.getValue();
                if (valueThis == (valueOther = ((JSONObject)other).get(name))) {
                    return true;
                }
                if (valueThis == null) {
                    return false;
                }
                if (!(valueThis instanceof JSONObject ? !((JSONObject)valueThis).similar(valueOther) : (valueThis instanceof JSONArray ? !((JSONArray)valueThis).similar(valueOther) : !valueThis.equals(valueOther)))) continue;
                return false;
            }
            return true;
        }
        catch (Throwable exception) {
            return false;
        }
    }

    protected static boolean isDecimalNotation(String val) {
        return val.indexOf(46) > -1 || val.indexOf(101) > -1 || val.indexOf(69) > -1 || "-0".equals(val);
    }

    protected static Number stringToNumber(String val) throws NumberFormatException {
        char initial = val.charAt(0);
        if (initial >= '0' && initial <= '9' || initial == '-') {
            if (JSONObject.isDecimalNotation(val)) {
                if (val.length() > 14) {
                    return new BigDecimal(val);
                }
                Double d2 = Double.valueOf(val);
                if (d2.isInfinite() || d2.isNaN()) {
                    return new BigDecimal(val);
                }
                return d2;
            }
            BigInteger bi2 = new BigInteger(val);
            if (bi2.bitLength() <= 31) {
                return bi2.intValue();
            }
            if (bi2.bitLength() <= 63) {
                return bi2.longValue();
            }
            return bi2;
        }
        throw new NumberFormatException("val [" + val + "] is not a valid number.");
    }

    public static Object stringToValue(String string) {
        if (string.equals("")) {
            return string;
        }
        if (string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("null")) {
            return NULL;
        }
        char initial = string.charAt(0);
        if (initial >= '0' && initial <= '9' || initial == '-') {
            try {
                if (JSONObject.isDecimalNotation(string)) {
                    Double d2 = Double.valueOf(string);
                    if (!d2.isInfinite() && !d2.isNaN()) {
                        return d2;
                    }
                } else {
                    Long myLong = Long.valueOf(string);
                    if (string.equals(myLong.toString())) {
                        if (myLong == (long)myLong.intValue()) {
                            return myLong.intValue();
                        }
                        return myLong;
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return string;
    }

    public static void testValidity(Object o2) throws JSONException {
        if (o2 != null && (o2 instanceof Double ? ((Double)o2).isInfinite() || ((Double)o2).isNaN() : o2 instanceof Float && (((Float)o2).isInfinite() || ((Float)o2).isNaN()))) {
            throw new JSONException("JSON does not allow non-finite numbers.");
        }
    }

    public JSONArray toJSONArray(JSONArray names) throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        JSONArray ja2 = new JSONArray();
        for (int i2 = 0; i2 < names.length(); ++i2) {
            ja2.put(this.opt(names.getString(i2)));
        }
        return ja2;
    }

    public String toString() {
        try {
            return this.toString(0);
        }
        catch (Exception e2) {
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString(int indentFactor) throws JSONException {
        StringWriter w2 = new StringWriter();
        StringBuffer stringBuffer = w2.getBuffer();
        synchronized (stringBuffer) {
            return this.write(w2, indentFactor, 0).toString();
        }
    }

    public static String valueToString(Object value) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof JSONString) {
            String object;
            try {
                object = ((JSONString)value).toJSONString();
            }
            catch (Exception e2) {
                throw new JSONException(e2);
            }
            if (object instanceof String) {
                return object;
            }
            throw new JSONException("Bad value from toJSONString: " + object);
        }
        if (value instanceof Number) {
            String numberAsString = JSONObject.numberToString((Number)value);
            try {
                BigDecimal unused = new BigDecimal(numberAsString);
                return numberAsString;
            }
            catch (NumberFormatException ex2) {
                return JSONObject.quote(numberAsString);
            }
        }
        if (value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray) {
            return value.toString();
        }
        if (value instanceof Map) {
            Map map = (Map)value;
            return new JSONObject(map).toString();
        }
        if (value instanceof Collection) {
            Collection coll = (Collection)value;
            return new JSONArray(coll).toString();
        }
        if (value.getClass().isArray()) {
            return new JSONArray(value).toString();
        }
        if (value instanceof Enum) {
            return JSONObject.quote(((Enum)value).name());
        }
        return JSONObject.quote(value.toString());
    }

    public static Object wrap(Object object) {
        try {
            String objectPackageName;
            if (object == null) {
                return NULL;
            }
            if (object instanceof JSONObject || object instanceof JSONArray || NULL.equals(object) || object instanceof JSONString || object instanceof Byte || object instanceof Character || object instanceof Short || object instanceof Integer || object instanceof Long || object instanceof Boolean || object instanceof Float || object instanceof Double || object instanceof String || object instanceof BigInteger || object instanceof BigDecimal || object instanceof Enum) {
                return object;
            }
            if (object instanceof Collection) {
                Collection coll = (Collection)object;
                return new JSONArray(coll);
            }
            if (object.getClass().isArray()) {
                return new JSONArray(object);
            }
            if (object instanceof Map) {
                Map map = (Map)object;
                return new JSONObject(map);
            }
            Package objectPackage = object.getClass().getPackage();
            String string = objectPackageName = objectPackage != null ? objectPackage.getName() : "";
            if (objectPackageName.startsWith("java.") || objectPackageName.startsWith("javax.") || object.getClass().getClassLoader() == null) {
                return object.toString();
            }
            return new JSONObject(object);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public Writer write(Writer writer) throws JSONException {
        return this.write(writer, 0, 0);
    }

    static final Writer writeValue(Writer writer, Object value, int indentFactor, int indent) throws JSONException, IOException {
        if (value == null || value.equals(null)) {
            writer.write("null");
        } else if (value instanceof JSONString) {
            String o2;
            try {
                o2 = ((JSONString)value).toJSONString();
            }
            catch (Exception e2) {
                throw new JSONException(e2);
            }
            writer.write(o2 != null ? o2.toString() : JSONObject.quote(value.toString()));
        } else if (value instanceof Number) {
            String numberAsString = JSONObject.numberToString((Number)value);
            try {
                BigDecimal testNum = new BigDecimal(numberAsString);
                writer.write(numberAsString);
            }
            catch (NumberFormatException ex2) {
                JSONObject.quote(numberAsString, writer);
            }
        } else if (value instanceof Boolean) {
            writer.write(value.toString());
        } else if (value instanceof Enum) {
            writer.write(JSONObject.quote(((Enum)value).name()));
        } else if (value instanceof JSONObject) {
            ((JSONObject)value).write(writer, indentFactor, indent);
        } else if (value instanceof JSONArray) {
            ((JSONArray)value).write(writer, indentFactor, indent);
        } else if (value instanceof Map) {
            Map map = (Map)value;
            new JSONObject(map).write(writer, indentFactor, indent);
        } else if (value instanceof Collection) {
            Collection coll = (Collection)value;
            new JSONArray(coll).write(writer, indentFactor, indent);
        } else if (value.getClass().isArray()) {
            new JSONArray(value).write(writer, indentFactor, indent);
        } else {
            JSONObject.quote(value.toString(), writer);
        }
        return writer;
    }

    static final void indent(Writer writer, int indent) throws IOException {
        for (int i2 = 0; i2 < indent; ++i2) {
            writer.write(32);
        }
    }

    public Writer write(Writer writer, int indentFactor, int indent) throws JSONException {
        try {
            boolean commanate = false;
            int length = this.length();
            writer.write(123);
            if (length == 1) {
                Map.Entry<String, Object> entry = this.entrySet().iterator().next();
                String key = entry.getKey();
                writer.write(JSONObject.quote(key));
                writer.write(58);
                if (indentFactor > 0) {
                    writer.write(32);
                }
                try {
                    JSONObject.writeValue(writer, entry.getValue(), indentFactor, indent);
                }
                catch (Exception e2) {
                    throw new JSONException("Unable to write JSONObject value for key: " + key, e2);
                }
            }
            if (length != 0) {
                int newindent = indent + indentFactor;
                for (Map.Entry<String, Object> entry : this.entrySet()) {
                    if (commanate) {
                        writer.write(44);
                    }
                    if (indentFactor > 0) {
                        writer.write(10);
                    }
                    JSONObject.indent(writer, newindent);
                    String key = entry.getKey();
                    writer.write(JSONObject.quote(key));
                    writer.write(58);
                    if (indentFactor > 0) {
                        writer.write(32);
                    }
                    try {
                        JSONObject.writeValue(writer, entry.getValue(), indentFactor, newindent);
                    }
                    catch (Exception e3) {
                        throw new JSONException("Unable to write JSONObject value for key: " + key, e3);
                    }
                    commanate = true;
                }
                if (indentFactor > 0) {
                    writer.write(10);
                }
                JSONObject.indent(writer, indent);
            }
            writer.write(125);
            return writer;
        }
        catch (IOException exception) {
            throw new JSONException(exception);
        }
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> results = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : this.entrySet()) {
            Object value = entry.getValue() == null || NULL.equals(entry.getValue()) ? null : (entry.getValue() instanceof JSONObject ? ((JSONObject)entry.getValue()).toMap() : (entry.getValue() instanceof JSONArray ? ((JSONArray)entry.getValue()).toList() : entry.getValue()));
            results.put(entry.getKey(), value);
        }
        return results;
    }

    private static final class Null {
        private Null() {
        }

        protected final Object clone() {
            return this;
        }

        public boolean equals(Object object) {
            return object == null || object == this;
        }

        public int hashCode() {
            return 0;
        }

        public String toString() {
            return "null";
        }
    }
}

