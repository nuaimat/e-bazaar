$(function () {
    console.log("mo - jquery loaded");
    $(".add-to-cart-btn").click(function (btn) {
        var prodid = $(btn.target).val();
        submitAddToCartForm(prodid);
    });

    var submitAddToCartForm = function (pid) {
        $("#add_to_cart_pid").val(pid);
        $("#add_to_cart_form").submit();
    }
});