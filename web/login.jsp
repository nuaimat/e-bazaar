<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="templates/header_template.jsp">
    <jsp:param name="title" value="Login"/>
</jsp:include>


<style>
    fieldset {
        width: 20%;
        margin: 0 auto;
    }

    input[type=submit] {
        display: block;
        width: 50%;
        margin: 0 auto;
    }

    div.ch {
        width: 60%;
        margin: 0 auto;
        padding: 0 0 10px 0;
    }

    .error {
        color: red;
    }
    
</style>
<script type="text/javascript">
    document.onload = function(){
        document.getElementById("form").target = location.protocol + '//' + location.host + location.pathname;
    }
</script>

<section class="loginform">
    <form name="login" method="post" id="form" action="<c:url value="/login" />">
        <fieldset>
            <legend>Restricted Area</legend>
            <c:choose>
                <c:when test="${not empty param.msg}"><p class="error">${param.msg}</p></c:when>
                <c:when test="${not empty param.errorMsg}"><p class="error">${param.errorMsg}</p></c:when>
                <c:otherwise><p>Login required to access this area</p></c:otherwise>
            </c:choose>

            <ul>
                <li><label for="id" style="display: block;">ID</label>
                    <input type="text" id="id" name="id" placeholder="username" required ></li>
                <li><label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="password" required ></li>
            </ul>
            <input type="submit" value="Login">

        </fieldset>
    </form>
</section>
<jsp:include page="templates/footer_template.jsp"/>