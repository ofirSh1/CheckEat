package application.logic;

import javax.persistence.ElementCollection;
import java.util.*;

public class GsonDish {
    public int getId() {
        return id;
    }

    private int id;

    public String getDishName() {
        return dishName;
    }

    public String getDishUrl() {
        return dishUrl;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    private String dishName;

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantCity() {
        return restaurantCity;
    }

    public String getRestaurantStreet() {
        return restaurantStreet;
    }

    public int getRestaurantStreetNum() {
        return restaurantStreetNum;
    }

    private String dishUrl;
    private Date uploadDate;

    public int getNumLikes() {
        return numLikes;
    }

    public Set<String> getSpecialTypes() {
        return specialTypes;
    }

    public Set<String> getOtherTypes() {
        return otherTypes;
    }

    public Set<String> getIngredients() {
        return ingredients;
    }

    private Set<String> specialTypes = new HashSet<>();

    private Set<String> otherTypes = new HashSet<>();

    private Set<String> ingredients = new HashSet<>();
    private int numLikes = 0;
    @ElementCollection
    private Map<String,String> comments = new HashMap<>();
    private String restaurantName;
    private String restaurantCity;
    private String restaurantStreet;
    private int restaurantStreetNum;


    public GsonDish(Dish dish) {
        this.id = dish.getId();
        this.dishName = dish.getDishName();
        this.specialTypes = dish.getSpecialTypes();
        this.otherTypes = dish.getOtherTypes();
        this.ingredients = dish.getIngredients();
        this.restaurantName = dish.getRestaurant().getRestaurantName();
        this.restaurantCity = dish.getRestaurant().getCity();
        this.dishUrl = dish.getDishUrl();
        this.uploadDate = dish.getUploadDate();
        this.numLikes = dish.getNumLikes();
        this.comments = dish.getComments();
        this.restaurantStreet = dish.getRestaurant().getStreet();
        this.restaurantStreetNum = dish.getRestaurant().getStreetNum();
    }
}
