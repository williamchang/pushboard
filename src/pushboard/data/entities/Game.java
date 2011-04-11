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
    - Modified: 2011-04-10
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
        NEW,
        RUNNING,
        WAITING
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

    public void setUser1Score(int user) {
        this.user1Score = user;
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

    public void setUser2Score(int user) {
        this.user2Score = user;
    }

    public String getUser3() {
        return user3;
    }

    public void setUser3(String user) {
        this.user3 = user;
    }

    public int getUser3Score() {
        return user1Score;
    }

    public void setUser3Score(int user) {
        this.user2Score = user;
    }

    public Text getBoard() {
        return board;
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getChannelKey(String user) {
        return user + KeyFactory.keyToString(id);
    }

    public String getChannelMessage() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user1", user1);
            jsonObject.put("user1Score", user1Score);
            jsonObject.put("user2", user2);
            jsonObject.put("user2Score", user2Score);
            jsonObject.put("user3", user3);
            jsonObject.put("user3Score", user3Score);
            jsonObject.put("board", new JSONArray(board.getValue()));
            jsonObject.put("timer", timer);
            jsonObject.put("winner", winner);
            jsonObject.put("state", state);
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void sendUpdateToClient(String user) {
        if(user != null && !user.isEmpty()) {
            ChannelService svc = ChannelServiceFactory.getChannelService();
            String channelKey = getChannelKey(user);
            svc.sendMessage(new ChannelMessage(channelKey, getChannelMessage()));
        }
    }

    public void sendUpdateToClients() {
        sendUpdateToClient(user1);
        sendUpdateToClient(user2);
        sendUpdateToClient(user3);
    }

    public boolean setMove(String userId, int userScore, String board, int timer, int state) {
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
            setBoard(board);
            setTimer(timer);
            setState(state);
            sendUpdateToClients();
            return true;
        }
        return false;
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