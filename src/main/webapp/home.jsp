<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="templates/header_template.jsp" />
<h2>Hello
    <c:choose>
        <c:when test="${loggedInUser}">
            <c:out value="${firstname}" />
        </c:when>
        <c:otherwise>
            Guest
        </c:otherwise>
    </c:choose>
!</h2>

<section class="biglogo">
    <img src="<c:url value="/images/logo_large.png" />" width="300" alt="e-bazaar logo"/>
</section>
<jsp:include page="templates/footer_template.jsp" />