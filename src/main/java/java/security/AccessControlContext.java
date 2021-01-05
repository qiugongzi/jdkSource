

package java.security;

import java.util.ArrayList;
import java.util.List;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;




public final class AccessControlContext {

    private ProtectionDomain context[];
    private boolean isPrivileged;
    private boolean isAuthorized = false;

    private AccessControlContext privilegedContext;

    private DomainCombiner combiner = null;

    private Permission permissions[];
    private AccessControlContext parent;
    private boolean isWrapped;

    private boolean isLimited;
    private ProtectionDomain limitedContext[];

    private static boolean debugInit = false;
    private static Debug debug = null;

    static Debug getDebug()
    {
        if (debugInit)
            return debug;
        else {
            if (Policy.isSet()) {
                debug = Debug.getInstance("access");
                debugInit = true;
            }
            return debug;
        }
    }


    public AccessControlContext(ProtectionDomain context[])
    {
        if (context.length == 0) {
            this.context = null;
        } else if (context.length == 1) {
            if (context[0] != null) {
                this.context = context.clone();
            } else {
                this.context = null;
            }
        } else {
            List<ProtectionDomain> v = new ArrayList<>(context.length);
            for (int i =0; i< context.length; i++) {
                if ((context[i] != null) &&  (!v.contains(context[i])))
                    v.add(context[i]);
            }
            if (!v.isEmpty()) {
                this.context = new ProtectionDomain[v.size()];
                this.context = v.toArray(this.context);
            }
        }
    }


    public AccessControlContext(AccessControlContext acc,
                                DomainCombiner combiner) {

        this(acc, combiner, false);
    }


    AccessControlContext(AccessControlContext acc,
                        DomainCombiner combiner,
                        boolean preauthorized) {
        if (!preauthorized) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPermission(SecurityConstants.CREATE_ACC_PERMISSION);
                this.isAuthorized = true;
            }
        } else {
            this.isAuthorized = true;
        }

        this.context = acc.context;

        this.combiner = combiner;
    }


    AccessControlContext(ProtectionDomain caller, DomainCombiner combiner,
        AccessControlContext parent, AccessControlContext context,
        Permission[] perms)
    {

        ProtectionDomain[] callerPDs = null;
        if (caller != null) {
             callerPDs = new ProtectionDomain[] { caller };
        }
        if (context != null) {
            if (combiner != null) {
                this.context = combiner.combine(callerPDs, context.context);
            } else {
                this.context = combine(callerPDs, context.context);
            }
        } else {

            if (combiner != null) {
                this.context = combiner.combine(callerPDs, null);
            } else {
                this.context = combine(callerPDs, null);
            }
        }
        this.combiner = combiner;

        Permission[] tmp = null;
        if (perms != null) {
            tmp = new Permission[perms.length];
            for (int i=0; i < perms.length; i++) {
                if (perms[i] == null) {
                    throw new NullPointerException("permission can't be null");
                }


                if (perms[i].getClass() == AllPermission.class) {
                    parent = null;
                }
                tmp[i] = perms[i];
            }
        }


        if (parent != null) {
            this.limitedContext = combine(parent.context, parent.limitedContext);
            this.isLimited = true;
            this.isWrapped = true;
            this.permissions = tmp;
            this.parent = parent;
            this.privilegedContext = context; }
        this.isAuthorized = true;
    }




    AccessControlContext(ProtectionDomain context[],
                         boolean isPrivileged)
    {
        this.context = context;
        this.isPrivileged = isPrivileged;
        this.isAuthorized = true;
    }


    AccessControlContext(ProtectionDomain[] context,
                         AccessControlContext privilegedContext)
    {
        this.context = context;
        this.privilegedContext = privilegedContext;
        this.isPrivileged = true;
    }


    ProtectionDomain[] getContext() {
        return context;
    }


    boolean isPrivileged()
    {
        return isPrivileged;
    }


    DomainCombiner getAssignedCombiner() {
        AccessControlContext acc;
        if (isPrivileged) {
            acc = privilegedContext;
        } else {
            acc = AccessController.getInheritedAccessControlContext();
        }
        if (acc != null) {
            return acc.combiner;
        }
        return null;
    }


    public DomainCombiner getDomainCombiner() {

        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SecurityConstants.GET_COMBINER_PERMISSION);
        }
        return getCombiner();
    }


    DomainCombiner getCombiner() {
        return combiner;
    }

    boolean isAuthorized() {
        return isAuthorized;
    }


    public void checkPermission(Permission perm)
        throws AccessControlException
    {
        boolean dumpDebug = false;

        if (perm == null) {
            throw new NullPointerException("permission can't be null");
        }
        if (getDebug() != null) {
            dumpDebug = !Debug.isOn("codebase=");
            if (!dumpDebug) {
                for (int i = 0; context != null && i < context.length; i++) {
                    if (context[i].getCodeSource() != null &&
                        context[i].getCodeSource().getLocation() != null &&
                        Debug.isOn("codebase=" + context[i].getCodeSource().getLocation().toString())) {
                        dumpDebug = true;
                        break;
                    }
                }
            }

            dumpDebug &= !Debug.isOn("permission=") ||
                Debug.isOn("permission=" + perm.getClass().getCanonicalName());

            if (dumpDebug && Debug.isOn("stack")) {
                Thread.dumpStack();
            }

            if (dumpDebug && Debug.isOn("domain")) {
                if (context == null) {
                    debug.println("domain (context is null)");
                } else {
                    for (int i=0; i< context.length; i++) {
                        debug.println("domain "+i+" "+context[i]);
                    }
                }
            }
        }





        if (context == null) {
            checkPermission2(perm);
            return;
        }

        for (int i=0; i< context.length; i++) {
            if (context[i] != null &&  !context[i].implies(perm)) {
                if (dumpDebug) {
                    debug.println("access denied " + perm);
                }

                if (Debug.isOn("failure") && debug != null) {
                    if (!dumpDebug) {
                        debug.println("access denied " + perm);
                    }
                    Thread.dumpStack();
                    final ProtectionDomain pd = context[i];
                    final Debug db = debug;
                    AccessController.doPrivileged (new PrivilegedAction<Void>() {
                        public Void run() {
                            db.println("domain that failed "+pd);
                            return null;
                        }
                    });
                }
                throw new AccessControlException("access denied "+perm, perm);
            }
        }

        if (dumpDebug) {
            debug.println("access allowed "+perm);
        }

        checkPermission2(perm);
    }


    private void checkPermission2(Permission perm) {
        if (!isLimited) {
            return;
        }


        if (privilegedContext != null) {
            privilegedContext.checkPermission2(perm);
        }


        if (isWrapped) {
            return;
        }


        if (permissions != null) {
            Class<?> permClass = perm.getClass();
            for (int i=0; i < permissions.length; i++) {
                Permission limit = permissions[i];
                if (limit.getClass().equals(permClass) && limit.implies(perm)) {
                    return;
                }
            }
        }


        if (parent != null) {

            if (permissions == null) {
                parent.checkPermission2(perm);
            } else {
                parent.checkPermission(perm);
            }
        }
    }


    AccessControlContext optimize() {
        AccessControlContext acc;
        DomainCombiner combiner = null;
        AccessControlContext parent = null;
        Permission[] permissions = null;

        if (isPrivileged) {
            acc = privilegedContext;
            if (acc != null) {

                if (acc.isWrapped) {
                    permissions = acc.permissions;
                    parent = acc.parent;
                }
            }
        } else {
            acc = AccessController.getInheritedAccessControlContext();
            if (acc != null) {

                if (acc.isLimited) {
                    parent = acc;
                }
            }
        }

        boolean skipStack = (context == null);

        boolean skipAssigned = (acc == null || acc.context == null);
        ProtectionDomain[] assigned = (skipAssigned) ? null : acc.context;
        ProtectionDomain[] pd;

        boolean skipLimited = ((acc == null || !acc.isWrapped) && parent == null);

        if (acc != null && acc.combiner != null) {
            if (getDebug() != null) {
                debug.println("AccessControlContext invoking the Combiner");
            }

            combiner = acc.combiner;
            pd = combiner.combine(context, assigned);
        } else {
            if (skipStack) {
                if (skipAssigned) {
                    calculateFields(acc, parent, permissions);
                    return this;
                } else if (skipLimited) {
                    return acc;
                }
            } else if (assigned != null) {
                if (skipLimited) {
                    if (context.length == 1 && context[0] == assigned[0]) {
                        return acc;
                    }
                }
            }

            pd = combine(context, assigned);
            if (skipLimited && !skipAssigned && pd == assigned) {
                return acc;
            } else if (skipAssigned && pd == context) {
                calculateFields(acc, parent, permissions);
                return this;
            }
        }

        this.context = pd;
        this.combiner = combiner;
        this.isPrivileged = false;

        calculateFields(acc, parent, permissions);
        return this;
    }



    private static ProtectionDomain[] combine(ProtectionDomain[]current,
        ProtectionDomain[] assigned) {

        boolean skipStack = (current == null);

        boolean skipAssigned = (assigned == null);

        int slen = (skipStack) ? 0 : current.length;

        if (skipAssigned && slen <= 2) {
            return current;
        }

        int n = (skipAssigned) ? 0 : assigned.length;

        ProtectionDomain pd[] = new ProtectionDomain[slen + n];

        if (!skipAssigned) {
            System.arraycopy(assigned, 0, pd, 0, n);
        }

        outer:
        for (int i = 0; i < slen; i++) {
            ProtectionDomain sd = current[i];
            if (sd != null) {
                for (int j = 0; j < n; j++) {
                    if (sd == pd[j]) {
                        continue outer;
                    }
                }
                pd[n++] = sd;
            }
        }

        if (n != pd.length) {
            if (!skipAssigned && n == assigned.length) {
                return assigned;
            } else if (skipAssigned && n == slen) {
                return current;
            }
            ProtectionDomain tmp[] = new ProtectionDomain[n];
            System.arraycopy(pd, 0, tmp, 0, n);
            pd = tmp;
        }

        return pd;
    }



    private void calculateFields(AccessControlContext assigned,
        AccessControlContext parent, Permission[] permissions)
    {
        ProtectionDomain[] parentLimit = null;
        ProtectionDomain[] assignedLimit = null;
        ProtectionDomain[] newLimit;

        parentLimit = (parent != null)? parent.limitedContext: null;
        assignedLimit = (assigned != null)? assigned.limitedContext: null;
        newLimit = combine(parentLimit, assignedLimit);
        if (newLimit != null) {
            if (context == null || !containsAllPDs(newLimit, context)) {
                this.limitedContext = newLimit;
                this.permissions = permissions;
                this.parent = parent;
                this.isLimited = true;
            }
        }
    }



    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (! (obj instanceof AccessControlContext))
            return false;

        AccessControlContext that = (AccessControlContext) obj;

        if (!equalContext(that))
            return false;

        if (!equalLimitedContext(that))
            return false;

        return true;
    }


    private boolean equalContext(AccessControlContext that) {
        if (!equalPDs(this.context, that.context))
            return false;

        if (this.combiner == null && that.combiner != null)
            return false;

        if (this.combiner != null && !this.combiner.equals(that.combiner))
            return false;

        return true;
    }

    private boolean equalPDs(ProtectionDomain[] a, ProtectionDomain[] b) {
        if (a == null) {
            return (b == null);
        }

        if (b == null)
            return false;

        if (!(containsAllPDs(a, b) && containsAllPDs(b, a)))
            return false;

        return true;
    }


    private boolean equalLimitedContext(AccessControlContext that) {
        if (that == null)
            return false;


        if (!this.isLimited && !that.isLimited)
            return true;


         if (!(this.isLimited && that.isLimited))
             return false;


        if ((this.isWrapped && !that.isWrapped) ||
            (!this.isWrapped && that.isWrapped)) {
            return false;
        }

        if (this.permissions == null && that.permissions != null)
            return false;

        if (this.permissions != null && that.permissions == null)
            return false;

        if (!(this.containsAllLimits(that) && that.containsAllLimits(this)))
            return false;


        AccessControlContext thisNextPC = getNextPC(this);
        AccessControlContext thatNextPC = getNextPC(that);


        if (thisNextPC == null && thatNextPC != null && thatNextPC.isLimited)
            return false;

        if (thisNextPC != null && !thisNextPC.equalLimitedContext(thatNextPC))
            return false;

        if (this.parent == null && that.parent != null)
            return false;

        if (this.parent != null && !this.parent.equals(that.parent))
            return false;

        return true;
    }


    private static AccessControlContext getNextPC(AccessControlContext acc) {
        while (acc != null && acc.privilegedContext != null) {
            acc = acc.privilegedContext;
            if (!acc.isWrapped)
                return acc;
        }
        return null;
    }

    private static boolean containsAllPDs(ProtectionDomain[] thisContext,
        ProtectionDomain[] thatContext) {
        boolean match = false;

        ProtectionDomain thisPd;
        for (int i = 0; i < thisContext.length; i++) {
            match = false;
            if ((thisPd = thisContext[i]) == null) {
                for (int j = 0; (j < thatContext.length) && !match; j++) {
                    match = (thatContext[j] == null);
                }
            } else {
                Class<?> thisPdClass = thisPd.getClass();
                ProtectionDomain thatPd;
                for (int j = 0; (j < thatContext.length) && !match; j++) {
                    thatPd = thatContext[j];

                    match = (thatPd != null &&
                        thisPdClass == thatPd.getClass() && thisPd.equals(thatPd));
                }
            }
            if (!match) return false;
        }
        return match;
    }

    private boolean containsAllLimits(AccessControlContext that) {
        boolean match = false;
        Permission thisPerm;

        if (this.permissions == null && that.permissions == null)
            return true;

        for (int i = 0; i < this.permissions.length; i++) {
            Permission limit = this.permissions[i];
            Class <?> limitClass = limit.getClass();
            match = false;
            for (int j = 0; (j < that.permissions.length) && !match; j++) {
                Permission perm = that.permissions[j];
                match = (limitClass.equals(perm.getClass()) &&
                    limit.equals(perm));
            }
            if (!match) return false;
        }
        return match;
    }




    public int hashCode() {
        int hashCode = 0;

        if (context == null)
            return hashCode;

        for (int i =0; i < context.length; i++) {
            if (context[i] != null)
                hashCode ^= context[i].hashCode();
        }

        return hashCode;
    }
}
