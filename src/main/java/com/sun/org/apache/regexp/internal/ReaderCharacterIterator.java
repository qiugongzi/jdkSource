


package com.sun.org.apache.regexp.internal;

import java.io.Reader;
import java.io.IOException;


public final class ReaderCharacterIterator implements CharacterIterator
{

    private final Reader reader;


    private final StringBuffer buff;


    private boolean closed;


    public ReaderCharacterIterator(Reader reader)
    {
        this.reader = reader;
        this.buff = new StringBuffer(512);
        this.closed = false;
    }


    public String substring(int beginIndex, int endIndex)
    {
        try
        {
            ensure(endIndex);
            return buff.toString().substring(beginIndex, endIndex);
        }
        catch (IOException e)
        {
            throw new StringIndexOutOfBoundsException(e.getMessage());
        }
    }


    public String substring(int beginIndex)
    {
        try
        {
            readAll();
            return buff.toString().substring(beginIndex);
        }
        catch (IOException e)
        {
            throw new StringIndexOutOfBoundsException(e.getMessage());
        }
    }


    public char charAt(int pos)
    {
        try
        {
            ensure(pos);
            return buff.charAt(pos);
        }
        catch (IOException e)
        {
            throw new StringIndexOutOfBoundsException(e.getMessage());
        }
    }


    public boolean isEnd(int pos)
    {
        if (buff.length() > pos)
        {
            return false;
        }
        else
        {
            try
            {
                ensure(pos);
                return (buff.length() <= pos);
            }
            catch (IOException e)
            {
                throw new StringIndexOutOfBoundsException(e.getMessage());
            }
        }
    }


    private int read(int n) throws IOException
    {
        if (closed)
        {
            return 0;
        }

        char[] c = new char[n];
        int count = 0;
        int read = 0;

        do
        {
            read = reader.read(c);
            if (read < 0) {
                closed = true;
                break;
            }
            count += read;
            buff.append(c, 0, read);
        }
        while (count < n);

        return count;
    }


    private void readAll() throws IOException
    {
        while(! closed)
        {
            read(1000);
        }
    }


    private void ensure(int idx) throws IOException
    {
        if (closed)
        {
            return;
        }

        if (idx < buff.length())
        {
            return;
        }
        read(idx + 1 - buff.length());
    }
}
