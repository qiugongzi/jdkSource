
    public DTMAxisIterator setStartNode(int node) {
        if (_isConstant) {
            _node = _startNode;
            return resetPosition();
        }
        else if (_isRestartable) {
            if (_node <= 0)
                _node = _startNode = node;
            return resetPosition();
        }
        return this;
    }

    public DTMAxisIterator reset() {
        if (_isConstant) {
            _node = _startNode;
            return resetPosition();
        }
        else {
            final boolean temp = _isRestartable;
            _isRestartable = true;
            setStartNode(_startNode);
            _isRestartable = temp;
        }
        return this;
    }

    public int next() {
        final int result = _node;
        _node = DTMAxisIterator.END;
        return returnNode(result);
    }

    public void setMark() {
        _markedNode = _node;
    }

    public void gotoMark() {
        _node = _markedNode;
    }
}
