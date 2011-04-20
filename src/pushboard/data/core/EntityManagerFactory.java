/**
@file
    EntityManagerFactory.java
@brief
    Copyright 2011 Creative Crew. All rights reserved.
@author
    William Chang
@version
    0.1
@date
    - Created: 2011-02-06
    - Modified: 2011-02-06
    .
@note
    References:
    - General:
        - Nothing.
        .
    .
*/

package pushboard.data.core;

import javax.persistence.Persistence;

public final class EntityManagerFactory {
    private static final javax.persistence.EntityManagerFactory emfInstance = Persistence.createEntityManagerFactory("transactions-optional");

    private EntityManagerFactory() {}

    public static javax.persistence.EntityManagerFactory get() {
        return emfInstance;
    }
}