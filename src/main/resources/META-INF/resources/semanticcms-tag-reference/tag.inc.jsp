<%--
semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
Copyright (C) 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
<%@ taglib prefix="section" uri="https://semanticcms.com/section/taglib/" %>
<%@ taglib prefix="tagref" uri="https://semanticcms.com/tag-reference/" %>

<%--
The view of one tag at /path/taglib.tld/tag-tagName

Arguments:
  arg.tldRef        The PageRef for the TLD file itself
  arg.tag           The parsed tag
  arg.requireLinks  When true, will fail when a class does not map to a
                    package in apiLinks.
                    Defaults to false.
  arg.apiLinks      The mapping of Java package name (with optional trailing '.')
                    to javadoc prefixes (including trailing '/')
--%>
<c:set var="tldRef" value="${arg.tldRef}" />
<c:set var="tag" value="${arg.tag}" />
<c:set var="requireLinks" value="${arg.requireLinks}" />
<c:set var="apiLinks" value="${arg.apiLinks}" />
<c:set var="dates" value="${tag.dates}" />
<core:page
  book="${tldRef.bookName}"
  path="${tldRef.path}/tag-${ao:decodeURI(ao:encodeURIComponent(tag.name))}"
  title="<${tag.taglib.shortName}:${tag.name}>"
  dateCreated="${dates.created}"
  datePublished="${dates.published}"
  dateModified="${dates.modified}"
  dateReviewed="${dates.reviewed}"
  allowRobots="${tag.allowRobots}"
>
  <core:parent
    book="${tldRef.bookName}"
    page="${tldRef.path}/tags"
  />
  <c:forEach var="description" items="${tag.descriptions}">
    <ao:out value="${description}" type="xhtml" />
  </c:forEach>
  <c:if test="${!empty tag.example}">
    <section:section label="Example">
      <ao:out value="${tag.example}" type="xhtml" />
    </section:section>
  </c:if>
  <section:section label="Tag Information">
    <table class="ao-grid">
      <tbody>
        <tr>
          <th>Tag Class:</th>
          <td style="white-space:nowrap">
            <tagref:linkedClassName requireLinks="${requireLinks}" apiLinks="${apiLinks}" className="${tag.tagClass}" />
          </td>
        </tr>
        <tr>
          <th>TagExtraInfo Class:</th>
          <td style="white-space:nowrap">
            <c:choose>
              <c:when test="${!empty tag.teiClass}">
                <tagref:linkedClassName requireLinks="${requireLinks}" apiLinks="${apiLinks}" className="${tag.teiClass}" />
              </c:when>
              <c:otherwise>
                <em>None</em>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <th>Body Content:</th>
          <td style="white-space:nowrap"><ao:out value="${tag.bodyContent}" /></td>
        </tr>
        <tr>
          <th>Display Name:</th>
          <td style="white-space:nowrap">
            <c:choose>
              <c:when test="${!empty tag.displayNames}">
                <c:forEach var="displayName" items="${tag.displayNames}" varStatus="displayNameStatus">
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
        <c:if test="${tag.dynamicAttributes}">
          <tr>
            <th>Dynamic Attributes:</th>
            <td style="white-space:nowrap"><ao:out value="${tag.dynamicAttributes}" /></td>
          </tr>
        </c:if>
      </tbody>
    </table>
  </section:section>
  <section:section label="Attributes">
    <c:choose>
      <c:when test="${!empty tag.attributes}">
        <table class="ao-grid">
          <thead>
            <tr>
              <th>Name</th>
              <th>Required</th>
              <th>Evaluation</th>
              <th>Type</th>
              <th>Description</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="attribute" items="${tag.attributes}">
              <c:set var="deferredMethod" value="${attribute.deferredMethod}" />
              <c:set var="deferredValue" value="${attribute.deferredValue}" />
              <c:set var="showType" value="${
                attribute.rtexprvalue
                || !empty attribute.type
                || (deferredMethod == null && deferredValue == null)
              }" />

              <c:set var="rowspan" value="0" />
              <c:if test="${showType}">
                <c:set var="rowspan" value="${rowspan + 1}" />
              </c:if>
              <c:if test="${deferredMethod != null}">
                <c:set var="rowspan" value="${rowspan + 1}" />
              </c:if>
              <c:if test="${deferredValue != null}">
                <c:set var="rowspan" value="${rowspan + 1}" />
              </c:if>

              <c:set var="row" value="0" />
              <c:if test="${showType}">
                <c:set var="row" value="${row + 1}" />
                <tr>
                  <c:if test="${row == 1}">
                    <td rowspan="${rowspan}" style="white-space:nowrap"><ao:out value="${attribute.name}" /></td>
                    <td rowspan="${rowspan}" style="white-space:nowrap"><ao:out value="${attribute.required ? 'Yes' : 'No'}" /></td>
                  </c:if>
                  <td style="white-space:nowrap">
                    <ao:out value="${attribute.rtexprvalue ? 'Runtime' : 'Static'}" />
                    <c:if test="${attribute.fragment}">
                      <ao:br />Fragment
                    </c:if>
                  </td>
                  <td style="white-space:nowrap">
                    <tagref:linkedClassName
                      requireLinks="${requireLinks}"
                      apiLinks="${apiLinks}"
                      className="${!empty attribute.type ? attribute.type : attribute.fragment ? 'javax.servlet.jsp.tagext.JspFragment' : 'java.lang.String'}"
                      shortName="true"
                    />
                  </td>
                  <c:if test="${row == 1}">
                    <td rowspan="${rowspan}">
                      <c:forEach var="description" items="${attribute.descriptions}">
                        <ao:out value="${description}" type="xhtml" />
                      </c:forEach>
                    </td>
                  </c:if>
                </tr>
              </c:if>
              <c:if test="${deferredMethod != null}">
                <c:set var="row" value="${row + 1}" />
                <tr>
                  <c:if test="${row == 1}">
                    <td rowspan="${rowspan}" style="white-space:nowrap"><ao:out value="${attribute.name}" /></td>
                    <td rowspan="${rowspan}" style="white-space:nowrap"><ao:out value="${attribute.required ? 'Yes' : 'No'}" /></td>
                  </c:if>
                  <td style="white-space:nowrap">Deferred-Method</td>
                  <td style="white-space:nowrap"><tagref:linkedSignature requireLinks="${requireLinks}" apiLinks="${apiLinks}" signature="${deferredMethod.methodSignature}" /></td>
                  <c:if test="${row == 1}">
                    <td rowspan="${rowspan}">
                      <c:forEach var="description" items="${attribute.descriptions}">
                        <ao:out value="${description}" type="xhtml" />
                      </c:forEach>
                    </td>
                  </c:if>
                </tr>
              </c:if>
              <c:if test="${deferredValue != null}">
                <c:set var="row" value="${row + 1}" />
                <tr>
                  <c:if test="${row == 1}">
                    <td rowspan="${rowspan}" style="white-space:nowrap"><ao:out value="${attribute.name}" /></td>
                    <td rowspan="${rowspan}" style="white-space:nowrap"><ao:out value="${attribute.required ? 'Yes' : 'No'}" /></td>
                  </c:if>
                  <td style="white-space:nowrap">Deferred-Value</td>
                  <td style="white-space:nowrap">
                    <tagref:linkedClassName
                      requireLinks="${requireLinks}"
                      apiLinks="${apiLinks}"
                      className="${!empty deferredValue.type ? deferredValue.type : 'java.lang.Object'}"
                      shortName="true"
                    />
                  </td>
                  <c:if test="${row == 1}">
                    <td rowspan="${rowspan}">
                      <c:forEach var="description" items="${attribute.descriptions}">
                        <ao:out value="${description}" type="xhtml" />
                      </c:forEach>
                    </td>
                  </c:if>
                </tr>
              </c:if>
            </c:forEach>
          </tbody>
        </table>
      </c:when>
      <c:otherwise>
        <em>No Attributes Defined.</em>
      </c:otherwise>
    </c:choose>
  </section:section>
  <section:section label="Variables">
    <%-- TODO:
    <c:choose>
      <c:when test="${!empty tag.variables}">
        TODO: Document a variable when first needed.  We don't use any variables at this time.
      </c:when>
      <c:otherwise>
    --%>
        <em>No Variables Defined.</em>
    <%-- TODO:
      </c:otherwise>
    </c:choose>
    --%>
  </section:section>
</core:page>
