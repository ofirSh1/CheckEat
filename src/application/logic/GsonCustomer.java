package application.logic;

import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class GsonCustomer  extends SignedUser {
    public List<GsonDish> getAddedDishes() {
        return addedDishes;
    }

    private List<GsonDish> addedDishes = new ArrayList<>();
    private List<GsonDish> favoritesDishes = new ArrayList<>();

    public GsonCustomer(Customer customer) {
        this.setUserName(customer.getUserName());
        this.setEmail(customer.getEmail());
        this.setType(Type.RESTAURANT);
        this.setPassword(customer.getPassword());
        this.setPassword2(customer.getPassword2());
        this.setPhone(customer.getPhone());
        GsonDish gsonDish;
        for (Dish dish: customer.getAddedDishes()) {
            gsonDish = new GsonDish(dish);
            this.addedDishes.add(gsonDish);
        }
        for (Dish dish: customer.getFavoritesDishes()) {
            gsonDish = new GsonDish(dish);
            this.favoritesDishes.add(gsonDish);
        }
    }
}
