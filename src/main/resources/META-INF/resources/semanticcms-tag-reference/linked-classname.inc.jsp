<%--
semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
Copyright (C) 2016  AO Industries, Inc.
    support@aoindustries.com
    7262 Bull Pen Cir
    Mobile, AL 36695

This file is part of semanticcms-tag-reference.

semanticcms-tag-reference is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

semanticcms-tag-reference is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with semanticcms-tag-reference.  If not, see <http://www.gnu.org/licenses />.
--%>
<%@ page language="java" pageEncoding="UTF-8" session="false" %>
<%@ taglib prefix="ao" uri="https://aoindustries.com/ao-taglib/" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--
Displays a classname, possibly linked to javadocs apis.

This parses the HTML snippet into a DOM each invocation.
For higher performance, use another mechanism to compute once and use repeatedly.
This is for convenience, not performance.

Arguments:
	arg.apiLinks   The mapping of java package prefix (including trailing '.') to javadoc prefixes (including trailing '/')
	arg.className  The classname to display.
--%>
<c:set var="className" value="${fn:trim(arg.className)}" />
<c:set var="done" value="false" />
<c:forEach var="entry" items="${arg.apiLinks}">
	<c:if test="${!done && fn:startsWith(className, entry.key)}">
		<ao:a href="${entry.value}${fn:replace(className, '.', '/')}.html"><ao:out value="${className}" /></ao:a>
		<c:set var="done" value="true" />
	</c:if>
</c:forEach>
<c:if test="${!done}">
	<ao:out value="${className}" />
</c:if>
