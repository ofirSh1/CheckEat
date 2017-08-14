package application.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    public static String getParameter(HttpServletRequest request, String param) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(param) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static void clearSession(HttpServletRequest request) {
        request.getSession().invalidate();
    }
}