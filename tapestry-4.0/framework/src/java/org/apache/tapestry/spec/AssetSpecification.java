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

package org.apache.tapestry.spec;

/**
 * Defines an internal, external or private asset.
 * 
 * @author Howard Lewis Ship
 */

public class AssetSpecification extends LocatablePropertyHolder implements IAssetSpecification
{
    protected String path;

    /** @since 4.0 */
    private String _propertyName;

    /**
     * Returns the base path for the asset. This may be interpreted as a URL, relative URL or the
     * path to a resource, depending on the type of asset. Starting with 4.0, this may have a prefix
     * added to identify the type of resource.
     */

    public String getPath()
    {
        return path;
    }

    /** @since 3.0 * */

    public void setPath(String path)
    {
        this.path = path;
    }

    /** @since 4.0 */
    public String getPropertyName()
    {
        return _propertyName;
    }

    /** @since 4.0 */
    public void setPropertyName(String propertyName)
    {
        _propertyName = propertyName;
    }
}