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

import java.util.Locale;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Resource;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.asset.AssetFactory;
import org.apache.tapestry.coerce.ValueConverter;
import org.apache.tapestry.describe.HTMLDescriber;
import org.apache.tapestry.engine.IPageSource;
import org.apache.tapestry.engine.IPropertySource;
import org.apache.tapestry.engine.IScriptSource;
import org.apache.tapestry.engine.ISpecificationSource;
import org.apache.tapestry.engine.state.ApplicationStateManager;
import org.apache.tapestry.error.ExceptionPresenter;
import org.apache.tapestry.error.RequestExceptionReporter;
import org.apache.tapestry.error.StaleLinkExceptionPresenter;
import org.apache.tapestry.error.StaleSessionExceptionPresenter;
import org.apache.tapestry.listener.ListenerInvoker;
import org.apache.tapestry.listener.ListenerMapSource;
import org.apache.tapestry.markup.MarkupWriterSource;
import org.apache.tapestry.spec.IApplicationSpecification;
import org.apache.tapestry.web.WebRequest;
import org.apache.tapestry.web.WebResponse;

/**
 * Tapestry infrastructure ... key services required by the {@link org.apache.tapestry.IEngine}
 * instance.
 * 
 * @author Howard Lewis Ship
 * @since 4.0
 */
public interface Infrastructure
{
    /**
     * Initializes the Infrastructure for a particular mode.
     * 
     * @throws IllegalStateException
     *             if the Infrastructure has already been initialized.
     */

    public void initialize(String mode);

    /**
     * Returns a named property.
     * 
     * @throws IllegalStateException
     *             if the Infrastructure has not yet been initialized.
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if no value has been contributed for specified property name.
     */

    public Object getProperty(String propertyName);

    /**
     * Returns the {@link org.apache.tapestry.spec.IApplicationSpecification}&nbsp;for the current
     * application.
     */

    public IApplicationSpecification getApplicationSpecification();

    /**
     * Returns an {@link IPropertySource}&nbsp;configured to search the application specification,
     * etc. See <code>tapestry.ApplicationPropertySource</code>.
     */
    public IPropertySource getApplicationPropertySource();

    /**
     * Returns an {@link IPropertySource}&nbsp;configured to search the servlet, servlet context,
     * and factory defaults.
     */

    public IPropertySource getGlobalPropertySource();

    /**
     * Returns the coordinator to be notified of reset events (which will, in turn, notify other
     * services that they should discard cached data).
     */

    public ResetEventHub getResetEventHub();

    /**
     * Returns the source of component message bundles.
     */

    public ComponentMessagesSource getComponentMessagesSource();

    /**
     * Returns component or page template contents.
     */

    public TemplateSource getTemplateSource();

    /**
     * Returns the source of all application, page, component and library specifications.
     */

    public ISpecificationSource getSpecificationSource();

    /**
     * Returns a generic, shared ObjectPool instance.
     */
    public ObjectPool getObjectPool();

    /**
     * Returns the source for pages. The source is a cache of pages, but also can create new
     * instances when needed.
     */

    public IPageSource getPageSource();

    /**
     * Returns the ClassResolver used by the Tapestry HiveMind module, which should be sufficient
     * for use throughout the application.
     */

    public ClassResolver getClassResolver();

    /**
     * The DataSqueezer, used when constructing and decoding values stored in URLs (as query
     * parameters or hidden form fields).
     */

    public DataSqueezer getDataSqueezer();

    /**
     * The source for ready-to-execute versions of Tapestry script templates.
     */

    public IScriptSource getScriptSource();

    /**
     * The object from which engine services are obtained.
     */

    public ServiceMap getServiceMap();

    /**
     * Service used to report exceptions to the console.
     */

    public RequestExceptionReporter getRequestExceptionReporter();

    /**
     * Renders the active page as the response.
     */

    public ResponseRenderer getResponseRenderer();

    /**
     * Constructs {@link org.apache.tapestry.engine.ILink}&nbsp;instances for
     * {@link org.apache.tapestry.engine.IEngineService}s.
     */

    public LinkFactory getLinkFactory();

    /**
     * Used by the {@link org.apache.tapestry.IEngine}&nbsp;to create instances of
     * {@link org.apache.tapestry.IRequestCycle}.
     */

    public RequestCycleFactory getRequestCycleFactory();

    /**
     * Accesses application state objects (Visit and Global from Tapestry 3.0, but now more can be
     * created).
     */

    public ApplicationStateManager getApplicationStateManager();

    /**
     * Returns the request for the current request cycle.
     */

    public WebRequest getRequest();

    /**
     * Returns the response for the current request cycle.
     */

    public WebResponse getResponse();

    /**
     * Returns the context path, which identifies the application within the application server.
     * Context path should be used as a prefix for any URLs generated. The context path may be the
     * empty string, and will not end in a slash (servlet paths should start with a slash).
     */

    public String getContextPath();

    /**
     * Returns the application's id; a unique name that is incorporated into various session
     * attribute keys and into certain paths when searching for resources. For a servlet-based
     * Tapestry application, the id is the name of the servlet.
     */

    public String getApplicationId();

    /**
     * Returns the root context resource, which is the starting point when looking for resources
     * within the application.
     */

    public Resource getContextRoot();

    /**
     * Returns an object used to access component meta-data properties.
     */

    public ComponentPropertySource getComponentPropertySource();

    /**
     * Invoked when the locale for the current thread is changed.
     * 
     * @see org.apache.tapestry.IEngine#setLocale(Locale)
     */

    public void setLocale(Locale value);

    public String getOutputEncoding();

    public MarkupWriterSource getMarkupWriterSource();

    public HTMLDescriber getHTMLDescriber();

    /**
     * Responsible for presenting an exception error report to the user.
     */

    public ExceptionPresenter getExceptionPresenter();

    /**
     * The source for {@link org.apache.tapestry.listener.ListenerMap}s, for components or other
     * objects.
     */

    public ListenerMapSource getListenerMapSource();

    /**
     * The service responsible for reporting {@link org.apache.tapestry.StaleSessionException}s.
     */

    public StaleSessionExceptionPresenter getStaleSessionExceptionPresenter();

    /**
     * The service responsible for reporting {@link org.apache.tapestry.StaleLinkException}s.
     */

    public StaleLinkExceptionPresenter getStaleLinkExceptionPresenter();

    /**
     * Service used to convert and coerce types.
     */

    public ValueConverter getValueConverter();

    /**
     * Service (possibly a pipeline) that will invoke {@link org.apache.tapestry.IActionListener}
     * objects.
     */

    public ListenerInvoker getListenerInvoker();

    /**
     * Service that is used to convert {@link org.apache.hivemind.Resource}s into
     * {@link org.apache.tapestry.IAsset}s.
     */

    public AssetFactory getAssetFactory();

    /**
     * Service used to access HTTP Cookies. This is only available for Servlet Tapestry; a
     * placeholder will be provided for Portlet Tapestry.
     */

    public CookieSource getCookieSource();

    /**
     * Used to search for a class name within a list of packages.
     */

    public ClassFinder getClassFinder();

    /**
     * Returns the request cycle for the current thread.
     */
    public IRequestCycle getRequestCycle();
}