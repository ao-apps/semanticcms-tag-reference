<%--
semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
Copyright (C) 2016, 2017, 2019, 2020, 2021, 2022, 2025, 2026  AO Industries, Inc.
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
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="core" uri="https://semanticcms.com/core/taglib/" %>
<%@ taglib prefix="section" uri="https://semanticcms.com/section/taglib/" %>
<%@ taglib prefix="tagref" uri="https://semanticcms.com/tag-reference/" %>

<%--
The view of one function at /path/taglib.tld/function-functionName

Arguments:
  arg.tldRef        The ResourceRef for the TLD file itself
  arg.function      The parsed function
  arg.requireLinks  When true, will fail when a class does not map to a
                    package in apiLinks.
                    Defaults to false.
  arg.apiLinks      The mapping of Java package name (with optional trailing '.')
                    to javadoc prefixes (including trailing '/')
--%>
<c:set var="tldRef" value="${arg.tldRef}" />
<c:set var="function" value="${arg.function}" />
<c:set var="requireLinks" value="${arg.requireLinks}" />
<c:set var="apiLinks" value="${arg.apiLinks}" />
<c:set var="dates" value="${function.dates}" />
<core:page
  book="${tldRef.bookName}"
  path="${tldRef.path}/function-${ao:decodeURI(ao:encodeURIComponent(function.name))}"
  title="\${${function.taglib.shortName}:${function.name}()}"
  dateCreated="${dates.created}"
  datePublished="${dates.published}"
  dateModified="${dates.modified}"
  dateReviewed="${dates.reviewed}"
  allowRobots="${function.allowRobots}"
>
  <core:parent
    book="${tldRef.bookName}"
    page="${tldRef.path}/functions"
  />
  <c:forEach var="description" items="${function.descriptions}">
    <ao:out value="${description}" type="xhtml" />
  </c:forEach>
  <c:if test="${!empty function.example}">
    <section:section label="Example">
      <ao:out value="${function.example}" type="xhtml" />
    </section:section>
  </c:if>
  <section:section label="Function Information">
    <table class="ao-grid">
      <tbody>
        <tr>
          <th>Function Class:</th>
          <td style="white-space:nowrap">
            <tagref:linkedClassName requireLinks="${requireLinks}" apiLinks="${apiLinks}" className="${function.functionClass}" />
          </td>
        </tr>
        <tr>
          <th>Function Signature:</th>
          <td style="white-space:nowrap"><tagref:linkedSignature requireLinks="${requireLinks}" apiLinks="${apiLinks}" signature="${function.functionSignature}" /></td>
        </tr>
        <tr>
          <th>Display Name:</th>
          <td style="white-space:nowrap">
            <c:choose>
              <c:when test="${!empty function.displayNames}">
                <c:forEach var="displayName" items="${function.displayNames}" varStatus="displayNameStatus">
                  <ao:out value="${displayName}" type="xhtml" />
                  <c:if test="${!displayNameStatus.last}">
                    <ao:br />
                  </c:if>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <em>None</em>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </tbody>
    </table>
  </section:section>
</core:page>
