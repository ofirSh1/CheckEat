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

        return !res.isEmpty();
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

        return !res.isEmpty();
    }

    public List<GsonDish> getDishesInRestaurant(List<GsonDish> dishesSearchResult, String restName) {
        List<GsonDish> result = new ArrayList<>();
        for(GsonDish dish: dishesSearchResult)
            if (dish.getRestaurantName().equals(restName))
                result.add(dish);
        return result;
    }

    public List<GsonDish> getDishes(EntityManager em, DishSearchFormat dSFormat) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Dish> q = cb.createQuery(Dish.class);
        Root<Dish> dish = q.from(Dish.class);

        List<Predicate> predicates = new ArrayList<>();
        // search by rest id
        if (dSFormat.getRestUsername() != null && !dSFormat.getRestUsername().isEmpty())
            equalQuery(predicates, cb, dSFormat.getRestUsername(), dish.get("restaurant").get("userName"));
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
        //if(dSFormat.getOtherTypes() != null && !dSFormat.getOtherTypes().isEmpty())
        //containQuery(predicates, cb, dSFormat.getOtherTypes(), dish.get("otherTypes"));
        // the user entered ingredients
        //if(dSFormat.getIngredients() != null && !dSFormat.getIngredients().isEmpty())
        //   containQuery(predicates, cb, dSFormat.getIngredients(), dish.get("ingredients"));

        q.select(dish);
        if(!predicates.isEmpty())
            q.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Dish> tq = em.createQuery(q);
        List<Dish> qr = tq.getResultList();
        List<GsonDish> result = new ArrayList<>();
        for(Dish d: qr) {
            if(matchesOrders(dSFormat.getIngredients(), d.getIngredients())
                    && matchesOrders(dSFormat.getOtherTypes(), d.getOtherTypes()))
                result.add(new GsonDish(d));
        }
        return result;
    }

    private boolean matchesOrders(Set<String> orders, Set<String> set) {
        if(orders == null || orders.isEmpty()) //no orders
            return true;
        if(set == null || set.isEmpty()) //orders but no categories in dish
            return false;

        boolean found = false;
        int dis;
        int maxLen;
        double p;
        for(String s1 : orders) {
            if(s1.equals("")){
                found = true;
                continue;
            }
            found = false;
            for (String s2 : set) {
                if (s2.equals(""))
                    continue;
                dis = levenshteinDistance(s1, s2);
                maxLen = Math.max(s1.length(), s2.length());
                p = ((double)dis) / maxLen;
                if(p <= 0.3) {
                    found = true;
                    break;
                }
            }
            if(!found)
                break;
        }
        return found;
    }

    private int levenshteinDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    public List<GsonDish> getDishesOrderedByLikes(EntityManager em) {
        javax.persistence.TypedQuery<Dish> query =
                em.createQuery("SELECT dish FROM Dish dish " +
                        "ORDER BY dish.numLikes DESC", Dish.class);

        List<Dish> res = query.getResultList();
        List<GsonDish> result = new ArrayList<>();
        for(Dish d: res) {
            result.add(new GsonDish(d));
        }
        return result;
    }

    public List<GsonDish> getDishesOrderedByUploadDate(EntityManager em) {
        javax.persistence.TypedQuery<Dish> query =
                em.createQuery("SELECT dish FROM Dish dish " +
                        "ORDER BY dish.uploadDate DESC", Dish.class);

        List<Dish> res = query.getResultList();
        List<GsonDish> result = new ArrayList<>();
        for(Dish d: res) {
            result.add(new GsonDish(d));
        }
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

    public List<CommentShowFormat> getDishComments(EntityManager em, int dishId) {
        List<CommentShowFormat> result = new ArrayList<>();
        Dish dish = em.find(Dish.class, dishId);
        List<Comment> comments = dish.getCommentList();
        sortCommentsByDate(comments);
        for(Comment c : comments){
            CommentShowFormat cs = new CommentShowFormat(c);
            result.add(cs);
        }

        return result;
    }

    public List<CommentShowFormat> getLatestComments(EntityManager em) {
        javax.persistence.TypedQuery<Comment> query =
                em.createQuery("SELECT comment FROM Comment comment", Comment.class);

        List<Comment> res = query.getResultList();
        sortCommentsByDate(res);
        List<CommentShowFormat> result = new ArrayList<>();
        for(Comment c: res) {
            result.add(new CommentShowFormat(c));
        }
        return result;
    }

    private void sortCommentsByDate(List<Comment> comments) {
        Collections.sort(comments, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        Collections.reverse(comments);
    }

    public PasswordResetToken getUserPasswordResetToken(EntityManager em, String username) {
        PasswordResetToken prt = null;
        javax.persistence.TypedQuery<PasswordResetToken> query =
                em.createQuery("SELECT p FROM PasswordResetToken p " +
                        "WHERE p.username = :username", PasswordResetToken.class);

        query.setParameter("username", username);
        List<PasswordResetToken> res = query.getResultList();

        if(!res.isEmpty()) {
            prt = res.get(0);
        }

        return prt;
    }

    public PasswordResetToken getPasswordResetToken(EntityManager em, String token) {
        PasswordResetToken prt = null;
        javax.persistence.TypedQuery<PasswordResetToken> query =
                em.createQuery("SELECT p FROM PasswordResetToken p " +
                        "WHERE p.token = :token", PasswordResetToken.class);

        query.setParameter("token", token);
        List<PasswordResetToken> res = query.getResultList();
        if(res != null && res.size() == 1) {
            prt = res.get(0);
            if (prt == null || !prt.getToken().equals(token) || prt.getExpiryDate().before(new Date()))
                prt = null;
        }

        return prt;
    }

    public List<GsonCustomer> getCustomers(EntityManager em) {
        javax.persistence.TypedQuery<Customer> query =
                em.createQuery("SELECT customer FROM Customer customer", Customer.class);

        List<Customer> res = query.getResultList();
        List<GsonCustomer> gsonRes = new ArrayList<>();
        for (Customer customer: res)
            gsonRes.add(new GsonCustomer(customer));

        return gsonRes;
    }

    public List<GsonRestaurant> getRestaurants(EntityManager em) {
        javax.persistence.TypedQuery<Restaurant> query =
                em.createQuery("SELECT restaurant FROM Restaurant restaurant", Restaurant.class);

        List<Restaurant> res = query.getResultList();
        List<GsonRestaurant> gsonRes = new ArrayList<>();
        for (Restaurant restaurant: res) {
            gsonRes.add(new GsonRestaurant(restaurant));
        }

        return gsonRes;
    }

    public Set<String> getAutoCompleteIngredients(EntityManager em) {
        Set<String> autoCompleteIngredients = new HashSet<>();
        javax.persistence.TypedQuery<Dish> query =
                em.createQuery("SELECT dish FROM Dish dish", Dish.class);
        List<Dish> res = query.getResultList();
        for (Dish dish: res) {
            for (String ingredients: dish.getIngredients()) {
                if (!ingredients.isEmpty() && !autoCompleteIngredients.contains(ingredients))
                    autoCompleteIngredients.add(ingredients.replaceAll("^\\s+", "").replaceAll("\\s+$", ""));
            }
        }
        return autoCompleteIngredients;
    }

    public Set<String> getAutoCompleteOtherTypes(EntityManager em) {
        Set<String> autoCompleteOtherTypes = new HashSet<>();
        javax.persistence.TypedQuery<Dish> query =
                em.createQuery("SELECT dish FROM Dish dish", Dish.class);
        List<Dish> res = query.getResultList();
        for (Dish dish: res) {
            for (String otherType: dish.getOtherTypes()) {
                if (!otherType.isEmpty() && !autoCompleteOtherTypes.contains(otherType))
                    autoCompleteOtherTypes.add(otherType.replaceAll("^\\s+", "").replaceAll("\\s+$", ""));
            }
        }
        return autoCompleteOtherTypes;
    }
}