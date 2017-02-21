/**
@file
    BaseServlet.java
@author
    William Chang
@version
    0.1
@date
    - Created: 2017-02-15
    - Modified: 2017-02-20
    .
@note
    References:
    - General:
        - http://users.polytech.unice.fr/~buffa/cours/internet/POLYS/servlets/Servlet-Tutorial-CGI-Variables.html
        .
    .
*/

package web.servlets;

import java.util.Enumeration;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import data.sqlite.repositories.*;

/**
 * Servlet implementation class Base.
 */
@SuppressWarnings("serial")
public class BaseServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(UserServlet.class.getName());

    /**
     * Default constructor.
     */
    public BaseServlet() {}

    /**
     * Get action name for MVC URL routing.
     */
    protected String getActionName(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if(pathInfo != null) {
            String[] pathSegments = pathInfo.split("/");
            if(pathSegments != null && pathSegments.length >= 2) {
                return pathSegments[1];
            }
            return null;
        }
        return "index";
    }

    /**
     * Compare two action names for MVC URL routing, case-insensitive and null checking.
     */
    protected boolean isActionEquals(String name1, String name2) {
        if(name1 != null && name2 != null) {
            return name1.equalsIgnoreCase(name2);
        }
        return false;
    }

    /**
     * Debug Information.
     */
    protected String getDebugInformation(HttpServletRequest req, HttpServletResponse res) {
        StringBuilder sb1 = new StringBuilder();

        log.info("Called getDebugInformation method");

        sb1.append(System.getProperty("line.separator"));
        sb1.append("Server Variable SCRIPT_NAME : ").append(req.getServletPath()).append(System.getProperty("line.separator"));
        sb1.append("Server Variable DOCUMENT_ROOT : ").append(getServletContext().getRealPath("/")).append(System.getProperty("line.separator"));
        sb1.append("Server Variable REQUEST_METHOD : ").append(req.getMethod()).append(System.getProperty("line.separator"));
        sb1.append(System.getProperty("line.separator"));

        // Get request headers.
        Enumeration<String> reqHeaderNames = req.getHeaderNames();
        sb1.append(System.getProperty("line.separator"));
        while(reqHeaderNames.hasMoreElements()) {
            String name = reqHeaderNames.nextElement();
            String value = req.getHeader(name);

            sb1.append("Request Header Name : ").append(name).append(System.getProperty("line.separator"));
            sb1.append("Request Header Value : ").append(value).append(System.getProperty("line.separator"));
            sb1.append(System.getProperty("line.separator"));
        }

        // Get request path.
        String pathInfo = req.getPathInfo();
        sb1.append(System.getProperty("line.separator"));
        sb1.append("Server Variable PATH_INFO : ").append(pathInfo).append(System.getProperty("line.separator"));
        if(pathInfo != null) {
            String[] pathSegments = pathInfo.split("/");
            for(int index = 0;index < pathSegments.length; index += 1) {
                sb1.append("Server Variable PATH_INFO Segment Index : ").append(index).append(", Value : ").append(pathSegments[index]).append(System.getProperty("line.separator"));
            }
        }
        sb1.append(System.getProperty("line.separator"));

        // Get request querystring.
        sb1.append(System.getProperty("line.separator"));
        sb1.append("Server Variable QUERY_STRING : ").append(req.getQueryString()).append(System.getProperty("line.separator"));
        sb1.append(System.getProperty("line.separator"));

        // Get request parameters.
        Enumeration<String> reqParameterNames = req.getParameterNames();
        sb1.append(System.getProperty("line.separator"));
        while(reqParameterNames.hasMoreElements()) {
            String name = reqParameterNames.nextElement();
            String[] values = req.getParameterValues(name);

            sb1.append("Request Parameter Name : ").append(name).append(System.getProperty("line.separator"));
            for(int index = 0;index < values.length; index += 1) {
                sb1.append("Request Parameter Value Index : ").append(index).append(", Value : ").append(values[index]).append(System.getProperty("line.separator"));
            }
            sb1.append(System.getProperty("line.separator"));
        }

        // Get debug information.
        sb1.append(System.getProperty("line.separator"));
        sb1.append("User Working Directory and FileInputStream Path : ").append(System.getProperty("user.dir")).append(System.getProperty("line.separator"));
        sb1.append("Servlet Context Real Path : ").append(getServletContext().getRealPath("/")).append(System.getProperty("line.separator"));
        sb1.append("Classes Folder Path : ").append(BaseRepository.getClassesFolderPath()).append(System.getProperty("line.separator"));
        sb1.append(System.getProperty("line.separator"));

        return sb1.toString();
    }
}
