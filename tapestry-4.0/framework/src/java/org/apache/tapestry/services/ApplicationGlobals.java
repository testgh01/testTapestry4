// Copyright 2004, 2005 The Apache Software Foundation
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

package org.apache.tapestry.services;

import java.util.List;

import javax.servlet.ServletContext;

import org.apache.tapestry.spec.IApplicationSpecification;
import org.apache.tapestry.web.WebActivator;
import org.apache.tapestry.web.WebContext;

/**
 * A "global" holder for various services and configurations. In many cases, these values end up as
 * properties of the {@link org.apache.tapestry.services.Infrastructure}. The servlet and portlet
 * implementations differentiate themselves by storing different values into these properties.
 * 
 * @author Howard Lewis Ship
 * @since 4.0
 */
public interface ApplicationGlobals
{
    /**
     * Invoked by the (indirectly) by the servlet at init(), after parsing the application
     * specification.
     */
    public void storeActivator(WebActivator activator);

    public void storeSpecification(IApplicationSpecification applicationSpecification);

    /**
     * Invoked (indirectly) by the servlet at init().
     */
    public void storeServletContext(ServletContext context);

    /**
     * Invoked (indirectly) by the servlet at init().
     */

    public void storeWebContext(WebContext context);

    /**
     * Returns the previously stored context.
     * 
     * @see #store(WebContext)}.
     */

    public WebContext getWebContext();

    /**
     * Returns the previously stored context.
     * 
     * @see #storeServletContext(ServletContext)
     */
    public ServletContext getServletContext();

    public WebActivator getActivator();

    public IApplicationSpecification getSpecification();

    public String getActivatorName();

    /**
     * Stores the default set of engine service definitions. Application services override factory
     * services with the same {@link org.apache.tapestry.engine.IEngineService#getName()name}.
     * 
     * @param factoryServices
     *            List of {@link org.apache.tapestry.engine.IEngineService}.
     */

    public void storeFactoryServices(List factoryServices);

    /**
     * Returns the factory default services as a List of
     * {@link org.apache.tapestry.engine.IEngineService}.
     */

    public List getFactoryServices();
}