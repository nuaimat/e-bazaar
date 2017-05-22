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
<style type="text/css">
    label {font-weight: bold}
</style>
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
        <div class="col-sm-7 col-lg-8 products-container">

            <div class="jumbotron">
                <h1>e-Bazaar</h1>
                <p>Editing a product</p>
            </div>


            <!-- start product listing -->
            <div class="row">
                <form method="post" action="/admin_products">
                    <dl>
                        <dt>Catalogue:</dt>
                        <dd><select name="catid">
                            <c:forEach items="${categories}" var="cat">
                                <c:choose>
                                    <c:when test="${cat.id eq product.catalog.id}" >
                                        <c:set var="sel" value="selected='selected'" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="sel" value="" />
                                    </c:otherwise>
                                </c:choose>
                                <option value="${cat.id}" ${sel} >${cat.name}</option>
                            </c:forEach>
                        </select></dd>

                        <dt>Name:</dt>
                        <dd><input type="text" name="name" value="${product.productName}"></dd>

                        <dt>Manufacture Date:</dt>
                        <dd><input type="text" name="mfg_date" value="${product.mfgDateFormatted}" placeholder="mm/dd/yyyy"></dd>

                        <dt># items in stock:</dt>
                        <dd><input type="number" name="quantity" value="${product.quantityAvail}"></dd>

                        <dt>Unit Price:</dt>
                        <dd><input type="number" step="0.01" name="unit_price" value="${product.unitPrice}"></dd>

                        <dt>Description:</dt>
                        <dd><textarea rows="2" cols="60" name="description">${product.description}</textarea></dd>
                    </dl>

                    <div class="text-center">
                        <input type="submit" class="btn btn-success" value="Save" />
                    </div>
                    <input type="hidden" name="pid" value="${product.productId}">
                    <input type="hidden" name="method" value="save_product">

                </form>
            </div>
            <!-- end product listing -->

        </div>
        <div class="col-sm-2 col-lg-2">
    </div>
</div>


<jsp:include page="templates/footer_template.jsp"/>