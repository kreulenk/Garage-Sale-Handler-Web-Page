package controllers;
import models.User;
import play.mvc.*;
import views.html.*;

import java.util.List;

import static com.avaje.ebean.Ebean.find;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * Renders the main.scala.html file that makes sure the music player appears on each page,
     * In addition to that page's content (since the music player is on each page).
     * @return the main HTML
     */
    public final Result main() {return ok(main.render());}

    /**
     * Renders the music player on each page of the application.
     * @return musicPlayer being rendered
     */
    public final Result musicPlayer() {return ok(musicPlayer.render());}

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public final Result welcome() {
        return ok(welcome.render());
    }

    /**
     *An action that renders a HTML page with the registration form
     *
     * @return Result register HTML page
     */
    public final Result register() {
        return ok(register.render(""));
    }

    /**
     *An action that renders a HTML page with the login form
     *
     * @return Result login HTML page
     */

    public final Result login() {
        return ok(login.render("Please enter a username and password"));
    }


    /**
     *Renders User's index page
     * @param username Username of user currently using application
     * @return Result index HTML page
     */
    public final Result index(final String username) {
        List<User> userList = find(User.class).where().ilike("username", username).findList();
        User userDB = userList.get(0);
        return ok(index.render(username, null, userDB));
    }

}