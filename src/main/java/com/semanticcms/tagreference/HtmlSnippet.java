/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2017  AO Industries, Inc.
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

import com.aoindustries.encoding.Coercion;
import java.io.StringReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class HtmlSnippet {

	/**
	 * Displays all elements with class="semanticcms-tag-reference-summary" of the provided HTML snippet.
	 * If there is no elements with this class, the entire snippet is displayed.
	 * <p>
	 * This parses the HTML snippet into a DOM each invocation.
	 * For higher performance, use another mechanism to compute once and use repeatedly.
	 * This is for convenience, not performance.
	 * </p>
	 */
	public static String getSummary(String htmlSnippet) throws XPathExpressionException {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		XPathExpression expression = xpath.compile("/html//*[@class='semanticcms-tag-reference-summary']");
		if(expression == null) throw new XPathExpressionException("expression is null");
		NodeList summaryNodes = (NodeList)expression.evaluate(
				new InputSource(
					new StringReader(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
						+ "<html>" + htmlSnippet + "</html>"
					)
				),
				XPathConstants.NODESET
			);
		if(summaryNodes != null && summaryNodes.getLength() > 0) {
			StringBuilder summary = new StringBuilder();
			for(int i = 0; i < summaryNodes.getLength(); i++) {
				summary.append(Coercion.toString(summaryNodes.item(i)));
			}
			return summary.toString();
		} else {
			return htmlSnippet;
		}
	}

	private HtmlSnippet() {}
}
