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

    public List<ContactMsg> getMsgs() {
        return msgs;
    }

    @OneToMany
    private List<ContactMsg> msgs = new ArrayList<>();
}