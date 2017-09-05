package application.servlets;

import application.logic.SignedUser;
import application.utils.Constants;
import application.utils.ServletUtils;
import application.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "RegisterTypeServlet", urlPatterns = {"/registerType"})
public class RegisterTypeServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        SignedUser.Type type = request.getParameter(Constants.TYPE).equals(Constants.CUSTOMER)
        ? SignedUser.Type.CUSTOMER : SignedUser.Type.RESTAURANT;

        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        if(usernameFromSession != null) {
            ServletUtils.redirect(response, "הינך רשום כבר במערכת", "index.html");
        }
        else if(type == SignedUser.Type.CUSTOMER) {
            ServletUtils.redirect(response, "", "customerReg.html");
        }
        else {
            ServletUtils.redirect(response, "", "restaurantReg.html");
        }
    }
}