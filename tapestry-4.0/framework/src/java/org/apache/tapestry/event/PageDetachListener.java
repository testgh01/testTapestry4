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

package org.apache.tapestry.event;

import java.util.EventListener;

/**
 * An interface for objects that want to know when the end of the request cycle occurs, so that any
 * resources that should be limited to just one request cycle can be released.
 * 
 * @see org.apache.tapestry.event.PageAttachListener
 * @author Howard Lewis Ship
 * @since 1.0.5
 */

public interface PageDetachListener extends EventListener
{
    /**
     * Invoked by the page from its {@link org.apache.tapestry.IPage#detach()}method.
     */

    public void pageDetached(PageEvent event);
}