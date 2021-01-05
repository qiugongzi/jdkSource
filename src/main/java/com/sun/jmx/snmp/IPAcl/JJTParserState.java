



package com.sun.jmx.snmp.IPAcl;

class JJTParserState {
  private java.util.Stack<Node> nodes;
  private java.util.Stack<Integer> marks;

  private int sp;               private int mk;               private boolean node_created;

  JJTParserState() {
    nodes = new java.util.Stack<>();
    marks = new java.util.Stack<>();
    sp = 0;
    mk = 0;
  }


  boolean nodeCreated() {
    return node_created;
  }


  void reset() {
    nodes.removeAllElements();
    marks.removeAllElements();
    sp = 0;
    mk = 0;
  }


  Node rootNode() {
    return nodes.elementAt(0);
  }


  void pushNode(Node n) {
    nodes.push(n);
    ++sp;
  }


  Node popNode() {
    if (--sp < mk) {
      mk = marks.pop().intValue();
    }
    return nodes.pop();
  }


  Node peekNode() {
    return nodes.peek();
  }


  int nodeArity() {
    return sp - mk;
  }


  void clearNodeScope(Node n) {
    while (sp > mk) {
      popNode();
    }
    mk = marks.pop().intValue();
  }


  void openNodeScope(Node n) {
    marks.push(new Integer(mk));
    mk = sp;
    n.jjtOpen();
  }



  void closeNodeScope(Node n, int num) {
    mk = marks.pop().intValue();
    while (num-- > 0) {
      Node c = popNode();
      c.jjtSetParent(n);
      n.jjtAddChild(c, num);
    }
    n.jjtClose();
    pushNode(n);
    node_created = true;
  }



  void closeNodeScope(Node n, boolean condition) {
    if (condition) {
      int a = nodeArity();
      mk = marks.pop().intValue();
      while (a-- > 0) {
        Node c = popNode();
        c.jjtSetParent(n);
        n.jjtAddChild(c, a);
      }
      n.jjtClose();
      pushNode(n);
      node_created = true;
    } else {
      mk = marks.pop().intValue();
      node_created = false;
    }
  }
}
