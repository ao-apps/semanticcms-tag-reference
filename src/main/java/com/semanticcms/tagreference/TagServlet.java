/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2016, 2017, 2018, 2019  AO Industries, Inc.
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

import com.aoindustries.servlet.http.Dispatcher;
import com.semanticcms.core.model.ResourceRef;
import com.semanticcms.core.pages.local.CaptureContext;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.SkipPageException;

public class TagServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String JSPX_TARGET = "/semanticcms-tag-reference/tag.inc.jsp";

	private final ResourceRef tldRef;
	private final Tag tag;
	private final Map<String,String> apiLinks;

	public TagServlet(
		ResourceRef tldRef,
		Tag tag,
		Map<String,String> apiLinks
	) {
		this.tldRef = tldRef;
		this.tag = tag;
		this.apiLinks = apiLinks;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String,Object> args = new LinkedHashMap<>();
		args.put("tldRef", tldRef);
		args.put("tag", tag);
		args.put("apiLinks", apiLinks);
		// TODO: Is there a way to get rid of this forward/include duality?
		// TODO: Perhaps something clever with the way forward is handled inside of a capture?
		if(CaptureContext.getCaptureContext(req) == null) {
			// Forward required so can set content type
			Dispatcher.forward(
				getServletContext(),
				JSPX_TARGET,
				req,
				resp,
				args
			);
		} else {
			try {
				// Include required on capture since forward interrupts the final output
				Dispatcher.include(
					getServletContext(),
					JSPX_TARGET,
					req,
					resp,
					args
				);
			} catch(SkipPageException e) {
				throw new ServletException(e);
			}
		}
	}
}
