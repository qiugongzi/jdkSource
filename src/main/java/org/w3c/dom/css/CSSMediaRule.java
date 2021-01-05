



package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.stylesheets.MediaList;


public interface CSSMediaRule extends CSSRule {

    public MediaList getMedia();


    public CSSRuleList getCssRules();


    public int insertRule(String rule,
                          int index)
                          throws DOMException;


    public void deleteRule(int index)
                           throws DOMException;

}
