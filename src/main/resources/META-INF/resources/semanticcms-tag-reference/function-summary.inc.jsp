<%--
semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
Copyright (C) 2016, 2017  AO Industries, Inc.
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
<%@ taglib prefix="core" uri="https://semanticcms.com/core/taglib/" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--
Shared function summary implementation.

Arguments:
	arg.tldRef    The ResourceRef for the TLD file itself
	arg.taglib    The parsed taglib
	arg.apiLinks  The mapping of java package prefix (including trailing '.') to javadoc prefixes (including trailing '/')
--%>
<c:set var="tldRef" value="${arg.tldRef}" />
<c:set var="taglib" value="${arg.taglib}" />
<c:set var="apiLinks" value="${arg.apiLinks}" />
<table class="thinTable">
	<tbody>
		<c:forEach var="function" items="${taglib.functions}">
			<c:set var="functionSignature" value="${function.functionSignature}" />
			<c:set var="returnType" value="${fn:substringBefore(functionSignature, ' ')}" />
			<c:set var="signatureParams" value="${fn:substringBefore(fn:substringAfter(functionSignature, '('), ')')}" />
			<tr>
				<td style="white-space:nowrap">
					<ao:include
						page="linked-classname.inc.jsp"
						arg.apiLinks="${apiLinks}"
						arg.className="${returnType}"
						arg.shortName="true"
					/>
				</td>
				<td style="white-space:nowrap">
					<c:set var="functionName" value="${function.name}" />
					\${<ao:out value="${taglib.shortName}" />:<core:link book="#{tldRef.bookName}" page="#{tldRef.path}/function-#{core:encodeUrlParam(functionName)}"
						><strong><ao:out value="${functionName}"
					/></strong></core:link>(<c:forEach var="paramType" items="${fn:split(signatureParams, ',')}" varStatus="paramTypeStatus"
						><ao:include
							page="linked-classname.inc.jsp"
							arg.apiLinks="${apiLinks}"
							arg.className="${paramType}"
							arg.shortName="true"
						/><c:if test="${!paramTypeStatus.last}">, </c:if
					></c:forEach
				>)}</td>
				<td><ao:out value="${function.descriptionSummary}" type="xhtml" /></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
