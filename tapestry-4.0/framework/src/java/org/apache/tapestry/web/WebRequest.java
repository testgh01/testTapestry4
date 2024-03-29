// Copyright 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry.web;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry.describe.Describable;

/**
 * Contains information about the current request, including URLs, schemes, parameters, properties
 * and attributes. This is essentially a generic version of
 * {@link javax.servlet.http.HttpServletRequest}. In some cases, certain methods will be
 * unsupported in some implementations (such as {@link #getHeader(String)} for Portlet Tapestry).
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public interface WebRequest extends AttributeHolder, Describable
{
    /**
     * Returns the names of all query parameters for this request. Note that this may return an
     * empty list if an HTML form submission uploads files (with a request encoding of
     * multipart/form-data). Accessing query parameters in such an event requires parsing of the
     * request input stream.
     * 
     * @returns List of Strings, in ascending alphabetical order
     */

    public List getParameterNames();

    /**
     * Returns a parameter value. If the parameter was submitted with multiple values, then the
     * first submitted value is returned. May return null if no parameter was submitted with the
     * given name.
     * 
     * @param parameterName
     *            name of parameter to obtain
     * @return the corresponding value, or null if a value for the parameter was not submitted in
     *         the request
     * @see #getParameterValues(String)
     */

    public String getParameterValue(String parameterName);

    /**
     * Returns all parameter values for a particular parameter name. May return null.
     * <p>
     * The caller should <em>not modify</em> the returned value.
     * 
     * @param parameterName
     *            name of parameter to obtain
     * @return the corresponding values, or null if no values for the parameter were submitted in
     *         the request
     * @see #getParameterValue(String)
     */

    public String[] getParameterValues(String parameterName);

    /**
     * Returns the portion of the request URI that indicates the context of the request. The context
     * path always comes first in a request URI. The path starts with a "/" character but does not
     * end with a "/" character.
     */

    public String getContextPath();

    /**
     * Returns the current {@link WebSession}associated with this request, possibly creating it if
     * it does not already exist. If create is false and the request has no valid session, this
     * method returns null. To make sure the session is properly maintained, you must call this
     * method <em>before</em> the response is committed.
     * 
     * @param create
     *            if true, the session will be created and returned if it does not already exist
     * @returns The session, or null if it does not exist (and create is false)
     */
    public WebSession getSession(boolean create);

    /**
     * Returns the name of the scheme used to make this request. For example, http, https, or ftp.
     * Different schemes have different rules for constructing URLs, as noted in RFC 1738.
     */
    public String getScheme();

    /**
     * Returns the host name of the server that received the request. Note that behind a firewall,
     * this may be obscured (i.e., it may be the name of the firewall server, which is not
     * necessarily visible to clients outside the firewall).
     * 
     * @see org.apache.tapestry.request.IRequestDecoder
     */

    public String getServerName();

    /**
     * Returns the port number on which this request was received.
     */

    public int getServerPort();

    /**
     * Returns the path portion of the request which triggered this request. Query parameters,
     * scheme, server and port are omitted.
     * <p>
     * Note: portlets do not know their request URI.
     */

    public String getRequestURI();

    /**
     * Redirects to the indicated URL. If the URL is local, then a forward occurs. Otherwise, a
     * client side redirect is returned to the client browser.
     */

    public void forward(String URL);

    /**
     * Returns the path of the resource which activated this request (this is the equivalent of the
     * servlet path for a servlet request). The activation path will not end with a slash.
     * 
     * @returns the full servlet path (for servlet requests), or a blank string (for portlet
     *          requests).
     */
    public String getActivationPath();

    /**
     * Return any additional path info beyond the servlet path itself. Path info, if non-null,
     * begins with a path.
     * 
     * @return path info, or null if no path info
     */

    public String getPathInfo();

    /**
     * Returns the preferred locale to which content should be localized, as specified by the client
     * or by the container. May return null.
     */
    public Locale getLocale();

    /**
     * Returns the value of the specified request header.
     * 
     * @param name
     *            the name of the header to retrieve
     * @return the header value as a string, or null if the header is not in the request.
     */

    public String getHeader(String name);

    /**
     * Returns the login of the user making this request, if the user has been authenticated, or
     * null if the user has not been authenticated.
     * 
     * @return a String specifying the login of the user making this request, or null if the user
     *         login is not known.
     */

    public String getRemoteUser();

    /**
     * Returns a java.security.Principal object containing the name of the current authenticated
     * user.
     * 
     * @return a java.security.Principal containing the name of the user making this request, or
     *         null if the user has not been authenticated.
     */
    public Principal getUserPrincipal();

    /**
     * * Returns a boolean indicating whether the authenticated user is included in the specified
     * logical "role". Roles and role membership can be defined using deployment descriptors. If the
     * user has not been authenticated, the method returns false.
     * 
     * @param role
     *            a String specifying the name of the role
     * @return a boolean indicating whether the user making this request belongs to a given role;
     *         false if the user has not been authenticated.
     */
    public boolean isUserInRole(String role);
}