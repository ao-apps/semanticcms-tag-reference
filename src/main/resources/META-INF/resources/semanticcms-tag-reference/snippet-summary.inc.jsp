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
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

<%--
Displays the first paragraph of the provided HTML snippet.
If there is no paragraph, the entire snippet is displayed.

This parses the HTML snippet into a DOM each invocation.
For higher performance, use another mechanism to compute once and use repeatedly.
This is for convenience, not performance.

Arguments:
	arg.htmlSnippet  The HTML snippet to display
--%>
<x:parse var="parsedHtml" xml="<html>${arg.htmlSnippet}</html>" />
<x:choose>
	<x:when select="boolean($parsedHtml/html//*[@class='semanticcms-tag-reference-summary'])">
		<x:forEach var="summaryElem" select="$parsedHtml/html//*[@class='semanticcms-tag-reference-summary']">
			<ao:out value="${summaryElem}" type="xhtml" />
		</x:forEach>
	</x:when>
	<x:otherwise>
		<ao:out value="${arg.htmlSnippet}" type="xhtml" />
	</x:otherwise>
</x:choose>
