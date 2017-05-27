<%--
  Created by IntelliJ IDEA.
  User: nuaimat
  Date: 5/22/17
  Time: 8:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="templates/header_template.jsp"/>
<c:set var="rand"><%= java.lang.Math.round(java.lang.Math.random() * 200) %></c:set>
<script src="<c:url value="/js/checkout.js" />?${rand}" ></script>
<script>
    $( function() {
        $( "#expdate" ).datepicker();
        $("#submit_form").click(function () {
            showTermsAndConditions();
        });

        var showTermsAndConditions = function () {
            $('#tcModal').modal();
        }
        
        $(".modalbtn").click(function (btn) {
            var val = $(btn.target).val();
            if(val == "deny") {
                $('#tcModal').modal('hide');
            } else {
                $('#tcModal').modal('hide');
                $("#payment").submit();
            }
        });
    } );
</script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<div class="container-fluid">
    <div class="row">
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
                            <li><a href="<c:url value="/secure_checkout" />"><span class="glyphicon glyphicon-check"></span> 1. Confirm Addresses</a></li>
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
                <c:if test="${not empty param.errormsg}">
                    <div class="alert alert-danger alert-dismissable">
                        <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                        <strong>Error: </strong> ${param.errormsg}
                    </div>
                </c:if>
                <div class="page-header">
                    <h4>Payment details: </h4>
                </div>
                <div style="padding:40px 50px;">
                    <form role="form" id="payment" method="post" action="<c:url value="/secure_checkout" />">
                        <input type="hidden" name="method" value="submitPayment">
                        <div class="form-group">
                            <label for="name">Name on Card</label>
                            <input type="text" name="name" class="form-control" id="name" placeholder="John Snow"
                                value="${cc.nameOnCard}">
                        </div>
                        <div class="form-group">
                            <label for="cardnumber">Card Number</label>
                            <input type="number" name="cardnumber" class="form-control" id="cardnumber" placeholder="11123"
                                   value="${cc.cardNum}">
                        </div>
                        <div class="form-group">
                            <label for="cardtype">Card Type</label>
                            <select class="form-control" id="cardtype" name="cardtype">
                                <c:forEach items="${cc_types}" var="cctype">
                                    <c:choose>
                                        <c:when test="${cctype eq cc.cardType}" >
                                            <c:set var="sel" value="selected='selected'" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="sel" value="" />
                                        </c:otherwise>
                                    </c:choose>
                                    <option value="${cctype}" ${sel}>${cctype}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="expdate">Expiration Date</label>
                            <input type="text" name="expdate" class="form-control" id="expdate" placeholder="mm/dd/yyyy"
                                   value="${cc.expirationDate}">
                        </div>
                    </form>
                </div>

            </div>
            <table class="table">
                <tbody>
                <tr>
                    <td><a href="<c:url value="/secure_checkout" />" class="btn btn-warning"><i class="fa fa-angle-left"></i> Back to Addresses</a></td>
                    <td class="hidden-xs"></td>
                    <td class="hidden-xs text-center"></td>
                    <td><input type="submit" class="btn btn-success btn-block" id="submit_form" value="Proceed Agreement &gt;"></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal -->
<div id="tcModal" class="modal fade" role="dialog">
    <div class="modal-dialog" style="margin-top: 70px">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Terms And Conditions</h4>
            </div>
            <div class="modal-body">
                <p>${tc_msg}</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default modalbtn" value="deny">Deny</button>
                <button type="button" class="btn btn-default modalbtn" value="accept">Accept</button>
            </div>
        </div>

    </div>
</div>

<jsp:include page="templates/footer_template.jsp"/>

