/**
@file
    GameWebSocket.java
@brief
    Copyright 2017 Creative Crew. All rights reserved.
@author
    William Chang
@version
    0.1
@date
    - Created: 2017-02-19
    - Modified: 2017-02-23
    .
@note
    References:
    - General:
        - http://svn.apache.org/viewvc/tomcat/trunk/webapps/examples/WEB-INF/classes/websocket/chat/ChatAnnotation.java?view=markup
        .
    .
*/

package web.websockets;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import data.entities.User;
import data.interfaces.IGameRepository;
import data.sqlite.repositories.BaseRepository;
import data.sqlite.repositories.GameRepository;

@ServerEndpoint("/gamewebsocket")
public class GameWebSocket {
    protected static final Set<GameWebSocket> wsClientConnections = new CopyOnWriteArraySet<GameWebSocket>();
    protected Session wsClientSession;
    protected User gameUser;

    public GameWebSocket() {
        System.out.println("WebSocket server initialized via default constructor");
        this.wsClientSession = null;
        this.gameUser = null;
    }

    public static void broadcast(String message) {
        for(GameWebSocket wsConnection : wsClientConnections) {
            try {
                synchronized(wsConnection) {
                    wsConnection.wsClientSession.getBasicRemote().sendText(message);
                }
            } catch(IOException ex) {
                System.out.println("WebSocket server failed to send message to the client : " + ex);
                wsClientConnections.remove(wsConnection);
                try {
                    wsConnection.wsClientSession.close();
                } catch(IOException e2) {
                    System.out.println("WebSocket server failed to close session of the client : " + ex);
                }
                broadcast(String.format("* %s %s", wsConnection.gameUser.getAlias(), "has been disconnected."));
            }
        }
    }

    public static void send(UUID userId, String message) {
        for(GameWebSocket wsConnection : wsClientConnections) {
            try {
                synchronized(wsConnection) {
                    if(wsConnection.gameUser != null && wsConnection.gameUser.getId().equals(userId)) {
                        wsConnection.wsClientSession.getBasicRemote().sendText(message);
                    }
                }
            } catch(IOException ex) {
                System.out.println("WebSocket server failed to send message to the client : " + ex);
                wsClientConnections.remove(wsConnection);
                try {
                    wsConnection.wsClientSession.close();
                } catch(IOException e2) {
                    System.out.println("WebSocket server failed to close session of the client : " + ex);
                }
            }
        }
    }

    @OnOpen
    public void onOpen(Session s) {
        System.out.println("WebSocket server open connection with Session Id : " + s.getId());
        this.wsClientSession = s;
        wsClientConnections.add(this);
    }

    @OnClose
    public void onClose(Session s) {
        System.out.println("WebSocket server close connection with Session Id : " + s.getId());
        wsClientConnections.remove(this);
    }

    @OnMessage
    public void onMessage(Session s, String message) {
        System.out.println("WebSocket server received message from the client with Session Id : " + s.getId() + ", Message : " + message);

        String sqliteConnectionString = BaseRepository.getDefaultConnectionString();

        if(sqliteConnectionString != null && message != null && !message.isEmpty()) {
            IGameRepository repoGame = new GameRepository(sqliteConnectionString);
            this.gameUser = repoGame.getUser(UUID.fromString(message));
        } else {
            throw new IllegalArgumentException();
        }
    }

    @OnError
    public void onError(Session s, Throwable t) {
        System.out.println("WebSocket server thrown error with Session Id : " + s.getId());
        t.printStackTrace();
    }
}

