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

import com.aoindustries.xml.XmlUtils;
import org.w3c.dom.Element;

/**
 * A *.tld file is parsed entirely on start-up to maximize runtime performance.
 *
 * TODO: This could be its own micro-project.
 */
public class DeferredValue {

	private final Attribute attribute;
	private final String type;

	public DeferredValue(
		Attribute attribute,
		Element deferredValueElem
	) {
		this.attribute = attribute;
		this.type = XmlUtils.getChildTextContent(deferredValueElem, "type");
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public String getType() {
		return type;
	}
}
