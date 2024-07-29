/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.xml.SAXAdapter;
import org.objectweb.asm.xml.SAXClassAdapter;
import org.xml.sax.helpers.AttributesImpl;

public final class SAXAnnotationAdapter
extends AnnotationVisitor {
    SAXAdapter sa;
    private final String elementName;

    public SAXAnnotationAdapter(SAXAdapter sAXAdapter, String string, int n2, String string2, String string3) {
        this(327680, sAXAdapter, string, n2, string3, string2, -1, -1, null, null, null, null);
    }

    public SAXAnnotationAdapter(SAXAdapter sAXAdapter, String string, int n2, int n3, String string2) {
        this(327680, sAXAdapter, string, n2, string2, null, n3, -1, null, null, null, null);
    }

    public SAXAnnotationAdapter(SAXAdapter sAXAdapter, String string, int n2, String string2, String string3, int n3, TypePath typePath) {
        this(327680, sAXAdapter, string, n2, string3, string2, -1, n3, typePath, null, null, null);
    }

    public SAXAnnotationAdapter(SAXAdapter sAXAdapter, String string, int n2, String string2, String string3, int n3, TypePath typePath, String[] stringArray, String[] stringArray2, int[] nArray) {
        this(327680, sAXAdapter, string, n2, string3, string2, -1, n3, typePath, stringArray, stringArray2, nArray);
    }

    protected SAXAnnotationAdapter(int n2, SAXAdapter sAXAdapter, String string, int n3, String string2, String string3, int n4) {
        this(n2, sAXAdapter, string, n3, string2, string3, n4, -1, null, null, null, null);
    }

    protected SAXAnnotationAdapter(int n2, SAXAdapter sAXAdapter, String string, int n3, String string2, String string3, int n4, int n5, TypePath typePath, String[] stringArray, String[] stringArray2, int[] nArray) {
        super(n2);
        int n6;
        StringBuffer stringBuffer;
        this.sa = sAXAdapter;
        this.elementName = string;
        AttributesImpl attributesImpl = new AttributesImpl();
        if (string3 != null) {
            attributesImpl.addAttribute("", "name", "name", "", string3);
        }
        if (n3 != 0) {
            attributesImpl.addAttribute("", "visible", "visible", "", n3 > 0 ? "true" : "false");
        }
        if (n4 != -1) {
            attributesImpl.addAttribute("", "parameter", "parameter", "", Integer.toString(n4));
        }
        if (string2 != null) {
            attributesImpl.addAttribute("", "desc", "desc", "", string2);
        }
        if (n5 != -1) {
            attributesImpl.addAttribute("", "typeRef", "typeRef", "", Integer.toString(n5));
        }
        if (typePath != null) {
            attributesImpl.addAttribute("", "typePath", "typePath", "", typePath.toString());
        }
        if (stringArray != null) {
            stringBuffer = new StringBuffer(stringArray[0]);
            for (n6 = 1; n6 < stringArray.length; ++n6) {
                stringBuffer.append(" ").append(stringArray[n6]);
            }
            attributesImpl.addAttribute("", "start", "start", "", stringBuffer.toString());
        }
        if (stringArray2 != null) {
            stringBuffer = new StringBuffer(stringArray2[0]);
            for (n6 = 1; n6 < stringArray2.length; ++n6) {
                stringBuffer.append(" ").append(stringArray2[n6]);
            }
            attributesImpl.addAttribute("", "end", "end", "", stringBuffer.toString());
        }
        if (nArray != null) {
            stringBuffer = new StringBuffer();
            stringBuffer.append(nArray[0]);
            for (n6 = 1; n6 < nArray.length; ++n6) {
                stringBuffer.append(" ").append(nArray[n6]);
            }
            attributesImpl.addAttribute("", "index", "index", "", stringBuffer.toString());
        }
        sAXAdapter.addStart(string, attributesImpl);
    }

    public void visit(String string, Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isArray()) {
            AnnotationVisitor annotationVisitor = this.visitArray(string);
            if (object instanceof byte[]) {
                byte[] byArray = (byte[])object;
                for (int i2 = 0; i2 < byArray.length; ++i2) {
                    annotationVisitor.visit(null, new Byte(byArray[i2]));
                }
            } else if (object instanceof char[]) {
                char[] cArray = (char[])object;
                for (int i3 = 0; i3 < cArray.length; ++i3) {
                    annotationVisitor.visit(null, new Character(cArray[i3]));
                }
            } else if (object instanceof short[]) {
                short[] sArray = (short[])object;
                for (int i4 = 0; i4 < sArray.length; ++i4) {
                    annotationVisitor.visit(null, new Short(sArray[i4]));
                }
            } else if (object instanceof boolean[]) {
                boolean[] blArray = (boolean[])object;
                for (int i5 = 0; i5 < blArray.length; ++i5) {
                    annotationVisitor.visit(null, blArray[i5]);
                }
            } else if (object instanceof int[]) {
                int[] nArray = (int[])object;
                for (int i6 = 0; i6 < nArray.length; ++i6) {
                    annotationVisitor.visit(null, new Integer(nArray[i6]));
                }
            } else if (object instanceof long[]) {
                long[] lArray = (long[])object;
                for (int i7 = 0; i7 < lArray.length; ++i7) {
                    annotationVisitor.visit(null, new Long(lArray[i7]));
                }
            } else if (object instanceof float[]) {
                float[] fArray = (float[])object;
                for (int i8 = 0; i8 < fArray.length; ++i8) {
                    annotationVisitor.visit(null, new Float(fArray[i8]));
                }
            } else if (object instanceof double[]) {
                double[] dArray = (double[])object;
                for (int i9 = 0; i9 < dArray.length; ++i9) {
                    annotationVisitor.visit(null, new Double(dArray[i9]));
                }
            }
            annotationVisitor.visitEnd();
        } else {
            this.addValueElement("annotationValue", string, Type.getDescriptor(clazz), object.toString());
        }
    }

    public void visitEnum(String string, String string2, String string3) {
        this.addValueElement("annotationValueEnum", string, string2, string3);
    }

    public AnnotationVisitor visitAnnotation(String string, String string2) {
        return new SAXAnnotationAdapter(this.sa, "annotationValueAnnotation", 0, string, string2);
    }

    public AnnotationVisitor visitArray(String string) {
        return new SAXAnnotationAdapter(this.sa, "annotationValueArray", 0, string, null);
    }

    public void visitEnd() {
        this.sa.addEnd(this.elementName);
    }

    private void addValueElement(String string, String string2, String string3, String string4) {
        AttributesImpl attributesImpl = new AttributesImpl();
        if (string2 != null) {
            attributesImpl.addAttribute("", "name", "name", "", string2);
        }
        if (string3 != null) {
            attributesImpl.addAttribute("", "desc", "desc", "", string3);
        }
        if (string4 != null) {
            attributesImpl.addAttribute("", "value", "value", "", SAXClassAdapter.encode(string4));
        }
        this.sa.addElement(string, attributesImpl);
    }
}

