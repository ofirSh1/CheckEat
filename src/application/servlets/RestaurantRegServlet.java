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
import java.io.*;

@WebServlet(name = "RestaurantRegServlet", urlPatterns = {"/restaurantReg"}, loadOnStartup = 1)
@MultipartConfig
public class RestaurantRegServlet extends HttpServlet{

    private EntityManagerFactory emf;
    private EntityManager em;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        em = emf.createEntityManager();
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        AppManager appManager = ServletUtils.getAppManager(getServletContext());

        Restaurant restaurant = new Restaurant();
        restaurant.setUserName(request.getParameter(Constants.USERNAME));
        restaurant.setEmail(request.getParameter(Constants.EMAIL));
        restaurant.setPhone(request.getParameter(Constants.PHONE));
        restaurant.setPassword(request.getParameter(Constants.PASSWORD));
        restaurant.setPassword2(request.getParameter(Constants.VERIFY_PASSWORD));
        restaurant.setRestaurantName(request.getParameter(Constants.REST_NAME));
        restaurant.setCity(request.getParameter("city"));
        restaurant.setStreet(request.getParameter("street"));
        restaurant.setStreetNum(request.getParameter("streetNum"));
        restaurant.setLink(request.getParameter(Constants.REST_LINK));
        restaurant.setContactName(request.getParameter(Constants.CONTACT_NAME));
        restaurant.setContactPhone(request.getParameter(Constants.CONTACT_PHONE));
        restaurant.setType(SignedUser.Type.RESTAURANT);
        String logoUrl = ServletUtils.uploadImageToCloudinary(request.getPart("image"));
        if (logoUrl==null)
            logoUrl=ServletUtils.loadImageURL(request.getParameter("imageURL"));
        restaurant.setLogoUrl(logoUrl);

        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        if(usernameFromSession != null && !usernameFromSession.equals("CheckEat")) {
            ServletUtils.redirect(response, "הינך רשום כבר במערכת", "index.html");
        }
        else if(!restaurant.isValidUser()) {
            if (usernameFromSession != null && usernameFromSession.equals("CheckEat"))
                ServletUtils.redirect(response, "נא למלא שדות חובה", "duplicateRestaurant.html");
            else
                ServletUtils.redirect(response, "נא למלא שדות חובה", "restaurantReg.html");
        }
        else if(!restaurant.isValidPassword()) {
            if (usernameFromSession != null && usernameFromSession.equals("CheckEat"))
                ServletUtils.redirect(response, "שגיאה באישור סיסמא", "duplicateRestaurant.html");
            else
                ServletUtils.redirect(response, "שגיאה באישור סיסמא", "customerReg.html");
        }
        else if(appManager.isUserExists(em, restaurant.getUserName())) {
            if (usernameFromSession != null && usernameFromSession.equals("CheckEat"))
                ServletUtils.redirect(response, "שם משתמש כבר קיים, נא לבחור שם אחר", "duplicateRestaurant.html");
            else
                ServletUtils.redirect(response, "שם משתמש כבר קיים, נא לבחור שם אחר", "restaurantReg.html");
        }
        // TODO: check if the restaurant exist in this city
        else {
            try{
                em.getTransaction().begin();
                em.persist(restaurant);
                em.getTransaction().commit();
            }
            finally {
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
                em.close();
            }
            if (usernameFromSession != null && usernameFromSession.equals("CheckEat")) // TODO check
                ServletUtils.redirect(response, "ההרשמה עברה בהצלחה", "admin.html");
            else {
                request.getSession(true).setAttribute(Constants.USERNAME, restaurant.getUserName());
                ServletUtils.redirect(response, "ההרשמה עברה בהצלחה", "index.html");
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}