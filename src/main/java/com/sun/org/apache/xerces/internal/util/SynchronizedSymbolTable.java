


package com.sun.org.apache.xerces.internal.util;



public final class SynchronizedSymbolTable
    extends SymbolTable {

    protected SymbolTable fSymbolTable;

    public SynchronizedSymbolTable(SymbolTable symbolTable) {
        fSymbolTable = symbolTable;
    } public SynchronizedSymbolTable() {
        fSymbolTable = new SymbolTable();
    } public SynchronizedSymbolTable(int size) {
        fSymbolTable = new SymbolTable(size);
    } public String addSymbol(String symbol) {

        synchronized (fSymbolTable) {
            return fSymbolTable.addSymbol(symbol);
        }

    } public String addSymbol(char[] buffer, int offset, int length) {

        synchronized (fSymbolTable) {
            return fSymbolTable.addSymbol(buffer, offset, length);
        }

    } public boolean containsSymbol(String symbol) {

        synchronized (fSymbolTable) {
            return fSymbolTable.containsSymbol(symbol);
        }

    } public boolean containsSymbol(char[] buffer, int offset, int length) {

        synchronized (fSymbolTable) {
            return fSymbolTable.containsSymbol(buffer, offset, length);
        }

    } }