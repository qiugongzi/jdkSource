

package com.sun.jmx.remote.security;

import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.Permission;
import java.security.Principal;
import java.security.PrivilegedAction;
import javax.security.auth.Subject;

import javax.management.remote.SubjectDelegationPermission;

import java.util.*;

public class SubjectDelegator {

    public AccessControlContext
        delegatedContext(AccessControlContext authenticatedACC,
                         Subject delegatedSubject,
                         boolean removeCallerContext)
            throws SecurityException {

        if (System.getSecurityManager() != null && authenticatedACC == null) {
            throw new SecurityException("Illegal AccessControlContext: null");
        }

        Collection<Principal> ps = getSubjectPrincipals(delegatedSubject);
        final Collection<Permission> permissions = new ArrayList<>(ps.size());
        for(Principal p : ps) {
            final String pname = p.getClass().getName() + "." + p.getName();
            permissions.add(new SubjectDelegationPermission(pname));
        }
        PrivilegedAction<Void> action =
            new PrivilegedAction<Void>() {
                public Void run() {
                    for (Permission sdp : permissions) {
                        AccessController.checkPermission(sdp);
                    }
                    return null;
                }
            };
        AccessController.doPrivileged(action, authenticatedACC);

        return getDelegatedAcc(delegatedSubject, removeCallerContext);
    }

    private AccessControlContext getDelegatedAcc(Subject delegatedSubject, boolean removeCallerContext) {
        if (removeCallerContext) {
            return JMXSubjectDomainCombiner.getDomainCombinerContext(delegatedSubject);
        } else {
            return JMXSubjectDomainCombiner.getContext(delegatedSubject);
        }
    }


    public static synchronized boolean
        checkRemoveCallerContext(Subject subject) {
        try {
            for (Principal p : getSubjectPrincipals(subject)) {
                final String pname =
                    p.getClass().getName() + "." + p.getName();
                final Permission sdp =
                    new SubjectDelegationPermission(pname);
                AccessController.checkPermission(sdp);
            }
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }


    private static Collection<Principal> getSubjectPrincipals(Subject subject) {
        if (subject.isReadOnly()) {
            return subject.getPrincipals();
        }

        List<Principal> principals = Arrays.asList(subject.getPrincipals().toArray(new Principal[0]));
        return Collections.unmodifiableList(principals);
    }
}
