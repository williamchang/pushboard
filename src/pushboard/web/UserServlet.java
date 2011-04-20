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
    - Modified: 2011-04-12
    .
@note
    References:
    - General:
        - http://jvdkamp.wordpress.com/2010/01/30/using-sessions-with-google-app-engine/
        - http://stackoverflow.com/questions/1134800/google-appengine-session-example
        .
    .
*/

package pushboard.web;

import java.io.*;
import java.nio.CharBuffer;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.KeyFactory;
import pushboard.data.core.EntityManagerFactory;
import pushboard.data.entities.*;

@SuppressWarnings("serial")
public class UserServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(UserServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession webSession = req.getSession(true);

        StringBuilder sb1 = new StringBuilder();
        String action = getInitParameter("action");
        String redirectLogoutUrl = getInitParameter("redirect_url.logout");
        String gameKey = req.getParameter("gameKey");

        log.info("HTTP GET");

        // Validate action from URL.
        if(action.equals("logout")) {
            sb1.append(logout(req, resp));
            resp.getWriter().write(sb1.toString());
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
        Reader reader = new InputStreamReader(new FileInputStream("register.tpl.html"), "UTF-8");
        CharBuffer buffer = CharBuffer.allocate(16384);
        reader.read(buffer);
        String s1 = new String(buffer.array());

        // Set template.
        s1 = s1.replaceAll("\\$game_key\\$", gameKey);
        s1 = s1.replaceAll("\\$status\\$", hostMessage);

        // Set view.
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(s1);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb1 = new StringBuilder();
        String action = getInitParameter("action");

        log.info("HTTP POST");

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");

        // Validate action from URL.
        if(action.equals("index")) {
            sb1.append(index(req, resp));
        } else if(action.equals("create")) {
            sb1.append(create(req, resp));
        } else {
            sb1.append("Missing action name.");
        }

        resp.getWriter().write(sb1.toString());
    }

    @SuppressWarnings("unchecked")
    public String index(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        StringBuilder sb1 = new StringBuilder();
        EntityManager em = EntityManagerFactory.get().createEntityManager();

        // Get record.
        Query q = em.createQuery("SELECT FROM Game");
        List<Game> objs1 = q.getResultList();

        // Create JSON string.
        String p1 = "";
        sb1.append("[");
        for(int i = 0;i < objs1.size();i += 1) {
            p1 = GameServlet.getGameUri(req.getRequestURL().toString(), KeyFactory.keyToString(objs1.get(i).getId()));

            sb1.append("{");
            sb1.append("\"userAlias\":\"");sb1.append(objs1.get(i).getUser1());sb1.append("\"");sb1.append(",");
            sb1.append("\"gameLink\":\"");sb1.append(p1);sb1.append("\"");
            sb1.append("}");
            sb1.append(", ");
        }
        if(objs1.size() > 0) {sb1.setLength(sb1.length() - 2);}
        sb1.append("]");

        return sb1.toString();
    }

    public String create(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userAlias = req.getParameter("userAlias");
        Date dateCreated = new Date();

        User obj1 = new User("", "", userAlias, dateCreated);
        EntityManager em = EntityManagerFactory.get().createEntityManager();
        try {
            em.persist(obj1);
        } finally {
            em.close();
        }

        return "1";
    }
    
    public String logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String redirectGuestUrl = getInitParameter("redirect_url.guest");

        HttpSession webSession = req.getSession(false);
        if(webSession != null) {
            webSession.invalidate();
        }
        // Redirect user.
        resp.sendRedirect(resp.encodeRedirectURL(redirectGuestUrl));
        return "1";
    }
}
