
package com.sun.jmx.snmp.IPAcl;

interface ParserConstants {

  int EOF = 0;
  int ACCESS = 7;
  int ACL = 8;
  int ASSIGN = 9;
  int COMMUNITIES = 10;
  int ENTERPRISE = 11;
  int HOSTS = 12;
  int LBRACE = 13;
  int MANAGERS = 14;
  int RANGE = 15;
  int RBRACE = 16;
  int RO = 17;
  int RW = 18;
  int TRAP = 19;
  int INFORM = 20;
  int TRAPCOMMUNITY = 21;
  int INFORMCOMMUNITY = 22;
  int TRAPNUM = 23;
  int INTEGER_LITERAL = 24;
  int DECIMAL_LITERAL = 25;
  int HEX_LITERAL = 26;
  int OCTAL_LITERAL = 27;
  int V6_ADDRESS = 28;
  int H = 29;
  int D = 30;
  int IDENTIFIER = 31;
  int LETTER = 32;
  int SEPARATOR = 33;
  int DIGIT = 34;
  int CSTRING = 35;
  int COMMA = 36;
  int DOT = 37;
  int MARK = 38;
  int MASK = 39;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<token of kind 5>",
    "<token of kind 6>",
    "\"access\"",
    "\"acl\"",
    "\"=\"",
    "\"communities\"",
    "\"enterprise\"",
    "\"hosts\"",
    "\"{\"",
    "\"managers\"",
    "\"-\"",
    "\"}\"",
    "\"read-only\"",
    "\"read-write\"",
    "\"trap\"",
    "\"inform\"",
    "\"trap-community\"",
    "\"inform-community\"",
    "\"trap-num\"",
    "<INTEGER_LITERAL>",
    "<DECIMAL_LITERAL>",
    "<HEX_LITERAL>",
    "<OCTAL_LITERAL>",
    "<V6_ADDRESS>",
    "<H>",
    "<D>",
    "<IDENTIFIER>",
    "<LETTER>",
    "<SEPARATOR>",
    "<DIGIT>",
    "<CSTRING>",
    "\",\"",
    "\".\"",
    "\"!\"",
    "\"/\"",
  };

}
