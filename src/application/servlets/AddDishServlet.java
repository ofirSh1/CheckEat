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
import java.util.Date;

@WebServlet(name = "AddDishServlet", urlPatterns = {"/addDish"})
@MultipartConfig
public class AddDishServlet extends HttpServlet{

    private EntityManagerFactory emf;
    private EntityManager em;
    private boolean newRest;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        em = emf.createEntityManager();
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        PrintWriter out = response.getWriter();

        Dish dish = getDish(request, usernameFromSession);
        Restaurant restaurant = getRestaurant(request, appManager, dish);

        if(!dish.isValidDish()) {
            out.println("נא למלא שדות חובה");
        }
        else if(appManager.isDishExists(em, dish)) {
            out.println("המנה קיימת במסעדה");
        }
        else {
            SignedUser signedUser = em.find(SignedUser.class, usernameFromSession);
            if(signedUser == null) {
                out.println("יש להתחבר קודם");
            }
            else {
                if(signedUser.getType() == SignedUser.Type.CUSTOMER) {
                    Customer customer = (Customer)signedUser;
                    customer.getAddedDishes().add(dish);
                }
                try {
                    em.getTransaction().begin();
                    if (newRest)
                        em.persist(restaurant);
                    dish.getRestaurant().addDish(dish);
                    em.persist(dish);
                    em.getTransaction().commit();
                }
                finally {
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                    em.close();
                }
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String[] getStringArray(String arrStr) {
        String[] arr = null;
        if(arrStr != null)
            arrStr = arrStr.replace("[", "");
        if(arrStr != null)
           arrStr = arrStr.replace("]", "");
        if(arrStr != null)
            arrStr = arrStr.replace("\"", "");
        if(arrStr != null)
            arr = arrStr.split(",");
        return arr;
    }

    private Dish getDish(HttpServletRequest request, String usernameFromSession)
            throws ServletException, IOException {

        Dish dish = new Dish();
        dish.setDishName(request.getParameter(Constants.DISH_NAME));
        dish.setUploadDate(new Date());
        dish.setAddByUserName(usernameFromSession);
        dish.setSpecialTypes(getStringArray(request.getParameter("specialTypes")));
        dish.setOtherTypes(getStringArray(request.getParameter("otherTypes")));
        dish.setIngredients(getStringArray(request.getParameter("ingredients")));
        String logoUrl = ServletUtils.uploadImageToCloudinary(request.getPart("image"));
        if (logoUrl==null)
            logoUrl=ServletUtils.loadImageURL(request.getParameter("imageURL"));
        dish.setDishUrl(logoUrl);
        return dish;
    }

    private Restaurant getRestaurant(HttpServletRequest request, AppManager appManager, Dish dish) {
        String restName = request.getParameter(Constants.REST_NAME);
        String restCity = request.getParameter("restCity");
        String restStreet = request.getParameter("restStreet");
        String restStreetNum = request.getParameter("restStreetNum");
        Restaurant restaurant = appManager.getDishRestaurant(em, restName, restCity, restStreet, restStreetNum);
        newRest = restaurant == null;

        if (!newRest)
            dish.setRestaurant(restaurant);
        else { // add dish to restaurant that does not exist // TODO
            restaurant = new Restaurant();
            restaurant.setUserName(restName + "_" + restCity);
            restaurant.setPassword(restName + "_" + restCity);
            restaurant.setPassword2(restName + "_" + restCity);
            restaurant.setEmail("@");
            restaurant.setType(SignedUser.Type.RESTAURANT);
            restaurant.setRestaurantName(restName);
            restaurant.setCity(restCity);
            restaurant.setStreet(restStreet);
            restaurant.setStreetNum(restStreetNum);
            restaurant.setContactName(restName);
            restaurant.setLink(restName);
            dish.setRestaurant(restaurant);
        }

        return restaurant;
    }
}