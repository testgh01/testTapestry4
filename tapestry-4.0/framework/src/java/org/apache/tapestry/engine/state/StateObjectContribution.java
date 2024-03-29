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

import org.apache.hivemind.impl.BaseLocatable;

/**
 * Contribution to the tapestry.state.ApplicationObjects or tapestry.state.FactoryObjects
 * configuration points.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class StateObjectContribution extends BaseLocatable
{
    private String _name;

    private String _scope;

    private StateObjectFactory _factory;

    public StateObjectFactory getFactory()
    {
        return _factory;
    }

    public void setFactory(StateObjectFactory factory)
    {
        _factory = factory;
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public String getScope()
    {
        return _scope;
    }

    public void setScope(String scope)
    {
        _scope = scope;
    }
}