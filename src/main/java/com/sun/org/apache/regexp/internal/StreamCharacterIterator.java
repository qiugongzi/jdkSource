


package com.sun.org.apache.regexp.internal;

import java.io.InputStream;
import java.io.IOException;


public final class StreamCharacterIterator implements CharacterIterator
{

    private final InputStream is;


    private final StringBuffer buff;


    private boolean closed;


    public StreamCharacterIterator(InputStream is)
    {
        this.is = is;
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

        int c;
        int i = n;
        while (--i >= 0)
        {
            c = is.read();
            if (c < 0) {
                closed = true;
                break;
            }
            buff.append((char) c);
        }
        return n - i;
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
