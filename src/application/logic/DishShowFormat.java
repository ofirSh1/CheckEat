package application.logic;

import java.text.SimpleDateFormat;
import java.util.*;

public class DishShowFormat {
    private int dishId;
    private String dishName;
    private String uploadDate;
    private String dishUrl;
    private Set<String> specialTypes;
    private Set<String> otherTypes;
    private Set<String> ingredients;
    private String restUsername;
    private String restName;
    private String restCity;
    private String restStreet;
    private int restStreetNum;
    private String restlink;
    private String restUrl;
    private String addByUserName;
    private int numLikes;
    private Map<String,String> comments;

    public DishShowFormat(Dish dish) {
        this.dishId = dish.getId();
        this.dishName = dish.getDishName();
        setUploadDate(dish.getUploadDate());
        this.dishUrl = dish.getDishUrl();
        this.specialTypes = dish.getSpecialTypes();
        this.otherTypes = dish.getOtherTypes();
        this.ingredients = dish.getIngredients();
        this.restUsername = dish.getRestaurant().getUserName();
        this.restName = dish.getRestaurant().getRestaurantName();
        this.restCity = dish.getRestaurant().getCity();
        this.restStreet = dish.getRestaurant().getStreet();
        this.restStreetNum = dish.getRestaurant().getStreetNum();
        this.restlink = dish.getRestaurant().getLink();
        this.restUrl = dish.getRestaurant().getLogoUrl();
        this.addByUserName = dish.getAddByUserName();
        this.numLikes = dish.getNumLikes();
        this.comments = dish.getComments();
    }

    private void setUploadDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.uploadDate = sdf.format(date);
    }

    public int getDishId() {
        return dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public Set<String> getSpecialTypes() { return specialTypes; }

    public Set<String> getOtherTypes() { return otherTypes; }

    public Set<String> getIngredients() { return ingredients; }

    public String getRestName() {
        return restName;
    }

    public String getRestCity() {
        return restCity;
    }

    public String getRestlink() {
        return restlink;
    }
}