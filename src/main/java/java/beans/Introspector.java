

package java.beans;

import com.sun.beans.TypeResolver;
import com.sun.beans.WeakCache;
import com.sun.beans.finder.ClassFinder;
import com.sun.beans.finder.MethodFinder;

import java.awt.Component;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.TreeMap;

import sun.reflect.misc.ReflectUtil;



public class Introspector {

    public final static int USE_ALL_BEANINFO           = 1;

    public final static int IGNORE_IMMEDIATE_BEANINFO  = 2;

    public final static int IGNORE_ALL_BEANINFO        = 3;

    private static final WeakCache<Class<?>, Method[]> declaredMethodCache = new WeakCache<>();

    private Class<?> beanClass;
    private BeanInfo explicitBeanInfo;
    private BeanInfo superBeanInfo;
    private BeanInfo additionalBeanInfo[];

    private boolean propertyChangeSource = false;
    private static Class<EventListener> eventListenerType = EventListener.class;

    private String defaultEventName;
    private String defaultPropertyName;
    private int defaultEventIndex = -1;
    private int defaultPropertyIndex = -1;

    private Map<String, MethodDescriptor> methods;

    private Map<String, PropertyDescriptor> properties;

    private Map<String, EventSetDescriptor> events;

    private final static EventSetDescriptor[] EMPTY_EVENTSETDESCRIPTORS = new EventSetDescriptor[0];

    static final String ADD_PREFIX = "add";
    static final String REMOVE_PREFIX = "remove";
    static final String GET_PREFIX = "get";
    static final String SET_PREFIX = "set";
    static final String IS_PREFIX = "is";

    public static BeanInfo getBeanInfo(Class<?> beanClass)
        throws IntrospectionException
    {
        if (!ReflectUtil.isPackageAccessible(beanClass)) {
            return (new Introspector(beanClass, null, USE_ALL_BEANINFO)).getBeanInfo();
        }
        ThreadGroupContext context = ThreadGroupContext.getContext();
        BeanInfo beanInfo;
        synchronized (declaredMethodCache) {
            beanInfo = context.getBeanInfo(beanClass);
        }
        if (beanInfo == null) {
            beanInfo = new Introspector(beanClass, null, USE_ALL_BEANINFO).getBeanInfo();
            synchronized (declaredMethodCache) {
                context.putBeanInfo(beanClass, beanInfo);
            }
        }
        return beanInfo;
    }


    public static BeanInfo getBeanInfo(Class<?> beanClass, int flags)
                                                throws IntrospectionException {
        return getBeanInfo(beanClass, null, flags);
    }


    public static BeanInfo getBeanInfo(Class<?> beanClass, Class<?> stopClass)
                                                throws IntrospectionException {
        return getBeanInfo(beanClass, stopClass, USE_ALL_BEANINFO);
    }


    public static BeanInfo getBeanInfo(Class<?> beanClass, Class<?> stopClass,
                                        int flags) throws IntrospectionException {
        BeanInfo bi;
        if (stopClass == null && flags == USE_ALL_BEANINFO) {
            bi = getBeanInfo(beanClass);
        } else {
            bi = (new Introspector(beanClass, stopClass, flags)).getBeanInfo();
        }
        return bi;

        }



    public static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                        Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }



    public static String[] getBeanInfoSearchPath() {
        return ThreadGroupContext.getContext().getBeanInfoFinder().getPackages();
    }



    public static void setBeanInfoSearchPath(String[] path) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPropertiesAccess();
        }
        ThreadGroupContext.getContext().getBeanInfoFinder().setPackages(path);
    }




    public static void flushCaches() {
        synchronized (declaredMethodCache) {
            ThreadGroupContext.getContext().clearBeanInfoCache();
            declaredMethodCache.clear();
        }
    }


    public static void flushFromCaches(Class<?> clz) {
        if (clz == null) {
            throw new NullPointerException();
        }
        synchronized (declaredMethodCache) {
            ThreadGroupContext.getContext().removeBeanInfo(clz);
            declaredMethodCache.put(clz, null);
        }
    }

    private Introspector(Class<?> beanClass, Class<?> stopClass, int flags)
                                            throws IntrospectionException {
        this.beanClass = beanClass;

        if (stopClass != null) {
            boolean isSuper = false;
            for (Class<?> c = beanClass.getSuperclass(); c != null; c = c.getSuperclass()) {
                if (c == stopClass) {
                    isSuper = true;
                }
            }
            if (!isSuper) {
                throw new IntrospectionException(stopClass.getName() + " not superclass of " +
                                        beanClass.getName());
            }
        }

        if (flags == USE_ALL_BEANINFO) {
            explicitBeanInfo = findExplicitBeanInfo(beanClass);
        }

        Class<?> superClass = beanClass.getSuperclass();
        if (superClass != stopClass) {
            int newFlags = flags;
            if (newFlags == IGNORE_IMMEDIATE_BEANINFO) {
                newFlags = USE_ALL_BEANINFO;
            }
            superBeanInfo = getBeanInfo(superClass, stopClass, newFlags);
        }
        if (explicitBeanInfo != null) {
            additionalBeanInfo = explicitBeanInfo.getAdditionalBeanInfo();
        }
        if (additionalBeanInfo == null) {
            additionalBeanInfo = new BeanInfo[0];
        }
    }


    private BeanInfo getBeanInfo() throws IntrospectionException {

        BeanDescriptor bd = getTargetBeanDescriptor();
        MethodDescriptor mds[] = getTargetMethodInfo();
        EventSetDescriptor esds[] = getTargetEventInfo();
        PropertyDescriptor pds[] = getTargetPropertyInfo();

        int defaultEvent = getTargetDefaultEventIndex();
        int defaultProperty = getTargetDefaultPropertyIndex();

        return new GenericBeanInfo(bd, esds, defaultEvent, pds,
                        defaultProperty, mds, explicitBeanInfo);

    }


    private static BeanInfo findExplicitBeanInfo(Class<?> beanClass) {
        return ThreadGroupContext.getContext().getBeanInfoFinder().find(beanClass);
    }



    private PropertyDescriptor[] getTargetPropertyInfo() {

        PropertyDescriptor[] explicitProperties = null;
        if (explicitBeanInfo != null) {
            explicitProperties = getPropertyDescriptors(this.explicitBeanInfo);
        }

        if (explicitProperties == null && superBeanInfo != null) {
            addPropertyDescriptors(getPropertyDescriptors(this.superBeanInfo));
        }

        for (int i = 0; i < additionalBeanInfo.length; i++) {
            addPropertyDescriptors(additionalBeanInfo[i].getPropertyDescriptors());
        }

        if (explicitProperties != null) {
            addPropertyDescriptors(explicitProperties);

        } else {

            Method methodList[] = getPublicDeclaredMethods(beanClass);

            for (int i = 0; i < methodList.length; i++) {
                Method method = methodList[i];
                if (method == null) {
                    continue;
                }
                int mods = method.getModifiers();
                if (Modifier.isStatic(mods)) {
                    continue;
                }
                String name = method.getName();
                Class<?>[] argTypes = method.getParameterTypes();
                Class<?> resultType = method.getReturnType();
                int argCount = argTypes.length;
                PropertyDescriptor pd = null;

                if (name.length() <= 3 && !name.startsWith(IS_PREFIX)) {
                    continue;
                }

                try {

                    if (argCount == 0) {
                        if (name.startsWith(GET_PREFIX)) {
                            pd = new PropertyDescriptor(this.beanClass, name.substring(3), method, null);
                        } else if (resultType == boolean.class && name.startsWith(IS_PREFIX)) {
                            pd = new PropertyDescriptor(this.beanClass, name.substring(2), method, null);
                        }
                    } else if (argCount == 1) {
                        if (int.class.equals(argTypes[0]) && name.startsWith(GET_PREFIX)) {
                            pd = new IndexedPropertyDescriptor(this.beanClass, name.substring(3), null, null, method, null);
                        } else if (void.class.equals(resultType) && name.startsWith(SET_PREFIX)) {
                            pd = new PropertyDescriptor(this.beanClass, name.substring(3), null, method);
                            if (throwsException(method, PropertyVetoException.class)) {
                                pd.setConstrained(true);
                            }
                        }
                    } else if (argCount == 2) {
                            if (void.class.equals(resultType) && int.class.equals(argTypes[0]) && name.startsWith(SET_PREFIX)) {
                            pd = new IndexedPropertyDescriptor(this.beanClass, name.substring(3), null, null, null, method);
                            if (throwsException(method, PropertyVetoException.class)) {
                                pd.setConstrained(true);
                            }
                        }
                    }
                } catch (IntrospectionException ex) {
                    pd = null;
                }

                if (pd != null) {
                    if (propertyChangeSource) {
                        pd.setBound(true);
                    }
                    addPropertyDescriptor(pd);
                }
            }
        }
        processPropertyDescriptors();

        PropertyDescriptor result[] =
                properties.values().toArray(new PropertyDescriptor[properties.size()]);

        if (defaultPropertyName != null) {
            for (int i = 0; i < result.length; i++) {
                if (defaultPropertyName.equals(result[i].getName())) {
                    defaultPropertyIndex = i;
                }
            }
        }

        return result;
    }

    private HashMap<String, List<PropertyDescriptor>> pdStore = new HashMap<>();


    private void addPropertyDescriptor(PropertyDescriptor pd) {
        String propName = pd.getName();
        List<PropertyDescriptor> list = pdStore.get(propName);
        if (list == null) {
            list = new ArrayList<>();
            pdStore.put(propName, list);
        }
        if (this.beanClass != pd.getClass0()) {
            Method read = pd.getReadMethod();
            Method write = pd.getWriteMethod();
            boolean cls = true;
            if (read != null) cls = cls && read.getGenericReturnType() instanceof Class;
            if (write != null) cls = cls && write.getGenericParameterTypes()[0] instanceof Class;
            if (pd instanceof IndexedPropertyDescriptor) {
                IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor) pd;
                Method readI = ipd.getIndexedReadMethod();
                Method writeI = ipd.getIndexedWriteMethod();
                if (readI != null) cls = cls && readI.getGenericReturnType() instanceof Class;
                if (writeI != null) cls = cls && writeI.getGenericParameterTypes()[1] instanceof Class;
                if (!cls) {
                    pd = new IndexedPropertyDescriptor(ipd);
                    pd.updateGenericsFor(this.beanClass);
                }
            }
            else if (!cls) {
                pd = new PropertyDescriptor(pd);
                pd.updateGenericsFor(this.beanClass);
            }
        }
        list.add(pd);
    }

    private void addPropertyDescriptors(PropertyDescriptor[] descriptors) {
        if (descriptors != null) {
            for (PropertyDescriptor descriptor : descriptors) {
                addPropertyDescriptor(descriptor);
            }
        }
    }

    private PropertyDescriptor[] getPropertyDescriptors(BeanInfo info) {
        PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        int index = info.getDefaultPropertyIndex();
        if ((0 <= index) && (index < descriptors.length)) {
            this.defaultPropertyName = descriptors[index].getName();
        }
        return descriptors;
    }


    private void processPropertyDescriptors() {
        if (properties == null) {
            properties = new TreeMap<>();
        }

        List<PropertyDescriptor> list;

        PropertyDescriptor pd, gpd, spd;
        IndexedPropertyDescriptor ipd, igpd, ispd;

        Iterator<List<PropertyDescriptor>> it = pdStore.values().iterator();
        while (it.hasNext()) {
            pd = null; gpd = null; spd = null;
            ipd = null; igpd = null; ispd = null;

            list = it.next();

            for (int i = 0; i < list.size(); i++) {
                pd = list.get(i);
                if (pd instanceof IndexedPropertyDescriptor) {
                    ipd = (IndexedPropertyDescriptor)pd;
                    if (ipd.getIndexedReadMethod() != null) {
                        if (igpd != null) {
                            igpd = new IndexedPropertyDescriptor(igpd, ipd);
                        } else {
                            igpd = ipd;
                        }
                    }
                } else {
                    if (pd.getReadMethod() != null) {
                        String pdName = pd.getReadMethod().getName();
                        if (gpd != null) {
                            String gpdName = gpd.getReadMethod().getName();
                            if (gpdName.equals(pdName) || !gpdName.startsWith(IS_PREFIX)) {
                                gpd = new PropertyDescriptor(gpd, pd);
                            }
                        } else {
                            gpd = pd;
                        }
                    }
                }
            }

            for (int i = 0; i < list.size(); i++) {
                pd = list.get(i);
                if (pd instanceof IndexedPropertyDescriptor) {
                    ipd = (IndexedPropertyDescriptor)pd;
                    if (ipd.getIndexedWriteMethod() != null) {
                        if (igpd != null) {
                            if (isAssignable(igpd.getIndexedPropertyType(), ipd.getIndexedPropertyType())) {
                                if (ispd != null) {
                                    ispd = new IndexedPropertyDescriptor(ispd, ipd);
                                } else {
                                    ispd = ipd;
                                }
                            }
                        } else {
                            if (ispd != null) {
                                ispd = new IndexedPropertyDescriptor(ispd, ipd);
                            } else {
                                ispd = ipd;
                            }
                        }
                    }
                } else {
                    if (pd.getWriteMethod() != null) {
                        if (gpd != null) {
                            if (isAssignable(gpd.getPropertyType(), pd.getPropertyType())) {
                                if (spd != null) {
                                    spd = new PropertyDescriptor(spd, pd);
                                } else {
                                    spd = pd;
                                }
                            }
                        } else {
                            if (spd != null) {
                                spd = new PropertyDescriptor(spd, pd);
                            } else {
                                spd = pd;
                            }
                        }
                    }
                }
            }

            pd = null; ipd = null;

            if (igpd != null && ispd != null) {
                if ((gpd == spd) || (gpd == null)) {
                    pd = spd;
                } else if (spd == null) {
                    pd = gpd;
                } else if (spd instanceof IndexedPropertyDescriptor) {
                    pd = mergePropertyWithIndexedProperty(gpd, (IndexedPropertyDescriptor) spd);
                } else if (gpd instanceof IndexedPropertyDescriptor) {
                    pd = mergePropertyWithIndexedProperty(spd, (IndexedPropertyDescriptor) gpd);
                } else {
                    pd = mergePropertyDescriptor(gpd, spd);
                }
                if (igpd == ispd) {
                    ipd = igpd;
                } else {
                    ipd = mergePropertyDescriptor(igpd, ispd);
                }
                if (pd == null) {
                    pd = ipd;
                } else {
                    Class<?> propType = pd.getPropertyType();
                    Class<?> ipropType = ipd.getIndexedPropertyType();
                    if (propType.isArray() && propType.getComponentType() == ipropType) {
                        pd = pd.getClass0().isAssignableFrom(ipd.getClass0())
                                ? new IndexedPropertyDescriptor(pd, ipd)
                                : new IndexedPropertyDescriptor(ipd, pd);
                    } else if (pd.getClass0().isAssignableFrom(ipd.getClass0())) {
                        pd = pd.getClass0().isAssignableFrom(ipd.getClass0())
                                ? new PropertyDescriptor(pd, ipd)
                                : new PropertyDescriptor(ipd, pd);
                    } else {
                        pd = ipd;
                    }
                }
            } else if (gpd != null && spd != null) {
                if (igpd != null) {
                    gpd = mergePropertyWithIndexedProperty(gpd, igpd);
                }
                if (ispd != null) {
                    spd = mergePropertyWithIndexedProperty(spd, ispd);
                }
                if (gpd == spd) {
                    pd = gpd;
                } else if (spd instanceof IndexedPropertyDescriptor) {
                    pd = mergePropertyWithIndexedProperty(gpd, (IndexedPropertyDescriptor) spd);
                } else if (gpd instanceof IndexedPropertyDescriptor) {
                    pd = mergePropertyWithIndexedProperty(spd, (IndexedPropertyDescriptor) gpd);
                } else {
                    pd = mergePropertyDescriptor(gpd, spd);
                }
            } else if (ispd != null) {
                pd = ispd;
                if (spd != null) {
                    pd = mergePropertyDescriptor(ispd, spd);
                }
                if (gpd != null) {
                    pd = mergePropertyDescriptor(ispd, gpd);
                }
            } else if (igpd != null) {
                pd = igpd;
                if (gpd != null) {
                    pd = mergePropertyDescriptor(igpd, gpd);
                }
                if (spd != null) {
                    pd = mergePropertyDescriptor(igpd, spd);
                }
            } else if (spd != null) {
                pd = spd;
            } else if (gpd != null) {
                pd = gpd;
            }

            if (pd instanceof IndexedPropertyDescriptor) {
                ipd = (IndexedPropertyDescriptor)pd;
                if (ipd.getIndexedReadMethod() == null && ipd.getIndexedWriteMethod() == null) {
                    pd = new PropertyDescriptor(ipd);
                }
            }

            if ( (pd == null) && (list.size() > 0) ) {
                pd = list.get(0);
            }

            if (pd != null) {
                properties.put(pd.getName(), pd);
            }
        }
    }

    private static boolean isAssignable(Class<?> current, Class<?> candidate) {
        return ((current == null) || (candidate == null)) ? current == candidate : current.isAssignableFrom(candidate);
    }

    private PropertyDescriptor mergePropertyWithIndexedProperty(PropertyDescriptor pd, IndexedPropertyDescriptor ipd) {
        Class<?> type = pd.getPropertyType();
        if (type.isArray() && (type.getComponentType() == ipd.getIndexedPropertyType())) {
            return pd.getClass0().isAssignableFrom(ipd.getClass0())
                    ? new IndexedPropertyDescriptor(pd, ipd)
                    : new IndexedPropertyDescriptor(ipd, pd);
        }
        return pd;
    }


    private PropertyDescriptor mergePropertyDescriptor(IndexedPropertyDescriptor ipd,
                                                       PropertyDescriptor pd) {
        PropertyDescriptor result = null;

        Class<?> propType = pd.getPropertyType();
        Class<?> ipropType = ipd.getIndexedPropertyType();

        if (propType.isArray() && propType.getComponentType() == ipropType) {
            if (pd.getClass0().isAssignableFrom(ipd.getClass0())) {
                result = new IndexedPropertyDescriptor(pd, ipd);
            } else {
                result = new IndexedPropertyDescriptor(ipd, pd);
            }
        } else if ((ipd.getReadMethod() == null) && (ipd.getWriteMethod() == null)) {
            if (pd.getClass0().isAssignableFrom(ipd.getClass0())) {
                result = new PropertyDescriptor(pd, ipd);
            } else {
                result = new PropertyDescriptor(ipd, pd);
            }
        } else {
            if (pd.getClass0().isAssignableFrom(ipd.getClass0())) {
                result = ipd;
            } else {
                result = pd;
                Method write = result.getWriteMethod();
                Method read = result.getReadMethod();

                if (read == null && write != null) {
                    read = findMethod(result.getClass0(),
                                      GET_PREFIX + NameGenerator.capitalize(result.getName()), 0);
                    if (read != null) {
                        try {
                            result.setReadMethod(read);
                        } catch (IntrospectionException ex) {
                            }
                    }
                }
                if (write == null && read != null) {
                    write = findMethod(result.getClass0(),
                                       SET_PREFIX + NameGenerator.capitalize(result.getName()), 1,
                                       new Class<?>[] { FeatureDescriptor.getReturnType(result.getClass0(), read) });
                    if (write != null) {
                        try {
                            result.setWriteMethod(write);
                        } catch (IntrospectionException ex) {
                            }
                    }
                }
            }
        }
        return result;
    }

    private PropertyDescriptor mergePropertyDescriptor(PropertyDescriptor pd1,
                                                       PropertyDescriptor pd2) {
        if (pd1.getClass0().isAssignableFrom(pd2.getClass0())) {
            return new PropertyDescriptor(pd1, pd2);
        } else {
            return new PropertyDescriptor(pd2, pd1);
        }
    }

    private IndexedPropertyDescriptor mergePropertyDescriptor(IndexedPropertyDescriptor ipd1,
                                                       IndexedPropertyDescriptor ipd2) {
        if (ipd1.getClass0().isAssignableFrom(ipd2.getClass0())) {
            return new IndexedPropertyDescriptor(ipd1, ipd2);
        } else {
            return new IndexedPropertyDescriptor(ipd2, ipd1);
        }
    }


    private EventSetDescriptor[] getTargetEventInfo() throws IntrospectionException {
        if (events == null) {
            events = new HashMap<>();
        }

        EventSetDescriptor[] explicitEvents = null;
        if (explicitBeanInfo != null) {
            explicitEvents = explicitBeanInfo.getEventSetDescriptors();
            int ix = explicitBeanInfo.getDefaultEventIndex();
            if (ix >= 0 && ix < explicitEvents.length) {
                defaultEventName = explicitEvents[ix].getName();
            }
        }

        if (explicitEvents == null && superBeanInfo != null) {
            EventSetDescriptor supers[] = superBeanInfo.getEventSetDescriptors();
            for (int i = 0 ; i < supers.length; i++) {
                addEvent(supers[i]);
            }
            int ix = superBeanInfo.getDefaultEventIndex();
            if (ix >= 0 && ix < supers.length) {
                defaultEventName = supers[ix].getName();
            }
        }

        for (int i = 0; i < additionalBeanInfo.length; i++) {
            EventSetDescriptor additional[] = additionalBeanInfo[i].getEventSetDescriptors();
            if (additional != null) {
                for (int j = 0 ; j < additional.length; j++) {
                    addEvent(additional[j]);
                }
            }
        }

        if (explicitEvents != null) {
            for (int i = 0 ; i < explicitEvents.length; i++) {
                addEvent(explicitEvents[i]);
            }

        } else {

            Method methodList[] = getPublicDeclaredMethods(beanClass);

            Map<String, Method> adds = null;
            Map<String, Method> removes = null;
            Map<String, Method> gets = null;

            for (int i = 0; i < methodList.length; i++) {
                Method method = methodList[i];
                if (method == null) {
                    continue;
                }
                int mods = method.getModifiers();
                if (Modifier.isStatic(mods)) {
                    continue;
                }
                String name = method.getName();
                if (!name.startsWith(ADD_PREFIX) && !name.startsWith(REMOVE_PREFIX)
                    && !name.startsWith(GET_PREFIX)) {
                    continue;
                }

                if (name.startsWith(ADD_PREFIX)) {
                    Class<?> returnType = method.getReturnType();
                    if (returnType == void.class) {
                        Type[] parameterTypes = method.getGenericParameterTypes();
                        if (parameterTypes.length == 1) {
                            Class<?> type = TypeResolver.erase(TypeResolver.resolveInClass(beanClass, parameterTypes[0]));
                            if (Introspector.isSubclass(type, eventListenerType)) {
                                String listenerName = name.substring(3);
                                if (listenerName.length() > 0 &&
                                    type.getName().endsWith(listenerName)) {
                                    if (adds == null) {
                                        adds = new HashMap<>();
                                    }
                                    adds.put(listenerName, method);
                                }
                            }
                        }
                    }
                }
                else if (name.startsWith(REMOVE_PREFIX)) {
                    Class<?> returnType = method.getReturnType();
                    if (returnType == void.class) {
                        Type[] parameterTypes = method.getGenericParameterTypes();
                        if (parameterTypes.length == 1) {
                            Class<?> type = TypeResolver.erase(TypeResolver.resolveInClass(beanClass, parameterTypes[0]));
                            if (Introspector.isSubclass(type, eventListenerType)) {
                                String listenerName = name.substring(6);
                                if (listenerName.length() > 0 &&
                                    type.getName().endsWith(listenerName)) {
                                    if (removes == null) {
                                        removes = new HashMap<>();
                                    }
                                    removes.put(listenerName, method);
                                }
                            }
                        }
                    }
                }
                else if (name.startsWith(GET_PREFIX)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 0) {
                        Class<?> returnType = FeatureDescriptor.getReturnType(beanClass, method);
                        if (returnType.isArray()) {
                            Class<?> type = returnType.getComponentType();
                            if (Introspector.isSubclass(type, eventListenerType)) {
                                String listenerName  = name.substring(3, name.length() - 1);
                                if (listenerName.length() > 0 &&
                                    type.getName().endsWith(listenerName)) {
                                    if (gets == null) {
                                        gets = new HashMap<>();
                                    }
                                    gets.put(listenerName, method);
                                }
                            }
                        }
                    }
                }
            }

            if (adds != null && removes != null) {
                Iterator<String> keys = adds.keySet().iterator();
                while (keys.hasNext()) {
                    String listenerName = keys.next();
                    if (removes.get(listenerName) == null || !listenerName.endsWith("Listener")) {
                        continue;
                    }
                    String eventName = decapitalize(listenerName.substring(0, listenerName.length()-8));
                    Method addMethod = adds.get(listenerName);
                    Method removeMethod = removes.get(listenerName);
                    Method getMethod = null;
                    if (gets != null) {
                        getMethod = gets.get(listenerName);
                    }
                    Class<?> argType = FeatureDescriptor.getParameterTypes(beanClass, addMethod)[0];

                    Method allMethods[] = getPublicDeclaredMethods(argType);
                    List<Method> validMethods = new ArrayList<>(allMethods.length);
                    for (int i = 0; i < allMethods.length; i++) {
                        if (allMethods[i] == null) {
                            continue;
                        }

                        if (isEventHandler(allMethods[i])) {
                            validMethods.add(allMethods[i]);
                        }
                    }
                    Method[] methods = validMethods.toArray(new Method[validMethods.size()]);

                    EventSetDescriptor esd = new EventSetDescriptor(eventName, argType,
                                                                    methods, addMethod,
                                                                    removeMethod,
                                                                    getMethod);

                    if (throwsException(addMethod,
                                        java.util.TooManyListenersException.class)) {
                        esd.setUnicast(true);
                    }
                    addEvent(esd);
                }
            } }
        EventSetDescriptor[] result;
        if (events.size() == 0) {
            result = EMPTY_EVENTSETDESCRIPTORS;
        } else {
            result = new EventSetDescriptor[events.size()];
            result = events.values().toArray(result);

            if (defaultEventName != null) {
                for (int i = 0; i < result.length; i++) {
                    if (defaultEventName.equals(result[i].getName())) {
                        defaultEventIndex = i;
                    }
                }
            }
        }
        return result;
    }

    private void addEvent(EventSetDescriptor esd) {
        String key = esd.getName();
        if (esd.getName().equals("propertyChange")) {
            propertyChangeSource = true;
        }
        EventSetDescriptor old = events.get(key);
        if (old == null) {
            events.put(key, esd);
            return;
        }
        EventSetDescriptor composite = new EventSetDescriptor(old, esd);
        events.put(key, composite);
    }


    private MethodDescriptor[] getTargetMethodInfo() {
        if (methods == null) {
            methods = new HashMap<>(100);
        }

        MethodDescriptor[] explicitMethods = null;
        if (explicitBeanInfo != null) {
            explicitMethods = explicitBeanInfo.getMethodDescriptors();
        }

        if (explicitMethods == null && superBeanInfo != null) {
            MethodDescriptor supers[] = superBeanInfo.getMethodDescriptors();
            for (int i = 0 ; i < supers.length; i++) {
                addMethod(supers[i]);
            }
        }

        for (int i = 0; i < additionalBeanInfo.length; i++) {
            MethodDescriptor additional[] = additionalBeanInfo[i].getMethodDescriptors();
            if (additional != null) {
                for (int j = 0 ; j < additional.length; j++) {
                    addMethod(additional[j]);
                }
            }
        }

        if (explicitMethods != null) {
            for (int i = 0 ; i < explicitMethods.length; i++) {
                addMethod(explicitMethods[i]);
            }

        } else {

            Method methodList[] = getPublicDeclaredMethods(beanClass);

            for (int i = 0; i < methodList.length; i++) {
                Method method = methodList[i];
                if (method == null) {
                    continue;
                }
                MethodDescriptor md = new MethodDescriptor(method);
                addMethod(md);
            }
        }

        MethodDescriptor result[] = new MethodDescriptor[methods.size()];
        result = methods.values().toArray(result);

        return result;
    }

    private void addMethod(MethodDescriptor md) {
        String name = md.getName();

        MethodDescriptor old = methods.get(name);
        if (old == null) {
            methods.put(name, md);
            return;
        }

        String[] p1 = md.getParamNames();
        String[] p2 = old.getParamNames();

        boolean match = false;
        if (p1.length == p2.length) {
            match = true;
            for (int i = 0; i < p1.length; i++) {
                if (p1[i] != p2[i]) {
                    match = false;
                    break;
                }
            }
        }
        if (match) {
            MethodDescriptor composite = new MethodDescriptor(old, md);
            methods.put(name, composite);
            return;
        }

        String longKey = makeQualifiedMethodName(name, p1);
        old = methods.get(longKey);
        if (old == null) {
            methods.put(longKey, md);
            return;
        }
        MethodDescriptor composite = new MethodDescriptor(old, md);
        methods.put(longKey, composite);
    }


    private static String makeQualifiedMethodName(String name, String[] params) {
        StringBuffer sb = new StringBuffer(name);
        sb.append('=');
        for (int i = 0; i < params.length; i++) {
            sb.append(':');
            sb.append(params[i]);
        }
        return sb.toString();
    }

    private int getTargetDefaultEventIndex() {
        return defaultEventIndex;
    }

    private int getTargetDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }

    private BeanDescriptor getTargetBeanDescriptor() {
        if (explicitBeanInfo != null) {
            BeanDescriptor bd = explicitBeanInfo.getBeanDescriptor();
            if (bd != null) {
                return (bd);
            }
        }
        return new BeanDescriptor(this.beanClass, findCustomizerClass(this.beanClass));
    }

    private static Class<?> findCustomizerClass(Class<?> type) {
        String name = type.getName() + "Customizer";
        try {
            type = ClassFinder.findClass(name, type.getClassLoader());
            if (Component.class.isAssignableFrom(type) && Customizer.class.isAssignableFrom(type)) {
                return type;
            }
        }
        catch (Exception exception) {
            }
        return null;
    }

    private boolean isEventHandler(Method m) {
        Type argTypes[] = m.getGenericParameterTypes();
        if (argTypes.length != 1) {
            return false;
        }
        return isSubclass(TypeResolver.erase(TypeResolver.resolveInClass(beanClass, argTypes[0])), EventObject.class);
    }


    private static Method[] getPublicDeclaredMethods(Class<?> clz) {
        if (!ReflectUtil.isPackageAccessible(clz)) {
            return new Method[0];
        }
        synchronized (declaredMethodCache) {
            Method[] result = declaredMethodCache.get(clz);
            if (result == null) {
                result = clz.getMethods();
                for (int i = 0; i < result.length; i++) {
                    Method method = result[i];
                    if (!method.getDeclaringClass().equals(clz)) {
                        result[i] = null; }
                    else {
                        try {
                            method = MethodFinder.findAccessibleMethod(method);
                            Class<?> type = method.getDeclaringClass();
                            result[i] = type.equals(clz) || type.isInterface()
                                    ? method
                                    : null; }
                        catch (NoSuchMethodException exception) {
                            }
                    }
                }
                declaredMethodCache.put(clz, result);
            }
            return result;
        }
    }

    private static Method internalFindMethod(Class<?> start, String methodName,
                                                 int argCount, Class args[]) {
        Method method = null;

        for (Class<?> cl = start; cl != null; cl = cl.getSuperclass()) {
            Method methods[] = getPublicDeclaredMethods(cl);
            for (int i = 0; i < methods.length; i++) {
                method = methods[i];
                if (method == null) {
                    continue;
                }

                if (method.getName().equals(methodName)) {
                    Type[] params = method.getGenericParameterTypes();
                    if (params.length == argCount) {
                        if (args != null) {
                            boolean different = false;
                            if (argCount > 0) {
                                for (int j = 0; j < argCount; j++) {
                                    if (TypeResolver.erase(TypeResolver.resolveInClass(start, params[j])) != args[j]) {
                                        different = true;
                                        continue;
                                    }
                                }
                                if (different) {
                                    continue;
                                }
                            }
                        }
                        return method;
                    }
                }
            }
        }
        method = null;

        Class ifcs[] = start.getInterfaces();
        for (int i = 0 ; i < ifcs.length; i++) {
            method = internalFindMethod(ifcs[i], methodName, argCount, null);
            if (method != null) {
                break;
            }
        }
        return method;
    }


    static Method findMethod(Class<?> cls, String methodName, int argCount) {
        return findMethod(cls, methodName, argCount, null);
    }


    static Method findMethod(Class<?> cls, String methodName, int argCount,
                             Class args[]) {
        if (methodName == null) {
            return null;
        }
        return internalFindMethod(cls, methodName, argCount, args);
    }


    static  boolean isSubclass(Class<?> a, Class<?> b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        for (Class<?> x = a; x != null; x = x.getSuperclass()) {
            if (x == b) {
                return true;
            }
            if (b.isInterface()) {
                Class<?>[] interfaces = x.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    if (isSubclass(interfaces[i], b)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private boolean throwsException(Method method, Class<?> exception) {
        Class exs[] = method.getExceptionTypes();
        for (int i = 0; i < exs.length; i++) {
            if (exs[i] == exception) {
                return true;
            }
        }
        return false;
    }


    static Object instantiate(Class<?> sibling, String className)
                 throws InstantiationException, IllegalAccessException,
                                                ClassNotFoundException {
        ClassLoader cl = sibling.getClassLoader();
        Class<?> cls = ClassFinder.findClass(className, cl);
        return cls.newInstance();
    }

} class GenericBeanInfo extends SimpleBeanInfo {

    private BeanDescriptor beanDescriptor;
    private EventSetDescriptor[] events;
    private int defaultEvent;
    private PropertyDescriptor[] properties;
    private int defaultProperty;
    private MethodDescriptor[] methods;
    private Reference<BeanInfo> targetBeanInfoRef;

    public GenericBeanInfo(BeanDescriptor beanDescriptor,
                EventSetDescriptor[] events, int defaultEvent,
                PropertyDescriptor[] properties, int defaultProperty,
                MethodDescriptor[] methods, BeanInfo targetBeanInfo) {
        this.beanDescriptor = beanDescriptor;
        this.events = events;
        this.defaultEvent = defaultEvent;
        this.properties = properties;
        this.defaultProperty = defaultProperty;
        this.methods = methods;
        this.targetBeanInfoRef = (targetBeanInfo != null)
                ? new SoftReference<>(targetBeanInfo)
                : null;
    }


    GenericBeanInfo(GenericBeanInfo old) {

        beanDescriptor = new BeanDescriptor(old.beanDescriptor);
        if (old.events != null) {
            int len = old.events.length;
            events = new EventSetDescriptor[len];
            for (int i = 0; i < len; i++) {
                events[i] = new EventSetDescriptor(old.events[i]);
            }
        }
        defaultEvent = old.defaultEvent;
        if (old.properties != null) {
            int len = old.properties.length;
            properties = new PropertyDescriptor[len];
            for (int i = 0; i < len; i++) {
                PropertyDescriptor oldp = old.properties[i];
                if (oldp instanceof IndexedPropertyDescriptor) {
                    properties[i] = new IndexedPropertyDescriptor(
                                        (IndexedPropertyDescriptor) oldp);
                } else {
                    properties[i] = new PropertyDescriptor(oldp);
                }
            }
        }
        defaultProperty = old.defaultProperty;
        if (old.methods != null) {
            int len = old.methods.length;
            methods = new MethodDescriptor[len];
            for (int i = 0; i < len; i++) {
                methods[i] = new MethodDescriptor(old.methods[i]);
            }
        }
        this.targetBeanInfoRef = old.targetBeanInfoRef;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return properties;
    }

    public int getDefaultPropertyIndex() {
        return defaultProperty;
    }

    public EventSetDescriptor[] getEventSetDescriptors() {
        return events;
    }

    public int getDefaultEventIndex() {
        return defaultEvent;
    }

    public MethodDescriptor[] getMethodDescriptors() {
        return methods;
    }

    public BeanDescriptor getBeanDescriptor() {
        return beanDescriptor;
    }

    public java.awt.Image getIcon(int iconKind) {
        BeanInfo targetBeanInfo = getTargetBeanInfo();
        if (targetBeanInfo != null) {
            return targetBeanInfo.getIcon(iconKind);
        }
        return super.getIcon(iconKind);
    }

    private BeanInfo getTargetBeanInfo() {
        if (this.targetBeanInfoRef == null) {
            return null;
        }
        BeanInfo targetBeanInfo = this.targetBeanInfoRef.get();
        if (targetBeanInfo == null) {
            targetBeanInfo = ThreadGroupContext.getContext().getBeanInfoFinder()
                    .find(this.beanDescriptor.getBeanClass());
            if (targetBeanInfo != null) {
                this.targetBeanInfoRef = new SoftReference<>(targetBeanInfo);
            }
        }
        return targetBeanInfo;
    }
}
