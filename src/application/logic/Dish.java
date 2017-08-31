package application.logic;

import javax.persistence.*;
import java.util.*;

@Entity
public class Dish{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String dishName;
    private String dishUrl;
    private String addByUserName;
    private Date uploadDate;
    @ElementCollection
    private Set<String> specialTypes = new HashSet<>();
    @ElementCollection
    private Set<String> otherTypes = new HashSet<>();
    @ElementCollection
    private Set<String> ingredients = new HashSet<>();
    private int numLikes = 0;
   @OneToMany(mappedBy = "dish")
   private List<Comment> commentList = new ArrayList<>();
    @ManyToOne
    private Restaurant restaurant;

    //Setter
    public void setDishName(String name) {
        this.dishName = name;
    }

    public void setDishUrl(String dishUrl) { this.dishUrl = dishUrl; }

    public void setAddByUserName(String addByUserName) { this.addByUserName = addByUserName; }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void setSpecialTypes(String[] specialTypes) {
        this.specialTypes = new HashSet<String>(Arrays.asList(specialTypes));
    }

    public void setSpecialTypes(Set<String> specialTypes) {
        this.specialTypes = specialTypes;
    }

    public void setOtherTypes(String[] otherTypes) {
        this.otherTypes = new HashSet<String>(Arrays.asList(otherTypes));
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = new HashSet<String>(Arrays.asList(ingredients));
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    //Getter
    public int getId() { return id; }

    public String getDishName() { return dishName; }

    public String getDishUrl() { return dishUrl; }

    public String getAddByUserName() { return addByUserName; }

    public Date getUploadDate() {
        return uploadDate;
    }

    public Set<String> getSpecialTypes() { return specialTypes; }

    public Set<String> getOtherTypes() { return otherTypes; }

    public Set<String> getIngredients() { return ingredients; }

    public int getNumLikes() { return numLikes; }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public Restaurant getRestaurant() { return restaurant; }

    //Validation
    public boolean isValidDish() {
        return isStringValid(dishName);
    }

    public boolean isStringValid(String str) {
        boolean res = false;

        if(str != null) {
            String temp = new String(str);
            res = !temp.trim().isEmpty();
        }

        return res;
    }

    public void gsonDishToDish(GsonDish gsonDish, Restaurant restaurant){
        this.id = gsonDish.getId();
        this.dishName = gsonDish.getDishName();
        this.dishUrl = gsonDish.getDishUrl();
        this.addByUserName= "CheckEat";
        this.uploadDate = new Date();
        this.restaurant = restaurant;
        this.ingredients = gsonDish.getIngredients();
        this.specialTypes = gsonDish.getSpecialTypes();
        this.otherTypes = gsonDish.getOtherTypes();
    }
}