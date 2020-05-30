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
 * Displays a function or method signature return type, possibly linked to javadocs apis.
 * </p>
 * <p>
 * Supports arrays {@code …[]} and generics {@code <…[, …]>}, possibly nested.
 * </p>
 */
public class LinkedSignatureReturnTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	private Map<String,String> apiLinks;
	private String signature;
	private boolean shortName;

	public LinkedSignatureReturnTag() {
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
	 * @param signature  The signature to display the return type of.
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
	public static void writeLinkedSignatureReturn(
		PageContext pageContext,
		Map<String,String> apiLinks,
		String signature,
		boolean shortName,
		Appendable out
	) throws IOException, JspTagException {
		if(signature != null) {
			signature = signature.trim();
			int leftParen = signature.indexOf('(');
			if(leftParen != -1) {
				int space = signature.lastIndexOf(' ', leftParen - 1);
				if(space != -1) {
					// Space found, assume everything left of space is the return type
					LinkedClassNameTag.writeLinkedClassName(pageContext, apiLinks, signature.substring(0, space), shortName, out);
				}
			}
		}
	}

	@Override
	public int doStartTag() throws JspTagException {
		try {
			writeLinkedSignatureReturn(pageContext, apiLinks, signature, shortName, pageContext.getOut());
			return SKIP_BODY;
		} catch(IOException err) {
			throw new JspTagException(err.getMessage(), err);
		} finally {
			init();
		}
	}
}
