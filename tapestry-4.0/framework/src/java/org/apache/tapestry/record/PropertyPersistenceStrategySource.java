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

package org.apache.tapestry.record;

import java.util.Collection;

import org.apache.tapestry.engine.ServiceEncoding;

/**
 * A source for {@link org.apache.tapestry.record.PropertyPersistenceStrategy}s.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public interface PropertyPersistenceStrategySource
{
    /**
     * Returns the name strategy.
     * 
     * @param name
     *            the name of the strategy to retrieve.
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if no such strategy exists.
     */
    public PropertyPersistenceStrategy getStrategy(String name);

    /**
     * Returns all changes ({@link PropertyChange}) collected by any persistence strategy for the
     * page. May return an empty list.
     * 
     * @see PropertyPersistenceStrategy#getStoredChanges(String)
     */

    public Collection getAllStoredChanges(String pageName);

    /**
     * Discards any stored property changes for the named page.
     */

    public void discardAllStoredChanged(String pageName);

    /**
     * Invoked by a {@link org.apache.tapestry.services.LinkFactory}&nbsp;, the parameters may be
     * modified (added to) to store information related to persistent properties. This method is
     * forwarded to all {@link PropertyPersistenceStrategy}s.
     * 
     * @param encoding
     *            Service encoding, which indentifies the URL and the query parameters from which
     *            the {@link org.apache.tapestry.engine.ILink} will be created.
     * @param post
     *            if true, then the link will be used for a post (not a get, i.e., for a HTML form);
     *            this may affect what information is encoded into the link
     * @see PropertyPersistenceStrategySource
     */

    public void addParametersForPersistentProperties(ServiceEncoding encoding, boolean post);
}