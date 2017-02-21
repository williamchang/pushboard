/**
@file
    HomeServlet.java
@author
    William Chang
@version
    0.1
@date
    - Created: 2017-02-05
    - Modified: 2017-02-15
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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Home.
 */
@WebServlet({"/home", "/api/v1", "/api/v1/home/*"})
public class HomeServlet extends BaseServlet {
    protected static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public HomeServlet() {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        StringBuilder sb1 = new StringBuilder();

        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");

        res.getWriter().println();
        res.getWriter().println("Hello World");
        res.getWriter().println();

        sb1.append(getDebugInformation(req, res));

        res.getWriter().write(sb1.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
