

package javax.management.relation;

import javax.management.Notification;
import javax.management.ObjectName;

import java.io.InvalidObjectException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;

import java.security.AccessController;
import java.security.PrivilegedAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import static com.sun.jmx.mbeanserver.Util.cast;


@SuppressWarnings("serial")  public class RelationNotification extends Notification {

    private static final long oldSerialVersionUID = -2126464566505527147L;
    private static final long newSerialVersionUID = -6871117877523310399L;
    private static final ObjectStreamField[] oldSerialPersistentFields =
    {
        new ObjectStreamField("myNewRoleValue", ArrayList.class),
        new ObjectStreamField("myOldRoleValue", ArrayList.class),
        new ObjectStreamField("myRelId", String.class),
        new ObjectStreamField("myRelObjName", ObjectName.class),
        new ObjectStreamField("myRelTypeName", String.class),
        new ObjectStreamField("myRoleName", String.class),
        new ObjectStreamField("myUnregMBeanList", ArrayList.class)
    };
    private static final ObjectStreamField[] newSerialPersistentFields =
    {
        new ObjectStreamField("newRoleValue", List.class),
        new ObjectStreamField("oldRoleValue", List.class),
        new ObjectStreamField("relationId", String.class),
        new ObjectStreamField("relationObjName", ObjectName.class),
        new ObjectStreamField("relationTypeName", String.class),
        new ObjectStreamField("roleName", String.class),
        new ObjectStreamField("unregisterMBeanList", List.class)
    };
    private static final long serialVersionUID;

    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat = false;
    static {
        try {
            GetPropertyAction act = new GetPropertyAction("jmx.serial.form");
            String form = AccessController.doPrivileged(act);
            compat = (form != null && form.equals("1.0"));
        } catch (Exception e) {
            }
        if (compat) {
            serialPersistentFields = oldSerialPersistentFields;
            serialVersionUID = oldSerialVersionUID;
        } else {
            serialPersistentFields = newSerialPersistentFields;
            serialVersionUID = newSerialVersionUID;
        }
    }
    public static final String RELATION_BASIC_CREATION = "jmx.relation.creation.basic";

    public static final String RELATION_MBEAN_CREATION = "jmx.relation.creation.mbean";

    public static final String RELATION_BASIC_UPDATE = "jmx.relation.update.basic";

    public static final String RELATION_MBEAN_UPDATE = "jmx.relation.update.mbean";

    public static final String RELATION_BASIC_REMOVAL = "jmx.relation.removal.basic";

    public static final String RELATION_MBEAN_REMOVAL = "jmx.relation.removal.mbean";

    private String relationId = null;


    private String relationTypeName = null;


    private ObjectName relationObjName = null;


    private List<ObjectName> unregisterMBeanList = null;


    private String roleName = null;


    private List<ObjectName> oldRoleValue = null;


    private List<ObjectName> newRoleValue = null;

    public RelationNotification(String notifType,
                                Object sourceObj,
                                long sequence,
                                long timeStamp,
                                String message,
                                String id,
                                String typeName,
                                ObjectName objectName,
                                List<ObjectName> unregMBeanList)
        throws IllegalArgumentException {

        super(notifType, sourceObj, sequence, timeStamp, message);

        if (!isValidBasicStrict(notifType,sourceObj,id,typeName) || !isValidCreate(notifType)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }

        relationId = id;
        relationTypeName = typeName;
        relationObjName = safeGetObjectName(objectName);
        unregisterMBeanList = safeGetObjectNameList(unregMBeanList);
    }


    public RelationNotification(String notifType,
                                Object sourceObj,
                                long sequence,
                                long timeStamp,
                                String message,
                                String id,
                                String typeName,
                                ObjectName objectName,
                                String name,
                                List<ObjectName> newValue,
                                List<ObjectName> oldValue
                                )
            throws IllegalArgumentException {

        super(notifType, sourceObj, sequence, timeStamp, message);

        if (!isValidBasicStrict(notifType,sourceObj,id,typeName) || !isValidUpdate(notifType,name,newValue,oldValue)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }

        relationId = id;
        relationTypeName = typeName;
        relationObjName = safeGetObjectName(objectName);

        roleName = name;
        oldRoleValue = safeGetObjectNameList(oldValue);
        newRoleValue = safeGetObjectNameList(newValue);
    }

    public String getRelationId() {
        return relationId;
    }


    public String getRelationTypeName() {
        return relationTypeName;
    }


    public ObjectName getObjectName() {
        return relationObjName;
    }


    public List<ObjectName> getMBeansToUnregister() {
        List<ObjectName> result;
        if (unregisterMBeanList != null) {
            result = new ArrayList<ObjectName>(unregisterMBeanList);
        } else {
            result = Collections.emptyList();
        }
        return result;
    }


    public String getRoleName() {
        String result = null;
        if (roleName != null) {
            result = roleName;
        }
        return result;
    }


    public List<ObjectName> getOldRoleValue() {
        List<ObjectName> result;
        if (oldRoleValue != null) {
            result = new ArrayList<ObjectName>(oldRoleValue);
        } else {
            result = Collections.emptyList();
        }
        return result;
    }


    public List<ObjectName> getNewRoleValue() {
        List<ObjectName> result;
        if (newRoleValue != null) {
            result = new ArrayList<ObjectName>(newRoleValue);
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    private boolean isValidBasicStrict(String notifType, Object sourceObj, String id, String typeName){
        if (sourceObj == null) {
            return false;
        }
        return isValidBasic(notifType,sourceObj,id,typeName);
    }

    private boolean isValidBasic(String notifType, Object sourceObj, String id, String typeName){
        if (notifType == null || id == null || typeName == null) {
            return false;
        }

        if (sourceObj != null && (
            !(sourceObj instanceof RelationService) &&
            !(sourceObj instanceof ObjectName))) {
            return false;
        }

        return true;
    }

    private boolean isValidCreate(String notifType) {
        String[] validTypes= {RelationNotification.RELATION_BASIC_CREATION,
                              RelationNotification.RELATION_MBEAN_CREATION,
                              RelationNotification.RELATION_BASIC_REMOVAL,
                              RelationNotification.RELATION_MBEAN_REMOVAL};

        Set<String> ctSet = new HashSet<String>(Arrays.asList(validTypes));
        return ctSet.contains(notifType);
    }

    private boolean isValidUpdate(String notifType, String name,
                                  List<ObjectName> newValue, List<ObjectName> oldValue) {

        if (!(notifType.equals(RelationNotification.RELATION_BASIC_UPDATE)) &&
            !(notifType.equals(RelationNotification.RELATION_MBEAN_UPDATE))) {
            return false;
        }

        if (name == null || oldValue == null || newValue == null) {
            return false;
        }

        return true;
    }

    private ArrayList<ObjectName> safeGetObjectNameList(List<ObjectName> src){
        ArrayList<ObjectName> dest = null;
        if (src != null) {
            dest = new ArrayList<ObjectName>();
            for (ObjectName item : src) {
                dest.add(ObjectName.getInstance(item));
            }
        }
        return dest;
    }

    private ObjectName safeGetObjectName(ObjectName src){
        ObjectName dest = null;
        if (src != null) {
            dest = ObjectName.getInstance(src);
        }
        return dest;
    }


    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {

        String tmpRelationId, tmpRelationTypeName, tmpRoleName;

        ObjectName tmpRelationObjName;
        List<ObjectName> tmpNewRoleValue, tmpOldRoleValue, tmpUnregMBeanList;

        ObjectInputStream.GetField fields = in.readFields();

        if (compat) {
            tmpRelationId = (String)fields.get("myRelId", null);
            tmpRelationTypeName = (String)fields.get("myRelTypeName", null);
            tmpRoleName = (String)fields.get("myRoleName", null);

            tmpRelationObjName = (ObjectName)fields.get("myRelObjName", null);
            tmpNewRoleValue = cast(fields.get("myNewRoleValue", null));
            tmpOldRoleValue = cast(fields.get("myOldRoleValue", null));
            tmpUnregMBeanList = cast(fields.get("myUnregMBeanList", null));
        }
        else {
            tmpRelationId = (String)fields.get("relationId", null);
            tmpRelationTypeName = (String)fields.get("relationTypeName", null);
            tmpRoleName = (String)fields.get("roleName", null);

            tmpRelationObjName = (ObjectName)fields.get("relationObjName", null);
            tmpNewRoleValue = cast(fields.get("newRoleValue", null));
            tmpOldRoleValue = cast(fields.get("oldRoleValue", null));
            tmpUnregMBeanList = cast(fields.get("unregisterMBeanList", null));
        }

        String notifType = super.getType();
        if (!isValidBasic(notifType,super.getSource(),tmpRelationId,tmpRelationTypeName)  ||
            (!isValidCreate(notifType) &&
             !isValidUpdate(notifType,tmpRoleName,tmpNewRoleValue,tmpOldRoleValue))) {

            super.setSource(null);
            throw new InvalidObjectException("Invalid object read");
        }

        relationObjName = safeGetObjectName(tmpRelationObjName);
        newRoleValue = safeGetObjectNameList(tmpNewRoleValue);
        oldRoleValue = safeGetObjectNameList(tmpOldRoleValue);
        unregisterMBeanList = safeGetObjectNameList(tmpUnregMBeanList);

        relationId = tmpRelationId;
        relationTypeName = tmpRelationTypeName;
        roleName = tmpRoleName;
    }



    private void writeObject(ObjectOutputStream out)
            throws IOException {
      if (compat)
      {
        ObjectOutputStream.PutField fields = out.putFields();
        fields.put("myNewRoleValue", newRoleValue);
        fields.put("myOldRoleValue", oldRoleValue);
        fields.put("myRelId", relationId);
        fields.put("myRelObjName", relationObjName);
        fields.put("myRelTypeName", relationTypeName);
        fields.put("myRoleName",roleName);
        fields.put("myUnregMBeanList", unregisterMBeanList);
        out.writeFields();
      }
      else
      {
        out.defaultWriteObject();
      }
    }
}
