/*
 * Decompiled with CFR 0.152.
 */
package javassist.tools.reflect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javassist.tools.reflect.CannotInvokeException;
import javassist.tools.reflect.ClassMetaobject;
import javassist.tools.reflect.Metalevel;

public class Metaobject
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected ClassMetaobject classmetaobject;
    protected Metalevel baseobject;
    protected Method[] methods;

    public Metaobject(Object self, Object[] args) {
        this.baseobject = (Metalevel)self;
        this.classmetaobject = this.baseobject._getClass();
        this.methods = this.classmetaobject.getReflectiveMethods();
    }

    protected Metaobject() {
        this.baseobject = null;
        this.classmetaobject = null;
        this.methods = null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.baseobject);
    }

    private void readObject(ObjectInputStream in2) throws IOException, ClassNotFoundException {
        this.baseobject = (Metalevel)in2.readObject();
        this.classmetaobject = this.baseobject._getClass();
        this.methods = this.classmetaobject.getReflectiveMethods();
    }

    public final ClassMetaobject getClassMetaobject() {
        return this.classmetaobject;
    }

    public final Object getObject() {
        return this.baseobject;
    }

    public final void setObject(Object self) {
        this.baseobject = (Metalevel)self;
        this.classmetaobject = this.baseobject._getClass();
        this.methods = this.classmetaobject.getReflectiveMethods();
        this.baseobject._setMetaobject(this);
    }

    public final String getMethodName(int identifier) {
        char c2;
        String mname = this.methods[identifier].getName();
        int j2 = 3;
        while ((c2 = mname.charAt(j2++)) >= '0' && '9' >= c2) {
        }
        return mname.substring(j2);
    }

    public final Class<?>[] getParameterTypes(int identifier) {
        return this.methods[identifier].getParameterTypes();
    }

    public final Class<?> getReturnType(int identifier) {
        return this.methods[identifier].getReturnType();
    }

    public Object trapFieldRead(String name) {
        Class<?> jc2 = this.getClassMetaobject().getJavaClass();
        try {
            return jc2.getField(name).get(this.getObject());
        }
        catch (NoSuchFieldException e2) {
            throw new RuntimeException(e2.toString());
        }
        catch (IllegalAccessException e3) {
            throw new RuntimeException(e3.toString());
        }
    }

    public void trapFieldWrite(String name, Object value) {
        Class<?> jc2 = this.getClassMetaobject().getJavaClass();
        try {
            jc2.getField(name).set(this.getObject(), value);
        }
        catch (NoSuchFieldException e2) {
            throw new RuntimeException(e2.toString());
        }
        catch (IllegalAccessException e3) {
            throw new RuntimeException(e3.toString());
        }
    }

    public Object trapMethodcall(int identifier, Object[] args) throws Throwable {
        try {
            return this.methods[identifier].invoke(this.getObject(), args);
        }
        catch (InvocationTargetException e2) {
            throw e2.getTargetException();
        }
        catch (IllegalAccessException e3) {
            throw new CannotInvokeException(e3);
        }
    }
}

