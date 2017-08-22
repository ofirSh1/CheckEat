package application.logic;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SignedUser
{
    public enum Type {
        CUSTOMER, RESTAURANT;
    }

    @Id
    private  String userName;
    private String email;
    private String phone;
    private String password;
    private String verifyPassword;
    private Type type;

    @ElementCollection // TODO check
    private List<Integer> likedDishes = new ArrayList<>();

    public List<Integer> getLikedDishes() {
        return likedDishes;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return verifyPassword;
    }

    public void setPassword2(String password2) {
        this.verifyPassword = password2;
    }

    public SignedUser() {
    }

    public boolean isValidUser() {
        return isStringValid(userName) &&
                isStringValid(email) &&
                isStringValid(password) &&
                isStringValid(verifyPassword);
    }

    public boolean isStringValid(String str) {
        if(str != null) {
            String temp = new String(str);
            return !temp.isEmpty() && temp.trim().length() > 0;
        }
        return false;
    }

    public boolean isValidPassword(){
        return password.equals(verifyPassword);
    }


}
