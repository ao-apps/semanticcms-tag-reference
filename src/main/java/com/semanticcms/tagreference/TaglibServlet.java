/*
 * semanticcms-google-analytics - Includes the Google Analytics tracking code in SemanticCMS pages.
 * Copyright (C) 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of semanticcms-google-analytics.
 *
 * semanticcms-google-analytics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * semanticcms-google-analytics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with semanticcms-google-analytics.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.semanticcms.tagreference;

import com.aoindustries.servlet.http.Dispatcher;
import com.semanticcms.core.model.PageRef;
import com.semanticcms.core.servlet.CapturePage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.SkipPageException;
import org.w3c.dom.Document;

public class TaglibServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String JSPX_TARGET = "/semanticcms-tag-reference/taglib.inc.jsp";

	private final String title;
	private final String shortTitle;
	private final PageRef tldRef;
	private final Document tldDoc;
	private final Map<String,String> apiLinks;

	public TaglibServlet(
		String title,
		String shortTitle,
		PageRef tldRef,
		Document tldDoc,
		Map<String,String> apiLinks
	) {
		this.title = title;
		this.shortTitle = shortTitle;
		this.tldRef = tldRef;
		this.tldDoc = tldDoc;
		this.apiLinks = apiLinks;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String,Object> args = new LinkedHashMap<String,Object>();
		args.put("title", title);
		args.put("shortTitle", shortTitle);
		args.put("tldRef", tldRef);
		args.put("tldDoc", tldDoc);
		args.put("apiLinks", apiLinks);
		// TODO: Is there a way to get rid of this forward/include duality?
		// TODO: Perhaps something clever with the way forward is handled inside of a capture?
		if(CapturePage.getCaptureContext(req) == null) {
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
