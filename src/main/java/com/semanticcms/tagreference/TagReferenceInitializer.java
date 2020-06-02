/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.tld.parser.Dates;
import com.aoindustries.tld.parser.Function;
import com.aoindustries.tld.parser.Tag;
import com.aoindustries.tld.parser.Taglib;
import com.semanticcms.core.controller.SemanticCMS;
import com.semanticcms.core.model.BookRef;
import com.semanticcms.core.model.ResourceRef;
import com.semanticcms.core.resources.Resource;
import com.semanticcms.core.resources.ResourceConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

	/**
	 * The property name used for JavaMail API.
	 */
	private static final String JAVAMAIL_PROPERTY = "javadoc.link.javamail";

	/**
	 * Bundled package lists
	 */
	private static final Map<String,Set<String>> packageListsByJavadocLink = new HashMap<>();

	private static void addPackageList(String property, String resource) throws IOException {
		String javadocLink = Maven.properties.getProperty(property);
		try (
			BufferedReader in = new BufferedReader(
				new InputStreamReader(
					TagReferenceInitializer.class.getResourceAsStream(resource),
					StandardCharsets.UTF_8
				)
			)
		) {
			Set<String> packages = new LinkedHashSet<>();
			String line;
			while((line = in.readLine()) != null) {
				line = line.trim();
				if(
					!line.isEmpty()
					&& !line.startsWith("module:")
				) {
					if(!packages.add(line)) throw new AssertionError("Duplicate package in " + property + ": " + line);
				}
			}
			if(packageListsByJavadocLink.put(javadocLink, packages) != null) throw new AssertionError("Duplicate javadocLink from " + property + ": " + javadocLink);
		}
	}

	static {
		try {
			// Note: This list matches ao-oss-parent/pom.xml and ao-javadoc-offline
			addPackageList("javadoc.link.javase.5",  "/com/aoindustries/javadoc/offline/javase/5/package-list");
			addPackageList("javadoc.link.javase.6",  "/com/aoindustries/javadoc/offline/javase/6/package-list");
			addPackageList("javadoc.link.javase.7",  "/com/aoindustries/javadoc/offline/javase/7/package-list");
			addPackageList("javadoc.link.javase.8",  "/com/aoindustries/javadoc/offline/javase/8/package-list");
			addPackageList("javadoc.link.javase.9",  "/com/aoindustries/javadoc/offline/javase/9/package-list");
			addPackageList("javadoc.link.javase.10", "/com/aoindustries/javadoc/offline/javase/10/element-list");
			addPackageList("javadoc.link.javase.11", "/com/aoindustries/javadoc/offline/javase/11/element-list");
			addPackageList("javadoc.link.javase.12", "/com/aoindustries/javadoc/offline/javase/12/element-list");
			addPackageList("javadoc.link.javase.13", "/com/aoindustries/javadoc/offline/javase/13/element-list");
			addPackageList("javadoc.link.javase.14", "/com/aoindustries/javadoc/offline/javase/14/element-list");
			addPackageList("javadoc.link.javase.15", "/com/aoindustries/javadoc/offline/javase/15/element-list");

			// Note: This list matches ao-oss-parent/pom.xml and ao-javadoc-offline
			addPackageList("javadoc.link.javamail", "/com/aoindustries/javadoc/offline/com.sun.mail/javax.mail/package-list");
			addPackageList("javadoc.link.javaee.5", "/com/aoindustries/javadoc/offline/javaee/5/package-list");
			addPackageList("javadoc.link.javaee.6", "/com/aoindustries/javadoc/offline/javaee/6/package-list");
			addPackageList("javadoc.link.javaee.7", "/com/aoindustries/javadoc/offline/javaee/7/package-list");
			addPackageList("javadoc.link.javaee.8", "/com/aoindustries/javadoc/offline/javaee/8/package-list");
		} catch(IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * Adds the packages for the given API URL.
	 */
	private static void addPackages(String javadocLink, Map<String,String> combinedApiLinks) {
		Set<String> packages = packageListsByJavadocLink.get(javadocLink);
		if(packages == null) throw new IllegalArgumentException("Bundled package list not found: " + javadocLink);
		for(String p : packages) {
			assert !p.endsWith(".");
			combinedApiLinks.put(p, javadocLink);
		}
	}

	private final String title;
	private final String shortTitle;
	private final ResourceRef tldRef;
	private final boolean requireLinks;
	private final Map<String,String> apiLinks;

	@SuppressWarnings("unchecked")
	public TagReferenceInitializer(
		String title,
		String shortTitle,
		ResourceRef tldRef,
		boolean requireLinks,
		String javadocLinkJavaSE, // TODO: Pass javase.release instead
		String javadocLinkJavaEE, // TODO: Pass javaee.version instead
		Map<String,String> ... additionalApiLinks
	) {
		this.title = title;
		this.shortTitle = shortTitle;
		this.tldRef = tldRef;
		this.requireLinks = requireLinks;
		// Add package matches
		Map<String,String> combinedApiLinks = new LinkedHashMap<>();

		// Java EE packages added before Java SE, so when a package exists in both it will use Java SE (for example javax.annotation)
		addPackages(javadocLinkJavaEE, combinedApiLinks);

		// JavaMail added after Java EE, so when a package exists in both it will use JavaMail
		addPackages(Maven.properties.getProperty(JAVAMAIL_PROPERTY), combinedApiLinks);

		// Java SE packages added last, to override any packages found in JavaMail or Java EE
		addPackages(javadocLinkJavaSE, combinedApiLinks);

		// All additional API links added last, to override any packages in Java SE, JavaMail, or Java EE
		if(additionalApiLinks != null) {
			for(Map<String,String> map : additionalApiLinks) {
				for(Map.Entry<String,String> entry : map.entrySet()) {
					String p = entry.getKey();
					// Strip trailing '.' for backward compatibility
					while(p.endsWith(".")) p = p.substring(0, p.length() - 1);
					combinedApiLinks.put(p, entry.getValue());
				}
			}
		}

		apiLinks = Collections.unmodifiableMap(combinedApiLinks);
	}

	@SuppressWarnings("unchecked")
	public TagReferenceInitializer(
		String title,
		String shortTitle,
		ResourceRef tldRef,
		boolean requireLinks,
		String javadocLinkJavaSE, // TODO: Pass javase.release instead
		String javadocLinkJavaEE, // TODO: Pass javaee.version instead
		Map<String,String> additionalApiLinks
	) {
		this(
			title,
			shortTitle,
			tldRef,
			requireLinks,
			javadocLinkJavaSE,
			javadocLinkJavaEE,
			additionalApiLinks == null ? null : (Map<String,String>[])new Map<?,?>[] {additionalApiLinks}
		);
	}

	/**
	 * The *.tld file is parsed entirely on start-up to maximize runtime performance.
	 */
	@Override
	public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
		try {
			// Books are not necessarily available during initialization
			BookRef tldBookRef = tldRef.getBookRef();
			String tldServletPath = tldBookRef.getPrefix() + tldRef.getPath().toString();
			// Parse TLD
			Taglib taglib;
			{
				Resource tldResource = SemanticCMS.getInstance(servletContext).getBook(tldBookRef).getResources().getResource(tldRef.getPath());
				try (ResourceConnection conn = tldResource.open()) {
					long tldLastModified = conn.getLastModified();
					try (InputStream tldIn = conn.getInputStream()) {
						taglib = new Taglib(
							SUMMARY_CLASS,
							tldRef.toString(),
							Dates.valueOf(
								null,
								null,
								tldLastModified == 0 ? null : new DateTime(tldLastModified),
								null
							),
							DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tldIn)
						);
					}
				}
			}
			// Dynamically add servlets
			{
				// /path/taglib.tld/
				String taglibServletUrlPattern = tldServletPath + "/";
				ServletRegistration.Dynamic registration = servletContext.addServlet(
					taglibServletUrlPattern,
					new TaglibServlet(title, shortTitle, tldRef, taglib, requireLinks, apiLinks)
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
						new TagServlet(tldRef, tag, requireLinks, apiLinks)
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
						new FunctionsServlet(tldRef, taglib, requireLinks, apiLinks)
					);
					registration.addMapping(functionsServletUrlPattern);
				}
				for(Function function : taglib.getFunctions()) {
					// /path/taglib.tld/function-functionName
					String functionServletUrlPattern = tldServletPath + "/function-" + URIDecoder.decodeURI(URIEncoder.encodeURIComponent(function.getName()));
					ServletRegistration.Dynamic registration = servletContext.addServlet(
						functionServletUrlPattern,
						new FunctionServlet(tldRef, function, requireLinks, apiLinks)
					);
					registration.addMapping(functionServletUrlPattern);
				}
			}
		} catch(IOException | ParserConfigurationException | XPathExpressionException | SAXException e) {
			throw new ServletException(e);
		}
	}
}
