package application.servlets;

import application.logic.*;
import application.utils.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
@MultipartConfig
public class LoginServlet extends HttpServlet {
    EntityManagerFactory emf;
    EntityManager em;
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        em = emf.createEntityManager();
        request.setCharacterEncoding("UTF-8");
        String requestType = request.getParameter(Constants.REQUEST_TYPE);

        if (requestType.equals(Constants.GET_USERNAME)) {
            getUsername(request, response);
        }
        else if(requestType.equals("getUserType")) {
            getUserType(request, response);
        }
        else if(requestType.equals("getGivenUserType")) {
            getGivenUserType(request, response);
        }
        else if(requestType.equals(Constants.LOGIN)) {
            login(request, response);
        }
        else if(requestType.equals(Constants.LOGOUT)) {
            logout(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void getUsername(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        response.setContentType("text/html;charset=UTF-8");
        if(usernameFromSession != null) {
            SignedUser signedUser = em.find(SignedUser.class, usernameFromSession);
            if(signedUser != null) {
                PrintWriter out = response.getWriter();
                if (signedUser.getType() == SignedUser.Type.RESTAURANT)
                    out.print("restaurant");
                else
                    out.print("customer");
                out.flush();
            }
        }
    }

    private void getUserType(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        response.setContentType("text/html;charset=UTF-8");
        if(usernameFromSession != null) {
            SignedUser signedUser = em.find(SignedUser.class, usernameFromSession);
            if(signedUser != null) {
                PrintWriter out = response.getWriter();
                if (signedUser.getType() == SignedUser.Type.RESTAURANT)
                    out.print("restaurant");
                else
                    out.print("customer");
                out.flush();
            }
        }
    }

    private void getGivenUserType(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String userName = request.getParameter("userName");
        if(userName != null) {
            SignedUser signedUser = em.find(SignedUser.class, userName);
            if(signedUser != null) {
                PrintWriter out = response.getWriter();
                if (signedUser.getType() == SignedUser.Type.RESTAURANT)
                    out.print("restaurant");
                else
                    out.print("customer");
                out.flush();
            }
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUtils.clearSession(request);
    }

    private void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SignedUser signedUser = null;
        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        if(usernameFromSession != null) {
            ServletUtils.redirect(response, "הינך רשום כבר במערכת", "index.html");
        }
        else if (usernameFromSession==null && request.getParameter(Constants.USERNAME).equals("CheckEat")) {
            if (request.getParameter(Constants.PASSWORD).equals("CheckEat")){
                if (em.find(Admin.class,"CheckEat")== null) { // TODO
                    em.getTransaction().begin();
                    em.persist(new Admin());
                    em.getTransaction().commit();
                }
                request.getSession(true).setAttribute(Constants.USERNAME, "CheckEat");
                ServletUtils.redirect(response, "הנך מועבר לפרופיל מנהל מערכת", "admin.html");
            }
            else {
                ServletUtils.redirect(response, "סיסמא לא נכונה", "login.html");
            }
        }
        else {
            String username = request.getParameter(Constants.USERNAME);
            String password = request.getParameter(Constants.PASSWORD);

            javax.persistence.TypedQuery<SignedUser> query =
                    em.createQuery("SELECT user FROM SignedUser user WHERE user.userName = '" + username + "'", SignedUser.class);
            List<SignedUser> res = query.getResultList();
            if (!res.isEmpty())
                signedUser = res.get(0);
            if (signedUser != null && signedUser.getPassword().equals(password)) {
                request.getSession(true).setAttribute(Constants.USERNAME, username); //check
                if (signedUser.getType() == SignedUser.Type.CUSTOMER)
                    ServletUtils.redirect(response, "הנך מועבר לפרופיל משתמש", "profile.html");
                else
                    ServletUtils.redirect(response, "הנך מועבר לפרופיל מסעדה", "restProfile.html");
            } else {
                ServletUtils.redirect(response, "שם משתמש או סיסמא לא נכונים", "login.html");
            }
        }
    }
}