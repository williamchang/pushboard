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
    - Modified: 2011-04-10
    .
@note
    References:
    - General:
        - Nothing.
        .
    .
*/

package pushboard.data.entities;

import java.util.*;
import javax.persistence.*;
import com.google.appengine.api.datastore.Key;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key id;
    
    private String email;
    
    private String role;
    
    private String alias;
    
    private Date dateCreated;

    @ManyToOne(fetch = FetchType.LAZY)
    private Game game;

    public User(String email, String role, String alias, Date dateCreated) {
        this.email = email;
        this.role = role;
        this.alias = alias;
        this.dateCreated = dateCreated;
    }

    public Key getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
