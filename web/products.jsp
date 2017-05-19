<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="templates/header_template.jsp" />
<h2>Our Products</h2>

<ul>
    <c:forEach items="${categories}" var="cat">
        <li><c:out value="${cat.name}" /></li>
    </c:forEach>
</ul>
<jsp:include page="templates/footer_template.jsp" />