package application.logic;

public class RestaurantShowFormat {
    private String restName;
    private String restCity;
    private String restStreet;
    private int restStreetNum;
    private String restLink;
    private String logoUrl;

    public RestaurantShowFormat(Restaurant restaurant) {
        this.restName = restaurant.getRestaurantName();
        this.restCity = restaurant.getCity();
        this.restStreet = restaurant.getStreet();
        this.restStreetNum = restaurant.getStreetNum();
        this.restLink = restaurant.getLink();
        this.logoUrl = restaurant.getLogoUrl();
    }
}