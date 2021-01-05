


package com.sun.jmx.snmp.IPAcl;

import java.io.*;

@SuppressWarnings("unchecked")  class Parserimplements ParserTreeConstants, ParserConstants {
  protected JJTParserState jjtree = new JJTParserState();

final public JDMSecurityDefs SecurityDefs() throws ParseException {

  JDMSecurityDefs jjtn000 = new JDMSecurityDefs(JJTSECURITYDEFS);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ACL:
        AclBlock();
        break;
      default:
        jj_la1[0] = jj_gen;
        ;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TRAP:
        TrapBlock();
        break;
      default:
        jj_la1[1] = jj_gen;
        ;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INFORM:
        InformBlock();
        break;
      default:
        jj_la1[2] = jj_gen;
        ;
      }
      jj_consume_token(0);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    {if (true) return jjtn000;}
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
    throw new Error("Missing return statement in function");
  }

  final public void AclBlock() throws ParseException {

  JDMAclBlock jjtn000 = new JDMAclBlock(JJTACLBLOCK);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(ACL);
      jj_consume_token(ASSIGN);
      jj_consume_token(LBRACE);
      label_1:
      while (true) {
        AclItem();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LBRACE:
          ;
          break;
        default:
          jj_la1[3] = jj_gen;
          break label_1;
        }
      }
      jj_consume_token(RBRACE);
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void AclItem() throws ParseException {

  JDMAclItem jjtn000 = new JDMAclItem(JJTACLITEM);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(LBRACE);
      jjtn000.com = Communities();
      jjtn000.access = Access();
      Managers();
      jj_consume_token(RBRACE);
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public JDMCommunities Communities() throws ParseException {

  JDMCommunities jjtn000 = new JDMCommunities(JJTCOMMUNITIES);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(COMMUNITIES);
      jj_consume_token(ASSIGN);
      Community();
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[4] = jj_gen;
          break label_2;
        }
        jj_consume_token(COMMA);
        Community();
      }
  jjtree.closeNodeScope(jjtn000, true);
  jjtc000 = false;
 {if (true) return jjtn000;}
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
    throw new Error("Missing return statement in function");
  }

  final public void Community() throws ParseException {

  JDMCommunity jjtn000 = new JDMCommunity(JJTCOMMUNITY);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(IDENTIFIER);
                 jjtree.closeNodeScope(jjtn000, true);
                 jjtc000 = false;
                jjtn000.communityString= t.image;
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public JDMAccess Access() throws ParseException {

  JDMAccess jjtn000 = new JDMAccess(JJTACCESS);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(ACCESS);
      jj_consume_token(ASSIGN);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case RO:
        jj_consume_token(RO);
                     jjtn000.access= RO;
        break;
      case RW:
        jj_consume_token(RW);
                     jjtn000.access= RW;
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
  jjtree.closeNodeScope(jjtn000, true);
  jjtc000 = false;
 {if (true) return jjtn000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
    throw new Error("Missing return statement in function");
  }

  final public void Managers() throws ParseException {

  JDMManagers jjtn000 = new JDMManagers(JJTMANAGERS);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(MANAGERS);
      jj_consume_token(ASSIGN);
      Host();
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[6] = jj_gen;
          break label_3;
        }
        jj_consume_token(COMMA);
        Host();
      }
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void Host() throws ParseException {

  JDMHost jjtn000 = new JDMHost(JJTHOST);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IDENTIFIER:
        HostName();
        break;
      default:
        jj_la1[7] = jj_gen;
        if (jj_2_1(2147483647)) {
          NetMask();
        } else if (jj_2_2(2147483647)) {
          NetMaskV6();
        } else if (jj_2_3(2147483647)) {
          IpAddress();
        } else {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case V6_ADDRESS:
            IpV6Address();
            break;
          case INTEGER_LITERAL:
            IpMask();
            break;
          default:
            jj_la1[8] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
        }
      }
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void HostName() throws ParseException {

  JDMHostName jjtn000 = new JDMHostName(JJTHOSTNAME);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(IDENTIFIER);
                   jjtn000.name.append(t.image);
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DOT:
          ;
          break;
        default:
          jj_la1[9] = jj_gen;
          break label_4;
        }
        jj_consume_token(DOT);
        t = jj_consume_token(IDENTIFIER);
   jjtn000.name.append( "." + t.image);
      }
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void IpAddress() throws ParseException {

JDMIpAddress jjtn000 = new JDMIpAddress(JJTIPADDRESS);
boolean jjtc000 = true;
jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(INTEGER_LITERAL);
   jjtn000.address.append(t.image);
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DOT:
          ;
          break;
        default:
          jj_la1[10] = jj_gen;
          break label_5;
        }
        jj_consume_token(DOT);
        t = jj_consume_token(INTEGER_LITERAL);
   jjtn000.address.append( "." + t.image);
      }
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void IpV6Address() throws ParseException {

JDMIpV6Address jjtn000 = new JDMIpV6Address(JJTIPV6ADDRESS);
boolean jjtc000 = true;
jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(V6_ADDRESS);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
   jjtn000.address.append(t.image);
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void IpMask() throws ParseException {

JDMIpMask jjtn000 = new JDMIpMask(JJTIPMASK);
boolean jjtc000 = true;
jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(INTEGER_LITERAL);
   jjtn000.address.append(t.image);
      label_6:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case MARK:
          ;
          break;
        default:
          jj_la1[11] = jj_gen;
          break label_6;
        }
        jj_consume_token(MARK);
        t = jj_consume_token(INTEGER_LITERAL);
   jjtn000.address.append( "." + t.image);
      }
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void NetMask() throws ParseException {

JDMNetMask jjtn000 = new JDMNetMask(JJTNETMASK);
boolean jjtc000 = true;
jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(INTEGER_LITERAL);
   jjtn000.address.append(t.image);
      label_7:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DOT:
          ;
          break;
        default:
          jj_la1[12] = jj_gen;
          break label_7;
        }
        jj_consume_token(DOT);
        t = jj_consume_token(INTEGER_LITERAL);
   jjtn000.address.append( "." + t.image);
      }
      jj_consume_token(MASK);
      t = jj_consume_token(INTEGER_LITERAL);
                              jjtree.closeNodeScope(jjtn000, true);
                              jjtc000 = false;
                             jjtn000.mask = t.image;
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void NetMaskV6() throws ParseException {

JDMNetMaskV6 jjtn000 = new JDMNetMaskV6(JJTNETMASKV6);
boolean jjtc000 = true;
jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(V6_ADDRESS);
   jjtn000.address.append(t.image);
      jj_consume_token(MASK);
      t = jj_consume_token(INTEGER_LITERAL);
                           jjtree.closeNodeScope(jjtn000, true);
                           jjtc000 = false;
                          jjtn000.mask = t.image;
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void TrapBlock() throws ParseException {

  JDMTrapBlock jjtn000 = new JDMTrapBlock(JJTTRAPBLOCK);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(TRAP);
      jj_consume_token(ASSIGN);
      jj_consume_token(LBRACE);
      label_8:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LBRACE:
          ;
          break;
        default:
          jj_la1[13] = jj_gen;
          break label_8;
        }
        TrapItem();
      }
      jj_consume_token(RBRACE);
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void TrapItem() throws ParseException {

  JDMTrapItem jjtn000 = new JDMTrapItem(JJTTRAPITEM);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(LBRACE);
      jjtn000.comm = TrapCommunity();
      TrapInterestedHost();
      label_9:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LBRACE:
          ;
          break;
        default:
          jj_la1[14] = jj_gen;
          break label_9;
        }
        Enterprise();
      }
      jj_consume_token(RBRACE);
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public JDMTrapCommunity TrapCommunity() throws ParseException {

  JDMTrapCommunity jjtn000 = new JDMTrapCommunity(JJTTRAPCOMMUNITY);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t;
    try {
      jj_consume_token(TRAPCOMMUNITY);
      jj_consume_token(ASSIGN);
      t = jj_consume_token(IDENTIFIER);
                                      jjtree.closeNodeScope(jjtn000, true);
                                      jjtc000 = false;
                                      jjtn000.community= t.image; {if (true) return jjtn000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
    throw new Error("Missing return statement in function");
  }

  final public void TrapInterestedHost() throws ParseException {

  JDMTrapInterestedHost jjtn000 = new JDMTrapInterestedHost(JJTTRAPINTERESTEDHOST);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(HOSTS);
      jj_consume_token(ASSIGN);
      HostTrap();
      label_10:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[15] = jj_gen;
          break label_10;
        }
        jj_consume_token(COMMA);
        HostTrap();
      }
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void HostTrap() throws ParseException {

  JDMHostTrap jjtn000 = new JDMHostTrap(JJTHOSTTRAP);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IDENTIFIER:
        HostName();
        break;
      case INTEGER_LITERAL:
        IpAddress();
        break;
      case V6_ADDRESS:
        IpV6Address();
        break;
      default:
        jj_la1[16] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void Enterprise() throws ParseException {

  JDMEnterprise jjtn000 = new JDMEnterprise(JJTENTERPRISE);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t;
    try {
      jj_consume_token(LBRACE);
      jj_consume_token(ENTERPRISE);
      jj_consume_token(ASSIGN);
      t = jj_consume_token(CSTRING);
                               jjtn000.enterprise= t.image;
      jj_consume_token(TRAPNUM);
      jj_consume_token(ASSIGN);
      TrapNum();
      label_11:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[17] = jj_gen;
          break label_11;
        }
        jj_consume_token(COMMA);
        TrapNum();
      }
      jj_consume_token(RBRACE);
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void TrapNum() throws ParseException {

  JDMTrapNum jjtn000 = new JDMTrapNum(JJTTRAPNUM);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(INTEGER_LITERAL);
                       jjtn000.low= Integer.parseInt(t.image);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case RANGE:
        jj_consume_token(RANGE);
        t = jj_consume_token(INTEGER_LITERAL);
                           jjtn000.high= Integer.parseInt(t.image);
        break;
      default:
        jj_la1[18] = jj_gen;
        ;
      }
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void InformBlock() throws ParseException {

  JDMInformBlock jjtn000 = new JDMInformBlock(JJTINFORMBLOCK);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(INFORM);
      jj_consume_token(ASSIGN);
      jj_consume_token(LBRACE);
      label_12:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LBRACE:
          ;
          break;
        default:
          jj_la1[19] = jj_gen;
          break label_12;
        }
        InformItem();
      }
      jj_consume_token(RBRACE);
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void InformItem() throws ParseException {

  JDMInformItem jjtn000 = new JDMInformItem(JJTINFORMITEM);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(LBRACE);
      jjtn000.comm = InformCommunity();
      InformInterestedHost();
      jj_consume_token(RBRACE);
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public JDMInformCommunity InformCommunity() throws ParseException {

  JDMInformCommunity jjtn000 = new JDMInformCommunity(JJTINFORMCOMMUNITY);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t;
    try {
      jj_consume_token(INFORMCOMMUNITY);
      jj_consume_token(ASSIGN);
      t = jj_consume_token(IDENTIFIER);
                                        jjtree.closeNodeScope(jjtn000, true);
                                        jjtc000 = false;
                                        jjtn000.community= t.image; {if (true) return jjtn000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
    throw new Error("Missing return statement in function");
  }

  final public void InformInterestedHost() throws ParseException {

  JDMInformInterestedHost jjtn000 = new JDMInformInterestedHost(JJTINFORMINTERESTEDHOST);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(HOSTS);
      jj_consume_token(ASSIGN);
      HostInform();
      label_13:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[20] = jj_gen;
          break label_13;
        }
        jj_consume_token(COMMA);
        HostInform();
      }
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final public void HostInform() throws ParseException {

  JDMHostInform jjtn000 = new JDMHostInform(JJTHOSTINFORM);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IDENTIFIER:
        HostName();
        break;
      case INTEGER_LITERAL:
        IpAddress();
        break;
      case V6_ADDRESS:
        IpV6Address();
        break;
      default:
        jj_la1[21] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
  if (jjtc000) {
    jjtree.clearNodeScope(jjtn000);
    jjtc000 = false;
  } else {
    jjtree.popNode();
  }
  if (jjte000 instanceof RuntimeException) {
    {if (true) throw (RuntimeException)jjte000;}
  }
  if (jjte000 instanceof ParseException) {
    {if (true) throw (ParseException)jjte000;}
  }
  {if (true) throw (Error)jjte000;}
    } finally {
  if (jjtc000) {
    jjtree.closeNodeScope(jjtn000, true);
  }
    }
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    boolean retval = !jj_3_1();
    jj_save(0, xla);
    return retval;
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    boolean retval = !jj_3_2();
    jj_save(1, xla);
    return retval;
  }

  final private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    boolean retval = !jj_3_3();
    jj_save(2, xla);
    return retval;
  }

  final private boolean jj_3_3() {
    if (jj_scan_token(INTEGER_LITERAL)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(DOT)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_2() {
    if (jj_scan_token(V6_ADDRESS)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(MASK)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(INTEGER_LITERAL)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_scan_token(INTEGER_LITERAL)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_14()) { jj_scanpos = xsp; break; }
      if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    }
    if (jj_scan_token(MASK)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(INTEGER_LITERAL)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_14() {
    if (jj_scan_token(DOT)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(INTEGER_LITERAL)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  public ParserTokenManager token_source;
  ASCII_CharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[22];
  final private int[] jj_la1_0 = {0x100,0x80000,0x100000,0x2000,0x0,0x60000,0x0,0x80000000,0x11000000,0x0,0x0,0x0,0x0,0x2000,0x2000,0x0,0x91000000,0x0,0x8000,0x2000,0x0,0x91000000,};
  final private int[] jj_la1_1 = {0x0,0x0,0x0,0x0,0x10,0x0,0x10,0x0,0x0,0x20,0x20,0x40,0x20,0x0,0x0,0x10,0x0,0x10,0x0,0x0,0x10,0x0,};
  final private JJCalls[] jj_2_rtns = new JJCalls[3];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public Parser(java.io.InputStream stream) {
    jj_input_stream = new ASCII_CharStream(stream, 1, 1);
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public Parser(java.io.Reader stream) {
    jj_input_stream = new ASCII_CharStream(stream, 1, 1);
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public Parser(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    return (jj_scanpos.kind != kind);
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector<int[]> jj_expentries = new java.util.Vector<>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration<int[]> enumv = jj_expentries.elements(); enumv.hasMoreElements();) {
        int[] oldentry = enumv.nextElement();
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  final public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[40];
    for (int i = 0; i < 40; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 22; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 40; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 3; i++) {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
          }
        }
        p = p.next;
      } while (p != null);
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
