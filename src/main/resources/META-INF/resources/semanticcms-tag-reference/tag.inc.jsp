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
The view of one tag at /path/taglib.tld/tag-tagName

Arguments:
	arg.tldRef   The PageRef for the TLD file itself
	arg.tldDoc   The XML DOM document for the .tld file
	arg.tagName  The name of the tag to display
--%>
<c:set var="tldRef" value="${arg.tldRef}" />
<c:set var="tldDoc" value="${arg.tldDoc}" />
<c:set var="tagName" value="${arg.tagName}" />
<x:set var="taglibElem" select="$tldDoc/taglib" />
<x:set var="tldShortName" select="string($taglibElem/short-name)" />
<x:set var="tagElem" select="$tldDoc/taglib/tag[string(name)=$tagName]" />
<core:page
	book="${tldRef.bookName}"
	path="${tldRef.path}/tag-${tagName}"
	title="<${tldShortName}:${tagName}>"
	dateModified="${ao:getLastModified(tldRef.servletPath)}"
>
	<core:parent
		book="${tldRef.bookName}"
		page="${tldRef.path}/tags"
	/>
	<x:forEach var="description" select="$tagElem/description">
		<x:out select="$description" escapeXml="false" />
	</x:forEach>
	<section:section label="Tag Information">
		<table class="thinTable">
			<tbody>
				<tr>
					<th>Tag Class:</th>
					<td><x:out select="$tagElem/tag-class" /></td>
				</tr>
				<tr>
					<th>TagExtraInfo Class:</th>
					<td>
						<x:choose>
							<x:when select="boolean($tagElem/tei-class)">
								<x:out select="$tagElem/tei-class" />
							</x:when>
							<x:otherwise>
								<em>None</em>
							</x:otherwise>
						</x:choose>
					</td>
				</tr>
				<tr>
					<th>Body Content:</th>
					<td><x:out select="$tagElem/body-content" /></td>
				</tr>
				<tr>
					<th>Display Name:</th>
					<td>
						<x:choose>
							<x:when select="boolean($tagElem/display-name)">
								<x:forEach var="displayName" select="$tagElem/display-name" varStatus="displayNameStatus">
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
				<x:if select="boolean($tagElem/dynamic-attributes)">
					<tr>
						<th>Dynamic Attributes:</th>
						<td><x:out select="$tagElem/dynamic-attributes" /></td>
					</tr>
				</x:if>
			</tbody>
		</table>
	</section:section>
	<section:section label="Attributes">
		<x:choose>
			<x:when select="boolean($tagElem/attribute)">
				<table class="thinTable">
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
						<x:forEach var="attribute" select="$tagElem/attribute">
							<c:set var="rowspan" value="1" />
							<x:if select="boolean($attribute/deferred-method)">
								<c:set var="rowspan" value="${rowspan + 1}" />
							</x:if>
							<x:if select="boolean($attribute/deferred-value)">
								<c:set var="rowspan" value="${rowspan + 1}" />
							</x:if>
							<tr>
								<td rowspan="${rowspan}"><x:out select="$attribute/name" /></td>
								<td rowspan="${rowspan}">
									<x:set var="required" select="string($attribute/required)" />
									<ao:out value="${(required != null && required=='true') ? 'Yes' : 'No'}" />
								</td>
								<td>
									<x:set var="rtexprvalue" select="string($attribute/rtexprvalue)" />
									<ao:out value="${(rtexprvalue != null && rtexprvalue=='true') ? 'Runtime' : 'Static'}" />
									<x:set var="fragment" select="string($attribute/fragment)" />
									<c:if test="${fragment != null && fragment=='true'}">
										<br />Fragment
									</c:if>
								</td>
								<td>
									<x:choose>
										<x:when select="boolean($attribute/type)">
											<x:out select="$attribute/type" />
										</x:when>
										<x:otherwise>
											java.lang.Object
										</x:otherwise>
									</x:choose>
								</td>
								<td rowspan="${rowspan}">
									<x:forEach var="description" select="$attribute/description">
										<x:out select="$description" escapeXml="false" />
									</x:forEach>
								</td>
							</tr>
							<x:if select="boolean($attribute/deferred-method)">
								<tr>
									<td>Deferred-Method</td>
									<td><x:out select="$attribute/deferred-method/method-signature" /></td>
								</tr>
							</x:if>
							<x:if select="boolean($attribute/deferred-value)">
								<tr>
									<td>Deferred-Value</td>
									<td>
										<x:choose>
											<x:when select="boolean($attribute/deferred-value/type)">
												<x:out select="$attribute/deferred-value/type" />
											</x:when>
											<x:otherwise>
												java.lang.Object
											</x:otherwise>
										</x:choose>
									</td>
								</tr>
							</x:if>
						</x:forEach>
					</tbody>
				</table>
			</x:when>
			<x:otherwise>
				<em>No Attributes Defined.</em>
			</x:otherwise>
		</x:choose>
	</section:section>
	<section:section label="Variables">
		<x:choose>
			<x:when select="boolean($tagElem/variable)">
				TODO: Document a variable when first needed.  We don't use any variables at this time.
			</x:when>
			<x:otherwise>
				<em>No Variables Defined.</em>
			</x:otherwise>
		</x:choose>
	</section:section>
	<x:if select="boolean($tagElem/example)">
		<section:section label="Example">
			<x:out select="$tagElem/example" escapeXml="false" />
		</section:section>
	</x:if>
</core:page>
