package controllers;

import com.avaje.ebean.Model;
import com.avaje.ebean.SqlUpdate;
import models.Item;
import models.Sale;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.createSale;
import views.html.transaction;
import views.html.viewSales;
import views.html.saleMainPage;
import views.html.closeSale;
import javax.persistence.PersistenceException;
import java.util.List;
import static com.avaje.ebean.Ebean.createSqlUpdate;
import static com.avaje.ebean.Ebean.find;
import static play.libs.Json.toJson;

/**
 * Created by kevin on 7/3/16.
 * SALE CONTROLLER WITH ALL METHODS PERTAINING TO SALES
 */
public class SaleController extends Controller {
    private String comma = ",";

    public final Result createSale(final String username) {
        List<User> userList = find(User.class).findList();
        return ok(createSale.render(username, "", userList));
    }

    /**
     * An Action that binds the input from the Registration
     * form to create a new Sale object
     *
     * @param username Username of user currently using application
     * @return Registration confirmation message.
     */
    public final Result registerSale(final String username) {
        List<User> userList = find(User.class).findList();
        try {
            Sale sale = Form.form(Sale.class).bindFromRequest().get();
            if (sale.bookkeeper == null) {
                return ok(createSale.render(username,
                        "Fail to create a sale: Please choose a Bookkeeper",
                        userList));
            }
            sale.save();
        } catch (PersistenceException e) {
            return ok(createSale.render(username,
                "Fail to create a sale: Sale name already exists", userList));
        }
        return ok(createSale.render(username, "Sale created", userList));
    }

    /**
     * An Action that renders the current created sale
     *
     * @return List of sales.
     */
    public final Result getSales() {
        List<Sale> sales = new Model.Finder<>(String.class, Sale.class).all();
        return ok(toJson(sales));
    }

    /**
     * Allows the user to view a list of sales
     * @param username A string of the username
     * @return renders the viewSales page
     */
    public final Result viewSales(final String username) {
        List<Sale> saleList = find(Sale.class).findList();
        return ok(viewSales.render(username, saleList));
    }

    /**
     * Main Page of Sales
     * @param usernameSName A concatonated string of the username
     * and the saleName separated by a comma
     * @return Sends user to the salemainPage
     */
    public final Result saleMainPage(final String usernameSName) {
        String username = usernameSName.split(comma)[0];
        String saleName = usernameSName.split(comma)[1];
        List<Item> itemList = find(Item.class).where()
                .ilike("sale", saleName).findList();
        return ok(saleMainPage.render(username, saleName, itemList));
    }

    /**
     * Transaction action
     * @param username A string that contains the username
     * @return Sends to transaction page
     */
    public final Result transaction(final String username) {
        List<Sale> saleList = find(Sale.class).findList();
        return ok(transaction.render(username, saleList));
    }

    /**
     * Renders the page that closes a sale with the correct information
     * @param username A string that contains the username
     * @return Sends to closeSale page
     */
    public final Result closeSale(final String username) {
        List<User> userList = find(User.class).findList();
        List<Sale> saleList = find(Sale.class).findList();
        return ok(closeSale.render(username, "", userList, saleList));
    }

    /**
     * Deletes a sale from the database, effectively closing it
     * @param usernameSName A string that has the username and the sale name
     * concatonated into a single string separated by a comma
     * @return The close sale web page with a success message
     */
    public final Result deleteSale(String usernameSName) {
        List<User> userList = find(User.class).findList();
        String username = usernameSName.split(comma)[0];
        String saleName = usernameSName.split(comma)[1];

        SqlUpdate down = createSqlUpdate(
                "DELETE FROM sale WHERE name = :param1 ");
        down.setParameter("param1", saleName);
        down.execute();

        SqlUpdate down2 = createSqlUpdate(
                "DELETE FROM item WHERE sale = :param1 ");
        down2.setParameter("param1", saleName);
        down2.execute();

        List<Sale> saleList = find(Sale.class).findList();
        return ok(closeSale.render(username,
                "You have successfully deleted the sale along "
                + "with all of the items in it!",
                userList, saleList));
    }
}
