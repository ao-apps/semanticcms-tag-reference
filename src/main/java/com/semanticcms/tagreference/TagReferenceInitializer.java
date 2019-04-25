/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2016, 2017, 2019  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of semanticcms-tag-reference.
 *
 * semanticcms-tag-reference is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * semanticcms-tag-reference is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with semanticcms-tag-reference.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.semanticcms.tagreference;

import com.aoindustries.net.Path;
import com.aoindustries.validation.ValidationException;
import com.semanticcms.core.controller.SemanticCMS;
import com.semanticcms.core.model.BookRef;
import com.semanticcms.core.model.ResourceRef;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

/**
 * The following URL patterns are registered:
 *
 * <ol>
 *   <li>/path/taglib.tld   The taglib file itself available for download (handled by default servlet)</li>
 *   <li>/path/taglib.tld/  The documentation for the taglib overall</li>
 *   <li>/path/taglib.tld/tags  The list of tags only</li>
 *   <li>/path/taglib.tld/tag-tagName  The detailed per-tag documentation</li>
 *   <li>/path/taglib.tld/functions  The list of functions only</li>
 *   <li>/path/taglib.tld/function-functionName  The detailed per-function documentation</li>
 * </ol>
 */
abstract public class TagReferenceInitializer implements ServletContainerInitializer {

	/**
	 * The encoding used within path encoding.
	 * We do not expect special characters in tag or function names but we still
	 * encode them because the future is an unknown place (wink).
	 */
	private static final String ENCODING = "UTF-8";

	private final String title;
	private final String shortTitle;
	private final ResourceRef tldRef;
	private final Map<String,String> apiLinks;

	public TagReferenceInitializer(
		String title,
		String shortTitle,
		ResourceRef tldRef,
		String javaApiLink,
		String javaEEApiLink,
		Map<String,String> additionalApiLinks
	) {
		this.title = title;
		this.shortTitle = shortTitle;
		this.tldRef = tldRef;
		// Add package matches
		Map<String,String> combinedApiLinks = new LinkedHashMap<>();
		combinedApiLinks.put("java.io.", javaApiLink);
		combinedApiLinks.put("java.lang.", javaApiLink);
		combinedApiLinks.put("java.util.", javaApiLink);
		// TODO: Java EE as-needed
		combinedApiLinks.putAll(additionalApiLinks);
		apiLinks = Collections.unmodifiableMap(combinedApiLinks);
	}

	private static Path toPath(String path) {
		try {
			return Path.valueOf(path);
		} catch(ValidationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Uses default domain of {@link BookRef#DEFAULT_DOMAIN}.
	 *
	 * @see  #TagReferenceInitializer(java.lang.String, java.lang.String, com.semanticcms.core.model.ResourceRef, java.lang.String, java.lang.String, java.util.Map)
	 *
	 * @deprecated  Please provide domain
	 */
	@Deprecated
	public TagReferenceInitializer(
		String title,
		String shortTitle,
		String tldBook,
		String tldPath,
		String javaApiLink,
		String javaEEApiLink,
		Map<String,String> additionalApiLinks
	) {
		this(
			title,
			shortTitle,
			new ResourceRef(new BookRef(BookRef.DEFAULT_DOMAIN, toPath(tldBook)), toPath(tldPath)),
			javaApiLink,
			javaEEApiLink,
			additionalApiLinks
		);
	}

	@Override
	public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
		try {
			// Books are not necessarily available during initialization
			BookRef tldBookRef = tldRef.getBookRef();
			String tldServletPath = tldBookRef.getPrefix() + tldRef.getPath().toString();
			// Parse TLD
			Taglib taglib;
			{
				try (InputStream tldIn = SemanticCMS.getInstance(servletContext).getBook(tldBookRef).getResources().getResource(tldRef.getPath()).getInputStream()) {
					taglib = new Taglib(tldRef, DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tldIn), apiLinks);
				}
			}
			// Dynamically add servlets
			{
				// /path/taglib.tld/
				String taglibServletUrlPattern = tldServletPath + "/";
				ServletRegistration.Dynamic registration = servletContext.addServlet(
					taglibServletUrlPattern,
					new TaglibServlet(title, shortTitle, tldRef, taglib, apiLinks)
				);
				registration.addMapping(taglibServletUrlPattern);
			}
			if(!taglib.getTags().isEmpty()) {
				{
					// /path/taglib.tld/tags
					String tagsServletUrlPattern = tldServletPath + "/tags";
					ServletRegistration.Dynamic registration = servletContext.addServlet(
						tagsServletUrlPattern,
						new TagsServlet(tldRef, taglib)
					);
					registration.addMapping(tagsServletUrlPattern);
				}
				for(Tag tag : taglib.getTags()) {
					// /path/taglib.tld/tag-tagName
					String tagServletUrlPattern = tldServletPath + "/tag-" + URLEncoder.encode(tag.getName(), ENCODING);
					ServletRegistration.Dynamic registration = servletContext.addServlet(
						tagServletUrlPattern,
						new TagServlet(tldRef, tag, apiLinks)
					);
					registration.addMapping(tagServletUrlPattern);
				}
			}
			if(!taglib.getFunctions().isEmpty()) {
				{
					// /path/taglib.tld/functions
					String functionsServletUrlPattern = tldServletPath + "/functions";
					ServletRegistration.Dynamic registration = servletContext.addServlet(
						functionsServletUrlPattern,
						new FunctionsServlet(tldRef, taglib, apiLinks)
					);
					registration.addMapping(functionsServletUrlPattern);
				}
				for(Function function : taglib.getFunctions()) {
					// /path/taglib.tld/function-functionName
					String functionServletUrlPattern = tldServletPath + "/function-" + URLEncoder.encode(function.getName(), ENCODING);
					ServletRegistration.Dynamic registration = servletContext.addServlet(
						functionServletUrlPattern,
						new FunctionServlet(tldRef, function, apiLinks)
					);
					registration.addMapping(functionServletUrlPattern);
				}
			}
		} catch(IOException | ParserConfigurationException | XPathExpressionException | SAXException e) {
			throw new ServletException(e);
		}
	}
}
