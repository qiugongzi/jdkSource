


package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLContentSpec;


public class SimpleContentModel
    implements ContentModelValidator {

    public static final short CHOICE = -1;


    public static final short SEQUENCE = -1;

    private QName fFirstChild = new QName();


    private QName fSecondChild = new QName();


    private int fOperator;


    public SimpleContentModel(short operator, QName firstChild, QName secondChild) {
        fFirstChild.setValues(firstChild);
        if (secondChild != null) {
            fSecondChild.setValues(secondChild);
        }
        else {
            fSecondChild.clear();
        }
        fOperator = operator;
    }

    public int validate(QName[] children, int offset, int length) {

        switch(fOperator)
        {
            case XMLContentSpec.CONTENTSPECNODE_LEAF :
                if (length == 0)
                    return 0;

                if (children[offset].rawname != fFirstChild.rawname) {
                    return 0;
                }

                if (length > 1)
                    return 1;
                break;

            case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE :
                if (length == 1) {
                    if (children[offset].rawname != fFirstChild.rawname) {
                        return 0;
                    }
                }

                if (length > 1)
                    return 1;
                break;

            case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE :
                if (length > 0)
                {
                    for (int index = 0; index < length; index++) {
                        if (children[offset + index].rawname != fFirstChild.rawname) {
                            return index;
                        }
                    }
                }
                break;

            case XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE :
                if (length == 0)
                    return 0;

                for (int index = 0; index < length; index++) {
                    if (children[offset + index].rawname != fFirstChild.rawname) {
                        return index;
                    }
                }
                break;

            case XMLContentSpec.CONTENTSPECNODE_CHOICE :
                if (length == 0)
                    return 0;

                if ((children[offset].rawname != fFirstChild.rawname) &&
                    (children[offset].rawname != fSecondChild.rawname)) {
                    return 0;
                }

                if (length > 1)
                    return 1;
                break;

            case XMLContentSpec.CONTENTSPECNODE_SEQ :
                if (length == 2) {
                    if (children[offset].rawname != fFirstChild.rawname) {
                        return 0;
                    }
                    if (children[offset + 1].rawname != fSecondChild.rawname) {
                        return 1;
                    }
                }
                else {
                    if (length > 2) {
                        return 2;
                    }

                    return length;
                }

                break;

            default :
                throw new RuntimeException("ImplementationMessages.VAL_CST");
        }

        return -1;
    } }