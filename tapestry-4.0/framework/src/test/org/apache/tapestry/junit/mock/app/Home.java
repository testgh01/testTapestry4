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

package org.apache.tapestry.junit.mock.app;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.junit.mock.lib.Dumper;

/**
 * Part of the Mock application test suite.
 * 
 * @author Howard Lewis Ship
 */

public abstract class Home extends BasePage
{
    public void linkClicked(IRequestCycle cycle)
    {
        Dumper dumper = (Dumper) cycle.getPage("lib:Dumper");

        dumper.setObjects(cycle.getListenerParameters());

        cycle.activate(dumper);
    }
}