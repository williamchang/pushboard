/**
@file
    Game.java
@brief
    Copyright 2011 Creative Crew. All rights reserved.
@author
    William Chang
@version
    0.1
@date
    - Created: 2011-04-04
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Game {
    public enum ChannelMessageType {
        UNKNOWN, // 0
        GENERIC,
        MOVE,
        RESET
    }

    public enum State {
        ENDED, // 0
        READY,
        RUNNING,
        WAITING
    }

    private UUID id;
    private Collection<User> users;
    private User user1;
    private UUID user1Id;
    private int user1Score;
    private User user2;
    private UUID user2Id;
    private int user2Score;
    private User user3;
    private UUID user3Id;
    private int user3Score;
    private String board;
    private int timer;
    private int state;
    private UUID userWinnerId;

    private Date dateCreated;

    public Game(UUID id, User user1, int user1Score, User user2, int user2Score, User user3, int user3Score, String board, int timer, int state, UUID userWinnerId, Date dateCreated) {
        this.id = id;
        this.users = new ArrayList<User>();
        this.user1 = user1;
        this.user1Id = (user1 == null ? new UUID(0, 0) : user1.getId());
        this.user1Score = user1Score;
        this.user2 = user2;
        this.user2Id = (user2 == null ? new UUID(0, 0) : user2.getId());
        this.user2Score = user2Score;
        this.user3 = user3;
        this.user3Id = (user3 == null ? new UUID(0, 0) : user3.getId());
        this.user3Score = user3Score;
        this.board = board;
        this.timer = timer;
        this.state = state;
        this.userWinnerId = userWinnerId;
        this.dateCreated = dateCreated;
    }

    public Game(User user1, String board, int timer, Date dateCreated) {
        this.id = UUID.randomUUID();
        this.users = new ArrayList<User>();
        this.user1 = user1;
        this.user1Id = (user1 == null ? new UUID(0, 0) : user1.getId());
        this.user1Score = 0;
        this.user2 = null;
        this.user2Id = new UUID(0, 0);
        this.user2Score = 0;
        this.user3 = null;
        this.user3Id = new UUID(0, 0);
        this.user3Score = 0;
        this.board = board;
        this.timer = timer;
        this.state = -1;
        this.userWinnerId = new UUID(0, 0);
        this.dateCreated = dateCreated;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public static String createPieces(int numPlayers, int maxLength) {
        Gson json = new Gson();
        Random randomGenerator = new Random();
        Collection<Piece> pieces = new ArrayList<Piece>();

        for(int index = 0;index < maxLength;index += 1) {
            pieces.add(new Piece(index, Integer.toString(randomGenerator.nextInt(numPlayers) + 1), 1, true));
        }
        return json.toJson(pieces);
    }

    public String getBoard() {
        return board;
    }

    public String getChannelKey(String userId) {
        return userId + id;
    }

    public String getChannelMessage(ChannelMessageType type) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("user1Alias", user1 == null ? null : user1.getAlias());
        jsonObject.addProperty("user1Score", user1Score);
        jsonObject.addProperty("user2Alias", user2 == null ? null : user2.getAlias());
        jsonObject.addProperty("user2Score", user2Score);
        jsonObject.addProperty("user3Alias", user3 == null ? null : user3.getAlias());
        jsonObject.addProperty("user3Score", user3Score);
        switch(type) {
            default:
            case UNKNOWN:
            case GENERIC:
            case RESET:
                jsonObject.add("board", jsonParser.parse(board).getAsJsonArray());
                jsonObject.addProperty("timer", timer);
                jsonObject.addProperty("state", state);
                break;
            case MOVE:
                jsonObject.add("board", jsonParser.parse(board).getAsJsonArray());
                break;
        }
        return jsonObject.toString();
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public UUID getId() {
        return id;
    }

    public int getState() {
        return state;
    }

    public int getTimer() {
        return timer;
    }

    public User getUser1() {
        return user1;
    }

    public UUID getUser1Id() {
        return user1Id;
    }

    public int getUser1Score() {
        return user1Score;
    }

    public User getUser2() {
        return user2;
    }

    public UUID getUser2Id() {
        return user2Id;
    }

    public int getUser2Score() {
        return user1Score;
    }

    public User getUser3() {
        return user3;
    }

    public UUID getUser3Id() {
        return user3Id;
    }

    public int getUser3Score() {
        return user3Score;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public UUID getUserWinnerId() {
        return userWinnerId;
    }

    private void sendUpdateToClient(UUID userId, ChannelMessageType type) {
        if(userId != null) {
            web.websockets.GameWebSocket.send(userId, getChannelMessage(type));
        }
    }

    public void sendUpdateToClients(ChannelMessageType type) {
        sendUpdateToClient(user1Id, type);
        sendUpdateToClient(user2Id, type);
        sendUpdateToClient(user3Id, type);
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean setGame(String board, int timer, int state) {
        setBoard(board);
        setTimer(timer);
        setState(state);
        sendUpdateToClients(ChannelMessageType.RESET);
        return true;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean setMove(String userId, int userScore, String move) {
        boolean pass = false;

        if(userId.equals(getUser1Id())) {
            setUser1Score(userScore);
            pass = true;
        } else if(userId.equals(getUser2Id())) {
            setUser2Score(userScore);
            pass = true;
        } else if(userId.equals(getUser3Id())) {
            setUser3Score(userScore);
            pass = true;
        }
        if(pass == true) {
            // Modify JSON string.
            String board = getBoard();
            String find = "\"numIndex\":" + move;
            String pairTrue = "\"boolVisible\":true";
            String pairFalse = "\"boolVisible\":false";
            int posFind = board.indexOf(find);
            int posPairBegin = -1;
            if(posFind >= 0) {
                posPairBegin = board.lastIndexOf(pairTrue, posFind);
            }
            if(posFind >= 0 && posPairBegin >= 0) {
                board = board.substring(0, posPairBegin) + pairFalse + board.substring(posPairBegin + pairTrue.length());
            }
            // Set board.
            setBoard(board);
            // Send updates to client-side.
            sendUpdateToClients(ChannelMessageType.MOVE);
            return true;
        }
        return false;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public void setUser1Id(UUID userId) {
        this.user1Id = userId;
    }

    public void setUser1Score(int score) {
        this.user1Score = score;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public void setUser2Id(UUID userId) {
        this.user2Id = userId;
    }

    public void setUser2Score(int score) {
        this.user2Score = score;
    }

    public void setUser3(User user3) {
        this.user3 = user3;
    }

    public void setUser3Id(UUID userId) {
        this.user3Id = userId;
    }

    public void setUser3Score(int score) {
        this.user3Score = score;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setUserWinnerId(UUID userWinnerId) {
        this.userWinnerId = userWinnerId;
    }
}
