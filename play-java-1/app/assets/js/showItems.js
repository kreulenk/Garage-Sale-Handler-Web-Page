/**
 * For showing items
 */
(function() {
    $(function() {
        return $.get("/item", function(items) {
            return $.each(items, function(index, item) {
                return $('#sale').append($("<a>").text(item.name));
            });
        });
    });

}).call(this);