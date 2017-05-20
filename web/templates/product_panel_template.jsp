<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: nuaimat
  Date: 5/19/17
  Time: 2:13 PM
  To change this template use File | Settings | File Templates.
--%>
<div class="col-md-4 text-center">
    <div class="panel panel-primary">
        <div class="panel-heading">${product.productName}</div>
        <div class="panel-body product-image" style="position: relative">
            <img src="<c:url value="/images/products/${product.productId}.jpg" />" class="img-responsive"
                 style="width: 100%;height: 300px" alt="${product.productName}" onerror="replaceWithDefault(this);">
        </div>
        <div class="panel-footer">${product.productName} for only $<span class="text-primary">${product.unitPrice}</span>
            <div style="text-align: center">
                <button class="btn btn-success add-to-cart-btn" value="${product.productId}">Add to Cart</button>
                <button class="btn btn-info more-info-btn" value="${product.productId}">More info</button>
            </div>
        </div>
    </div>
</div>