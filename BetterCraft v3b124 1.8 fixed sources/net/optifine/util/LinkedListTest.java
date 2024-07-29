/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.optifine.render.VboRange;
import net.optifine.util.LinkedList;

public class LinkedListTest {
    public static void main(String[] args) throws Exception {
        LinkedList<VboRange> linkedlist = new LinkedList<VboRange>();
        ArrayList<VboRange> list = new ArrayList<VboRange>();
        ArrayList<VboRange> list1 = new ArrayList<VboRange>();
        Random random = new Random();
        int i2 = 100;
        int j2 = 0;
        while (j2 < i2) {
            VboRange vborange = new VboRange();
            vborange.setPosition(j2);
            list.add(vborange);
            ++j2;
        }
        int k2 = 0;
        while (k2 < 100000) {
            block7: {
                block6: {
                    VboRange vborange3;
                    block9: {
                        LinkedList.Node<VboRange> node2;
                        block10: {
                            block8: {
                                LinkedListTest.checkLists(list, list1, i2);
                                LinkedListTest.checkLinkedList(linkedlist, list1.size());
                                if (k2 % 5 == 0) {
                                    LinkedListTest.dbgLinkedList(linkedlist);
                                }
                                if (!random.nextBoolean()) break block6;
                                if (list.isEmpty()) break block7;
                                vborange3 = (VboRange)list.get(random.nextInt(list.size()));
                                node2 = vborange3.getNode();
                                if (!random.nextBoolean()) break block8;
                                linkedlist.addFirst(node2);
                                LinkedListTest.dbg("Add first: " + vborange3.getPosition());
                                break block9;
                            }
                            if (!random.nextBoolean()) break block10;
                            linkedlist.addLast(node2);
                            LinkedListTest.dbg("Add last: " + vborange3.getPosition());
                            break block9;
                        }
                        if (list1.isEmpty()) break block7;
                        VboRange vborange1 = (VboRange)list1.get(random.nextInt(list1.size()));
                        LinkedList.Node<VboRange> node1 = vborange1.getNode();
                        linkedlist.addAfter(node1, node2);
                        LinkedListTest.dbg("Add after: " + vborange1.getPosition() + ", " + vborange3.getPosition());
                    }
                    list.remove(vborange3);
                    list1.add(vborange3);
                    break block7;
                }
                if (!list1.isEmpty()) {
                    VboRange vborange2 = (VboRange)list1.get(random.nextInt(list1.size()));
                    LinkedList.Node<VboRange> node = vborange2.getNode();
                    linkedlist.remove(node);
                    LinkedListTest.dbg("Remove: " + vborange2.getPosition());
                    list1.remove(vborange2);
                    list.add(vborange2);
                }
            }
            ++k2;
        }
    }

    private static void dbgLinkedList(LinkedList<VboRange> linkedList) {
        StringBuffer stringbuffer = new StringBuffer();
        linkedList.iterator().forEachRemaining(vboRangeNode -> {
            LinkedList.Node node = vboRangeNode;
            if (node.getItem() == null) {
                return;
            }
            VboRange vborange = (VboRange)node.getItem();
            if (stringbuffer.length() > 0) {
                stringbuffer.append(", ");
            }
            stringbuffer.append(vborange.getPosition());
        });
        LinkedListTest.dbg("List: " + stringbuffer);
    }

    private static void checkLinkedList(LinkedList<VboRange> linkedList, int used) {
        if (linkedList.getSize() != used) {
            throw new RuntimeException("Wrong size, linked: " + linkedList.getSize() + ", used: " + used);
        }
        int i2 = 0;
        LinkedList.Node<VboRange> node = linkedList.getFirst();
        while (node != null) {
            ++i2;
            node = node.getNext();
        }
        if (linkedList.getSize() != i2) {
            throw new RuntimeException("Wrong count, linked: " + linkedList.getSize() + ", count: " + i2);
        }
        int j2 = 0;
        LinkedList.Node<VboRange> node1 = linkedList.getLast();
        while (node1 != null) {
            ++j2;
            node1 = node1.getPrev();
        }
        if (linkedList.getSize() != j2) {
            throw new RuntimeException("Wrong count back, linked: " + linkedList.getSize() + ", count: " + j2);
        }
    }

    private static void checkLists(List<VboRange> listFree, List<VboRange> listUsed, int count) {
        int i2 = listFree.size() + listUsed.size();
        if (i2 != count) {
            throw new RuntimeException("Total size: " + i2);
        }
    }

    private static void dbg(String str) {
        System.out.println(str);
    }
}

