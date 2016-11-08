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
<%@ taglib prefix="section" uri="https://semanticcms.com/section/taglib/" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

<%--
The view of one function at /path/taglib.tld/function-functionName

Arguments:
	arg.tldRef        The PageRef for the TLD file itself
	arg.tldDoc        The XML DOM document for the .tld file
	arg.functionName  The name of the function to display
	arg.apiLinks      The mapping of java package prefix (including trailing '.') to javadoc prefixes (including trailing '/')
--%>
<c:set var="tldRef" value="${arg.tldRef}" />
<c:set var="tldDoc" value="${arg.tldDoc}" />
<c:set var="functionName" value="${arg.functionName}" />
<c:set var="apiLinks" value="${arg.apiLinks}" />
<x:set var="taglibElem" select="$tldDoc/taglib" />
<x:set var="tldShortName" select="string($taglibElem/short-name)" />
<x:set var="functionElem" select="$tldDoc/taglib/function[string(name)=$functionName]" />
<core:page
	book="${tldRef.bookName}"
	path="${tldRef.path}/function-${functionName}"
	title="\${${tldShortName}:${functionName}()}"
	dateModified="${ao:getLastModified(tldRef.servletPath)}"
>
	<core:parent
		book="${tldRef.bookName}"
		page="${tldRef.path}/functions"
	/>
	<x:forEach var="description" select="$functionElem/description">
		<x:out select="$description" escapeXml="false" />
	</x:forEach>
	<x:if select="boolean($functionElem/example)">
		<section:section label="Example">
			<x:out select="$functionElem/example" escapeXml="false" />
		</section:section>
	</x:if>
	<section:section label="Function Information">
		<table class="thinTable">
			<tbody>
				<tr>
					<th>Function Class:</th>
					<td>
						<x:set var="functionClass" select="string($functionElem/function-class)" />
						<ao:include page="linked-classname.inc.jsp" arg.apiLinks="${apiLinks}" arg.className="${functionClass}" />
					</td>
				</tr>
				<tr>
					<th>Function Signature:</th>
					<td><x:out select="$functionElem/function-signature" /></td>
				</tr>
				<tr>
					<th>Display Name:</th>
					<td>
						<x:choose>
							<x:when select="boolean($functionElem/display-name)">
								<x:forEach var="displayName" select="$functionElem/display-name" varStatus="displayNameStatus">
									<x:out select="$displayName" escapeXml="false" />
									<c:if test="${!displayNameStatus.last}">
										<br />
									</c:if>
								</x:forEach>
							</x:when>
							<x:otherwise>
								<em>None</em>
							</x:otherwise>
						</x:choose>
					</td>
				</tr>
			</tbody>
		</table>
	</section:section>
</core:page>
