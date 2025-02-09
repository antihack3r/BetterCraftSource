/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode.annotation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationDefaultAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;

public class AnnotationImpl
implements InvocationHandler {
    private static final String JDK_ANNOTATION_CLASS_NAME = "java.lang.annotation.Annotation";
    private static Method JDK_ANNOTATION_TYPE_METHOD = null;
    private Annotation annotation;
    private ClassPool pool;
    private ClassLoader classLoader;
    private transient Class<?> annotationType;
    private transient int cachedHashCode = Integer.MIN_VALUE;

    public static Object make(ClassLoader cl2, Class<?> clazz, ClassPool cp2, Annotation anon) throws IllegalArgumentException {
        AnnotationImpl handler = new AnnotationImpl(anon, cp2, cl2);
        return Proxy.newProxyInstance(cl2, new Class[]{clazz}, (InvocationHandler)handler);
    }

    private AnnotationImpl(Annotation a2, ClassPool cp2, ClassLoader loader) {
        this.annotation = a2;
        this.pool = cp2;
        this.classLoader = loader;
    }

    public String getTypeName() {
        return this.annotation.getTypeName();
    }

    private Class<?> getAnnotationType() {
        if (this.annotationType == null) {
            String typeName = this.annotation.getTypeName();
            try {
                this.annotationType = this.classLoader.loadClass(typeName);
            }
            catch (ClassNotFoundException e2) {
                NoClassDefFoundError error = new NoClassDefFoundError("Error loading annotation class: " + typeName);
                error.setStackTrace(e2.getStackTrace());
                throw error;
            }
        }
        return this.annotationType;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MemberValue mv2;
        String name = method.getName();
        if (Object.class == method.getDeclaringClass()) {
            if ("equals".equals(name)) {
                Object obj = args[0];
                return this.checkEquals(obj);
            }
            if ("toString".equals(name)) {
                return this.annotation.toString();
            }
            if ("hashCode".equals(name)) {
                return this.hashCode();
            }
        } else if ("annotationType".equals(name) && method.getParameterTypes().length == 0) {
            return this.getAnnotationType();
        }
        if ((mv2 = this.annotation.getMemberValue(name)) == null) {
            return this.getDefault(name, method);
        }
        return mv2.getValue(this.classLoader, this.pool, method);
    }

    private Object getDefault(String name, Method method) throws ClassNotFoundException, RuntimeException {
        String classname = this.annotation.getTypeName();
        if (this.pool != null) {
            try {
                AnnotationDefaultAttribute ainfo;
                CtClass cc2 = this.pool.get(classname);
                ClassFile cf2 = cc2.getClassFile2();
                MethodInfo minfo = cf2.getMethod(name);
                if (minfo != null && (ainfo = (AnnotationDefaultAttribute)minfo.getAttribute("AnnotationDefault")) != null) {
                    MemberValue mv2 = ainfo.getDefaultValue();
                    return mv2.getValue(this.classLoader, this.pool, method);
                }
            }
            catch (NotFoundException e2) {
                throw new RuntimeException("cannot find a class file: " + classname);
            }
        }
        throw new RuntimeException("no default value: " + classname + "." + name + "()");
    }

    public int hashCode() {
        if (this.cachedHashCode == Integer.MIN_VALUE) {
            int hashCode = 0;
            this.getAnnotationType();
            Method[] methods = this.annotationType.getDeclaredMethods();
            for (int i2 = 0; i2 < methods.length; ++i2) {
                String name = methods[i2].getName();
                int valueHashCode = 0;
                MemberValue mv2 = this.annotation.getMemberValue(name);
                Object value = null;
                try {
                    if (mv2 != null) {
                        value = mv2.getValue(this.classLoader, this.pool, methods[i2]);
                    }
                    if (value == null) {
                        value = this.getDefault(name, methods[i2]);
                    }
                }
                catch (RuntimeException e2) {
                    throw e2;
                }
                catch (Exception e3) {
                    throw new RuntimeException("Error retrieving value " + name + " for annotation " + this.annotation.getTypeName(), e3);
                }
                if (value != null) {
                    valueHashCode = value.getClass().isArray() ? AnnotationImpl.arrayHashCode(value) : value.hashCode();
                }
                hashCode += 127 * name.hashCode() ^ valueHashCode;
            }
            this.cachedHashCode = hashCode;
        }
        return this.cachedHashCode;
    }

    private boolean checkEquals(Object obj) throws Exception {
        InvocationHandler ih2;
        if (obj == null) {
            return false;
        }
        if (obj instanceof Proxy && (ih2 = Proxy.getInvocationHandler(obj)) instanceof AnnotationImpl) {
            AnnotationImpl other = (AnnotationImpl)ih2;
            return this.annotation.equals(other.annotation);
        }
        Class otherAnnotationType = (Class)JDK_ANNOTATION_TYPE_METHOD.invoke(obj, new Object[0]);
        if (!this.getAnnotationType().equals(otherAnnotationType)) {
            return false;
        }
        Method[] methods = this.annotationType.getDeclaredMethods();
        for (int i2 = 0; i2 < methods.length; ++i2) {
            String name = methods[i2].getName();
            MemberValue mv2 = this.annotation.getMemberValue(name);
            Object value = null;
            Object otherValue = null;
            try {
                if (mv2 != null) {
                    value = mv2.getValue(this.classLoader, this.pool, methods[i2]);
                }
                if (value == null) {
                    value = this.getDefault(name, methods[i2]);
                }
                otherValue = methods[i2].invoke(obj, new Object[0]);
            }
            catch (RuntimeException e2) {
                throw e2;
            }
            catch (Exception e3) {
                throw new RuntimeException("Error retrieving value " + name + " for annotation " + this.annotation.getTypeName(), e3);
            }
            if (value == null && otherValue != null) {
                return false;
            }
            if (value == null || value.equals(otherValue)) continue;
            return false;
        }
        return true;
    }

    private static int arrayHashCode(Object object) {
        if (object == null) {
            return 0;
        }
        int result = 1;
        Object[] array = (Object[])object;
        for (int i2 = 0; i2 < array.length; ++i2) {
            int elementHashCode = 0;
            if (array[i2] != null) {
                elementHashCode = array[i2].hashCode();
            }
            result = 31 * result + elementHashCode;
        }
        return result;
    }

    static {
        try {
            Class<?> clazz = Class.forName(JDK_ANNOTATION_CLASS_NAME);
            JDK_ANNOTATION_TYPE_METHOD = clazz.getMethod("annotationType", null);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

