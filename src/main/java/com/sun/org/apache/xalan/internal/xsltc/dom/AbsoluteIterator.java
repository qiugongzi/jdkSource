



package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBase;


public final class AbsoluteIterator extends DTMAxisIteratorBase {


    private DTMAxisIterator _source;

    public AbsoluteIterator(DTMAxisIterator source) {
        _source = source;
}

    public void setRestartable(boolean isRestartable) {
        _isRestartable = isRestartable;
        _source.setRestartable(isRestartable);
    }

    public DTMAxisIterator setStartNode(int node) {
        _startNode = DTMDefaultBase.ROOTNODE;
        if (_isRestartable) {
            _source.setStartNode(_startNode);
            resetPosition();
        }
        return this;
    }

    public int next() {
        return returnNode(_source.next());
    }

    public DTMAxisIterator cloneIterator() {
        try {
            final AbsoluteIterator clone = (AbsoluteIterator) super.clone();
            clone._source = _source.cloneIterator();    clone.resetPosition();
            clone._isRestartable = false;
            return clone;
        }
        catch (CloneNotSupportedException e) {
            BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
                                      e.toString());
            return null;
        }
    }

    public DTMAxisIterator reset() {
        _source.reset();
        return resetPosition();
    }

    public void setMark() {
        _source.setMark();
    }

    public void gotoMark() {
        _source.gotoMark();
    }
}
