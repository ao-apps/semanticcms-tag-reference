<%--
semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
Copyright (C) 2016, 2017, 2019, 2021, 2022  AO Industries, Inc.
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
The view of tag summaries at /path/taglib.tld/tags

Arguments:
  arg.tldRef   The ResourceRef for the TLD file itself
  arg.taglib   The parsed taglib
--%>
<c:set var="tldRef" value="${arg.tldRef}" />
<c:set var="taglib" value="${arg.taglib}" />
<c:set var="dates" value="${taglib.tagsEffectiveDates}" />
<core:page
  book="${tldRef.bookName}"
  path="${tldRef.path}/tags"
  title="Tag Summary"
  shortTitle="Tags"
  dateCreated="${dates.created}"
  datePublished="${dates.published}"
  dateModified="${dates.modified}"
  dateReviewed="${dates.reviewed}"
>
  <%-- Add one child per tag --%>
  <c:forEach var="tag" items="${taglib.tags}">
    <core:child
      book="${tldRef.bookName}"
      page="${tldRef.path}/tag-${ao:decodeURI(ao:encodeURIComponent(tag.name))}"
    />
  </c:forEach>
  <ao:include
    page="tag-summary.inc.jsp"
    arg.tldRef="${tldRef}"
    arg.taglib="${taglib}"
  />
</core:page>
