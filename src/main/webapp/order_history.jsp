<%--
  Created by IntelliJ IDEA.
  User: nuaimat
  Date: 5/20/17
  Time: 5:03 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="templates/header_template.jsp"/>
<div class="container-fluid">
    <div class="row">
        <div class="col-lg-3">&nbsp;</div>
        <div class="col-sm-12 col-lg-6 text-center">
            <div class="jumbotron">
                <h1>e-Bazaar</h1>
                <p>Order History</p>
            </div>
            <c:if test="${not empty param.msg}">
                <div class="alert alert-success alert-dismissable">
                    <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                        ${param.msg}
                </div>
            </c:if>
            <table id="order_history" class="table table-hover">
                <thead>
                <tr>
                    <th style="width:10%" class="text-center">Order ID</th>
                    <th style="width:10%" class="text-center">Order Date</th>
                    <th style="width:80%" class="text-center">Total Price</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${order_history}" var="order">
                    <c:choose>
                        <c:when test="${param.oid eq order.orderId}">
                            <tr class="active info">
                        </c:when>
                        <c:otherwise>
                            <tr>
                        </c:otherwise>
                    </c:choose>
                        <td>${order.orderId}</td>
                        <td>${order.orderDateFormatted}</td>
                        <td><fmt:formatNumber value="${order.totalPrice}" type="currency"/></td>
                        <td><a href="<c:url value="/order_history?op=details&oid=${order.orderId}" />"><i class="fa fa-info-circle"></i></a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <c:if test="${not empty order_items}">
                <div class="row">
                    <table id="order_items" class="table table-hover">
                        <thead>
                        <tr>
                            <th style="width:70%">Product Name</th>
                            <th style="width:10%" class="text-center">Quantity</th>
                            <th style="width:10%" class="text-center">Unit Price</th>
                            <th style="width:10%" class="text-center">Total Price</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${order_items}" var="order_item">
                            <tr>
                                <td class="text-left">${order_item.productName}</td>
                                <td>${order_item.quantity}</td>
                                <td><fmt:formatNumber value="${order_item.unitPrice}" type="currency"/></td>
                                <td><fmt:formatNumber value="${order_item.totalPrice}" type="currency"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>
        <div class="col-lg-3">&nbsp;</div>
    </div>
</div>

<jsp:include page="templates/footer_template.jsp"/>