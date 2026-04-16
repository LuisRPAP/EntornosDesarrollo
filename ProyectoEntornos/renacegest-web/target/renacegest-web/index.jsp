<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:choose>
    <c:when test="${empty sessionScope.currentRole}">
        <c:redirect url="/login" />
    </c:when>
    <c:when test="${sessionScope.currentRole == 'Amigo'}">
        <c:redirect url="/amigos" />
    </c:when>
    <c:otherwise>
        <c:redirect url="/home" />
    </c:otherwise>
</c:choose>
