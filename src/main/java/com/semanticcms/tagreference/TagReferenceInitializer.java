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

import com.aoindustries.net.URIDecoder;
import com.aoindustries.net.URIEncoder;
import com.aoindustries.servlet.ServletContextCache;
import com.aoindustries.tld.parser.Dates;
import com.aoindustries.tld.parser.Function;
import com.aoindustries.tld.parser.Tag;
import com.aoindustries.tld.parser.Taglib;
import com.semanticcms.core.model.PageRef;
import java.io.IOException;
import java.io.InputStream;
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
import org.joda.time.DateTime;
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
	 * The css class that marks an element as being a summary.
	 */
	private static final String SUMMARY_CLASS = "semanticcms-tag-reference-summary";

	private final String title;
	private final String shortTitle;
	private final String tldBook;
	private final String tldPath;
	private final Map<String,String> apiLinks;

	public TagReferenceInitializer(
		String title,
		String shortTitle,
		String tldBook,
		String tldPath,
		String javaApiLink,
		String javaEEApiLink,
		Map<String,String> additionalApiLinks
	) {
		this.title = title;
		this.shortTitle = shortTitle;
		this.tldBook = tldBook;
		this.tldPath = tldPath;
		// Add package matches
		Map<String,String> combinedApiLinks = new LinkedHashMap<>();
		combinedApiLinks.put("java.io.", javaApiLink);
		combinedApiLinks.put("java.lang.", javaApiLink);
		combinedApiLinks.put("java.util.", javaApiLink);
		// TODO: Java EE as-needed
		combinedApiLinks.putAll(additionalApiLinks);
		apiLinks = Collections.unmodifiableMap(combinedApiLinks);
	}

	/**
	 * The *.tld file is parsed entirely on start-up to maximize runtime performance.
	 */
	@Override
	public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
		try {
			// Books are not necessarily available during initialization
			PageRef tldRef = new PageRef(tldBook, tldPath);
			String tldServletPath = tldRef.getServletPath();
			// Parse TLD
			Taglib taglib;
			{
				InputStream tldIn = servletContext.getResourceAsStream(tldServletPath);
				if(tldIn == null) throw new IOException("TLD not found: " + tldServletPath);
				try {
					long tldLastModified = ServletContextCache.getLastModified(servletContext, tldServletPath);
					taglib = new Taglib(
						SUMMARY_CLASS,
						tldRef.toString(),
						Dates.valueOf(
							null,
							null,
							tldLastModified == 0 ? null : new DateTime(tldLastModified),
							null
						),
						DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tldIn),
						apiLinks
					);
				} finally {
					tldIn.close();
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
					String tagServletUrlPattern = tldServletPath + "/tag-" + URIDecoder.decodeURI(URIEncoder.encodeURIComponent(tag.getName()));
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
					String functionServletUrlPattern = tldServletPath + "/function-" + URIDecoder.decodeURI(URIEncoder.encodeURIComponent(function.getName()));
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
