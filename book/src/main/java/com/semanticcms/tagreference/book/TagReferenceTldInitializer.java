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
package com.semanticcms.tagreference.book;

import com.semanticcms.tagreference.TagReferenceInitializer;
import java.util.Collections;

public class TagReferenceTldInitializer extends TagReferenceInitializer {

	public TagReferenceTldInitializer() {
		super(
			Maven.properties.getProperty("project.name") + " Taglib Reference",
			"Taglib Reference",
			"/tag-reference",
			"/tag-reference.tld",
			true,
			Maven.properties.getProperty("documented.javadoc.link.javase"),
			Maven.properties.getProperty("documented.javadoc.link.javaee"),
			// Self
			Collections.singletonMap("com.semanticcms.tagreference", Maven.properties.getProperty("project.url") + "apidocs/")
		);
	}
}
