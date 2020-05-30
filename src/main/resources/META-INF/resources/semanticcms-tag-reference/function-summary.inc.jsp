<%--
semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
Copyright (C) 2016, 2017, 2019, 2020  AO Industries, Inc.
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
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib prefix="ao" uri="https://aoindustries.com/ao-taglib/" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="core" uri="https://semanticcms.com/core/taglib/" %>
<%@ taglib prefix="tagref" uri="https://semanticcms.com/tag-reference/" %>

<%--
Shared function summary implementation.

Arguments:
	arg.tldRef    The ResourceRef for the TLD file itself
	arg.taglib    The parsed taglib
	arg.apiLinks  The mapping of Java package name (with optional trailing '.')
	              to javadoc prefixes (including trailing '/')
--%>
<c:set var="tldRef" value="${arg.tldRef}" />
<c:set var="taglib" value="${arg.taglib}" />
<c:set var="apiLinks" value="${arg.apiLinks}" />
<table class="ao-grid">
	<tbody>
		<c:forEach var="function" items="${taglib.functions}">
			<tr>
				<td style="white-space:nowrap">
					<tagref:linkedSignatureReturn apiLinks="${apiLinks}" signature="${function.functionSignature}" shortName="true" />
				</td>
				<td style="white-space:nowrap">
					\${<ao:out value="${taglib.shortName}" />:<core:link book="#{tldRef.bookName}" page="#{tldRef.path}/function-#{ao:decodeURI(ao:encodeURIComponent(function.name))}"
						><strong><ao:out value="${function.name}"
					/></strong></core:link><tagref:linkedSignatureParams apiLinks="${apiLinks}" signature="${function.functionSignature}" shortName="true"
				/>}</td>
				<td><ao:out value="${function.descriptionSummary}" type="xhtml" /></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
