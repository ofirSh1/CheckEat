package application.servlets;

import application.logic.*;
import application.utils.Constants;
import application.utils.ServletUtils;
import application.utils.SessionUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@WebServlet(name = "SearchDishServlet", urlPatterns = {"/dish"})
@MultipartConfig
public class SearchDishServlet extends HttpServlet
{
    private EntityManagerFactory emf;
    private EntityManager em;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        em = emf.createEntityManager();
        request.setCharacterEncoding("UTF-8");
        String requestType = request.getParameter(Constants.REQUEST_TYPE);

        if (requestType.equals("loadCities")) {
            loadCities(request, response);
        }
        else if(requestType.equals("findDishes")) {
            findDishes(request, response);
        }
        else if (requestType.equals("checkIfSaved")) {
            checkIfDishSavedByUser(request,response);
        }
        else if (requestType.equals("checkIfLiked")){
            checkIfDishLikedByUser(request,response);
        }
        else if (requestType.equals("findDishesInRestaurant")){
            findDishesInRestaurant(request,response);
        }
        else if (requestType.equals("getRestaurantsNearby")){
            getRestaurantsNearby(request,response);
        }
        else if (requestType.equals("getDishesOrderedByLikes")){
            getDishesOrderedByLikes(request,response);
        }
        else if (requestType.equals("getDishesOrderedByUploadDate")){
            getDishesOrderedByUploadDate(request,response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void findDishesInRestaurant(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        Gson gson = new Gson();
        TypeToken<List<DishShowFormat>> token = new TypeToken<List<DishShowFormat>>(){};
        List<DishShowFormat> dishesSearchResult = gson.fromJson(request.getParameter("dishes"), token.getType());

        //List<DishShowFormat> dishesSearchResult = request.getParameterValues("dishes");
        String restName = request.getParameter("restName");
        List<DishShowFormat> result = appManager.getDishesInRestaurant(dishesSearchResult,restName);
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }

    private void checkIfDishLikedByUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        if (usernameFromSession != null) {
            Customer customer = em.find(Customer.class, usernameFromSession);
            if (customer != null) { // no signed user or the user is restaurant
                Dish dish = em.find(Dish.class, Integer.parseInt(request.getParameter("dishId")));
                if (customer.getLikedDished().contains(dish.getId())) // already liked this dish
                    out.println(true);
                else
                    out.println(false);
                out.flush();
            }
        }
    }

    private void checkIfDishSavedByUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
        if (usernameFromSession!=null) {
            Customer customer = em.find(Customer.class, usernameFromSession);
            if (customer != null) { // no signed user or the user is restaurant
                Dish dish = em.find(Dish.class, Integer.parseInt(request.getParameter("dishId")));
                if (customer.getFavoritesDishes().contains(dish)) // already saved this dish
                    out.println(true);
                else
                    out.println(false);
                out.flush();
            }
        }
    }

    private void findDishes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AppManager appManager = ServletUtils.getAppManager(getServletContext());

        DishSearchFormat dishSearchFormat = new DishSearchFormat();
        dishSearchFormat.setRestName(request.getParameter(Constants.REST_NAME));
        dishSearchFormat.setRestCity(request.getParameter(Constants.REST_CITY));
        dishSearchFormat.setDishName(request.getParameter(Constants.DISH_NAME));
        dishSearchFormat.setSpecialTypes(request.getParameterValues(Constants.SPECIAL_TYPES));
        dishSearchFormat.setOtherTypes(request.getParameterValues(Constants.OTHER_TYPES));
        dishSearchFormat.setIngredients(request.getParameterValues(Constants.INGREDIENTS));

        List<DishShowFormat> result = appManager.getDishes(em, dishSearchFormat);

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }

    private void loadCities(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        InputStream jsonInputFile = getClass().getClassLoader().getResourceAsStream("../resources/cities.txt");

        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(jsonInputFile, "UTF-8")));
        String lineSeparator = System.getProperty("line.separator");

        try {
            if (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine());
            }
            while (scanner.hasNextLine()) {
                fileContents.append(lineSeparator + scanner.nextLine());
            }
            out.println(fileContents.toString());
            out.flush();
        } finally {
            scanner.close();
        }
    }

    private void getRestaurantsNearby(HttpServletRequest request, HttpServletResponse response) throws IOException {

        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        String city = request.getParameter("city");
        List<RestaurantShowFormat> result = appManager.getRestaurantsNearby(em, city);

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }

    private void getDishesOrderedByLikes(HttpServletRequest request, HttpServletResponse response) throws IOException {

        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        List<DishShowFormat> result = appManager.getDishesOrderedByLikes(em);

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }

    private void getDishesOrderedByUploadDate(HttpServletRequest request, HttpServletResponse response) throws IOException {

        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        List<DishShowFormat> result = appManager.getDishesOrderedByUploadDate(em);

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }
}