


package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSConstants;
import com.sun.org.apache.xerces.internal.xs.XSModelGroup;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;


public class XSModelGroupImpl implements XSModelGroup {

    public static final short MODELGROUP_CHOICE       = 101;
    public static final short MODELGROUP_SEQUENCE     = 102;
    public static final short MODELGROUP_ALL          = 103;

    public short fCompositor;

    public XSParticleDecl[] fParticles = null;
    public int fParticleCount = 0;

    public XSObjectList fAnnotations = null;

    public boolean isEmpty() {
        for (int i = 0; i < fParticleCount; i++) {
            if (!fParticles[i].isEmpty())
                return false;
        }
        return true;
    }


    public int minEffectiveTotalRange() {
        if (fCompositor == MODELGROUP_CHOICE)
            return minEffectiveTotalRangeChoice();
        else
            return minEffectiveTotalRangeAllSeq();
    }

    private int minEffectiveTotalRangeAllSeq() {
        int total = 0;
        for (int i = 0; i < fParticleCount; i++)
            total += fParticles[i].minEffectiveTotalRange();
        return total;
    }

    private int minEffectiveTotalRangeChoice() {
        int min = 0, one;
        if (fParticleCount > 0)
            min = fParticles[0].minEffectiveTotalRange();

        for (int i = 1; i < fParticleCount; i++) {
            one = fParticles[i].minEffectiveTotalRange();
            if (one < min)
                min = one;
        }

        return min;
    }

    public int maxEffectiveTotalRange() {
        if (fCompositor == MODELGROUP_CHOICE)
            return maxEffectiveTotalRangeChoice();
        else
            return maxEffectiveTotalRangeAllSeq();
    }

    private int maxEffectiveTotalRangeAllSeq() {
        int total = 0, one;
        for (int i = 0; i < fParticleCount; i++) {
            one = fParticles[i].maxEffectiveTotalRange();
            if (one == SchemaSymbols.OCCURRENCE_UNBOUNDED)
                return SchemaSymbols.OCCURRENCE_UNBOUNDED;
            total += one;
        }
        return total;
    }

    private int maxEffectiveTotalRangeChoice() {
        int max = 0, one;
        if (fParticleCount > 0) {
            max = fParticles[0].maxEffectiveTotalRange();
            if (max == SchemaSymbols.OCCURRENCE_UNBOUNDED)
                return SchemaSymbols.OCCURRENCE_UNBOUNDED;
        }

        for (int i = 1; i < fParticleCount; i++) {
            one = fParticles[i].maxEffectiveTotalRange();
            if (one == SchemaSymbols.OCCURRENCE_UNBOUNDED)
                return SchemaSymbols.OCCURRENCE_UNBOUNDED;
            if (one > max)
                max = one;
        }
        return max;
    }


    private String fDescription = null;
    public String toString() {
        if (fDescription == null) {
            StringBuffer buffer = new StringBuffer();
            if (fCompositor == MODELGROUP_ALL)
                buffer.append("all(");
            else  buffer.append('(');
            if (fParticleCount > 0)
                buffer.append(fParticles[0].toString());
            for (int i = 1; i < fParticleCount; i++) {
                if (fCompositor == MODELGROUP_CHOICE)
                    buffer.append('|');
                else
                    buffer.append(',');
                buffer.append(fParticles[i].toString());
            }
            buffer.append(')');
            fDescription = buffer.toString();
        }
        return fDescription;
    }

    public void reset(){
        fCompositor = MODELGROUP_SEQUENCE;
        fParticles = null;
        fParticleCount = 0;
        fDescription = null;
        fAnnotations = null;
    }


    public short getType() {
        return XSConstants.MODEL_GROUP;
    }


    public String getName() {
        return null;
    }


    public String getNamespace() {
        return null;
    }


    public short getCompositor() {
        if (fCompositor == MODELGROUP_CHOICE)
            return XSModelGroup.COMPOSITOR_CHOICE;
        else if (fCompositor == MODELGROUP_SEQUENCE)
            return XSModelGroup.COMPOSITOR_SEQUENCE;
        else
            return XSModelGroup.COMPOSITOR_ALL;
    }


    public XSObjectList getParticles() {
        return new XSObjectListImpl(fParticles, fParticleCount);
    }


    public XSAnnotation getAnnotation() {
        return (fAnnotations != null) ? (XSAnnotation) fAnnotations.item(0) : null;
    }


    public XSObjectList getAnnotations() {
        return (fAnnotations != null) ? fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }


    public XSNamespaceItem getNamespaceItem() {
        return null;
    }

}