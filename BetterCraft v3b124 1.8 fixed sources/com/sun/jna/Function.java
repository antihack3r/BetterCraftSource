/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  [Lcom.sun.jna.WString;
 *  [Lcom.sun.jna.Pointer;
 *  [Lcom.sun.jna.NativeMapped;
 *  [Lcom.sun.jna.Structure;
 *  [Ljava.lang.String;
 */
package com.sun.jna;

import [Lcom.sun.jna.CfrRenamed5706;
import [Lcom.sun.jna.CfrRenamed5715;
import [Lcom.sun.jna.CfrRenamed5716;
import [Lcom.sun.jna.CfrRenamed5718;
import [Ljava.lang.CfrRenamed5689;
import com.sun.jna.Callback;
import com.sun.jna.CallbackReference;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.FunctionParameterContext;
import com.sun.jna.FunctionResultContext;
import com.sun.jna.Memory;
import com.sun.jna.MethodParameterContext;
import com.sun.jna.MethodResultContext;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.NativeString;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class Function
extends Pointer {
    public static final int MAX_NARGS = 256;
    public static final int C_CONVENTION = 0;
    public static final int ALT_CONVENTION = 1;
    private static final int MASK_CC = 3;
    public static final int THROW_LAST_ERROR = 4;
    static final Integer INTEGER_TRUE = new Integer(-1);
    static final Integer INTEGER_FALSE = new Integer(0);
    private NativeLibrary library;
    private final String functionName;
    int callFlags;
    final Map options;
    static final String OPTION_INVOKING_METHOD = "invoking-method";
    static /* synthetic */ Class array$Lcom$sun$jna$Structure$ByReference;

    public static Function getFunction(String libraryName, String functionName) {
        return NativeLibrary.getInstance(libraryName).getFunction(functionName);
    }

    public static Function getFunction(String libraryName, String functionName, int callFlags) {
        return NativeLibrary.getInstance(libraryName).getFunction(functionName, callFlags);
    }

    public static Function getFunction(Pointer p2) {
        return Function.getFunction(p2, 0);
    }

    public static Function getFunction(Pointer p2, int callFlags) {
        return new Function(p2, callFlags);
    }

    Function(NativeLibrary library, String functionName, int callFlags) {
        this.checkCallingConvention(callFlags & 3);
        if (functionName == null) {
            throw new NullPointerException("Function name must not be null");
        }
        this.library = library;
        this.functionName = functionName;
        this.callFlags = callFlags;
        this.options = library.options;
        try {
            this.peer = library.getSymbolAddress(functionName);
        }
        catch (UnsatisfiedLinkError e2) {
            throw new UnsatisfiedLinkError("Error looking up function '" + functionName + "': " + e2.getMessage());
        }
    }

    Function(Pointer functionAddress, int callFlags) {
        this.checkCallingConvention(callFlags & 3);
        if (functionAddress == null || functionAddress.peer == 0L) {
            throw new NullPointerException("Function address may not be null");
        }
        this.functionName = functionAddress.toString();
        this.callFlags = callFlags;
        this.peer = functionAddress.peer;
        this.options = Collections.EMPTY_MAP;
    }

    private void checkCallingConvention(int convention) throws IllegalArgumentException {
        switch (convention) {
            case 0: 
            case 1: {
                break;
            }
            default: {
                throw new IllegalArgumentException("Unrecognized calling convention: " + convention);
            }
        }
    }

    public String getName() {
        return this.functionName;
    }

    public int getCallingConvention() {
        return this.callFlags & 3;
    }

    public Object invoke(Class returnType, Object[] inArgs) {
        return this.invoke(returnType, inArgs, this.options);
    }

    public Object invoke(Class returnType, Object[] inArgs, Map options) {
        Object[] args = new Object[]{};
        if (inArgs != null) {
            if (inArgs.length > 256) {
                throw new UnsupportedOperationException("Maximum argument count is 256");
            }
            args = new Object[inArgs.length];
            System.arraycopy(inArgs, 0, args, 0, args.length);
        }
        TypeMapper mapper = (TypeMapper)options.get("type-mapper");
        Method invokingMethod = (Method)options.get(OPTION_INVOKING_METHOD);
        boolean allowObjects = Boolean.TRUE.equals(options.get("allow-objects"));
        for (int i2 = 0; i2 < args.length; ++i2) {
            args[i2] = this.convertArgument(args, i2, invokingMethod, mapper, allowObjects);
        }
        Class nativeType = returnType;
        FromNativeConverter resultConverter = null;
        if (NativeMapped.class.isAssignableFrom(returnType)) {
            NativeMappedConverter tc2 = NativeMappedConverter.getInstance(returnType);
            resultConverter = tc2;
            nativeType = tc2.nativeType();
        } else if (mapper != null && (resultConverter = mapper.getFromNativeConverter(returnType)) != null) {
            nativeType = resultConverter.nativeType();
        }
        Object result = this.invoke(args, nativeType, allowObjects);
        if (resultConverter != null) {
            FunctionResultContext context = invokingMethod != null ? new MethodResultContext(returnType, this, inArgs, invokingMethod) : new FunctionResultContext(returnType, this, inArgs);
            result = resultConverter.fromNative(result, context);
        }
        if (inArgs != null) {
            for (int i3 = 0; i3 < inArgs.length; ++i3) {
                Object inArg = inArgs[i3];
                if (inArg == null) continue;
                if (inArg instanceof Structure) {
                    if (inArg instanceof Structure.ByValue) continue;
                    ((Structure)inArg).autoRead();
                    continue;
                }
                if (args[i3] instanceof PostCallRead) {
                    ((PostCallRead)args[i3]).read();
                    if (!(args[i3] instanceof PointerArray)) continue;
                    PointerArray array = (PointerArray)args[i3];
                    if (!(array$Lcom$sun$jna$Structure$ByReference == null ? Function.class$("[Lcom.sun.jna.Structure$ByReference;") : array$Lcom$sun$jna$Structure$ByReference).isAssignableFrom(inArg.getClass())) continue;
                    Class<?> type = inArg.getClass().getComponentType();
                    Structure[] ss2 = (Structure[])inArg;
                    for (int si2 = 0; si2 < ss2.length; ++si2) {
                        Pointer p2 = array.getPointer(Pointer.SIZE * si2);
                        ss2[si2] = Structure.updateStructureByReference(type, ss2[si2], p2);
                    }
                    continue;
                }
                if (!(array$Lcom$sun$jna$Structure == null ? Function.class$("[Lcom.sun.jna.Structure;") : array$Lcom$sun$jna$Structure).isAssignableFrom(inArg.getClass())) continue;
                Structure.autoRead((Structure[])inArg);
            }
        }
        return result;
    }

    Object invoke(Object[] args, Class returnType, boolean allowObjects) {
        Object result = null;
        if (returnType == null || returnType == Void.TYPE || returnType == Void.class) {
            Native.invokeVoid(this.peer, this.callFlags, args);
            result = null;
        } else if (returnType == Boolean.TYPE || returnType == Boolean.class) {
            result = Function.valueOf(Native.invokeInt(this.peer, this.callFlags, args) != 0);
        } else if (returnType == Byte.TYPE || returnType == Byte.class) {
            result = new Byte((byte)Native.invokeInt(this.peer, this.callFlags, args));
        } else if (returnType == Short.TYPE || returnType == Short.class) {
            result = new Short((short)Native.invokeInt(this.peer, this.callFlags, args));
        } else if (returnType == Character.TYPE || returnType == Character.class) {
            result = new Character((char)Native.invokeInt(this.peer, this.callFlags, args));
        } else if (returnType == Integer.TYPE || returnType == Integer.class) {
            result = new Integer(Native.invokeInt(this.peer, this.callFlags, args));
        } else if (returnType == Long.TYPE || returnType == Long.class) {
            result = new Long(Native.invokeLong(this.peer, this.callFlags, args));
        } else if (returnType == Float.TYPE || returnType == Float.class) {
            result = new Float(Native.invokeFloat(this.peer, this.callFlags, args));
        } else if (returnType == Double.TYPE || returnType == Double.class) {
            result = new Double(Native.invokeDouble(this.peer, this.callFlags, args));
        } else if (returnType == String.class) {
            result = this.invokeString(this.callFlags, args, false);
        } else if (returnType == WString.class) {
            String s2 = this.invokeString(this.callFlags, args, true);
            if (s2 != null) {
                result = new WString(s2);
            }
        } else {
            if (Pointer.class.isAssignableFrom(returnType)) {
                return this.invokePointer(this.callFlags, args);
            }
            if (Structure.class.isAssignableFrom(returnType)) {
                if (Structure.ByValue.class.isAssignableFrom(returnType)) {
                    Structure s3 = Native.invokeStructure(this.peer, this.callFlags, args, Structure.newInstance(returnType));
                    s3.autoRead();
                    result = s3;
                } else {
                    result = this.invokePointer(this.callFlags, args);
                    if (result != null) {
                        Structure s4 = Structure.newInstance(returnType);
                        s4.useMemory((Pointer)result);
                        s4.autoRead();
                        result = s4;
                    }
                }
            } else if (Callback.class.isAssignableFrom(returnType)) {
                result = this.invokePointer(this.callFlags, args);
                if (result != null) {
                    result = CallbackReference.getCallback(returnType, (Pointer)result);
                }
            } else if (returnType == CfrRenamed5689.class) {
                Pointer p2 = this.invokePointer(this.callFlags, args);
                if (p2 != null) {
                    result = p2.getStringArray(0L);
                }
            } else if (returnType == CfrRenamed5706.class) {
                Pointer p3 = this.invokePointer(this.callFlags, args);
                if (p3 != null) {
                    String[] arr2 = p3.getStringArray(0L, true);
                    WString[] warr = new WString[arr2.length];
                    for (int i2 = 0; i2 < arr2.length; ++i2) {
                        warr[i2] = new WString(arr2[i2]);
                    }
                    result = warr;
                }
            } else if (returnType == CfrRenamed5715.class) {
                Pointer p4 = this.invokePointer(this.callFlags, args);
                if (p4 != null) {
                    result = p4.getPointerArray(0L);
                }
            } else if (allowObjects) {
                result = Native.invokeObject(this.peer, this.callFlags, args);
                if (result != null && !returnType.isAssignableFrom(result.getClass())) {
                    throw new ClassCastException("Return type " + returnType + " does not match result " + result.getClass());
                }
            } else {
                throw new IllegalArgumentException("Unsupported return type " + returnType + " in function " + this.getName());
            }
        }
        return result;
    }

    private Pointer invokePointer(int callFlags, Object[] args) {
        long ptr = Native.invokePointer(this.peer, callFlags, args);
        return ptr == 0L ? null : new Pointer(ptr);
    }

    private Object convertArgument(Object[] args, int index, Method invokingMethod, TypeMapper mapper, boolean allowObjects) {
        Object arg2 = args[index];
        if (arg2 != null) {
            Class<?> type = arg2.getClass();
            ToNativeConverter converter = null;
            if (NativeMapped.class.isAssignableFrom(type)) {
                converter = NativeMappedConverter.getInstance(type);
            } else if (mapper != null) {
                converter = mapper.getToNativeConverter(type);
            }
            if (converter != null) {
                FunctionParameterContext context = invokingMethod != null ? new MethodParameterContext(this, args, index, invokingMethod) : new FunctionParameterContext(this, args, index);
                arg2 = converter.toNative(arg2, context);
            }
        }
        if (arg2 == null || this.isPrimitiveArray(arg2.getClass())) {
            return arg2;
        }
        Class<?> argClass = arg2.getClass();
        if (arg2 instanceof Structure) {
            Structure struct = (Structure)arg2;
            struct.autoWrite();
            if (struct instanceof Structure.ByValue) {
                Class<?> ptype = struct.getClass();
                if (invokingMethod != null) {
                    Class<?>[] ptypes = invokingMethod.getParameterTypes();
                    if (Function.isVarArgs(invokingMethod)) {
                        if (index < ptypes.length - 1) {
                            ptype = ptypes[index];
                        } else {
                            Class<?> etype = ptypes[ptypes.length - 1].getComponentType();
                            if (etype != Object.class) {
                                ptype = etype;
                            }
                        }
                    } else {
                        ptype = ptypes[index];
                    }
                }
                if (Structure.ByValue.class.isAssignableFrom(ptype)) {
                    return struct;
                }
            }
            return struct.getPointer();
        }
        if (arg2 instanceof Callback) {
            return CallbackReference.getFunctionPointer((Callback)arg2);
        }
        if (arg2 instanceof String) {
            return new NativeString((String)arg2, false).getPointer();
        }
        if (arg2 instanceof WString) {
            return new NativeString(arg2.toString(), true).getPointer();
        }
        if (arg2 instanceof Boolean) {
            return Boolean.TRUE.equals(arg2) ? INTEGER_TRUE : INTEGER_FALSE;
        }
        if (CfrRenamed5689.class == argClass) {
            return new StringArray((String[])arg2);
        }
        if (CfrRenamed5706.class == argClass) {
            return new StringArray((WString[])arg2);
        }
        if (CfrRenamed5715.class == argClass) {
            return new PointerArray((Pointer[])arg2);
        }
        if (CfrRenamed5716.class.isAssignableFrom(argClass)) {
            return new NativeMappedArray((NativeMapped[])arg2);
        }
        if (CfrRenamed5718.class.isAssignableFrom(argClass)) {
            Class<?> type;
            Structure[] ss2 = (Structure[])arg2;
            boolean byRef = Structure.ByReference.class.isAssignableFrom(type = argClass.getComponentType());
            if (byRef) {
                Pointer[] pointers = new Pointer[ss2.length + 1];
                for (int i2 = 0; i2 < ss2.length; ++i2) {
                    pointers[i2] = ss2[i2] != null ? ss2[i2].getPointer() : null;
                }
                return new PointerArray(pointers);
            }
            if (ss2.length == 0) {
                throw new IllegalArgumentException("Structure array must have non-zero length");
            }
            if (ss2[0] == null) {
                Structure.newInstance(type).toArray(ss2);
                return ss2[0].getPointer();
            }
            Structure.autoWrite(ss2);
            return ss2[0].getPointer();
        }
        if (argClass.isArray()) {
            throw new IllegalArgumentException("Unsupported array argument type: " + argClass.getComponentType());
        }
        if (allowObjects) {
            return arg2;
        }
        if (!Native.isSupportedNativeType(arg2.getClass())) {
            throw new IllegalArgumentException("Unsupported argument type " + arg2.getClass().getName() + " at parameter " + index + " of function " + this.getName());
        }
        return arg2;
    }

    private boolean isPrimitiveArray(Class argClass) {
        return argClass.isArray() && argClass.getComponentType().isPrimitive();
    }

    public void invoke(Object[] args) {
        this.invoke(Void.class, args);
    }

    private String invokeString(int callFlags, Object[] args, boolean wide) {
        Pointer ptr = this.invokePointer(callFlags, args);
        String s2 = null;
        if (ptr != null) {
            s2 = wide ? ptr.getString(0L, wide) : ptr.getString(0L);
        }
        return s2;
    }

    public String toString() {
        if (this.library != null) {
            return "native function " + this.functionName + "(" + this.library.getName() + ")@0x" + Long.toHexString(this.peer);
        }
        return "native function@0x" + Long.toHexString(this.peer);
    }

    public Object invokeObject(Object[] args) {
        return this.invoke(Object.class, args);
    }

    public Pointer invokePointer(Object[] args) {
        return (Pointer)this.invoke(Pointer.class, args);
    }

    public String invokeString(Object[] args, boolean wide) {
        Class clazz = wide ? WString.class : String.class;
        Object o2 = this.invoke(clazz, args);
        return o2 != null ? o2.toString() : null;
    }

    public int invokeInt(Object[] args) {
        return (Integer)this.invoke(Integer.class, args);
    }

    public long invokeLong(Object[] args) {
        return (Long)this.invoke(Long.class, args);
    }

    public float invokeFloat(Object[] args) {
        return ((Float)this.invoke(Float.class, args)).floatValue();
    }

    public double invokeDouble(Object[] args) {
        return (Double)this.invoke(Double.class, args);
    }

    public void invokeVoid(Object[] args) {
        this.invoke(Void.class, args);
    }

    public boolean equals(Object o2) {
        if (o2 == this) {
            return true;
        }
        if (o2 == null) {
            return false;
        }
        if (o2.getClass() == this.getClass()) {
            Function other = (Function)o2;
            return other.callFlags == this.callFlags && ((Object)other.options).equals(this.options) && other.peer == this.peer;
        }
        return false;
    }

    public int hashCode() {
        return this.callFlags + ((Object)this.options).hashCode() + super.hashCode();
    }

    static Object[] concatenateVarArgs(Object[] inArgs) {
        if (inArgs != null && inArgs.length > 0) {
            Class<?> argType;
            Object lastArg = inArgs[inArgs.length - 1];
            Class<?> clazz = argType = lastArg != null ? lastArg.getClass() : null;
            if (argType != null && argType.isArray()) {
                Object[] varArgs = (Object[])lastArg;
                Object[] fullArgs = new Object[inArgs.length + varArgs.length];
                System.arraycopy(inArgs, 0, fullArgs, 0, inArgs.length - 1);
                System.arraycopy(varArgs, 0, fullArgs, inArgs.length - 1, varArgs.length);
                fullArgs[fullArgs.length - 1] = null;
                inArgs = fullArgs;
            }
        }
        return inArgs;
    }

    static boolean isVarArgs(Method m2) {
        try {
            Method v2 = m2.getClass().getMethod("isVarArgs", new Class[0]);
            return Boolean.TRUE.equals(v2.invoke((Object)m2, new Object[0]));
        }
        catch (SecurityException e2) {
        }
        catch (NoSuchMethodException e3) {
        }
        catch (IllegalArgumentException e4) {
        }
        catch (IllegalAccessException e5) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
        return false;
    }

    static Boolean valueOf(boolean b2) {
        return b2 ? Boolean.TRUE : Boolean.FALSE;
    }

    private static class PointerArray
    extends Memory
    implements PostCallRead {
        private final Pointer[] original;

        public PointerArray(Pointer[] arg2) {
            super(Pointer.SIZE * (arg2.length + 1));
            this.original = arg2;
            for (int i2 = 0; i2 < arg2.length; ++i2) {
                this.setPointer(i2 * Pointer.SIZE, arg2[i2]);
            }
            this.setPointer(Pointer.SIZE * arg2.length, null);
        }

        public void read() {
            this.read(0L, this.original, 0, this.original.length);
        }
    }

    private static class NativeMappedArray
    extends Memory
    implements PostCallRead {
        private final NativeMapped[] original;

        public NativeMappedArray(NativeMapped[] arg2) {
            super(Native.getNativeSize(arg2.getClass(), arg2));
            this.original = arg2;
            this.setValue(0L, this.original, this.original.getClass());
        }

        public void read() {
            this.getValue(0L, this.original.getClass(), this.original);
        }
    }

    public static interface PostCallRead {
        public void read();
    }
}

