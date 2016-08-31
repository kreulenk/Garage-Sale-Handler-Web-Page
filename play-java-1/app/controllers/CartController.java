package controllers;

import com.avaje.ebean.SqlUpdate;
import models.Cart;
import play.data.Form;
import play.mvc.Controller;
import models.Item;
import models.User;
import models.Sale;
import play.mvc.Result;
import services.ViewString;
import views.html.emptyCart;
import views.html.transactionItemPage;
import views.html.viewCart;
import views.html.checkedOut;
import java.util.ArrayList;
import java.util.List;
import static com.avaje.ebean.Ebean.createSqlUpdate;
import static com.avaje.ebean.Ebean.find;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Created by kevin on 7/3/16.
 * CART CONTROLLER for adding, viewing, and checking out a user's shopping cart.
 */
public class CartController extends Controller {
    private String comma = ",";
    private String userSt = "username";
    private String emptySt = "";
    private String idSt = "id";

    /**
     * This method adds an item to the cart
     * @param usernameINameSName A concatonated string that contains
     * the username, item Id and Sale name separated by commas
     * @return sends to the transactionItemPage
     */
    public final Result addToCart(final String usernameINameSName) {
        String username = usernameINameSName.split(comma)[0];
        String itemId = usernameINameSName.split(comma)[1];
        String saleName = usernameINameSName.split(emptySt)[2];
        List<Item> itemList = find(Item.class).where().ilike(
            idSt, itemId).findList();
        Item item = itemList.get(0);
        List<User> userList = find(User.class).where().ilike(
            userSt, username).findList();
        User user = userList.get(0);
        List<Cart> cartList = find(Cart.class).where().ilike(
            idSt, Integer.toString(user.id)).findList();
        //Finds cart with id matching to the user id
        Cart cart = cartList.get(0);
        String itemsInCart = cart.items;
        //Gets the string of the items in the cart from the database
        if (itemsInCart.equals(emptySt)) {
            itemsInCart = itemsInCart + item.id;
        } else {
            itemsInCart = itemsInCart + comma + item.id;
        }
        SqlUpdate down = createSqlUpdate(
            "DELETE FROM Cart WHERE id = :param1 ");
        down.setParameter("param1", user.id);
        down.execute();
        Cart cartForSave = new Cart(cart.id, itemsInCart, cart.numItems + 1);
        cartForSave.save();
        return ok(transactionItemPage.render(username, saleName, itemList,
            "You have successfully added " + item.itemName + " to your cart"));
    }

    /**
     * Allows you to add Multiple Items to your cart at once.
     * @param username A string that contains the username.
     * @return sends you back to the Homepage after
     * you've added these Items (input into a form)
     */
    public final Result addToCartMultiple(String username) {
        ViewString viewString = Form.form(ViewString.class).bindFromRequest().
            get();
        List<User> userList = find(User.class).where().ilike(
            userSt, username).findList();
        User user = userList.get(0);
        List<Cart> cartList = find(Cart.class).where().ilike(idSt,
            Integer.toString(user.id)).findList();
        //Finds cart with id matching to the user id
        Cart cart = cartList.get(0);
        String itemsInCart = cart.items;
        //Gets the string of the items in the cart from the database
        int count = 0;
        String[] itemsARR = viewString.items.split(" ");
        for (String eachElem: itemsARR) {
            count++;
            if (itemsInCart.equals(emptySt)) {
                itemsInCart = itemsInCart + eachElem;
            } else {
                itemsInCart = itemsInCart + comma + eachElem;
            }
        }
        SqlUpdate down = createSqlUpdate(
            "DELETE FROM Cart WHERE id = :param1 ");
        down.setParameter("param1", user.id);
        down.execute();

        Cart cartForSave = new Cart(
            cart.id, itemsInCart, cart.numItems + count);
        cartForSave.save();
        return redirect(routes.HomeController.index(username));

    }

    /**
     * Allows you to view what's in your cart
     * @param username A string that contains the username
     * @return sends to viewCart page
     */
    public final Result viewCart(String username) {
        List<User> userList = find(User.class).where().ilike(userSt,
            username).findList();
        User user = userList.get(0);
        List<Cart> cartList = find(Cart.class).where().ilike(idSt,
            Integer.toString(user.id)).findList();
        //Finds cart with id matching to the user id
        Cart cart = cartList.get(0);
        if (cart.items.equals(emptySt)) {
            return redirect(routes.CartController.emptyCart(username));
        }
        String[] items = cart.items.split(comma);
        List<Item> itemsInCartOBJ = new ArrayList<>();
        for (String item:items) {
            List<Item> tempItem = find(Item.class).where().ilike(
                idSt, item).findList();
            itemsInCartOBJ.add(tempItem.get(0));
        }
        return ok(viewCart.render(username, itemsInCartOBJ));
    }


    /**
     * Checks user out
     * @param username A string that contains the username
     * @return send you to checkedOut page
     */
    public final Result checkedOut(final String username) {
        List<User> userList = find(User.class).where().ilike(
            userSt, username).findList();
        User user = userList.get(0);
        List<Cart> cartList = find(Cart.class).where().ilike(idSt,
            Integer.toString(user.id)).findList(); //Finds cart with id matching to the user id
        Cart cart = cartList.get(0);
        String[] items = cart.items.split(comma);
        double priceTotal = 0;
        List<Item> itemsInCartOBJ = new ArrayList<>();
        String itemsBought = ""; //String for keeping track of what items are bough for email
        for (String item:items) {
            List<Item> tempItem = find(Item.class).where().ilike(
                "id", item).findList();
            priceTotal += tempItem.get(0).price;
            itemsBought += tempItem.get(0) + "\n";

            List<Sale> saleList = find(Sale.class).where().ilike("name",
                    tempItem.get(0).sale).findList();
            Sale sale = saleList.get(0);


            SqlUpdate down = createSqlUpdate("UPDATE sale SET profit= " + (sale.profit + tempItem.get(0).price)
                    + " WHERE name= \'" + sale.name + "\'");

            down.execute();


            itemsInCartOBJ.add(tempItem.get(0));
        }
        SqlUpdate down = createSqlUpdate("UPDATE cart SET items=  \"\""
                + " WHERE id= " + user.id);
        down.execute();
        String priceTotalSTR = Double.toString(priceTotal);



        itemsBought += priceTotalSTR;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("2340emailer@gmail.com","PassForClass1");
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@no-spam.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(user.email));
            message.setSubject("Receipt");
            message.setText(itemsBought);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }





        return ok(checkedOut.render(username, itemsInCartOBJ, priceTotalSTR));
    }

    /**
     * This method redirects to "emptyCart" page which states that your cart
     * is empty
     * It allows you to click a button on that page so that your
     * return to the index.scala.html (Homepage)
     * @param username which is the user's username
     * @return send to emptyCart page
     */
    public final Result emptyCart(final String username) {
        return ok(emptyCart.render(username));
    }
}
