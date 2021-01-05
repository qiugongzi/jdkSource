



package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBase;


public final class DupFilterIterator extends DTMAxisIteratorBase {


    private DTMAxisIterator _source;


    private IntegerArray _nodes = new IntegerArray();


    private int _current = 0;


    private int _nodesSize = 0;


    private int _lastNext = END;


    private int _markedLastNext = END;

    public DupFilterIterator(DTMAxisIterator source) {
        _source = source;
if (source instanceof KeyIndex) {
            setStartNode(DTMDefaultBase.ROOTNODE);
        }
    }


    public DTMAxisIterator setStartNode(int node) {
        if (_isRestartable) {
            boolean sourceIsKeyIndex = _source instanceof KeyIndex;

            if (sourceIsKeyIndex
                    && _startNode == DTMDefaultBase.ROOTNODE) {
                return this;
            }

            if (node != _startNode) {
                _source.setStartNode(_startNode = node);

                _nodes.clear();
                while ((node = _source.next()) != END) {
                    _nodes.add(node);
                }

                if (!sourceIsKeyIndex) {
                    _nodes.sort();
                }
                _nodesSize = _nodes.cardinality();
                _current = 0;
                _lastNext = END;
                resetPosition();
            }
        }
        return this;
    }

    public int next() {
        while (_current < _nodesSize) {
            final int next = _nodes.at(_current++);
            if (next != _lastNext) {
                return returnNode(_lastNext = next);
            }
        }
        return END;
    }

    public DTMAxisIterator cloneIterator() {
        try {
            final DupFilterIterator clone =
                (DupFilterIterator) super.clone();
            clone._nodes = (IntegerArray) _nodes.clone();
            clone._source = _source.cloneIterator();
            clone._isRestartable = false;
            return clone.reset();
        }
        catch (CloneNotSupportedException e) {
            BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
                                      e.toString());
            return null;
        }
    }

    public void setRestartable(boolean isRestartable) {
        _isRestartable = isRestartable;
        _source.setRestartable(isRestartable);
    }

    public void setMark() {
        _markedNode = _current;
        _markedLastNext = _lastNext;    }

    public void gotoMark() {
        _current = _markedNode;
        _lastNext = _markedLastNext;    }

    public DTMAxisIterator reset() {
        _current = 0;
        _lastNext = END;
        return resetPosition();
    }
}
