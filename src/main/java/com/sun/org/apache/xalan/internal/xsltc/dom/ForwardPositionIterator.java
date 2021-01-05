



package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;


public final class ForwardPositionIterator extends DTMAxisIteratorBase {

    private DTMAxisIterator _source;

    public ForwardPositionIterator(DTMAxisIterator source) {
        _source = source;
    }

    public DTMAxisIterator cloneIterator() {
        try {
            final ForwardPositionIterator clone =
                (ForwardPositionIterator) super.clone();
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

    public int next() {
        return returnNode(_source.next());
    }

    public DTMAxisIterator setStartNode(int node) {
        _source.setStartNode(node);
        return this;
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
