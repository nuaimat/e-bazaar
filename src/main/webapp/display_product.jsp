<%--
  Created by IntelliJ IDEA.
  User: nuaimat
  Date: 5/19/17
  Time: 10:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="templates/header_template.jsp" />
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
                                <li><a href="<c:url value="/products" />?cid=${cat.id}">${cat.name}</a></li>
                            </c:forEach>
                        </ul>
                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>
        <div class="col-sm-9 col-lg-10 products-container">
            <!-- start product det -->
            <div class="row">
                <jsp:include page="templates/product_details.jsp" />
            </div>
            <!-- end product det -->

        </div>
    </div>
</div>

<jsp:include page="templates/footer_template.jsp" />
