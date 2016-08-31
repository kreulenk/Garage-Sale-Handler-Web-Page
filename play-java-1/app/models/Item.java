package models;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.avaje.ebean.Model;
/**
 * Created by kevin on 6/22/16.
 * Represents an item that may be placed into a database.
 */
@Entity
public class Item extends Model {
    @Id
    public int id;
    public String itemName;
    public String description;
    public double price;
    public String sale;
    public String imgPath;

    /**
     * Creates a string that displays information about the item.
     * @return A string that has information about the item.
     */
    @Override
    public String toString() {
        String temp = String.valueOf(price);
        char cTemp = temp.charAt(temp.length() - 3);
        if (cTemp != '.') {
            return ("The item No " + id + " is " + itemName + " with price of $" + price + "0");
        }
        return ("The item No " + id + " is " + itemName + " with price of $" + price);
    }
}
