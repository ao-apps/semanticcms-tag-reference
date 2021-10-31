/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2021  AO Industries, Inc.
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
module com.semanticcms.tagreference {
	exports com.semanticcms.tagreference;
	// Direct
	requires com.aoapps.collections; // <groupId>com.aoapps</groupId><artifactId>ao-collections</artifactId>
	requires com.aoapps.encoding; // <groupId>com.aoapps</groupId><artifactId>ao-encoding</artifactId>
	requires com.aoapps.lang; // <groupId>com.aoapps</groupId><artifactId>ao-lang</artifactId>
	requires com.aoapps.net.types; // <groupId>com.aoapps</groupId><artifactId>ao-net-types</artifactId>
	requires com.aoapps.servlet.util; // <groupId>com.aoapps</groupId><artifactId>ao-servlet-util</artifactId>
	requires com.aoapps.style; // <groupId>com.aoapps</groupId><artifactId>ao-style</artifactId>
	requires com.aoapps.taglib; // <groupId>com.aoapps</groupId><artifactId>ao-taglib</artifactId>
	requires com.aoapps.tldparser; // <groupId>com.aoapps</groupId><artifactId>ao-tld-parser</artifactId>
	requires com.aoapps.web.resources.registry; // <groupId>com.aoapps</groupId><artifactId>ao-web-resources-registry</artifactId>
	requires com.aoapps.web.resources.servlet; // <groupId>com.aoapps</groupId><artifactId>ao-web-resources-servlet</artifactId>
	requires javax.servlet.api; // <groupId>javax.servlet</groupId><artifactId>javax.servlet-api</artifactId>
	requires javax.servlet.jsp.api; // <groupId>javax.servlet.jsp</groupId><artifactId>javax.servlet.jsp-api</artifactId>
	requires org.joda.time; // <groupId>joda-time</groupId><artifactId>joda-time</artifactId>
	requires com.semanticcms.core.controller; // <groupId>com.semanticcms</groupId><artifactId>semanticcms-core-controller</artifactId>
	requires com.semanticcms.core.model; // <groupId>com.semanticcms</groupId><artifactId>semanticcms-core-model</artifactId>
	requires com.semanticcms.core.pages.local; // <groupId>com.semanticcms</groupId><artifactId>semanticcms-core-pages-local</artifactId>
	requires com.semanticcms.core.resources; // <groupId>com.semanticcms</groupId><artifactId>semanticcms-core-resources</artifactId>
	requires com.semanticcms.core.taglib; // <groupId>com.semanticcms</groupId><artifactId>semanticcms-core-taglib</artifactId>
	requires com.semanticcms.section.taglib; // <groupId>com.semanticcms</groupId><artifactId>semanticcms-section-taglib</artifactId>
	requires taglibs.standard.spec; // <groupId>org.apache.taglibs</groupId><artifactId>taglibs-standard-spec</artifactId>
	// Java SE
	requires java.xml;
}
