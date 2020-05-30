/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2020  AO Industries, Inc.
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

import java.io.IOException;
import java.util.Map;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * <p>
 * Displays function or method parameters including parentheses, possibly linked to javadocs apis.
 * </p>
 * <p>
 * Supports arrays {@code …[]} and generics {@code <…[, …]>}, possibly nested.
 * </p>
 */
public class LinkedSignatureParamsTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	private Map<String,String> apiLinks;
	private String signature;
	private boolean shortName;

	public LinkedSignatureParamsTag() {
		init();
	}

	private void init() {
		apiLinks = null;
		signature = null;
		shortName = false;
	}

	/**
	 * @param apiLinks  The mapping of Java package name (with optional trailing {@code '.'})
	 *                  to javadoc prefixes (including trailing {@code '/'}).
	 */
	public void setApiLinks(Map<String,String> apiLinks) {
		this.apiLinks = apiLinks;
	}

	/**
	 * @param signature  The signature to display the parameters of.
	 */
	public void setSignature(String signature) {
		this.signature = signature.trim();
	}

	/**
	 * @param shortName  When {@code true}, will display class names in short format.
	 *                   Defaults to {@code false}.
	 */
	public void setShortName(boolean shortName) {
		this.shortName = shortName;
	}

	// TODO: Use ao-fluent-html
	public static void writeLinkedSignatureParams(
		PageContext pageContext,
		Map<String,String> apiLinks,
		String signature,
		boolean shortName,
		Appendable out
	) throws IOException, JspTagException {
		if(signature != null) {
			signature = signature.trim();
			int leftParen = signature.indexOf('(');
			if(leftParen == -1) {
				// No left parenthesis found, just run the whole thing through class name linking
				LinkedClassNameTag.writeLinkedClassName(pageContext, apiLinks, signature, shortName, out);
			} else {
				int space = signature.lastIndexOf(' ', leftParen - 1);
				LinkedClassNameTag.writeLinkedClassName(pageContext, apiLinks, signature.substring(leftParen), shortName, out);
			}
		}
	}

	@Override
	public int doStartTag() throws JspTagException {
		try {
			writeLinkedSignatureParams(pageContext, apiLinks, signature, shortName, pageContext.getOut());
			return SKIP_BODY;
		} catch(IOException err) {
			throw new JspTagException(err.getMessage(), err);
		} finally {
			init();
		}
	}
}
