package application.logic;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Restaurant extends SignedUser {
    private String restaurantName;
    private String link;
    private String contactName;
    private String contactPhone;
    private String logoUrl;
    private String city;
    private String street;
    private String streetNum;
    @OneToMany(mappedBy = "restaurant")
    private List<Dish> dishes = new ArrayList<>();

    //Setter
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setStreetNum(String streetNum) {
        this.streetNum = streetNum;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    //Getter
    public String getRestaurantName() {
        return restaurantName;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNum() {
        return streetNum;
    }

    public String getLink() {
        return link;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    //Add dish
    public void addDish(Dish dish) {
        this.dishes.add(dish);
        if (dish.getRestaurant() != this) {
            dish.setRestaurant(this);
        }
    }

    //validation
    @Override
    public boolean isValidUser() {
        return super.isValidUser() &&
                isStringValid(restaurantName) &&
                isStringValid(contactName) &&
                isStringValid(city);
    }
}