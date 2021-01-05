

package javax.sql.rowset;

import javax.sql.*;
import java.sql.*;



 public interface Predicate {

    public boolean evaluate(RowSet rs);



    public boolean evaluate(Object value, int column) throws SQLException;


    public boolean evaluate(Object value, String columnName) throws SQLException;

}
