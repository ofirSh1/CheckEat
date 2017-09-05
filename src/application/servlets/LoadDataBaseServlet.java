package application.servlets;

import application.logic.*;
import application.utils.Constants;
import application.utils.ServletUtils;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;


@WebServlet(name = "loadDataBaseServlet", urlPatterns = {"/loadDataBase"})
@MultipartConfig
public class LoadDataBaseServlet extends HttpServlet {

    private EntityManager em;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        em = emf.createEntityManager();
        request.setCharacterEncoding("UTF-8");
        String requestType = request.getParameter(Constants.REQUEST_TYPE);

        if(requestType.equals("addRestaurants")) {
            addRestaurants(request, response);
        }
        else if (requestType.equals("addDishes")){
            addDishes(request,response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void addRestaurants(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        InputStream jsonRestaurantsFile = request.getPart("restFile").getInputStream();
        loadRestaurantsFile(jsonRestaurantsFile);
    }

    private void addDishes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        InputStream jsonDishesFile = request.getPart("dishFile").getInputStream();
        loadDishesFile(jsonDishesFile);
    }

    private void loadRestaurantsFile(InputStream jsonRestaurantsFile)
            throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(jsonRestaurantsFile, "UTF-8")));

        while (scanner.hasNextLine())
            fileContents.append(scanner.nextLine());
        scanner.close();

        Gson gson = new Gson();
        TypeToken<List<Restaurant>> tokenRest = new TypeToken<List<Restaurant>>(){};
        List<Restaurant> restaurants = gson.fromJson(fileContents.toString(), tokenRest.getType());

        for (Restaurant restaurant: restaurants) {
            if (em.find(Restaurant.class,restaurant.getUserName()) == null){ // if rest not in dataBase
                restaurant.setLogoUrl(ServletUtils.loadImageURL(restaurant.getLogoUrl()));
                em.getTransaction().begin();
                restaurant.setType(SignedUser.Type.RESTAURANT);
                em.persist(restaurant);
                em.getTransaction().commit();

            }
        }
    }

    private void loadDishesFile(InputStream jsonDishesFile)
            throws IOException{
        AppManager appManager = ServletUtils.getAppManager(getServletContext());
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(jsonDishesFile, "UTF-8")));

        while (scanner.hasNextLine())
            fileContents.append(scanner.nextLine());
        scanner.close();

        Gson gson = new Gson();
        TypeToken<List<GsonDish>> tokenDish = new TypeToken<List<GsonDish>>(){};
        List<GsonDish> dishes = gson.fromJson(fileContents.toString(), tokenDish.getType());
        Dish newDish;
        Restaurant dishRestaurant;
        for (GsonDish dish: dishes) {
            if (em.find(Dish.class,dish.getId()) == null){ // if dish not in dataBase
                newDish = new Dish();
                dishRestaurant = appManager.getDishRestaurant(em, dish.getRestaurantName(), dish.getRestaurantCity(),
                        dish.getRestaurantStreet(), dish.getRestaurantStreetNum());
                newDish.gsonDishToDish(dish,dishRestaurant);
                newDish.setDishUrl(ServletUtils.loadImageURL(dish.getDishUrl()));
                em.getTransaction().begin();
                em.persist(newDish);
                dishRestaurant.addDish(newDish);
                em.getTransaction().commit();
            }
        }
    }
}