/**
 * For showing sales
 */
(function() {
    $(function() {
        return $.get("/sale", function(sales) {
            return $.each(sales, function(index, sale) {
                return $('#sale').append($("<a>").text(sale.name));
            });
        });
    });

}).call(this);