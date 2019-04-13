<%--
semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
Copyright (C) 2016, 2017, 2019  AO Industries, Inc.
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

<%--
The view of one tag at /path/taglib.tld/tag-tagName

Arguments:
	arg.tldRef    The ResourceRef for the TLD file itself
	arg.tag       The parsed tag
	arg.apiLinks  The mapping of java package prefix (including trailing '.') to javadoc prefixes (without any trailing '/')
--%>
<c:set var="tldRef" value="${arg.tldRef}" />
<c:set var="tag" value="${arg.tag}" />
<c:set var="apiLinks" value="${arg.apiLinks}" />
<core:page
	book="${tldRef.bookName}"
	path="${tldRef.path}/tag-${core:encodeUrlParam(tag.name)}"
	title="<${tag.taglib.shortName}:${tag.name}>"
	dateModified="${ao:getLastModified(tldRef.servletPath)}"
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
		<table class="thinTable">
			<tbody>
				<tr>
					<th>Tag Class:</th>
					<td>
						<ao:include page="linked-classname.inc.jsp" arg.apiLinks="${apiLinks}" arg.className="${tag.tagClass}" />
					</td>
				</tr>
				<tr>
					<th>TagExtraInfo Class:</th>
					<td>
						<ao:choose>
							<ao:when test="#{!empty tag.teiClass}">
								<ao:include page="linked-classname.inc.jsp" arg.apiLinks="${apiLinks}" arg.className="${tag.teiClass}" />
							</ao:when>
							<ao:otherwise>
								<em>None</em>
							</ao:otherwise>
						</ao:choose>
					</td>
				</tr>
				<tr>
					<th>Body Content:</th>
					<td><ao:out value="${tag.bodyContent}" /></td>
				</tr>
				<tr>
					<th>Display Name:</th>
					<td>
						<ao:choose>
							<ao:when test="#{!empty tag.displayNames}">
								<c:forEach var="displayName" items="${tag.displayNames}" varStatus="displayNameStatus">
									<ao:out value="${displayName}" type="xhtml" />
									<c:if test="${!displayNameStatus.last}">
										<br />
									</c:if>
								</c:forEach>
							</ao:when>
							<ao:otherwise>
								<em>None</em>
							</ao:otherwise>
						</ao:choose>
					</td>
				</tr>
				<c:if test="${tag.dynamicAttributes}">
					<tr>
						<th>Dynamic Attributes:</th>
						<td><ao:out value="${tag.dynamicAttributes}" /></td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</section:section>
	<section:section label="Attributes">
		<ao:choose>
			<ao:when test="#{!empty tag.attributes}">
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
										<td rowspan="${rowspan}"><ao:out value="${attribute.name}" /></td>
										<td rowspan="${rowspan}"><ao:out value="${attribute.required ? 'Yes' : 'No'}" /></td>
									</c:if>
									<td>
										<ao:out value="${attribute.rtexprvalue ? 'Runtime' : 'Static'}" />
										<c:if test="${attribute.fragment}">
											<br />Fragment
										</c:if>
									</td>
									<td>
										<ao:choose>
											<ao:when test="#{!empty attribute.type}">
												<ao:include
													page="linked-classname.inc.jsp"
													arg.apiLinks="${apiLinks}"
													arg.className="${attribute.type}"
													arg.shortName="true"
												/>
											</ao:when>
											<ao:otherwise>
												<ao:include
													page="linked-classname.inc.jsp"
													arg.apiLinks="${apiLinks}"
													arg.className="java.lang.Object"
													arg.shortName="true"
												/>
											</ao:otherwise>
										</ao:choose>
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
										<td rowspan="${rowspan}"><ao:out value="${attribute.name}" /></td>
										<td rowspan="${rowspan}"><ao:out value="${attribute.required ? 'Yes' : 'No'}" /></td>
									</c:if>
									<td>Deferred-Method</td>
									<td><ao:out value="${deferredMethod.methodSignature}" /></td>
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
										<td rowspan="${rowspan}"><ao:out value="${attribute.name}" /></td>
										<td rowspan="${rowspan}"><ao:out value="${attribute.required ? 'Yes' : 'No'}" /></td>
									</c:if>
									<td>Deferred-Value</td>
									<td>
										<ao:choose>
											<ao:when test="#{deferredValue.type != null}">
												<ao:include
													page="linked-classname.inc.jsp"
													arg.apiLinks="${apiLinks}"
													arg.className="${deferredValue.type}"
													arg.shortName="true"
												/>
											</ao:when>
											<ao:otherwise>
												<ao:include
													page="linked-classname.inc.jsp"
													arg.apiLinks="${apiLinks}"
													arg.className="java.lang.Object"
													arg.shortName="true"
												/>
											</ao:otherwise>
										</ao:choose>
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
			</ao:when>
			<ao:otherwise>
				<em>No Attributes Defined.</em>
			</ao:otherwise>
		</ao:choose>
	</section:section>
	<section:section label="Variables">
		<%-- TODO:
		<ao:choose>
			<ao:when test="#{!empty tag.variables}">
				TODO: Document a variable when first needed.  We don't use any variables at this time.
			</ao:when>
			<ao:otherwise>
		--%>
				<em>No Variables Defined.</em>
		<%-- TODO:
			</ao:otherwise>
		</ao:choose>
		--%>
	</section:section>
</core:page>
