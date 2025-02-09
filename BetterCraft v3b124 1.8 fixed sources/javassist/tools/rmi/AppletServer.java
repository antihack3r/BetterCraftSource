/*
 * Decompiled with CFR 0.152.
 */
package javassist.tools.rmi;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import javassist.tools.rmi.ExportedObject;
import javassist.tools.rmi.RemoteRef;
import javassist.tools.rmi.StubGenerator;
import javassist.tools.web.BadHttpRequest;
import javassist.tools.web.Webserver;

public class AppletServer
extends Webserver {
    private StubGenerator stubGen;
    private Map<String, ExportedObject> exportedNames = new Hashtable<String, ExportedObject>();
    private List<ExportedObject> exportedObjects = new Vector<ExportedObject>();
    private static final byte[] okHeader = "HTTP/1.0 200 OK\r\n\r\n".getBytes();

    public AppletServer(String port) throws IOException, NotFoundException, CannotCompileException {
        this(Integer.parseInt(port));
    }

    public AppletServer(int port) throws IOException, NotFoundException, CannotCompileException {
        this(ClassPool.getDefault(), new StubGenerator(), port);
    }

    public AppletServer(int port, ClassPool src) throws IOException, NotFoundException, CannotCompileException {
        this(new ClassPool(src), new StubGenerator(), port);
    }

    private AppletServer(ClassPool loader, StubGenerator gen, int port) throws IOException, NotFoundException, CannotCompileException {
        super(port);
        this.stubGen = gen;
        this.addTranslator(loader, gen);
    }

    @Override
    public void run() {
        super.run();
    }

    public synchronized int exportObject(String name, Object obj) throws CannotCompileException {
        Class<?> clazz = obj.getClass();
        ExportedObject eo2 = new ExportedObject();
        eo2.object = obj;
        eo2.methods = clazz.getMethods();
        this.exportedObjects.add(eo2);
        eo2.identifier = this.exportedObjects.size() - 1;
        if (name != null) {
            this.exportedNames.put(name, eo2);
        }
        try {
            this.stubGen.makeProxyClass(clazz);
        }
        catch (NotFoundException e2) {
            throw new CannotCompileException(e2);
        }
        return eo2.identifier;
    }

    @Override
    public void doReply(InputStream in2, OutputStream out, String cmd) throws IOException, BadHttpRequest {
        if (cmd.startsWith("POST /rmi ")) {
            this.processRMI(in2, out);
        } else if (cmd.startsWith("POST /lookup ")) {
            this.lookupName(cmd, in2, out);
        } else {
            super.doReply(in2, out, cmd);
        }
    }

    private void processRMI(InputStream ins, OutputStream outs) throws IOException {
        ObjectInputStream in2 = new ObjectInputStream(ins);
        int objectId = in2.readInt();
        int methodId = in2.readInt();
        Exception err = null;
        Object rvalue = null;
        try {
            ExportedObject eo2 = this.exportedObjects.get(objectId);
            Object[] args = this.readParameters(in2);
            rvalue = this.convertRvalue(eo2.methods[methodId].invoke(eo2.object, args));
        }
        catch (Exception e2) {
            err = e2;
            this.logging2(e2.toString());
        }
        outs.write(okHeader);
        ObjectOutputStream out = new ObjectOutputStream(outs);
        if (err != null) {
            out.writeBoolean(false);
            out.writeUTF(err.toString());
        } else {
            try {
                out.writeBoolean(true);
                out.writeObject(rvalue);
            }
            catch (NotSerializableException e3) {
                this.logging2(e3.toString());
            }
            catch (InvalidClassException e4) {
                this.logging2(e4.toString());
            }
        }
        out.flush();
        out.close();
        in2.close();
    }

    private Object[] readParameters(ObjectInputStream in2) throws IOException, ClassNotFoundException {
        int n2 = in2.readInt();
        Object[] args = new Object[n2];
        for (int i2 = 0; i2 < n2; ++i2) {
            Object a2 = in2.readObject();
            if (a2 instanceof RemoteRef) {
                RemoteRef ref = (RemoteRef)a2;
                ExportedObject eo2 = this.exportedObjects.get(ref.oid);
                a2 = eo2.object;
            }
            args[i2] = a2;
        }
        return args;
    }

    private Object convertRvalue(Object rvalue) throws CannotCompileException {
        if (rvalue == null) {
            return null;
        }
        String classname = rvalue.getClass().getName();
        if (this.stubGen.isProxyClass(classname)) {
            return new RemoteRef(this.exportObject(null, rvalue), classname);
        }
        return rvalue;
    }

    private void lookupName(String cmd, InputStream ins, OutputStream outs) throws IOException {
        ObjectInputStream in2 = new ObjectInputStream(ins);
        String name = DataInputStream.readUTF(in2);
        ExportedObject found = this.exportedNames.get(name);
        outs.write(okHeader);
        ObjectOutputStream out = new ObjectOutputStream(outs);
        if (found == null) {
            this.logging2(name + "not found.");
            out.writeInt(-1);
            out.writeUTF("error");
        } else {
            this.logging2(name);
            out.writeInt(found.identifier);
            out.writeUTF(found.object.getClass().getName());
        }
        out.flush();
        out.close();
        in2.close();
    }
}

