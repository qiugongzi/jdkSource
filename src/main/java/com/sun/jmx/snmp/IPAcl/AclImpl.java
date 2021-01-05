


package com.sun.jmx.snmp.IPAcl;



import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.NotOwnerException;

import java.io.Serializable;
import java.security.acl.Permission;
import java.util.Vector;
import java.util.Enumeration;




class AclImpl extends OwnerImpl implements Acl, Serializable {
  private static final long serialVersionUID = -2250957591085270029L;

  private Vector<AclEntry> entryList = null;
  private String aclName = null;


  public AclImpl (PrincipalImpl owner, String name) {
        super(owner);
        entryList = new Vector<>();
        aclName = name;
  }


  @Override
  public void setName(Principal caller, String name)
        throws NotOwnerException {
          if (!isOwner(caller))
                throw new NotOwnerException();
          aclName = name;
  }


  @Override
  public String getName(){
        return aclName;
  }


  @Override
  public boolean addEntry(Principal caller, AclEntry entry)
        throws NotOwnerException {
          if (!isOwner(caller))
                throw new NotOwnerException();

          if (entryList.contains(entry))
                return false;


          entryList.addElement(entry);
          return true;
  }


  @Override
  public boolean removeEntry(Principal caller, AclEntry entry)
        throws NotOwnerException {
          if (!isOwner(caller))
                throw new NotOwnerException();

          return (entryList.removeElement(entry));
  }


  public void removeAll(Principal caller)
        throws NotOwnerException {
          if (!isOwner(caller))
                throw new NotOwnerException();
        entryList.removeAllElements();
  }


  @Override
  public Enumeration<Permission> getPermissions(Principal user){
        Vector<Permission> empty = new Vector<>();
        for (Enumeration<AclEntry> e = entryList.elements();e.hasMoreElements();){
          AclEntry ent = e.nextElement();
          if (ent.getPrincipal().equals(user))
                return ent.permissions();
        }
        return empty.elements();
  }


  @Override
  public Enumeration<AclEntry> entries(){
        return entryList.elements();
  }


  @Override
  public boolean checkPermission(Principal user,
                                 java.security.acl.Permission perm) {
        for (Enumeration<AclEntry> e = entryList.elements();e.hasMoreElements();){
          AclEntry ent = e.nextElement();
          if (ent.getPrincipal().equals(user))
                if (ent.checkPermission(perm)) return true;
        }
        return false;
  }


  public boolean checkPermission(Principal user, String community,
                                 java.security.acl.Permission perm) {
        for (Enumeration<AclEntry> e = entryList.elements();e.hasMoreElements();){
          AclEntryImpl ent = (AclEntryImpl) e.nextElement();
          if (ent.getPrincipal().equals(user))
                if (ent.checkPermission(perm) && ent.checkCommunity(community)) return true;
        }
        return false;
  }


  public boolean checkCommunity(String community) {
        for (Enumeration<AclEntry> e = entryList.elements();e.hasMoreElements();){
          AclEntryImpl ent = (AclEntryImpl) e.nextElement();
          if (ent.checkCommunity(community)) return true;
        }
        return false;
  }


  @Override
  public String toString(){
        return ("AclImpl: "+ getName());
  }
}
