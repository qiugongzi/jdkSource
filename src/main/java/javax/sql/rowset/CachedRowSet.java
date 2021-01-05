

package javax.sql.rowset;

import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import java.io.*;
import java.math.*;
import java.util.*;

import javax.sql.rowset.spi.*;



public interface CachedRowSet extends RowSet, Joinable {


    public void populate(ResultSet data) throws SQLException;


    public void execute(Connection conn) throws SQLException;


    public void acceptChanges() throws SyncProviderException;


    public void acceptChanges(Connection con) throws SyncProviderException;


    public void restoreOriginal() throws SQLException;


    public void release() throws SQLException;


    public void undoDelete() throws SQLException;


    public void undoInsert() throws SQLException;



    public void undoUpdate() throws SQLException;


    public boolean columnUpdated(int idx) throws SQLException;



    public boolean columnUpdated(String columnName) throws SQLException;


    public Collection<?> toCollection() throws SQLException;


    public Collection<?> toCollection(int column) throws SQLException;


    public Collection<?> toCollection(String column) throws SQLException;


    public SyncProvider getSyncProvider() throws SQLException;


    public void setSyncProvider(String provider) throws SQLException;


    public int size();


    public void setMetaData(RowSetMetaData md) throws SQLException;


   public ResultSet getOriginal() throws SQLException;


    public ResultSet getOriginalRow() throws SQLException;


    public void setOriginalRow() throws SQLException;


    public String getTableName() throws SQLException;


   public void setTableName(String tabName) throws SQLException;


    public int[] getKeyColumns() throws SQLException;


    public void setKeyColumns(int[] keys) throws SQLException;



    public RowSet createShared() throws SQLException;


    public CachedRowSet createCopy() throws SQLException;


    public CachedRowSet createCopySchema() throws SQLException;


    public CachedRowSet createCopyNoConstraints() throws SQLException;


    public RowSetWarning getRowSetWarnings() throws SQLException;


    public boolean getShowDeleted() throws SQLException;


    public void setShowDeleted(boolean b) throws SQLException;


    public void commit() throws SQLException;


    public void rollback() throws SQLException;


    public void rollback(Savepoint s) throws SQLException;


    @Deprecated
    public static final boolean COMMIT_ON_ACCEPT_CHANGES = true;


    public void rowSetPopulated(RowSetEvent event, int numRows) throws SQLException;


    public void populate(ResultSet rs, int startRow) throws SQLException;


    public void setPageSize(int size) throws SQLException;


    public int getPageSize();


    public boolean nextPage() throws SQLException;


    public boolean previousPage() throws SQLException;

}
