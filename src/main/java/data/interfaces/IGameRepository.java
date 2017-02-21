/**
@file
    IGameRepository.java
@author
    William Chang
@version
    0.1
@date
    - Created: 2017-02-19
    - Modified: 2017-02-20
    .
@note
    References:
    - General:
        - Nothing.
        .
    .
*/

package data.interfaces;

import java.util.Collection;
import java.util.UUID;
import data.entities.*;

/**
 * Game repository interface.
 * */
public interface IGameRepository {
    /**
     * Create (INSERT) game.
     */
    Game createGame(Game g);

    /**
     * Create (INSERT) user.
     */
    User createUser(User u);

    /**
     * Delete (DELETE) game permanently.
     */
    boolean deleteGame(UUID id);

    /**
     * Delete (DELETE) game permanently by user1 id.
     */
    boolean deleteGameByUser1Id(UUID user1Id);

    /**
     * Delete (DELETE) game permanently.
     */
    boolean deleteUser(UUID id);

    /**
     * Get (SELECT) game.
     */
    Game getGame(UUID id);

    /**
     * Get (SELECT) one or more games.
     */
    Collection<Game> getGames();

    /**
     * Get (SELECT) user by alias.
     */
    User getUser(String alias);

    /**
     * Get (SELECT) user by id.
     */
    User getUser(UUID id);

    /**
     * Set (UPDATE) game.
     */
    Game setGame(Game g);
}
