/**
 * For the users
 * Created by kevin on 7/10/16.
 */
(function() {
    $(function() {
        return $.get("/user", function(users) {
            return $.each(users, function(index, user) {
                return $('#user').append($("<input>").text(user.name));
            });
        });
    });

}).call(this);