/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2020, 2021, 2022, 2023, 2025, 2026  AO Industries, Inc.
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

package com.semanticcms.tagreference.book;

import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoapps.net.Path;
import com.semanticcms.core.model.BookRef;
import com.semanticcms.core.model.ResourceRef;
import com.semanticcms.tagreference.TagReferenceInitializer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.ServletContainerInitializer;

/**
 * Initializes a tag reference during {@linkplain ServletContainerInitializer application start-up}.
 */
public class TagReferenceTldInitializer extends TagReferenceInitializer {

  /**
   * Parses the TLD file.
   */
  @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
  public TagReferenceTldInitializer() throws ValidationException {
    super(
        Maven.properties.getProperty("documented.name") + " Taglib Reference",
        "Taglib Reference",
        new ResourceRef(
            new BookRef(
                DomainName.valueOf("semanticcms.com"),
                Path.valueOf("/tag-reference")
            ),
            Path.valueOf("/tag-reference.tld")
        ),
        true,
        Maven.properties.getProperty("documented.javadoc.link.javase"),
        Maven.properties.getProperty("documented.javadoc.link.javaee"),
        // Self
        "com.semanticcms.tagreference", Maven.properties.getProperty("project.url") + "apidocs/com.semanticcms.tagreference/"
    );
  }
}
