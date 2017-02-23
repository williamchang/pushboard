/**
@file
    GameServlet.java
@brief
    Copyright 2011 Creative Crew. All rights reserved.
@author
    William Chang
@version
    0.1
@date
    - Created: 2011-04-04
    - Modified: 2017-02-23
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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.util.Date;
import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import data.entities.*;
import data.entities.Game.*;
import data.interfaces.*;
import data.sqlite.repositories.*;

@SuppressWarnings("serial")
@WebServlet("/pushboard")
public class GameServlet extends BaseServlet {
    protected IGameRepository repoGame;

    /**
     * Default constructor.
     */
    public GameServlet() {
        String sqliteConnectionString = BaseRepository.getDefaultConnectionString();
        if(sqliteConnectionString != null) {
            repoGame = new GameRepository(sqliteConnectionString);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession webSession = req.getSession(true);

        String redirectGuestUrl = "/pushboard/user";
        String gameKey = req.getParameter("gameKey");
        String userAlias = req.getParameter("userAlias");
        Date dateCreated = new Date();

        // Validate user.
        if(userAlias == null || userAlias.isEmpty()) {
            // Redirect user.
            res.sendRedirect(res.encodeRedirectURL(getGameUrl(redirectGuestUrl, gameKey)));
            return;
        }

        // Declare and init prerequisites.
        User reqUser = null;
        Game reqGame = null;

        // Get record.
        reqUser = repoGame.getUser(userAlias);

        // Validate host game key.
        if(gameKey == null || gameKey.isEmpty()) {
            // Get host game key from session.
            String hostGameKey = (String)webSession.getAttribute("gameKey");
            // Validate host game key from sesison.
            if(hostGameKey != null && !hostGameKey.isEmpty()) {
                gameKey = hostGameKey;
            }
        }
        // Validate game key.
        if(gameKey != null && !gameKey.isEmpty()) {
            // Get record.
            reqGame = repoGame.getGame(UUID.fromString(gameKey));
            // Validate record.
            if(reqGame == null) {
                // Redirect user.
                res.sendRedirect(res.encodeRedirectURL(redirectGuestUrl));
                return;
            }
            // Create record.
            if(reqUser == null) {
                reqUser = repoGame.createUser(new User("", "", userAlias, dateCreated));
            }
            // Set record for other users.
            if(reqGame.getUser2() == null && !reqUser.getId().equals(reqGame.getUser1Id()) && !reqUser.getId().equals(reqGame.getUser3Id())) {
                reqGame.setUser2(reqUser);
                reqGame.setUser2Id(reqUser.getId());
                reqGame = repoGame.setGame(reqGame);
            } else if(reqGame.getUser3() == null && !reqUser.getId().equals(reqGame.getUser1Id()) && !reqUser.getId().equals(reqGame.getUser2Id())) {
                reqGame.setUser3(reqUser);
                reqGame.setUser3Id(reqUser.getId());
                reqGame = repoGame.setGame(reqGame);
            }
        } else {
            // Delete records.
            if(reqUser != null) {
                repoGame.deleteGameByUser1Id(reqUser.getId());
            }
            // Create record.
            reqUser = repoGame.createUser(new User("", "", userAlias, dateCreated));
            reqGame = repoGame.createGame(new Game(reqUser, Game.createPieces(3, 36), 8, dateCreated));
            // Get game key.
            gameKey = reqGame.getId().toString();
            webSession.setAttribute("gameKey", gameKey);
        }

        // Get template.
        Reader reader = new InputStreamReader(getServletContext().getResourceAsStream("/game.tpl.html"));
        CharBuffer buffer = CharBuffer.allocate(16384);
        reader.read(buffer);
        String strHtml = new String(buffer.array());

        // Set template.
        strHtml = strHtml.replaceAll("[\u0000]", "");
        strHtml = strHtml.replaceAll("\\$user_me_id\\$", reqUser.getId().toString());
        strHtml = strHtml.replaceAll("\\$user_me_alias\\$", reqUser.getAlias());
        strHtml = strHtml.replaceAll("\\$game_timer\\$", Integer.toString(reqGame.getTimer()));
        strHtml = strHtml.replaceAll("\\$game_key\\$", gameKey);
        strHtml = strHtml.replaceAll("\\$game_link\\$", getGameUrl(req.getRequestURL().toString(), gameKey));
        strHtml = strHtml.replaceAll("\\$channel_message_initial\\$", reqGame.getChannelMessage(ChannelMessageType.GENERIC));

        // Set view.
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(strHtml);

        if(reader != null) try {reader.close();} catch(Exception ex) {}
    }

    /**
     * Get game URL aka URI.
     * */
    public static String getGameUrl(String url, String gameKey) throws IOException {
        try {
            String uriQuery = null;
            String uriFragment = null;
            if(gameKey != null && !gameKey.isEmpty()) {
                uriQuery = "gameKey=" + gameKey;
            }
            URI uriThis = new URI(url);
            URI uriWithOptionalParameters = new URI(
                uriThis.getScheme(),
                uriThis.getUserInfo(),
                uriThis.getHost(),
                uriThis.getPort(),
                uriThis.getPath(),
                uriQuery,
                uriFragment
            );
            return uriWithOptionalParameters.toString();
        } catch(URISyntaxException ex) {
            throw new IOException(ex.getMessage());
        }
    }
}
