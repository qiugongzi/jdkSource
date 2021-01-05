



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.dtm.Axis;


public abstract class LocationPathPattern extends Pattern {
    private Template _template;
    private int _importPrecedence;
    private double _priority = Double.NaN;
    private int _position = 0;

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return Type.Void;               }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        }

    public void setTemplate(final Template template) {
        _template = template;
        _priority = template.getPriority();
        _importPrecedence = template.getImportPrecedence();
        _position = template.getPosition();
    }

    public Template getTemplate() {
        return _template;
    }

    public final double getPriority() {
        return Double.isNaN(_priority) ? getDefaultPriority() : _priority;
    }

    public double getDefaultPriority() {
        return 0.5;
    }


    public boolean noSmallerThan(LocationPathPattern other) {
        if (_importPrecedence > other._importPrecedence) {
            return true;
        }
        else if (_importPrecedence == other._importPrecedence) {
            if (_priority > other._priority) {
                return true;
            }
            else if (_priority == other._priority) {
                if (_position > other._position) {
                    return true;
                }
            }
        }
        return false;
    }

    public abstract StepPattern getKernelPattern();

    public abstract void reduceKernelPattern();

    public abstract boolean isWildcard();

    public int getAxis() {
        final StepPattern sp = getKernelPattern();
        return (sp != null) ? sp.getAxis() : Axis.CHILD;
    }

    public String toString() {
        return "root()";
    }
}
