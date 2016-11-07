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
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

public class TaglibServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String JSPX_TARGET = "/semanticcms-tag-reference/taglib.inc.jsp";

	private final String title;
	private final String shortTitle;
	private final PageRef tldRef;
	private final Document tldDoc;

	public TaglibServlet(String title, String shortTitle, PageRef tldRef, Document tldDoc) {
		this.title = title;
		this.shortTitle = shortTitle;
		this.tldRef = tldRef;
		this.tldDoc = tldDoc;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String,Object> args = new LinkedHashMap<String,Object>();
		args.put("title", title);
		args.put("shortTitle", shortTitle);
		args.put("tldRef", tldRef);
		args.put("tldDoc", tldDoc);
		Dispatcher.forward(
			getServletContext(),
			JSPX_TARGET,
			req,
			resp,
			args
		);
	}
}
