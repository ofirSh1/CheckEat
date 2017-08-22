package application.logic;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer extends SignedUser {
    public List<Dish> getAddedDishes() {
        return addedDishes;
    }

    @OneToMany
    private List<Dish> addedDishes = new ArrayList<>();

    public List<Dish> getFavoritesDishes() {
        return favoritesDishes;
    }

    @OneToMany
    private List<Dish> favoritesDishes = new ArrayList<>();

  /*  @ElementCollection // TODO check
    private List<Integer> likedDished = new ArrayList<>();

    public List<Integer> getLikedDished() {
        return likedDished;
    }*/
}
