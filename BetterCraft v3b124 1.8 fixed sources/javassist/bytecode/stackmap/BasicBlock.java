/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode.stackmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;

public class BasicBlock {
    protected int position;
    protected int length;
    protected int incoming;
    protected BasicBlock[] exit;
    protected boolean stop;
    protected Catch toCatch;

    protected BasicBlock(int pos) {
        this.position = pos;
        this.length = 0;
        this.incoming = 0;
    }

    public static BasicBlock find(BasicBlock[] blocks, int pos) throws BadBytecode {
        for (BasicBlock b2 : blocks) {
            if (b2.position > pos || pos >= b2.position + b2.length) continue;
            return b2;
        }
        throw new BadBytecode("no basic block at " + pos);
    }

    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        String cname = this.getClass().getName();
        int i2 = cname.lastIndexOf(46);
        sbuf.append(i2 < 0 ? cname : cname.substring(i2 + 1));
        sbuf.append("[");
        this.toString2(sbuf);
        sbuf.append("]");
        return sbuf.toString();
    }

    protected void toString2(StringBuffer sbuf) {
        sbuf.append("pos=").append(this.position).append(", len=").append(this.length).append(", in=").append(this.incoming).append(", exit{");
        if (this.exit != null) {
            for (BasicBlock b2 : this.exit) {
                sbuf.append(b2.position).append(",");
            }
        }
        sbuf.append("}, {");
        Catch th2 = this.toCatch;
        while (th2 != null) {
            sbuf.append("(").append(th2.body.position).append(", ").append(th2.typeIndex).append("), ");
            th2 = th2.next;
        }
        sbuf.append("}");
    }

    public static class Maker {
        protected BasicBlock makeBlock(int pos) {
            return new BasicBlock(pos);
        }

        protected BasicBlock[] makeArray(int size) {
            return new BasicBlock[size];
        }

        private BasicBlock[] makeArray(BasicBlock b2) {
            BasicBlock[] array = this.makeArray(1);
            array[0] = b2;
            return array;
        }

        private BasicBlock[] makeArray(BasicBlock b1, BasicBlock b2) {
            BasicBlock[] array = this.makeArray(2);
            array[0] = b1;
            array[1] = b2;
            return array;
        }

        public BasicBlock[] make(MethodInfo minfo) throws BadBytecode {
            CodeAttribute ca2 = minfo.getCodeAttribute();
            if (ca2 == null) {
                return null;
            }
            CodeIterator ci = ca2.iterator();
            return this.make(ci, 0, ci.getCodeLength(), ca2.getExceptionTable());
        }

        public BasicBlock[] make(CodeIterator ci, int begin, int end, ExceptionTable et2) throws BadBytecode {
            Map<Integer, Mark> marks = this.makeMarks(ci, begin, end, et2);
            BasicBlock[] bb2 = this.makeBlocks(marks);
            this.addCatchers(bb2, et2);
            return bb2;
        }

        private Mark makeMark(Map<Integer, Mark> table, int pos) {
            return this.makeMark0(table, pos, true, true);
        }

        private Mark makeMark(Map<Integer, Mark> table, int pos, BasicBlock[] jump, int size, boolean always) {
            Mark m2 = this.makeMark0(table, pos, false, false);
            m2.setJump(jump, size, always);
            return m2;
        }

        private Mark makeMark0(Map<Integer, Mark> table, int pos, boolean isBlockBegin, boolean isTarget) {
            Integer p2 = pos;
            Mark m2 = table.get(p2);
            if (m2 == null) {
                m2 = new Mark(pos);
                table.put(p2, m2);
            }
            if (isBlockBegin) {
                if (m2.block == null) {
                    m2.block = this.makeBlock(pos);
                }
                if (isTarget) {
                    ++m2.block.incoming;
                }
            }
            return m2;
        }

        private Map<Integer, Mark> makeMarks(CodeIterator ci, int begin, int end, ExceptionTable et2) throws BadBytecode {
            int index;
            ci.begin();
            ci.move(begin);
            HashMap<Integer, Mark> marks = new HashMap<Integer, Mark>();
            while (ci.hasNext() && (index = ci.next()) < end) {
                int op2 = ci.byteAt(index);
                if (153 <= op2 && op2 <= 166 || op2 == 198 || op2 == 199) {
                    Mark to2 = this.makeMark(marks, index + ci.s16bitAt(index + 1));
                    Mark next = this.makeMark(marks, index + 3);
                    this.makeMark(marks, index, this.makeArray(to2.block, next.block), 3, false);
                    continue;
                }
                if (167 <= op2 && op2 <= 171) {
                    switch (op2) {
                        case 167: {
                            this.makeGoto(marks, index, index + ci.s16bitAt(index + 1), 3);
                            break;
                        }
                        case 168: {
                            this.makeJsr(marks, index, index + ci.s16bitAt(index + 1), 3);
                            break;
                        }
                        case 169: {
                            this.makeMark(marks, index, null, 2, true);
                            break;
                        }
                        case 170: {
                            int p2;
                            int pos = (index & 0xFFFFFFFC) + 4;
                            int low = ci.s32bitAt(pos + 4);
                            int high = ci.s32bitAt(pos + 8);
                            int ncases = high - low + 1;
                            BasicBlock[] to3 = this.makeArray(ncases + 1);
                            to3[0] = this.makeMark(marks, (int)(index + ci.s32bitAt((int)pos))).block;
                            int n2 = p2 + ncases * 4;
                            int k2 = 1;
                            for (p2 = pos + 12; p2 < n2; p2 += 4) {
                                to3[k2++] = this.makeMark(marks, (int)(index + ci.s32bitAt((int)p2))).block;
                            }
                            this.makeMark(marks, index, to3, n2 - index, true);
                            break;
                        }
                        case 171: {
                            int p3;
                            int pos = (index & 0xFFFFFFFC) + 4;
                            int ncases = ci.s32bitAt(pos + 4);
                            BasicBlock[] to4 = this.makeArray(ncases + 1);
                            to4[0] = this.makeMark(marks, (int)(index + ci.s32bitAt((int)pos))).block;
                            int n3 = p3 + ncases * 8 - 4;
                            int k3 = 1;
                            for (p3 = pos + 8 + 4; p3 < n3; p3 += 8) {
                                to4[k3++] = this.makeMark(marks, (int)(index + ci.s32bitAt((int)p3))).block;
                            }
                            this.makeMark(marks, index, to4, n3 - index, true);
                            break;
                        }
                    }
                    continue;
                }
                if (172 <= op2 && op2 <= 177 || op2 == 191) {
                    this.makeMark(marks, index, null, 1, true);
                    continue;
                }
                if (op2 == 200) {
                    this.makeGoto(marks, index, index + ci.s32bitAt(index + 1), 5);
                    continue;
                }
                if (op2 == 201) {
                    this.makeJsr(marks, index, index + ci.s32bitAt(index + 1), 5);
                    continue;
                }
                if (op2 != 196 || ci.byteAt(index + 1) != 169) continue;
                this.makeMark(marks, index, null, 4, true);
            }
            if (et2 != null) {
                int i2 = et2.size();
                while (--i2 >= 0) {
                    this.makeMark0(marks, et2.startPc(i2), true, false);
                    this.makeMark(marks, et2.handlerPc(i2));
                }
            }
            return marks;
        }

        private void makeGoto(Map<Integer, Mark> marks, int pos, int target, int size) {
            Mark to2 = this.makeMark(marks, target);
            BasicBlock[] jumps = this.makeArray(to2.block);
            this.makeMark(marks, pos, jumps, size, true);
        }

        protected void makeJsr(Map<Integer, Mark> marks, int pos, int target, int size) throws BadBytecode {
            throw new JsrBytecode();
        }

        private BasicBlock[] makeBlocks(Map<Integer, Mark> markTable) {
            Object[] marks = markTable.values().toArray(new Mark[markTable.size()]);
            Arrays.sort(marks);
            ArrayList<BasicBlock> blocks = new ArrayList<BasicBlock>();
            int i2 = 0;
            BasicBlock prev = marks.length > 0 && ((Mark)marks[0]).position == 0 && ((Mark)marks[0]).block != null ? Maker.getBBlock((Mark)marks[i2++]) : this.makeBlock(0);
            blocks.add(prev);
            while (i2 < marks.length) {
                Object m2;
                BasicBlock bb2;
                if ((bb2 = Maker.getBBlock((Mark)(m2 = marks[i2++]))) == null) {
                    if (prev.length > 0) {
                        prev = this.makeBlock(prev.position + prev.length);
                        blocks.add(prev);
                    }
                    prev.length = ((Mark)m2).position + ((Mark)m2).size - prev.position;
                    prev.exit = ((Mark)m2).jump;
                    prev.stop = ((Mark)m2).alwaysJmp;
                    continue;
                }
                if (prev.length == 0) {
                    prev.length = ((Mark)m2).position - prev.position;
                    ++bb2.incoming;
                    prev.exit = this.makeArray(bb2);
                } else if (prev.position + prev.length < ((Mark)m2).position) {
                    prev = this.makeBlock(prev.position + prev.length);
                    blocks.add(prev);
                    prev.length = ((Mark)m2).position - prev.position;
                    prev.stop = true;
                    prev.exit = this.makeArray(bb2);
                }
                blocks.add(bb2);
                prev = bb2;
            }
            return blocks.toArray(this.makeArray(blocks.size()));
        }

        private static BasicBlock getBBlock(Mark m2) {
            BasicBlock b2 = m2.block;
            if (b2 != null && m2.size > 0) {
                b2.exit = m2.jump;
                b2.length = m2.size;
                b2.stop = m2.alwaysJmp;
            }
            return b2;
        }

        private void addCatchers(BasicBlock[] blocks, ExceptionTable et2) throws BadBytecode {
            if (et2 == null) {
                return;
            }
            int i2 = et2.size();
            while (--i2 >= 0) {
                BasicBlock handler = BasicBlock.find(blocks, et2.handlerPc(i2));
                int start = et2.startPc(i2);
                int end = et2.endPc(i2);
                int type = et2.catchType(i2);
                --handler.incoming;
                for (int k2 = 0; k2 < blocks.length; ++k2) {
                    BasicBlock bb2 = blocks[k2];
                    int iPos = bb2.position;
                    if (start > iPos || iPos >= end) continue;
                    bb2.toCatch = new Catch(handler, type, bb2.toCatch);
                    ++handler.incoming;
                }
            }
        }
    }

    static class Mark
    implements Comparable<Mark> {
        int position;
        BasicBlock block;
        BasicBlock[] jump;
        boolean alwaysJmp;
        int size;
        Catch catcher;

        Mark(int p2) {
            this.position = p2;
            this.block = null;
            this.jump = null;
            this.alwaysJmp = false;
            this.size = 0;
            this.catcher = null;
        }

        @Override
        public int compareTo(Mark obj) {
            if (null == obj) {
                return -1;
            }
            return this.position - obj.position;
        }

        void setJump(BasicBlock[] bb2, int s2, boolean always) {
            this.jump = bb2;
            this.size = s2;
            this.alwaysJmp = always;
        }
    }

    public static class Catch {
        public Catch next;
        public BasicBlock body;
        public int typeIndex;

        Catch(BasicBlock b2, int i2, Catch c2) {
            this.body = b2;
            this.typeIndex = i2;
            this.next = c2;
        }
    }

    static class JsrBytecode
    extends BadBytecode {
        private static final long serialVersionUID = 1L;

        JsrBytecode() {
            super("JSR");
        }
    }
}

