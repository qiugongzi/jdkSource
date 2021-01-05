



package com.sun.org.apache.xalan.internal.xsltc.dom;

import java.text.Collator;
import java.util.Locale;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;


final class SortSettings {

    private AbstractTranslet _translet;


    private int[] _sortOrders;


    private int[] _types;


    private Locale[] _locales;


    private Collator[] _collators;


    private String[] _caseOrders;


    SortSettings(AbstractTranslet translet, int[] sortOrders, int[] types,
                 Locale[] locales, Collator[] collators, String[] caseOrders) {
        _translet = translet;
        _sortOrders = sortOrders;
        _types = types;
        _locales = locales;
        _collators = collators;
        _caseOrders = caseOrders;
    }


    AbstractTranslet getTranslet() {
        return _translet;
    }


    int[] getSortOrders() {
        return _sortOrders;
    }


    int[] getTypes() {
        return _types;
    }


    Locale[] getLocales() {
        return _locales;
    }


    Collator[] getCollators() {
        return _collators;
    }


    String[] getCaseOrders() {
        return _caseOrders;
    }
}
