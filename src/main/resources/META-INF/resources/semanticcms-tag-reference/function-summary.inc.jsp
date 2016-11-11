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
<%@ taglib prefix="core" uri="https://semanticcms.com/core/taglib/" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

<%--
Shared function summary implementation.

Arguments:
	arg.tldRef    The PageRef for the TLD file itself
	arg.tldDoc    The XML DOM document for the .tld file
	arg.apiLinks  The mapping of java package prefix (including trailing '.') to javadoc prefixes (including trailing '/')
--%>
<c:set var="tldRef" value="${arg.tldRef}" />
<c:set var="tldDoc" value="${arg.tldDoc}" />
<c:set var="apiLinks" value="${arg.apiLinks}" />
<x:set var="taglibElem" select="$tldDoc/taglib" />
<x:set var="tldShortName" select="string($taglibElem/short-name)" />
<table class="thinTable">
	<tbody>
		<x:forEach select="$taglibElem/function">
			<x:set var="functionSignature" select="string(function-signature)" />
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
					<x:set var="functionName" select="string(name)" />
					\${<ao:out value="${tldShortName}" />:<core:link book="#{tldRef.bookName}" page="#{tldRef.path}/function-#{functionName}"
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
				<td>
					<x:set var="description" select="string(description[1])" />
					<c:if test="${!empty description}">
						<ao:include page="snippet-summary.inc.jsp" arg.htmlSnippet="${description}" />
					</c:if>
				</td>
			</tr>
		</x:forEach>
	</tbody>
</table>
