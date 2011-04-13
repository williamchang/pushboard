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
    - Modified: 2011-04-13
    .
@note
    References:
    - General:
        - http://json.org/java/
        .
    .
*/

package pushboard.data.entities;

import java.util.*;
import javax.persistence.*;
import com.google.appengine.api.channel.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.repackaged.org.json.*;

@Entity
public class Game {
    public enum State {
        ENDED, // 0
        READY,
        RUNNING,
        WAITING
    }
    public enum ChannelMessageType {
        UNKNOWN, // 0
        GENERIC,
        MOVE,
        RESET
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key id;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<User> users;

    private String user1;

    private int user1Score;

    private String user2;

    private int user2Score;

    private String user3;

    private int user3Score;

    @Basic
    private Text board;

    private int timer;

    private String winner;

    private int state;

    private Date dateCreated;

    public Game(String user1, String user2, String user3, String board, int timer, Date dateCreated) {
        this.users = new ArrayList<User>();
        this.user1 = user1;
        this.user1Score = 0;
        this.user2 = user2;
        this.user2Score = 0;
        this.user3 = user3;
        this.user3Score = 0;
        this.board = new Text(board);
        this.timer = timer;
        this.winner = "";
        this.state = -1;
        this.dateCreated = dateCreated;
    }

    public Key getId() {
        return id;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user) {
        this.user1 = user;
    }

    public int getUser1Score() {
        return user1Score;
    }

    public void setUser1Score(int score) {
        this.user1Score = score;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user) {
        this.user2 = user;
    }

    public int getUser2Score() {
        return user1Score;
    }

    public void setUser2Score(int score) {
        this.user2Score = score;
    }

    public String getUser3() {
        return user3;
    }

    public void setUser3(String user) {
        this.user3 = user;
    }

    public int getUser3Score() {
        return user3Score;
    }

    public void setUser3Score(int score) {
        this.user3Score = score;
    }

    public String getBoard() {
        return board.getValue();
    }

    public void setBoard(String board) {
        this.board = new Text(board);
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    
    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getChannelKey(String user) {
        return user + KeyFactory.keyToString(id);
    }

    public String getChannelMessage(ChannelMessageType type) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user1", user1);
            jsonObject.put("user1Score", user1Score);
            jsonObject.put("user2", user2);
            jsonObject.put("user2Score", user2Score);
            jsonObject.put("user3", user3);
            jsonObject.put("user3Score", user3Score);
            switch(type) {
                default:
                case UNKNOWN:
                case GENERIC:
                case RESET:
                    jsonObject.put("board", new JSONArray(board.getValue()));
                    jsonObject.put("timer", timer);
                    jsonObject.put("state", state);
                    break;
                case MOVE:
                    jsonObject.put("board", new JSONArray(board.getValue()));
                    break;
            }
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void sendUpdateToClient(String user, ChannelMessageType type) {
        if(user != null && !user.isEmpty()) {
            ChannelService svc = ChannelServiceFactory.getChannelService();
            String channelKey = getChannelKey(user);
            svc.sendMessage(new ChannelMessage(channelKey, getChannelMessage(type)));
        }
    }

    public void sendUpdateToClients(ChannelMessageType type) {
        sendUpdateToClient(user1, type);
        sendUpdateToClient(user2, type);
        sendUpdateToClient(user3, type);
    }

    public boolean setMove(String userId, int userScore, String move) {
        boolean pass = false;

        if(userId.equals(getUser1())) {
            setUser1Score(userScore);
            pass = true;
        } else if(userId.equals(getUser2())) {
            setUser2Score(userScore);
            pass = true;
        } else if(userId.equals(getUser3())) {
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

    public boolean setGame(String board, int timer, int state) {
        setBoard(board);
        setTimer(timer);
        setState(state);
        sendUpdateToClients(ChannelMessageType.RESET);
        return true;
    }

    public static String createPieces(int numPlayers, int maxLength) {
        JSONArray jsonArray = new JSONArray();
        Random randomGenerator = new Random();

        for(int i = 0;i < maxLength;i += 1) {
            JSONObject jsonObject = new JSONObject(new Piece(i, Integer.toString(randomGenerator.nextInt(numPlayers) + 1), 1, true));
            jsonObject.remove("class");
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }
}