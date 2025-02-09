/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.Opcode;

class CodeAnalyzer
implements Opcode {
    private ConstPool constPool;
    private CodeAttribute codeAttr;

    public CodeAnalyzer(CodeAttribute ca2) {
        this.codeAttr = ca2;
        this.constPool = ca2.getConstPool();
    }

    public int computeMaxStack() throws BadBytecode {
        boolean repeat;
        CodeIterator ci = this.codeAttr.iterator();
        int length = ci.getCodeLength();
        int[] stack = new int[length];
        this.constPool = this.codeAttr.getConstPool();
        this.initStack(stack, this.codeAttr);
        do {
            repeat = false;
            for (int i2 = 0; i2 < length; ++i2) {
                if (stack[i2] >= 0) continue;
                repeat = true;
                this.visitBytecode(ci, stack, i2);
            }
        } while (repeat);
        int maxStack = 1;
        for (int i3 = 0; i3 < length; ++i3) {
            if (stack[i3] <= maxStack) continue;
            maxStack = stack[i3];
        }
        return maxStack - 1;
    }

    private void initStack(int[] stack, CodeAttribute ca2) {
        stack[0] = -1;
        ExceptionTable et2 = ca2.getExceptionTable();
        if (et2 != null) {
            int size = et2.size();
            for (int i2 = 0; i2 < size; ++i2) {
                stack[et2.handlerPc((int)i2)] = -2;
            }
        }
    }

    private void visitBytecode(CodeIterator ci, int[] stack, int index) throws BadBytecode {
        int codeLength = stack.length;
        ci.move(index);
        int stackDepth = -stack[index];
        int[] jsrDepth = new int[]{-1};
        while (ci.hasNext()) {
            index = ci.next();
            stack[index] = stackDepth;
            int op2 = ci.byteAt(index);
            stackDepth = this.visitInst(op2, ci, index, stackDepth);
            if (stackDepth < 1) {
                throw new BadBytecode("stack underflow at " + index);
            }
            if (this.processBranch(op2, ci, index, codeLength, stack, stackDepth, jsrDepth) || CodeAnalyzer.isEnd(op2)) break;
            if (op2 != 168 && op2 != 201) continue;
            --stackDepth;
        }
    }

    private boolean processBranch(int opcode, CodeIterator ci, int index, int codeLength, int[] stack, int stackDepth, int[] jsrDepth) throws BadBytecode {
        if (153 <= opcode && opcode <= 166 || opcode == 198 || opcode == 199) {
            int target = index + ci.s16bitAt(index + 1);
            this.checkTarget(index, target, codeLength, stack, stackDepth);
        } else {
            switch (opcode) {
                case 167: {
                    int target = index + ci.s16bitAt(index + 1);
                    this.checkTarget(index, target, codeLength, stack, stackDepth);
                    return true;
                }
                case 200: {
                    int target = index + ci.s32bitAt(index + 1);
                    this.checkTarget(index, target, codeLength, stack, stackDepth);
                    return true;
                }
                case 168: 
                case 201: {
                    int target = opcode == 168 ? index + ci.s16bitAt(index + 1) : index + ci.s32bitAt(index + 1);
                    this.checkTarget(index, target, codeLength, stack, stackDepth);
                    if (jsrDepth[0] < 0) {
                        jsrDepth[0] = stackDepth;
                        return false;
                    }
                    if (stackDepth == jsrDepth[0]) {
                        return false;
                    }
                    throw new BadBytecode("sorry, cannot compute this data flow due to JSR: " + stackDepth + "," + jsrDepth[0]);
                }
                case 169: {
                    if (jsrDepth[0] < 0) {
                        jsrDepth[0] = stackDepth + 1;
                        return false;
                    }
                    if (stackDepth + 1 == jsrDepth[0]) {
                        return true;
                    }
                    throw new BadBytecode("sorry, cannot compute this data flow due to RET: " + stackDepth + "," + jsrDepth[0]);
                }
                case 170: 
                case 171: {
                    int index2 = (index & 0xFFFFFFFC) + 4;
                    int target = index + ci.s32bitAt(index2);
                    this.checkTarget(index, target, codeLength, stack, stackDepth);
                    if (opcode == 171) {
                        int npairs = ci.s32bitAt(index2 + 4);
                        index2 += 12;
                        for (int i2 = 0; i2 < npairs; ++i2) {
                            target = index + ci.s32bitAt(index2);
                            this.checkTarget(index, target, codeLength, stack, stackDepth);
                            index2 += 8;
                        }
                    } else {
                        int low = ci.s32bitAt(index2 + 4);
                        int high = ci.s32bitAt(index2 + 8);
                        int n2 = high - low + 1;
                        index2 += 12;
                        for (int i3 = 0; i3 < n2; ++i3) {
                            target = index + ci.s32bitAt(index2);
                            this.checkTarget(index, target, codeLength, stack, stackDepth);
                            index2 += 4;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void checkTarget(int opIndex, int target, int codeLength, int[] stack, int stackDepth) throws BadBytecode {
        if (target < 0 || codeLength <= target) {
            throw new BadBytecode("bad branch offset at " + opIndex);
        }
        int d2 = stack[target];
        if (d2 == 0) {
            stack[target] = -stackDepth;
        } else if (d2 != stackDepth && d2 != -stackDepth) {
            throw new BadBytecode("verification error (" + stackDepth + "," + d2 + ") at " + opIndex);
        }
    }

    private static boolean isEnd(int opcode) {
        return 172 <= opcode && opcode <= 177 || opcode == 191;
    }

    private int visitInst(int op2, CodeIterator ci, int index, int stack) throws BadBytecode {
        switch (op2) {
            case 180: {
                stack += this.getFieldSize(ci, index) - 1;
                break;
            }
            case 181: {
                stack -= this.getFieldSize(ci, index) + 1;
                break;
            }
            case 178: {
                stack += this.getFieldSize(ci, index);
                break;
            }
            case 179: {
                stack -= this.getFieldSize(ci, index);
                break;
            }
            case 182: 
            case 183: {
                String desc = this.constPool.getMethodrefType(ci.u16bitAt(index + 1));
                stack += Descriptor.dataSize(desc) - 1;
                break;
            }
            case 184: {
                String desc = this.constPool.getMethodrefType(ci.u16bitAt(index + 1));
                stack += Descriptor.dataSize(desc);
                break;
            }
            case 185: {
                String desc = this.constPool.getInterfaceMethodrefType(ci.u16bitAt(index + 1));
                stack += Descriptor.dataSize(desc) - 1;
                break;
            }
            case 186: {
                String desc = this.constPool.getInvokeDynamicType(ci.u16bitAt(index + 1));
                stack += Descriptor.dataSize(desc);
                break;
            }
            case 191: {
                stack = 1;
                break;
            }
            case 197: {
                stack += 1 - ci.byteAt(index + 3);
                break;
            }
            case 196: {
                op2 = ci.byteAt(index + 1);
            }
            default: {
                stack += STACK_GROW[op2];
            }
        }
        return stack;
    }

    private int getFieldSize(CodeIterator ci, int index) {
        String desc = this.constPool.getFieldrefType(ci.u16bitAt(index + 1));
        return Descriptor.dataSize(desc);
    }
}

