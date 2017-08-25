package application.logic;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Calendar;
import java.util.Date;

@Entity
public class PasswordResetToken {
    //Members
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String token;
    private String username;
    private Date expiryDate;

    public void setPasswordToken(String token, String username) {
        this.token = token;
        this.username = username;
        setExpiryDate();
    }

    //Setter
    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiryDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        this.expiryDate = c.getTime();
    }

    //Getter
    public int getId() { return id; }

    public String getToken() {
        return token;
    }

    public String getUsername() { return username; }

    public Date getExpiryDate() { return expiryDate; }
}