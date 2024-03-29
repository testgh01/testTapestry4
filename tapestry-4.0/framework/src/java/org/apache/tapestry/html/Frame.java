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

package org.apache.tapestry.html;

import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;

/**
 * Implements a &lt;frame&gt; within a &lt;frameset&gt;. [ <a
 * href="../../../../../ComponentReference/Frame.html">Component Reference </a>]
 * 
 * @author Howard Lewis Ship
 */

public abstract class Frame extends AbstractComponent
{
    /**
     * Injected engine service.
     * 
     * @since 4.0
     */
    public abstract IEngineService getPageService();

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        IEngineService pageService = getPageService();
        ILink link = pageService.getLink(false, getTargetPage());

        writer.beginEmpty("frame");
        writer.attribute("src", link.getURL());

        renderInformalParameters(writer, cycle);

        writer.closeTag();
    }

    public abstract String getTargetPage();

    public abstract void setTargetPage(String targetPage);
}