package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import com.avaje.ebean.Model;


/**
 * Created by kevin on 6/30/16.
 */
@Entity
public class Cart extends Model {

    /**
     * Cart 3-arg constructor
     * @param id is the id of the cart
     * @param items a string of items
     * @param numItems the number of items in the cart
     */
    public Cart(final int id, final String items, final int numItems) {
        this.id = id;
        this.items = items;
        this.numItems = numItems;
    }

    /**
     * Cart's 1-arg constructor
     * @param id the id of the cart to be initialized
     */
   public Cart(final int id) {
       this.id = id;
       this.items = "";
       numItems = 0;
    }
    @Id
    public int id;
    public String items;
    public int numItems;

}
