package controllers;

import com.avaje.ebean.Model;
import com.avaje.ebean.SqlUpdate;
import models.Item;
import models.Sale;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;

import java.io.File;
import java.util.List;

import static com.avaje.ebean.Ebean.createSqlUpdate;
import static com.avaje.ebean.Ebean.find;
import static play.libs.Json.toJson;

/**
 * Created by kevin on 7/3/16.
 * ITEM CONTROLLER
 */
public class ItemController extends Controller {
    private String comma = ",";

    /**
     * Action that proceeds to send user to transactionItemPage
     *
     * @param usernameINameSName A concatonated string that contains
     * the username and sale name separated by commas.
     * @return renders the next page
     */
    public final Result transactionItemPage(final String usernameINameSName) {
        String username = usernameINameSName.split(comma)[0];
        String saleName = usernameINameSName.split(comma)[1];
        List<Item> itemList = find(Item.class).where()
                .ilike("sale", saleName).findList();
        return ok(transactionItemPage.render(username, saleName, itemList, ""));
    }

    /**
     * An Action that updates an item in the database that has been edited.
     *
     * @param username Username of user currently using application
     * @return Renders changes to sale.
     */
    public final Result updateItem(final String username) {
        Item item = Form.form(Item.class).bindFromRequest().get();
        SqlUpdate down = createSqlUpdate(
            "DELETE FROM Item WHERE id = :param1 ");
        down.setParameter("param1", item.id);
        down.execute();
        item.save();
        return redirect(routes.HomeController.index(username));
    }

    /**
     * An Action that edits a currently existing item
     *
     * @param usernameItemID A string that combines the username
     * and item into a string, seperated by a comma.
     * @return Renders Username and new item.
     */
    public final Result editItem(final String usernameItemID) {
        String username = usernameItemID.split(comma)[0];
        String itemID = usernameItemID.split(comma)[1];
        List<Item> itemList = find(Item.class).where()
                .ilike("id", itemID).findList();
        Item item = itemList.get(0);
        return ok(editItem.render(username, item));
    }

    /**
     * An Action that binds the input from search
     *form to find an item in a sale by username
     * @param username Username of user currently using application
     * @return Renders Username and item list.
     */
    public final Result itemSearch(String username) {
        Item item = Form.form(Item.class).bindFromRequest().get();
        List<Item> itemList = find(Item.class).where().ilike(
            "itemName", item.itemName).findList();
        List<User> userList =
                find(User.class).where().ilike("username", username).findList();
        User userDB = userList.get(0);
        return ok(index.render(username, itemList, userDB));
    }

    /**
     * A Getter Method for obtaining Items
     * @return the list of items
     */
    public final Result getItems() {
        List<Item> items = new Model.Finder<>(String.class, Item.class).all();
        return ok(toJson(items));
    }

    /**
     * Allows user to view items
     * @param username A string that contains the username
     * @return renders viewTag.scala.html
     */
    public final Result viewItems(final String username) {
        List<Item> items = find(Item.class).findList();
        return ok(viewTag.render(username, items));
    }

    /**
     * An Action that binds the input from the registration
     * form to create a new item object
     *
     * @param username Username of user currently using application
     * @return Confirmation message.
     */
    public final Result registerItem(final String username) {
        Item item = Form.form(Item.class).bindFromRequest().get();
        item.save();
        return redirect(routes.HomeController.index(username));
    }

    /**
     * Action that creates an item
     * @param username A string that contains the username
     * @return renders the item-creation page
     */
    public final Result createItem(final String username) {
        Sale sale = Form.form(Sale.class).bindFromRequest().get();
        return ok(createItem.render(username, sale.name));
    }

    /**
     * Image uploader
     * @return renders the image uploader
     */
    public final Result img() {
        return ok(uploader.render());
    }

    /**
     * Action that uploads an image
     * @return renders the image uploading page
     */
    public final Result upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart photo = body.getFile("photo");
        if (photo != null) {
            String fileName = photo.getFilename();
            File file = (File) photo.getFile();
            File newFile = new File(play.Play.application().path().toString() + "//public//images//" + "_" + fileName);
            file.renameTo(newFile); //here you are moving photo to new directory
            return ok(imgPath.render(newFile.getName()));
        }
        return ok("no file");
    }

}
