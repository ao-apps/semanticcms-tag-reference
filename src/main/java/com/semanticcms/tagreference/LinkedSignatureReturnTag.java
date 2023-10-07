/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2020, 2021, 2022, 2023  AO Industries, Inc.
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import javax.servlet.jsp.JspException;
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

  private transient boolean requireLinks;
  private transient Map<String, String> apiLinks;
  private transient String signature;
  private transient boolean shortName;

  public LinkedSignatureReturnTag() {
    init();
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    init();
  }

  private void init() {
    requireLinks = false;
    apiLinks = null;
    signature = null;
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
      boolean requireLinks,
      Map<String, String> apiLinks,
      String signature,
      boolean shortName,
      Appendable out
  ) throws IOException, JspTagException {
    if (signature != null) {
      signature = signature.trim();
      int leftParen = signature.indexOf('(');
      if (leftParen != -1) {
        int space = signature.lastIndexOf(' ', leftParen - 1);
        if (space != -1) {
          // Space found, assume everything left of space is the return type
          LinkedClassNameTag.writeLinkedClassName(pageContext, requireLinks, apiLinks, signature.substring(0, space), shortName, out);
        }
      }
    }
  }

  @Override
  public int doStartTag() throws JspException {
    try {
      writeLinkedSignatureReturn(pageContext, requireLinks, apiLinks, signature, shortName, pageContext.getOut());
      return SKIP_BODY;
    } catch (IOException err) {
      throw new JspTagException(err.getMessage(), err);
    } finally {
      init();
    }
  }
}
