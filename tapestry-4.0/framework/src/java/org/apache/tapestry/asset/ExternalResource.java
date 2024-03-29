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

package org.apache.tapestry.asset;

import java.net.URL;
import java.util.Locale;

import org.apache.hivemind.Resource;
import org.apache.hivemind.util.AbstractResource;

/**
 * Corresponds to the {@link org.apache.tapestry.asset.ExternalAsset}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class ExternalResource extends AbstractResource
{
    public ExternalResource(String path, Locale locale)
    {
        super(path, locale);
    }

    protected Resource newResource(String path)
    {
        return new ExternalResource(path, getLocale());
    }

    public URL getResourceURL()
    {
        throw new UnsupportedOperationException("getResourceURL()");
    }

    public Resource getLocalization(Locale locale)
    {
        return this;
    }

}
