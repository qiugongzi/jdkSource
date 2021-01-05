


package com.sun.org.apache.xerces.internal.util;


public final class IntStack {

    private int fDepth;


    private int[] fData;

    public int size() {
        return fDepth;
    }


    public void push(int value) {
        ensureCapacity(fDepth + 1);
        fData[fDepth++] = value;
    }


    public int peek() {
        return fData[fDepth - 1];
    }


    public int elementAt(int depth) {
        return fData[depth];
    }


    public int pop() {
        return fData[--fDepth];
    }


    public void clear() {
        fDepth = 0;
    }

    public void print() {
        System.out.print('(');
        System.out.print(fDepth);
        System.out.print(") {");
        for (int i = 0; i < fDepth; i++) {
            if (i == 3) {
                System.out.print(" ...");
                break;
            }
            System.out.print(' ');
            System.out.print(fData[i]);
            if (i < fDepth - 1) {
                System.out.print(',');
            }
        }
        System.out.print(" }");
        System.out.println();
    }

    private void ensureCapacity(int size) {
        if (fData == null) {
            fData = new int[32];
        }
        else if (fData.length <= size) {
            int[] newdata = new int[fData.length * 2];
            System.arraycopy(fData, 0, newdata, 0, fData.length);
            fData = newdata;
        }
    }

}