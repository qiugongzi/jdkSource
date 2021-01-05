

package com.sun.imageio.plugins.jpeg;

import javax.imageio.IIOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.plugins.jpeg.JPEGQTable;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;


class DQTMarkerSegment extends MarkerSegment {
    List tables = new ArrayList();  DQTMarkerSegment(float quality, boolean needTwo) {
        super(JPEG.DQT);
        tables.add(new Qtable(true, quality));
        if (needTwo) {
            tables.add(new Qtable(false, quality));
        }
    }

    DQTMarkerSegment(JPEGBuffer buffer) throws IOException {
        super(buffer);
        int count = length;
        while (count > 0) {
            Qtable newGuy = new Qtable(buffer);
            tables.add(newGuy);
            count -= newGuy.data.length+1;
        }
        buffer.bufAvail -= length;
    }

    DQTMarkerSegment(JPEGQTable[] qtables) {
        super(JPEG.DQT);
        for (int i = 0; i < qtables.length; i++) {
            tables.add(new Qtable(qtables[i], i));
        }
    }

    DQTMarkerSegment(Node node) throws IIOInvalidTreeException {
        super(JPEG.DQT);
        NodeList children = node.getChildNodes();
        int size = children.getLength();
        if ((size < 1) || (size > 4)) {
            throw new IIOInvalidTreeException("Invalid DQT node", node);
        }
        for (int i = 0; i < size; i++) {
            tables.add(new Qtable(children.item(i)));
        }
    }

    protected Object clone() {
        DQTMarkerSegment newGuy = (DQTMarkerSegment) super.clone();
        newGuy.tables = new ArrayList(tables.size());
        Iterator iter = tables.iterator();
        while (iter.hasNext()) {
            Qtable table = (Qtable) iter.next();
            newGuy.tables.add(table.clone());
        }
        return newGuy;
    }

    IIOMetadataNode getNativeNode() {
        IIOMetadataNode node = new IIOMetadataNode("dqt");
        for (int i= 0; i<tables.size(); i++) {
            Qtable table = (Qtable) tables.get(i);
            node.appendChild(table.getNativeNode());
        }
        return node;
    }


    void write(ImageOutputStream ios) throws IOException {
        }

    void print() {
        printTag("DQT");
        System.out.println("Num tables: "
                           + Integer.toString(tables.size()));
        for (int i= 0; i<tables.size(); i++) {
            Qtable table = (Qtable) tables.get(i);
            table.print();
        }
        System.out.println();
    }


    Qtable getChromaForLuma(Qtable luma) {
        Qtable newGuy = null;
        boolean allSame = true;
        for (int i = 1; i < luma.QTABLE_SIZE; i++) {
            if (luma.data[i] != luma.data[i-1]) {
                allSame = false;
                break;
            }
        }
        if (allSame) {
            newGuy = (Qtable) luma.clone();
            newGuy.tableID = 1;
        } else {
            int largestPos = 0;
            for (int i = 1; i < luma.QTABLE_SIZE; i++) {
                if (luma.data[i] > luma.data[largestPos]) {
                    largestPos = i;
                }
            }
            float scaleFactor = ((float)(luma.data[largestPos]))
                / ((float)(JPEGQTable.K1Div2Luminance.getTable()[largestPos]));
            JPEGQTable jpegTable =
                JPEGQTable.K2Div2Chrominance.getScaledInstance(scaleFactor,
                                                               true);
            newGuy = new Qtable(jpegTable, 1);
        }
        return newGuy;
    }

    Qtable getQtableFromNode(Node node) throws IIOInvalidTreeException {
        return new Qtable(node);
    }


    class Qtable implements Cloneable {
        int elementPrecision;
        int tableID;
        final int QTABLE_SIZE = 64;
        int [] data; private final int [] zigzag = {
            0,  1,  5,  6, 14, 15, 27, 28,
            2,  4,  7, 13, 16, 26, 29, 42,
            3,  8, 12, 17, 25, 30, 41, 43,
            9, 11, 18, 24, 31, 40, 44, 53,
            10, 19, 23, 32, 39, 45, 52, 54,
            20, 22, 33, 38, 46, 51, 55, 60,
            21, 34, 37, 47, 50, 56, 59, 61,
            35, 36, 48, 49, 57, 58, 62, 63
        };

        Qtable(boolean wantLuma, float quality) {
            elementPrecision = 0;
            JPEGQTable base = null;
            if (wantLuma) {
                tableID = 0;
                base = JPEGQTable.K1Div2Luminance;
            } else {
                tableID = 1;
                base = JPEGQTable.K2Div2Chrominance;
            }
            if (quality != JPEG.DEFAULT_QUALITY) {
                quality = JPEG.convertToLinearQuality(quality);
                if (wantLuma) {
                    base = JPEGQTable.K1Luminance.getScaledInstance
                        (quality, true);
                } else {
                    base = JPEGQTable.K2Div2Chrominance.getScaledInstance
                        (quality, true);
                }
            }
            data = base.getTable();
        }

        Qtable(JPEGBuffer buffer) throws IIOException {
            elementPrecision = buffer.buf[buffer.bufPtr] >>> 4;
            tableID = buffer.buf[buffer.bufPtr++] & 0xf;
            if (elementPrecision != 0) {
                throw new IIOException ("Unsupported element precision");
            }
            data = new int [QTABLE_SIZE];
            for (int i = 0; i < QTABLE_SIZE; i++) {
                data[i] = buffer.buf[buffer.bufPtr+zigzag[i]] & 0xff;
            }
            buffer.bufPtr += QTABLE_SIZE;
        }

        Qtable(JPEGQTable table, int id) {
            elementPrecision = 0;
            tableID = id;
            data = table.getTable();
        }

        Qtable(Node node) throws IIOInvalidTreeException {
            if (node.getNodeName().equals("dqtable")) {
                NamedNodeMap attrs = node.getAttributes();
                int count = attrs.getLength();
                if ((count < 1) || (count > 2)) {
                    throw new IIOInvalidTreeException
                        ("dqtable node must have 1 or 2 attributes", node);
                }
                elementPrecision = 0;
                tableID = getAttributeValue(node, attrs, "qtableId", 0, 3, true);
                if (node instanceof IIOMetadataNode) {
                    IIOMetadataNode ourNode = (IIOMetadataNode) node;
                    JPEGQTable table = (JPEGQTable) ourNode.getUserObject();
                    if (table == null) {
                        throw new IIOInvalidTreeException
                            ("dqtable node must have user object", node);
                    }
                    data = table.getTable();
                } else {
                    throw new IIOInvalidTreeException
                        ("dqtable node must have user object", node);
                }
            } else {
                throw new IIOInvalidTreeException
                    ("Invalid node, expected dqtable", node);
            }
        }

        protected Object clone() {
            Qtable newGuy = null;
            try {
                newGuy = (Qtable) super.clone();
            } catch (CloneNotSupportedException e) {} if (data != null) {
                newGuy.data = (int []) data.clone();
            }
            return newGuy;
        }

        IIOMetadataNode getNativeNode() {
            IIOMetadataNode node = new IIOMetadataNode("dqtable");
            node.setAttribute("elementPrecision",
                              Integer.toString(elementPrecision));
            node.setAttribute("qtableId",
                              Integer.toString(tableID));
            node.setUserObject(new JPEGQTable(data));
            return node;
        }

        void print() {
            System.out.println("Table id: " + Integer.toString(tableID));
            System.out.println("Element precision: "
                               + Integer.toString(elementPrecision));

            (new JPEGQTable(data)).toString();

        }
    }
}
