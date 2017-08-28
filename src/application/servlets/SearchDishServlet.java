package application.servlets;

import application.logic.*;
import application.utils.Constants;
import application.utils.ServletUtils;
import application.utils.SessionUtils;
import com.google.gson.Gson;
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
        else if (requestType.equals("findDishesInRestaurant")){ // TODO delete
            findDishesInRestaurant(request,response);
        }
        else if (requestType.equals("getDishesOrderedByLikes")){
            getDishesOrderedByLikes(request,response);
        }
        else if (requestType.equals("getDishesOrderedByUploadDate")){
            getDishesOrderedByUploadDate(request,response);
        }
        else if (requestType.equals("getFilteredRestaurantDishes")){
            getFilteredRestaurantDishes(request,response);
        }
        else if (requestType.equals("getDishComments")){
            getDishComments(request,response);
        }
        else if (requestType.equals("getLatestComments")){
            getLatestComments(request,response);
        }
        else if (requestType.equals("likeDislikeDish")){
            likeAndDislike(request,response);
        }
        else if (requestType.equals("addComment")){
            addComment(request,response);
        }
        else if (requestType.equals("deleteComment")){
            deleteComment(request,response);
        }
        else if (requestType.equals("canDeleteComment")){
            canDeleteComment(request,response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // TODO delete
    private void findDishesInRestaurant(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        Gson gson = new Gson();
        TypeToken<List<GsonDish>> token = new TypeToken<List<GsonDish>>(){};
        List<GsonDish> dishesSearchResult = gson.fromJson(request.getParameter("dishes"), token.getType());

        String restName = request.getParameter("restName");
        List<GsonDish> result = appManager.getDishesInRestaurant(dishesSearchResult,restName);
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }

    private void findDishes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        List<GsonDish> result = new ArrayList<>();

        if (request.getParameter("searchNearBy").equals("true")) {
            Gson gson = new Gson();
            TypeToken<List<GsonRestaurant>> token = new TypeToken<List<GsonRestaurant>>(){};
            List<GsonRestaurant> restaurantsNearBy = gson.fromJson(request.getParameter("restaurantsNearby"), token.getType());
            Set<String> cities = new HashSet<>();
            for (GsonRestaurant rest: restaurantsNearBy) {
                cities.add(rest.getCity());
            }
            for (String city: cities){
                DishSearchFormat dishSearchFormat = new DishSearchFormat();
                dishSearchFormat.setRestName(request.getParameter(Constants.REST_NAME));
                dishSearchFormat.setRestCity(city);
                dishSearchFormat.setDishName(request.getParameter(Constants.DISH_NAME));
                dishSearchFormat.setSpecialTypes(request.getParameterValues(Constants.SPECIAL_TYPES));
                dishSearchFormat.setOtherTypes(request.getParameterValues(Constants.OTHER_TYPES));
                dishSearchFormat.setIngredients(request.getParameterValues(Constants.INGREDIENTS));
                result.addAll(appManager.getDishes(em, dishSearchFormat));
            }
        }
        else {
            DishSearchFormat dishSearchFormat = new DishSearchFormat();
            dishSearchFormat.setRestName(request.getParameter(Constants.REST_NAME));
            dishSearchFormat.setRestCity(request.getParameter(Constants.REST_CITY));
            dishSearchFormat.setDishName(request.getParameter(Constants.DISH_NAME));
            dishSearchFormat.setSpecialTypes(request.getParameterValues(Constants.SPECIAL_TYPES));
            dishSearchFormat.setOtherTypes(request.getParameterValues(Constants.OTHER_TYPES));
            dishSearchFormat.setIngredients(request.getParameterValues(Constants.INGREDIENTS));
            result = appManager.getDishes(em, dishSearchFormat);
        }

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

    private void getDishesOrderedByLikes(HttpServletRequest request, HttpServletResponse response) throws IOException {

        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        List<GsonDish> result = appManager.getDishesOrderedByLikes(em);

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
        List<GsonDish> result = appManager.getDishesOrderedByUploadDate(em);

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }

    private void getFilteredRestaurantDishes(HttpServletRequest request, HttpServletResponse response) throws IOException {

        AppManager appManager = ServletUtils.getAppManager(getServletContext());

        DishSearchFormat dishSearchFormat = new DishSearchFormat();
        dishSearchFormat.setRestUsername(request.getParameter("restUsername"));
        dishSearchFormat.setSpecialTypes(request.getParameterValues(Constants.SPECIAL_TYPES));
        dishSearchFormat.setOtherTypes(request.getParameterValues(Constants.OTHER_TYPES));
        dishSearchFormat.setIngredients(request.getParameterValues(Constants.INGREDIENTS));

        List<GsonDish> result = appManager.getDishes(em, dishSearchFormat);

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }

    //LIKE*************************************************************************************************************
    private void likeAndDislike(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
            Dish dish = em.find(Dish.class, Integer.parseInt(request.getParameter("dishId")));
            if (usernameFromSession != null) {
                SignedUser signedUser = em.find(SignedUser.class, usernameFromSession);
                if (signedUser != null) {
                    if (!signedUser.getLikedDishes().contains(dish.getId()))
                        likeDish(dish, signedUser);
                    else
                        dislikeDish(dish, signedUser);

                    out.print("true");
                    out.flush();
                }
            }
        }
    }

    private void likeDish(Dish dish, SignedUser signedUser) {
        try {
            em.getTransaction().begin();
            signedUser.getLikedDishes().add(dish.getId());
            dish.setNumLikes(dish.getNumLikes() + 1);;
            em.getTransaction().commit();
        }
        finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }

    private void dislikeDish(Dish dish, SignedUser signedUser) {
        try {
            em.getTransaction().begin();
            signedUser.getLikedDishes().remove((Integer) dish.getId());
            dish.setNumLikes(dish.getNumLikes() - 1);
            em.getTransaction().commit();
        }
        finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }

    //COMMENT**********************************************************************************************************
    private void getDishComments(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            AppManager appManager = ServletUtils.getAppManager(getServletContext());
            List<CommentShowFormat> result = appManager.getDishComments(em, Integer.parseInt(request.getParameter("dishId")));
            Gson gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }

    private void getLatestComments(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            AppManager appManager = ServletUtils.getAppManager(getServletContext());
            List<CommentShowFormat> result = appManager.getLatestComments(em);
            Gson gson = new Gson();
            String json = gson.toJson(result);
            out.println(json);
            out.flush();
        }
    }

    private void addComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Date date = new Date();
            Dish dish = em.find(Dish.class, Integer.parseInt(request.getParameter("dishId")));
            String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
            String content = request.getParameter("content");
            if (usernameFromSession != null && dish != null && ServletUtils.isValidString(content)) {
                SignedUser signedUser = em.find(SignedUser.class, usernameFromSession);
                if (signedUser != null) {
                    Comment comment = new Comment();
                    comment.setDetails(dish, signedUser, content, date);
                    try {
                        em.getTransaction().begin();
                        em.persist(comment);
                        dish.getCommentList().add(comment);
                        em.getTransaction().commit();
                        out.print("true");
                        out.flush();
                    } finally {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                    }
                }
            }
        }
    }

    private void deleteComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Dish dish = em.find(Dish.class, Integer.parseInt(request.getParameter("dishId")));
            Comment comment = em.find(Comment.class, Integer.parseInt(request.getParameter("commentId")));
            if (dish != null && comment != null && dish.getCommentList().contains(comment)) {
                try {
                    em.getTransaction().begin();
                    dish.getCommentList().remove(comment);
                    em.remove(comment);
                    em.getTransaction().commit();
                    out.print("true");
                    out.flush();
                }
                finally {
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                    em.close();
                }
            }
        }
    }

    private void canDeleteComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Comment comment = em.find(Comment.class, Integer.parseInt(request.getParameter("commentId")));
            Dish dish = comment.getDish();
            Restaurant restaurant = dish.getRestaurant();
            String usernameFromSession = SessionUtils.getParameter(request, Constants.USERNAME);
            if (comment != null && dish.getCommentList().contains(comment) && usernameFromSession != null) {
                if (usernameFromSession.equals("CheckEat")
                        || usernameFromSession.equals(comment.getUserName())
                        || usernameFromSession.equals(restaurant.getUserName())) {
                    out.print("true");
                    out.flush();
                }
            }
        }
    }
}