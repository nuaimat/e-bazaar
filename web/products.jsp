<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="templates/header_template.jsp"/>
<script type="text/javascript">
    var replaceWithDefault = function(image){
        image.onerror = "";
        image.src = "/images/products/product_image_unavailable.jpg";
        return true;
    }
</script>
<form method="get" action="<c:url value="/cart" />" id="add_to_cart_form">
    <input type="hidden" name="pid" value="0" id="add_to_cart_pid">
    <input type="hidden" name="method" value="add_to_cart">
</form>

<div class="container-fluid">
    <div class="row">
        <div class="col-sm-3 col-lg-2">
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
                        <span class="visible-xs navbar-brand">Catalogues</span>
                    </div>
                    <div class="navbar-collapse collapse sidebar-navbar-collapse">
                        <div class="navbar-header">
                            <a class="navbar-brand" href="#">Catalogues</a>
                        </div>
                        <ul class="nav navbar-nav">
                            <c:forEach items="${categories}" var="cat">
                                <li><a href="?cid=${cat.id}">${cat.name}</a></li>
                            </c:forEach>
                        </ul>
                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>
        <div class="col-sm-9 col-lg-10 products-container">
            <c:if test="${empty product_list}">
                <div class="jumbotron">
                    <h1>e-Bazaar</h1>
                    <p>Select a catalog from left navigation</p>
                </div>
            </c:if>

            <!-- start product listing -->
            <div class="row">
                <c:set var="product_count" value="0" scope="page"/>
                <c:forEach items="${product_list}" var="product">
                    <c:if test="${product_count % 3 == 0}"><div class="row"></c:if>
                    <%-- set variable product and call the panel template --%>
                    <c:set var="product" value="${product}" scope="request" />
                    <jsp:include page="/templates/product_panel_template.jsp" />
                    <c:if test="${product_count % 3 == 2}"></div></c:if>
                    <c:set var="product_count" value="${product_count + 1}" scope="page"/>
                </c:forEach>
            </div>
            <!-- end product listing -->

        </div>
    </div>
</div>


<jsp:include page="templates/footer_template.jsp"/>