


package com.sun.org.apache.regexp.internal;

import java.io.Serializable;


public class REProgram implements Serializable
{
    static final int OPT_HASBACKREFS = 1;

    char[] instruction;         int lenInstruction;         char[] prefix;              int flags;                  int maxParens = -1;


    public REProgram(char[] instruction)
    {
        this(instruction, instruction.length);
    }


    public REProgram(int parens, char[] instruction)
    {
        this(instruction, instruction.length);
        this.maxParens = parens;
    }


    public REProgram(char[] instruction, int lenInstruction)
    {
        setInstructions(instruction, lenInstruction);
    }


    public char[] getInstructions()
    {
        if (lenInstruction != 0)
        {
            char[] ret = new char[lenInstruction];
            System.arraycopy(instruction, 0, ret, 0, lenInstruction);
            return ret;
        }
        return null;
    }


    public void setInstructions(char[] instruction, int lenInstruction)
    {
        this.instruction = instruction;
        this.lenInstruction = lenInstruction;

        flags = 0;
        prefix = null;

        if (instruction != null && lenInstruction != 0)
        {
            if (lenInstruction >= RE.nodeSize && instruction[0 + RE.offsetOpcode] == RE.OP_BRANCH)
            {
                int next = instruction[0 + RE.offsetNext];
                if (instruction[next + RE.offsetOpcode] == RE.OP_END)
                {
                    if (lenInstruction >= (RE.nodeSize * 2) && instruction[RE.nodeSize + RE.offsetOpcode] == RE.OP_ATOM)
                    {
                        int lenAtom = instruction[RE.nodeSize + RE.offsetOpdata];
                        prefix = new char[lenAtom];
                        System.arraycopy(instruction, RE.nodeSize * 2, prefix, 0, lenAtom);
                    }
                }
            }

            BackrefScanLoop:

            for (int i = 0; i < lenInstruction; i += RE.nodeSize)
            {
                switch (instruction[i + RE.offsetOpcode])
                {
                    case RE.OP_ANYOF:
                        i += (instruction[i + RE.offsetOpdata] * 2);
                        break;

                    case RE.OP_ATOM:
                        i += instruction[i + RE.offsetOpdata];
                        break;

                    case RE.OP_BACKREF:
                        flags |= OPT_HASBACKREFS;
                        break BackrefScanLoop;
                }
            }
        }
    }
}
