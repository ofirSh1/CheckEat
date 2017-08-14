package application.logic;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;


public class AppManager {
    public boolean isUserExists(EntityManager em, String name) {
        javax.persistence.TypedQuery<SignedUser> query =
                em.createQuery("SELECT user FROM SignedUser user WHERE user.userName = :name", SignedUser.class);
        query.setParameter("name", name);
        List<SignedUser> res = query.getResultList();

        if (res.isEmpty())
            return false;
        else
            return true;
    }

    public List<DishShowFormat> getDishesNearby(EntityManager em, String city)
    {
        javax.persistence.TypedQuery<Dish> query =
                em.createQuery("SELECT dish FROM Dish dish WHERE dish.restaurant.city = :city", Dish.class);
        query.setParameter("city", city);
        List<Dish> res = query.getResultList();

        List<DishShowFormat> result = new ArrayList<>();
        for(Dish d: res)
            result.add(new DishShowFormat(d));
        return result;
    }

    private void equalQuery(List<Predicate> predicates, CriteriaBuilder cb,
                            String reqPar, Expression<String> par) {
        if (reqPar != null)
            predicates.add(cb.equal(par, reqPar));
    }

    private void containQuery(List<Predicate> predicates, CriteriaBuilder cb,
                              Set<String> reqSet, Expression<Collection<String>> collection) {
        if (reqSet != null && !reqSet.isEmpty()) {
            for (String val : reqSet) {
                if(!val.trim().isEmpty())
                    predicates.add(cb.isMember(val, collection));
            }
        }
    }

    public boolean isDishExists(EntityManager em, Dish i_dish) {
        javax.persistence.TypedQuery<Dish> query =
                em.createQuery("SELECT dish FROM Dish dish WHERE dish.dishName = :dishName" +
                        " AND dish.restaurant.restaurantName = :restName" +
                        " AND dish.restaurant.city = :restCity", Dish.class);
        query.setParameter("dishName", i_dish.getDishName());
        query.setParameter("restName", i_dish.getRestaurant().getRestaurantName());
        query.setParameter("restCity", i_dish.getRestaurant().getCity());
        List<Dish> res = query.getResultList();

        if (res.isEmpty())
            return false;
        else
            return true;
    }

    public List<DishShowFormat> getDishesInRestaurant(List<DishShowFormat> dishesSearchResult, String restName) {
        List<DishShowFormat> result = new ArrayList<>();
        for(DishShowFormat dish: dishesSearchResult)
            if (dish.getRestName().equals(restName))
                result.add(dish);
        return result;
    }

    public List<DishShowFormat> getDishes(EntityManager em, DishSearchFormat dSFormat) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Dish> q = cb.createQuery(Dish.class);
        Root<Dish> dish = q.from(Dish.class);

        List<Predicate> predicates = new ArrayList<>();
        // the user entered rest name
        if (dSFormat.getRestName() != null && !dSFormat.getRestName().isEmpty())
            equalQuery(predicates, cb, dSFormat.getRestName(), dish.get("restaurant").get("restaurantName"));
        // the user entered rest city
        if (dSFormat.getRestCity() != null && !dSFormat.getRestCity().isEmpty())
            equalQuery(predicates, cb, dSFormat.getRestCity(), dish.get("restaurant").get("city"));
        // the user entered dish name
        if (dSFormat.getDishName() != null && !dSFormat.getDishName().isEmpty())
            equalQuery(predicates, cb, dSFormat.getDishName(), dish.get("dishName"));
        // the user entered special types
        if(dSFormat.getSpecialTypes() != null && !dSFormat.getSpecialTypes().isEmpty())
            containQuery(predicates, cb, dSFormat.getSpecialTypes(), dish.get("specialTypes"));
        // the user entered other types
        if(dSFormat.getOtherTypes() != null && !dSFormat.getOtherTypes().isEmpty())
            containQuery(predicates, cb, dSFormat.getOtherTypes(), dish.get("otherTypes"));
        // the user entered ingredients
        if(dSFormat.getIngredients() != null && !dSFormat.getIngredients().isEmpty())
            containQuery(predicates, cb, dSFormat.getIngredients(), dish.get("ingredients"));

        q.select(dish);
        if(!predicates.isEmpty())
            q.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Dish> tq = em.createQuery(q);
        List<Dish> qr = tq.getResultList();
        List<DishShowFormat> result = new ArrayList<>();
        for(Dish d: qr)
            result.add(new DishShowFormat(d));
        return result;
    }

    public List<DishShowFormat> getDishesOrderedByLikes(EntityManager em) {
        javax.persistence.TypedQuery<Dish> query =
                em.createQuery("SELECT dish FROM Dish dish " +
                        "ORDER BY dish.numLikes DESC", Dish.class);

        List<Dish> res = query.getResultList();
        List<DishShowFormat> result = new ArrayList<>();
        for(Dish d: res) {
            if(d.getId() == 46 || d.getId()== 53 || d.getId() == 61)
                result.add(new DishShowFormat(d));
        }
        return result;
    }

    public List<DishShowFormat> getDishesOrderedByUploadDate(EntityManager em) {
        javax.persistence.TypedQuery<Dish> query =
                em.createQuery("SELECT dish FROM Dish dish " +
                        "ORDER BY dish.uploadDate DESC", Dish.class);

        List<Dish> res = query.getResultList();
        List<DishShowFormat> result = new ArrayList<>();
        for(Dish d: res) {
            if(d.getId() == 63 || d.getId()== 64 || d.getId() == 65)
            result.add(new DishShowFormat(d));
        }
        return result;
    }

    public List<RestaurantShowFormat> getRestaurantsNearby(EntityManager em, String city)
    {
        city = "תל אביב יפו";
        if (city == null || city.isEmpty())
            return null;

        javax.persistence.TypedQuery<Restaurant> query =
                em.createQuery("SELECT rest FROM Restaurant rest" +
                " WHERE rest.city = :city", Restaurant.class);
        query.setParameter("city", city);
        List<Restaurant> res = query.getResultList();

        List<RestaurantShowFormat> result = new ArrayList<>();
        for(Restaurant r: res)
            result.add(new RestaurantShowFormat(r));
        return result;
    }

    public Restaurant getDishRestaurant(EntityManager em, String restName, String restCity, String restStreet, String restStreetNum) {
        javax.persistence.TypedQuery<Restaurant> query =
                em.createQuery("SELECT rest FROM Restaurant rest WHERE rest.restaurantName = :restName " +
                        "AND rest.city = :restCity AND rest.street = :restStreet AND rest.streetNum = :restStreetNum", Restaurant.class);

        query.setParameter("restName", restName);
        query.setParameter("restCity", restCity);
        query.setParameter("restStreet", restStreet);
        query.setParameter("restStreetNum", restStreetNum);

        List<Restaurant> res = query.getResultList();
        Restaurant dishRest = null;
        if(!res.isEmpty())
            dishRest = res.get(0);

        return dishRest;
    }
}