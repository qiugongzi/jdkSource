


package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.ranges.RangeException;



public class RangeImpl  implements Range {

    DocumentImpl fDocument;
    Node fStartContainer;
    Node fEndContainer;
    int fStartOffset;
    int fEndOffset;
    boolean fIsCollapsed;
    boolean fDetach = false;
    Node fInsertNode = null;
    Node fDeleteNode = null;
    Node fSplitNode = null;
    boolean fInsertedFromRange = false;


    public RangeImpl(DocumentImpl document) {
        fDocument = document;
        fStartContainer = document;
        fEndContainer = document;
        fStartOffset = 0;
        fEndOffset = 0;
        fDetach = false;
    }

    public Node getStartContainer() {
        if ( fDetach ) {
            throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }
        return fStartContainer;
    }

    public int getStartOffset() {
        if ( fDetach ) {
            throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }
        return fStartOffset;
    }

    public Node getEndContainer() {
        if ( fDetach ) {
            throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }
        return fEndContainer;
    }

    public int getEndOffset() {
        if ( fDetach ) {
            throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }
        return fEndOffset;
    }

    public boolean getCollapsed() {
        if ( fDetach ) {
            throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }
        return (fStartContainer == fEndContainer
             && fStartOffset == fEndOffset);
    }

    public Node getCommonAncestorContainer() {
        if ( fDetach ) {
            throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }
        Vector startV = new Vector();
        Node node;
        for (node=fStartContainer; node != null;
             node=node.getParentNode())
        {
            startV.addElement(node);
        }
        Vector endV = new Vector();
        for (node=fEndContainer; node != null;
             node=node.getParentNode())
        {
            endV.addElement(node);
        }
        int s = startV.size()-1;
        int e = endV.size()-1;
        Object result = null;
        while (s>=0 && e>=0) {
            if (startV.elementAt(s) == endV.elementAt(e)) {
                result = startV.elementAt(s);
            } else {
                break;
            }
            --s;
            --e;
        }
        return (Node)result;
    }


    public void setStart(Node refNode, int offset)
                         throws RangeException, DOMException
    {
        if (fDocument.errorChecking) {
            if ( fDetach) {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
            }
            if ( !isLegalContainer(refNode)) {
                throw new RangeExceptionImpl(
                        RangeException.INVALID_NODE_TYPE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_NODE_TYPE_ERR", null));
            }
            if ( fDocument != refNode.getOwnerDocument() && fDocument != refNode) {
                throw new DOMException(
                        DOMException.WRONG_DOCUMENT_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null));
            }
        }

        checkIndex(refNode, offset);

        fStartContainer = refNode;
        fStartOffset = offset;

        if (getCommonAncestorContainer() == null
                || (fStartContainer == fEndContainer && fEndOffset < fStartOffset)) {
            collapse(true);
        }
    }

    public void setEnd(Node refNode, int offset)
                       throws RangeException, DOMException
    {
        if (fDocument.errorChecking) {
            if (fDetach) {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
            }
            if ( !isLegalContainer(refNode)) {
                throw new RangeExceptionImpl(
                        RangeException.INVALID_NODE_TYPE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_NODE_TYPE_ERR", null));
            }
            if ( fDocument != refNode.getOwnerDocument() && fDocument != refNode) {
                throw new DOMException(
                        DOMException.WRONG_DOCUMENT_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null));
            }
        }

        checkIndex(refNode, offset);

        fEndContainer = refNode;
        fEndOffset = offset;

        if (getCommonAncestorContainer() == null
                || (fStartContainer == fEndContainer && fEndOffset < fStartOffset)) {
            collapse(false);
        }
    }

    public void setStartBefore(Node refNode)
        throws RangeException
    {
        if (fDocument.errorChecking) {
            if (fDetach) {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
            }
            if ( !hasLegalRootContainer(refNode) ||
                    !isLegalContainedNode(refNode) )
            {
                throw new RangeExceptionImpl(
                        RangeException.INVALID_NODE_TYPE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_NODE_TYPE_ERR", null));
            }
            if ( fDocument != refNode.getOwnerDocument() && fDocument != refNode) {
                throw new DOMException(
                        DOMException.WRONG_DOCUMENT_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null));
            }
        }

        fStartContainer = refNode.getParentNode();
        int i = 0;
        for (Node n = refNode; n!=null; n = n.getPreviousSibling()) {
            i++;
        }
        fStartOffset = i-1;

        if (getCommonAncestorContainer() == null
                || (fStartContainer == fEndContainer && fEndOffset < fStartOffset)) {
            collapse(true);
        }
    }

    public void setStartAfter(Node refNode)
        throws RangeException
    {
        if (fDocument.errorChecking) {
            if (fDetach) {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
            }
            if ( !hasLegalRootContainer(refNode) ||
                    !isLegalContainedNode(refNode)) {
                throw new RangeExceptionImpl(
                        RangeException.INVALID_NODE_TYPE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_NODE_TYPE_ERR", null));
            }
            if ( fDocument != refNode.getOwnerDocument() && fDocument != refNode) {
                throw new DOMException(
                        DOMException.WRONG_DOCUMENT_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null));
            }
        }
        fStartContainer = refNode.getParentNode();
        int i = 0;
        for (Node n = refNode; n!=null; n = n.getPreviousSibling()) {
            i++;
        }
        fStartOffset = i;

        if (getCommonAncestorContainer() == null
                || (fStartContainer == fEndContainer && fEndOffset < fStartOffset)) {
            collapse(true);
        }
    }

    public void setEndBefore(Node refNode)
        throws RangeException
    {
        if (fDocument.errorChecking) {
            if (fDetach) {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
            }
            if ( !hasLegalRootContainer(refNode) ||
                    !isLegalContainedNode(refNode)) {
                throw new RangeExceptionImpl(
                        RangeException.INVALID_NODE_TYPE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_NODE_TYPE_ERR", null));
            }
            if ( fDocument != refNode.getOwnerDocument() && fDocument != refNode) {
                throw new DOMException(
                        DOMException.WRONG_DOCUMENT_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null));
            }
        }
        fEndContainer = refNode.getParentNode();
        int i = 0;
        for (Node n = refNode; n!=null; n = n.getPreviousSibling()) {
            i++;
        }
        fEndOffset = i-1;

        if (getCommonAncestorContainer() == null
                || (fStartContainer == fEndContainer && fEndOffset < fStartOffset)) {
            collapse(false);
        }
    }

    public void setEndAfter(Node refNode)
        throws RangeException
    {
        if (fDocument.errorChecking) {
            if( fDetach) {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
            }
            if ( !hasLegalRootContainer(refNode) ||
                    !isLegalContainedNode(refNode)) {
                throw new RangeExceptionImpl(
                        RangeException.INVALID_NODE_TYPE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_NODE_TYPE_ERR", null));
            }
            if ( fDocument != refNode.getOwnerDocument() && fDocument != refNode) {
                throw new DOMException(
                        DOMException.WRONG_DOCUMENT_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null));
            }
        }
        fEndContainer = refNode.getParentNode();
        int i = 0;
        for (Node n = refNode; n!=null; n = n.getPreviousSibling()) {
            i++;
        }
        fEndOffset = i;

        if (getCommonAncestorContainer() == null
                || (fStartContainer == fEndContainer && fEndOffset < fStartOffset)) {
            collapse(false);
        }
    }

    public void collapse(boolean toStart) {

        if( fDetach) {
                throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }

        if (toStart) {
            fEndContainer = fStartContainer;
            fEndOffset = fStartOffset;
        } else {
            fStartContainer = fEndContainer;
            fStartOffset = fEndOffset;
        }
    }

    public void selectNode(Node refNode)
        throws RangeException
    {
        if (fDocument.errorChecking) {
            if (fDetach) {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
            }
            if ( !isLegalContainer( refNode.getParentNode() ) ||
                    !isLegalContainedNode( refNode ) ) {
                throw new RangeExceptionImpl(
                        RangeException.INVALID_NODE_TYPE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_NODE_TYPE_ERR", null));
            }
            if ( fDocument != refNode.getOwnerDocument() && fDocument != refNode) {
                throw new DOMException(
                        DOMException.WRONG_DOCUMENT_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null));
            }
        }
        Node parent = refNode.getParentNode();
        if (parent != null ) {
            fStartContainer = parent;
            fEndContainer = parent;
            int i = 0;
            for (Node n = refNode; n!=null; n = n.getPreviousSibling()) {
                i++;
            }
            fStartOffset = i-1;
            fEndOffset = fStartOffset+1;
        }
    }

    public void selectNodeContents(Node refNode)
        throws RangeException
    {
        if (fDocument.errorChecking) {
            if( fDetach) {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
            }
            if ( !isLegalContainer(refNode)) {
                throw new RangeExceptionImpl(
                        RangeException.INVALID_NODE_TYPE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_NODE_TYPE_ERR", null));
            }
            if ( fDocument != refNode.getOwnerDocument() && fDocument != refNode) {
                throw new DOMException(
                        DOMException.WRONG_DOCUMENT_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null));
            }
        }
        fStartContainer = refNode;
        fEndContainer = refNode;
        Node first = refNode.getFirstChild();
        fStartOffset = 0;
        if (first == null) {
            fEndOffset = 0;
        } else {
            int i = 0;
            for (Node n = first; n!=null; n = n.getNextSibling()) {
                i++;
            }
            fEndOffset = i;
        }

    }

    public short compareBoundaryPoints(short how, Range sourceRange)
        throws DOMException
    {
        if (fDocument.errorChecking) {
            if( fDetach) {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
            }
            if ((fDocument != sourceRange.getStartContainer().getOwnerDocument()
                    && fDocument != sourceRange.getStartContainer()
                    && sourceRange.getStartContainer() != null)
                    || (fDocument != sourceRange.getEndContainer().getOwnerDocument()
                            && fDocument != sourceRange.getEndContainer()
                            && sourceRange.getStartContainer() != null)) {
                throw new DOMException(DOMException.WRONG_DOCUMENT_ERR,
                        DOMMessageFormatter.formatMessage( DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null));
            }
        }

        Node endPointA;
        Node endPointB;
        int offsetA;
        int offsetB;

        if (how == START_TO_START) {
            endPointA = sourceRange.getStartContainer();
            endPointB = fStartContainer;
            offsetA = sourceRange.getStartOffset();
            offsetB = fStartOffset;
        } else
        if (how == START_TO_END) {
            endPointA = sourceRange.getStartContainer();
            endPointB = fEndContainer;
            offsetA = sourceRange.getStartOffset();
            offsetB = fEndOffset;
        } else
        if (how == END_TO_START) {
            endPointA = sourceRange.getEndContainer();
            endPointB = fStartContainer;
            offsetA = sourceRange.getEndOffset();
            offsetB = fStartOffset;
        } else {
            endPointA = sourceRange.getEndContainer();
            endPointB = fEndContainer;
            offsetA = sourceRange.getEndOffset();
            offsetB = fEndOffset;
        }

        if (endPointA == endPointB) {
            if (offsetA < offsetB) return 1;
            if (offsetA == offsetB) return 0;
            return -1;
        }
        for ( Node c = endPointB, p = c.getParentNode();
             p != null;
             c = p, p = p.getParentNode())
        {
            if (p == endPointA) {
                int index = indexOf(c, endPointA);
                if (offsetA <= index) return 1;
                return -1;
            }
        }

        for ( Node c = endPointA, p = c.getParentNode();
             p != null;
             c = p, p = p.getParentNode())
        {
            if (p == endPointB) {
                int index = indexOf(c, endPointB);
                if (index < offsetB) return 1;
                return -1;
            }
        }

        int depthDiff = 0;
        for ( Node n = endPointA; n != null; n = n.getParentNode() )
            depthDiff++;
        for ( Node n = endPointB; n != null; n = n.getParentNode() )
            depthDiff--;
        while (depthDiff > 0) {
            endPointA = endPointA.getParentNode();
            depthDiff--;
        }
        while (depthDiff < 0) {
            endPointB = endPointB.getParentNode();
            depthDiff++;
        }
        for (Node pA = endPointA.getParentNode(),
             pB = endPointB.getParentNode();
             pA != pB;
             pA = pA.getParentNode(), pB = pB.getParentNode() )
        {
            endPointA = pA;
            endPointB = pB;
        }
        for ( Node n = endPointA.getNextSibling();
             n != null;
             n = n.getNextSibling() )
        {
            if (n == endPointB) {
                return 1;
            }
        }
        return -1;
    }

    public void deleteContents()
        throws DOMException
    {
        traverseContents(DELETE_CONTENTS);
    }

    public DocumentFragment extractContents()
        throws DOMException
    {
        return traverseContents(EXTRACT_CONTENTS);
    }

    public DocumentFragment cloneContents()
        throws DOMException
    {
        return traverseContents(CLONE_CONTENTS);
    }

    public void insertNode(Node newNode)
        throws DOMException, RangeException
    {
        if ( newNode == null ) return; int type = newNode.getNodeType();

        if (fDocument.errorChecking) {
            if (fDetach) {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
            }
            if ( fDocument != newNode.getOwnerDocument() ) {
                throw new DOMException(DOMException.WRONG_DOCUMENT_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null));
            }

            if (type == Node.ATTRIBUTE_NODE
                    || type == Node.ENTITY_NODE
                    || type == Node.NOTATION_NODE
                    || type == Node.DOCUMENT_NODE)
            {
                throw new RangeExceptionImpl(
                        RangeException.INVALID_NODE_TYPE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_NODE_TYPE_ERR", null));
            }
        }
        Node cloneCurrent;
        Node current;
        int currentChildren = 0;
        fInsertedFromRange = true;

        if (fStartContainer.getNodeType() == Node.TEXT_NODE) {

            Node parent = fStartContainer.getParentNode();
            currentChildren = parent.getChildNodes().getLength(); cloneCurrent = fStartContainer.cloneNode(false);
            ((TextImpl)cloneCurrent).setNodeValueInternal(
                    (cloneCurrent.getNodeValue()).substring(fStartOffset));
            ((TextImpl)fStartContainer).setNodeValueInternal(
                    (fStartContainer.getNodeValue()).substring(0,fStartOffset));
            Node next = fStartContainer.getNextSibling();
            if (next != null) {
                    if (parent !=  null) {
                        parent.insertBefore(newNode, next);
                        parent.insertBefore(cloneCurrent, next);
                    }
            } else {
                    if (parent != null) {
                        parent.appendChild(newNode);
                        parent.appendChild(cloneCurrent);
                    }
            }
             if ( fEndContainer == fStartContainer) {
                  fEndContainer = cloneCurrent; fEndOffset -= fStartOffset;
             }
             else if ( fEndContainer == parent ) {    fEndOffset += (parent.getChildNodes().getLength() - currentChildren);
             }

             signalSplitData(fStartContainer, cloneCurrent, fStartOffset);


        } else { if ( fEndContainer == fStartContainer )      currentChildren= fEndContainer.getChildNodes().getLength();

            current = fStartContainer.getFirstChild();
            int i = 0;
            for(i = 0; i < fStartOffset && current != null; i++) {
                current=current.getNextSibling();
            }
            if (current != null) {
                fStartContainer.insertBefore(newNode, current);
            } else {
                fStartContainer.appendChild(newNode);
            }
            if ( fEndContainer == fStartContainer && fEndOffset != 0 ) {     fEndOffset += (fEndContainer.getChildNodes().getLength() - currentChildren);
            }
        }
        fInsertedFromRange = false;
    }

    public void surroundContents(Node newParent)
        throws DOMException, RangeException
    {
        if (newParent==null) return;
        int type = newParent.getNodeType();

        if (fDocument.errorChecking) {
            if (fDetach) {
                throw new DOMException(
                        DOMException.INVALID_STATE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
            }
            if (type == Node.ATTRIBUTE_NODE
                    || type == Node.ENTITY_NODE
                    || type == Node.NOTATION_NODE
                    || type == Node.DOCUMENT_TYPE_NODE
                    || type == Node.DOCUMENT_NODE
                    || type == Node.DOCUMENT_FRAGMENT_NODE)
            {
                throw new RangeExceptionImpl(
                        RangeException.INVALID_NODE_TYPE_ERR,
                        DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_NODE_TYPE_ERR", null));
            }
        }

        Node realStart = fStartContainer;
        Node realEnd = fEndContainer;
        if (fStartContainer.getNodeType() == Node.TEXT_NODE) {
            realStart = fStartContainer.getParentNode();
        }
        if (fEndContainer.getNodeType() == Node.TEXT_NODE) {
            realEnd = fEndContainer.getParentNode();
        }

        if (realStart != realEnd) {
                throw new RangeExceptionImpl(
                RangeException.BAD_BOUNDARYPOINTS_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "BAD_BOUNDARYPOINTS_ERR", null));
        }

        DocumentFragment frag = extractContents();
        insertNode(newParent);
        newParent.appendChild(frag);
        selectNode(newParent);
    }

    public Range cloneRange(){
        if( fDetach) {
                throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }

        Range range = fDocument.createRange();
        range.setStart(fStartContainer, fStartOffset);
        range.setEnd(fEndContainer, fEndOffset);
        return range;
    }

    public String toString(){
        if( fDetach) {
                throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }

        Node node = fStartContainer;
        Node stopNode = fEndContainer;
        StringBuffer sb = new StringBuffer();
        if (fStartContainer.getNodeType() == Node.TEXT_NODE
         || fStartContainer.getNodeType() == Node.CDATA_SECTION_NODE
        ) {
            if (fStartContainer == fEndContainer) {
                sb.append(fStartContainer.getNodeValue().substring(fStartOffset, fEndOffset));
                return sb.toString();
            }
            sb.append(fStartContainer.getNodeValue().substring(fStartOffset));
            node=nextNode (node,true); }
        else {  node=node.getFirstChild();
            if (fStartOffset>0) { int counter=0;
               while (counter<fStartOffset && node!=null) {
                   node=node.getNextSibling();
                   counter++;
               }
            }
            if (node == null) {
                   node = nextNode(fStartContainer,false);
            }
        }
        if ( fEndContainer.getNodeType()!= Node.TEXT_NODE &&
             fEndContainer.getNodeType()!= Node.CDATA_SECTION_NODE ){
             int i=fEndOffset;
             stopNode = fEndContainer.getFirstChild();
             while( i>0 && stopNode!=null ){
                 --i;
                 stopNode = stopNode.getNextSibling();
             }
             if ( stopNode == null )
                 stopNode = nextNode( fEndContainer, false );
         }
         while (node != stopNode) {  if (node == null) break;
             if (node.getNodeType() == Node.TEXT_NODE
             ||  node.getNodeType() == Node.CDATA_SECTION_NODE) {
                 sb.append(node.getNodeValue());
             }

             node = nextNode(node, true);
         }

        if (fEndContainer.getNodeType() == Node.TEXT_NODE
         || fEndContainer.getNodeType() == Node.CDATA_SECTION_NODE) {
            sb.append(fEndContainer.getNodeValue().substring(0,fEndOffset));
        }
        return sb.toString();
    }

    public void detach() {
        if( fDetach) {
            throw new DOMException(
            DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }
        fDetach = true;
        fDocument.removeRange(this);
    }

    void signalSplitData(Node node, Node newNode, int offset) {
        fSplitNode = node;
        fDocument.splitData(node, newNode, offset);
        fSplitNode = null;
    }


    void receiveSplitData(Node node, Node newNode, int offset) {
        if (node == null || newNode == null) return;
        if (fSplitNode == node) return;

        if (node == fStartContainer
        && fStartContainer.getNodeType() == Node.TEXT_NODE) {
            if (fStartOffset > offset) {
                fStartOffset = fStartOffset - offset;
                fStartContainer = newNode;
            }
        }
        if (node == fEndContainer
        && fEndContainer.getNodeType() == Node.TEXT_NODE) {
            if (fEndOffset > offset) {
                fEndOffset = fEndOffset-offset;
                fEndContainer = newNode;
            }
        }

    }


    void deleteData(CharacterData node, int offset, int count) {
        fDeleteNode = node;
        node.deleteData( offset,  count);
        fDeleteNode = null;
    }



    void receiveDeletedText(Node node, int offset, int count) {
        if (node == null) return;
        if (fDeleteNode == node) return;
        if (node == fStartContainer
        && fStartContainer.getNodeType() == Node.TEXT_NODE) {
            if (fStartOffset > offset+count) {
                fStartOffset = offset+(fStartOffset-(offset+count));
            } else
            if (fStartOffset > offset) {
                fStartOffset = offset;
            }
        }
        if (node == fEndContainer
        && fEndContainer.getNodeType() == Node.TEXT_NODE) {
            if (fEndOffset > offset+count) {
                fEndOffset = offset+(fEndOffset-(offset+count));
            } else
            if (fEndOffset > offset) {
                fEndOffset = offset;
            }
        }

    }


    void insertData(CharacterData node, int index, String insert) {
        fInsertNode = node;
        node.insertData( index,  insert);
        fInsertNode = null;
    }



    void receiveInsertedText(Node node, int index, int len) {
        if (node == null) return;
        if (fInsertNode == node) return;
        if (node == fStartContainer
        && fStartContainer.getNodeType() == Node.TEXT_NODE) {
            if (index < fStartOffset) {
                fStartOffset = fStartOffset+len;
            }
        }
        if (node == fEndContainer
        && fEndContainer.getNodeType() == Node.TEXT_NODE) {
            if (index < fEndOffset) {
                fEndOffset = fEndOffset+len;
            }
        }

    }


    void receiveReplacedText(Node node) {
        if (node == null) return;
        if (node == fStartContainer
        && fStartContainer.getNodeType() == Node.TEXT_NODE) {
            fStartOffset = 0;
        }
        if (node == fEndContainer
        && fEndContainer.getNodeType() == Node.TEXT_NODE) {
            fEndOffset = 0;
        }

    }


    public void insertedNodeFromDOM(Node node) {
        if (node == null) return;
        if (fInsertNode == node) return;
        if (fInsertedFromRange) return; Node parent = node.getParentNode();

        if (parent == fStartContainer) {
            int index = indexOf(node, fStartContainer);
            if (index < fStartOffset) {
                fStartOffset++;
            }
        }

        if (parent == fEndContainer) {
            int index = indexOf(node, fEndContainer);
            if (index < fEndOffset) {
                fEndOffset++;
            }
        }

    }



    Node fRemoveChild = null;
    Node removeChild(Node parent, Node child) {
        fRemoveChild = child;
        Node n = parent.removeChild(child);
        fRemoveChild = null;
        return n;
    }


    void removeNode(Node node) {
        if (node == null) return;
        if (fRemoveChild == node) return;

        Node parent = node.getParentNode();

        if (parent == fStartContainer) {
            int index = indexOf(node, fStartContainer);
            if (index < fStartOffset) {
                fStartOffset--;
            }
        }

        if (parent == fEndContainer) {
            int index = indexOf(node, fEndContainer);
            if (index < fEndOffset) {
                fEndOffset--;
            }
        }
        if (parent != fStartContainer
        ||  parent != fEndContainer) {
            if (isAncestorOf(node, fStartContainer)) {
                fStartContainer = parent;
                fStartOffset = indexOf( node, parent);
            }
            if (isAncestorOf(node, fEndContainer)) {
                fEndContainer = parent;
                fEndOffset = indexOf( node, parent);
            }
        }

    }

    static final int EXTRACT_CONTENTS = 1;
    static final int CLONE_CONTENTS = 2;
    static final int DELETE_CONTENTS = 3;


    private DocumentFragment traverseContents( int how )
        throws DOMException
    {
        if (fStartContainer == null || fEndContainer == null) {
            return null; }

        if( fDetach) {
            throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }



        if ( fStartContainer == fEndContainer )
            return traverseSameContainer( how );


        int endContainerDepth = 0;
        for ( Node c = fEndContainer, p = c.getParentNode();
             p != null;
             c = p, p = p.getParentNode())
        {
            if (p == fStartContainer)
                return traverseCommonStartContainer( c, how );
            ++endContainerDepth;
        }

        int startContainerDepth = 0;
        for ( Node c = fStartContainer, p = c.getParentNode();
             p != null;
             c = p, p = p.getParentNode())
        {
            if (p == fEndContainer)
                return traverseCommonEndContainer( c, how );
            ++startContainerDepth;
        }

        int depthDiff = startContainerDepth - endContainerDepth;

        Node startNode = fStartContainer;
        while (depthDiff > 0) {
            startNode = startNode.getParentNode();
            depthDiff--;
        }

        Node endNode = fEndContainer;
        while (depthDiff < 0) {
            endNode = endNode.getParentNode();
            depthDiff++;
        }

        for( Node sp = startNode.getParentNode(), ep = endNode.getParentNode();
             sp!=ep;
             sp = sp.getParentNode(), ep = ep.getParentNode() )
        {
            startNode = sp;
            endNode = ep;
        }
        return traverseCommonAncestors( startNode, endNode, how );
    }


    private DocumentFragment traverseSameContainer( int how )
    {
        DocumentFragment frag = null;
        if ( how!=DELETE_CONTENTS)
            frag = fDocument.createDocumentFragment();

        if ( fStartOffset==fEndOffset )
            return frag;

        if ( fStartContainer.getNodeType()==Node.TEXT_NODE )
        {
            String s = fStartContainer.getNodeValue();
            String sub = s.substring( fStartOffset, fEndOffset );

            if ( how != CLONE_CONTENTS )
            {
                ((TextImpl)fStartContainer).deleteData(fStartOffset,
                     fEndOffset-fStartOffset) ;
                collapse( true );
            }
            if ( how==DELETE_CONTENTS)
                return null;
            frag.appendChild( fDocument.createTextNode(sub) );
            return frag;
        }

        Node n = getSelectedNode( fStartContainer, fStartOffset );
        int cnt = fEndOffset - fStartOffset;
        while( cnt > 0 )
        {
            Node sibling = n.getNextSibling();
            Node xferNode = traverseFullySelected( n, how );
            if ( frag!=null )
                frag.appendChild( xferNode );
            --cnt;
            n = sibling;
        }

        if ( how != CLONE_CONTENTS )
            collapse( true );
        return frag;
    }


    private DocumentFragment
        traverseCommonStartContainer( Node endAncestor, int how )
    {
        DocumentFragment frag = null;
        if ( how!=DELETE_CONTENTS)
            frag = fDocument.createDocumentFragment();
        Node n = traverseRightBoundary( endAncestor, how );
        if ( frag!=null )
            frag.appendChild( n );

        int endIdx = indexOf( endAncestor, fStartContainer );
        int cnt = endIdx - fStartOffset;
        if ( cnt <=0 )
        {
            if ( how != CLONE_CONTENTS )
            {
                setEndBefore( endAncestor );
                collapse( false );
            }
            return frag;
        }

        n = endAncestor.getPreviousSibling();
        while( cnt > 0 )
        {
            Node sibling = n.getPreviousSibling();
            Node xferNode = traverseFullySelected( n, how );
            if ( frag!=null )
                frag.insertBefore( xferNode, frag.getFirstChild() );
            --cnt;
            n = sibling;
        }
        if ( how != CLONE_CONTENTS )
        {
            setEndBefore( endAncestor );
            collapse( false );
        }
        return frag;
    }


    private DocumentFragment
        traverseCommonEndContainer( Node startAncestor, int how )
    {
        DocumentFragment frag = null;
        if ( how!=DELETE_CONTENTS)
            frag = fDocument.createDocumentFragment();
        Node n = traverseLeftBoundary( startAncestor, how );
        if ( frag!=null )
            frag.appendChild( n );
        int startIdx = indexOf( startAncestor, fEndContainer );
        ++startIdx;  int cnt = fEndOffset - startIdx;
        n = startAncestor.getNextSibling();
        while( cnt > 0 )
        {
            Node sibling = n.getNextSibling();
            Node xferNode = traverseFullySelected( n, how );
            if ( frag!=null )
                frag.appendChild( xferNode );
            --cnt;
            n = sibling;
        }

        if ( how != CLONE_CONTENTS )
        {
            setStartAfter( startAncestor );
            collapse( true );
        }

        return frag;
    }


    private DocumentFragment
        traverseCommonAncestors( Node startAncestor, Node endAncestor, int how )
    {
        DocumentFragment frag = null;
        if ( how!=DELETE_CONTENTS)
            frag = fDocument.createDocumentFragment();

        Node n = traverseLeftBoundary( startAncestor, how );
        if ( frag!=null )
            frag.appendChild( n );

        Node commonParent = startAncestor.getParentNode();
        int startOffset = indexOf( startAncestor, commonParent );
        int endOffset = indexOf( endAncestor, commonParent );
        ++startOffset;

        int cnt = endOffset - startOffset;
        Node sibling = startAncestor.getNextSibling();

        while( cnt > 0 )
        {
            Node nextSibling = sibling.getNextSibling();
            n = traverseFullySelected( sibling, how );
            if ( frag!=null )
                frag.appendChild( n );
            sibling = nextSibling;
            --cnt;
        }

        n = traverseRightBoundary( endAncestor, how );
        if ( frag!=null )
            frag.appendChild( n );

        if ( how != CLONE_CONTENTS )
        {
            setStartAfter( startAncestor );
            collapse( true );
        }
        return frag;
    }


    private Node traverseRightBoundary( Node root, int how )
    {
        Node next = getSelectedNode( fEndContainer, fEndOffset-1 );
        boolean isFullySelected = ( next!=fEndContainer );

        if ( next==root )
            return traverseNode( next, isFullySelected, false, how );

        Node parent = next.getParentNode();
        Node clonedParent = traverseNode( parent, false, false, how );

        while( parent!=null )
        {
            while( next!=null )
            {
                Node prevSibling = next.getPreviousSibling();
                Node clonedChild =
                    traverseNode( next, isFullySelected, false, how );
                if ( how!=DELETE_CONTENTS )
                {
                    clonedParent.insertBefore(
                        clonedChild,
                        clonedParent.getFirstChild()
                    );
                }
                isFullySelected = true;
                next = prevSibling;
            }
            if ( parent==root )
                return clonedParent;

            next = parent.getPreviousSibling();
            parent = parent.getParentNode();
            Node clonedGrandParent = traverseNode( parent, false, false, how );
            if ( how!=DELETE_CONTENTS )
                clonedGrandParent.appendChild( clonedParent );
            clonedParent = clonedGrandParent;

        }

        return null;
    }


    private Node traverseLeftBoundary( Node root, int how )
    {
        Node next = getSelectedNode( getStartContainer(), getStartOffset() );
        boolean isFullySelected = ( next!=getStartContainer() );

        if ( next==root )
            return traverseNode( next, isFullySelected, true, how );

        Node parent = next.getParentNode();
        Node clonedParent = traverseNode( parent, false, true, how );

        while( parent!=null )
        {
            while( next!=null )
            {
                Node nextSibling = next.getNextSibling();
                Node clonedChild =
                    traverseNode( next, isFullySelected, true, how );
                if ( how!=DELETE_CONTENTS )
                    clonedParent.appendChild(clonedChild);
                isFullySelected = true;
                next = nextSibling;
            }
            if ( parent==root )
                return clonedParent;

            next = parent.getNextSibling();
            parent = parent.getParentNode();
            Node clonedGrandParent = traverseNode( parent, false, true, how );
            if ( how!=DELETE_CONTENTS )
                clonedGrandParent.appendChild( clonedParent );
            clonedParent = clonedGrandParent;

        }

        return null;

    }


    private Node traverseNode( Node n, boolean isFullySelected, boolean isLeft, int how )
    {
        if ( isFullySelected )
            return traverseFullySelected( n, how );
        if ( n.getNodeType()==Node.TEXT_NODE )
            return traverseTextNode( n, isLeft, how );
        return traversePartiallySelected( n, how );
    }


    private Node traverseFullySelected( Node n, int how )
    {
        switch( how )
        {
        case CLONE_CONTENTS:
            return n.cloneNode( true );
        case EXTRACT_CONTENTS:
            if ( n.getNodeType()==Node.DOCUMENT_TYPE_NODE )
            {
                throw new DOMException(
                        DOMException.HIERARCHY_REQUEST_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "HIERARCHY_REQUEST_ERR", null));
            }
            return n;
        case DELETE_CONTENTS:
            n.getParentNode().removeChild(n);
            return null;
        }
        return null;
    }


    private Node traversePartiallySelected( Node n, int how )
    {
        switch( how )
        {
        case DELETE_CONTENTS:
            return null;
        case CLONE_CONTENTS:
        case EXTRACT_CONTENTS:
            return n.cloneNode( false );
        }
        return null;
    }


    private Node traverseTextNode( Node n, boolean isLeft, int how )
    {
        String txtValue = n.getNodeValue();
        String newNodeValue;
        String oldNodeValue;

        if ( isLeft )
        {
            int offset = getStartOffset();
            newNodeValue = txtValue.substring( offset );
            oldNodeValue = txtValue.substring( 0, offset );
        }
        else
        {
            int offset = getEndOffset();
            newNodeValue = txtValue.substring( 0, offset );
            oldNodeValue = txtValue.substring( offset );
        }

        if ( how != CLONE_CONTENTS )
            n.setNodeValue( oldNodeValue );
        if ( how==DELETE_CONTENTS )
            return null;
        Node newNode = n.cloneNode( false );
        newNode.setNodeValue( newNodeValue );
        return newNode;
    }

    void checkIndex(Node refNode, int offset) throws DOMException
    {
        if (offset < 0) {
            throw new DOMException(
                DOMException.INDEX_SIZE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INDEX_SIZE_ERR", null));
        }

        int type = refNode.getNodeType();

        if (type == Node.TEXT_NODE
            || type == Node.CDATA_SECTION_NODE
            || type == Node.COMMENT_NODE
            || type == Node.PROCESSING_INSTRUCTION_NODE) {
            if (offset > refNode.getNodeValue().length()) {
                throw new DOMException(DOMException.INDEX_SIZE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INDEX_SIZE_ERR", null));
            }
        }
        else {
            if (offset > refNode.getChildNodes().getLength()) {
                throw new DOMException(DOMException.INDEX_SIZE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INDEX_SIZE_ERR", null));
            }
        }
    }


        private Node getRootContainer( Node node )
        {
                if ( node==null )
                        return null;

                while( node.getParentNode()!=null )
                        node = node.getParentNode();
                return node;
        }


        private boolean isLegalContainer( Node node )
        {
                if ( node==null )
                        return false;

                while( node!=null )
                {
                        switch( node.getNodeType() )
                        {
                        case Node.ENTITY_NODE:
                        case Node.NOTATION_NODE:
                        case Node.DOCUMENT_TYPE_NODE:
                                return false;
                        }
                        node = node.getParentNode();
                }

                return true;
        }



        private boolean hasLegalRootContainer( Node node )
        {
                if ( node==null )
                        return false;

                Node rootContainer = getRootContainer( node );
                switch( rootContainer.getNodeType() )
                {
                case Node.ATTRIBUTE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.DOCUMENT_FRAGMENT_NODE:
                        return true;
                }
                return false;
        }


        private boolean isLegalContainedNode( Node node )
        {
                if ( node==null )
                        return false;
                switch( node.getNodeType() )
                {
                case Node.DOCUMENT_NODE:
                case Node.DOCUMENT_FRAGMENT_NODE:
                case Node.ATTRIBUTE_NODE:
                case Node.ENTITY_NODE:
                case Node.NOTATION_NODE:
                        return false;
                }
                return true;
        }

    Node nextNode(Node node, boolean visitChildren) {

        if (node == null) return null;

        Node result;
        if (visitChildren) {
            result = node.getFirstChild();
            if (result != null) {
                return result;
            }
        }

        result = node.getNextSibling();
        if (result != null) {
            return result;
        }


        Node parent = node.getParentNode();
        while (parent != null
               && parent != fDocument
                ) {
            result = parent.getNextSibling();
            if (result != null) {
                return result;
            } else {
                parent = parent.getParentNode();
            }

        } return null;
    }


    boolean isAncestorOf(Node a, Node b) {
        for (Node node=b; node != null; node=node.getParentNode()) {
            if (node == a) return true;
        }
        return false;
    }


    int indexOf(Node child, Node parent) {
        if (child.getParentNode() != parent) return -1;
        int i = 0;
        for(Node node = parent.getFirstChild(); node!= child; node=node.getNextSibling()) {
            i++;
        }
        return i;
    }


    private Node getSelectedNode( Node container, int offset )
    {
        if ( container.getNodeType() == Node.TEXT_NODE )
            return container;

        if ( offset<0 )
            return container;

        Node child = container.getFirstChild();
        while( child!=null && offset > 0 )
        {
            --offset;
            child = child.getNextSibling();
        }
        if ( child!=null )
            return child;
        return container;
    }

}
