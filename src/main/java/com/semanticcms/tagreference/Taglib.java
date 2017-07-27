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

import com.aoindustries.util.AoCollections;
import com.aoindustries.xml.XmlUtils;
import com.semanticcms.core.model.PageRef;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * A *.tld file is parsed entirely on start-up to maximize runtime performance.
 *
 * TODO: This could be its own micro-project.
 */
public class Taglib {

	private final PageRef tldRef;
	private final List<String> descriptions;
	private final List<String> displayNames;
	private final String tlibVersion;
	private final String shortName;
	private final String uri;
	private final Map<String,Tag> tag;
	private final List<Tag> tags;
	private final Map<String,Function> function;
	private final List<Function> functions;

	public Taglib(
		PageRef tldRef,
		Document tldDoc,
		Map<String,String> apiLinks
	) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		this.tldRef = tldRef;

		Element taglibElem = tldDoc.getDocumentElement();

		List<String> newDescriptions = new ArrayList<String>();
		for(Element descriptionElem : XmlUtils.iterableChildElementsByTagName(taglibElem, "description")) {
			newDescriptions.add(descriptionElem.getTextContent());
		}
		this.descriptions = AoCollections.optimalUnmodifiableList(newDescriptions);

		List<String> newDisplayNames = new ArrayList<String>();
		for(Element displayNameElem : XmlUtils.iterableChildElementsByTagName(taglibElem, "display-name")) {
			newDisplayNames.add(displayNameElem.getTextContent());
		}
		this.displayNames = AoCollections.optimalUnmodifiableList(newDisplayNames);

		this.tlibVersion = XmlUtils.getChildTextContent(taglibElem, "tlib-version");
		this.shortName = XmlUtils.getChildTextContent(taglibElem, "short-name");
		this.uri = XmlUtils.getChildTextContent(taglibElem, "uri");

		Map<String,Tag> newTags = new LinkedHashMap<String,Tag>();
		for(Element tagElem : XmlUtils.iterableChildElementsByTagName(taglibElem, "tag")) {
			Tag newTag = new Tag(this, tagElem);
			String tagName = newTag.getName();
			if(newTags.put(tagName, newTag) != null) throw new IllegalArgumentException("Duplicate tag name: " + tagName);
		}
		this.tag = AoCollections.optimalUnmodifiableMap(newTags);
		this.tags = AoCollections.optimalUnmodifiableList(new ArrayList<Tag>(newTags.values()));

		Map<String,Function> newFunctions = new LinkedHashMap<String,Function>();
		for(Element functionElem : XmlUtils.iterableChildElementsByTagName(taglibElem, "function")) {
			Function newFunction = new Function(this, functionElem, apiLinks);
			String functionName = newFunction.getName();
			if(newFunctions.put(functionName, newFunction) != null) throw new IllegalArgumentException("Duplicate function name: " + functionName);
		}
		this.function = AoCollections.optimalUnmodifiableMap(newFunctions);
		this.functions = AoCollections.optimalUnmodifiableList(new ArrayList<Function>(newFunctions.values()));
	}

	public PageRef getTldRef() {
		return tldRef;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public List<String> getDisplayNames() {
		return displayNames;
	}

	public String getTlibVersion() {
		return tlibVersion;
	}

	public String getShortName() {
		return shortName;
	}

	public String getUri() {
		return uri;
	}

	public Map<String,Tag> getTag() {
		return tag;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public Map<String,Function> getFunction() {
		return function;
	}

	public List<Function> getFunctions() {
		return functions;
	}
}
