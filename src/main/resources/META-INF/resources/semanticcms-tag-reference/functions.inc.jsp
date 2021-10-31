<%--
semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
Copyright (C) 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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
along with semanticcms-tag-reference.  If not, see <https://www.gnu.org/licenses/>.
--%>
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib prefix="ao" uri="https://oss.aoapps.com/taglib/" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="core" uri="https://semanticcms.com/core/taglib/" %>

<%--
The view of function summaries at /path/taglib.tld/functions

Arguments:
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
<c:set var="dates" value="${taglib.functionsEffectiveDates}" />
<core:page
	book="${tldRef.bookName}"
	path="${tldRef.path}/functions"
	title="Function Summary"
	shortTitle="Functions"
	dateCreated="${dates.created}"
	datePublished="${dates.published}"
	dateModified="${dates.modified}"
	dateReviewed="${dates.reviewed}"
>
	<%-- Add one child per function --%>
	<c:forEach var="function" items="${taglib.functions}">
		<core:child
			book="${tldRef.bookName}"
			page="${tldRef.path}/function-${ao:decodeURI(ao:encodeURIComponent(function.name))}"
		/>
	</c:forEach>
	<ao:include
		page="function-summary.inc.jsp"
		arg.tldRef="${tldRef}"
		arg.taglib="${taglib}"
		arg.requireLinks="${requireLinks}"
		arg.apiLinks="${apiLinks}"
	/>
</core:page>
