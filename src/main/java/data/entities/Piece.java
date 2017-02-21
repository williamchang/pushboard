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

import java.util.UUID;

public class Piece {
    private UUID id;
    private int numIndex;
    private String numPlayerId;
    private int numValue;
    private boolean boolVisible;

    public Piece(int index, String playerId, int value, boolean boolVisible) {
        this.id = UUID.randomUUID();
        this.numIndex = index;
        this.numPlayerId = playerId;
        this.numValue = value;
        this.boolVisible = boolVisible;
    }

    public UUID getId() {
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

    public boolean isBoolVisible() {
        return boolVisible;
    }

    public void setBoolVisible(boolean boolVisible) {
        this.boolVisible = boolVisible;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setNumIndex(int numIndex) {
        this.numIndex = numIndex;
    }

    public void setNumPlayerId(String numPlayerId) {
        this.numPlayerId = numPlayerId;
    }

    public void setNumValue(int numValue) {
        this.numValue = numValue;
    }
}
