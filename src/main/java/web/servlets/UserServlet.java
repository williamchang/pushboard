/**
@file
    UserRegisterServlet.java
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
        - Nothing.
        .
    .
*/

package web.servlets;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import data.entities.*;
import data.interfaces.*;
import data.sqlite.repositories.*;

@SuppressWarnings("serial")
@WebServlet("/pushboard/user/*")
public class UserServlet extends BaseServlet {
    protected IGameRepository repoGame;

    /**
     * Default constructor.
     */
    public UserServlet() {
        String sqliteConnectionString = BaseRepository.getDefaultConnectionString();
        if(sqliteConnectionString != null) {
            repoGame = new GameRepository(sqliteConnectionString);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession webSession = req.getSession(true);

        StringBuilder sb1 = new StringBuilder();
        String action = getActionName(req);
        String redirectLogoutUrl = "/pushboard/user/logout";
        String gameKey = req.getParameter("gameKey");

        // Validate action from URL.
        if(isActionEquals(action, "logout")) {
            sb1.append(logout(req, res));
            res.getWriter().write(sb1.toString());
            return;
        }

        // Validate parameters.
        if(gameKey == null || gameKey.isEmpty()) {
            gameKey = "";
        }

        // Validate host.
        String hostGameKey = (String)webSession.getAttribute("gameKey");
        String hostMessage = "";
        if(hostGameKey != null && !hostGameKey.isEmpty()) {
            hostMessage = "You are currently hosting a game, please <a href=" + redirectLogoutUrl + ">logout</a>.";
        }

        // Get template.
        Reader reader = new InputStreamReader(getServletContext().getResourceAsStream("/register.tpl.html"));
        CharBuffer buffer = CharBuffer.allocate(16384);
        reader.read(buffer);
        String strHtml = new String(buffer.array());

        // Set template.
        strHtml = strHtml.replaceAll("[\u0000]", "");
        strHtml = strHtml.replaceAll("\\$game_key\\$", gameKey);
        strHtml = strHtml.replaceAll("\\$status\\$", hostMessage);

        // Set view.
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(strHtml);

        if(reader != null) try {reader.close();} catch(Exception ex) {}
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        StringBuilder sb1 = new StringBuilder();
        String action = getActionName(req);

        // Set view.
        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");

        // Validate action from URL.
        if(isActionEquals(action, "index")) {
            sb1.append(index(req, res));
        } else if(isActionEquals(action, "create")) {
            sb1.append(create(req, res));
        } else {
            sb1.append("Missing action name.");
        }

        res.getWriter().write(sb1.toString());
    }

    public String index(HttpServletRequest req, HttpServletResponse res) throws IOException {
        StringBuilder sb1 = new StringBuilder();

        // Set view.
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        // Get records.
        List<Game> objs1 = (List<Game>)repoGame.getGames();
        // Create JSON string.
        String p1 = "";
        sb1.append("[");
        for(int index = 0;index < objs1.size();index += 1) {
            p1 = GameServlet.getGameUrl(req.getRequestURL().toString(), objs1.get(index).getId().toString());

            sb1.append("{");
            sb1.append("\"userAlias\":\"").append(objs1.get(index).getUser1().getAlias()).append("\"").append(",");
            sb1.append("\"gameLink\":\"").append(p1).append("\"");
            sb1.append("}");
            sb1.append(", ");
        }
        if(objs1.size() > 0) {sb1.setLength(sb1.length() - 2);}
        sb1.append("]");

        return sb1.toString();
    }

    public String create(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String userAlias = req.getParameter("userAlias");

        repoGame.createUser(new User("", "", userAlias, new Date()));
        return "1";
    }

    public String logout(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String redirectGuestUrl = "/pushboard/user";
        HttpSession webSession = req.getSession(false);

        if(webSession != null) {
            webSession.invalidate();
        }
        // Redirect user.
        res.sendRedirect(res.encodeRedirectURL(redirectGuestUrl));
        return "1";
    }
}
