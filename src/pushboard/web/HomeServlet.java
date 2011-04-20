/**
@file
    HomeServlet.java
@brief
    Copyright 2011 Creative Crew. All rights reserved.
@author
    William Chang
@version
    0.1
@date
    - Created: 2011-04-04
    - Modified: 2011-04-11
    .
@note
    References:
    - General:
        - Nothing.
        .
    .
*/

package pushboard.web;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, world");
    }
}
