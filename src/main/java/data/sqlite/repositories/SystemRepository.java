/**
@file
    SystemRepository.java
@author
    William Chang
@version
    0.1
@date
    - Created: 2017-02-05
    - Modified: 2017-02-11
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
 * System repository.
 */
public class SystemRepository extends BaseRepository implements ISystemRepository {
    protected String _sqlConnectionString;

    /**
     * Default constructor.
     */
    protected SystemRepository() {}

    /**
     * Argument constructor.
     */
    public SystemRepository(String sqlConnectionString) {
        this._sqlConnectionString = sqlConnectionString;
    }
}
