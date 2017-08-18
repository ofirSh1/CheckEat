package application.servlets;

import application.logic.*;
import application.utils.ServletUtils;
import com.cloudinary.utils.ObjectUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cloudinary.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


@WebServlet(name = "loadDataBaseServlet", urlPatterns = {"/loadDataBase"})
public class LoadDataBaseServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        EntityManager em = emf.createEntityManager();
        request.setCharacterEncoding("UTF-8");
        AppManager appManager = ServletUtils.getAppManager(getServletContext());

        InputStream jsonRestaurantsFile = getClass().getClassLoader().getResourceAsStream("../resources/dataBaseRestaurants.txt");
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(jsonRestaurantsFile, "UTF-8")));

        while (scanner.hasNextLine()) {
            fileContents.append(scanner.nextLine());
        }
        scanner.close();

        Gson gson = new Gson();
        TypeToken<List<Restaurant>> tokenRest = new TypeToken<List<Restaurant>>(){};
        List<Restaurant> restaurants = gson.fromJson(fileContents.toString(), tokenRest.getType());

        for (Restaurant restaurant: restaurants) {
            if (em.find(Restaurant.class,restaurant.getUserName())==null){ // if rest not in dataBase
                    restaurant.setLogoUrl(ServletUtils.loadImageURL(restaurant.getLogoUrl()));
                    em.getTransaction().begin();
                    restaurant.setType(SignedUser.Type.RESTAURANT);
                    em.persist(restaurant);
                    em.getTransaction().commit();

            }
        }

        InputStream jsonDishesFile = getClass().getClassLoader().getResourceAsStream("../resources/dataBaseDishes.txt");
        fileContents = new StringBuilder();
        scanner = new Scanner(new BufferedReader(new InputStreamReader(jsonDishesFile, "UTF-8")));


        while (scanner.hasNextLine()) {
            fileContents.append(scanner.nextLine());
        }
        scanner.close();

 /*       String content = fileContents.toString();
        String[] sentence = content.split(" ");
        for(String word: sentence)
        {
            if(word.equals("\"specialTypes\":")){

            }
            if (word.equals("\"otherTypes\":")){

            }
            if (word.equals("\"ingredients\":")){

            }
        }*/

        gson = new Gson();
        TypeToken<List<GsonDish>> tokenDish = new TypeToken<List<GsonDish>>(){};
        List<GsonDish> dishes = gson.fromJson(fileContents.toString(), tokenDish.getType());
        Dish newDish;
        Restaurant dishRestaurant;
        for (GsonDish dish: dishes) {
            if (em.find(Dish.class,dish.getId())==null){ // if dish not in dataBase
                    newDish = new Dish();
                    dishRestaurant = appManager.getDishRestaurant(em,dish.getRestaurantName(),dish.getRestaurantCity(),dish.getRestaurantStreet(),Integer.toString(dish.getRestaurantStreetNum()));
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