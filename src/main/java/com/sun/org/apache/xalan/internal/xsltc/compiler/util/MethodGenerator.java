



package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.FLOAD;
import com.sun.org.apache.bcel.internal.generic.FSTORE;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.IfInstruction;
import com.sun.org.apache.bcel.internal.generic.IndexedInstruction;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.InstructionTargeter;
import com.sun.org.apache.bcel.internal.generic.LLOAD;
import com.sun.org.apache.bcel.internal.generic.LSTORE;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.LocalVariableInstruction;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.org.apache.bcel.internal.generic.TargetLostException;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;


public class MethodGenerator extends MethodGen
    implements com.sun.org.apache.xalan.internal.xsltc.compiler.Constants {
    protected static final int INVALID_INDEX   = -1;

    private static final String START_ELEMENT_SIG
        = "(" + STRING_SIG + ")V";
    private static final String END_ELEMENT_SIG
        = START_ELEMENT_SIG;

    private InstructionList _mapTypeSub;

    private static final int DOM_INDEX       = 1;
    private static final int ITERATOR_INDEX  = 2;
    private static final int HANDLER_INDEX   = 3;

    private static final int MAX_METHOD_SIZE = 65535;
    private static final int MAX_BRANCH_TARGET_OFFSET = 32767;
    private static final int MIN_BRANCH_TARGET_OFFSET = -32768;

    private static final int TARGET_METHOD_SIZE = 60000;
    private static final int MINIMUM_OUTLINEABLE_CHUNK_SIZE = 1000;

    private Instruction       _iloadCurrent;
    private Instruction       _istoreCurrent;
    private final Instruction _astoreHandler;
    private final Instruction _aloadHandler;
    private final Instruction _astoreIterator;
    private final Instruction _aloadIterator;
    private final Instruction _aloadDom;
    private final Instruction _astoreDom;

    private final Instruction _startElement;
    private final Instruction _endElement;
    private final Instruction _startDocument;
    private final Instruction _endDocument;
    private final Instruction _attribute;
    private final Instruction _uniqueAttribute;
    private final Instruction _namespace;

    private final Instruction _setStartNode;
    private final Instruction _reset;
    private final Instruction _nextNode;

    private SlotAllocator _slotAllocator;
    private boolean _allocatorInit = false;
    private LocalVariableRegistry _localVariableRegistry;

        private Map<Pattern, InstructionList> _preCompiled = new HashMap<>();


    public MethodGenerator(int access_flags, Type return_type,
                           Type[] arg_types, String[] arg_names,
                           String method_name, String class_name,
                           InstructionList il, ConstantPoolGen cpg) {
        super(access_flags, return_type, arg_types, arg_names, method_name,
              class_name, il, cpg);

        _astoreHandler  = new ASTORE(HANDLER_INDEX);
        _aloadHandler   = new ALOAD(HANDLER_INDEX);
        _astoreIterator = new ASTORE(ITERATOR_INDEX);
        _aloadIterator  = new ALOAD(ITERATOR_INDEX);
        _aloadDom       = new ALOAD(DOM_INDEX);
        _astoreDom      = new ASTORE(DOM_INDEX);

        final int startElement =
            cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                      "startElement",
                                      START_ELEMENT_SIG);
        _startElement = new INVOKEINTERFACE(startElement, 2);

        final int endElement =
            cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                      "endElement",
                                      END_ELEMENT_SIG);
        _endElement = new INVOKEINTERFACE(endElement, 2);

        final int attribute =
            cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                      "addAttribute",
                                      "("
                                      + STRING_SIG
                                      + STRING_SIG
                                      + ")V");
        _attribute = new INVOKEINTERFACE(attribute, 3);

        final int uniqueAttribute =
            cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                      "addUniqueAttribute",
                                      "("
                                      + STRING_SIG
                                      + STRING_SIG
                                      + "I)V");
        _uniqueAttribute = new INVOKEINTERFACE(uniqueAttribute, 4);

        final int namespace =
            cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                      "namespaceAfterStartElement",
                                      "("
                                      + STRING_SIG
                                      + STRING_SIG
                                      + ")V");
        _namespace = new INVOKEINTERFACE(namespace, 3);

        int index = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                              "startDocument",
                                              "()V");
        _startDocument = new INVOKEINTERFACE(index, 1);

        index = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                          "endDocument",
                                          "()V");
        _endDocument = new INVOKEINTERFACE(index, 1);


        index = cpg.addInterfaceMethodref(NODE_ITERATOR,
                                          SET_START_NODE,
                                          SET_START_NODE_SIG);
        _setStartNode = new INVOKEINTERFACE(index, 2);

        index = cpg.addInterfaceMethodref(NODE_ITERATOR,
                                          "reset", "()"+NODE_ITERATOR_SIG);
        _reset = new INVOKEINTERFACE(index, 1);

        index = cpg.addInterfaceMethodref(NODE_ITERATOR, NEXT, NEXT_SIG);
        _nextNode = new INVOKEINTERFACE(index, 1);

        _slotAllocator = new SlotAllocator();
        _slotAllocator.initialize(getLocalVariableRegistry().getLocals(false));
        _allocatorInit = true;
    }


    public LocalVariableGen addLocalVariable(String name, Type type,
                                             InstructionHandle start,
                                             InstructionHandle end)
    {
        LocalVariableGen lvg;

        if (_allocatorInit) {
            lvg = addLocalVariable2(name, type, start);
        } else {
            lvg = super.addLocalVariable(name, type, start, end);
            getLocalVariableRegistry().registerLocalVariable(lvg);
        }
        return lvg;
    }

    public LocalVariableGen addLocalVariable2(String name, Type type,
                                              InstructionHandle start)
    {
        LocalVariableGen lvg = super.addLocalVariable(name, type,
                                              _slotAllocator.allocateSlot(type),
                                              start, null);
        getLocalVariableRegistry().registerLocalVariable(lvg);
        return lvg;
    }
    private LocalVariableRegistry getLocalVariableRegistry() {
        if (_localVariableRegistry == null) {
            _localVariableRegistry = new LocalVariableRegistry();
        }

        return _localVariableRegistry;
    }


    protected class LocalVariableRegistry {

        protected ArrayList _variables = new ArrayList();


        protected HashMap _nameToLVGMap = new HashMap();


        protected void registerLocalVariable(LocalVariableGen lvg) {
            int slot = lvg.getIndex();

            int registrySize = _variables.size();

            if (slot >= registrySize) {
                for (int i = registrySize; i < slot; i++) {
                    _variables.add(null);
                }
                _variables.add(lvg);
            } else {
                Object localsInSlot = _variables.get(slot);
                if (localsInSlot != null) {
                    if (localsInSlot instanceof LocalVariableGen) {
                        ArrayList listOfLocalsInSlot = new ArrayList();
                        listOfLocalsInSlot.add(localsInSlot);
                        listOfLocalsInSlot.add(lvg);
                        _variables.set(slot, listOfLocalsInSlot);
                    } else {
                        ((ArrayList) localsInSlot).add(lvg);
                    }
                } else {
                    _variables.set(slot, lvg);
                }
            }

            registerByName(lvg);
        }


        protected LocalVariableGen lookupRegisteredLocalVariable(int slot,
                                                                 int offset) {
            Object localsInSlot = (_variables != null) ? _variables.get(slot)
                                                       : null;

            if (localsInSlot != null) {
                if (localsInSlot instanceof LocalVariableGen) {
                    LocalVariableGen lvg = (LocalVariableGen)localsInSlot;
                    if (offsetInLocalVariableGenRange(lvg, offset)) {
                        return lvg;
                    }
                } else {
                    ArrayList listOfLocalsInSlot = (ArrayList) localsInSlot;
                    int size = listOfLocalsInSlot.size();

                    for (int i = 0; i < size; i++) {
                        LocalVariableGen lvg =
                            (LocalVariableGen)listOfLocalsInSlot.get(i);
                        if (offsetInLocalVariableGenRange(lvg, offset)) {
                            return lvg;
                        }
                    }
                }
            }

            return null;
        }


        protected void registerByName(LocalVariableGen lvg) {
            Object duplicateNameEntry = _nameToLVGMap.get(lvg.getName());

            if (duplicateNameEntry == null) {
                _nameToLVGMap.put(lvg.getName(), lvg);
            } else {
                ArrayList sameNameList;

                if (duplicateNameEntry instanceof ArrayList) {
                    sameNameList = (ArrayList) duplicateNameEntry;
                    sameNameList.add(lvg);
                } else {
                    sameNameList = new ArrayList();
                    sameNameList.add(duplicateNameEntry);
                    sameNameList.add(lvg);
                }

                _nameToLVGMap.put(lvg.getName(), sameNameList);
            }
        }


        protected void removeByNameTracking(LocalVariableGen lvg) {
            Object duplicateNameEntry = _nameToLVGMap.get(lvg.getName());

            if (duplicateNameEntry instanceof ArrayList) {
                ArrayList sameNameList = (ArrayList) duplicateNameEntry;
                for (int i = 0; i < sameNameList.size(); i++) {
                    if (sameNameList.get(i) == lvg) {
                        sameNameList.remove(i);
                        break;
                    }
                }
            } else {
                _nameToLVGMap.remove(lvg);
            }
        }


        protected LocalVariableGen lookUpByName(String name) {
            LocalVariableGen lvg = null;
            Object duplicateNameEntry = _nameToLVGMap.get(name);

            if (duplicateNameEntry instanceof ArrayList) {
                ArrayList sameNameList = (ArrayList) duplicateNameEntry;

                for (int i = 0; i < sameNameList.size(); i++) {
                    lvg = (LocalVariableGen)sameNameList.get(i);
                    if (lvg.getName() == name) {
                        break;
                    }
                }
            } else {
                lvg = (LocalVariableGen) duplicateNameEntry;
            }

            return lvg;
        }


        protected LocalVariableGen[] getLocals(boolean includeRemoved) {
            LocalVariableGen[] locals = null;
            ArrayList allVarsEverDeclared = new ArrayList();

            if (includeRemoved) {
                int slotCount = allVarsEverDeclared.size();

                for (int i = 0; i < slotCount; i++) {
                    Object slotEntries = _variables.get(i);
                    if (slotEntries != null) {
                        if (slotEntries instanceof ArrayList) {
                            ArrayList slotList = (ArrayList) slotEntries;

                            for (int j = 0; j < slotList.size(); j++) {
                                allVarsEverDeclared.add(slotList.get(i));
                            }
                        } else {
                            allVarsEverDeclared.add(slotEntries);
                        }
                    }
                }
            } else {
                Iterator nameVarsPairsIter = _nameToLVGMap.entrySet().iterator();

                while (nameVarsPairsIter.hasNext()) {
                    Map.Entry nameVarsPair =
                                  (Map.Entry) nameVarsPairsIter.next();
                    Object vars = nameVarsPair.getValue();
                    if (vars != null) {
                        if (vars instanceof ArrayList) {
                            ArrayList varsList = (ArrayList) vars;
                            for (int i = 0; i < varsList.size(); i++) {
                                allVarsEverDeclared.add(varsList.get(i));
                            }
                        } else {
                            allVarsEverDeclared.add(vars);
                        }
                    }
                }
            }

            locals = new LocalVariableGen[allVarsEverDeclared.size()];
            allVarsEverDeclared.toArray(locals);

            return locals;
        }
    }


    boolean offsetInLocalVariableGenRange(LocalVariableGen lvg, int offset) {
        InstructionHandle lvgStart = lvg.getStart();
        InstructionHandle lvgEnd = lvg.getEnd();

        if (lvgStart == null) {
            lvgStart = getInstructionList().getStart();
        }

        if (lvgEnd == null) {
            lvgEnd = getInstructionList().getEnd();
        }

        return ((lvgStart.getPosition() <= offset)
                    && (lvgEnd.getPosition()
                            + lvgEnd.getInstruction().getLength() >= offset));
    }

    public void removeLocalVariable(LocalVariableGen lvg) {
        _slotAllocator.releaseSlot(lvg);
        getLocalVariableRegistry().removeByNameTracking(lvg);
        super.removeLocalVariable(lvg);
    }

    public Instruction loadDOM() {
        return _aloadDom;
    }

    public Instruction storeDOM() {
        return _astoreDom;
    }

    public Instruction storeHandler() {
        return _astoreHandler;
    }

    public Instruction loadHandler() {
        return _aloadHandler;
    }

    public Instruction storeIterator() {
        return _astoreIterator;
    }

    public Instruction loadIterator() {
        return _aloadIterator;
    }

    public final Instruction setStartNode() {
        return _setStartNode;
    }

    public final Instruction reset() {
        return _reset;
    }

    public final Instruction nextNode() {
        return _nextNode;
    }

    public final Instruction startElement() {
        return _startElement;
    }

    public final Instruction endElement() {
        return _endElement;
    }

    public final Instruction startDocument() {
        return _startDocument;
    }

    public final Instruction endDocument() {
        return _endDocument;
    }

    public final Instruction attribute() {
        return _attribute;
    }

    public final Instruction uniqueAttribute() {
        return _uniqueAttribute;
    }

    public final Instruction namespace() {
        return _namespace;
    }

    public Instruction loadCurrentNode() {
        if (_iloadCurrent == null) {
            int idx = getLocalIndex("current");
            if (idx > 0)
                _iloadCurrent = new ILOAD(idx);
            else
                _iloadCurrent = new ICONST(0);
        }
        return _iloadCurrent;
    }

    public Instruction storeCurrentNode() {
        return _istoreCurrent != null
            ? _istoreCurrent
            : (_istoreCurrent = new ISTORE(getLocalIndex("current")));
    }


    public Instruction loadContextNode() {
        return loadCurrentNode();
    }

    public Instruction storeContextNode() {
        return storeCurrentNode();
    }

    public int getLocalIndex(String name) {
        return getLocalVariable(name).getIndex();
    }

    public LocalVariableGen getLocalVariable(String name) {
        return getLocalVariableRegistry().lookUpByName(name);
    }

    public void setMaxLocals() {

        int maxLocals = super.getMaxLocals();
        int prevLocals = maxLocals;

        final LocalVariableGen[] localVars = super.getLocalVariables();
        if (localVars != null) {
            if (localVars.length > maxLocals)
                maxLocals = localVars.length;
        }

        if (maxLocals < 5) maxLocals = 5;

        super.setMaxLocals(maxLocals);
    }


    public void addInstructionList(Pattern pattern, InstructionList ilist) {
        _preCompiled.put(pattern, ilist);
    }


    public InstructionList getInstructionList(Pattern pattern) {
        return _preCompiled.get(pattern);
    }


    private class Chunk implements Comparable {

        private InstructionHandle m_start;


        private InstructionHandle m_end;


        private int m_size;


        Chunk(InstructionHandle start, InstructionHandle end) {
            m_start = start;
            m_end = end;
            m_size = end.getPosition() - start.getPosition();
        }


        boolean isAdjacentTo(Chunk neighbour) {
            return getChunkEnd().getNext() == neighbour.getChunkStart();
        }


        InstructionHandle getChunkStart() {
            return m_start;
        }


        InstructionHandle getChunkEnd() {
            return m_end;
        }


        int getChunkSize() {
            return m_size;
        }


        public int compareTo(Object comparand) {
            return getChunkSize() - ((Chunk)comparand).getChunkSize();
        }
    }


    private ArrayList getCandidateChunks(ClassGenerator classGen,
                                         int totalMethodSize) {
        Iterator instructions = getInstructionList().iterator();
        ArrayList candidateChunks = new ArrayList();
        ArrayList currLevelChunks = new ArrayList();
        Stack subChunkStack = new Stack();
        boolean openChunkAtCurrLevel = false;
        boolean firstInstruction = true;

        InstructionHandle currentHandle;

        if (m_openChunks != 0) {
            String msg =
                (new ErrorMsg(ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS))
                    .toString();
            throw new InternalError(msg);
        }

        do {
            currentHandle = instructions.hasNext()
                                    ? (InstructionHandle) instructions.next()
                                    : null;
            Instruction inst =
                    (currentHandle != null) ? currentHandle.getInstruction()
                                            : null;

            if (firstInstruction) {
                openChunkAtCurrLevel = true;
                currLevelChunks.add(currentHandle);
                firstInstruction = false;
            }

            if (inst instanceof OutlineableChunkStart) {
                if (openChunkAtCurrLevel) {
                    subChunkStack.push(currLevelChunks);
                    currLevelChunks = new ArrayList();
                }

                openChunkAtCurrLevel = true;
                currLevelChunks.add(currentHandle);
            } else if (currentHandle == null
                           || inst instanceof OutlineableChunkEnd) {
                ArrayList nestedSubChunks = null;

                if (!openChunkAtCurrLevel) {
                    nestedSubChunks = currLevelChunks;
                    currLevelChunks = (ArrayList)subChunkStack.pop();
                }

                InstructionHandle chunkStart =
                        (InstructionHandle) currLevelChunks.get(
                                                      currLevelChunks.size()-1);

                int chunkEndPosition =
                        (currentHandle != null) ? currentHandle.getPosition()
                                                : totalMethodSize;
                int chunkSize = chunkEndPosition - chunkStart.getPosition();

                if (chunkSize <= TARGET_METHOD_SIZE) {
                    currLevelChunks.add(currentHandle);
                } else {
                    if (!openChunkAtCurrLevel) {
                        int childChunkCount = nestedSubChunks.size() / 2;
                        if (childChunkCount > 0) {
                            Chunk[] childChunks = new Chunk[childChunkCount];

                            for (int i = 0; i < childChunkCount; i++) {
                                InstructionHandle start =
                                    (InstructionHandle) nestedSubChunks
                                                            .get(i*2);
                                InstructionHandle end =
                                    (InstructionHandle) nestedSubChunks
                                                            .get(i*2+1);

                                childChunks[i] = new Chunk(start, end);
                            }

                            ArrayList mergedChildChunks =
                                        mergeAdjacentChunks(childChunks);

                            for (int i = 0; i < mergedChildChunks.size(); i++) {
                                Chunk mergedChunk =
                                    (Chunk)mergedChildChunks.get(i);
                                int mergedSize = mergedChunk.getChunkSize();

                                if (mergedSize >= MINIMUM_OUTLINEABLE_CHUNK_SIZE
                                        && mergedSize <= TARGET_METHOD_SIZE) {
                                    candidateChunks.add(mergedChunk);
                                }
                            }
                        }
                    }

                    currLevelChunks.remove(currLevelChunks.size() - 1);
                }

                openChunkAtCurrLevel = ((currLevelChunks.size() & 0x1) == 1);
            }

        } while (currentHandle != null);

        return candidateChunks;
    }


    private ArrayList mergeAdjacentChunks(Chunk[] chunks) {
        int[] adjacencyRunStart = new int[chunks.length];
        int[] adjacencyRunLength = new int[chunks.length];
        boolean[] chunkWasMerged = new boolean[chunks.length];

        int maximumRunOfChunks = 0;
        int startOfCurrentRun;
        int numAdjacentRuns = 0;

        ArrayList mergedChunks = new ArrayList();

        startOfCurrentRun = 0;

        for (int i = 1; i < chunks.length; i++) {
            if (!chunks[i-1].isAdjacentTo(chunks[i])) {
                int lengthOfRun = i - startOfCurrentRun;

                if (maximumRunOfChunks < lengthOfRun) {
                    maximumRunOfChunks = lengthOfRun;
                }

                if (lengthOfRun > 1 ) {
                    adjacencyRunLength[numAdjacentRuns] = lengthOfRun;
                    adjacencyRunStart[numAdjacentRuns] = startOfCurrentRun;
                    numAdjacentRuns++;
                }

                startOfCurrentRun = i;
            }
        }

        if (chunks.length - startOfCurrentRun > 1) {
            int lengthOfRun = chunks.length - startOfCurrentRun;

            if (maximumRunOfChunks < lengthOfRun) {
                maximumRunOfChunks = lengthOfRun;
            }

            adjacencyRunLength[numAdjacentRuns] =
                        chunks.length - startOfCurrentRun;
            adjacencyRunStart[numAdjacentRuns] = startOfCurrentRun;
            numAdjacentRuns++;
        }

        for (int numToMerge = maximumRunOfChunks; numToMerge>1; numToMerge--) {
            for (int run = 0; run < numAdjacentRuns; run++) {
                int runStart = adjacencyRunStart[run];
                int runEnd = runStart + adjacencyRunLength[run] - 1;

                boolean foundChunksToMerge = false;

                for (int mergeStart = runStart;
                     mergeStart+numToMerge-1 <= runEnd && !foundChunksToMerge;
                     mergeStart++) {
                    int mergeEnd = mergeStart + numToMerge - 1;
                    int mergeSize = 0;

                    for (int j = mergeStart; j <= mergeEnd; j++) {
                        mergeSize = mergeSize + chunks[j].getChunkSize();
                    }

                    if (mergeSize <= TARGET_METHOD_SIZE) {
                        foundChunksToMerge = true;

                        for (int j = mergeStart; j <= mergeEnd; j++) {
                            chunkWasMerged[j] = true;
                        }

                        mergedChunks.add(
                                new Chunk(chunks[mergeStart].getChunkStart(),
                                          chunks[mergeEnd].getChunkEnd()));

                        adjacencyRunLength[run] =
                                adjacencyRunStart[run] - mergeStart;

                        int trailingRunLength = runEnd - mergeEnd;

                        if (trailingRunLength >= 2) {
                            adjacencyRunStart[numAdjacentRuns] = mergeEnd + 1;
                            adjacencyRunLength[numAdjacentRuns] =
                                                            trailingRunLength;
                            numAdjacentRuns++;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < chunks.length; i++) {
            if (!chunkWasMerged[i]) {
                mergedChunks.add(chunks[i]);
            }
        }

        return mergedChunks;
    }


    public Method[] outlineChunks(ClassGenerator classGen,
                                  int originalMethodSize) {
        ArrayList methodsOutlined = new ArrayList();
        int currentMethodSize = originalMethodSize;

        int outlinedCount = 0;
        boolean moreMethodsOutlined;
        String originalMethodName = getName();

        if (originalMethodName.equals("<init>")) {
            originalMethodName = "$lt$init$gt$";
        } else if (originalMethodName.equals("<clinit>")) {
            originalMethodName = "$lt$clinit$gt$";
        }

        do {
            ArrayList candidateChunks = getCandidateChunks(classGen,
                                                           currentMethodSize);
            Collections.sort(candidateChunks);

            moreMethodsOutlined = false;

            for (int i = candidateChunks.size()-1;
                 i >= 0 && currentMethodSize > TARGET_METHOD_SIZE;
                 i--) {
                Chunk chunkToOutline = (Chunk)candidateChunks.get(i);

                methodsOutlined.add(outline(chunkToOutline.getChunkStart(),
                                            chunkToOutline.getChunkEnd(),
                                            originalMethodName + "$outline$"
                                                               + outlinedCount,
                                            classGen));
                outlinedCount++;
                moreMethodsOutlined = true;

                InstructionList il = getInstructionList();
                InstructionHandle lastInst = il.getEnd();
                il.setPositions();

                currentMethodSize =
                        lastInst.getPosition()
                                + lastInst.getInstruction().getLength();
            }
        } while (moreMethodsOutlined && currentMethodSize > TARGET_METHOD_SIZE);

        if (currentMethodSize > MAX_METHOD_SIZE) {
            String msg = (new ErrorMsg(ErrorMsg.OUTLINE_ERR_METHOD_TOO_BIG))
                                  .toString();
            throw new InternalError(msg);
        }

        Method[] methodsArr = new Method[methodsOutlined.size() + 1];
        methodsOutlined.toArray(methodsArr);

        methodsArr[methodsOutlined.size()] = getThisMethod();

        return methodsArr;
    }


    private Method outline(InstructionHandle first, InstructionHandle last,
                           String outlinedMethodName, ClassGenerator classGen) {
        if (getExceptionHandlers().length != 0) {
            String msg = (new ErrorMsg(ErrorMsg.OUTLINE_ERR_TRY_CATCH))
                                  .toString();
            throw new InternalError(msg);
        }

        int outlineChunkStartOffset = first.getPosition();
        int outlineChunkEndOffset = last.getPosition()
                                        + last.getInstruction().getLength();

        ConstantPoolGen cpg = getConstantPool();

        final InstructionList newIL = new InstructionList();

        final XSLTC  xsltc = classGen.getParser().getXSLTC();
        final String argTypeName = xsltc.getHelperClassName();
        final Type[] argTypes =
            new Type[] {(new ObjectType(argTypeName)).toJCType()};
        final String argName = "copyLocals";
        final String[] argNames = new String[] {argName};

        int methodAttributes = ACC_PRIVATE | ACC_FINAL;
        final boolean isStaticMethod = (getAccessFlags() & ACC_STATIC) != 0;

        if (isStaticMethod) {
            methodAttributes = methodAttributes | ACC_STATIC;
        }

        final MethodGenerator outlinedMethodGen =
            new MethodGenerator(methodAttributes,
                                com.sun.org.apache.bcel.internal.generic.Type.VOID,
                                argTypes, argNames, outlinedMethodName,
                                getClassName(), newIL, cpg);

        ClassGenerator copyAreaCG
            = new ClassGenerator(argTypeName, OBJECT_CLASS, argTypeName+".java",
                                 ACC_FINAL | ACC_PUBLIC | ACC_SUPER, null,
                                 classGen.getStylesheet()) {
                      public boolean isExternal() {
                          return true;
                      }
                  };
        ConstantPoolGen copyAreaCPG = copyAreaCG.getConstantPool();
        copyAreaCG.addEmptyConstructor(ACC_PUBLIC);

        int copyAreaFieldCount = 0;

        InstructionHandle limit = last.getNext();

        InstructionList oldMethCopyInIL  = new InstructionList();
        InstructionList oldMethCopyOutIL = new InstructionList();
        InstructionList newMethCopyInIL  = new InstructionList();
        InstructionList newMethCopyOutIL = new InstructionList();

        InstructionHandle outlinedMethodCallSetup =
            oldMethCopyInIL.append(new NEW(cpg.addClass(argTypeName)));
        oldMethCopyInIL.append(InstructionConstants.DUP);
        oldMethCopyInIL.append(InstructionConstants.DUP);
        oldMethCopyInIL.append(
            new INVOKESPECIAL(cpg.addMethodref(argTypeName, "<init>", "()V")));

        InstructionHandle outlinedMethodRef;

        if (isStaticMethod) {
            outlinedMethodRef =
                oldMethCopyOutIL.append(
                    new INVOKESTATIC(cpg.addMethodref(
                                          classGen.getClassName(),
                                          outlinedMethodName,
                                          outlinedMethodGen.getSignature())));
        } else {
            oldMethCopyOutIL.append(InstructionConstants.THIS);
            oldMethCopyOutIL.append(InstructionConstants.SWAP);
            outlinedMethodRef =
                oldMethCopyOutIL.append(
                    new INVOKEVIRTUAL(cpg.addMethodref(
                                          classGen.getClassName(),
                                          outlinedMethodName,
                                          outlinedMethodGen.getSignature())));
        }

        boolean chunkStartTargetMappingsPending = false;
        InstructionHandle pendingTargetMappingHandle = null;

        InstructionHandle lastCopyHandle = null;

        HashMap targetMap   = new HashMap();

        HashMap localVarMap = new HashMap();

        HashMap revisedLocalVarStart = new HashMap();
        HashMap revisedLocalVarEnd = new HashMap();

        for (InstructionHandle ih = first; ih != limit; ih = ih.getNext()) {
            Instruction inst = ih.getInstruction();

            if (inst instanceof MarkerInstruction) {
                if (ih.hasTargeters()) {
                    if (inst instanceof OutlineableChunkEnd) {
                        targetMap.put(ih, lastCopyHandle);
                    } else {
                        if (!chunkStartTargetMappingsPending)  {
                            chunkStartTargetMappingsPending = true;
                            pendingTargetMappingHandle = ih;
                        }
                    }
                }
            } else {
                Instruction c = inst.copy(); if (c instanceof BranchInstruction) {
                    lastCopyHandle = newIL.append((BranchInstruction)c);
                } else {
                    lastCopyHandle = newIL.append(c);
                }

                if (c instanceof LocalVariableInstruction
                        || c instanceof RET) {
                    IndexedInstruction lvi = (IndexedInstruction)c;
                    int oldLocalVarIndex = lvi.getIndex();
                    LocalVariableGen oldLVG =
                            getLocalVariableRegistry()
                                .lookupRegisteredLocalVariable(oldLocalVarIndex,
                                                              ih.getPosition());
                    LocalVariableGen newLVG =
                            (LocalVariableGen)localVarMap.get(oldLVG);

                    if (localVarMap.get(oldLVG) == null) {
                        boolean copyInLocalValue =
                            offsetInLocalVariableGenRange(oldLVG,
                                                (outlineChunkStartOffset != 0)
                                                    ? outlineChunkStartOffset-1
                                                    : 0);
                        boolean copyOutLocalValue =
                            offsetInLocalVariableGenRange(oldLVG,
                                                outlineChunkEndOffset+1);

                        if (copyInLocalValue || copyOutLocalValue) {
                            String varName = oldLVG.getName();
                            Type varType = oldLVG.getType();
                            newLVG = outlinedMethodGen.addLocalVariable(varName,
                                                                        varType,
                                                                        null,
                                                                        null);
                            int newLocalVarIndex = newLVG.getIndex();
                            String varSignature = varType.getSignature();

                            localVarMap.put(oldLVG, newLVG);

                            copyAreaFieldCount++;
                            String copyAreaFieldName =
                                           "field" + copyAreaFieldCount;
                            copyAreaCG.addField(
                                new Field(ACC_PUBLIC,
                                        copyAreaCPG.addUtf8(copyAreaFieldName),
                                        copyAreaCPG.addUtf8(varSignature),
                                        null, copyAreaCPG.getConstantPool()));

                            int fieldRef = cpg.addFieldref(argTypeName,
                                                           copyAreaFieldName,
                                                           varSignature);

                            if (copyInLocalValue) {
                                oldMethCopyInIL.append(
                                        InstructionConstants.DUP);
                                InstructionHandle copyInLoad =
                                    oldMethCopyInIL.append(
                                        loadLocal(oldLocalVarIndex, varType));
                                oldMethCopyInIL.append(new PUTFIELD(fieldRef));

                                if (!copyOutLocalValue) {
                                    revisedLocalVarEnd.put(oldLVG, copyInLoad);
                                }

                                newMethCopyInIL.append(
                                        InstructionConstants.ALOAD_1);
                                newMethCopyInIL.append(new GETFIELD(fieldRef));
                                newMethCopyInIL.append(
                                        storeLocal(newLocalVarIndex, varType));
                            }

                            if (copyOutLocalValue) {
                                newMethCopyOutIL.append(
                                        InstructionConstants.ALOAD_1);
                                newMethCopyOutIL.append(
                                        loadLocal(newLocalVarIndex, varType));
                                newMethCopyOutIL.append(new PUTFIELD(fieldRef));

                                oldMethCopyOutIL.append(
                                        InstructionConstants.DUP);
                                oldMethCopyOutIL.append(new GETFIELD(fieldRef));
                                InstructionHandle copyOutStore =
                                    oldMethCopyOutIL.append(
                                        storeLocal(oldLocalVarIndex, varType));

                                if (!copyInLocalValue) {
                                    revisedLocalVarStart.put(oldLVG,
                                                             copyOutStore);
                                }
                            }
                        }
                    }
                }

                if (ih.hasTargeters()) {
                    targetMap.put(ih, lastCopyHandle);
                }

                if (chunkStartTargetMappingsPending) {
                    do {
                         targetMap.put(pendingTargetMappingHandle,
                                       lastCopyHandle);
                         pendingTargetMappingHandle =
                                 pendingTargetMappingHandle.getNext();
                    } while(pendingTargetMappingHandle != ih);

                    chunkStartTargetMappingsPending = false;
                }
            }
        }

        InstructionHandle ih = first;
        InstructionHandle ch = newIL.getStart();

        while (ch != null) {
            Instruction i = ih.getInstruction();
            Instruction c = ch.getInstruction();

            if (i instanceof BranchInstruction) {
                BranchInstruction bc      = (BranchInstruction)c;
                BranchInstruction bi      = (BranchInstruction)i;
                InstructionHandle itarget = bi.getTarget(); InstructionHandle newTarget =
                    (InstructionHandle)targetMap.get(itarget);

                bc.setTarget(newTarget);

                if (bi instanceof Select) {
                    InstructionHandle[] itargets = ((Select)bi).getTargets();
                    InstructionHandle[] ctargets = ((Select)bc).getTargets();

                    for (int j=0; j < itargets.length; j++) {
                        ctargets[j] =
                            (InstructionHandle)targetMap.get(itargets[j]);
                    }
                }
            }  else if (i instanceof LocalVariableInstruction
                            || i instanceof RET) {
                IndexedInstruction lvi = (IndexedInstruction)c;
                int oldLocalVarIndex = lvi.getIndex();
                LocalVariableGen oldLVG =
                        getLocalVariableRegistry()
                                .lookupRegisteredLocalVariable(oldLocalVarIndex,
                                                              ih.getPosition());
                LocalVariableGen newLVG =
                        (LocalVariableGen)localVarMap.get(oldLVG);
                int newLocalVarIndex;

                if (newLVG == null) {
                    String varName = oldLVG.getName();
                    Type varType = oldLVG.getType();
                    newLVG = outlinedMethodGen.addLocalVariable(varName,
                                                                varType,
                                                                null,
                                                                null);
                    newLocalVarIndex = newLVG.getIndex();
                    localVarMap.put(oldLVG, newLVG);

                    revisedLocalVarStart.put(oldLVG, outlinedMethodRef);
                    revisedLocalVarEnd.put(oldLVG, outlinedMethodRef);
                } else {
                    newLocalVarIndex = newLVG.getIndex();
                }
                lvi.setIndex(newLocalVarIndex);
            }

            if (ih.hasTargeters()) {
                InstructionTargeter[] targeters = ih.getTargeters();

                for (int idx = 0; idx < targeters.length; idx++) {
                    InstructionTargeter targeter = targeters[idx];

                    if (targeter instanceof LocalVariableGen
                            && ((LocalVariableGen)targeter).getEnd()==ih) {
                        Object newLVG = localVarMap.get(targeter);
                        if (newLVG != null) {
                            outlinedMethodGen.removeLocalVariable(
                                                  (LocalVariableGen)newLVG);
                        }
                    }
                }
            }

            if (!(i instanceof MarkerInstruction)) {
                ch = ch.getNext();
            }
            ih = ih.getNext();

        }

        oldMethCopyOutIL.append(InstructionConstants.POP);

        Iterator revisedLocalVarStartPairIter = revisedLocalVarStart.entrySet()
                                                                    .iterator();
        while (revisedLocalVarStartPairIter.hasNext()) {
            Map.Entry lvgRangeStartPair =
                    (Map.Entry)revisedLocalVarStartPairIter.next();
            LocalVariableGen lvg = (LocalVariableGen)lvgRangeStartPair.getKey();
            InstructionHandle startInst =
                    (InstructionHandle)lvgRangeStartPair.getValue();

            lvg.setStart(startInst);

        }

        Iterator revisedLocalVarEndPairIter = revisedLocalVarEnd.entrySet()
                                                                .iterator();
        while (revisedLocalVarEndPairIter.hasNext()) {
            Map.Entry lvgRangeEndPair =
                    (Map.Entry)revisedLocalVarEndPairIter.next();
            LocalVariableGen lvg = (LocalVariableGen)lvgRangeEndPair.getKey();
            InstructionHandle endInst =
                    (InstructionHandle)lvgRangeEndPair.getValue();

            lvg.setEnd(endInst);
        }

        xsltc.dumpClass(copyAreaCG.getJavaClass());

        InstructionList oldMethodIL = getInstructionList();

        oldMethodIL.insert(first, oldMethCopyInIL);
        oldMethodIL.insert(first, oldMethCopyOutIL);

        newIL.insert(newMethCopyInIL);
        newIL.append(newMethCopyOutIL);
        newIL.append(InstructionConstants.RETURN);

        try {
            oldMethodIL.delete(first, last);
        } catch (TargetLostException e) {
            InstructionHandle[] targets = e.getTargets();
            for (int i = 0; i < targets.length; i++) {
                InstructionHandle lostTarget = targets[i];
                InstructionTargeter[] targeters = lostTarget.getTargeters();
                for (int j = 0; j < targeters.length; j++) {
                    if (targeters[j] instanceof LocalVariableGen) {
                        LocalVariableGen lvgTargeter =
                                             (LocalVariableGen) targeters[j];
                        if (lvgTargeter.getStart() == lostTarget) {
                            lvgTargeter.setStart(outlinedMethodRef);
                        }
                        if (lvgTargeter.getEnd() == lostTarget) {
                            lvgTargeter.setEnd(outlinedMethodRef);
                        }
                    } else {
                        targeters[j].updateTarget(lostTarget,
                                                  outlinedMethodCallSetup);
                    }
                }
            }
        }

        String[] exceptions = getExceptions();
        for (int i = 0; i < exceptions.length; i++) {
            outlinedMethodGen.addException(exceptions[i]);
        }

        return outlinedMethodGen.getThisMethod();
    }


    private static Instruction loadLocal(int index, Type type) {
        if (type == Type.BOOLEAN) {
           return new ILOAD(index);
        } else if (type == Type.INT) {
           return new ILOAD(index);
        } else if (type == Type.SHORT) {
           return new ILOAD(index);
        } else if (type == Type.LONG) {
           return new LLOAD(index);
        } else if (type == Type.BYTE) {
           return new ILOAD(index);
        } else if (type == Type.CHAR) {
           return new ILOAD(index);
        } else if (type == Type.FLOAT) {
           return new FLOAD(index);
        } else if (type == Type.DOUBLE) {
           return new DLOAD(index);
        } else {
           return new ALOAD(index);
        }
    }


    private static Instruction storeLocal(int index, Type type) {
        if (type == Type.BOOLEAN) {
           return new ISTORE(index);
        } else if (type == Type.INT) {
           return new ISTORE(index);
        } else if (type == Type.SHORT) {
           return new ISTORE(index);
        } else if (type == Type.LONG) {
           return new LSTORE(index);
        } else if (type == Type.BYTE) {
           return new ISTORE(index);
        } else if (type == Type.CHAR) {
           return new ISTORE(index);
        } else if (type == Type.FLOAT) {
           return new FSTORE(index);
        } else if (type == Type.DOUBLE) {
           return new DSTORE(index);
        } else {
           return new ASTORE(index);
        }
    }


    private int m_totalChunks = 0;


    private int m_openChunks = 0;


    public void markChunkStart() {
        getInstructionList()
                .append(OutlineableChunkStart.OUTLINEABLECHUNKSTART);
        m_totalChunks++;
        m_openChunks++;
    }


    public void markChunkEnd() {
        getInstructionList()
                .append(OutlineableChunkEnd.OUTLINEABLECHUNKEND);
        m_openChunks--;
        if (m_openChunks < 0) {
            String msg = (new ErrorMsg(ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS))
                                 .toString();
            throw new InternalError(msg);
        }
    }


    Method[] getGeneratedMethods(ClassGenerator classGen) {
        Method[] generatedMethods;
        InstructionList il = getInstructionList();
        InstructionHandle last = il.getEnd();

        il.setPositions();

        int instructionListSize =
                    last.getPosition() + last.getInstruction().getLength();

        if (instructionListSize > MAX_BRANCH_TARGET_OFFSET) {
            boolean ilChanged = widenConditionalBranchTargetOffsets();

            if (ilChanged) {
                il.setPositions();
                last = il.getEnd();
                instructionListSize =
                        last.getPosition() + last.getInstruction().getLength();
            }
        }

        if (instructionListSize > MAX_METHOD_SIZE) {
            generatedMethods = outlineChunks(classGen, instructionListSize);
        } else {
            generatedMethods = new Method[] {getThisMethod()};
        }
        return generatedMethods;
    }

    protected Method getThisMethod() {
        stripAttributes(true);
        setMaxLocals();
        setMaxStack();
        removeNOPs();

        return getMethod();
    }

    boolean widenConditionalBranchTargetOffsets() {
        boolean ilChanged = false;
        int maxOffsetChange = 0;
        InstructionList il = getInstructionList();

        for (InstructionHandle ih = il.getStart();
             ih != null;
             ih = ih.getNext()) {
            Instruction inst = ih.getInstruction();

            switch (inst.getOpcode()) {
                case Constants.GOTO:
                case Constants.JSR:
                    maxOffsetChange = maxOffsetChange + 2;
                    break;
                case Constants.TABLESWITCH:
                case Constants.LOOKUPSWITCH:
                    maxOffsetChange = maxOffsetChange + 3;
                    break;
                case Constants.IF_ACMPEQ:
                case Constants.IF_ACMPNE:
                case Constants.IF_ICMPEQ:
                case Constants.IF_ICMPGE:
                case Constants.IF_ICMPGT:
                case Constants.IF_ICMPLE:
                case Constants.IF_ICMPLT:
                case Constants.IF_ICMPNE:
                case Constants.IFEQ:
                case Constants.IFGE:
                case Constants.IFGT:
                case Constants.IFLE:
                case Constants.IFLT:
                case Constants.IFNE:
                case Constants.IFNONNULL:
                case Constants.IFNULL:
                    maxOffsetChange = maxOffsetChange + 5;
                    break;
            }
        }

        for (InstructionHandle ih = il.getStart();
             ih != null;
             ih = ih.getNext()) {
            Instruction inst = ih.getInstruction();

            if (inst instanceof IfInstruction) {
                IfInstruction oldIfInst = (IfInstruction)inst;
                BranchHandle oldIfHandle = (BranchHandle)ih;
                InstructionHandle target = oldIfInst.getTarget();
                int relativeTargetOffset = target.getPosition()
                                               - oldIfHandle.getPosition();

                if ((relativeTargetOffset - maxOffsetChange
                             < MIN_BRANCH_TARGET_OFFSET)
                        || (relativeTargetOffset + maxOffsetChange
                                    > MAX_BRANCH_TARGET_OFFSET)) {
                    InstructionHandle nextHandle = oldIfHandle.getNext();
                    IfInstruction invertedIfInst = oldIfInst.negate();
                    BranchHandle invertedIfHandle = il.append(oldIfHandle,
                                                              invertedIfInst);

                    BranchHandle gotoHandle = il.append(invertedIfHandle,
                                                        new GOTO(target));

                    if (nextHandle == null) {
                        nextHandle = il.append(gotoHandle, NOP);
                    }

                    invertedIfHandle.updateTarget(target, nextHandle);

                    if (oldIfHandle.hasTargeters()) {
                        InstructionTargeter[] targeters =
                                                  oldIfHandle.getTargeters();

                        for (int i = 0; i < targeters.length; i++) {
                            InstructionTargeter targeter = targeters[i];
                            if (targeter instanceof LocalVariableGen) {
                                LocalVariableGen lvg =
                                        (LocalVariableGen) targeter;
                                if (lvg.getStart() == oldIfHandle) {
                                    lvg.setStart(invertedIfHandle);
                                } else if (lvg.getEnd() == oldIfHandle) {
                                    lvg.setEnd(gotoHandle);
                                }
                            } else {
                                targeter.updateTarget(oldIfHandle,
                                                      invertedIfHandle);
                            }
                        }
                    }

                    try {
                        il.delete(oldIfHandle);
                    } catch (TargetLostException tle) {
                        String msg =
                            new ErrorMsg(ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
                                         tle.getMessage()).toString();
                        throw new InternalError(msg);
                    }

                    ih = gotoHandle;

                    ilChanged = true;
                }
            }
        }

        return ilChanged;
    }
}
