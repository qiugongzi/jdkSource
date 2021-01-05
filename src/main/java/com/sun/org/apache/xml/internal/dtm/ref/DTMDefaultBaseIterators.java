


package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.*;

import javax.xml.transform.Source;

import com.sun.org.apache.xml.internal.utils.XMLStringFactory;

import com.sun.org.apache.xml.internal.res.XMLErrorResources;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter;


public abstract class DTMDefaultBaseIterators extends DTMDefaultBaseTraversers
{


  public DTMDefaultBaseIterators(DTMManager mgr, Source source,
                                 int dtmIdentity,
                                 DTMWSFilter whiteSpaceFilter,
                                 XMLStringFactory xstringfactory,
                                 boolean doIndexing)
  {
    super(mgr, source, dtmIdentity, whiteSpaceFilter,
          xstringfactory, doIndexing);
  }


  public DTMDefaultBaseIterators(DTMManager mgr, Source source,
                                 int dtmIdentity,
                                 DTMWSFilter whiteSpaceFilter,
                                 XMLStringFactory xstringfactory,
                                 boolean doIndexing,
                                 int blocksize,
                                 boolean usePrevsib,
                                 boolean newNameTable)
  {
    super(mgr, source, dtmIdentity, whiteSpaceFilter,
          xstringfactory, doIndexing, blocksize, usePrevsib, newNameTable);
  }


  public DTMAxisIterator getTypedAxisIterator(int axis, int type)
  {

    DTMAxisIterator iterator = null;



    {
      switch (axis)
      {
      case Axis.SELF :
        iterator = new TypedSingletonIterator(type);
        break;
      case Axis.CHILD :
        iterator = new TypedChildrenIterator(type);
        break;
      case Axis.PARENT :
        return (new ParentIterator().setNodeType(type));
      case Axis.ANCESTOR :
        return (new TypedAncestorIterator(type));
      case Axis.ANCESTORORSELF :
        return ((new TypedAncestorIterator(type)).includeSelf());
      case Axis.ATTRIBUTE :
        return (new TypedAttributeIterator(type));
      case Axis.DESCENDANT :
        iterator = new TypedDescendantIterator(type);
        break;
      case Axis.DESCENDANTORSELF :
        iterator = (new TypedDescendantIterator(type)).includeSelf();
        break;
      case Axis.FOLLOWING :
        iterator = new TypedFollowingIterator(type);
        break;
      case Axis.PRECEDING :
        iterator = new TypedPrecedingIterator(type);
        break;
      case Axis.FOLLOWINGSIBLING :
        iterator = new TypedFollowingSiblingIterator(type);
        break;
      case Axis.PRECEDINGSIBLING :
        iterator = new TypedPrecedingSiblingIterator(type);
        break;
      case Axis.NAMESPACE :
        iterator = new TypedNamespaceIterator(type);
        break;
      case Axis.ROOT :
        iterator = new TypedRootIterator(type);
        break;
      default :
        throw new DTMException(XMLMessages.createXMLMessage(
            XMLErrorResources.ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
            new Object[]{Axis.getNames(axis)}));
            }
    }

    return (iterator);
  }


  public DTMAxisIterator getAxisIterator(final int axis)
  {

    DTMAxisIterator iterator = null;

    switch (axis)
    {
    case Axis.SELF :
      iterator = new SingletonIterator();
      break;
    case Axis.CHILD :
      iterator = new ChildrenIterator();
      break;
    case Axis.PARENT :
      return (new ParentIterator());
    case Axis.ANCESTOR :
      return (new AncestorIterator());
    case Axis.ANCESTORORSELF :
      return ((new AncestorIterator()).includeSelf());
    case Axis.ATTRIBUTE :
      return (new AttributeIterator());
    case Axis.DESCENDANT :
      iterator = new DescendantIterator();
      break;
    case Axis.DESCENDANTORSELF :
      iterator = (new DescendantIterator()).includeSelf();
      break;
    case Axis.FOLLOWING :
      iterator = new FollowingIterator();
      break;
    case Axis.PRECEDING :
      iterator = new PrecedingIterator();
      break;
    case Axis.FOLLOWINGSIBLING :
      iterator = new FollowingSiblingIterator();
      break;
    case Axis.PRECEDINGSIBLING :
      iterator = new PrecedingSiblingIterator();
      break;
    case Axis.NAMESPACE :
      iterator = new NamespaceIterator();
      break;
    case Axis.ROOT :
      iterator = new RootIterator();
      break;
    default :
      throw new DTMException(XMLMessages.createXMLMessage(
        XMLErrorResources.ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
        new Object[]{Axis.getNames(axis)}));
        }

    return (iterator);
  }


  public abstract class InternalAxisIteratorBase extends DTMAxisIteratorBase
  {

    protected int _currentNode;


    public void setMark()
    {
      _markedNode = _currentNode;
    }


    public void gotoMark()
    {
      _currentNode = _markedNode;
    }

  }  public final class ChildrenIterator extends InternalAxisIteratorBase
  {


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = (node == DTM.NULL) ? DTM.NULL
                                          : _firstch(makeNodeIdentity(node));

        return resetPosition();
      }

      return this;
    }


    public int next()
    {
      if (_currentNode != NULL) {
        int node = _currentNode;
        _currentNode = _nextsib(node);
        return returnNode(makeNodeHandle(node));
      }

      return END;
    }
  }  public final class ParentIterator extends InternalAxisIteratorBase
  {


    private int _nodeType = -1;


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = getParent(node);

        return resetPosition();
      }

      return this;
    }


    public DTMAxisIterator setNodeType(final int type)
    {

      _nodeType = type;

      return this;
    }


    public int next()
    {
      int result = _currentNode;

      if (_nodeType >= DTM.NTYPES) {
        if (_nodeType != getExpandedTypeID(_currentNode)) {
          result = END;
        }
      } else if (_nodeType != NULL) {
        if (_nodeType != getNodeType(_currentNode)) {
          result = END;
        }
      }

      _currentNode = END;

      return returnNode(result);
    }
  }  public final class TypedChildrenIterator extends InternalAxisIteratorBase
  {


    private final int _nodeType;


    public TypedChildrenIterator(int nodeType)
    {
      _nodeType = nodeType;
    }


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = (node == DTM.NULL)
                                   ? DTM.NULL
                                   : _firstch(makeNodeIdentity(_startNode));

        return resetPosition();
      }

      return this;
    }


    public int next()
    {
      int eType;
      int node = _currentNode;

      int nodeType = _nodeType;

      if (nodeType >= DTM.NTYPES) {
        while (node != DTM.NULL && _exptype(node) != nodeType) {
          node = _nextsib(node);
        }
      } else {
        while (node != DTM.NULL) {
          eType = _exptype(node);
          if (eType < DTM.NTYPES) {
            if (eType == nodeType) {
              break;
            }
          } else if (m_expandedNameTable.getType(eType) == nodeType) {
            break;
          }
          node = _nextsib(node);
        }
      }

      if (node == DTM.NULL) {
        _currentNode = DTM.NULL;
        return DTM.NULL;
      } else {
        _currentNode = _nextsib(node);
        return returnNode(makeNodeHandle(node));
      }

    }
  }  public final class NamespaceChildrenIterator
          extends InternalAxisIteratorBase
  {


    private final int _nsType;


    public NamespaceChildrenIterator(final int type)
    {
      _nsType = type;
    }


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = (node == DTM.NULL) ? DTM.NULL : NOTPROCESSED;

        return resetPosition();
      }

      return this;
    }


    public int next()
    {
      if (_currentNode != DTM.NULL) {
        for (int node = (NOTPROCESSED == _currentNode)
                                  ? _firstch(makeNodeIdentity(_startNode))
                                  : _nextsib(_currentNode);
             node != END;
             node = _nextsib(node)) {
          if (m_expandedNameTable.getNamespaceID(_exptype(node)) == _nsType) {
            _currentNode = node;

            return returnNode(node);
          }
        }
      }

      return END;
    }
  }  public class NamespaceIterator
          extends InternalAxisIteratorBase
  {


    public NamespaceIterator()
    {

      super();
    }


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = getFirstNamespaceNode(node, true);

        return resetPosition();
      }

      return this;
    }


    public int next()
    {

      int node = _currentNode;

      if (DTM.NULL != node)
        _currentNode = getNextNamespaceNode(_startNode, node, true);

      return returnNode(node);
    }
  }  public class TypedNamespaceIterator extends NamespaceIterator
  {


    private final int _nodeType;


    public TypedNamespaceIterator(int nodeType)
    {
      super();
      _nodeType = nodeType;
    }


    public int next()
    {
        int node;

      for (node = _currentNode;
           node != END;
           node = getNextNamespaceNode(_startNode, node, true)) {
        if (getExpandedTypeID(node) == _nodeType
            || getNodeType(node) == _nodeType
            || getNamespaceType(node) == _nodeType) {
          _currentNode = node;

          return returnNode(node);
        }
      }

      return (_currentNode =END);
    }
  }  public class RootIterator
          extends InternalAxisIteratorBase
  {


    public RootIterator()
    {

      super();
    }


    public DTMAxisIterator setStartNode(int node)
    {

      if (_isRestartable)
      {
        _startNode = getDocumentRoot(node);
        _currentNode = NULL;

        return resetPosition();
      }

      return this;
    }


    public int next()
    {
      if(_startNode == _currentNode)
        return NULL;

      _currentNode = _startNode;

      return returnNode(_startNode);
    }
  }  public class TypedRootIterator extends RootIterator
  {


    private final int _nodeType;


    public TypedRootIterator(int nodeType)
    {
      super();
      _nodeType = nodeType;
    }


    public int next()
    {
        if(_startNode == _currentNode)
        return NULL;

      int nodeType = _nodeType;
      int node = _startNode;
      int expType = getExpandedTypeID(node);

      _currentNode = node;

      if (nodeType >= DTM.NTYPES) {
        if (nodeType == expType) {
          return returnNode(node);
        }
      } else {
        if (expType < DTM.NTYPES) {
          if (expType == nodeType) {
            return returnNode(node);
          }
        } else {
          if (m_expandedNameTable.getType(expType) == nodeType) {
            return returnNode(node);
          }
        }
      }

      return END;
    }
  }  public final class NamespaceAttributeIterator
          extends InternalAxisIteratorBase
  {


    private final int _nsType;


    public NamespaceAttributeIterator(int nsType)
    {

      super();

      _nsType = nsType;
    }


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = getFirstNamespaceNode(node, false);

        return resetPosition();
      }

      return this;
    }


    public int next()
    {

      int node = _currentNode;

      if (DTM.NULL != node)
        _currentNode = getNextNamespaceNode(_startNode, node, false);

      return returnNode(node);
    }
  }  public class FollowingSiblingIterator extends InternalAxisIteratorBase
  {


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = makeNodeIdentity(node);

        return resetPosition();
      }

      return this;
    }


    public int next()
    {
      _currentNode = (_currentNode == DTM.NULL) ? DTM.NULL
                                                : _nextsib(_currentNode);
      return returnNode(makeNodeHandle(_currentNode));
    }
  }  public final class TypedFollowingSiblingIterator
          extends FollowingSiblingIterator
  {


    private final int _nodeType;


    public TypedFollowingSiblingIterator(int type)
    {
      _nodeType = type;
    }


    public int next()
    {
      if (_currentNode == DTM.NULL) {
        return DTM.NULL;
      }

      int node = _currentNode;
      int eType;
      int nodeType = _nodeType;

      if (nodeType >= DTM.NTYPES) {
        do {
          node = _nextsib(node);
        } while (node != DTM.NULL && _exptype(node) != nodeType);
      } else {
        while ((node = _nextsib(node)) != DTM.NULL) {
          eType = _exptype(node);
          if (eType < DTM.NTYPES) {
            if (eType == nodeType) {
              break;
            }
          } else if (m_expandedNameTable.getType(eType) == nodeType) {
            break;
          }
        }
      }

      _currentNode = node;

      return (_currentNode == DTM.NULL)
                      ? DTM.NULL
                      : returnNode(makeNodeHandle(_currentNode));
    }
  }  public final class AttributeIterator extends InternalAxisIteratorBase
  {

    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = getFirstAttributeIdentity(makeNodeIdentity(node));

        return resetPosition();
      }

      return this;
    }


    public int next()
    {

      final int node = _currentNode;

      if (node != NULL) {
        _currentNode = getNextAttributeIdentity(node);
        return returnNode(makeNodeHandle(node));
      }

      return NULL;
    }
  }  public final class TypedAttributeIterator extends InternalAxisIteratorBase
  {


    private final int _nodeType;


    public TypedAttributeIterator(int nodeType)
    {
      _nodeType = nodeType;
    }

    public DTMAxisIterator setStartNode(int node)
    {
      if (_isRestartable)
      {
        _startNode = node;

        _currentNode = getTypedAttribute(node, _nodeType);

        return resetPosition();
      }

      return this;
    }


    public int next()
    {

      final int node = _currentNode;

      _currentNode = NULL;

      return returnNode(node);
    }
  }  public class PrecedingSiblingIterator extends InternalAxisIteratorBase
  {


    protected int _startNodeID;


    public boolean isReverse()
    {
      return true;
    }


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        node = _startNodeID = makeNodeIdentity(node);

        if(node == NULL)
        {
          _currentNode = node;
          return resetPosition();
        }

        int type = m_expandedNameTable.getType(_exptype(node));
        if(ExpandedNameTable.ATTRIBUTE == type
           || ExpandedNameTable.NAMESPACE == type )
        {
          _currentNode = node;
        }
        else
        {
          _currentNode = _parent(node);
          if(NULL!=_currentNode)
            _currentNode = _firstch(_currentNode);
          else
            _currentNode = node;
        }

        return resetPosition();
      }

      return this;
    }


    public int next()
    {

      if (_currentNode == _startNodeID || _currentNode == DTM.NULL)
      {
        return NULL;
      }
      else
      {
        final int node = _currentNode;
        _currentNode = _nextsib(node);

        return returnNode(makeNodeHandle(node));
      }
    }
  }  public final class TypedPrecedingSiblingIterator
          extends PrecedingSiblingIterator
  {


    private final int _nodeType;


    public TypedPrecedingSiblingIterator(int type)
    {
      _nodeType = type;
    }


    public int next()
    {
      int node = _currentNode;
      int expType;

      int nodeType = _nodeType;
      int startID = _startNodeID;

      if (nodeType >= DTM.NTYPES) {
        while (node != NULL && node != startID && _exptype(node) != nodeType) {
          node = _nextsib(node);
        }
      } else {
        while (node != NULL && node != startID) {
          expType = _exptype(node);
          if (expType < DTM.NTYPES) {
            if (expType == nodeType) {
              break;
            }
          } else {
            if (m_expandedNameTable.getType(expType) == nodeType) {
              break;
            }
          }
          node = _nextsib(node);
        }
      }

      if (node == DTM.NULL || node == _startNodeID) {
        _currentNode = NULL;
        return NULL;
      } else {
        _currentNode = _nextsib(node);
        return returnNode(makeNodeHandle(node));
      }
    }
  }  public class PrecedingIterator extends InternalAxisIteratorBase
  {


    private final int _maxAncestors = 8;


    protected int[] _stack = new int[_maxAncestors];


    protected int _sp, _oldsp;

    protected int _markedsp, _markedNode, _markedDescendant;




    public boolean isReverse()
    {
      return true;
    }


    public DTMAxisIterator cloneIterator()
    {
      _isRestartable = false;

      try
      {
        final PrecedingIterator clone = (PrecedingIterator) super.clone();
        final int[] stackCopy = new int[_stack.length];
        System.arraycopy(_stack, 0, stackCopy, 0, _stack.length);

        clone._stack = stackCopy;

        return clone;
      }
      catch (CloneNotSupportedException e)
      {
        throw new DTMException(XMLMessages.createXMLMessage(XMLErrorResources.ER_ITERATOR_CLONE_NOT_SUPPORTED, null)); }
    }


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        node = makeNodeIdentity(node);

        int parent, index;

       if (_type(node) == DTM.ATTRIBUTE_NODE)
        node = _parent(node);

        _startNode = node;
        _stack[index = 0] = node;



                parent=node;
                while ((parent = _parent(parent)) != NULL)
                {
                        if (++index == _stack.length)
                        {
                                final int[] stack = new int[index + 4];
                                System.arraycopy(_stack, 0, stack, 0, index);
                                _stack = stack;
                        }
                        _stack[index] = parent;
        }
        if(index>0)
                --index; _currentNode=_stack[index]; _oldsp = _sp = index;

        return resetPosition();
      }

      return this;
    }


    public int next()
    {
        for(++_currentNode;
                        _sp>=0;
                        ++_currentNode)
                {
                        if(_currentNode < _stack[_sp])
                        {
                                if(_type(_currentNode) != ATTRIBUTE_NODE &&
                                        _type(_currentNode) != NAMESPACE_NODE)
                                        return returnNode(makeNodeHandle(_currentNode));
                        }
                        else
                                --_sp;
                }
                return NULL;
    }

    public DTMAxisIterator reset()
    {

      _sp = _oldsp;

      return resetPosition();
    }

    public void setMark() {
        _markedsp = _sp;
        _markedNode = _currentNode;
        _markedDescendant = _stack[0];
    }

    public void gotoMark() {
        _sp = _markedsp;
        _currentNode = _markedNode;
    }
  }  public final class TypedPrecedingIterator extends PrecedingIterator
  {


    private final int _nodeType;


    public TypedPrecedingIterator(int type)
    {
      _nodeType = type;
    }


    public int next()
    {
      int node = _currentNode;
      int nodeType = _nodeType;

      if (nodeType >= DTM.NTYPES) {
        while (true) {
          node = node + 1;

          if (_sp < 0) {
            node = NULL;
            break;
          } else if (node >= _stack[_sp]) {
            if (--_sp < 0) {
              node = NULL;
              break;
            }
          } else if (_exptype(node) == nodeType) {
            break;
          }
        }
      } else {
        int expType;

        while (true) {
          node = node + 1;

          if (_sp < 0) {
            node = NULL;
            break;
          } else if (node >= _stack[_sp]) {
            if (--_sp < 0) {
              node = NULL;
              break;
            }
          } else {
            expType = _exptype(node);
            if (expType < DTM.NTYPES) {
              if (expType == nodeType) {
                break;
              }
            } else {
              if (m_expandedNameTable.getType(expType) == nodeType) {
                break;
              }
            }
          }
        }
      }

      _currentNode = node;

      return (node == NULL) ? NULL : returnNode(makeNodeHandle(node));
    }
  }  public class FollowingIterator extends InternalAxisIteratorBase
  {
    DTMAxisTraverser m_traverser; public FollowingIterator()
    {
      m_traverser = getAxisTraverser(Axis.FOLLOWING);
    }


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;

        _currentNode = m_traverser.first(node);

        return resetPosition();
      }

      return this;
    }


    public int next()
    {

      int node = _currentNode;

      _currentNode = m_traverser.next(_startNode, _currentNode);

      return returnNode(node);
    }
  }  public final class TypedFollowingIterator extends FollowingIterator
  {


    private final int _nodeType;


    public TypedFollowingIterator(int type)
    {
      _nodeType = type;
    }


    public int next()
    {

      int node;

      do{
       node = _currentNode;

      _currentNode = m_traverser.next(_startNode, _currentNode);

      }
      while (node != DTM.NULL
             && (getExpandedTypeID(node) != _nodeType && getNodeType(node) != _nodeType));

      return (node == DTM.NULL ? DTM.NULL :returnNode(node));
    }
  }  public class AncestorIterator extends InternalAxisIteratorBase
  {
    com.sun.org.apache.xml.internal.utils.NodeVector m_ancestors =
         new com.sun.org.apache.xml.internal.utils.NodeVector();

    int m_ancestorsPos;

    int m_markedPos;


    int m_realStartNode;


    public int getStartNode()
    {
      return m_realStartNode;
    }


    public final boolean isReverse()
    {
      return true;
    }


    public DTMAxisIterator cloneIterator()
    {
      _isRestartable = false;  try
      {
        final AncestorIterator clone = (AncestorIterator) super.clone();

        clone._startNode = _startNode;

        return clone;
      }
      catch (CloneNotSupportedException e)
      {
        throw new DTMException(XMLMessages.createXMLMessage(XMLErrorResources.ER_ITERATOR_CLONE_NOT_SUPPORTED, null)); }
    }


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      m_realStartNode = node;

      if (_isRestartable)
      {
        int nodeID = makeNodeIdentity(node);

        if (!_includeSelf && node != DTM.NULL) {
          nodeID = _parent(nodeID);
          node = makeNodeHandle(nodeID);
        }

        _startNode = node;

        while (nodeID != END) {
          m_ancestors.addElement(node);
          nodeID = _parent(nodeID);
          node = makeNodeHandle(nodeID);
        }
        m_ancestorsPos = m_ancestors.size()-1;

        _currentNode = (m_ancestorsPos>=0)
                               ? m_ancestors.elementAt(m_ancestorsPos)
                               : DTM.NULL;

        return resetPosition();
      }

      return this;
    }


    public DTMAxisIterator reset()
    {

      m_ancestorsPos = m_ancestors.size()-1;

      _currentNode = (m_ancestorsPos>=0) ? m_ancestors.elementAt(m_ancestorsPos)
                                         : DTM.NULL;

      return resetPosition();
    }


    public int next()
    {

      int next = _currentNode;

      int pos = --m_ancestorsPos;

      _currentNode = (pos >= 0) ? m_ancestors.elementAt(m_ancestorsPos)
                                : DTM.NULL;

      return returnNode(next);
    }

    public void setMark() {
        m_markedPos = m_ancestorsPos;
    }

    public void gotoMark() {
        m_ancestorsPos = m_markedPos;
        _currentNode = m_ancestorsPos>=0 ? m_ancestors.elementAt(m_ancestorsPos)
                                         : DTM.NULL;
    }
  }  public final class TypedAncestorIterator extends AncestorIterator
  {


    private final int _nodeType;


    public TypedAncestorIterator(int type)
    {
      _nodeType = type;
    }


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      m_realStartNode = node;

      if (_isRestartable)
      {
        int nodeID = makeNodeIdentity(node);
        int nodeType = _nodeType;

        if (!_includeSelf && node != DTM.NULL) {
          nodeID = _parent(nodeID);
        }

        _startNode = node;

        if (nodeType >= DTM.NTYPES) {
          while (nodeID != END) {
            int eType = _exptype(nodeID);

            if (eType == nodeType) {
              m_ancestors.addElement(makeNodeHandle(nodeID));
            }
            nodeID = _parent(nodeID);
          }
        } else {
          while (nodeID != END) {
            int eType = _exptype(nodeID);

            if ((eType >= DTM.NTYPES
                    && m_expandedNameTable.getType(eType) == nodeType)
                || (eType < DTM.NTYPES && eType == nodeType)) {
              m_ancestors.addElement(makeNodeHandle(nodeID));
            }
            nodeID = _parent(nodeID);
          }
        }
        m_ancestorsPos = m_ancestors.size()-1;

        _currentNode = (m_ancestorsPos>=0)
                               ? m_ancestors.elementAt(m_ancestorsPos)
                               : DTM.NULL;

        return resetPosition();
      }

      return this;
    }
  }  public class DescendantIterator extends InternalAxisIteratorBase
  {


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        node = makeNodeIdentity(node);
        _startNode = node;

        if (_includeSelf)
          node--;

        _currentNode = node;

        return resetPosition();
      }

      return this;
    }


    protected boolean isDescendant(int identity)
    {
      return (_parent(identity) >= _startNode) || (_startNode == identity);
    }


    public int next()
    {
      if (_startNode == NULL) {
        return NULL;
      }

      if (_includeSelf && (_currentNode + 1) == _startNode)
          return returnNode(makeNodeHandle(++_currentNode)); int node = _currentNode;
      int type;

      do {
        node++;
        type = _type(node);

        if (NULL == type ||!isDescendant(node)) {
          _currentNode = NULL;
          return END;
        }
      } while(ATTRIBUTE_NODE == type || TEXT_NODE == type
                 || NAMESPACE_NODE == type);

      _currentNode = node;
      return returnNode(makeNodeHandle(node));  }


  public DTMAxisIterator reset()
  {

    final boolean temp = _isRestartable;

    _isRestartable = true;

    setStartNode(makeNodeHandle(_startNode));

    _isRestartable = temp;

    return this;
  }

  }  public final class TypedDescendantIterator extends DescendantIterator
  {


    private final int _nodeType;


    public TypedDescendantIterator(int nodeType)
    {
      _nodeType = nodeType;
    }


    public int next()
    {
      int node;
      int type;

      if (_startNode == NULL) {
        return NULL;
      }

      node = _currentNode;

      do
      {
        node++;
        type = _type(node);

        if (NULL == type ||!isDescendant(node)) {
          _currentNode = NULL;
          return END;
        }
      }
      while (type != _nodeType && _exptype(node) != _nodeType);

      _currentNode = node;
      return returnNode(makeNodeHandle(node));
    }
  }  public class NthDescendantIterator extends DescendantIterator
  {


    int _pos;


    public NthDescendantIterator(int pos)
    {
      _pos = pos;
    }


    public int next()
    {

      int node;

      while ((node = super.next()) != END)
      {
        node = makeNodeIdentity(node);

        int parent = _parent(node);
        int child = _firstch(parent);
        int pos = 0;

        do
        {
          int type = _type(child);

          if (ELEMENT_NODE == type)
            pos++;
        }
        while ((pos < _pos) && (child = _nextsib(child)) != END);

        if (node == child)
          return node;
      }

      return (END);
    }
  }  public class SingletonIterator extends InternalAxisIteratorBase
  {


    private boolean _isConstant;


    public SingletonIterator()
    {
      this(Integer.MIN_VALUE, false);
    }


    public SingletonIterator(int node)
    {
      this(node, false);
    }


    public SingletonIterator(int node, boolean constant)
    {
      _currentNode = _startNode = node;
      _isConstant = constant;
    }


    public DTMAxisIterator setStartNode(int node)
    {
if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isConstant)
      {
        _currentNode = _startNode;

        return resetPosition();
      }
      else if (_isRestartable)
      {
          _currentNode = _startNode = node;

        return resetPosition();
      }

      return this;
    }


    public DTMAxisIterator reset()
    {

      if (_isConstant)
      {
        _currentNode = _startNode;

        return resetPosition();
      }
      else
      {
        final boolean temp = _isRestartable;

        _isRestartable = true;

        setStartNode(_startNode);

        _isRestartable = temp;
      }

      return this;
    }


    public int next()
    {

      final int result = _currentNode;

      _currentNode = END;

      return returnNode(result);
    }
  }  public final class TypedSingletonIterator extends SingletonIterator
  {


    private final int _nodeType;


    public TypedSingletonIterator(int nodeType)
    {
      _nodeType = nodeType;
    }


    public int next()
    {

      final int result = _currentNode;
      int nodeType = _nodeType;

      _currentNode = END;

      if (nodeType >= DTM.NTYPES) {
        if (getExpandedTypeID(result) == nodeType) {
          return returnNode(result);
        }
      } else {
        if (getNodeType(result) == nodeType) {
          return returnNode(result);
        }
      }

      return NULL;
    }
  }  }