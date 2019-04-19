/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2017, 2019  AO Industries, Inc.
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

import com.aoindustries.util.AoCollections;
import com.aoindustries.xml.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * A *.tld file is parsed entirely on start-up to maximize runtime performance.
 *
 * TODO: This could be its own micro-project.
 */
public class Function {

	private final Taglib taglib;
	private final List<String> descriptions;
	private final List<String> displayNames;
	private final String name;
	private final String functionClass;
	private final String functionSignature;
	private final String example;

	private final String descriptionSummary;

	public Function(
		Taglib taglib,
		Element functionElem,
		Map<String,String> apiLinks
	) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		this.taglib = taglib;

		List<String> newDescriptions = new ArrayList<>();
		for(Element descriptionElem : XmlUtils.iterableChildElementsByTagName(functionElem, "description")) {
			newDescriptions.add(descriptionElem.getTextContent());
		}
		this.descriptions = AoCollections.optimalUnmodifiableList(newDescriptions);

		List<String> newDisplayNames = new ArrayList<>();
		for(Element displayNameElem : XmlUtils.iterableChildElementsByTagName(functionElem, "display-name")) {
			newDisplayNames.add(displayNameElem.getTextContent());
		}
		this.displayNames = AoCollections.optimalUnmodifiableList(newDisplayNames);

		this.name = XmlUtils.getChildTextContent(functionElem, "name");
		this.functionClass = XmlUtils.getChildTextContent(functionElem, "function-class");
		this.functionSignature = XmlUtils.getChildTextContent(functionElem, "function-signature");
		this.example = XmlUtils.getChildTextContent(functionElem, "example");

		try {
			this.descriptionSummary = descriptions.isEmpty() ? null : HtmlSnippet.getSummary(descriptions.get(0));
		} catch(XPathExpressionException e) {
			XPathExpressionException wrapped = new XPathExpressionException(taglib.getTldRef() + "/" + name + "/description: " + e.getMessage());
			wrapped.initCause(e);
			throw wrapped;
		}
	}

	public Taglib getTaglib() {
		return taglib;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public List<String> getDisplayNames() {
		return displayNames;
	}

	public String getName() {
		return name;
	}

	public String getFunctionClass() {
		return functionClass;
	}

	public String getFunctionSignature() {
		return functionSignature;
	}

	public String getExample() {
		return example;
	}

	/**
	 * Gets a summary of the description.
	 * If there is more than once description, only the first is used in generating the summary.
	 * If there are no descriptions, returns {@code null}.
	 *
	 * @see  HtmlSnippet#getSummary(java.lang.String)
	 */
	public String getDescriptionSummary() {
		return descriptionSummary;
	}
}
