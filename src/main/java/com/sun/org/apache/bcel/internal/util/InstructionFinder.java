
package com.sun.org.apache.bcel.internal.util;



import java.util.*;
import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.*;
import com.sun.org.apache.regexp.internal.*;


public class InstructionFinder {
  private static final int OFFSET     = 32767; private static final int NO_OPCODES = 256;   private static final HashMap map = new HashMap(); private InstructionList     il;
  private String              il_string;    private InstructionHandle[] handles;      public InstructionFinder(InstructionList il) {
    this.il = il;
    reread();
  }


  public final void reread() {
    int    size  = il.getLength();
    char[] buf   = new char[size]; handles      = il.getInstructionHandles();

    for(int i=0; i < size; i++)
      buf[i] = makeChar(handles[i].getInstruction().getOpcode());

    il_string = new String(buf);
  }


  private static final String mapName(String pattern) {
    String result = (String)map.get(pattern);

    if(result != null)
      return result;

    for(short i=0; i < NO_OPCODES; i++)
      if(pattern.equals(Constants.OPCODE_NAMES[i]))
        return "" + makeChar(i);

    throw new RuntimeException("Instruction unknown: " + pattern);
  }


  private static final String compilePattern(String pattern) {
    String       lower      = pattern.toLowerCase();
    StringBuffer buf        = new StringBuffer();
    int          size       = pattern.length();

    for(int i=0; i < size; i++) {
      char ch = lower.charAt(i);

      if(Character.isLetterOrDigit(ch)) {
        StringBuffer name = new StringBuffer();

        while((Character.isLetterOrDigit(ch) || ch == '_') && i < size) {
          name.append(ch);

          if(++i < size)
            ch = lower.charAt(i);
          else
            break;
        }

        i--;

        buf.append(mapName(name.toString()));
      } else if(!Character.isWhitespace(ch))
        buf.append(ch);
    }

    return buf.toString();
  }


  private InstructionHandle[] getMatch(int matched_from, int match_length) {
    InstructionHandle[] match = new InstructionHandle[match_length];
    System.arraycopy(handles, matched_from, match, 0, match_length);

    return match;
  }


  public final Iterator search(String pattern, InstructionHandle from,
                               CodeConstraint constraint)
  {
    String search = compilePattern(pattern);
    int  start    = -1;

    for(int i=0; i < handles.length; i++) {
      if(handles[i] == from) {
        start = i; break;
      }
    }

    if(start == -1)
      throw new ClassGenException("Instruction handle " + from +
                                  " not found in instruction list.");
    try {
      RE regex = new RE(search);
      ArrayList matches = new ArrayList();

      while(start < il_string.length() && regex.match(il_string, start)) {
        int startExpr = regex.getParenStart(0);
        int endExpr   = regex.getParenEnd(0);
        int lenExpr   = regex.getParenLength(0);

        InstructionHandle[] match = getMatch(startExpr, lenExpr);

        if((constraint == null) || constraint.checkCode(match))
          matches.add(match);
        start = endExpr;
      }

      return matches.iterator();
    } catch(RESyntaxException e) {
      System.err.println(e);
    }

    return null;
  }


  public final Iterator search(String pattern) {
    return search(pattern, il.getStart(), null);
  }


  public final Iterator search(String pattern, InstructionHandle from) {
    return search(pattern, from, null);
  }


  public final Iterator search(String pattern, CodeConstraint constraint) {
    return search(pattern, il.getStart(), constraint);
  }


  private static final char makeChar(short opcode) {
    return (char)(opcode + OFFSET);
  }


  public final InstructionList getInstructionList() { return il; }


  public interface CodeConstraint {

    public boolean checkCode(InstructionHandle[] match);
  }

  static {
    map.put("arithmeticinstruction", "(irem|lrem|iand|ior|ineg|isub|lneg|fneg|fmul|ldiv|fadd|lxor|frem|idiv|land|ixor|ishr|fsub|lshl|fdiv|iadd|lor|dmul|lsub|ishl|imul|lmul|lushr|dneg|iushr|lshr|ddiv|drem|dadd|ladd|dsub)");
    map.put("invokeinstruction", "(invokevirtual|invokeinterface|invokestatic|invokespecial)");
    map.put("arrayinstruction", "(baload|aastore|saload|caload|fastore|lastore|iaload|castore|iastore|aaload|bastore|sastore|faload|laload|daload|dastore)");
    map.put("gotoinstruction", "(goto|goto_w)");
    map.put("conversioninstruction", "(d2l|l2d|i2s|d2i|l2i|i2b|l2f|d2f|f2i|i2d|i2l|f2d|i2c|f2l|i2f)");
    map.put("localvariableinstruction", "(fstore|iinc|lload|dstore|dload|iload|aload|astore|istore|fload|lstore)");
    map.put("loadinstruction", "(fload|dload|lload|iload|aload)");
    map.put("fieldinstruction", "(getfield|putstatic|getstatic|putfield)");
    map.put("cpinstruction", "(ldc2_w|invokeinterface|multianewarray|putstatic|instanceof|getstatic|checkcast|getfield|invokespecial|ldc_w|invokestatic|invokevirtual|putfield|ldc|new|anewarray)");
    map.put("stackinstruction", "(dup2|swap|dup2_x2|pop|pop2|dup|dup2_x1|dup_x2|dup_x1)");
    map.put("branchinstruction", "(ifle|if_acmpne|if_icmpeq|if_acmpeq|ifnonnull|goto_w|iflt|ifnull|if_icmpne|tableswitch|if_icmple|ifeq|if_icmplt|jsr_w|if_icmpgt|ifgt|jsr|goto|ifne|ifge|lookupswitch|if_icmpge)");
    map.put("returninstruction", "(lreturn|ireturn|freturn|dreturn|areturn|return)");
    map.put("storeinstruction", "(istore|fstore|dstore|astore|lstore)");
    map.put("select", "(tableswitch|lookupswitch)");
    map.put("ifinstruction", "(ifeq|ifgt|if_icmpne|if_icmpeq|ifge|ifnull|ifne|if_icmple|if_icmpge|if_acmpeq|if_icmplt|if_acmpne|ifnonnull|iflt|if_icmpgt|ifle)");
    map.put("jsrinstruction", "(jsr|jsr_w)");
    map.put("variablelengthinstruction", "(tableswitch|jsr|goto|lookupswitch)");
    map.put("unconditionalbranch", "(goto|jsr|jsr_w|athrow|goto_w)");
    map.put("constantpushinstruction", "(dconst|bipush|sipush|fconst|iconst|lconst)");
    map.put("typedinstruction", "(imul|lsub|aload|fload|lor|new|aaload|fcmpg|iand|iaload|lrem|idiv|d2l|isub|dcmpg|dastore|ret|f2d|f2i|drem|iinc|i2c|checkcast|frem|lreturn|astore|lushr|daload|dneg|fastore|istore|lshl|ldiv|lstore|areturn|ishr|ldc_w|invokeinterface|aastore|lxor|ishl|l2d|i2f|return|faload|sipush|iushr|caload|instanceof|invokespecial|putfield|fmul|ireturn|laload|d2f|lneg|ixor|i2l|fdiv|lastore|multianewarray|i2b|getstatic|i2d|putstatic|fcmpl|saload|ladd|irem|dload|jsr_w|dconst|dcmpl|fsub|freturn|ldc|aconst_null|castore|lmul|ldc2_w|dadd|iconst|f2l|ddiv|dstore|land|jsr|anewarray|dmul|bipush|dsub|sastore|d2i|i2s|lshr|iadd|l2i|lload|bastore|fstore|fneg|iload|fadd|baload|fconst|ior|ineg|dreturn|l2f|lconst|getfield|invokevirtual|invokestatic|iastore)");
    map.put("popinstruction", "(fstore|dstore|pop|pop2|astore|putstatic|istore|lstore)");
    map.put("allocationinstruction", "(multianewarray|new|anewarray|newarray)");
    map.put("indexedinstruction", "(lload|lstore|fload|ldc2_w|invokeinterface|multianewarray|astore|dload|putstatic|instanceof|getstatic|checkcast|getfield|invokespecial|dstore|istore|iinc|ldc_w|ret|fstore|invokestatic|iload|putfield|invokevirtual|ldc|new|aload|anewarray)");
    map.put("pushinstruction", "(dup|lload|dup2|bipush|fload|ldc2_w|sipush|lconst|fconst|dload|getstatic|ldc_w|aconst_null|dconst|iload|ldc|iconst|aload)");
    map.put("stackproducer", "(imul|lsub|aload|fload|lor|new|aaload|fcmpg|iand|iaload|lrem|idiv|d2l|isub|dcmpg|dup|f2d|f2i|drem|i2c|checkcast|frem|lushr|daload|dneg|lshl|ldiv|ishr|ldc_w|invokeinterface|lxor|ishl|l2d|i2f|faload|sipush|iushr|caload|instanceof|invokespecial|fmul|laload|d2f|lneg|ixor|i2l|fdiv|getstatic|i2b|swap|i2d|dup2|fcmpl|saload|ladd|irem|dload|jsr_w|dconst|dcmpl|fsub|ldc|arraylength|aconst_null|tableswitch|lmul|ldc2_w|iconst|dadd|f2l|ddiv|land|jsr|anewarray|dmul|bipush|dsub|d2i|newarray|i2s|lshr|iadd|lload|l2i|fneg|iload|fadd|baload|fconst|lookupswitch|ior|ineg|lconst|l2f|getfield|invokevirtual|invokestatic)");
    map.put("stackconsumer", "(imul|lsub|lor|iflt|fcmpg|if_icmpgt|iand|ifeq|if_icmplt|lrem|ifnonnull|idiv|d2l|isub|dcmpg|dastore|if_icmpeq|f2d|f2i|drem|i2c|checkcast|frem|lreturn|astore|lushr|pop2|monitorexit|dneg|fastore|istore|lshl|ldiv|lstore|areturn|if_icmpge|ishr|monitorenter|invokeinterface|aastore|lxor|ishl|l2d|i2f|return|iushr|instanceof|invokespecial|fmul|ireturn|d2f|lneg|ixor|pop|i2l|ifnull|fdiv|lastore|i2b|if_acmpeq|ifge|swap|i2d|putstatic|fcmpl|ladd|irem|dcmpl|fsub|freturn|ifgt|castore|lmul|dadd|f2l|ddiv|dstore|land|if_icmpne|if_acmpne|dmul|dsub|sastore|ifle|d2i|i2s|lshr|iadd|l2i|bastore|fstore|fneg|fadd|ior|ineg|ifne|dreturn|l2f|if_icmple|getfield|invokevirtual|invokestatic|iastore)");
    map.put("exceptionthrower", "(irem|lrem|laload|putstatic|baload|dastore|areturn|getstatic|ldiv|anewarray|iastore|castore|idiv|saload|lastore|fastore|putfield|lreturn|caload|getfield|return|aastore|freturn|newarray|instanceof|multianewarray|athrow|faload|iaload|aaload|dreturn|monitorenter|checkcast|bastore|arraylength|new|invokevirtual|sastore|ldc_w|ireturn|invokespecial|monitorexit|invokeinterface|ldc|invokestatic|daload)");
    map.put("loadclass", "(multianewarray|invokeinterface|instanceof|invokespecial|putfield|checkcast|putstatic|invokevirtual|new|getstatic|invokestatic|getfield|anewarray)");
    map.put("instructiontargeter", "(ifle|if_acmpne|if_icmpeq|if_acmpeq|ifnonnull|goto_w|iflt|ifnull|if_icmpne|tableswitch|if_icmple|ifeq|if_icmplt|jsr_w|if_icmpgt|ifgt|jsr|goto|ifne|ifge|lookupswitch|if_icmpge)");

    map.put("if_icmp", "(if_icmpne|if_icmpeq|if_icmple|if_icmpge|if_icmplt|if_icmpgt)");
    map.put("if_acmp", "(if_acmpeq|if_acmpne)");
    map.put("if", "(ifeq|ifne|iflt|ifge|ifgt|ifle)");

    map.put("iconst", precompile(Constants.ICONST_0, Constants.ICONST_5, Constants.ICONST_M1));
    map.put("lconst", new String(new char[] { '(', makeChar(Constants.LCONST_0), '|',
                                              makeChar(Constants.LCONST_1), ')' }));
    map.put("dconst", new String(new char[] { '(', makeChar(Constants.DCONST_0), '|',
                                              makeChar(Constants.DCONST_1), ')' }));
    map.put("fconst", new String(new char[] { '(', makeChar(Constants.FCONST_0), '|',
                                              makeChar(Constants.FCONST_1), ')' }));

    map.put("iload", precompile(Constants.ILOAD_0, Constants.ILOAD_3, Constants.ILOAD));
    map.put("dload", precompile(Constants.DLOAD_0, Constants.DLOAD_3, Constants.DLOAD));
    map.put("fload", precompile(Constants.FLOAD_0, Constants.FLOAD_3, Constants.FLOAD));
    map.put("aload", precompile(Constants.ALOAD_0, Constants.ALOAD_3, Constants.ALOAD));

    map.put("istore", precompile(Constants.ISTORE_0, Constants.ISTORE_3, Constants.ISTORE));
    map.put("dstore", precompile(Constants.DSTORE_0, Constants.DSTORE_3, Constants.DSTORE));
    map.put("fstore", precompile(Constants.FSTORE_0, Constants.FSTORE_3, Constants.FSTORE));
    map.put("astore", precompile(Constants.ASTORE_0, Constants.ASTORE_3, Constants.ASTORE));

    for(Iterator i = map.keySet().iterator(); i.hasNext(); ) {
      String key   = (String)i.next();
      String value = (String)map.get(key);

      char ch = value.charAt(1); if(ch < OFFSET) {
        map.put(key, compilePattern(value)); }
    }

    StringBuffer buf = new StringBuffer("(");

    for(short i=0; i < NO_OPCODES; i++) {
      if(Constants.NO_OF_OPERANDS[i] != Constants.UNDEFINED) { buf.append(makeChar(i));

        if(i < NO_OPCODES - 1)
          buf.append('|');
      }
    }
    buf.append(')');

    map.put("instruction", buf.toString());
  }

  private static String precompile(short from, short to, short extra) {
    StringBuffer buf = new StringBuffer("(");

    for(short i=from; i <= to; i++) {
      buf.append(makeChar(i));
      buf.append('|');
    }

    buf.append(makeChar(extra));
    buf.append(")");
    return buf.toString();
  }


  private static final String pattern2string(String pattern) {
    return pattern2string(pattern, true);
  }

  private static final String pattern2string(String pattern, boolean make_string) {
    StringBuffer buf = new StringBuffer();

    for(int i=0; i < pattern.length(); i++) {
      char ch = pattern.charAt(i);

      if(ch >= OFFSET) {
        if(make_string)
          buf.append(Constants.OPCODE_NAMES[ch - OFFSET]);
        else
          buf.append((int)(ch - OFFSET));
      } else
        buf.append(ch);
    }

    return buf.toString();
  }
}
