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
<div class="container-fluid">
    <div class="row">
        <form method="get" action="<c:url value="/cart" />" id="update_cart_form">
            <input type="hidden" name="pid" value="0" id="shopping_cart_pid">
            <input type="hidden" name="quantity" value="0" id="shopping_cart_quanity">
            <input type="hidden" name="method" id="shopping_cart_method" value="add_to_cart">
        </form>
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
                        <span class="visible-xs navbar-brand"><span class="glyphicon glyphicon-shopping-cart"></span> Shopping Cart</span>
                    </div>
                    <div class="navbar-collapse collapse sidebar-navbar-collapse">
                        <div class="navbar-header">
                            <a class="hidden-xs navbar-brand" href="#"><span class="glyphicon glyphicon-shopping-cart"></span> Shopping Cart</a>
                        </div>
                        <ul class="nav navbar-nav">
                            <li><a href="<c:url value="/secure_cart" />?action=retrieveSavedCart">Retreive Saved
                                Cart</a></li>
                            <li><a href="<c:url value="/secure_cart" />?action=save">Save Cart</a></li>
                            <li><a href="<c:url value="/cart" />?action=clear">Clear Cart</a></li>
                        </ul>
                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>
        <div class="col-sm-8 col-lg-9 products-container">
            <!-- start product det -->
            <div class="row">
                <h3><span class="glyphicon glyphicon-shopping-cart"></span> Shopping Cart</h3>
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
                                <input type="number" class="form-control text-center shopping-cart-item-quantity" value="${item.quantity}">
                            </td>
                            <td><fmt:formatNumber value="${prod_price[item.productid]}" type="currency"/></td>
                            <td><fmt:formatNumber value="${item.totalprice}" type="currency"/></td>
                            <c:set var="grand_total" value="${grand_total + item.totalprice}" />
                            <td class="actions" data-th="">
                                <button class="btn btn-info btn-sm shopping-cart-save-quantity" value="${item.productid}"><i class="fa fa-floppy-o"></i></button>
                                <button class="btn btn-danger btn-sm shopping-cart-remove-item" value="${item.productid}"><i class="fa fa-trash-o"></i></button>
                            </td>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td colspan="2">&nbsp;</td>
                        <th>Grand total</th>
                        <td><fmt:formatNumber value="${grand_total}" type="currency"/></td>
                    </tr>
                    <tr>
                        <td><a href="<c:url value="/products" />" class="btn btn-warning"><i class="fa fa-angle-left"></i> Continue Shopping</a></td>
                        <td class="hidden-xs"></td>
                        <td class="hidden-xs text-center"></td>
                        <td><a href="<c:url value="/checkout" />" class="btn btn-success btn-block">Checkout <i class="fa fa-angle-right"></i></a></td>
                    </tr>
                    </tbody>
                </table>

            </div>
            <!-- end product det -->

        </div>
    </div>
</div>

<jsp:include page="templates/footer_template.jsp"/>

