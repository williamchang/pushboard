/**
@file
    ChannelOpenedServlet.java
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
        - http://en.wikipedia.org/wiki/List_of_HTTP_status_codes
        - http://www.winstonprakash.com/articles/netbeans/JPA_Add_Update_Delete.html
        .
    .
*/

package pushboard.web;

import java.io.IOException;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.KeyFactory;
import pushboard.data.core.EntityManagerFactory;
import pushboard.data.entities.*;
import pushboard.data.entities.Game.ChannelMessageType;

@SuppressWarnings("serial")
public class ChannelServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ChannelServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("HTTP GET");

        // Set view.
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(401);
        resp.getWriter().write("401 Unauthorized");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb1 = new StringBuilder();
        String action = getInitParameter("action");

        log.info("HTTP POST");

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");

        if(action.equals("opened")) {
            sb1.append(opened(req, resp));
        } else if(action.equals("check")) {
            sb1.append(check(req, resp));
        } else if(action.equals("move")) {
            sb1.append(move(req, resp));
        } else if(action.equals("reset")) {
            sb1.append(reset(req, resp));
        } else {
            sb1.append("Missing action name.");
        }

        resp.getWriter().write(sb1.toString());
    }

    public String opened(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String gameKey = req.getParameter("gameKey");

        // Declare and init prerequisites.
        EntityManager em = EntityManagerFactory.get().createEntityManager();
        try {
            // Get record.
            Game obj1 = em.find(Game.class, KeyFactory.stringToKey(gameKey));
            // Validate record.
            if(obj1 != null) {
                obj1.sendUpdateToClients(ChannelMessageType.GENERIC);
                return "1";
            } else {
                resp.setStatus(401);
            }
        } finally {
            em.close();
        }
        return "0";
    }

    public String check(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String gameKey = req.getParameter("gameKey");
        int timer = new Integer(req.getParameter("timer"));
        int state = new Integer(req.getParameter("state"));

        // Declare and init prerequisites.
        EntityManager em = EntityManagerFactory.get().createEntityManager();
        try {
            // Get record.
            Game obj1 = em.find(Game.class, KeyFactory.stringToKey(gameKey));
            // Validate and set record.
            if(obj1 != null) {
                obj1.setTimer(timer);
                obj1.setState(state);
                obj1.sendUpdateToClients(ChannelMessageType.GENERIC);
                return "1";
            } else {
                resp.setStatus(401);
            }
        } finally {
            em.close();
        }
        return "0";
    }

    public String move(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String gameKey = req.getParameter("gameKey");
        String userId = req.getParameter("userId");
        int userScore = new Integer(req.getParameter("userScore"));
        String move = req.getParameter("move");

        // Declare and init prerequisites.
        EntityManager em = EntityManagerFactory.get().createEntityManager();
        try {
            // Get record.
            Game obj1 = em.find(Game.class, KeyFactory.stringToKey(gameKey));
            // Validate and set record.
            if(obj1 != null && obj1.setMove(userId, userScore, move)) {
                return "1";
            } else {
                resp.setStatus(401);
            }
        } finally {
            em.close();
        }
        return "0";
    }

    public String reset(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String gameKey = req.getParameter("gameKey");
        String board = req.getParameter("board");
        int timer = new Integer(req.getParameter("timer"));
        int state = new Integer(req.getParameter("state"));

        // Declare and init prerequisites.
        EntityManager em = EntityManagerFactory.get().createEntityManager();
        try {
            // Get record.
            Game obj1 = em.find(Game.class, KeyFactory.stringToKey(gameKey));
            // Validate and set record.
            if(obj1 != null && obj1.setGame(board, timer, state)) {
                return "1";
            } else {
                resp.setStatus(401);
            }
        } finally {
            em.close();
        }

        return "0";
    }
}