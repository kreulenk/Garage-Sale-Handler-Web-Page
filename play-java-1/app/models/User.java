package models;
/**
 * Created by kevin on 6/9/16.
 */
import com.avaje.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
/**
 * Class represents a user object with all of the attributes of a user
 */
public class User extends Model {
    @Id
    public int id;
    public String username;
    public String password;
    public String email;
    public String description;
    public int attempts;
    public int guest; //If guest==1 then role is guest. If guest==0 then it is a normal user.

}
