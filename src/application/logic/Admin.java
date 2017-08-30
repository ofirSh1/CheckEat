package application.logic;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Admin {
    @Id
    private String userName = "CheckEat";
    private String password = "CheckEat";
    private String email = "checkeatteam@gmail.com";
    private String emailPassword = "finalProject";
    @OneToMany
    private List<ContactMsg> msgs = new ArrayList<>();

    //Setter
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    //Getter
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public List<ContactMsg> getMsgs() {
        return msgs;
    }

}