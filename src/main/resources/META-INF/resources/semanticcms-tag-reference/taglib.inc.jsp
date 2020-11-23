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
<%@ taglib prefix="section" uri="https://semanticcms.com/section/taglib/" %>

<%--
The overall view of the taglib at /path/taglib.tld/

Arguments:
	arg.title         The page title
	arg.shortTitle    The page shortTitle
	arg.tldRef        The PageRef for the TLD file itself
	arg.taglib        The parsed taglib
	arg.requireLinks  When true, will fail when a class does not map to a
	                  package in apiLinks.
	                  Defaults to false.
	arg.apiLinks      The mapping of Java package name (with optional trailing '.')
	                  to javadoc prefixes (including trailing '/')
--%>
<c:set var="tldRef" value="${arg.tldRef}" />
<c:set var="taglib" value="${arg.taglib}" />
<c:set var="requireLinks" value="${arg.requireLinks}" />
<c:set var="apiLinks" value="${arg.apiLinks}" />
<c:set var="dates" value="${taglib.taglibEffectiveDates}" />
<core:page
	book="${tldRef.bookName}"
	path="${tldRef.path}/"
	title="${arg.title}"
	shortTitle="${arg.shortTitle}"
	dateCreated="${dates.created}"
	datePublished="${dates.published}"
	dateModified="${dates.modified}"
	dateReviewed="${dates.reviewed}"
	allowRobots="${taglib.allowRobots}"
>
	<%-- TODO: file:file link to .tld file itself? --%>
	<c:forEach var="description" items="${taglib.descriptions}">
		<ao:out value="${description}" type="xhtml" />
	</c:forEach>
	<section:section label="Usage">
		<section:section label="Standard Syntax">
			<code>&lt;%@ taglib prefix="<ao:out value="${taglib.shortName}" />" uri="<ao:out value="${taglib.uri}" />" %&gt;</code>
		</section:section>
		<section:section label="XML Syntax">
			<code>&lt;anyxmlelement xmlns:<ao:out value="${taglib.shortName}" />="<ao:out value="${taglib.uri}" />" /&gt;</code>
		</section:section>
	</section:section>
	<section:section label="Tag Library Information">
		<table class="ao-grid">
			<tbody>
				<c:if test="${!empty taglib.displayNames}">
					<tr>
						<th>Display Name:</th>
						<td style="white-space:nowrap">
							<c:forEach var="displayName" items="${taglib.displayNames}" varStatus="displayNameStatus">
								<ao:out value="${displayName}" type="xhtml" />
								<c:if test="${!displayNameStatus.last}">
									<ao:br />
								</c:if>
							</c:forEach>
						</td>
					</tr>
				</c:if>
				<tr>
					<th>Version:</th>
					<td style="white-space:nowrap"><ao:out value="${taglib.tlibVersion}" /></td>
				</tr>
				<tr>
					<th>Short Name:</th>
					<td style="white-space:nowrap"><ao:out value="${taglib.shortName}" /></td>
				</tr>
				<c:if test="${!empty taglib.uri}">
					<tr>
						<th>URI:</th>
						<td style="white-space:nowrap"><ao:out value="${taglib.uri}" /></td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</section:section>
	<c:if test="${!empty taglib.tags}">
		<core:child book="${tldRef.bookName}" page="${tldRef.path}/tags" />
		<%-- TODO: Links in section labels --%>
		<section:nav label="Tag Summary">
			<ao:include
				page="tag-summary.inc.jsp"
				arg.tldRef="${tldRef}"
				arg.taglib="${taglib}"
			/>
		</section:nav>
	</c:if>
	<c:if test="${!empty taglib.functions}">
		<core:child book="${tldRef.bookName}" page="${tldRef.path}/functions" />
		<%-- TODO: Links in section labels --%>
		<section:nav label="Function Summary">
			<ao:include
				page="function-summary.inc.jsp"
				arg.tldRef="${tldRef}"
				arg.taglib="${taglib}"
				arg.requireLinks="${requireLinks}"
				arg.apiLinks="${apiLinks}"
			/>
		</section:nav>
	</c:if>
</core:page>
