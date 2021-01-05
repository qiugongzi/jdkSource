


package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;

import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;


public class XSDFACM
    implements XSCMValidator {

    private static final boolean DEBUG = false;

    private static final boolean DEBUG_VALIDATE_CONTENT = false;

    private Object fElemMap[] = null;


    private int fElemMapType[] = null;


    private int fElemMapId[] = null;


    private int fElemMapSize = 0;


    private boolean fFinalStateFlags[] = null;


    private CMStateSet fFollowList[] = null;


    private CMNode fHeadNode = null;


    private int fLeafCount = 0;


    private XSCMLeaf fLeafList[] = null;


    private int fLeafListType[] = null;


    private int fTransTable[][] = null;

    private Occurence [] fCountingStates = null;
    static final class Occurence {
        final int minOccurs;
        final int maxOccurs;
        final int elemIndex;
        public Occurence (XSCMRepeatingLeaf leaf, int elemIndex) {
            minOccurs = leaf.getMinOccurs();
            maxOccurs = leaf.getMaxOccurs();
            this.elemIndex = elemIndex;
        }
        public String toString() {
            return "minOccurs=" + minOccurs
                + ";maxOccurs=" +
                ((maxOccurs != SchemaSymbols.OCCURRENCE_UNBOUNDED)
                        ? Integer.toString(maxOccurs) : "unbounded");
        }
    }


    private int fTransTableSize = 0;


    private int fElemMapCounter[];


    private int fElemMapCounterLowerBound[];


    private int fElemMapCounterUpperBound[];   public XSDFACM(CMNode syntaxTree, int leafCount) {

        fLeafCount = leafCount;

        if(DEBUG_VALIDATE_CONTENT) {
            XSDFACM.time -= System.currentTimeMillis();
        }

        buildDFA(syntaxTree);

        if(DEBUG_VALIDATE_CONTENT) {
            XSDFACM.time += System.currentTimeMillis();
            System.out.println("DFA build: " + XSDFACM.time + "ms");
        }
    }

    private static long time = 0;

    public boolean isFinalState (int state) {
        return (state < 0)? false :
            fFinalStateFlags[state];
    }


    public Object oneTransition(QName curElem, int[] state, SubstitutionGroupHandler subGroupHandler) {
        int curState = state[0];

        if(curState == XSCMValidator.FIRST_ERROR || curState == XSCMValidator.SUBSEQUENT_ERROR) {
            if(curState == XSCMValidator.FIRST_ERROR)
                state[0] = XSCMValidator.SUBSEQUENT_ERROR;

            return findMatchingDecl(curElem, subGroupHandler);
        }

        int nextState = 0;
        int elemIndex = 0;
        Object matchingDecl = null;

        for (; elemIndex < fElemMapSize; elemIndex++) {
            nextState = fTransTable[curState][elemIndex];
            if (nextState == -1)
                continue;
            int type = fElemMapType[elemIndex] ;
            if (type == XSParticleDecl.PARTICLE_ELEMENT) {
                matchingDecl = subGroupHandler.getMatchingElemDecl(curElem, (XSElementDecl)fElemMap[elemIndex]);
                if (matchingDecl != null) {
                    if (fElemMapCounter[elemIndex] >= 0) {
                        fElemMapCounter[elemIndex]++;
                    }
                    break;
                }
            }
            else if (type == XSParticleDecl.PARTICLE_WILDCARD) {
                if (((XSWildcardDecl)fElemMap[elemIndex]).allowNamespace(curElem.uri)) {
                    matchingDecl = fElemMap[elemIndex];
                    if (fElemMapCounter[elemIndex] >= 0) {
                        fElemMapCounter[elemIndex]++;
                    }
                    break;
                }
            }
        }

        if (elemIndex == fElemMapSize) {
            state[1] = state[0];
            state[0] = XSCMValidator.FIRST_ERROR;
            return findMatchingDecl(curElem, subGroupHandler);
        }

        if (fCountingStates != null) {
            Occurence o = fCountingStates[curState];
            if (o != null) {
                if (curState == nextState) {
                    if (++state[2] > o.maxOccurs &&
                        o.maxOccurs != SchemaSymbols.OCCURRENCE_UNBOUNDED) {
                        return findMatchingDecl(curElem, state, subGroupHandler, elemIndex);
                    }
                }
                else if (state[2] < o.minOccurs) {
                    state[1] = state[0];
                    state[0] = XSCMValidator.FIRST_ERROR;
                    return findMatchingDecl(curElem, subGroupHandler);
                }
                else {
                    o = fCountingStates[nextState];
                    if (o != null) {
                        state[2] = (elemIndex == o.elemIndex) ? 1 : 0;
                    }
                }
            }
            else {
                o = fCountingStates[nextState];
                if (o != null) {
                    state[2] = (elemIndex == o.elemIndex) ? 1 : 0;
                }
            }
        }

        state[0] = nextState;
        return matchingDecl;
    } Object findMatchingDecl(QName curElem, SubstitutionGroupHandler subGroupHandler) {
        Object matchingDecl = null;

        for (int elemIndex = 0; elemIndex < fElemMapSize; elemIndex++) {
            int type = fElemMapType[elemIndex] ;
            if (type == XSParticleDecl.PARTICLE_ELEMENT) {
                matchingDecl = subGroupHandler.getMatchingElemDecl(curElem, (XSElementDecl)fElemMap[elemIndex]);
                if (matchingDecl != null) {
                    return matchingDecl;
                }
            }
            else if (type == XSParticleDecl.PARTICLE_WILDCARD) {
                if(((XSWildcardDecl)fElemMap[elemIndex]).allowNamespace(curElem.uri))
                    return fElemMap[elemIndex];
            }
        }

        return null;
    } Object findMatchingDecl(QName curElem, int[] state, SubstitutionGroupHandler subGroupHandler, int elemIndex) {

        int curState = state[0];
        int nextState = 0;
        Object matchingDecl = null;

        while (++elemIndex < fElemMapSize) {
            nextState = fTransTable[curState][elemIndex];
            if (nextState == -1)
                continue;
            int type = fElemMapType[elemIndex] ;
            if (type == XSParticleDecl.PARTICLE_ELEMENT) {
                matchingDecl = subGroupHandler.getMatchingElemDecl(curElem, (XSElementDecl)fElemMap[elemIndex]);
                if (matchingDecl != null) {
                    break;
                }
            }
            else if (type == XSParticleDecl.PARTICLE_WILDCARD) {
                if (((XSWildcardDecl)fElemMap[elemIndex]).allowNamespace(curElem.uri)) {
                    matchingDecl = fElemMap[elemIndex];
                    break;
                }
            }
        }

        if (elemIndex == fElemMapSize) {
            state[1] = state[0];
            state[0] = XSCMValidator.FIRST_ERROR;
            return findMatchingDecl(curElem, subGroupHandler);
        }

        state[0] = nextState;
        final Occurence o = fCountingStates[nextState];
        if (o != null) {
            state[2] = (elemIndex == o.elemIndex) ? 1 : 0;
        }
        return matchingDecl;
    } public int[] startContentModel() {
        for (int elemIndex = 0; elemIndex < fElemMapSize; elemIndex++) {
            if (fElemMapCounter[elemIndex] != -1) {
                fElemMapCounter[elemIndex] = 0;
            }
        }
        return new int [3];
    } public boolean endContentModel(int[] state) {
        final int curState = state[0];
        if (fFinalStateFlags[curState]) {
            if (fCountingStates != null) {
                Occurence o = fCountingStates[curState];
                if (o != null && state[2] < o.minOccurs) {
                    return false;
                }
            }
            return true;
        }
        return false;
    } private void buildDFA(CMNode syntaxTree) {
        int EOCPos = fLeafCount;
        XSCMLeaf nodeEOC = new XSCMLeaf(XSParticleDecl.PARTICLE_ELEMENT, null, -1, fLeafCount++);
        fHeadNode = new XSCMBinOp(
            XSModelGroupImpl.MODELGROUP_SEQUENCE,
            syntaxTree,
            nodeEOC
        );

        fLeafList = new XSCMLeaf[fLeafCount];
        fLeafListType = new int[fLeafCount];
        postTreeBuildInit(fHeadNode);

        fFollowList = new CMStateSet[fLeafCount];
        for (int index = 0; index < fLeafCount; index++)
            fFollowList[index] = new CMStateSet(fLeafCount);
        calcFollowList(fHeadNode);
        fElemMap = new Object[fLeafCount];
        fElemMapType = new int[fLeafCount];
        fElemMapId = new int[fLeafCount];

        fElemMapCounter = new int[fLeafCount];
        fElemMapCounterLowerBound = new int[fLeafCount];
        fElemMapCounterUpperBound = new int[fLeafCount];

        fElemMapSize = 0;
        Occurence [] elemOccurenceMap = null;

        for (int outIndex = 0; outIndex < fLeafCount; outIndex++) {
            fElemMap[outIndex] = null;

            int inIndex = 0;
            final int id = fLeafList[outIndex].getParticleId();
            for (; inIndex < fElemMapSize; inIndex++) {
                if (id == fElemMapId[inIndex])
                    break;
            }

            if (inIndex == fElemMapSize) {
                XSCMLeaf leaf = fLeafList[outIndex];
                fElemMap[fElemMapSize] = leaf.getLeaf();
                if (leaf instanceof XSCMRepeatingLeaf) {
                    if (elemOccurenceMap == null) {
                        elemOccurenceMap = new Occurence[fLeafCount];
                    }
                    elemOccurenceMap[fElemMapSize] = new Occurence((XSCMRepeatingLeaf) leaf, fElemMapSize);
                }

                fElemMapType[fElemMapSize] = fLeafListType[outIndex];
                fElemMapId[fElemMapSize] = id;

                int[] bounds = (int[]) leaf.getUserData();
                if (bounds != null) {
                    fElemMapCounter[fElemMapSize] = 0;
                    fElemMapCounterLowerBound[fElemMapSize] = bounds[0];
                    fElemMapCounterUpperBound[fElemMapSize] = bounds[1];
                } else {
                    fElemMapCounter[fElemMapSize] = -1;
                    fElemMapCounterLowerBound[fElemMapSize] = -1;
                    fElemMapCounterUpperBound[fElemMapSize] = -1;
                }

                fElemMapSize++;
            }
        }

        if (DEBUG) {
            if (fElemMapId[fElemMapSize-1] != -1)
                System.err.println("interal error in DFA: last element is not EOC.");
        }
        fElemMapSize--;



        int[] fLeafSorter = new int[fLeafCount + fElemMapSize];
        int fSortCount = 0;

        for (int elemIndex = 0; elemIndex < fElemMapSize; elemIndex++) {
            final int id = fElemMapId[elemIndex];
            for (int leafIndex = 0; leafIndex < fLeafCount; leafIndex++) {
                if (id == fLeafList[leafIndex].getParticleId())
                    fLeafSorter[fSortCount++] = leafIndex;
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



        while (unmarkedState < curState) {
            setT = statesToDo[unmarkedState];
            int[] transEntry = fTransTable[unmarkedState];

            fFinalStateFlags[unmarkedState] = setT.getBit(EOCPos);

            unmarkedState++;

            CMStateSet newSet = null;

            int sorterIndex = 0;

            for (int elemIndex = 0; elemIndex < fElemMapSize; elemIndex++) {
                if (newSet == null)
                    newSet = new CMStateSet(fLeafCount);
                else
                    newSet.zeroBits();


                int leafIndex = fLeafSorter[sorterIndex++];

                while (leafIndex != -1) {
                    if (setT.getBit(leafIndex)) {
                        newSet.union(fFollowList[leafIndex]);
                    }

                   leafIndex = fLeafSorter[sorterIndex++];
                }


                if (!newSet.isEmpty()) {
                    Integer stateObj = (Integer)stateTable.get(newSet);
                    int stateIndex = (stateObj == null ? curState : stateObj.intValue());


                    if (stateIndex == curState) {
                        statesToDo[curState] = newSet;
                        fTransTable[curState] = makeDefStateList();


                        stateTable.put(newSet, new Integer(curState));


                        curState++;

                        newSet = null;
                    }

                    transEntry[elemIndex] = stateIndex;

                    if (curState == curArraySize) {
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

        if (elemOccurenceMap != null) {
            fCountingStates = new Occurence[curState];
            for (int i = 0; i < curState; ++i) {
                int [] transitions = fTransTable[i];
                for (int j = 0; j < transitions.length; ++j) {
                    if (i == transitions[j]) {
                        fCountingStates[i] = elemOccurenceMap[j];
                        break;
                    }
                }
            }
        }

        if (DEBUG_VALIDATE_CONTENT)
            dumpTree(fHeadNode, 0);
        fHeadNode = null;
        fLeafList = null;
        fFollowList = null;
        fLeafListType = null;
        fElemMapId = null;
    }


    private void calcFollowList(CMNode nodeCur) {
        if (nodeCur.type() == XSModelGroupImpl.MODELGROUP_CHOICE) {
            calcFollowList(((XSCMBinOp)nodeCur).getLeft());
            calcFollowList(((XSCMBinOp)nodeCur).getRight());
        }
         else if (nodeCur.type() == XSModelGroupImpl.MODELGROUP_SEQUENCE) {
            calcFollowList(((XSCMBinOp)nodeCur).getLeft());
            calcFollowList(((XSCMBinOp)nodeCur).getRight());

            final CMStateSet last  = ((XSCMBinOp)nodeCur).getLeft().lastPos();
            final CMStateSet first = ((XSCMBinOp)nodeCur).getRight().firstPos();

            for (int index = 0; index < fLeafCount; index++) {
                if (last.getBit(index))
                    fFollowList[index].union(first);
            }
        }
         else if (nodeCur.type() == XSParticleDecl.PARTICLE_ZERO_OR_MORE
        || nodeCur.type() == XSParticleDecl.PARTICLE_ONE_OR_MORE) {
            calcFollowList(((XSCMUniOp)nodeCur).getChild());

            final CMStateSet first = nodeCur.firstPos();
            final CMStateSet last  = nodeCur.lastPos();

            for (int index = 0; index < fLeafCount; index++) {
                if (last.getBit(index))
                    fFollowList[index].union(first);
            }
        }

        else if (nodeCur.type() == XSParticleDecl.PARTICLE_ZERO_OR_ONE) {
            calcFollowList(((XSCMUniOp)nodeCur).getChild());
        }

    }


    private void dumpTree(CMNode nodeCur, int level) {
        for (int index = 0; index < level; index++)
            System.out.print("   ");

        int type = nodeCur.type();

        switch(type ) {

        case XSModelGroupImpl.MODELGROUP_CHOICE:
        case XSModelGroupImpl.MODELGROUP_SEQUENCE: {
            if (type == XSModelGroupImpl.MODELGROUP_CHOICE)
                System.out.print("Choice Node ");
            else
                System.out.print("Seq Node ");

            if (nodeCur.isNullable())
                System.out.print("Nullable ");

            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());

            dumpTree(((XSCMBinOp)nodeCur).getLeft(), level+1);
            dumpTree(((XSCMBinOp)nodeCur).getRight(), level+1);
            break;
        }
        case XSParticleDecl.PARTICLE_ZERO_OR_MORE:
        case XSParticleDecl.PARTICLE_ONE_OR_MORE:
        case XSParticleDecl.PARTICLE_ZERO_OR_ONE: {
            System.out.print("Rep Node ");

            if (nodeCur.isNullable())
                System.out.print("Nullable ");

            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());

            dumpTree(((XSCMUniOp)nodeCur).getChild(), level+1);
            break;
        }
        case XSParticleDecl.PARTICLE_ELEMENT: {
            System.out.print
            (
                "Leaf: (pos="
                + ((XSCMLeaf)nodeCur).getPosition()
                + "), "
                + "(elemIndex="
                + ((XSCMLeaf)nodeCur).getLeaf()
                + ") "
            );

            if (nodeCur.isNullable())
                System.out.print(" Nullable ");

            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());
            break;
        }
        case XSParticleDecl.PARTICLE_WILDCARD:
              System.out.print("Any Node: ");

            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());
            break;
        default: {
            throw new RuntimeException("ImplementationMessages.VAL_NIICM");
        }
        }

    }



    private int[] makeDefStateList()
    {
        int[] retArray = new int[fElemMapSize];
        for (int index = 0; index < fElemMapSize; index++)
            retArray[index] = -1;
        return retArray;
    }


    private void postTreeBuildInit(CMNode nodeCur) throws RuntimeException {
        nodeCur.setMaxStates(fLeafCount);

        XSCMLeaf leaf = null;
        int pos = 0;
        if (nodeCur.type() == XSParticleDecl.PARTICLE_WILDCARD) {
            leaf = (XSCMLeaf)nodeCur;
            pos = leaf.getPosition();
            fLeafList[pos] = leaf;
            fLeafListType[pos] = XSParticleDecl.PARTICLE_WILDCARD;
        }
        else if ((nodeCur.type() == XSModelGroupImpl.MODELGROUP_CHOICE) ||
                 (nodeCur.type() == XSModelGroupImpl.MODELGROUP_SEQUENCE)) {
            postTreeBuildInit(((XSCMBinOp)nodeCur).getLeft());
            postTreeBuildInit(((XSCMBinOp)nodeCur).getRight());
        }
        else if (nodeCur.type() == XSParticleDecl.PARTICLE_ZERO_OR_MORE ||
                 nodeCur.type() == XSParticleDecl.PARTICLE_ONE_OR_MORE ||
                 nodeCur.type() == XSParticleDecl.PARTICLE_ZERO_OR_ONE) {
            postTreeBuildInit(((XSCMUniOp)nodeCur).getChild());
        }
        else if (nodeCur.type() == XSParticleDecl.PARTICLE_ELEMENT) {
            leaf = (XSCMLeaf)nodeCur;
            pos = leaf.getPosition();
            fLeafList[pos] = leaf;
            fLeafListType[pos] = XSParticleDecl.PARTICLE_ELEMENT;
        }
        else {
            throw new RuntimeException("ImplementationMessages.VAL_NIICM");
        }
    }


    public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler subGroupHandler) throws XMLSchemaException {
        byte conflictTable[][] = new byte[fElemMapSize][fElemMapSize];

        for (int i = 0; i < fTransTable.length && fTransTable[i] != null; i++) {
            for (int j = 0; j < fElemMapSize; j++) {
                for (int k = j+1; k < fElemMapSize; k++) {
                    if (fTransTable[i][j] != -1 &&
                        fTransTable[i][k] != -1) {
                        if (conflictTable[j][k] == 0) {
                            if (XSConstraints.overlapUPA
                                    (fElemMap[j], fElemMap[k],
                                            subGroupHandler)) {
                                if (fCountingStates != null) {
                                    Occurence o = fCountingStates[i];
                                    if (o != null &&
                                        fTransTable[i][j] == i ^ fTransTable[i][k] == i &&
                                        o.minOccurs == o.maxOccurs) {
                                        conflictTable[j][k] = (byte) -1;
                                        continue;
                                    }
                                }
                                conflictTable[j][k] = (byte) 1;
                            }
                            else {
                                conflictTable[j][k] = (byte) -1;
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < fElemMapSize; i++) {
            for (int j = 0; j < fElemMapSize; j++) {
                if (conflictTable[i][j] == 1) {
                    throw new XMLSchemaException("cos-nonambig", new Object[]{fElemMap[i].toString(),
                                                                              fElemMap[j].toString()});
                }
            }
        }

        for (int i = 0; i < fElemMapSize; i++) {
            if (fElemMapType[i] == XSParticleDecl.PARTICLE_WILDCARD) {
                XSWildcardDecl wildcard = (XSWildcardDecl)fElemMap[i];
                if (wildcard.fType == XSWildcardDecl.NSCONSTRAINT_LIST ||
                    wildcard.fType == XSWildcardDecl.NSCONSTRAINT_NOT) {
                    return true;
                }
            }
        }

        return false;
    }


    public Vector whatCanGoHere(int[] state) {
        int curState = state[0];
        if (curState < 0)
            curState = state[1];
        Occurence o = (fCountingStates != null) ?
                fCountingStates[curState] : null;
        int count = state[2];

        Vector ret = new Vector();
        for (int elemIndex = 0; elemIndex < fElemMapSize; elemIndex++) {
            int nextState = fTransTable[curState][elemIndex];
            if (nextState != -1) {
                if (o != null) {
                    if (curState == nextState) {
                        if (count >= o.maxOccurs &&
                            o.maxOccurs != SchemaSymbols.OCCURRENCE_UNBOUNDED) {
                            continue;
                        }
                    }
                    else if (count < o.minOccurs) {
                        continue;
                    }
                }
                ret.addElement(fElemMap[elemIndex]);
            }
        }
        return ret;
    }


    public ArrayList checkMinMaxBounds() {
        ArrayList result = null;
        for (int elemIndex = 0; elemIndex < fElemMapSize; elemIndex++) {
            int count = fElemMapCounter[elemIndex];
            if (count == -1) {
                continue;
            }
            final int minOccurs = fElemMapCounterLowerBound[elemIndex];
            final int maxOccurs = fElemMapCounterUpperBound[elemIndex];
            if (count < minOccurs) {
                if (result == null) result = new ArrayList();
                result.add("cvc-complex-type.2.4.b");
                result.add("{" + fElemMap[elemIndex] + "}");
            }
            if (maxOccurs != -1 && count > maxOccurs) {
                if (result == null) result = new ArrayList();
                result.add("cvc-complex-type.2.4.e");
                result.add("{" + fElemMap[elemIndex] + "}");
            }
        }
        return result;
    }

}