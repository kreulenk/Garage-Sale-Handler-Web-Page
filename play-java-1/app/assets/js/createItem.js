/**
 * For showing items
 */
(function() {
    $(function() {
        return $.get("/sale", function(sales) {
            return $.each(sales, function(index, sale) {
                return $('#sale').append($("<option>").text(sale.name));
            });
        });
    });

}).call(this);