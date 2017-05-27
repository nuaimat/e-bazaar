<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: nuaimat
  Date: 5/19/17
  Time: 10:46 PM
  To change this template use File | Settings | File Templates.
--%>
<div class="biglogo" style="width: 100%">
    <img src="<c:url value="/images/products/${product.productId}.jpg" />" class="img-responsive"
         style="width: 300px;" alt="${product.productName}" onerror="replaceWithDefault(this);">
</div>
<dl>
    <dt>Item Name</dt>
    <dd>${product.productName}</dd>
    <dt>Price</dt>
    <dd>${product.unitPrice}</dd>
    <dt>Quantity Available</dt>
    <dd>${product.quantityAvail}</dd>
    <dt>Review</dt>
    <dd>${product.description}</dd>
</dl>