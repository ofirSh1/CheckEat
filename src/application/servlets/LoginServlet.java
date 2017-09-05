package application.servlets;

import application.logic.Admin;
import application.logic.AppManager;
import application.logic.PasswordResetToken;
import application.logic.SignedUser;
import application.utils.Constants;
import application.utils.ServletUtils;
import application.utils.SessionUtils;

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
import java.util.UUID;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
@MultipartConfig
public class LoginServlet extends HttpServlet {
    private EntityManager em;
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        em = emf.createEntityManager();
        request.setCharacterEncoding("UTF-8");
        String requestType = request.getParameter(Constants.REQUEST_TYPE);

        switch (requestType) {
            case Constants.GET_USERNAME:
                getUsername(request, response);
                break;
            case "getUserType":
                getUserType(request, response);
                break;
            case Constants.LOGIN:
                login(request, response);
                break;
            case Constants.LOGOUT:
                logout(request, response);
                break;
            case "forgotPassword":
                forgotPassword(request, response);
                break;
            case "resetPassword":
                resetPassword(request, response);
                break;
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
            PrintWriter out = response.getWriter();
            SignedUser signedUser = em.find(SignedUser.class, usernameFromSession);
            if(signedUser != null) {
                if (signedUser.getType() == SignedUser.Type.RESTAURANT)
                    out.print("restaurant");
                else
                    out.print("customer");
            }
            else
                out.print("CheckEat");
            out.flush();
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
        else if (request.getParameter(Constants.USERNAME).equals("CheckEat")) {
            if (request.getParameter(Constants.PASSWORD).equals("CheckEat")){
                if (em.find(Admin.class,"CheckEat")== null) {
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
                    em.createQuery("SELECT user FROM SignedUser user WHERE user.userName = :username", SignedUser.class);
            query.setParameter("username", username);
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

    private void forgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        Admin admin = em.find(Admin.class, "CheckEat");
        if(ServletUtils.isValidString(username)) {
            SignedUser signedUser = em.find(SignedUser.class, username);
            if(signedUser != null) {
                PasswordResetToken prt = createPasswordResetToken(em, username);
                sendPasswordResetLink(admin,signedUser.getEmail(), prt.getToken());
                out.print("קוד איפוס סיסמא נשלח למייל");
            }
            else
                out.print("שם משתמש לא נמצא");
        }
        else
            out.print("לא ניתן היה לאפס את הסיסמא");
        out.flush();
    }

    private PasswordResetToken createPasswordResetToken(EntityManager em, String username) {
        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        PasswordResetToken prt = appManager.getUserPasswordResetToken(em, username);
        String token = UUID.randomUUID().toString();
        if(prt == null) {
            prt = new PasswordResetToken();
            prt.setPasswordToken(token, username);
            em.getTransaction().begin();
            em.persist(prt);
            em.getTransaction().commit();
        }
        else {
            em.getTransaction().begin();
            prt.setToken(token);
            prt.setExpiryDate();
            em.getTransaction().commit();
        }

        return prt;
    }

    private boolean sendPasswordResetLink(Admin admin, String userEmail, String token) {
        String subject = "checkEat: reset password";
  //      String url = "localhost:8080/resetPassword.html?token=" + token; //TODO real url
        String url = "https://vmedu126.mtacloud.co.il/checkEat/resetPassword.html?token=" + token;
        String body = "To reset your password click the link below:\n" + url + "\n";
        return ServletUtils.sendEmail(admin, userEmail, subject, body);
    }

    private void resetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String token = request.getParameter("token");
        String newPass = request.getParameter("password");

        PasswordResetToken prt = appManager.getPasswordResetToken(em, token);
        if(prt != null) {
            SignedUser signedUser = em.find(SignedUser.class, prt.getUsername());
            if(signedUser != null){
                em.getTransaction().begin();
                signedUser.setPassword(newPass);
                em.remove(prt);
                em.getTransaction().commit();
            }
            out.print("true");
        }
        else
            out.print("false");
        out.flush();
    }
}