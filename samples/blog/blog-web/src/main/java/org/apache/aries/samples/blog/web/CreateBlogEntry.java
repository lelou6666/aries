/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.samples.blog.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.aries.samples.blog.api.BloggingService;
import org.apache.aries.samples.blog.web.util.FormServlet;
import org.apache.aries.samples.blog.web.util.JNDIHelper;

public class CreateBlogEntry extends HttpServlet
{
  private static final long serialVersionUID = -6484228320837122235L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException{
        doPost(req,resp);
  }
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException
  {
    // new blog entry values
    String email = req.getParameter("email");
    String title = req.getParameter("title");
    String text = req.getParameter("text");
    String tags = req.getParameter("tags");
    
    BloggingService service = JNDIHelper.getBloggingService();
    
    if (service.getBlogAuthor(email) != null) {
      service.createBlogEntry(email, title, text, tags);
      resp.sendRedirect("ViewBlog");
    } else {
      storeParam(req, "email", email);
      storeParam(req, "title", title);
      storeParam(req, "text", text);
      storeParam(req, "tags", tags);
      
      if (email.equals(""))
        FormServlet.addError(req, "The email field is required.");
      else
        FormServlet.addError(req, "The author's email is not valid.");
      
      resp.sendRedirect("CreateBlogEntryForm");
    }
  }
  
  private void storeParam(HttpServletRequest req, String param, String value) 
  {
    FormServlet.storeParam(req, CreateBlogEntryForm.ID, param, value);
  }
}