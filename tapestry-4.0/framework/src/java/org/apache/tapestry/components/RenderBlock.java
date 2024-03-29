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

package org.apache.tapestry.components;

import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

/**
 * Renders the text and components wrapped by a {@link Block}component. [ <a
 * href="../../../../../ComponentReference/RenderBlock.html">Component Reference </a>]
 * <p>
 * It is possible for an RenderBlock to obtain a Block from a page <em>other than</em> the render
 * page. This works, even when the Block contains links, forms and form components. The action and
 * direct services will create URLs that properly address this situation.
 * <p>
 * However, because the rendering page can't know ahead of time about these foreign Blocks,
 * {@link org.apache.tapestry.event.PageBeginRenderListener} and
 * {@link org.apache.tapestry.event.PageEndRenderListener} methods (for components and objects of the
 * foreign page) via RenderBlock will <em>not</em> be executed. This specifically affects the
 * methods of the {@link org.apache.tapestry.event.PageBeginRenderListener} and
 * {@link org.apache.tapestry.event.PageEndRenderListener} interfaces.
 * <p>
 * Before rendering its {@link Block}, RenderBlock will set itself as the Block's inserter, and
 * will reset the inserter after the {@link Block}is rendered. This gives the components contained
 * in the {@link Block}access to its inserted environment via the RenderBlock. In particular this
 * allows the contained components to access the informal parameters of the RenderBlock which
 * effectively allows parameters to be passed to the components contained in a Block.
 * 
 * @author Howard Lewis Ship
 */

public abstract class RenderBlock extends AbstractComponent
{
    /**
     * If block is not null, then
     * {@link Block#renderForComponent(IMarkupWriter, IRequestCycle, IComponent)} is invoked.
     */

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        Block block = getBlock();

        if (block == null)
            return;

        block.renderForComponent(writer, cycle, this);
    }

    public abstract Block getBlock();

}