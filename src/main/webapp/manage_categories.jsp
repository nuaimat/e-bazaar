<%--
  Created by IntelliJ IDEA.
  User: nuaimat
  Date: 5/21/17
  Time: 4:49 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="templates/header_template.jsp"/>
<div class="container-fluid">
    <div class="jumbotron">
        <h1>e-Bazaar</h1>
        <p>Manage Cataloges</p>
    </div>
    <script type="text/javascript">
        $(function () {
            $(".cat-submit-new").click(function () {
                var newCatName = $("#catalogue_name").val();
                if(newCatName == null || newCatName.length < 1) {
                    alert("Invalid catalogue name");
                    return;
                }
                submitCatForm("new_cat", "", newCatName);

            });

            $(".cat-delete").click(function (btn) {
                var catid = $(btn.target).val();
                if(catid.length < 1 || parseInt(catid) < 1){
                    alert("Wrong Catalogue ID");
                    return false;
                }
                submitCatForm("del_cat", catid, "");
            });

            $(".cat-edit").click(function (btn) {
                var catid = $(btn.target).val();
                if(catid.length < 1 || parseInt(catid) < 1){
                    alert("Wrong Catalogue ID");
                    return false;
                }
                window.location = "<c:url value="/admin_products?method=edit_catalogues&catid=" />" + catid;
            });

            $(".cat-save-edit").click(function (btn) {
                var catid = $(btn.target).val();
                if(catid.length < 1 || parseInt(catid) < 1){
                    alert("Wrong Catalogue ID");
                    return false;
                }
                var newCatName = $("#catalogue_name").val();
                if(newCatName == null || newCatName.length < 1) {
                    alert("Invalid catalogue name");
                    return false;
                }
                submitCatForm("save_cat", catid, newCatName);
            });

            var submitCatForm = function (cmethod, cid, cname) {
                $("#form_method").val(cmethod);
                $("#form_id").val(cid);
                $("#form_name").val(cname);
                $("#catalogue_form").submit();
            }
        });
    </script>
    <form method="post" action="/admin_products" id="catalogue_form">
        <input type="hidden" name="method" value="new_cat" id="form_method">
        <input type="hidden" name="id" value="" id="form_id">
        <input type="hidden" name="name" id="form_name">
    </form>
    <div class="row">
        <div class="col-lg-3">
        </div>
        <div class="col-sm-12 col-lg-6">
            <c:if test="${not empty param.msg}">
                <div class="alert alert-success alert-dismissable">
                    <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                        ${param.msg}
                </div>
            </c:if>
            <table class="table table-hover">
                <thead>
                <tr>
                    <th style="width:10%">Catalogue ID</th>
                    <th style="width:70%">Catalogue Name</th>
                    <th style="width:20%"></th>

                </tr>
                </thead>
                <tbody>
                <c:forEach items="${categories}" var="cat">
                    <c:choose>
                        <c:when test="${method eq 'edit_cat' && cat.id eq catid}">
                            <c:set var="selected_category" value="${cat}" />
                        </c:when>
                        <c:otherwise>
                            <tr id="cat-${cat.id}">
                                <td>${cat.id}</td>
                                <td>${cat.name}</td>
                                <td class="actions" data-th="">
                                    <button class="btn btn-info btn-sm cat-edit" value="${cat.id}"><i
                                            class="fa fa-pencil-square-o"></i></button>
                                    <button class="btn btn-danger btn-sm cat-delete" value="${cat.id}"><i
                                            class="fa fa-trash-o"></i></button>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>

                </c:forEach>
                <tr>
                    <td>&nbsp;</td>
                    <td colspan="2"><strong>
                                <c:choose>
                                    <c:when test="${method eq 'edit_cat'}">
                                        Edit
                                    </c:when>
                                    <c:otherwise>
                                        New
                                    </c:otherwise>
                                </c:choose>
                        category:</strong></td>
                </tr>
                <tr>
                    <td>&nbsp;
                    </td>
                    <td>
                        Name: <input type="text" id="catalogue_name" value="${selected_category.name}" required>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${method eq 'edit_cat'}">
                                <button class="btn btn-info btn-sm cat-save-edit" value="${selected_category.id}"><i
                                        class="fa fa-floppy-o"></i></button>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-info btn-sm cat-submit-new"><i
                                        class="fa fa-floppy-o"></i></button>
                            </c:otherwise>
                        </c:choose>


                    </td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="col-lg-3">
        </div>

    </div>
</div>

<jsp:include page="templates/footer_template.jsp"/>