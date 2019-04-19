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
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;

/**
 * A *.tld file is parsed entirely on start-up to maximize runtime performance.
 *
 * TODO: This could be its own micro-project: ao-tld-parser
 */
public class Attribute {

	private final Tag tag;
	private final List<String> descriptions;
	private final String name;
	private final boolean required;
	private final boolean rtexprvalue;
	private final boolean fragment;
	private final String type;
	private final DeferredMethod deferredMethod;
	private final DeferredValue deferredValue;

	private final String descriptionSummary;

	public Attribute(
		Tag tag,
		Element attributeElem
	) throws XPathExpressionException {
		this.tag = tag;

		List<String> newDescriptions = new ArrayList<>();
		for(Element descriptionElem : XmlUtils.iterableChildElementsByTagName(attributeElem, "description")) {
			newDescriptions.add(descriptionElem.getTextContent());
		}
		this.descriptions = AoCollections.optimalUnmodifiableList(newDescriptions);

		this.name = XmlUtils.getChildTextContent(attributeElem, "name");
		this.required = Boolean.parseBoolean(XmlUtils.getChildTextContent(attributeElem, "required"));
		this.rtexprvalue = Boolean.parseBoolean(XmlUtils.getChildTextContent(attributeElem, "rtexprvalue"));
		this.fragment = Boolean.parseBoolean(XmlUtils.getChildTextContent(attributeElem, "fragment"));
		this.type = XmlUtils.getChildTextContent(attributeElem, "type");

		Element deferredMethodElem = XmlUtils.getChildElementByTagName(attributeElem, "deferred-method");
		this.deferredMethod = deferredMethodElem == null ? null : new DeferredMethod(this, deferredMethodElem);

		Element deferredValueElem = XmlUtils.getChildElementByTagName(attributeElem, "deferred-value");
		this.deferredValue = deferredValueElem == null ? null : new DeferredValue(this, deferredValueElem);

		try {
			this.descriptionSummary = descriptions.isEmpty() ? null : HtmlSnippet.getSummary(descriptions.get(0));
		} catch(XPathExpressionException e) {
			XPathExpressionException wrapped = new XPathExpressionException(tag.getTaglib().getTldRef() + "/" + tag.getName() + "/" + name + "/description: " + e.getMessage());
			wrapped.initCause(e);
			throw wrapped;
		}
	}

	public Tag getTag() {
		return tag;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public String getName() {
		return name;
	}

	public boolean getRequired() {
		return required;
	}

	public boolean getRtexprvalue() {
		return rtexprvalue;
	}

	public boolean getFragment() {
		return fragment;
	}

	public String getType() {
		return type;
	}

	public DeferredMethod getDeferredMethod() {
		return deferredMethod;
	}

	public DeferredValue getDeferredValue() {
		return deferredValue;
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
