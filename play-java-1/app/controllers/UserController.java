package controllers;
import com.avaje.ebean.SqlUpdate;
import models.Cart;
import models.Sale;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.editProfile;
import views.html.index;
import views.html.login;
import views.html.register;
import views.html.userList;
import views.html.showFinancialReport;
import javax.persistence.PersistenceException;
import java.util.List;
import static com.avaje.ebean.Ebean.createSqlUpdate;
import static com.avaje.ebean.Ebean.find;


/**
 * Created by kevin on 7/3/16.
 * USER CONTROLLER FOR ALL USER PROFILE AND USER-VERIFICATION PURPOSES
 */
public class UserController extends Controller {
    /**
     * Allows User to edit Profile
     * @param username Username of user currently using application
     * @return send to editProfile page
     */
    public final Result editProfile(final String username) {
        List<User> userList =
            find(User.class).where().ilike("username", username).findList();
        User userDB = userList.get(0);
        return ok(editProfile.render(userDB));
    }

    /**
     * Updates logged in User
     *
     * @param username Username of user currently using application
     * @return Result index HTML page of User
     */
    public final Result updateProfile(String username) {
        User user = Form.form(User.class).bindFromRequest().get();
        SqlUpdate down =
            createSqlUpdate("DELETE FROM User WHERE username = :param1 ");
        down.setParameter("param1", username);
        down.execute();
        user.save();
        List<User> userList =
                find(User.class).where().ilike("username", username).findList();
        User userDB = userList.get(0);

        return ok(index.render(username, null, userDB));
    }


    /**
     * An action that verifies if a user exists in the User database and checks if
     * they haven't had too many bad login attempts on their account.
     * @return Confirmation message
     */
    public final Result verifyUser() {
        User user = Form.form(User.class).bindFromRequest().get();
        List<User> userList =
            find(User.class).where().ilike(
                "username", user.username).findList();
        try {
            User userDB = userList.get(0);
            if (userDB != null) {
                if (userDB.password.equals(user.password) && userDB.attempts < 5) {
                    SqlUpdate down = createSqlUpdate("UPDATE user SET attempts= 0"
                            + " WHERE username= \'" + userDB.username + "\'");

                    down.execute();
                    return ok(index.render(userDB.username, null, userDB));
                } else if (userDB.attempts < 5) {
                    SqlUpdate down = createSqlUpdate("UPDATE user SET attempts= "
                            + Integer.toString(userDB.attempts + 1)
                            + " WHERE username= \'" + userDB.username + "\'");

                    down.execute();
                    return (ok(login.render("Incorrect username/password. Try again. That user has "
                            + (userDB.attempts + 1) + " bad login attempts.")));
                } else {
                    SqlUpdate down = createSqlUpdate("UPDATE user SET attempts= "
                            + Integer.toString(userDB.attempts + 1)
                            + " WHERE username= \'" + userDB.username + "\'");

                    down.execute();
                    return (ok(login.render("This user has been locked out due to too many bad login attempts.")));
                }
            }
        } catch(Exception e) {
            return (ok(
            login.render("Incorrect username/password. Try again.")));
        }
        return (ok(
            login.render("Incorrect username/password. Try again.")));
    }

    /**
     *An Action that binds the input from the registration
     *form to create a new User object. It also creates a cart
     *for that user so that they can add items to it.
     * @return Registration confirmation message.
     */
    public final Result registerUser() {
        try {
            User user = Form.form(User.class).bindFromRequest().get();
            user.save();
            Cart cart = new Cart(user.id);
            cart.save();
        } catch (PersistenceException e) {
            return ok(register.render("Username already exists"));
        }
        return ok(login.render(
            "Thank you for registering! Please enter your username/password."));
    }

    /**
     * Loads the userList page.
     * @param username Username of user currently using application
     * @return The userList web page
     */
    public final Result userList(final String username) {
        List<User> users = find(User.class).findList();
        List<Sale> sales = find(Sale.class).findList();
        return ok(userList.render(username, users, sales));
    }

    /**
     * Loads the page that shows the financial report for the book keeper
     * @param username Username of user currently using application
     * @return The showFinancialReport web page
     */
    public final Result showFinancialReport(final String username) {
        List<Sale> sales = find(Sale.class).findList();
        return ok(showFinancialReport.render(username, sales));
    }

    /**
     * Unlocks a user who has been locked out due to too many bad login attempts
     * @param usernameUnlockUser Username of user currently using application
     * concatonated with the username of the person to be unlocked
     * @return Loads the index page
     */
    public final Result unlockUser(final String usernameUnlockUser) {
        String username = usernameUnlockUser.split(",")[0];
        String unlockUser = usernameUnlockUser.split(",")[1];
        SqlUpdate down = createSqlUpdate("UPDATE user SET attempts= 0"
                + " WHERE username= \'" + unlockUser + "\'");

        down.execute();
        List<User> userList =
                find(User.class).where().ilike("username", username).findList();
        User userDB = userList.get(0);
        return ok(index.render(username, null, userDB));
    }

}