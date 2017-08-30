package application.logic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class GsonDish {

    private int id;
    private String dishName;
    private String dishUrl;
    private Date uploadDate;
    private Set<String> specialTypes = new HashSet<>();
    private Set<String> otherTypes = new HashSet<>();
    private Set<String> ingredients = new HashSet<>();
    private int numLikes = 0;
    private String restaurantName;
    private String restaurantCity;
    private String restaurantStreet;
    private String restaurantStreetNum;
    private String restUsername;
    private String addByUserName;
    private String restLink;
    private String restUrl;
    private String dateStr;

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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.dateStr = sdf.format(this.uploadDate);
        this.numLikes = dish.getNumLikes();
        this.restaurantStreet = dish.getRestaurant().getStreet();
        this.restaurantStreetNum = dish.getRestaurant().getStreetNum();
        this.restUsername = dish.getRestaurant().getUserName();
        this.addByUserName = dish.getAddByUserName();
        this.restLink = dish.getRestaurant().getLink();
        this.restUrl = dish.getRestaurant().getLogoUrl();
    }

    //Getter
    public int getId() {
        return id;
    }

    public String getDishName() {
        return dishName;
    }

    public String getDishUrl() {
        return dishUrl;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public String getRestUsername() {
        return restUsername;
    }

    public String getDateStr() { return dateStr; }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantCity() {
        return restaurantCity;
    }

    public String getRestaurantStreet() {
        return restaurantStreet;
    }

    public String getRestaurantStreetNum() {
        return restaurantStreetNum;
    }

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



}
