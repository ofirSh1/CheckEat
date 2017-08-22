package application.logic;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String content;
    private String userName;
    private String userType;
    private String userUrl;
    private Date date;
    @ManyToOne
    private Dish dish;

    public void setDetails(Dish dish, SignedUser signedUser, String content, Date date) {
        this.dish = dish;
        this.content = content;
        this.userName = signedUser.getUserName();
        this.userType = signedUser.getType().name();
        this.date = date;
        if(signedUser.getType() == SignedUser.Type.RESTAURANT){
            Restaurant restaurant = (Restaurant)signedUser;
            this.userUrl = restaurant.getLogoUrl();
        }
    }

    //Setter
    public void setContent(String content) {
        this.content = content;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    //Getter
    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserType() {
        return userType;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public Date getDate() {
        return date;
    }

    public Dish getDish() {
        return dish;
    }
}