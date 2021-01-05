

package com.sun.jmx.mbeanserver;

import javax.management.openmbean.*;
import com.sun.jmx.mbeanserver.MXBeanMapping;
import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;
import java.lang.reflect.Type;


public abstract class MXBeanMappingFactory {

    protected MXBeanMappingFactory() {}


    public static final MXBeanMappingFactory DEFAULT =
            new DefaultMXBeanMappingFactory();


    public abstract MXBeanMapping mappingForType(Type t, MXBeanMappingFactory f)
    throws OpenDataException;
}
