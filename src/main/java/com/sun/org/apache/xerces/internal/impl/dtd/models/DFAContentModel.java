


package com.sun.org.apache.xerces.internal.impl.dtd.models;

import java.util.HashMap;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLContentSpec;
import com.sun.org.apache.xerces.internal.xni.QName;


public class DFAContentModel
    implements ContentModelValidator {

    private static String fEpsilonString = "<<CMNODE_EPSILON>>";


    private static String fEOCString = "<<CMNODE_EOC>>";


    static {
        fEpsilonString = fEpsilonString.intern();
        fEOCString = fEOCString.intern();
    }

    private static final boolean DEBUG_VALIDATE_CONTENT = false;

    private QName fElemMap[] = null;


    private int fElemMapType[] = null;


    private int fElemMapSize = 0;


    private boolean fMixed;


    private int fEOCPos = 0;



    private boolean fFinalStateFlags[] = null;


    private CMStateSet fFollowList[] = null;


    private CMNode fHeadNode = null;


    private int fLeafCount = 0;


    private CMLeaf fLeafList[] = null;


    private int fLeafListType[] = null;

    private int fTransTable[][] = null;


    private int fTransTableSize = 0;


    private boolean fEmptyContentIsValid = false;

    private final QName fQName = new QName();

    public DFAContentModel(CMNode syntaxTree, int leafCount, boolean mixed) {
        fLeafCount = leafCount;


        fMixed = mixed;

        buildDFA(syntaxTree);
    }

    public int validate(QName[] children, int offset, int length) {

        if (DEBUG_VALIDATE_CONTENT)
            System.out.println("DFAContentModel#validateContent");

        if (length == 0) {
            if (DEBUG_VALIDATE_CONTENT) {
                System.out.println("!!! no children");
                System.out.println("elemMap="+fElemMap);
                for (int i = 0; i < fElemMap.length; i++) {
                    String uri = fElemMap[i].uri;
                    String localpart = fElemMap[i].localpart;

                    System.out.println("fElemMap["+i+"]="+uri+","+
                                       localpart+" ("+
                                       uri+", "+
                                       localpart+
                                       ')');

                }
                System.out.println("EOCIndex="+fEOCString);
            }

            return fEmptyContentIsValid ? -1 : 0;

        } int curState = 0;
        for (int childIndex = 0; childIndex < length; childIndex++)
        {
            final QName curElem = children[offset + childIndex];
            if (fMixed && curElem.localpart == null) {
                continue;
            }

            int elemIndex = 0;
            for (; elemIndex < fElemMapSize; elemIndex++)
            {
                int type = fElemMapType[elemIndex] & 0x0f ;
                if (type == XMLContentSpec.CONTENTSPECNODE_LEAF) {
                    if (fElemMap[elemIndex].rawname == curElem.rawname) {
                        break;
                    }
                }
                else if (type == XMLContentSpec.CONTENTSPECNODE_ANY) {
                    String uri = fElemMap[elemIndex].uri;
                    if (uri == null || uri == curElem.uri) {
                        break;
                    }
                }
                else if (type == XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL) {
                    if (curElem.uri == null) {
                        break;
                    }
                }
                else if (type == XMLContentSpec.CONTENTSPECNODE_ANY_OTHER) {
                    if (fElemMap[elemIndex].uri != curElem.uri) {
                        break;
                    }
                }
            }

            if (elemIndex == fElemMapSize) {
                if (DEBUG_VALIDATE_CONTENT) {
                    System.out.println("!!! didn't find it");

                    System.out.println("curElem : " +curElem );
                    for (int i=0; i<fElemMapSize; i++) {
                        System.out.println("fElemMap["+i+"] = " +fElemMap[i] );
                        System.out.println("fElemMapType["+i+"] = " +fElemMapType[i] );
                    }
                }

                return childIndex;
            }

            curState = fTransTable[curState][elemIndex];

            if (curState == -1) {
                if (DEBUG_VALIDATE_CONTENT)
                    System.out.println("!!! not a legal transition");
                return childIndex;
            }
        }

        if (DEBUG_VALIDATE_CONTENT)
            System.out.println("curState="+curState+", childCount="+length);
        if (!fFinalStateFlags[curState])
            return length;

        return -1;
    } private void buildDFA(CMNode syntaxTree)
    {
        fQName.setValues(null, fEOCString, fEOCString, null);
        CMLeaf nodeEOC = new CMLeaf(fQName);
        fHeadNode = new CMBinOp
        (
            XMLContentSpec.CONTENTSPECNODE_SEQ
            , syntaxTree
            , nodeEOC
        );

        fEOCPos = fLeafCount;
        nodeEOC.setPosition(fLeafCount++);

        fLeafList = new CMLeaf[fLeafCount];
        fLeafListType = new int[fLeafCount];
        postTreeBuildInit(fHeadNode, 0);

        fFollowList = new CMStateSet[fLeafCount];
        for (int index = 0; index < fLeafCount; index++)
            fFollowList[index] = new CMStateSet(fLeafCount);
        calcFollowList(fHeadNode);
        fElemMap = new QName[fLeafCount];
        fElemMapType = new int[fLeafCount];
        fElemMapSize = 0;
        for (int outIndex = 0; outIndex < fLeafCount; outIndex++)
        {
            fElemMap[outIndex] = new QName();



            final QName element = fLeafList[outIndex].getElement();

            int inIndex = 0;
            for (; inIndex < fElemMapSize; inIndex++)
            {
                if (fElemMap[inIndex].rawname == element.rawname) {
                    break;
                }
            }

            if (inIndex == fElemMapSize) {
                fElemMap[fElemMapSize].setValues(element);
                fElemMapType[fElemMapSize] = fLeafListType[outIndex];
                fElemMapSize++;
            }
        }
        int[] fLeafSorter = new int[fLeafCount + fElemMapSize];
        int fSortCount = 0;

        for (int elemIndex = 0; elemIndex < fElemMapSize; elemIndex++) {
            for (int leafIndex = 0; leafIndex < fLeafCount; leafIndex++) {
                    final QName leaf = fLeafList[leafIndex].getElement();
                    final QName element = fElemMap[elemIndex];
                    if (leaf.rawname == element.rawname) {
                            fLeafSorter[fSortCount++] = leafIndex;
                    }
            }
            fLeafSorter[fSortCount++] = -1;
        }



        int curArraySize = fLeafCount * 4;
        CMStateSet[] statesToDo = new CMStateSet[curArraySize];
        fFinalStateFlags = new boolean[curArraySize];
        fTransTable = new int[curArraySize][];

        CMStateSet setT = fHeadNode.firstPos();

        int unmarkedState = 0;
        int curState = 0;

        fTransTable[curState] = makeDefStateList();
        statesToDo[curState] = setT;
        curState++;



        HashMap stateTable = new HashMap();



        while (unmarkedState < curState)
        {
            setT = statesToDo[unmarkedState];
            int[] transEntry = fTransTable[unmarkedState];

            fFinalStateFlags[unmarkedState] = setT.getBit(fEOCPos);

            unmarkedState++;

            CMStateSet newSet = null;

            int sorterIndex = 0;

            for (int elemIndex = 0; elemIndex < fElemMapSize; elemIndex++)
            {
                if (newSet == null)
                    newSet = new CMStateSet(fLeafCount);
                else
                    newSet.zeroBits();


                int leafIndex = fLeafSorter[sorterIndex++];

                while (leafIndex != -1) {
                if (setT.getBit(leafIndex))
                    {
                        newSet.union(fFollowList[leafIndex]);
                            }

                   leafIndex = fLeafSorter[sorterIndex++];
        }


                if (!newSet.isEmpty())
                {
                    Integer stateObj = (Integer)stateTable.get(newSet);
            int stateIndex = (stateObj == null ? curState : stateObj.intValue());


                    if (stateIndex == curState)
                    {
                        statesToDo[curState] = newSet;
                        fTransTable[curState] = makeDefStateList();


                        stateTable.put(newSet, new Integer(curState));


                        curState++;

                        newSet = null;
                    }

                    transEntry[elemIndex] = stateIndex;

                    if (curState == curArraySize)
                    {
                        final int newSize = (int)(curArraySize * 1.5);
                        CMStateSet[] newToDo = new CMStateSet[newSize];
                        boolean[] newFinalFlags = new boolean[newSize];
                        int[][] newTransTable = new int[newSize][];

                        System.arraycopy(statesToDo, 0, newToDo, 0, curArraySize);
                        System.arraycopy(fFinalStateFlags, 0, newFinalFlags, 0, curArraySize);
                        System.arraycopy(fTransTable, 0, newTransTable, 0, curArraySize);

                        curArraySize = newSize;
                        statesToDo = newToDo;
                        fFinalStateFlags = newFinalFlags;
                        fTransTable = newTransTable;
                    }
                }
            }
        }

        fEmptyContentIsValid = ((CMBinOp)fHeadNode).getLeft().isNullable();

        if (DEBUG_VALIDATE_CONTENT)
            dumpTree(fHeadNode, 0);
        fHeadNode = null;
        fLeafList = null;
        fFollowList = null;

    }


    private void calcFollowList(CMNode nodeCur)
    {
        if (nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_CHOICE)
        {
            calcFollowList(((CMBinOp)nodeCur).getLeft());
            calcFollowList(((CMBinOp)nodeCur).getRight());
        }
         else if (nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_SEQ)
        {
            calcFollowList(((CMBinOp)nodeCur).getLeft());
            calcFollowList(((CMBinOp)nodeCur).getRight());

            final CMStateSet last  = ((CMBinOp)nodeCur).getLeft().lastPos();
            final CMStateSet first = ((CMBinOp)nodeCur).getRight().firstPos();

            for (int index = 0; index < fLeafCount; index++)
            {
                if (last.getBit(index))
                    fFollowList[index].union(first);
            }
        }

         else if (nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE
            || nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE)
        {
            calcFollowList(((CMUniOp)nodeCur).getChild());

            final CMStateSet first = nodeCur.firstPos();
            final CMStateSet last  = nodeCur.lastPos();

            for (int index = 0; index < fLeafCount; index++)
            {
                if (last.getBit(index))
                    fFollowList[index].union(first);
            }
        }

        else if (nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE) {
            calcFollowList(((CMUniOp)nodeCur).getChild());
        }

    }


    private void dumpTree(CMNode nodeCur, int level)
    {
        for (int index = 0; index < level; index++)
            System.out.print("   ");

        int type = nodeCur.type();
        if ((type == XMLContentSpec.CONTENTSPECNODE_CHOICE)
        ||  (type == XMLContentSpec.CONTENTSPECNODE_SEQ))
        {
            if (type == XMLContentSpec.CONTENTSPECNODE_CHOICE)
                System.out.print("Choice Node ");
            else
                System.out.print("Seq Node ");

            if (nodeCur.isNullable())
                System.out.print("Nullable ");

            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());

            dumpTree(((CMBinOp)nodeCur).getLeft(), level+1);
            dumpTree(((CMBinOp)nodeCur).getRight(), level+1);
        }
         else if (nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE)
        {
            System.out.print("Rep Node ");

            if (nodeCur.isNullable())
                System.out.print("Nullable ");

            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());

            dumpTree(((CMUniOp)nodeCur).getChild(), level+1);
        }
         else if (nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_LEAF)
        {
            System.out.print
            (
                "Leaf: (pos="
                + ((CMLeaf)nodeCur).getPosition()
                + "), "
                + ((CMLeaf)nodeCur).getElement()
                + "(elemIndex="
                + ((CMLeaf)nodeCur).getElement()
                + ") "
            );

            if (nodeCur.isNullable())
                System.out.print(" Nullable ");

            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());
        }
         else
        {
            throw new RuntimeException("ImplementationMessages.VAL_NIICM");
        }
    }



    private int[] makeDefStateList()
    {
        int[] retArray = new int[fElemMapSize];
        for (int index = 0; index < fElemMapSize; index++)
            retArray[index] = -1;
        return retArray;
    }


    private int postTreeBuildInit(CMNode nodeCur, int curIndex)
    {
        nodeCur.setMaxStates(fLeafCount);

        if ((nodeCur.type() & 0x0f) == XMLContentSpec.CONTENTSPECNODE_ANY ||
            (nodeCur.type() & 0x0f) == XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL ||
            (nodeCur.type() & 0x0f) == XMLContentSpec.CONTENTSPECNODE_ANY_OTHER) {
            QName qname = new QName(null, null, null, ((CMAny)nodeCur).getURI());
            fLeafList[curIndex] = new CMLeaf(qname, ((CMAny)nodeCur).getPosition());
            fLeafListType[curIndex] = nodeCur.type();
            curIndex++;
        }
        else if ((nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_CHOICE)
        ||  (nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_SEQ))
        {
            curIndex = postTreeBuildInit(((CMBinOp)nodeCur).getLeft(), curIndex);
            curIndex = postTreeBuildInit(((CMBinOp)nodeCur).getRight(), curIndex);
        }
         else if (nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE
             || nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE
             || nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE)
        {
            curIndex = postTreeBuildInit(((CMUniOp)nodeCur).getChild(), curIndex);
        }
         else if (nodeCur.type() == XMLContentSpec.CONTENTSPECNODE_LEAF)
        {
            final QName node = ((CMLeaf)nodeCur).getElement();
            if (node.localpart != fEpsilonString) {
                fLeafList[curIndex] = (CMLeaf)nodeCur;
                fLeafListType[curIndex] = XMLContentSpec.CONTENTSPECNODE_LEAF;
                curIndex++;
            }
        }
         else
        {
            throw new RuntimeException("ImplementationMessages.VAL_NIICM: type="+nodeCur.type());
        }
        return curIndex;
    }

}