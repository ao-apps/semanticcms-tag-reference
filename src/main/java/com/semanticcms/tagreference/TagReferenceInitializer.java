/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoindustries.collections.AoCollections;
import com.aoindustries.net.URIDecoder;
import com.aoindustries.net.URIEncoder;
import com.aoindustries.servlet.ServletContextCache;
import com.aoindustries.tld.parser.Dates;
import com.aoindustries.tld.parser.Function;
import com.aoindustries.tld.parser.Tag;
import com.aoindustries.tld.parser.Taglib;
import com.semanticcms.core.model.PageRef;
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
	public static final String SUMMARY_CLASS = "semanticcms-tag-reference-summary";

	public static final String NOFOLLOW_PREFIX = "nofollow:";

	/**
	 * The property name used for JavaMail API.
	 */
	private static final String JAVAMAIL_PROPERTY = "javadoc.link.javamail";

	/**
	 * Bundled package lists
	 */
	private static final Map<String, Set<String>> packageListsByJavadocLink = new HashMap<>();

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
			addPackageList("javadoc.link.javase.16", "/com/aoindustries/javadoc/offline/javase/16/element-list");

			// Note: This list matches ao-oss-parent/pom.xml and ao-javadoc-offline
			addPackageList(JAVAMAIL_PROPERTY, "/com/aoindustries/javadoc/offline/com.sun.mail/javax.mail/package-list");
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
	private static void addPackages(String javadocLink, Map<String, String> combinedApiLinks, boolean nofollow) {
		Set<String> packages = packageListsByJavadocLink.get(javadocLink);
		if(packages == null) throw new IllegalArgumentException("Bundled package list not found: " + javadocLink);
		for(String p : packages) {
			assert !p.endsWith(".");
			if(!combinedApiLinks.containsKey(p)) combinedApiLinks.put(p, nofollow ? (NOFOLLOW_PREFIX + javadocLink) : javadocLink);
		}
	}

	private final String title;
	private final String shortTitle;
	private final String tldBook;
	private final String tldPath;
	private final boolean requireLinks;
	private final Map<String, String> apiLinks;

	/**
	 * @param javadocLinkJavaSE  The Java SE API URL.
	 *                           This matches values used in Maven build property <code>${javadoc.link.javase}</code>.
	 *
	 * @param javadocLinkJavaEE  The Java EE API URL.
	 *                           This matches values used in Maven build property <code>${javadoc.link.javaee}</code>.
	 *
	 * @param additionalApiLinks  Additional API links.
	 *                            When there are duplicate packages, the first match wins.
	 *                            The API links may be prefixed with {@link #NOFOLLOW_PREFIX} to have <code>rel="nofollow"</code> in the generated links.
	 */
	public TagReferenceInitializer(
		String title,
		String shortTitle,
		String tldBook,
		String tldPath,
		boolean requireLinks,
		String javadocLinkJavaSE,
		String javadocLinkJavaEE,
		Map<String, String> additionalApiLinks
	) {
		this.title = title;
		this.shortTitle = shortTitle;
		this.tldBook = tldBook;
		this.tldPath = tldPath;
		this.requireLinks = requireLinks;
		// Add package matches
		Map<String, String> combinedApiLinks = new LinkedHashMap<>();

		// All additional API links added first, to override any packages in Java SE, JavaMail, or Java EE
		if(additionalApiLinks != null) {
			for(Map.Entry<String, String> entry : additionalApiLinks.entrySet()) {
				String p = entry.getKey();
				// Strip trailing '.' for backward compatibility
				while(p.endsWith(".")) {
					p = p.substring(0, p.length() - 1);
				}
				if(!combinedApiLinks.containsKey(p)) combinedApiLinks.put(p, entry.getValue());
			}
		}

		// Java SE packages added next, to override any packages found in JavaMail or Java EE
		addPackages(javadocLinkJavaSE, combinedApiLinks, true);

		// JavaMail added before Java EE, so when a package exists in both it will use JavaMail
		addPackages(Maven.properties.getProperty(JAVAMAIL_PROPERTY), combinedApiLinks, true);

		// Java EE packages added after Java SE, so when a package exists in both it will use Java SE (for example javax.annotation)
		addPackages(javadocLinkJavaEE, combinedApiLinks, true);

		apiLinks = Collections.unmodifiableMap(combinedApiLinks);
	}

	private static Map<String, String> convertToMap(String ... additionalApiLinks) {
		if(additionalApiLinks == null) return null;
		int len = additionalApiLinks.length;
		if((len & 1) != 0) throw new IllegalArgumentException("Uneven number of elements in additionalApiLinks, must be in even pairs (package, apiLinks), ...");
		Map<String, String> map = AoCollections.newLinkedHashMap(len >> 1);
		for(int i = 0; i < len; i += 2) {
			String p = additionalApiLinks[i];
			if(!map.containsKey(p)) map.put(p, additionalApiLinks[i + 1]);
		}
		return map;
	}

	/**
	 * @param javadocLinkJavaSE  The Java SE API URL.
	 *                           This matches values used in Maven build property <code>${javadoc.link.javase}</code>.
	 *
	 * @param javadocLinkJavaEE  The Java EE API URL.
	 *                           This matches values used in Maven build property <code>${javadoc.link.javaee}</code>.
	 *
	 * @param additionalApiLinks  Additional API links, must be in even pairs (package, apiLinks), ...
	 *                            When there are duplicate packages, the first match wins.)
	 */
	public TagReferenceInitializer(
		String title,
		String shortTitle,
		String tldBook,
		String tldPath,
		boolean requireLinks,
		String javadocLinkJavaSE,
		String javadocLinkJavaEE,
		String ... additionalApiLinks
	) {
		this(
			title,
			shortTitle,
			tldBook,
			tldPath,
			requireLinks,
			javadocLinkJavaSE,
			javadocLinkJavaEE,
			convertToMap(additionalApiLinks)
		);
	}

	/**
	 * Calls {@link #TagReferenceInitializer(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, java.util.Map)},
	 * with {@code requireLinks = false} for backward compatibility.
	 *
	 * @deprecated  Please provide {@code requireLinks} to either {@link #TagReferenceInitializer(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, java.util.Map)}
	 *              or {@link #TagReferenceInitializer(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, java.lang.String...)}
	 */
	@Deprecated
	public TagReferenceInitializer(
		String title,
		String shortTitle,
		String tldBook,
		String tldPath,
		String javadocLinkJavaSE,
		String javadocLinkJavaEE,
		Map<String, String> additionalApiLinks
	) {
		this(
			title,
			shortTitle,
			tldBook,
			tldPath,
			false,
			javadocLinkJavaSE,
			javadocLinkJavaEE,
			additionalApiLinks
		);
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
				try (InputStream tldIn = servletContext.getResourceAsStream(tldServletPath)) {
					if(tldIn == null) throw new IOException("TLD not found: " + tldServletPath);
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
						DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tldIn)
					);
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
