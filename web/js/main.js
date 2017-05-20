$(function () {
    console.log("mo - jquery loaded");

    (function(){
        $("#navBarLeftLinks > li").removeClass("active");
        var activeLink = "home-nav-link";
        if(window.location.pathname.match("\/order_history")){
            activeLink = "order-history-nav-link";
        } else if (window.location.pathname.match("\/products")) {
            activeLink = "products-nav-link";
        } else if (window.location.pathname.match("\/cart") || window.location.pathname.match("\/secure_cart")) {
            activeLink = "shopping-cart-nav-link";
        }
        $("." + activeLink).addClass("active");

    })();

    $(".add-to-cart-btn").click(function (btn) {
        var prodid = $(btn.target).val();
        submitCartForm(prodid, "add_to_cart");
    });

    $(".remove-from-cart-btn").click(function (btn) {
        var prodid = $(btn.target).val();
        submitCartForm(prodid, "remove_item");
    });

    $(".cart-item-more-info").click(function (btn) {
        var prodid = $(btn.target).val();
        submitCartForm(prodid, "display_product");
    });

    var submitCartForm = function (pid, op) {
        $("#shopping_cart_pid").val(pid);
        $("#shopping_cart_method").val(op);
        $("#add_to_cart_form").submit();
    }

    $(".shopping-cart-save-quantity").click(function (btn) {
        var prodid = $(btn.target).val();
        var q = $("#shopping-cart-item-" + prodid + " .shopping-cart-item-quantity").val();
        updateCartItemForm(prodid, "update_quantity", q);
    });

    $(".shopping-cart-remove-item").click(function (btn) {
        var prodid = $(btn.target).val();
        var q = 0;
        updateCartItemForm(prodid, "remove_item", q);
    });

    var updateCartItemForm = function(pid, op, q){
        if(pid.length < 1){
            alert("invalid product ID");
            return false;
        }
        $("#shopping_cart_pid").val(pid);
        $("#shopping_cart_method").val(op);
        $("#shopping_cart_quanity").val(q);

        $("#update_cart_form").submit();
    }



});

var replaceWithDefault = function(image){
    image.onerror = "";
    image.src = "/images/products/product_image_unavailable.jpg";
    return true;
}