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
The overall view of the taglib at /path/taglib.tld/

Arguments:
	arg.title       The page title
	arg.shortTitle  The page shortTitle
	arg.tldRef      The PageRef for the TLD file itself
	arg.tldDoc      The XML DOM document for the .tld file
	arg.apiLinks    The mapping of java package prefix (including trailing '.') to javadoc prefixes (including trailing '/')
--%>
<c:set var="tldRef" value="${arg.tldRef}" />
<c:set var="tldDoc" value="${arg.tldDoc}" />
<c:set var="apiLinks" value="${arg.apiLinks}" />
<x:set var="taglibElem" select="$tldDoc/taglib" />
<core:page
	book="${tldRef.bookName}"
	path="${tldRef.path}/"
	title="${arg.title}"
	shortTitle="${arg.shortTitle}"
	dateModified="${ao:getLastModified(tldRef.servletPath)}"
>
	<%-- TODO: file:file link to .tld file itself? --%>
	<x:forEach var="description" select="$taglibElem/description">
		<x:out select="$description" escapeXml="false" />
	</x:forEach>
	<section:section label="Usage">
		<section:section label="Standard Syntax">
			<code>&lt;%@ taglib prefix="<x:out select="$taglibElem/short-name" />" uri="<x:out select="$taglibElem/uri" />" %&gt;</code>
		</section:section>
		<section:section label="XML Syntax">
			<code>&lt;anyxmlelement xmlns:<x:out select="$taglibElem/short-name" />="<x:out select="$taglibElem/uri" />" /&gt;</code>
		</section:section>
	</section:section>
	<section:section label="Tag Library Information">
		<table class="thinTable">
			<tbody>
				<x:if select="boolean($taglibElem/display-name)">
					<tr>
						<th>Display Name:</th>
						<td>
							<x:forEach var="displayName" select="$taglibElem/display-name" varStatus="displayNameStatus">
								<x:out select="$displayName" escapeXml="false" />
								<c:if test="${!displayNameStatus.last}">
									<br />
								</c:if>
							</x:forEach>
						</td>
					</tr>
				</x:if>
				<tr>
					<th>Version:</th>
					<td><x:out select="$taglibElem/tlib-version" /></td>
				</tr>
				<tr>
					<th>Short Name:</th>
					<td><x:out select="$taglibElem/short-name" /></td>
				</tr>
				<x:if select="boolean($taglibElem/uri)">
					<tr>
						<th>URI:</th>
						<td><x:out select="$taglibElem/uri" /></td>
					</tr>
				</x:if>
			</tbody>
		</table>
	</section:section>
	<x:if select="boolean($taglibElem/tag)">
		<core:child book="${tldRef.bookName}" page="${tldRef.path}/tags" />
		<%-- TODO: Links in section labels --%>
		<section:section label="Tag Summary">
			<ao:include
				page="tag-summary.inc.jsp"
				arg.tldRef="${tldRef}"
				arg.tldDoc="${tldDoc}"
			/>
		</section:section>
	</x:if>
	<x:if select="boolean($taglibElem/function)">
		<core:child book="${tldRef.bookName}" page="${tldRef.path}/functions" />
		<%-- TODO: Links in section labels --%>
		<section:section label="Function Summary">
			<ao:include
				page="function-summary.inc.jsp"
				arg.tldRef="${tldRef}"
				arg.tldDoc="${tldDoc}"
				arg.apiLinks="${apiLinks}"
			/>
		</section:section>
	</x:if>
</core:page>
