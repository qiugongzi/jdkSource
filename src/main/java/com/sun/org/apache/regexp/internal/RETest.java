


package com.sun.org.apache.regexp.internal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.IOException;


public class RETest
{
    static final boolean showSuccesses = false;

    static final String NEW_LINE = System.getProperty( "line.separator" );

    REDebugCompiler compiler = new REDebugCompiler();


    public static void main(String[] args)
    {
        try
        {
            if (!test( args )) {
                System.exit(1);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static boolean test( String[] args ) throws Exception
    {
        RETest test = new RETest();
        if (args.length == 2)
        {
            test.runInteractiveTests(args[1]);
        }
        else if (args.length == 1)
        {
            test.runAutomatedTests(args[0]);
        }
        else
        {
            System.out.println( "Usage: RETest ([-i] [regex]) ([/path/to/testfile.txt])" );
            System.out.println( "By Default will run automated tests from file 'docs/RETest.txt' ..." );
            System.out.println();
            test.runAutomatedTests("docs/RETest.txt");
        }
        return test.failures == 0;
    }


    public RETest()
    {
    }


    void runInteractiveTests(String expr)
    {
        RE r = new RE();
        try
        {
            r.setProgram(compiler.compile(expr));

            say("" + NEW_LINE + "" + expr + "" + NEW_LINE + "");

            PrintWriter writer = new PrintWriter( System.out );
            compiler.dumpProgram( writer );
            writer.flush();

            boolean running = true;
            while ( running )
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("> ");
                System.out.flush();
                String match = br.readLine();

                if ( match != null )
                {
                    if (r.match(match))
                    {
                        say("Match successful.");
                    }
                    else
                    {
                        say("Match failed.");
                    }

                    showParens(r);
                }
                else
                {
                    running = false;
                    System.out.println();
                }
            }
        }
        catch (Exception e)
        {
            say("Error: " + e.toString());
            e.printStackTrace();
        }
    }


    void die(String s)
    {
        say("FATAL ERROR: " + s);
        System.exit(-1);
    }


    void fail(StringBuffer log, String s)
    {
        System.out.print(log.toString());
        fail(s);
    }


    void fail(String s)
    {
        failures++;
        say("" + NEW_LINE + "");
        say("*******************************************************");
        say("*********************  FAILURE!  **********************");
        say("*******************************************************");
        say("" + NEW_LINE + "");
        say(s);
        say("");
        if (compiler != null) {
            PrintWriter writer = new PrintWriter( System.out );
            compiler.dumpProgram( writer );
            writer.flush();
            say("" + NEW_LINE + "");
        }
    }


    void say(String s)
    {
        System.out.println(s);
    }


    void showParens(RE r)
    {
        for (int i = 0; i < r.getParenCount(); i++)
        {
            say("$" + i + " = " + r.getParen(i));
        }
    }


    int testCount = 0;


    int failures = 0;


    void runAutomatedTests(String testDocument) throws Exception
    {
        long ms = System.currentTimeMillis();

        testPrecompiledRE();
        testSplitAndGrep();
        testSubst();
        testOther();

        File testInput = new File(testDocument);
        if (! testInput.exists()) {
            throw new Exception ("Could not find: " + testDocument);
        }

        BufferedReader br = new BufferedReader(new FileReader(testInput));
        try
        {
            while (br.ready())
            {
                RETestCase testcase = getNextTestCase(br);
                if (testcase != null) {
                    testcase.runTest();
                }
            }
        }
        finally
        {
            br.close();
        }

        say(NEW_LINE + NEW_LINE + "Match time = " + (System.currentTimeMillis() - ms) + " ms.");

        if (failures > 0) {
            say("*************** THERE ARE FAILURES! *******************");
        }
        say("Tests complete.  " + testCount + " tests, " + failures + " failure(s).");
    }


    void testOther() throws Exception
    {
        RE r = new RE("(a*)b");
        say("Serialized/deserialized (a*)b");
        ByteArrayOutputStream out = new ByteArrayOutputStream(128);
        new ObjectOutputStream(out).writeObject(r);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        r = (RE)new ObjectInputStream(in).readObject();
        if (!r.match("aaab"))
        {
            fail("Did not match 'aaab' with deserialized RE.");
        } else {
            say("aaaab = true");
            showParens(r);
        }

        out.reset();
        say("Deserialized (a*)b");
        new ObjectOutputStream(out).writeObject(r);
        in = new ByteArrayInputStream(out.toByteArray());
        r = (RE)new ObjectInputStream(in).readObject();
        if (r.getParenCount() != 0)
        {
            fail("Has parens after deserialization.");
        }
        if (!r.match("aaab"))
        {
            fail("Did not match 'aaab' with deserialized RE.");
        } else {
            say("aaaab = true");
            showParens(r);
        }

        r = new RE("abc(\\w*)");
        say("MATCH_CASEINDEPENDENT abc(\\w*)");
        r.setMatchFlags(RE.MATCH_CASEINDEPENDENT);
        say("abc(d*)");
        if (!r.match("abcddd"))
        {
            fail("Did not match 'abcddd'.");
        } else {
            say("abcddd = true");
            showParens(r);
        }

        if (!r.match("aBcDDdd"))
        {
            fail("Did not match 'aBcDDdd'.");
        } else {
            say("aBcDDdd = true");
            showParens(r);
        }

        if (!r.match("ABCDDDDD"))
        {
            fail("Did not match 'ABCDDDDD'.");
        } else {
            say("ABCDDDDD = true");
            showParens(r);
        }

        r = new RE("(A*)b\\1");
        r.setMatchFlags(RE.MATCH_CASEINDEPENDENT);
        if (!r.match("AaAaaaBAAAAAA"))
        {
            fail("Did not match 'AaAaaaBAAAAAA'.");
        } else {
            say("AaAaaaBAAAAAA = true");
            showParens(r);
        }

        r = new RE("[A-Z]*");
        r.setMatchFlags(RE.MATCH_CASEINDEPENDENT);
        if (!r.match("CaBgDe12"))
        {
            fail("Did not match 'CaBgDe12'.");
        } else {
            say("CaBgDe12 = true");
            showParens(r);
        }

        r = new RE("^abc$", RE.MATCH_MULTILINE);
        if (!r.match("\nabc")) {
            fail("\"\\nabc\" doesn't match \"^abc$\"");
        }
        if (!r.match("\rabc")) {
            fail("\"\\rabc\" doesn't match \"^abc$\"");
        }
        if (!r.match("\r\nabc")) {
            fail("\"\\r\\nabc\" doesn't match \"^abc$\"");
        }
        if (!r.match("\u0085abc")) {
            fail("\"\\u0085abc\" doesn't match \"^abc$\"");
        }
        if (!r.match("\u2028abc")) {
            fail("\"\\u2028abc\" doesn't match \"^abc$\"");
        }
        if (!r.match("\u2029abc")) {
            fail("\"\\u2029abc\" doesn't match \"^abc$\"");
        }

        r = new RE("^a.*b$", RE.MATCH_MULTILINE);
        if (r.match("a\nb")) {
            fail("\"a\\nb\" matches \"^a.*b$\"");
        }
        if (r.match("a\rb")) {
            fail("\"a\\rb\" matches \"^a.*b$\"");
        }
        if (r.match("a\r\nb")) {
            fail("\"a\\r\\nb\" matches \"^a.*b$\"");
        }
        if (r.match("a\u0085b")) {
            fail("\"a\\u0085b\" matches \"^a.*b$\"");
        }
        if (r.match("a\u2028b")) {
            fail("\"a\\u2028b\" matches \"^a.*b$\"");
        }
        if (r.match("a\u2029b")) {
            fail("\"a\\u2029b\" matches \"^a.*b$\"");
        }
    }

    private void testPrecompiledRE()
    {
        char[] re1Instructions =
        {
            0x007c, 0x0000, 0x001a, 0x007c, 0x0000, 0x000d, 0x0041,
            0x0001, 0x0004, 0x0061, 0x007c, 0x0000, 0x0003, 0x0047,
            0x0000, 0xfff6, 0x007c, 0x0000, 0x0003, 0x004e, 0x0000,
            0x0003, 0x0041, 0x0001, 0x0004, 0x0062, 0x0045, 0x0000,
            0x0000,
        };

        REProgram re1 = new REProgram(re1Instructions);

        RE r = new RE(re1);
        say("a*b");
        boolean result = r.match("aaab");
        say("aaab = " + result);
        showParens(r);
        if (!result) {
            fail("\"aaab\" doesn't match to precompiled \"a*b\"");
        }

        result = r.match("b");
        say("b = " + result);
        showParens(r);
        if (!result) {
            fail("\"b\" doesn't match to precompiled \"a*b\"");
        }

        result = r.match("c");
        say("c = " + result);
        showParens(r);
        if (result) {
            fail("\"c\" matches to precompiled \"a*b\"");
        }

        result = r.match("ccccaaaaab");
        say("ccccaaaaab = " + result);
        showParens(r);
        if (!result) {
            fail("\"ccccaaaaab\" doesn't match to precompiled \"a*b\"");
        }
    }

    private void testSplitAndGrep()
    {
        String[] expected = {"xxxx", "xxxx", "yyyy", "zzz"};
        RE r = new RE("a*b");
        String[] s = r.split("xxxxaabxxxxbyyyyaaabzzz");
        for (int i = 0; i < expected.length && i < s.length; i++) {
            assertEquals("Wrong splitted part", expected[i], s[i]);
        }
        assertEquals("Wrong number of splitted parts", expected.length,
                     s.length);

        r = new RE("x+");
        expected = new String[] {"xxxx", "xxxx"};
        s = r.grep(s);
        for (int i = 0; i < s.length; i++)
        {
            say("s[" + i + "] = " + s[i]);
            assertEquals("Grep fails", expected[i], s[i]);
        }
        assertEquals("Wrong number of string found by grep", expected.length,
                     s.length);
    }

    private void testSubst()
    {
        RE r = new RE("a*b");
        String expected = "-foo-garply-wacky-";
        String actual = r.subst("aaaabfooaaabgarplyaaabwackyb", "-");
        assertEquals("Wrong result of substitution in \"a*b\"", expected, actual);

        r = new RE("http:actual = r.subst("visit us: http:"1234<a href=\"$0\">$0</a>", RE.REPLACE_BACKREFERENCES);
        assertEquals("Wrong subst() result", "visit us: 1234<a href=\"http:r = new RE("(.*?)=(.*)");
        actual = r.subst("variable=value",
                         "$1_test_$212", RE.REPLACE_BACKREFERENCES);
        assertEquals("Wrong subst() result", "variable_test_value12", actual);

        r = new RE("^a$");
        actual = r.subst("a",
                         "b", RE.REPLACE_BACKREFERENCES);
        assertEquals("Wrong subst() result", "b", actual);

        r = new RE("^a$", RE.MATCH_MULTILINE);
        actual = r.subst("\r\na\r\n",
                         "b", RE.REPLACE_BACKREFERENCES);
        assertEquals("Wrong subst() result", "\r\nb\r\n", actual);
    }

    public void assertEquals(String message, String expected, String actual)
    {
        if (expected != null && !expected.equals(actual)
            || actual != null && !actual.equals(expected))
        {
            fail(message + " (expected \"" + expected
                 + "\", actual \"" + actual + "\")");
        }
    }

    public void assertEquals(String message, int expected, int actual)
    {
        if (expected != actual) {
            fail(message + " (expected \"" + expected
                 + "\", actual \"" + actual + "\")");
        }
    }


    private boolean getExpectedResult(String yesno)
    {
        if ("NO".equals(yesno))
        {
            return false;
        }
        else if ("YES".equals(yesno))
        {
            return true;
        }
        else
        {
            die("Test script error!");
            return false; }
    }


    private String findNextTest(BufferedReader br) throws IOException
    {
        String number = "";

        while (br.ready())
        {
            number = br.readLine();
            if (number == null)
            {
                break;
            }
            number = number.trim();
            if (number.startsWith("#"))
            {
                break;
            }
            if (!number.equals(""))
            {
                say("Script error.  Line = " + number);
                System.exit(-1);
            }
        }
        return number;
    }


    private RETestCase getNextTestCase(BufferedReader br) throws IOException
    {
        final String tag = findNextTest(br);

        if (!br.ready())
        {
            return null;
        }

        final String expr = br.readLine();

        final String matchAgainst = br.readLine();
        final boolean badPattern = "ERR".equals(matchAgainst);
        boolean shouldMatch = false;
        int expectedParenCount = 0;
        String[] expectedParens = null;

        if (!badPattern) {
            shouldMatch = getExpectedResult(br.readLine().trim());
            if (shouldMatch) {
                expectedParenCount = Integer.parseInt(br.readLine().trim());
                expectedParens = new String[expectedParenCount];
                for (int i = 0; i < expectedParenCount; i++) {
                    expectedParens[i] = br.readLine();
                }
            }
        }

        return new RETestCase(this, tag, expr, matchAgainst, badPattern,
                              shouldMatch, expectedParens);
    }
}

final class RETestCase
{
    final private StringBuffer log = new StringBuffer();
    final private int number;
    final private String tag; final private String pattern;
    final private String toMatch;
    final private boolean badPattern;
    final private boolean shouldMatch;
    final private String[] parens;
    final private RETest test;
    private RE regexp;

    public RETestCase(RETest test, String tag, String pattern,
                      String toMatch, boolean badPattern,
                      boolean shouldMatch, String[] parens)
    {
        this.number = ++test.testCount;
        this.test = test;
        this.tag = tag;
        this.pattern = pattern;
        this.toMatch = toMatch;
        this.badPattern = badPattern;
        this.shouldMatch = shouldMatch;
        if (parens != null) {
            this.parens = new String[parens.length];
            for (int i = 0; i < parens.length; i++) {
                this.parens[i] = parens[i];
            }
        } else {
            this.parens = null;
        }
    }

    public void runTest()
    {
        test.say(tag + "(" + number + "): " + pattern);
        if (testCreation()) {
            testMatch();
        }
    }

    boolean testCreation()
    {
        try
        {
            regexp = new RE();
            regexp.setProgram(test.compiler.compile(pattern));
            if (badPattern)
            {
                test.fail(log, "Was expected to be an error, but wasn't.");
                return false;
            }

            return true;
        }
        catch (Exception e)
        {
            if (badPattern)
            {
                log.append("   Match: ERR\n");
                success("Produces an error (" + e.toString() + "), as expected.");
                return false;
            }

            String message = (e.getMessage() == null) ? e.toString() : e.getMessage();
            test.fail(log, "Produces an unexpected exception \"" + message + "\"");
            e.printStackTrace();
        }
        catch (Error e)
        {
            test.fail(log, "Compiler threw fatal error \"" + e.getMessage() + "\"");
            e.printStackTrace();
        }

        return false;
    }

    private void testMatch()
    {
        log.append("   Match against: '" + toMatch + "'\n");
        try
        {
            boolean result = regexp.match(toMatch);
            log.append("   Matched: " + (result ? "YES" : "NO") + "\n");

            if (checkResult(result) && (!shouldMatch || checkParens()))
            {
                log.append("   Match using StringCharacterIterator\n");
                if (!tryMatchUsingCI(new StringCharacterIterator(toMatch)))
                    return;

                log.append("   Match using CharacterArrayCharacterIterator\n");
                if (!tryMatchUsingCI(new CharacterArrayCharacterIterator(toMatch.toCharArray(), 0, toMatch.length())))
                    return;

                log.append("   Match using StreamCharacterIterator\n");
                if (!tryMatchUsingCI(new StreamCharacterIterator(new StringBufferInputStream(toMatch))))
                    return;

                log.append("   Match using ReaderCharacterIterator\n");
                if (!tryMatchUsingCI(new ReaderCharacterIterator(new StringReader(toMatch))))
                    return;
            }
        }
        catch(Exception e)
        {
            test.fail(log, "Matcher threw exception: " + e.toString());
            e.printStackTrace();
        }
        catch(Error e)
        {
            test.fail(log, "Matcher threw fatal error \"" + e.getMessage() + "\"");
            e.printStackTrace();
        }
    }

    private boolean checkResult(boolean result)
    {
        if (result == shouldMatch) {
            success((shouldMatch ? "Matched" : "Did not match")
                    + " \"" + toMatch + "\", as expected:");
            return true;
        } else {
            if (shouldMatch) {
                test.fail(log, "Did not match \"" + toMatch + "\", when expected to.");
            } else {
                test.fail(log, "Matched \"" + toMatch + "\", when not expected to.");
            }
            return false;
        }
    }

    private boolean checkParens()
    {
        if (RETest.showSuccesses)
        {
            test.showParens(regexp);
        }

        log.append("   Paren count: " + regexp.getParenCount() + "\n");
        if (!assertEquals(log, "Wrong number of parens", parens.length, regexp.getParenCount()))
        {
            return false;
        }

        for (int p = 0; p < regexp.getParenCount(); p++)
        {
            log.append("   Paren " + p + ": " + regexp.getParen(p) + "\n");

            if ("null".equals(parens[p]) && regexp.getParen(p) == null)
            {
                continue;
            }
            if (!assertEquals(log, "Wrong register " + p, parens[p], regexp.getParen(p)))
            {
                return false;
            }
        }

        return true;
    }

    boolean tryMatchUsingCI(CharacterIterator matchAgainst)
    {
        try {
            boolean result = regexp.match(matchAgainst, 0);
            log.append("   Match: " + (result ? "YES" : "NO") + "\n");
            return checkResult(result) && (!shouldMatch || checkParens());
        }
        catch(Exception e)
        {
            test.fail(log, "Matcher threw exception: " + e.toString());
            e.printStackTrace();
        }
        catch(Error e)
        {
            test.fail(log, "Matcher threw fatal error \"" + e.getMessage() + "\"");
            e.printStackTrace();
        }
        return false;
    }

    public boolean assertEquals(StringBuffer log, String message, String expected, String actual)
    {
        if (expected != null && !expected.equals(actual)
            || actual != null && !actual.equals(expected))
        {
            test.fail(log, message + " (expected \"" + expected
                      + "\", actual \"" + actual + "\")");
            return false;
        }
        return true;
    }

    public boolean assertEquals(StringBuffer log, String message, int expected, int actual)
    {
        if (expected != actual) {
            test.fail(log, message + " (expected \"" + expected
                      + "\", actual \"" + actual + "\")");
            return false;
        }
        return true;
    }


    void success(String s)
    {
        if (RETest.showSuccesses)
        {
            test.say("" + RETest.NEW_LINE + "-----------------------" + RETest.NEW_LINE + "");
            test.say("Expression #" + (number) + " \"" + pattern + "\" ");
            test.say("Success: " + s);
        }
    }
}
