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

import java.util.Locale;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.l10n.ResourceLocalizer;
import org.apache.tapestry.web.WebContext;
import org.apache.tapestry.web.WebContextResource;

/**
 * All "context:" prefixed asset paths are interpreted relative to the web context (the web
 * application's root folder).
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class ContextAssetFactory implements AssetFactory
{
    private String _contextPath;

    private AssetFactory _classpathAssetFactory;

    private WebContext _webContext;

    private ResourceLocalizer _localizer;

    public void setWebContext(WebContext webContext)
    {
        _webContext = webContext;
    }

    public IAsset createAsset(Resource baseResource, String path, Locale locale, Location location)
    {
        // We always create a new asset relative to an existing resource; the type of resource
        // will jive with the type of asset returned. Path may start with a leading slash, which
        // yields an absolute, not relative, path to the resource.

        Resource assetResource = baseResource.getRelativeResource(path);

        // Here's the thing; In Tapestry 3.0 and earlier, you could specify
        // library path like /org/apache/tapestry/contrib/Contrib.library. In the new scheme
        // of things, that should be "classpath:/org/apache/tapestry/contrib/Contrib.library".
        // But to keep a lot of things from breaking, we'll kludgely allow that here.

        if (assetResource.getResourceURL() == null && path.startsWith("/"))
            return _classpathAssetFactory.createAbsoluteAsset(path, locale, location);

        Resource localized = _localizer.findLocalization(assetResource, locale);

        if (localized == null)
            throw new ApplicationRuntimeException(AssetMessages.missingAsset(path, baseResource),
                    location, null);

        return createAsset(localized, location);
    }

    public IAsset createAbsoluteAsset(String path, Locale locale, Location location)
    {
        Resource base = new WebContextResource(_webContext, path);
        Resource localized = _localizer.findLocalization(base, locale);

        if (localized == null)
            throw new ApplicationRuntimeException(AssetMessages.missingContextResource(path),
                    location, null);

        return createAsset(localized, location);
    }

    public IAsset createAsset(Resource resource, Location location)
    {
        return new ContextAsset(_contextPath, resource, location);
    }

    public void setContextPath(String contextPath)
    {
        _contextPath = contextPath;
    }

    public void setClasspathAssetFactory(AssetFactory classpathAssetFactory)
    {
        _classpathAssetFactory = classpathAssetFactory;
    }

    public void setLocalizer(ResourceLocalizer localizer)
    {
        _localizer = localizer;
    }
}