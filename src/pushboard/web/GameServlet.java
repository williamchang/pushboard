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
    - Modified: 2011-05-03
    .
@note
    References:
    - General:
        - http://code.google.com/p/java-channel-tic-tac-toe/
        .
    .
*/

package pushboard.web;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.util.Date;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.*;
import com.google.appengine.api.channel.*;
import com.google.appengine.api.datastore.KeyFactory;
import pushboard.data.core.EntityManagerFactory;
import pushboard.data.entities.*;
import pushboard.data.entities.Game.ChannelMessageType;

@SuppressWarnings("serial")
public class GameServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(GameServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession webSession = req.getSession(true);

        String redirectGuestUrl = getInitParameter("redirect_url.guest");
        String gameKey = req.getParameter("gameKey");
        String userId = req.getParameter("userId");
        Date dateCreated = new Date();

        log.info("HTTP GET");

        // Validate user.
        if(userId == null || userId.isEmpty()) {
            // Redirect user.
            resp.sendRedirect(resp.encodeRedirectURL(getGameUri(redirectGuestUrl, gameKey)));
            return;
        }

        // Declare and init prerequisites.
        EntityManager em = EntityManagerFactory.get().createEntityManager();
        Game obj1 = null;

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
            try {
                // Get record.
                obj1 = em.find(Game.class, KeyFactory.stringToKey(gameKey));
                // Validate record.
                if(obj1 == null) {
                    // Redirect user.
                    resp.sendRedirect(resp.encodeRedirectURL(redirectGuestUrl));
                    return;
                }
                // Set record for other users.
                if(obj1.getUser2().isEmpty() && !userId.equals(obj1.getUser1()) && !userId.equals(obj1.getUser3())) {
                    obj1.setUser2(userId);
                } else if(obj1.getUser3().isEmpty() && !userId.equals(obj1.getUser1()) && !userId.equals(obj1.getUser2())) {
                    obj1.setUser3(userId);
                }
            } finally {
                em.close();
            }
        } else {
            // Delete records.
            Query q = em.createQuery("DELETE FROM Game WHERE user1 = :user1");
            q.setParameter("user1", userId);
            q.executeUpdate();
            // Create record.
            obj1 = new Game(userId, "", "", Game.createPieces(3, 36), 12, dateCreated);
            try {
                em.persist(obj1);
            } finally {
                em.close(); // Needed to create id (aka key).
            }
            // Get game key.
            gameKey = KeyFactory.keyToString(obj1.getId());
            webSession.setAttribute("gameKey", gameKey);
        }

        // Init channel service.
        ChannelService svc = ChannelServiceFactory.getChannelService();
        String token = svc.createChannel(obj1.getChannelKey(userId));

        // Get template.
        Reader reader = new InputStreamReader(new FileInputStream("game.tpl.html"), "UTF-8");
        CharBuffer buffer = CharBuffer.allocate(16384);
        reader.read(buffer);
        String s1 = new String(buffer.array());

        // Set template.
        s1 = s1.replaceAll("[\u0000]", "");
        s1 = s1.replaceAll("\\$user_me\\$", userId);
        s1 = s1.replaceAll("\\$game_timer\\$", Integer.toString(obj1.getTimer()));
        s1 = s1.replaceAll("\\$game_key\\$", gameKey);
        s1 = s1.replaceAll("\\$game_link\\$", getGameUri(req.getRequestURL().toString(), gameKey));
        s1 = s1.replaceAll("\\$channel_token\\$", token);
        s1 = s1.replaceAll("\\$channel_message_initial\\$", obj1.getChannelMessage(ChannelMessageType.GENERIC));

        // Set view.
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(s1);
    }

    /** Get game URI. */
    public static String getGameUri(String url, String gameKey) throws IOException {
        try {
            String qs;
            if(gameKey == null) {
                qs = "";
            } else {
                qs = "gameKey=" + gameKey;
            }
            URI uriThis = new URI(url);
            URI uriWithOptionalGameParam = new URI(
                uriThis.getScheme(),
                uriThis.getUserInfo(),
                uriThis.getHost(),
                uriThis.getPort(),
                uriThis.getPath(),
                qs,
                null
            );
            return uriWithOptionalGameParam.toString();
        } catch(URISyntaxException ex) {
            throw new IOException(ex.getMessage());
        }
    }
}
