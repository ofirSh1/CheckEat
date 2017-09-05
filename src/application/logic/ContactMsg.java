package application.logic;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ContactMsg {

    public ContactMsg() {}

    @Id @GeneratedValue
    private int id;

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String email;
    private String phone;
    private String request;
    private String content;
}
