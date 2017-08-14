package application.servlets;

import application.logic.*;
import application.utils.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "RegisterTypeServlet", urlPatterns = {"/registerType"})
public class RegisterTypeServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        SignedUser.Type type = request.getParameter(Constants.TYPE).equals(Constants.CUSTOMER)
        ? SignedUser.Type.CUSTOMER : SignedUser.Type.RESTAURANT;

        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        if(usernameFromSession != null) {
            sendAlert(response, "הינך רשום כבר במערכת", "location='index.html';");
        }
        else if(type == SignedUser.Type.CUSTOMER) {
            sendAlert(response, "הנך מועבר להרשמה", "location='customerReg.html';");
        }
        else {
            sendAlert(response, "הנך מועבר להרשמה", "location='restaurantReg.html';");
        }
    }
        private void sendAlert(HttpServletResponse response, String message,String location) throws IOException{
            PrintWriter out = response.getWriter();
            out.println("<script type=\"text/javascript\">");
            out.println(String.format("alert('%s');", message));
            out.println(location);
            out.println("</script>");
        }
}