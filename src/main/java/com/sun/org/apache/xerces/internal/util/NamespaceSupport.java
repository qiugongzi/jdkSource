


package com.sun.org.apache.xerces.internal.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;


public class NamespaceSupport implements NamespaceContext {

    protected String[] fNamespace = new String[16 * 2];


    protected int fNamespaceSize;

    protected int[] fContext = new int[8];


    protected int fCurrentContext;

    protected String[] fPrefixes = new String[16];

    public NamespaceSupport() {
    } public NamespaceSupport(NamespaceContext context) {
        pushContext();
        Enumeration prefixes = context.getAllPrefixes();
        while (prefixes.hasMoreElements()){
            String prefix = (String)prefixes.nextElement();
            String uri = context.getURI(prefix);
            declarePrefix(prefix, uri);
        }
    } public void reset() {

        fNamespaceSize = 0;
        fCurrentContext = 0;


        fNamespace[fNamespaceSize++] = XMLSymbols.PREFIX_XML;
        fNamespace[fNamespaceSize++] = NamespaceContext.XML_URI;
        fNamespace[fNamespaceSize++] = XMLSymbols.PREFIX_XMLNS;
        fNamespace[fNamespaceSize++] = NamespaceContext.XMLNS_URI;

        fContext[fCurrentContext] = fNamespaceSize;
        } public void pushContext() {

        if (fCurrentContext + 1 == fContext.length) {
            int[] contextarray = new int[fContext.length * 2];
            System.arraycopy(fContext, 0, contextarray, 0, fContext.length);
            fContext = contextarray;
        }

        fContext[++fCurrentContext] = fNamespaceSize;
        } public void popContext() {
        fNamespaceSize = fContext[fCurrentContext--];
        } public boolean declarePrefix(String prefix, String uri) {
        if (prefix == XMLSymbols.PREFIX_XML || prefix == XMLSymbols.PREFIX_XMLNS) {
            return false;
        }

        for (int i = fNamespaceSize; i > fContext[fCurrentContext]; i -= 2) {
            if (fNamespace[i - 2] == prefix) {
                fNamespace[i - 1] = uri;
                return true;
            }
        }

        if (fNamespaceSize == fNamespace.length) {
            String[] namespacearray = new String[fNamespaceSize * 2];
            System.arraycopy(fNamespace, 0, namespacearray, 0, fNamespaceSize);
            fNamespace = namespacearray;
        }

        fNamespace[fNamespaceSize++] = prefix;
        fNamespace[fNamespaceSize++] = uri;

        return true;

    } public String getURI(String prefix) {

        for (int i = fNamespaceSize; i > 0; i -= 2) {
            if (fNamespace[i - 2] == prefix) {
                return fNamespace[i - 1];
            }
        }

        return null;

    } public String getPrefix(String uri) {

        for (int i = fNamespaceSize; i > 0; i -= 2) {
            if (fNamespace[i - 1] == uri) {
                if (getURI(fNamespace[i - 2]) == uri)
                    return fNamespace[i - 2];
            }
        }

        return null;

    } public int getDeclaredPrefixCount() {
        return (fNamespaceSize - fContext[fCurrentContext]) / 2;
    } public String getDeclaredPrefixAt(int index) {
        return fNamespace[fContext[fCurrentContext] + index * 2];
    } public Iterator getPrefixes(){
        int count = 0;
        if (fPrefixes.length < (fNamespace.length/2)) {
            String[] prefixes = new String[fNamespaceSize];
            fPrefixes = prefixes;
        }
        String prefix = null;
        boolean unique = true;
        for (int i = 2; i < (fNamespaceSize-2); i += 2) {
            prefix = fNamespace[i + 2];
            for (int k=0;k<count;k++){
                if (fPrefixes[k]==prefix){
                    unique = false;
                    break;
                }
            }
            if (unique){
                fPrefixes[count++] = prefix;
            }
            unique = true;
        }
        return new IteratorPrefixes(fPrefixes, count);
    }public Enumeration getAllPrefixes() {
        int count = 0;
        if (fPrefixes.length < (fNamespace.length/2)) {
            String[] prefixes = new String[fNamespaceSize];
            fPrefixes = prefixes;
        }
        String prefix = null;
        boolean unique = true;
        for (int i = 2; i < (fNamespaceSize-2); i += 2) {
            prefix = fNamespace[i + 2];
            for (int k=0;k<count;k++){
                if (fPrefixes[k]==prefix){
                    unique = false;
                    break;
                }
            }
            if (unique){
                fPrefixes[count++] = prefix;
            }
            unique = true;
        }
        return new Prefixes(fPrefixes, count);
    }

    public  Vector getPrefixes(String uri){
        int count = 0;
        String prefix = null;
        boolean unique = true;
        Vector prefixList = new Vector();
        for (int i = fNamespaceSize; i >0 ; i -= 2) {
            if(fNamespace[i-1] == uri){
                if(!prefixList.contains(fNamespace[i-2]))
                    prefixList.add(fNamespace[i-2]);
            }
        }
        return prefixList;
    }




    public boolean containsPrefix(String prefix) {

        for (int i = fNamespaceSize; i > 0; i -= 2) {
            if (fNamespace[i - 2] == prefix) {
                return true;
            }
        }

        return false;
    }


    public boolean containsPrefixInCurrentContext(String prefix) {

        for (int i = fContext[fCurrentContext]; i < fNamespaceSize; i += 2) {
            if (fNamespace[i] == prefix) {
                return true;
            }
        }

        return false;
    }

    protected final class IteratorPrefixes implements Iterator  {
        private String[] prefixes;
        private int counter = 0;
        private int size = 0;


        public IteratorPrefixes(String [] prefixes, int size) {
            this.prefixes = prefixes;
            this.size = size;
        }


        public boolean hasNext() {
            return (counter < size);
        }


        public Object next() {
            if (counter< size){
                return fPrefixes[counter++];
            }
            throw new NoSuchElementException("Illegal access to Namespace prefixes enumeration.");
        }

        public String toString(){
            StringBuffer buf = new StringBuffer();
            for (int i=0;i<size;i++){
                buf.append(prefixes[i]);
                buf.append(" ");
            }

            return buf.toString();
        }

        public void remove(){
            throw new UnsupportedOperationException();
        }
    }


    protected final class Prefixes implements Enumeration {
        private String[] prefixes;
        private int counter = 0;
        private int size = 0;


        public Prefixes(String [] prefixes, int size) {
            this.prefixes = prefixes;
            this.size = size;
        }


        public boolean hasMoreElements() {
            return (counter< size);
        }


        public Object nextElement() {
            if (counter< size){
                return fPrefixes[counter++];
            }
            throw new NoSuchElementException("Illegal access to Namespace prefixes enumeration.");
        }

        public String toString(){
            StringBuffer buf = new StringBuffer();
            for (int i=0;i<size;i++){
                buf.append(prefixes[i]);
                buf.append(" ");
            }

            return buf.toString();
        }


    }

}