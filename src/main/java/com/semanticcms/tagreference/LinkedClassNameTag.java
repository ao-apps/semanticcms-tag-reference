/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2020, 2021  AO Industries, Inc.
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

import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoapps.encoding.TextInXhtmlEncoder.encodeTextInXhtml;
import com.aoapps.net.EmptyURIParameters;
import com.aoapps.servlet.http.HttpServletUtil;
import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * <p>
 * Displays a class name, possibly linked to javadocs apis.
 * </p>
 * <p>
 * Supports arrays {@code …[]} and generics {@code <…[, …]>}, possibly nested.
 * </p>
 */
public class LinkedClassNameTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	private boolean requireLinks;
	private Map<String, String> apiLinks;
	private String className;
	private boolean shortName;

	public LinkedClassNameTag() {
		init();
	}

	private void init() {
		requireLinks = false;
		apiLinks = null;
		className = null;
		shortName = false;
	}

	/**
	 * @param requireLinks  When {@code true}, will fail when a class does not map to a
	 *                      package in {@linkplain #setApiLinks(java.util.Map) apiLinks}.
	 *                      Defaults to {@code false}.
	 */
	public void setRequireLinks(boolean requireLinks) {
		this.requireLinks = requireLinks;
	}

	/**
	 * @param apiLinks  The mapping of Java package name (with optional trailing {@code '.'})
	 *                  to javadoc prefixes (including trailing {@code '/'}).
	 */
	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter") // Passed unmodifiable
	public void setApiLinks(Map<String, String> apiLinks) {
		this.apiLinks = apiLinks;
	}

	/**
	 * @param className  The class name to display.
	 */
	public void setClassName(String className) {
		this.className = className.trim();
	}

	/**
	 * @param shortName  When {@code true}, will display the class name in short format.
	 *                   Defaults to {@code false}.
	 */
	public void setShortName(boolean shortName) {
		this.shortName = shortName;
	}

	// TODO: Use ao-fluent-html
	public static void writeLinkedClassName(
		PageContext pageContext,
		boolean requireLinks,
		Map<String, String> apiLinks,
		String className,
		boolean shortName,
		Appendable out
	) throws IOException, JspTagException {
		if(className != null) {
			className = className.trim();
			final String TOKENS = "()[]<>&,";
			StringTokenizer tokenizer = new StringTokenizer(className, TOKENS, true);
			while(tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				char ch;
				if(token.length() == 1 && TOKENS.indexOf(ch = token.charAt(0)) != -1) {
					if(ch == ',') {
						encodeTextInXhtml(", ", out);
					} else if(ch == '&') {
						encodeTextInXhtml(" & ", out);
					} else {
						encodeTextInXhtml(ch, out);
					}
				} else {
					token = token.trim();
					if(
						// Primitives
						   "boolean".equals(token)
						|| "byte"   .equals(token)
						|| "char"   .equals(token)
						|| "short"  .equals(token)
						|| "int"    .equals(token)
						|| "long"   .equals(token)
						|| "float"  .equals(token)
						|| "double" .equals(token)
						// Other keywords
						|| "void"   .equals(token) // Can be seen in deferred-method signature
						|| "?"      .equals(token) // Unbounded generics <?>
					) {
						encodeTextInXhtml(token, out);
					} else {
						final String EXTENDS = " extends ";
						int pos = token.indexOf(EXTENDS);
						if(pos != -1) {
							encodeTextInXhtml(token.substring(0, pos).trim(), out);
							out.append(EXTENDS);
							token = token.substring(pos + EXTENDS.length());
						} else {
							final String SUPER = " super ";
							pos = token.indexOf(SUPER);
							if(pos != -1) {
								encodeTextInXhtml(token.substring(0, pos).trim(), out);
								out.append(SUPER);
								token = token.substring(pos + SUPER.length());
							}
						}
						if(!token.isEmpty()) {
							String packageName;
							String nameOnly;
							{
								int lastDot = token.lastIndexOf('.');
								if(lastDot == -1) {
									packageName = "";
									nameOnly = token;
								} else {
									packageName = token.substring(0, lastDot);
									nameOnly = token.substring(lastDot + 1);
								}
							}
							String javadocLink = apiLinks.get(packageName);
							if(javadocLink != null) {
								// Link to javadocs
								out.append("<a ");
								if(javadocLink.startsWith(TagReferenceInitializer.NOFOLLOW_PREFIX)) {
									out.append("rel=\"nofollow\" ");
									javadocLink = javadocLink.substring(TagReferenceInitializer.NOFOLLOW_PREFIX.length());
								}
								out.append("href=\"");
								encodeTextInXhtmlAttribute(
									HttpServletUtil.buildURL(
										pageContext,
										javadocLink + token.replace('.', '/').replace('$', '.') + ".html",
										EmptyURIParameters.getInstance(),
										false,
										true // Javadocs do not take parameters, even within the same site
									),
									out
								);
								out.append("\">");
								encodeTextInXhtml(shortName ? nameOnly : token, out);
								out.append("</a>");
							} else if(requireLinks) {
								throw new JspTagException("Package not found in apiLinks: " + token);
							} else {
								// No javadoc link found, show full class name
								encodeTextInXhtml(token, out);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			writeLinkedClassName(pageContext, requireLinks, apiLinks, className, shortName, pageContext.getOut());
			return SKIP_BODY;
		} catch(IOException err) {
			throw new JspTagException(err.getMessage(), err);
		} finally {
			init();
		}
	}
}
