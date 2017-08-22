package application.logic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DishSearchFormat {
    private String restUsername;
    private String restName;
    private String restCity;
    private String dishName;
    private Set<String> specialTypes = new HashSet<>();
    private Set<String> otherTypes = new HashSet<>();
    private Set<String> ingredients = new HashSet<>();

    //Getter and Setter
    public String getRestUsername() {
        return restUsername;
    }

    public void setRestUsername(String restUsername) {
        this.restUsername = restUsername;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getRestCity() {
        return restCity;
    }

    public void setRestCity(String restCity) {
        this.restCity = restCity;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public Set<String> getSpecialTypes() {
        return specialTypes;
    }

    public void setSpecialTypes(String[] specialTypes) {
        if(specialTypes != null)
            this.specialTypes = new HashSet<String>(Arrays.asList(specialTypes));
    }

    public Set<String> getOtherTypes() {
        return otherTypes;
    }

    public void setOtherTypes(String[] otherTypes) {
        if(otherTypes != null)
            this.otherTypes = new HashSet<String>(Arrays.asList(otherTypes));
    }

    public Set<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        if(ingredients != null)
            this.ingredients = new HashSet<String>(Arrays.asList(ingredients));
    }
}