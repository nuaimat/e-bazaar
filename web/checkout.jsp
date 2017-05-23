<%--
  Created by IntelliJ IDEA.
  User: nuaimat
  Date: 5/19/17
  Time: 11:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="templates/header_template.jsp"/>
<c:set var="rand"><%= java.lang.Math.round(java.lang.Math.random() * 200) %></c:set>
<script src="<c:url value="/js/checkout.js" />?${rand}" ></script>
<div class="container-fluid">
    <div class="row">
        <div class="col-sm-4 col-lg-3">
            <div class="sidebar-nav">
                <div class="navbar navbar-default" role="navigation">
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle" data-toggle="collapse"
                                data-target=".sidebar-navbar-collapse">
                            <span class="sr-only">Toggle Catalogues</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                        <span class="visible-xs navbar-brand"><span class="glyphicon glyphicon-road"></span> Checkout</span>
                    </div>
                    <div class="navbar-collapse collapse sidebar-navbar-collapse">
                        <div class="navbar-header">
                            <a class="hidden-xs navbar-brand" href="#"><span class="glyphicon glyphicon-road"></span> Checkout</a>
                        </div>
                        <ul class="nav navbar-nav">
                            <li><a href="<c:url value="/secure_checkout" />">1. Confirm Addresses</a></li>
                            <li><a href="<c:url value="/secure_checkout" />">2. Confirm Payment</a></li>
                            <li><a href="<c:url value="/secure_checkout" />">3. Final Review</a></li>
                        </ul>
                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>
        <div class="col-sm-8 col-lg-9 products-container">
            <!-- start product det -->
            <div class="row">
                <h3><span class="glyphicon glyphicon-road"></span> Checkout</h3>
                <c:if test="${not empty param.msg}">
                    <div class="alert alert-success alert-dismissable">
                        <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                        ${param.msg}
                    </div>
                </c:if>
                <c:if test="${not empty param.errormsg}">
                    <div class="alert alert-danger alert-dismissable">
                        <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                        <strong>Error: </strong> ${param.errormsg}
                    </div>
                </c:if>
                <div class="page-header">
                    <h4>Items: </h4>
                </div>
                <table class="table table-hover">
                    <thead>
                    <tr>
                        <th style="width:50%">Item Name</th>
                        <th style="width:10%">Quantity</th>
                        <th style="width:8%">Price</th>
                        <th style="width:22%" class="text-center">Subtotal</th>
                        <th style="width:10%"></th>

                    </tr>
                    </thead>
                    <tbody>
                    <c:set var="grand_total" value="0" />
                    <c:forEach items="${cart_items}" var="item">
                        <tr id="shopping-cart-item-${item.productid}">
                            <td>${item.productName}&nbsp;<a href="<c:url value="/cart?pid=${item.productid}&method=display_product" /> "><span class="glyphicon glyphicon-info-sign"></span></a></td>
                            <td>
                                    ${item.quantity}
                            </td>
                            <td><fmt:formatNumber value="${prod_price[item.productid]}" type="currency"/></td>
                            <td><fmt:formatNumber value="${item.totalprice}" type="currency"/></td>
                            <c:set var="grand_total" value="${grand_total + item.totalprice}" />
                            <td class="actions" data-th="">
                            </td>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td colspan="2">&nbsp;</td>
                        <th>Grand total</th>
                        <td><fmt:formatNumber value="${grand_total}" type="currency"/></td>
                    </tr>
                    </tbody>
                </table>

            </div>
            <form id="checkout_addresses_form" method="post" action="<c:url value="/secure_checkout"/>">
            <input type="hidden" name="method" value="show_payment">
            <div class=row">
                <div class="page-header">
                    <h4>Addresses: </h4>
                </div>

                <div class="col-sm-6 col-lg-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">Shipping</div>
                        <div class="panel-body">
                            <div class="row mg10">
                                <select name="ship_address" id="ship_address" >
                                    <optgroup label="default">
                                        <option value="-1" selected="selected">${def_shipping_addresses.multiLineRepresentation}</option>
                                    </optgroup>
                                    <optgroup label="other">
                                    <c:forEach items="${all_shipping_addresses}" var="saddr" varStatus="loop">
                                        <option value="${loop.index}">${saddr.oneLineRepresentation}</option>
                                    </c:forEach>
                                    </optgroup>
                                    <option value="-2">Add new address ...</option>
                                </select>
                            </div>
                            <div class="row mg10">
                                <label>Selected Address:</label>
                                <textarea id="selected_ship_address" name="selected_ship_address" readonly="readonly" rows="4" disabled="disabled" style="width: 100%;">${def_shipping_addresses.multiLineRepresentation}</textarea>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-lg-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">Billing</div>
                        <div class="panel-body">
                            <div class="row mg10">
                                <select name="bill_address" id="bill_address">
                                    <optgroup label="default">
                                        <option value="-1" selected="selected">${def_billing_addresses.multiLineRepresentation}</option>
                                    </optgroup>
                                    <optgroup label="other">
                                    <c:forEach items="${all_billing_addresses}" var="baddr" varStatus="loop">
                                        <option value="${loop.index}">${baddr.oneLineRepresentation}</option>
                                    </c:forEach>
                                    </optgroup>
                                    <option value="-2">Add new address ...</option>
                                </select>
                            </div>
                            <div class="row mg10">
                                <label>Selected Address:</label>
                                <textarea id="selected_bill_address" name="selected_bill_address"  readonly="readonly" rows="4" disabled="disabled" style="width: 100%;">${def_billing_addresses.multiLineRepresentation}</textarea>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
            <div class="row">
                <div class="col-sm-12 col-lg-12">
                    <label class="pdr10"><input type="checkbox" name="b_as_s" id="b_as_s"> Billing same as shipping </label>
                    <label class="pdr10"><input type="checkbox" name="save_ship"> Save shipping address </label>
                    <label class="pdr10"><input type="checkbox" name="save_bill"> Save billing address </label>
                    <script type="text/javascript">


                        var json_def_billing_addresses = ${json_def_billing_addresses};
                        var json_def_shipping_addresses = ${json_def_shipping_addresses};
                        var json_all_shipping_addresses = ${json_all_shipping_addresses};
                        var json_all_billing_addresses = ${json_all_billing_addresses};

                    </script>
                </div>
            </div>
            <table class="table">
                <tbody>
                <tr>
                    <td><a href="<c:url value="/cart" />" class="btn btn-warning"><i class="fa fa-angle-left"></i> Back to Shopping Cart</a></td>
                    <td class="hidden-xs"></td>
                    <td class="hidden-xs text-center"></td>
                    <td><input type="submit" class="btn btn-success btn-block" value="Proceed to payment &gt;"></td>
                </tr>
                </tbody>
            </table>
            </form>
        </div>
    </div>
</div>

<div id="addNewAddressModal" class="modal fade" role="dialog">
    <div class="modal-dialog modal-sm">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header" style="padding:35px 50px;">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4><span class="glyphicon glyphicon-envelope"></span> Add new Address</h4>
            </div>
            <div class="modal-body" style="padding:40px 50px;">
                <form role="form" id="newAddressForm">
                    <div class="form-group">
                        <label for="street">Street</label>
                        <input type="text" class="form-control" id="street" placeholder="ex: 1 Hacker way">
                    </div>
                    <div class="form-group">
                        <label for="city">City</label>
                        <input type="text" class="form-control" id="city" placeholder="ex: Mountain View">
                    </div>
                    <div class="form-group">
                        <label for="state">State</label>
                        <input type="text" class="form-control" id="state" placeholder="ex: CA">
                    </div>
                    <div class="form-group">
                        <label for="zipcode">Zipcode</label>
                        <input type="text" class="form-control" id="zipcode" placeholder="ex: 98432">
                    </div>
                    <button type="submit" class="btn btn-success btn-block"><span class="glyphicon glyphicon-ok"></span> Use this address</button>
                </form>
            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-danger btn-default pull-left" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Cancel</button>
            </div>
        </div>

    </div>
</div>

<jsp:include page="templates/footer_template.jsp"/>

