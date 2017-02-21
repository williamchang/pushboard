/**
@file
    BaseRepository.java
@author
    William Chang
@version
    0.1
@date
    - Created: 2017-02-19
    - Modified: 2017-02-21
    .
@note
    References:
    - General:
        - Nothing.
        .
    .
*/

package data.sqlite.repositories;

import data.interfaces.*;

/**
 * Base Repository.
 */
public class BaseRepository implements IBaseRepository {
    /**
     * Default constructor.
     */
    public BaseRepository() {
        // TODO Auto-generated constructor stub
    }

    public static java.util.Date convertToDate(String date) {
        try {
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return formatter.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getClassesFolderPath() {
        return new Object() {}.getClass().getResource("/").getPath().substring(1);
    }

    public static String getDefaultConnectionString() {
        String filePath = getClassesFolderPath() + "ApplicationDatabase.sqlite3";

        /*System.out.println();
        System.out.println("data.sqlite.repositories.BaseRepository.GetDefaultConnectionString, Classes Folder Path : " + getClassesFolderPath());
        System.out.println();*/

        if(isFileExist(filePath)) {
            return "jdbc:sqlite:" + filePath;
        } else {
            return null;
        }
    }

    public static boolean isFileExist(String filePath) {
        return new java.io.File(filePath).isFile();
    }

    public void closeConnection(java.sql.Connection sqlConnection) {
        if(sqlConnection != null) {
            try {sqlConnection.close();} catch(java.sql.SQLException e) {}
        }
    }

    public void closePreparedStatement(java.sql.PreparedStatement sqlPrepareStatement) {
        if(sqlPrepareStatement != null) {
            try {sqlPrepareStatement.close();} catch(java.sql.SQLException e) {}
            try {closeConnection(sqlPrepareStatement.getConnection());} catch(java.sql.SQLException e) {}
        }
    }

    public java.sql.Connection openConnection(String sqlConnectionString) throws Exception {
        Class.forName("org.sqlite.JDBC");
        return java.sql.DriverManager.getConnection(sqlConnectionString);
    }

    public java.sql.PreparedStatement openPrepareStatement(String sqlConnectionString, String sqlPrepareStatementQuery) throws Exception {
        Class.forName("org.sqlite.JDBC");
        return openConnection(sqlConnectionString).prepareStatement(sqlPrepareStatementQuery);
    }
}
