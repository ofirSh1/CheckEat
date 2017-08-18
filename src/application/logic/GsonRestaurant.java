package application.logic;

import java.util.ArrayList;
import java.util.List;

public class GsonRestaurant extends SignedUser{
    private String restaurantName;
    private String city;
    private String link;
    private String contactName;
    private String contactPhone;
    private String logoUrl;
    private String street;
    private int streetNum;

    public List<GsonDish> getDishes() {
        return dishes;
    }

    private List<GsonDish> dishes = new ArrayList<>();

    public GsonRestaurant(Restaurant restaurant) {
        this.setUserName(restaurant.getUserName());
        this.setEmail(restaurant.getEmail());
        this.setType(Type.RESTAURANT);
        this.setPassword(restaurant.getPassword());
        this.setPassword2(restaurant.getPassword2());
        this.setPhone(restaurant.getPhone());
        this.restaurantName = restaurant.getRestaurantName();
        this.city = restaurant.getCity();
        this.link = restaurant.getLink();
        this.contactName = restaurant.getContactName();
        this.contactPhone = restaurant.getContactPhone();
        GsonDish gsonDish;
        for (Dish dish: restaurant.getDishes()) {
            gsonDish = new GsonDish(dish);
            this.dishes.add(gsonDish);
        }
        this.logoUrl = restaurant.getLogoUrl();
        this.street = restaurant.getStreet();
        this.streetNum = restaurant.getStreetNum();
    }
}
