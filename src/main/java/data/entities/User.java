/**
@file
    User.java
@brief
    Copyright 2011 Creative Crew. All rights reserved.
@author
    William Chang
@version
    0.1
@date
    - Created: 2011-04-05
    - Modified: 2017-02-19
    .
@note
    References:
    - General:
        - Nothing.
        .
    .
*/

package data.entities;

import java.util.Date;
import java.util.UUID;

public class User {
    private UUID id;
    private String email;
    private String role;
    private String alias;
    private Date dateCreated;
    private UUID gameId;

    public User(String email, String role, String alias, Date dateCreated) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.role = role;
        this.alias = alias;
        this.dateCreated = dateCreated;
        this.gameId = new UUID(0, 0);
    }

    public User(UUID id, String email, String role, String alias, Date dateCreated) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.alias = alias;
        this.dateCreated = dateCreated;
        this.gameId = new UUID(0, 0);
    }

    public String getAlias() {
        return alias;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getEmail() {
        return email;
    }

    public UUID getGameId() {
        return gameId;
    }

    public UUID getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
