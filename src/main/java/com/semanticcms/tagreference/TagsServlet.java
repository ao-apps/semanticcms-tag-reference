/*
 * semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
 * Copyright (C) 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.semanticcms.tagreference;

import com.aoapps.servlet.http.Dispatcher;
import com.aoapps.style.AoStyle;
import com.aoapps.tldparser.Taglib;
import com.aoapps.web.resources.servlet.PageServlet;
import com.aoapps.web.resources.servlet.RegistryEE;
import com.semanticcms.core.model.PageRef;
import com.semanticcms.core.servlet.CapturePage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.SkipPageException;

public class TagsServlet extends PageServlet {

  private static final long serialVersionUID = 1L;

  private static final String JSPX_TARGET = "/semanticcms-tag-reference/tags.inc.jsp";

  private final PageRef tldRef;
  private final Taglib taglib;

  public TagsServlet(PageRef tldRef, Taglib taglib) {
    this.tldRef = tldRef;
    this.taglib = taglib;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // We use ao-style directly, activate
    RegistryEE.Page.get(req).activate(AoStyle.RESOURCE_GROUP);

    Map<String, Object> args = new LinkedHashMap<>();
    args.put("tldRef", tldRef);
    args.put("taglib", taglib);

    // TODO: Is there a way to get rid of this forward/include duality?
    // TODO: Perhaps something clever with the way forward is handled inside of a capture?
    ServletContext servletContext = getServletContext();
    if (CapturePage.getCaptureContext(req) == null) {
      // Forward required so can set content type
      Dispatcher.forward(
        servletContext,
        JSPX_TARGET,
        req,
        resp,
        args
      );
    } else {
      try {
        // Include required on capture since forward interrupts the final output
        Dispatcher.include(
          servletContext,
          JSPX_TARGET,
          req,
          resp,
          args
        );
      } catch (SkipPageException e) {
        throw new ServletException(e);
      }
    }
  }
}
