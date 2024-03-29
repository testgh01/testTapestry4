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

package org.apache.tapestry.util.io;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.services.DataSqueezer;
import org.apache.tapestry.util.ComponentAddress;

/**
 * Squeezes a org.apache.tapestry.ComponentAddress.
 * 
 * @author mindbridge
 * @since 2.2
 */

public class ComponentAddressAdaptor implements SqueezeAdaptor
{
    private static final String PREFIX = "A";

    private static final char SEPARATOR = ',';

    public String getPrefix()
    {
        return PREFIX;
    }

    public Class getDataClass()
    {
        return ComponentAddress.class;
    }

    public String squeeze(DataSqueezer squeezer, Object data)
    {
        ComponentAddress address = (ComponentAddress) data;

        // a 'null' id path is encoded as an empty string
        String idPath = address.getIdPath();
        if (idPath == null)
            idPath = "";

        return PREFIX + address.getPageName() + SEPARATOR + idPath;
    }

    public Object unsqueeze(DataSqueezer squeezer, String string)
    {
        int separator = string.indexOf(SEPARATOR);
        if (separator < 0)
            throw new ApplicationRuntimeException(Tapestry
                    .getMessage("ComponentAddressAdaptor.no-separator"));

        String pageName = string.substring(1, separator);
        String idPath = string.substring(separator + 1);
        if (idPath.equals(""))
            idPath = null;

        return new ComponentAddress(pageName, idPath);
    }

}