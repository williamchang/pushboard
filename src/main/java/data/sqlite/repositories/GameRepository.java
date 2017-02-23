/**
@file
    GameRepository.java
@author
    William Chang
@version
    0.1
@date
    - Created: 2017-02-19
    - Modified: 2017-02-23
    .
@note
    References:
    - General:
        - Nothing.
        .
    .
*/

package data.sqlite.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import data.entities.*;
import data.interfaces.*;

/**
 * System repository.
 */
public class GameRepository extends BaseRepository implements IGameRepository {
    protected String _sqlConnectionString;

    /**
     * Default constructor.
     */
    protected GameRepository() {}

    /**
     * Argument constructor.
     */
    public GameRepository(String sqlConnectionString) {
        this._sqlConnectionString = sqlConnectionString;
    }

    public Game createGame(Game g) {
        Connection sqlConnection = null;
        PreparedStatement sqlStatement = null;

        try {
            Class.forName("org.sqlite.JDBC");
            sqlConnection = DriverManager.getConnection(this._sqlConnectionString);
            sqlStatement = openPrepareStatement(this._sqlConnectionString, "INSERT INTO Game (Id, User1Id, User1Score, User2Id, User2Score, User3Id, User3Score, Board, Timer, State, UserWinnerId, DateCreated) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            sqlStatement.setString(1, g.getId().toString());
            sqlStatement.setString(2, g.getUser1Id().toString());
            sqlStatement.setInt(3, g.getUser1Score());
            sqlStatement.setString(4, g.getUser2Id().toString());
            sqlStatement.setInt(5, g.getUser2Score());
            sqlStatement.setString(6, g.getUser3Id().toString());
            sqlStatement.setInt(7, g.getUser3Score());
            sqlStatement.setString(8, g.getBoard());
            sqlStatement.setInt(9, g.getTimer());
            sqlStatement.setInt(10, g.getState());
            sqlStatement.setString(11, g.getUserWinnerId().toString());
            sqlStatement.setDate(12, new java.sql.Date(g.getDateCreated().getTime()));
            int numRowsAffected = sqlStatement.executeUpdate();
            if(numRowsAffected <= 0) {
                return null;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(sqlConnection != null) try {sqlConnection.close();} catch(Exception ex) {}
            if(sqlStatement != null) try {sqlStatement.close();} catch(Exception ex) {}
        }
        return g;
    }

    public User createUser(User u) {
        Connection sqlConnection = null;
        PreparedStatement sqlStatement = null;

        try {
            Class.forName("org.sqlite.JDBC");
            sqlConnection = DriverManager.getConnection(this._sqlConnectionString);
            sqlStatement = sqlConnection.prepareStatement("INSERT INTO User (Id, Email, Role, Alias, GameId, DateCreated) VALUES (?, ?, ?, ?, ?, ?);");
            sqlStatement.setString(1, u.getId().toString());
            sqlStatement.setString(2, u.getEmail());
            sqlStatement.setString(3, u.getRole());
            sqlStatement.setString(4, u.getAlias());
            sqlStatement.setString(5, u.getGameId().toString());
            sqlStatement.setDate(6, new java.sql.Date(u.getDateCreated().getTime()));
            int numRowsAffected = sqlStatement.executeUpdate();
            if(numRowsAffected <= 0) {
                return null;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(sqlConnection != null) try {sqlConnection.close();} catch(Exception ex) {}
            if(sqlStatement != null) try {sqlStatement.close();} catch(Exception ex) {}
        }
        return u;
    }

    public boolean deleteGame(UUID id) {
        Connection sqlConnection = null;
        PreparedStatement sqlStatement = null;

        try {
            Class.forName("org.sqlite.JDBC");
            sqlConnection = DriverManager.getConnection(this._sqlConnectionString);
            sqlStatement = sqlConnection.prepareStatement("DELETE FROM Game WHERE Id = ?;");
            sqlStatement.setString(1, id.toString());
            if(sqlStatement.executeUpdate() <= 0) {
                return false;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(sqlConnection != null) try {sqlConnection.close();} catch(Exception ex) {}
            if(sqlStatement != null) try {sqlStatement.close();} catch(Exception ex) {}
        }
        return true;
    }

    public boolean deleteGameByUser1Id(UUID user1Id) {
        Connection sqlConnection = null;
        PreparedStatement sqlStatement = null;

        try {
            Class.forName("org.sqlite.JDBC");
            sqlConnection = DriverManager.getConnection(this._sqlConnectionString);
            sqlStatement = sqlConnection.prepareStatement("DELETE FROM Game WHERE User1Id = ?;");
            sqlStatement.setString(1, user1Id.toString());
            if(sqlStatement.executeUpdate() <= 0) {
                return false;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(sqlConnection != null) try {sqlConnection.close();} catch(Exception ex) {}
            if(sqlStatement != null) try {sqlStatement.close();} catch(Exception ex) {}
        }
        return true;
    }

    public boolean deleteUser(UUID id) {
        Connection sqlConnection = null;
        PreparedStatement sqlStatement = null;

        try {
            Class.forName("org.sqlite.JDBC");
            sqlConnection = DriverManager.getConnection(this._sqlConnectionString);
            sqlStatement = sqlConnection.prepareStatement("DELETE FROM User WHERE Id = ?;");
            sqlStatement.setString(1, id.toString());
            if(sqlStatement.executeUpdate() <= 0) {
                return false;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(sqlConnection != null) try {sqlConnection.close();} catch(Exception ex) {}
            if(sqlStatement != null) try {sqlStatement.close();} catch(Exception ex) {}
        }
        return true;
    }

    public Game getGame(UUID id) {
        Connection sqlConnection = null;
        PreparedStatement sqlStatement = null;
        ResultSet sqlResultSet = null;

        try {
            Class.forName("org.sqlite.JDBC");
            sqlConnection = DriverManager.getConnection(this._sqlConnectionString);
            sqlStatement = sqlConnection.prepareStatement("SELECT * FROM Game WHERE Id = ?;");
            sqlStatement.setString(1, id.toString());
            sqlResultSet = sqlStatement.executeQuery();
            if(sqlResultSet.next()) {
                return new Game(
                    UUID.fromString(sqlResultSet.getString("Id")),
                    getUser(UUID.fromString(sqlResultSet.getString("User1Id"))),
                    sqlResultSet.getInt("User1Score"),
                    getUser(UUID.fromString(sqlResultSet.getString("User2Id"))),
                    sqlResultSet.getInt("User2Score"),
                    getUser(UUID.fromString(sqlResultSet.getString("User3Id"))),
                    sqlResultSet.getInt("User3Score"),
                    sqlResultSet.getString("Board"),
                    sqlResultSet.getInt("Timer"),
                    sqlResultSet.getInt("State"),
                    UUID.fromString(sqlResultSet.getString("UserWinnerId")),
                    sqlResultSet.getDate("DateCreated")
                );
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(sqlConnection != null) try {sqlConnection.close();} catch(Exception ex) {}
            if(sqlStatement != null) try {sqlStatement.close();} catch(Exception ex) {}
            if(sqlResultSet != null) try {sqlResultSet.close();} catch(Exception ex) {}
        }
        return null;
    }

    public Collection<Game> getGames() {
        Connection sqlConnection = null;
        PreparedStatement sqlStatement = null;
        ResultSet sqlResultSet = null;

        try {
            Class.forName("org.sqlite.JDBC");
            sqlConnection = DriverManager.getConnection(this._sqlConnectionString);
            sqlStatement = sqlConnection.prepareStatement("SELECT * FROM Game;");
            sqlResultSet = sqlStatement.executeQuery();
            Collection<Game> objs1 = new ArrayList<Game>();
            while(sqlResultSet.next()) {
                objs1.add(new Game(
                    UUID.fromString(sqlResultSet.getString("Id")),
                    getUser(UUID.fromString(sqlResultSet.getString("User1Id"))),
                    sqlResultSet.getInt("User1Score"),
                    getUser(UUID.fromString(sqlResultSet.getString("User2Id"))),
                    sqlResultSet.getInt("User2Score"),
                    getUser(UUID.fromString(sqlResultSet.getString("User3Id"))),
                    sqlResultSet.getInt("User3Score"),
                    sqlResultSet.getString("Board"),
                    sqlResultSet.getInt("Timer"),
                    sqlResultSet.getInt("State"),
                    UUID.fromString(sqlResultSet.getString("UserWinnerId")),
                    sqlResultSet.getDate("DateCreated")
                ));
            }
            return objs1;
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(sqlConnection != null) try {sqlConnection.close();} catch(Exception ex) {}
            if(sqlStatement != null) try {sqlStatement.close();} catch(Exception ex) {}
            if(sqlResultSet != null) try {sqlResultSet.close();} catch(Exception ex) {}
        }
        return new ArrayList<Game>();
    }

    public User getUser(String alias) {
        Connection sqlConnection = null;
        PreparedStatement sqlStatement = null;
        ResultSet sqlResultSet = null;

        try {
            Class.forName("org.sqlite.JDBC");
            sqlConnection = DriverManager.getConnection(this._sqlConnectionString);
            sqlStatement = sqlConnection.prepareStatement("SELECT * FROM User WHERE Alias = ?;");
            sqlStatement.setString(1, alias);
            sqlResultSet = sqlStatement.executeQuery();
            if(sqlResultSet.next()) {
                return new User(
                    UUID.fromString(sqlResultSet.getString("Id")),
                    sqlResultSet.getString("Email"),
                    sqlResultSet.getString("Role"),
                    sqlResultSet.getString("Alias"),
                    sqlResultSet.getDate("DateCreated")
                );
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(sqlConnection != null) try {sqlConnection.close();} catch(Exception ex) {}
            if(sqlStatement != null) try {sqlStatement.close();} catch(Exception ex) {}
            if(sqlResultSet != null) try {sqlResultSet.close();} catch(Exception ex) {}
        }
        return null;
    }

    public User getUser(UUID id) {
        Connection sqlConnection = null;
        PreparedStatement sqlStatement = null;
        ResultSet sqlResultSet = null;

        try {
            Class.forName("org.sqlite.JDBC");
            sqlConnection = DriverManager.getConnection(this._sqlConnectionString);
            sqlStatement = sqlConnection.prepareStatement("SELECT * FROM User WHERE Id = ?;");
            sqlStatement.setString(1, id.toString());
            sqlResultSet = sqlStatement.executeQuery();
            if(sqlResultSet.next()) {
                return new User(
                    UUID.fromString(sqlResultSet.getString("Id")),
                    sqlResultSet.getString("Email"),
                    sqlResultSet.getString("Role"),
                    sqlResultSet.getString("Alias"),
                    sqlResultSet.getDate("DateCreated")
                );
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(sqlConnection != null) try {sqlConnection.close();} catch(Exception ex) {}
            if(sqlStatement != null) try {sqlStatement.close();} catch(Exception ex) {}
            if(sqlResultSet != null) try {sqlResultSet.close();} catch(Exception ex) {}
        }
        return null;
    }

    public Game setGame(Game g) {
        Connection sqlConnection = null;
        PreparedStatement sqlStatement = null;

        try {
            Class.forName("org.sqlite.JDBC");
            sqlConnection = DriverManager.getConnection(this._sqlConnectionString);
            sqlStatement = sqlConnection.prepareStatement("UPDATE Game SET User1Id = ?, User1Score = ?, User2Id = ?, User2Score = ?, User3Id = ?, User3Score = ?, Board = ?, Timer = ?, State = ? WHERE Id = ?;");
            sqlStatement.setString(1, g.getUser1Id().toString());
            sqlStatement.setInt(2, g.getUser1Score());
            sqlStatement.setString(3, g.getUser2Id().toString());
            sqlStatement.setInt(4, g.getUser2Score());
            sqlStatement.setString(5, g.getUser3Id().toString());
            sqlStatement.setInt(6, g.getUser3Score());
            sqlStatement.setString(7, g.getBoard());
            sqlStatement.setInt(8, g.getTimer());
            sqlStatement.setInt(9, g.getState());
            sqlStatement.setString(10, g.getId().toString());
            int numRowsAffected = sqlStatement.executeUpdate();
            if(numRowsAffected <= 0) {
                return null;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(sqlConnection != null) try {sqlConnection.close();} catch(Exception ex) {}
            if(sqlStatement != null) try {sqlStatement.close();} catch(Exception ex) {}
        }
        return g;
    }
}
