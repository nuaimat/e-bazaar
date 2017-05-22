<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="templates/header_template.jsp"/>
<script type="text/javascript">
    $(function () {
        $(".edit-product-btn").click(function (btn) {
            var pid = $(btn.target).val();
            if(pid.length < 1 || parseInt(pid) < 1){
                alert("Wrong Prod ID");
                return false;
            }
            window.location = "<c:url value="/admin_products?method=edit_products&cid=${param.cid}&pid=" />" + pid;
        });

        /* $(".cat-save-edit").click(function (btn) {
            var catid = $(btn.target).val();
            if(catid.length < 1 || parseInt(catid) < 1){
                alert("Wrong Catalogue ID");
                return false;
            }
            var newCatName = $("#catalogue_name").val();
            if(newCatName == null || catalogue_name.length < 1) {
                alert("Invalid catalogue name");
                return false;
            }
            submitProdForm("save_cat", catid, newCatName);
        }); */

        var submitProdForm = function (cmethod, cid, cname) {
            $("#manage_product_method").val(cmethod);
            $("#manage_product_pid").val(cid);
            //$("#form_name").val(cname);
            $("#manage_product_form").submit();
        }
    });
</script>
<form method="post" action="<c:url value="/admin_products" />" id="manage_product_form">
    <input type="hidden" name="pid" value="0" id="manage_product_pid">
    <input type="hidden" name="method" id="manage_product_method" value="delete_product">
    <input type="hidden" name="cid" value="" id="${param.cid}">
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
                                <li><a href="<c:url value="/admin_products" />?method=manage_products&cid=${cat.id}">${cat.name}</a></li>
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
            <c:if test="${not empty param.msg}">
                <div class="alert alert-success alert-dismissable">
                    <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                        ${param.msg}
                </div>
            </c:if>

            <!-- start product listing -->
            <div class="row">
                <c:set var="product_count" value="0" scope="page"/>
                <c:forEach items="${product_list}" var="product">
                    <c:if test="${product_count % 3 == 0}"><div class="row"></c:if>
                    <%-- set variable product and call the panel template --%>
                    <c:set var="product" value="${product}" scope="request" />
                    <jsp:include page="/templates/admin_product_panel_template.jsp" />
                    <c:if test="${product_count % 3 == 2}"></div></c:if>
                    <c:set var="product_count" value="${product_count + 1}" scope="page"/>
                </c:forEach>
            </div>
            <!-- end product listing -->

        </div>
    </div>
</div>


<jsp:include page="templates/footer_template.jsp"/>