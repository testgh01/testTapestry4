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

package org.apache.tapestry.engine.state;

import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.tapestry.SessionStoreOptimized;
import org.apache.tapestry.web.WebRequest;
import org.apache.tapestry.web.WebSession;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.tapestry.engine.state.SessionScopeManager}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class TestSessionScopeManager extends HiveMindTestCase
{
    private WebRequest newRequest(boolean create, WebSession session)
    {
        MockControl control = newControl(WebRequest.class);
        WebRequest request = (WebRequest) control.getMock();

        request.getSession(create);
        control.setReturnValue(session);

        return request;
    }

    private WebRequest newRequest(WebSession session)
    {
        MockControl control = newControl(WebRequest.class);
        WebRequest request = (WebRequest) control.getMock();

        request.getSession(true);
        control.setReturnValue(session);

        return request;
    }

    private WebSession newSession(String key, Object value)
    {
        WebSession session = newSession();

        trainGetAttribute(session, key, value);

        return session;
    }

    private StateObjectFactory newFactory(Object stateObject)
    {
        MockControl control = newControl(StateObjectFactory.class);
        StateObjectFactory factory = (StateObjectFactory) control.getMock();

        factory.createStateObject();
        control.setReturnValue(stateObject);

        return factory;
    }

    public void testExistsNoSession()
    {
        WebRequest request = newRequest(false, null);

        replayControls();

        SessionScopeManager m = new SessionScopeManager();
        m.setRequest(request);

        assertEquals(false, m.exists("doesn't matter"));

        verifyControls();
    }

    public void testExistsMissing()
    {
        WebSession session = newSession("state:myapp:fred", null);
        WebRequest request = newRequest(false, session);

        replayControls();

        SessionScopeManager m = new SessionScopeManager();
        m.setRequest(request);
        m.setApplicationId("myapp");

        assertEquals(false, m.exists("fred"));

        verifyControls();
    }

    public void testExists()
    {
        WebSession session = newSession("state:testapp:fred", "XXX");
        WebRequest request = newRequest(false, session);

        replayControls();

        SessionScopeManager m = new SessionScopeManager();
        m.setRequest(request);
        m.setApplicationId("testapp");

        assertEquals(true, m.exists("fred"));

        verifyControls();
    }

    public void testGetExists()
    {
        Object stateObject = new Object();
        WebSession session = newSession("state:testapp:fred", stateObject);
        WebRequest request = newRequest(session);

        replayControls();

        SessionScopeManager m = new SessionScopeManager();
        m.setRequest(request);
        m.setApplicationId("testapp");

        assertSame(stateObject, m.get("fred", null));

        verifyControls();
    }

    public void testGetAndCreate()
    {
        Object stateObject = new Object();
        StateObjectFactory factory = newFactory(stateObject);

        WebSession session = newSession();

        trainGetAttribute(session, "state:myapp:fred", null);

        session.setAttribute("state:myapp:fred", stateObject);

        WebRequest request = newRequest(session);

        replayControls();

        SessionScopeManager m = new SessionScopeManager();
        m.setRequest(request);
        m.setApplicationId("myapp");

        assertSame(stateObject, m.get("fred", factory));

        verifyControls();
    }

    protected void trainGetAttribute(WebSession session, String name, Object attribute)
    {
        session.getAttribute(name);

        setReturnValue(session,attribute);
    }

    public void testStore()
    {
        Object stateObject = new Object();

        WebSession session = newSession();

        session.setAttribute("state:myapp:fred", stateObject);

        WebRequest request = newRequest(session);

        replayControls();

        SessionScopeManager m = new SessionScopeManager();
        m.setRequest(request);
        m.setApplicationId("myapp");

        m.store("fred", stateObject);

        verifyControls();
    }

    protected WebSession newSession()
    {
        return (WebSession) newMock(WebSession.class);
    }

    protected SessionStoreOptimized newOptimized(boolean dirty)
    {
        SessionStoreOptimized optimized = (SessionStoreOptimized) newMock(SessionStoreOptimized.class);

        optimized.isStoreToSessionNeeded();
        setReturnValue(optimized,dirty);

        return optimized;
    }

    public void testStoreOptimizedClean()
    {
        Object stateObject = newOptimized(false);

        SessionScopeManager m = new SessionScopeManager();

        replayControls();

        m.store("fred", stateObject);

        verifyControls();
    }

    public void testStoreOptimizedDirty()
    {
        Object stateObject = newOptimized(true);

        WebSession session = newSession();

        session.setAttribute("state:myapp:fred", stateObject);

        WebRequest request = newRequest(session);

        replayControls();

        SessionScopeManager m = new SessionScopeManager();
        m.setRequest(request);
        m.setApplicationId("myapp");

        m.store("fred", stateObject);

        verifyControls();
    }
}