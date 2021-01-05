


package com.sun.org.apache.regexp.internal;

import java.io.PrintWriter;
import java.util.Hashtable;


public class REDebugCompiler extends RECompiler
{

    static Hashtable hashOpcode = new Hashtable();
    static
    {
        hashOpcode.put(new Integer(RE.OP_RELUCTANTSTAR),    "OP_RELUCTANTSTAR");
        hashOpcode.put(new Integer(RE.OP_RELUCTANTPLUS),    "OP_RELUCTANTPLUS");
        hashOpcode.put(new Integer(RE.OP_RELUCTANTMAYBE),   "OP_RELUCTANTMAYBE");
        hashOpcode.put(new Integer(RE.OP_END),              "OP_END");
        hashOpcode.put(new Integer(RE.OP_BOL),              "OP_BOL");
        hashOpcode.put(new Integer(RE.OP_EOL),              "OP_EOL");
        hashOpcode.put(new Integer(RE.OP_ANY),              "OP_ANY");
        hashOpcode.put(new Integer(RE.OP_ANYOF),            "OP_ANYOF");
        hashOpcode.put(new Integer(RE.OP_BRANCH),           "OP_BRANCH");
        hashOpcode.put(new Integer(RE.OP_ATOM),             "OP_ATOM");
        hashOpcode.put(new Integer(RE.OP_STAR),             "OP_STAR");
        hashOpcode.put(new Integer(RE.OP_PLUS),             "OP_PLUS");
        hashOpcode.put(new Integer(RE.OP_MAYBE),            "OP_MAYBE");
        hashOpcode.put(new Integer(RE.OP_NOTHING),          "OP_NOTHING");
        hashOpcode.put(new Integer(RE.OP_GOTO),             "OP_GOTO");
        hashOpcode.put(new Integer(RE.OP_ESCAPE),           "OP_ESCAPE");
        hashOpcode.put(new Integer(RE.OP_OPEN),             "OP_OPEN");
        hashOpcode.put(new Integer(RE.OP_CLOSE),            "OP_CLOSE");
        hashOpcode.put(new Integer(RE.OP_BACKREF),          "OP_BACKREF");
        hashOpcode.put(new Integer(RE.OP_POSIXCLASS),       "OP_POSIXCLASS");
        hashOpcode.put(new Integer(RE.OP_OPEN_CLUSTER),      "OP_OPEN_CLUSTER");
        hashOpcode.put(new Integer(RE.OP_CLOSE_CLUSTER),      "OP_CLOSE_CLUSTER");
    }


    String opcodeToString(char opcode)
    {
        String ret =(String)hashOpcode.get(new Integer(opcode));

        if (ret == null)
        {
            ret = "OP_????";
        }
        return ret;
    }


    String charToString(char c)
    {
        if (c < ' ' || c > 127)
        {
            return "\\" + (int)c;
        }

        return String.valueOf(c);
    }


    String nodeToString(int node)
    {
        char opcode =      instruction[node + RE.offsetOpcode];
        int opdata  = (int)instruction[node + RE.offsetOpdata];

        return opcodeToString(opcode) + ", opdata = " + opdata;
    }








    public void dumpProgram(PrintWriter p)
    {
        for (int i = 0; i < lenInstruction; )
        {
            char opcode =        instruction[i + RE.offsetOpcode];
            char opdata =        instruction[i + RE.offsetOpdata];
            short next  = (short)instruction[i + RE.offsetNext];

            p.print(i + ". " + nodeToString(i) + ", next = ");

            if (next == 0)
            {
                p.print("none");
            }
            else
            {
                p.print(i + next);
            }

            i += RE.nodeSize;

            if (opcode == RE.OP_ANYOF)
            {
                p.print(", [");

                int rangeCount = opdata;
                for (int r = 0; r < rangeCount; r++)
                {
                    char charFirst = instruction[i++];
                    char charLast  = instruction[i++];

                    if (charFirst == charLast)
                    {
                        p.print(charToString(charFirst));
                    }
                    else
                    {
                        p.print(charToString(charFirst) + "-" + charToString(charLast));
                    }
                }

                p.print("]");
            }

            if (opcode == RE.OP_ATOM)
            {
                p.print(", \"");

                for (int len = opdata; len-- != 0; )
                {
                    p.print(charToString(instruction[i++]));
                }

                p.print("\"");
            }

            p.println("");
        }
    }
}
