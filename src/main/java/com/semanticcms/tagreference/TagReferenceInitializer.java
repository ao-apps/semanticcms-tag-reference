/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2016, 2017, 2019, 2020, 2021, 2022, 2023, 2024, 2025  AO Industries, Inc.
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
 * along with semanticcms-tag-reference.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.semanticcms.tagreference;

import com.aoapps.collections.AoCollections;
import com.aoapps.lang.xml.XmlUtils;
import com.aoapps.net.URIDecoder;
import com.aoapps.net.URIEncoder;
import com.aoapps.tldparser.Dates;
import com.aoapps.tldparser.Function;
import com.aoapps.tldparser.Tag;
import com.aoapps.tldparser.Taglib;
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
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.joda.time.DateTime;
import org.xml.sax.SAXException;

/**
 * Initializes a tag reference during {@linkplain ServletContainerInitializer application start-up}.
 * The following URL patterns are registered:
 * <ol>
 *   <li>/path/taglib.tld   The taglib file itself available for download (handled by default servlet)</li>
 *   <li>/path/taglib.tld/  The documentation for the taglib overall</li>
 *   <li>/path/taglib.tld/tags  The list of tags only</li>
 *   <li>/path/taglib.tld/tag-tagName  The detailed per-tag documentation</li>
 *   <li>/path/taglib.tld/functions  The list of functions only</li>
 *   <li>/path/taglib.tld/function-functionName  The detailed per-function documentation</li>
 * </ol>
 */
public abstract class TagReferenceInitializer implements ServletContainerInitializer {

  /**
   * The css class that marks an element as being a summary.
   */
  public static final String SUMMARY_CLASS = "semanticcms-tag-reference-summary";

  public static final String NOFOLLOW_PREFIX = "nofollow:";

  /**
   * The property name used for Activation API.
   */
  private static final String ACTIVATION_PROPERTY = "javadoc.link.activation";

  /**
   * The property name used for JavaMail API.
   */
  private static final String JAVAMAIL_PROPERTY = "javadoc.link.javamail";

  /**
   * Bundled package maps.  The key is the javadoc link, the value is a mapping from package to module javadoc link,
   * including any necessary module path.
   */
  private static final Map<String, Map<String, String>> packageMapsByJavadocLink = new HashMap<>();

  private static void addPackageMap(String property, String resource) throws IOException {
    String javadocLink = Maven.properties.getProperty(property);
    InputStream resourceIn = TagReferenceInitializer.class.getResourceAsStream("/" + resource);
    if (resourceIn == null) {
      // Try ClassLoader for when modules enabled
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      resourceIn = (classloader != null)
          ? classloader.getResourceAsStream(resource)
          : ClassLoader.getSystemResourceAsStream(resource);
    }
    if (resourceIn == null) {
      throw new IOException("Resource not found: " + resource);
    }
    try (BufferedReader in = new BufferedReader(new InputStreamReader(resourceIn, StandardCharsets.UTF_8))) {
      Map<String, String> packages = new LinkedHashMap<>();
      String moduleLink = javadocLink;
      String line;
      while ((line = in.readLine()) != null) {
        line = line.trim();
        if (!line.isEmpty()) {
          final String modulePrefix = "module:";
          if (line.startsWith(modulePrefix)) {
            moduleLink = javadocLink + line.substring(modulePrefix.length()).trim() + '/';
          } else if (packages.put(line, moduleLink) != null) {
            throw new AssertionError("Duplicate package in " + property + ": " + line);
          }
        }
      }
      if (packageMapsByJavadocLink.put(javadocLink, packages) != null) {
        throw new AssertionError("Duplicate javadocLink from " + property + ": " + javadocLink);
      }
    }
  }

  static {
    try {
      // Note: This list matches ao-oss-parent/pom.xml and ao-javadoc-offline
      addPackageMap("javadoc.link.javase.5",  "com/aoapps/javadoc/offline/javase/5/package-list");
      addPackageMap("javadoc.link.javase.6",  "com/aoapps/javadoc/offline/javase/6/package-list");
      addPackageMap("javadoc.link.javase.7",  "com/aoapps/javadoc/offline/javase/7/package-list");
      addPackageMap("javadoc.link.javase.8",  "com/aoapps/javadoc/offline/javase/8/package-list");
      addPackageMap("javadoc.link.javase.9",  "com/aoapps/javadoc/offline/javase/9/package-list");
      addPackageMap("javadoc.link.javase.10", "com/aoapps/javadoc/offline/javase/10/element-list");
      addPackageMap("javadoc.link.javase.11", "com/aoapps/javadoc/offline/javase/11/element-list");
      addPackageMap("javadoc.link.javase.12", "com/aoapps/javadoc/offline/javase/12/element-list");
      addPackageMap("javadoc.link.javase.13", "com/aoapps/javadoc/offline/javase/13/element-list");
      addPackageMap("javadoc.link.javase.14", "com/aoapps/javadoc/offline/javase/14/element-list");
      addPackageMap("javadoc.link.javase.15", "com/aoapps/javadoc/offline/javase/15/element-list");
      addPackageMap("javadoc.link.javase.16", "com/aoapps/javadoc/offline/javase/16/element-list");
      addPackageMap("javadoc.link.javase.17", "com/aoapps/javadoc/offline/javase/17/element-list");
      addPackageMap("javadoc.link.javase.18", "com/aoapps/javadoc/offline/javase/18/element-list");
      addPackageMap("javadoc.link.javase.19", "com/aoapps/javadoc/offline/javase/19/element-list");
      addPackageMap("javadoc.link.javase.20", "com/aoapps/javadoc/offline/javase/20/element-list");
      addPackageMap("javadoc.link.javase.21", "com/aoapps/javadoc/offline/javase/21/element-list");
      addPackageMap("javadoc.link.javase.22", "com/aoapps/javadoc/offline/javase/22/element-list");

      // Note: This list matches ao-oss-parent/pom.xml and ao-javadoc-offline
      addPackageMap(ACTIVATION_PROPERTY,     "com/aoapps/javadoc/offline/javax.activation/activation/element-list");
      addPackageMap(JAVAMAIL_PROPERTY,       "com/aoapps/javadoc/offline/com.sun.mail/javax.mail/package-list");
      addPackageMap("javadoc.link.javaee.5", "com/aoapps/javadoc/offline/javaee/5/package-list");
      addPackageMap("javadoc.link.javaee.6", "com/aoapps/javadoc/offline/javaee/6/package-list");
      addPackageMap("javadoc.link.javaee.7", "com/aoapps/javadoc/offline/javaee/7/package-list");
      addPackageMap("javadoc.link.jakartaee.8",   "com/aoapps/javadoc/offline/jakartaee/8/package-list");
      addPackageMap("javadoc.link.jakartaee.9",   "com/aoapps/javadoc/offline/jakartaee/9/package-list");
      addPackageMap("javadoc.link.jakartaee.9.1", "com/aoapps/javadoc/offline/jakartaee/9.1/package-list");
      addPackageMap("javadoc.link.jakartaee.10",  "com/aoapps/javadoc/offline/jakartaee/10/element-list");
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  /**
   * Adds the packages for the given API URL.
   */
  private static void addPackages(String javadocLink, Map<String, String> combinedApiLinks, boolean nofollow) {
    Map<String, String> packages = packageMapsByJavadocLink.get(javadocLink);
    if (packages == null) {
      throw new IllegalArgumentException("Bundled package list not found: " + javadocLink);
    }
    for (Map.Entry<String, String> entry : packages.entrySet()) {
      String p = entry.getKey();
      assert !p.endsWith(".");
      if (!combinedApiLinks.containsKey(p)) {
        String moduleLink = entry.getValue();
        combinedApiLinks.put(p, nofollow ? (NOFOLLOW_PREFIX + moduleLink) : moduleLink);
      }
    }
  }

  private final String title;
  private final String shortTitle;
  private final ResourceRef tldRef;
  private final boolean requireLinks;
  private final Map<String, String> apiLinks;

  /**
   * Parses the TLD file.
   *
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
  protected TagReferenceInitializer(
      String title,
      String shortTitle,
      ResourceRef tldRef,
      boolean requireLinks,
      String javadocLinkJavaSE,
      String javadocLinkJavaEE,
      Map<String, String> additionalApiLinks
  ) {
    this.title = title;
    this.shortTitle = shortTitle;
    this.tldRef = tldRef;
    this.requireLinks = requireLinks;
    // Add package matches
    Map<String, String> combinedApiLinks = new LinkedHashMap<>();

    // All additional API links added first, to override any packages in Java SE, JavaMail, or Java EE
    if (additionalApiLinks != null) {
      for (Map.Entry<String, String> entry : additionalApiLinks.entrySet()) {
        String p = entry.getKey();
        // Strip trailing '.' for backward compatibility
        while (p.endsWith(".")) {
          p = p.substring(0, p.length() - 1);
        }
        if (!combinedApiLinks.containsKey(p)) {
          combinedApiLinks.put(p, entry.getValue());
        }
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

  /**
   * Parses the TLD file.
   *
   * @param javadocLinkJavaSE  The Java SE API URL.
   *                           This matches values used in Maven build property <code>${javadoc.link.javase}</code>.
   *
   * @param javadocLinkJavaEE  The Java EE API URL.
   *                           This matches values used in Maven build property <code>${javadoc.link.javaee}</code>.
   *
   * @param additionalApiLinks  Additional API links, must be in even pairs (package, apiLinks), ...
   *                            When there are duplicate packages, the first match wins.)
   */
  protected TagReferenceInitializer(
      String title,
      String shortTitle,
      ResourceRef tldRef,
      boolean requireLinks,
      String javadocLinkJavaSE,
      String javadocLinkJavaEE,
      String ... additionalApiLinks
  ) {
    this(
        title,
        shortTitle,
        tldRef,
        requireLinks,
        javadocLinkJavaSE,
        javadocLinkJavaEE,
        convertToMap(additionalApiLinks)
    );
  }

  private static Map<String, String> convertToMap(String ... additionalApiLinks) {
    if (additionalApiLinks == null) {
      return null;
    }
    int len = additionalApiLinks.length;
    if ((len & 1) != 0) {
      throw new IllegalArgumentException("Uneven number of elements in additionalApiLinks, must be in even pairs (package, apiLinks), ...");
    }
    Map<String, String> map = AoCollections.newLinkedHashMap(len >> 1);
    for (int i = 0; i < len; i += 2) {
      String p = additionalApiLinks[i];
      if (!map.containsKey(p)) {
        map.put(p, additionalApiLinks[i + 1]);
      }
    }
    return map;
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
                XmlUtils.parseXml(tldIn)
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
      if (!taglib.getTags().isEmpty()) {
        {
          // /path/taglib.tld/tags
          String tagsServletUrlPattern = tldServletPath + "/tags";
          ServletRegistration.Dynamic registration = servletContext.addServlet(
              tagsServletUrlPattern,
              new TagsServlet(tldRef, taglib)
          );
          registration.addMapping(tagsServletUrlPattern);
        }
        for (Tag tag : taglib.getTags()) {
          // /path/taglib.tld/tag-tagName
          String tagServletUrlPattern = tldServletPath + "/tag-" + URIDecoder.decodeURI(URIEncoder.encodeURIComponent(tag.getName()));
          ServletRegistration.Dynamic registration = servletContext.addServlet(
              tagServletUrlPattern,
              new TagServlet(tldRef, tag, requireLinks, apiLinks)
          );
          registration.addMapping(tagServletUrlPattern);
        }
      }
      if (!taglib.getFunctions().isEmpty()) {
        {
          // /path/taglib.tld/functions
          String functionsServletUrlPattern = tldServletPath + "/functions";
          ServletRegistration.Dynamic registration = servletContext.addServlet(
              functionsServletUrlPattern,
              new FunctionsServlet(tldRef, taglib, requireLinks, apiLinks)
          );
          registration.addMapping(functionsServletUrlPattern);
        }
        for (Function function : taglib.getFunctions()) {
          // /path/taglib.tld/function-functionName
          String functionServletUrlPattern = tldServletPath + "/function-" + URIDecoder.decodeURI(URIEncoder.encodeURIComponent(function.getName()));
          ServletRegistration.Dynamic registration = servletContext.addServlet(
              functionServletUrlPattern,
              new FunctionServlet(tldRef, function, requireLinks, apiLinks)
          );
          registration.addMapping(functionServletUrlPattern);
        }
      }
    } catch (IOException | ParserConfigurationException | XPathExpressionException | SAXException e) {
      throw new ServletException(e);
    }
  }
}
