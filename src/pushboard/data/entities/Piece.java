/**
@file
    Piece.java
@brief
    Copyright 2011 Creative Crew. All rights reserved.
@author
    William Chang
@version
    0.1
@date
    - Created: 2011-04-09
    - Modified: 2011-04-11
    .
@note
    References:
    - General:
        - Nothing.
        .
    .
*/

package pushboard.data.entities;

import javax.persistence.*;
import com.google.appengine.api.datastore.Key;

@Entity
public class Piece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key id;

    private int numIndex;

    private String numPlayerId;

    private int numValue;

    private boolean boolVisible;

    public Piece(int index, String playerId, int value, boolean boolVisible) {
        this.numIndex = index;
        this.numPlayerId = playerId;
        this.numValue = value;
        this.boolVisible = boolVisible;
    }

    protected Key getId() {
        return id;
    }

    public int getNumIndex() {
        return numIndex;
    }

    public String getNumPlayerId() {
        return numPlayerId;
    }

    public int getNumValue() {
        return numValue;
    }

    public boolean getBoolVisible() {
        return boolVisible;
    }
}