package application.servlets;

import application.logic.*;
import application.utils.Constants;
import application.utils.ServletUtils;
import application.utils.SessionUtils;
import com.google.gson.Gson;

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

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
@MultipartConfig
public class AdminServlet extends HttpServlet {
    EntityManagerFactory emf;
    EntityManager em;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        em = emf.createEntityManager();
        request.setCharacterEncoding("UTF-8");
        String requestType = request.getParameter(Constants.REQUEST_TYPE);

        if (requestType.equals("contact")) {
            contact(request, response);
        }
        else if (requestType.equals("getCustomers")){
            getCustomers(request,response);
        }
        else if (requestType.equals("getRestaurants")){
            getRestaurants(request,response);
        }
        else if (requestType.equals("removeUser")){
            removeUser(request,response);
        }
        else if (requestType.equals("getAdminMsg")) {
            getAdminMsg(request, response);
        }
        else if (requestType.equals("removeMsg")) {
            removeMsg(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void contact(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String userEmail = request.getParameter("email");
        String phone = request.getParameter("phone");
        String reason = request.getParameter("request");
        String content = request.getParameter("content");

        ContactMsg contactMsg = new ContactMsg();
        contactMsg.setName(name);
        contactMsg.setEmail(userEmail);
        contactMsg.setPhone(phone);
        contactMsg.setRequest(reason);
        contactMsg.setContent(content);
        Admin admin = em.find(Admin.class, "CheckEat");
        if (admin!=null) {
            em.getTransaction().begin();
            em.persist(contactMsg);
            admin.getMsgs().add(contactMsg);
            em.getTransaction().commit();
        }
        else
        {
            admin = new Admin();
            em.getTransaction().begin();
            em.persist(contactMsg);
            em.persist(admin);
            admin.getMsgs().add(contactMsg);
            em.getTransaction().commit();
        }

        StringBuilder body = new StringBuilder();
        body.append("שם: ");
        body.append(name);
        body.append("\n");
        body.append("אימייל: ");
        body.append(userEmail);
        body.append("\n");
        body.append("טלפון: ");
        body.append(phone);
        body.append("\n");
        body.append("סיבת הפנייה: ");
        body.append(reason);
        body.append("\n");
        body.append("תוכן: ");
        body.append(content);
        body.append("\n");

        String subject = "בקשה ליצירת קשר";

        if (ServletUtils.sendEmail(admin, admin.getEmail(), subject, body.toString()))
            ServletUtils.redirect(response, "הבקשה נקלטה בהצלחה", "index.html");
        else
            ServletUtils.redirect(response, "לא היה ניתן לשלוח את הבקשה", "contact.html");
    }

    private void getCustomers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            AppManager appManager = ServletUtils.getAppManager(getServletContext());
            List<GsonCustomer> result = appManager.getCustomers(em);
            Gson gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }

    private void getRestaurants(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            AppManager appManager = ServletUtils.getAppManager(getServletContext());
            List<GsonRestaurant> result = appManager.getRestaurants(em);
            Gson gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }

    private void removeUser(HttpServletRequest request, HttpServletResponse response) {
        String userToDelete = request.getParameter("userName");
        SignedUser signedUser = em.find(SignedUser.class, userToDelete);
        if(signedUser != null) {
            em.getTransaction().begin();
            em.remove(signedUser);
            em.getTransaction().commit();
        }
    }

    private void getAdminMsg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        Admin admin = em.find(Admin.class,usernameFromSession);
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String json = gson.toJson(admin.getMsgs());
        out.println(json);
        out.flush();
    }

    private void removeMsg(HttpServletRequest request, HttpServletResponse response) {
        int numMsg = Integer.parseInt(request.getParameter("numMsg"));
        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        Admin admin = em.find(Admin.class, usernameFromSession);
        em.getTransaction().begin();
        admin.getMsgs().remove(admin.getMsgs().get(numMsg));
        em.getTransaction().commit();
    }
}