/*
 * semanticcms-google-analytics - Includes the Google Analytics tracking code in SemanticCMS pages.
 * Copyright (C) 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of semanticcms-google-analytics.
 *
 * semanticcms-google-analytics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * semanticcms-google-analytics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with semanticcms-google-analytics.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.semanticcms.tagreference;

import com.aoindustries.xml.XmlUtils;
import com.semanticcms.core.model.PageRef;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
	private final String tldBook;
	private final String tldPath;

	public TagReferenceInitializer(String title, String shortTitle, String tldBook, String tldPath) {
		this.title = title;
		this.shortTitle = shortTitle;
		this.tldBook = tldBook;
		this.tldPath = tldPath;
	}

	@Override
	public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
		try {
			// Books are not necessarily available during initialization
			PageRef tldRef = new PageRef(tldBook, tldPath);
			String tldServletPath = tldRef.getServletPath();
			// Parse TLD
			Document tldDoc;
			{
				InputStream tldIn = servletContext.getResourceAsStream(tldServletPath);
				if(tldIn == null) throw new IOException("TLD not found: " + tldServletPath);
				try {
					tldDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tldIn);
				} finally {
					tldIn.close();
				}
			}
			Element taglibElem = tldDoc.getDocumentElement();
			// Dynamically add servlets
			{
				// /path/taglib.tld/
				String taglibServletUrlPattern = tldServletPath + "/";
				ServletRegistration.Dynamic registration = servletContext.addServlet(
					taglibServletUrlPattern,
					new TaglibServlet(title, shortTitle, tldRef, tldDoc)
				);
				registration.addMapping(taglibServletUrlPattern);
			}
			Iterator<Element> tagIter = XmlUtils.iterableChildElementsByTagName(taglibElem, "tag").iterator();
			if(tagIter.hasNext()) {
				{
					// /path/taglib.tld/tags
					String tagsServletUrlPattern = tldServletPath + "/tags";
					ServletRegistration.Dynamic registration = servletContext.addServlet(
						tagsServletUrlPattern,
						new TagsServlet(tldRef, tldDoc)
					);
					registration.addMapping(tagsServletUrlPattern);
				}
				do {
					// /path/taglib.tld/tag-tagName
					String tagName = XmlUtils.getChildElementByTagName(tagIter.next(), "name").getTextContent().trim();
					String tagServletUrlPattern = tldServletPath + "/tag-" + URLEncoder.encode(tagName, ENCODING);
					ServletRegistration.Dynamic registration = servletContext.addServlet(
						tagServletUrlPattern,
						new TagServlet(tldRef, tldDoc, tagName)
					);
					registration.addMapping(tagServletUrlPattern);
				} while(tagIter.hasNext());
			}
			Iterator<Element> functionIter = XmlUtils.iterableChildElementsByTagName(taglibElem, "function").iterator();
			if(functionIter.hasNext()) {
				{
					// /path/taglib.tld/functions
					String functionsServletUrlPattern = tldServletPath + "/functions";
					ServletRegistration.Dynamic registration = servletContext.addServlet(
						functionsServletUrlPattern,
						new FunctionsServlet(tldRef, tldDoc)
					);
					registration.addMapping(functionsServletUrlPattern);
				}
				do {
					// /path/taglib.tld/function-functionName
					String functionName = XmlUtils.getChildElementByTagName(functionIter.next(), "name").getTextContent().trim();
					String functionServletUrlPattern = tldServletPath + "/function-" + URLEncoder.encode(functionName, ENCODING);
					ServletRegistration.Dynamic registration = servletContext.addServlet(
						functionServletUrlPattern,
						new FunctionServlet(tldRef, tldDoc, functionName)
					);
					registration.addMapping(functionServletUrlPattern);
				} while(functionIter.hasNext());
			}
		} catch(IOException e) {
			throw new ServletException(e);
		} catch(ParserConfigurationException e) {
			throw new ServletException(e);
		} catch(SAXException e) {
			throw new ServletException(e);
		}
	}
}
