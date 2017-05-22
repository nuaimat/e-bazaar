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
            <div class=row">
                <div class="page-header">
                    <h4>Addresses: </h4>
                </div>

                <div class="col-sm-6 col-lg-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">Shipping</div>
                        <div class="panel-body">Shipping Content</div>
                    </div>
                </div>
                <div class="col-sm-6 col-lg-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">Billing</div>
                        <div class="panel-body">Billing Content</div>
                    </div>
                </div>

            </div>
            <table class="table">
                <tbody>
                <tr>
                    <td><a href="<c:url value="/cart" />" class="btn btn-warning"><i class="fa fa-angle-left"></i> Back to Shopping Cart</a></td>
                    <td class="hidden-xs"></td>
                    <td class="hidden-xs text-center"></td>
                    <td><a href="<c:url value="/secure_checkout?method=show_payment" />" class="btn btn-success btn-block">Proceed to payment <i class="fa fa-angle-right"></i></a></td>
                </tr>
                </tbody>
            </table>

        </div>
    </div>
</div>

<jsp:include page="templates/footer_template.jsp"/>

