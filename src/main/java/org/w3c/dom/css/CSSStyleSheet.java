



package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.stylesheets.StyleSheet;


public interface CSSStyleSheet extends StyleSheet {

    public CSSRule getOwnerRule();


    public CSSRuleList getCssRules();


    public int insertRule(String rule,
                          int index)
                          throws DOMException;


    public void deleteRule(int index)
                           throws DOMException;

}
