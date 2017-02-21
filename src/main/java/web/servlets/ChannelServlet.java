/**
@file
    ChannelServlet.java
@brief
    Copyright 2011 Creative Crew. All rights reserved.
@author
    William Chang
@version
    0.1
@date
    - Created: 2011-04-04
    - Modified: 2017-02-21
    .
@note
    References:
    - General:
        - http://en.wikipedia.org/wiki/List_of_HTTP_status_codes
        .
    .
*/

package web.servlets;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import data.entities.*;
import data.entities.Game.ChannelMessageType;
import data.interfaces.*;
import data.sqlite.repositories.*;

@SuppressWarnings("serial")
@WebServlet("/pushboard/channel/*")
public class ChannelServlet extends BaseServlet {
    protected IGameRepository repoGame;

    /**
     * Default constructor.
     */
    public ChannelServlet() {
        String sqliteConnectionString = BaseRepository.getDefaultConnectionString();
        if(sqliteConnectionString != null) {
            repoGame = new GameRepository(sqliteConnectionString);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Set view.
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        res.setStatus(401);
        res.getWriter().write("401 Unauthorized");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        StringBuilder sb1 = new StringBuilder();
        String action = getActionName(req);

        // Set view.
        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");

        if(isActionEquals(action, "opened")) {
            sb1.append(opened(req, res));
        } else if(isActionEquals(action, "check")) {
            sb1.append(check(req, res));
        } else if(isActionEquals(action, "move")) {
            sb1.append(move(req, res));
        } else if(isActionEquals(action, "reset")) {
            sb1.append(reset(req, res));
        } else {
            sb1.append("Missing action name.");
        }

        res.getWriter().write(sb1.toString());
    }

    public String opened(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String gameKey = req.getParameter("gameKey");

        // Declare and init prerequisites.
        Game obj1 = null;
        // Get record.
        obj1 = repoGame.getGame(UUID.fromString(gameKey));
        // Validate record.
        if(obj1 != null) {
            obj1.sendUpdateToClients(ChannelMessageType.GENERIC);
            return "1";
        } else {
            res.setStatus(401);
        }
        return "0";
    }

    public String check(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String gameKey = req.getParameter("gameKey");
        int timer = new Integer(req.getParameter("timer"));
        int state = new Integer(req.getParameter("state"));

        // Declare and init prerequisites.
        Game obj1 = null;
        // Get record.
        obj1 = repoGame.getGame(UUID.fromString(gameKey));
        // Validate and set record.
        if(obj1 != null) {
            obj1.setTimer(timer);
            obj1.setState(state);
            obj1.sendUpdateToClients(ChannelMessageType.GENERIC);
            return "1";
        } else {
            res.setStatus(401);
        }
        return "0";
    }

    public String move(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String gameKey = req.getParameter("gameKey");
        String userId = req.getParameter("userId");
        int userScore = new Integer(req.getParameter("userScore"));
        String move = req.getParameter("move");

        // Declare and init prerequisites.
        Game obj1 = null;
        // Get record.
        obj1 = repoGame.getGame(UUID.fromString(gameKey));
        // Validate and set record.
        if(obj1 != null && obj1.setMove(userId, userScore, move)) {
            return "1";
        } else {
            res.setStatus(401);
        }
        return "0";
    }

    public String reset(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String gameKey = req.getParameter("gameKey");
        String board = req.getParameter("board");
        int timer = new Integer(req.getParameter("timer"));
        int state = new Integer(req.getParameter("state"));

        // Declare and init prerequisites.
        Game obj1 = null;
        // Get record.
        obj1 = repoGame.getGame(UUID.fromString(gameKey));
        // Validate and set record.
        if(obj1 != null && obj1.setGame(board, timer, state)) {
            return "1";
        } else {
            res.setStatus(401);
        }
        return "0";
    }
}