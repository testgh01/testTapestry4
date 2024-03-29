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

package org.apache.tapestry.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.util.Defense;
import org.apache.tapestry.IAction;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.StaleSessionException;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.services.LinkFactory;
import org.apache.tapestry.services.ResponseRenderer;
import org.apache.tapestry.services.ServiceConstants;
import org.apache.tapestry.web.WebRequest;
import org.apache.tapestry.web.WebSession;

/**
 * A context-sensitive service related to {@link org.apache.tapestry.form.Form}and
 * {@link org.apache.tapestry.link.ActionLink}. Encodes the page, component and an action id in the
 * service context.
 * 
 * @author Howard Lewis Ship
 * @since 1.0.9
 * @deprecated To be removed in 4.1.
 */

public class ActionService implements IEngineService
{
    /** @since 4.0 */
    private ResponseRenderer _responseRenderer;

    /** @since 4.0 */
    private LinkFactory _linkFactory;

    /** @since 4.0 */
    private static final String ACTION = "action";

    /** @since 4.0 */
    private WebRequest _request;

    /** @since 4.0 */
    private IRequestCycle _requestCycle;

    public ILink getLink(boolean post, Object parameter)
    {
        Defense.isAssignable(parameter, ActionServiceParameter.class, "parameter");

        ActionServiceParameter asp = (ActionServiceParameter) parameter;

        IComponent component = asp.getComponent();
        IPage activePage = _requestCycle.getPage();
        IPage componentPage = component.getPage();

        Map parameters = new HashMap();

        boolean stateful = _request.getSession(false) != null;

        parameters.put(ServiceConstants.COMPONENT, component.getIdPath());
        parameters.put(ServiceConstants.PAGE, activePage.getPageName());
        parameters.put(ServiceConstants.CONTAINER, activePage == componentPage ? null
                : componentPage.getPageName());
        parameters.put(ACTION, asp.getActionId());
        parameters.put(ServiceConstants.SESSION, stateful ? "T" : null);

        return _linkFactory.constructLink(this, post, parameters, true);
    }

    public void service(IRequestCycle cycle) throws IOException
    {
        String componentId = cycle.getParameter(ServiceConstants.COMPONENT);
        String componentPageName = cycle.getParameter(ServiceConstants.CONTAINER);
        String activePageName = cycle.getParameter(ServiceConstants.PAGE);
        String actionId = cycle.getParameter(ACTION);
        boolean activeSession = cycle.getParameter(ServiceConstants.SESSION) != null;

        IPage page = cycle.getPage(activePageName);

        // Setup the page for the rewind, then do the rewind.

        cycle.activate(page);

        IPage componentPage = componentPageName == null ? page : cycle.getPage(componentPageName);

        IComponent component = componentPage.getNestedComponent(componentId);

        IAction action = null;

        try
        {
            action = (IAction) component;
        }
        catch (ClassCastException ex)
        {
            throw new ApplicationRuntimeException(EngineMessages.wrongComponentType(
                    component,
                    IAction.class), component, null, ex);
        }

        // Only perform the stateful check if the application was stateful
        // when the URL was rendered.

        if (activeSession && action.getRequiresSession())
        {
            WebSession session = _request.getSession(false);

            if (session == null || session.isNew())
                throw new StaleSessionException(EngineMessages.requestStateSession(component),
                        componentPage);

        }

        cycle.rewindPage(actionId, action);

        // During the rewind, a component may change the page. This will take
        // effect during the second render, which renders the HTML response.

        // Render that response.

        _responseRenderer.renderResponse(cycle);
    }

    public String getName()
    {
        return Tapestry.ACTION_SERVICE;
    }

    /** @since 4.0 */
    public void setResponseRenderer(ResponseRenderer responseRenderer)
    {
        _responseRenderer = responseRenderer;
    }

    /** @since 4.0 */
    public void setLinkFactory(LinkFactory linkFactory)
    {
        _linkFactory = linkFactory;
    }

    /** @since 4.0 */
    public void setRequest(WebRequest request)
    {
        _request = request;
    }

    /** @since 4.0 */
    public void setRequestCycle(IRequestCycle requestCycle)
    {
        _requestCycle = requestCycle;
    }
}