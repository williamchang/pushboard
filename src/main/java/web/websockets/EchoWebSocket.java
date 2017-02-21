package web.websockets;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/echowebsocket")
public class EchoWebSocket {
    public EchoWebSocket() {
        System.out.println("WebSocket server initialized via default constructor");
    }

    @OnOpen
    public void onOpen(Session s) {
        System.out.println("WebSocket server open connection with Session Id : " + s.getId());
    }

    @OnClose
    public void onClose(Session s) {
        System.out.println("WebSocket server close connection with Session Id : " + s.getId());
    }

    @OnMessage
    public String onMessage(Session s, String message) {
        System.out.println("WebSocket server received message from the client with Session Id : " + s.getId() + ", Message : " + message);
        return "Echo with Session Id : " + s.getId() + ", Message : " + message;
    }

    @OnError
    public void onError(Session s, Throwable t) {
        System.out.println("WebSocket server thrown error with Session Id : " + s.getId());
        t.printStackTrace();
    }
}

