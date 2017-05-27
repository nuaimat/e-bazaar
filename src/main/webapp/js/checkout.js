$(function () {
    $("#ship_address").change(function () {
        var selected_index = $(this).val();
        if (selected_index == -2) {
            showAddNewAddress("#selected_ship_address");
        }
        if (selected_index == -1) {
            $("#selected_ship_address").val(addressToText(json_def_shipping_addresses));
        }
        if (selected_index < 0) {
            return;
        }
        var selected_add = json_all_shipping_addresses[selected_index];
        $("#selected_ship_address").val(addressToText(selected_add));
        if($("#b_as_s").is(':checked')){
            $("#selected_bill_address").val(addressToText(selected_add));
        }
    });

    $("#bill_address").change(function () {
        var selected_index = $(this).val();
        if (selected_index == -2) {
            showAddNewAddress("#selected_bill_address");
        }
        if (selected_index == -1) {
            $("#selected_bill_address").val(addressToText(json_def_billing_addresses));
        }
        if (selected_index < 0) {
            return;
        }
        var selected_add = json_all_billing_addresses[selected_index];
        $("#selected_bill_address").val(addressToText(selected_add));
    });

    var addressToText = function (js) {
        return js.street + "\n" + js.city + "\n" + js.state + " " + js.zip;
    }

    var newAddressPlaceHolder = null;
    var showAddNewAddress = function (id) {
        //$(id).val("this should be a result of a modal");
        newAddressPlaceHolder = id;
        $('#addNewAddressModal').modal('show');
    }

    $("#b_as_s").click(function () {
        if($(this).is(':checked')){
            $("#bill_address").prop( "disabled", true );
            $("#bill_address").css('visibility', 'hidden');
            $("#selected_bill_address").val($("#selected_ship_address").val());
        } else {
            $("#bill_address").prop( "disabled", false );
            $("#bill_address").css('visibility', 'visible');
        }
    });

    $("#addNewAddressModal").modal({ show: false});

    $("#newAddressForm").submit(function (evt) {
        evt.stopPropagation();
        evt.preventDefault();

        var form = $(this);
        var newAddressObj = {
            "street":   $("#street").val(),
            "city":   $("#city").val(),
            "state":    $("#state").val(),
            "zip":  $("#zipcode").val()
        };
        var newAddress = addressToText(newAddressObj);
        $('#addNewAddressModal').modal('hide');

        if(newAddressPlaceHolder != null){
            $(newAddressPlaceHolder).val(newAddress);
            if($("#b_as_s").is(':checked')){
                $("#selected_bill_address").val(newAddress);
            }
            newAddressPlaceHolder = null;
        }

        return false;
    });

    $("#checkout_addresses_form").submit(function () {
        $("#selected_bill_address").prop( "disabled", false );
        $("#selected_ship_address").prop( "disabled", false );
        return true;
    });
});