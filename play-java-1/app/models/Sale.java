package models;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.avaje.ebean.Model;

@Entity
/**
 * Created by kevin on 6/22/16.
 * Represents a sale that may be placed into a database
 */
public class Sale extends Model {
    @Id
    public int id;
    public String name;
    public String description;
    public double profit;
    public String admin;
    public String bookkeeper;
}
