/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2020, 2021, 2022, 2023, 2024, 2025, 2026  AO Industries, Inc.
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

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * Displays function or method parameters including parentheses, possibly linked to javadocs apis.
 *
 * <p>Supports arrays {@code …[]} and generics {@code <…[, …]>}, possibly nested.</p>
 */
public class LinkedSignatureParamsTag extends TagSupport {

  private static final long serialVersionUID = 1L;

  private transient boolean requireLinks;
  private transient Map<String, String> apiLinks;
  private transient String signature;
  private transient boolean shortName;

  public LinkedSignatureParamsTag() {
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
   *                      package in {@linkplain LinkedSignatureParamsTag#setApiLinks(java.util.Map) apiLinks}.
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
      boolean requireLinks,
      Map<String, String> apiLinks,
      String signature,
      boolean shortName,
      Appendable out
  ) throws IOException, JspTagException {
    if (signature != null) {
      signature = signature.trim();
      int leftParen = signature.indexOf('(');
      if (leftParen == -1) {
        // No left parenthesis found, just run the whole thing through class name linking
        LinkedClassNameTag.writeLinkedClassName(pageContext, requireLinks, apiLinks, signature, shortName, out);
      } else {
        int space = signature.lastIndexOf(' ', leftParen - 1);
        LinkedClassNameTag.writeLinkedClassName(pageContext, requireLinks, apiLinks, signature.substring(leftParen), shortName, out);
      }
    }
  }

  @Override
  public int doStartTag() throws JspException {
    try {
      writeLinkedSignatureParams(pageContext, requireLinks, apiLinks, signature, shortName, pageContext.getOut());
      return SKIP_BODY;
    } catch (IOException err) {
      throw new JspTagException(err.getMessage(), err);
    } finally {
      init();
    }
  }
}
