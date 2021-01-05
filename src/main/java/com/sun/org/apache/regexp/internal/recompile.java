


package com.sun.org.apache.regexp.internal;

import com.sun.org.apache.regexp.internal.RECompiler;
import com.sun.org.apache.regexp.internal.RESyntaxException;


public class recompile
{

    static public void main(String[] arg)
    {
        RECompiler r = new RECompiler();

        if (arg.length <= 0 || arg.length % 2 != 0)
        {
            System.out.println("Usage: recompile <patternname> <pattern>");
            System.exit(0);
        }

        for (int i = 0; i < arg.length; i += 2)
        {
            try
            {
                String name         = arg[i];
                String pattern      = arg[i+1];
                String instructions = name + "PatternInstructions";

                System.out.print("\n    + "    private static char[] " + instructions + " = \n    {");

                REProgram program = r.compile(pattern);

                int numColumns = 7;

                char[] p = program.getInstructions();
                for (int j = 0; j < p.length; j++)
                {
                    if ((j % numColumns) == 0)
                    {
                        System.out.print("\n        ");
                    }

                    String hex = Integer.toHexString(p[j]);
                    while (hex.length() < 4)
                    {
                        hex = "0" + hex;
                    }
                    System.out.print("0x" + hex + ", ");
                }

                System.out.println("\n    };");
                System.out.println("\n    private static RE " + name + "Pattern = new RE(new REProgram(" + instructions + "));");
            }
            catch (RESyntaxException e)
            {
                System.out.println("Syntax error in expression \"" + arg[i] + "\": " + e.toString());
            }
            catch (Exception e)
            {
                System.out.println("Unexpected exception: " + e.toString());
            }
            catch (Error e)
            {
                System.out.println("Internal error: " + e.toString());
            }
        }
    }
}
