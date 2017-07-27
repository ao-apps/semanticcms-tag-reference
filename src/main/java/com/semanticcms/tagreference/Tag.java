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

import com.aoindustries.lang.NotImplementedException;
import com.aoindustries.util.AoCollections;
import com.aoindustries.xml.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
public class Tag {

	private final Taglib taglib;
	private final List<String> descriptions;
	private final List<String> displayNames;
	private final String name;
	private final String tagClass;
	private final String teiClass;
	private final String bodyContent;
	private final Map<String,Attribute> attribute;
	private final List<Attribute> attributes;
	private final boolean dynamicAttributes;
	// TODO: Variables
	private final String example;

	private final String descriptionSummary;

	public Tag(
		Taglib taglib,
		Element tagElem
	) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		this.taglib = taglib;

		List<String> newDescriptions = new ArrayList<String>();
		for(Element descriptionElem : XmlUtils.iterableChildElementsByTagName(tagElem, "description")) {
			newDescriptions.add(descriptionElem.getTextContent());
		}
		this.descriptions = AoCollections.optimalUnmodifiableList(newDescriptions);

		List<String> newDisplayNames = new ArrayList<String>();
		for(Element displayNameElem : XmlUtils.iterableChildElementsByTagName(tagElem, "display-name")) {
			newDisplayNames.add(displayNameElem.getTextContent());
		}
		this.displayNames = AoCollections.optimalUnmodifiableList(newDisplayNames);

		this.name = XmlUtils.getChildTextContent(tagElem, "name");
		this.tagClass = XmlUtils.getChildTextContent(tagElem, "tag-class");
		this.teiClass = XmlUtils.getChildTextContent(tagElem, "tei-class");
		this.bodyContent = XmlUtils.getChildTextContent(tagElem, "body-content");

		Map<String,Attribute> newAttributes = new LinkedHashMap<String,Attribute>();
		for(Element attributeElem : XmlUtils.iterableChildElementsByTagName(tagElem, "attribute")) {
			Attribute newAttribute = new Attribute(this, attributeElem);
			String attributeName = newAttribute.getName();
			if(newAttributes.put(attributeName, newAttribute) != null) throw new IllegalArgumentException("Duplicate attribute name: " + attributeName);
		}
		this.attribute = AoCollections.optimalUnmodifiableMap(newAttributes);
		this.attributes = AoCollections.optimalUnmodifiableList(new ArrayList<Attribute>(newAttributes.values()));

		this.dynamicAttributes = Boolean.parseBoolean(XmlUtils.getChildTextContent(tagElem, "dynamic-attributes"));
		if(XmlUtils.iterableChildElementsByTagName(tagElem, "variable").iterator().hasNext()) {
			throw new NotImplementedException("TODO: Document variables when first needed.  We don't use any variables at this time.");
		}
		this.example = XmlUtils.getChildTextContent(tagElem, "example");

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

	public String getTagClass() {
		return tagClass;
	}

	public String getTeiClass() {
		return teiClass;
	}

	public String getBodyContent() {
		return bodyContent;
	}

	public Map<String,Attribute> getAttribute() {
		return attribute;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public boolean getDynamicAttributes() {
		return dynamicAttributes;
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
